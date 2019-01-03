//
// MIT License
//
// Copyright (c) 2019 Alexander Söderberg
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package org.incendo.permission;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
@ToString(of = "name")
@EqualsAndHashCode(of = "name")
public final class Group {

    private final String name;
    private final Group parent;
    private final Collection<Consumer<Group>> updateSubscribers = Lists.newArrayList();

    private final Collection<Permission> permissions = new ArrayList<>(); // initial permissions
    private final Collection<Permission> effectivePermissions = new HashSet<>();
    private final Map<String, String> properties = new HashMap<>();

    private boolean modified = true;

    public boolean hasParent() {
        return this.parent != null;
    }

    public boolean hasProperty(@NotNull final String key) {
        Preconditions.checkNotNull(key, "key");
        if (this.properties.containsKey(key)) {
            return true;
        }
        Group group = this;
        while ((group = group.getParent()) != null) {
            if (group.properties.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    public void setPermissions(@NotNull final Collection<Permission> permissions) {
        Preconditions.checkNotNull(permissions, "permissions");
        this.permissions.addAll(permissions);
    }

    public void setProperties(@NotNull final Map<String, String> properties) {
        Preconditions.checkNotNull(properties, "properties");
        this.properties.putAll(properties);
    }

    @Nullable public String getProperty(@NotNull final String key) {
        Preconditions.checkNotNull(key, "key");
        if (this.properties.containsKey(key)) {
            return this.properties.get(key);
        }
        Group group = this;
        while ((group = group.getParent()) != null) {
            if (group.properties.containsKey(key)) {
                return group.properties.get(key);
            }
        }
        return null;
    }

    public void setProperty(@NotNull final String key, @NotNull final String value) {
        Preconditions.checkNotNull(key, "key");
        Preconditions.checkNotNull(value, "value");
        this.properties.put(key, value);
    }

    public void removeProperty(@NotNull final String key) {
        Preconditions.checkNotNull(key, "key");
        this.properties.remove(key);
    }

    public void registerUpdateSubscriber(@NotNull final Consumer<Group> subscriber) {
        Preconditions.checkNotNull(subscriber);
        this.updateSubscribers.add(subscriber);
    }

    public void addPermission(@NotNull final Permission permission) {
        Preconditions.checkNotNull(permission);
        this.removePermission(permission); // make sure to remove any one similar permission before
        this.permissions.add(permission);
        this.setModified();
        this.update();
    }

    public void removePermission(@NotNull final Permission permission) {
        Preconditions.checkNotNull(permission);
        getPermissionByName(permission.getRawName()).ifPresent(effectivePermission -> {
            this.permissions.remove(effectivePermission);
            setModified();
            update();
        });
    }

    private void setModified() {
        this.modified = true;
    }

    private void update() {
        if (this.modified) {
            this.getEffectivePermissions();
            this.updateSubscribers.forEach(consumer -> consumer.accept(this));
        }
    }

    @NotNull public Optional<Permission> getPermissionByName(@NotNull final String name) {
        for (final Permission permission : this.getPermissions()) {
            if (permission.getRawName().equalsIgnoreCase(name)) {
                return Optional.of(permission);
            }
        }
        return Optional.empty();
    }

    public Collection<Permission> getEffectivePermissions() {
        if (modified) {
            final Map<String, Permission> priorityPermissions = new HashMap<>();
            final Set<Group> checked = new HashSet<>();
            for (;;) {
                Group tempGroup = this;
                // find highest order parent that hasn't been checked
                while (tempGroup.hasParent()) {
                    final Group parent = tempGroup.getParent();
                    if (checked.contains(parent)) {
                        break;
                    } else {
                        tempGroup = parent;
                    }
                }
                // check all group permissions
                for (final Permission permission : tempGroup.getPermissions()) {
                    if (priorityPermissions.containsKey(permission.getRawName())) {
                        final Permission priorityPermission = priorityPermissions.get(permission.getRawName());
                        // if the previously stored permission is a priority permission, and this one isn't
                        // then the old one will remain
                        if (priorityPermission.isPriority() && !permission.isPriority()) {
                            continue;
                        }
                    }
                    priorityPermissions.put(permission.getRawName(), permission);
                }
                checked.add(tempGroup);
                // if this is true, then we've checked all effective groups
                if (tempGroup == this) {
                    break;
                }
            }
            this.effectivePermissions.clear();
            this.effectivePermissions.addAll(priorityPermissions.values());
            this.modified = false;
        }
        return Collections.unmodifiableCollection(this.effectivePermissions);
    }

}
