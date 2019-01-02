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
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
public abstract class PermissionPlayer implements CommandCaller<PermissionPlayer> {

    @Getter private final UUID uuid;
    private final Collection<Group> groups = new HashSet<>();
    private final Collection<Permission> permissions = new HashSet<>(); // player specific permissions
    private final Collection<Permission> effectivePermissions = new HashSet<>();
    private final Collection<Consumer<PermissionPlayer>> updateSubscribers = Lists.newArrayList();

    public boolean hasWorldDependent, hasGameModeDependent;

    protected final void setup() {
        // Load groups
        for (final Group group : groups) {
            group.registerUpdateSubscriber(g -> this.updateEffectivePermissions());
        }
        this.updateEffectivePermissions();
    }

    public final boolean hasWorldDependent() {
        return false; // TODO: Implement this
    }

    public boolean hasGameModeDependent() {
        return false; // TODO: Implement this
    }

    @NotNull public final Collection<Permission> getEffectivePermissions() {
        return Collections.unmodifiableCollection(effectivePermissions);
    }

    public void registerUpdateSubscriber(@NotNull final Consumer<PermissionPlayer> subscriber) {
        Preconditions.checkNotNull(subscriber);
        this.updateSubscribers.add(subscriber);
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

    /**
     * Performs a greedy search for effectively positive permissions across the players
     * personal permissions, and their group permissions
     */
    protected final void updateEffectivePermissions() {
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
