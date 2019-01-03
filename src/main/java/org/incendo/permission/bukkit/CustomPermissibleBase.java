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

package org.incendo.permission.bukkit;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

final class CustomPermissibleBase extends PermissibleBase {

    private final PermissionPlugin permissions;
    private final PermissibleBase oldBase;
    private final BukkitPlayer bukkitPlayer;

    CustomPermissibleBase(@NotNull final PermissionPlugin permissions,
        @NotNull final BukkitPlayer bukkitPlayer, @NotNull final Player player,
        @NotNull final PermissibleBase oldBase) {
        super(player);
        Preconditions.checkNotNull(permissions, "permissins");
        Preconditions.checkNotNull(bukkitPlayer, "bukkit player");
        Preconditions.checkNotNull(player, "player");
        Preconditions.checkNotNull(oldBase, "old base");
        this.permissions = permissions;
        this.oldBase = oldBase;
        this.bukkitPlayer = bukkitPlayer;
    }

    @Override public boolean hasPermission(@NotNull final String permissionNode) {
        if (bukkitPlayer.hasPermission(permissionNode)) {
            return true;
        }
        return oldBase.hasPermission(permissionNode);
    }

    @Override public boolean hasPermission(@NotNull final Permission perm) {
        return super.hasPermission(perm);
    }

    @Override public void removeAttachment(@NotNull final PermissionAttachment attachment) {
        oldBase.removeAttachment(attachment);
    }

    @Override public boolean isOp() {
        return oldBase.isOp();
    }

    @Override public void setOp(final boolean value) {
        oldBase.setOp(value);
    }

    @Override public boolean isPermissionSet(@NotNull final String name) {
        if (this.bukkitPlayer.hasPermissionSet(name)) {
            return true;
        }
        return oldBase.isPermissionSet(name);
    }

    @Override public boolean isPermissionSet(@NotNull final Permission perm) {
        if (this.bukkitPlayer.hasPermissionSet(perm.getName())) {
            return true;
        }
        return oldBase.isPermissionSet(perm);
    }

    @Override public PermissionAttachment addAttachment(@NotNull final Plugin plugin,
        @NotNull final String name, final boolean value) {
        return oldBase.addAttachment(plugin, name, value);
    }

    @Override public PermissionAttachment addAttachment(@NotNull final Plugin plugin) {
        return oldBase.addAttachment(plugin);
    }

    @Override public void recalculatePermissions() {
        oldBase.recalculatePermissions();
    }

    @Override public synchronized void clearPermissions() {
        oldBase.clearPermissions();
    }

    @Override public PermissionAttachment addAttachment(@NotNull final Plugin plugin, @NotNull final String name,
        final boolean value, final int ticks) {
        return oldBase.addAttachment(plugin, name, value, ticks);
    }

    @Override public PermissionAttachment addAttachment(@NotNull final Plugin plugin, final int ticks) {
        return oldBase.addAttachment(plugin, ticks);
    }

    @Override public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        // oldBase.getEffectivePermissions();
        final Set<PermissionAttachmentInfo> minecraftAttachments = oldBase.getEffectivePermissions();
        final Set<PermissionAttachmentInfo> infos = new HashSet<>(oldBase.getEffectivePermissions().size()
            + bukkitPlayer.getEffectivePermissions().size());
        infos.addAll(minecraftAttachments);
        for (final org.incendo.permission.Permission effectivePermission : this.bukkitPlayer.getEffectivePermissions()) {
            final PermissionAttachmentInfo permissionAttachmentInfo = new PermissionAttachmentInfo(bukkitPlayer.getPlayer(),
                effectivePermission.getRawName(), new PermissionAttachment(permissions, bukkitPlayer.getPlayer()), effectivePermission.getValue());
            infos.add(permissionAttachmentInfo);
        }
        return infos;
    }
}
