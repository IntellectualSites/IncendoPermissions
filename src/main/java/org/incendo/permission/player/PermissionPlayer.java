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

package org.incendo.permission.player;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellectualsites.commands.callers.CommandCaller;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.incendo.permission.Group;
import org.incendo.permission.Permission;
import org.incendo.permission.Permissions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
public abstract class PermissionPlayer implements CommandCaller<PermissionPlayer> {

    @Getter private final Permissions permissionsInstance;
    @Getter private final UUID uuid;

    private final Collection<Group> groups = new HashSet<>();
    private final Collection<Permission> permissions = new HashSet<>(); // player specific permissions
    private final Collection<Permission> effectivePermissions = new HashSet<>();
    private final Collection<Consumer<PermissionPlayer>> updateSubscribers = Lists.newArrayList();
    private final Map<String, String> properties = new HashMap<>();

    private boolean hasWorldDependent = false;
    private boolean hasGameModeDependent = false;

    protected final void setup() {
        // Load groups
        boolean hasDefault = false;
        for (final Group group : groups) {
            group.registerUpdateSubscriber(g -> this.updateEffectivePermissions());
            if (group.getName().equalsIgnoreCase("default")) {
                hasDefault = true;
            }
        }
        if (!hasDefault) {
            // insert default group if it isn't already registered
            this.groups.add(permissionsInstance.getGroupByName("default").orElse(null));
        }
        this.updateEffectivePermissions();
    }

    @Contract(pure = true) public final boolean hasWorldDependent() {
        return false; // TODO: Implement this
    }

    public boolean hasGameModeDependent() {
        return false; // TODO: Implement this
    }

    @Contract(pure = true) @NotNull public final Collection<Permission> getEffectivePermissions() {
        return Collections.unmodifiableCollection(effectivePermissions);
    }

    public boolean hasProperty(@NotNull final String key) {
        Preconditions.checkNotNull(key, "key");
        if (this.properties.containsKey(key)) {
            return true;
        }
        for (final Group group : this.groups) {
            if (group.hasProperty(key)) {
                return true;
            }
        }
        return false;
    }

    @Nullable public String getProperty(@NotNull final String key) {
        Preconditions.checkNotNull(key, "key");
        if (this.properties.containsKey(key)) {
            return this.properties.get(key);
        }
        for (final Group group : this.groups) {
            if (group.hasProperty(key)) {
                return group.getProperty(key);
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

    public void registerUpdateSubscriber(@NotNull final Consumer<PermissionPlayer> subscriber) {
        Preconditions.checkNotNull(subscriber);
        this.updateSubscribers.add(subscriber);
    }

    public void addGroup(@NotNull final Group group) {
        Preconditions.checkNotNull(group);
        if (this.groups.contains(group)) {
            throw new IllegalArgumentException(String.format("The player is already a member of"
                + " the group \"%s\"", group.getName()));
        }
        this.groups.add(group);
        this.updateEffectivePermissions();
    }

    public void removeGroup(@NotNull final Group group) {
        Preconditions.checkNotNull(group);
        if (!this.groups.contains(group)) {
            throw new IllegalArgumentException(String.format("The player is not a member of the"
                + " group \"%s\"", group.getName()));
        }
        this.groups.remove(group);
        this.updateEffectivePermissions();
    }

    public void addPermission(@NotNull final Permission permission) {
        Preconditions.checkNotNull(permission);
        this.removePermission(permission); // make sure to remove any one similar permission before
        this.permissions.add(permission);
        this.updateEffectivePermissions();
    }

    public void removePermission(@NotNull final Permission permission) {
        Preconditions.checkNotNull(permission);
        getPermissionByName(permission.getRawName()).ifPresent(effectivePermission -> {
            this.permissions.remove(effectivePermission);
            this.updateEffectivePermissions();
        });
    }

    public boolean hasPermission(@NotNull final String permission) {
        for (final Permission effectivePermission : this.effectivePermissions) {
            if (effectivePermission.matches(permission)) {
                return effectivePermission.getValue();
            }
        }
        return false;
    }

    public boolean hasPermissionSet(@NotNull final String permission) {
        for (final Permission effectivePermission : this.effectivePermissions) {
            if (effectivePermission.matches(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Performs a greedy search for effectively positive permissions across the players
     * personal permissions, and their group permissions
     */
    public final void updateEffectivePermissions() {
        final Map<String, Permission> effectivePermissions = Maps.newHashMap();
        for (final Group group : groups) {
            for (final Permission permission : group.getEffectivePermissions()) {
                if (effectivePermissions.containsKey(permission.getRawName())) {
                    final Permission effectivePermission = effectivePermissions.get(permission.getRawName());
                    if (permission.getValue() || !effectivePermission.getValue()) {
                        effectivePermissions.put(permission.getRawName(), permission);
                    }
                }
            }
        }
        for (final Permission permission : permissions) {
            if (effectivePermissions.containsKey(permission.getRawName())) {
                final Permission effectivePermission = effectivePermissions.get(permission.getRawName());
                if (permission.getValue() || !effectivePermission.getValue()) {
                    effectivePermissions.put(permission.getRawName(), permission);
                }
            }
        }
        this.effectivePermissions.clear();
        this.effectivePermissions.addAll(effectivePermissions.values());
        this.updateSubscribers.forEach(subscriber -> subscriber.accept(this));
    }

    @NotNull public final Optional<Permission> getPermissionByName(@NotNull final String rawPermission) {
        Preconditions.checkNotNull(rawPermission, "raw permission");
        for (final Permission permission : this.getEffectivePermissions()) {
            if (permission.matches(rawPermission)) {
                return Optional.of(permission);
            }
        }
        return Optional.empty();
    }

}
