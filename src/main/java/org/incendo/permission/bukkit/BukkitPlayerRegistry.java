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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftHumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Player registry responsible for handling conversion
 * between {@link Player Bukkit players} and
 * {@link org.incendo.permission.player.PermissionPlayer Permission players}
 */
@RequiredArgsConstructor final class BukkitPlayerRegistry implements Listener {

    private static Field FIELD_PERM;
    static {
        try {
            FIELD_PERM = CraftHumanEntity.class.getDeclaredField("perm");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private final Map<UUID, BukkitPlayer> playerMap = new ConcurrentHashMap<>();
    @Getter private final BukkitConsole console = new BukkitConsole();

    private final PermissionPlugin plugin;

    void addPlayer(@NotNull final UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");
        final BukkitPlayer player = new BukkitPlayer(plugin.getPermissions(), uuid);
        // player.registerUpdateSubscriber(p -> attachPermissions((BukkitPlayer) p));
        this.playerMap.put(uuid, player);
    }

    private boolean isLoaded(@NotNull final UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");
        return this.playerMap.containsKey(uuid);
    }

    void removeInternally(@NotNull final Player player) {
        this.removePlayer(player.getUniqueId());
    }

    private void removeAttachments(@Nullable final Player player) {
        if (player != null && player.isOnline()) {
            final List<PermissionAttachment> permissionsToRemove = new ArrayList<>();
            for (final PermissionAttachmentInfo attachment : player.getEffectivePermissions()) {
                if (attachment.getAttachment() != null && attachment.getAttachment().getPlugin() != null &&
                    attachment.getAttachment().getPlugin().equals(plugin)) {
                    permissionsToRemove.add(attachment.getAttachment());
                }
            }
            permissionsToRemove.forEach(player::removeAttachment);
        }
    }

    private void removePlayer(@NotNull final UUID uuid) {
        Preconditions.checkNotNull(uuid, "uuid");
        this.playerMap.remove(uuid);
        final Player player = Bukkit.getPlayer(uuid);
        removeAttachments(player);
    }

    /**
     * Get a {@link BukkitPlayer} from a {@link Player}
     *
     * @param bukkitPlayer Player
     * @return Bukkit player
     * @throws IllegalStateException if the UUID of the player isn't loaded in the registry
     */
    @NotNull BukkitPlayer getPlayer(@NotNull final Player bukkitPlayer) {
        Preconditions.checkNotNull(bukkitPlayer, "player");
        final UUID uuid = bukkitPlayer.getUniqueId();
        if (!this.playerMap.containsKey(uuid)) {
            throw new IllegalStateException(
                String.format("No BukkitPlayer with UUID \"%s\" is loaded.", uuid.toString()));
        }
        return this.playerMap.get(uuid);
    }

    /**
     * Attempt to load the permissions before the player joins
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerPreLogin(@NotNull final AsyncPlayerPreLoginEvent event) {
        Preconditions.checkNotNull(event.getUniqueId());
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }
        this.addPlayer(event.getUniqueId());
    }

    //
    // Filters listeners
    //

    /**
     * Re-calculate permissions if the player has any game mode dependent permissions
     */
    @EventHandler(priority = EventPriority.LOWEST) public void onPlayerGameModeChange(
        @NotNull final PlayerGameModeChangeEvent event) {
        final BukkitPlayer player = getPlayer(event.getPlayer());
        if (player.hasGameModeDependent()) {
            // this.attachPermissions(player);
            player.updateEffectivePermissions();
        }
    }

    /**
     * Re-calculate permissions if the player has any world dependent permissions
     */
    @EventHandler(priority = EventPriority.LOWEST) public void onPlayerWorldChange(
        @NotNull final PlayerChangedWorldEvent event) {
        final BukkitPlayer player = getPlayer(event.getPlayer());
        if (player.hasWorldDependent()) {
            player.updateEffectivePermissions();
        }
    }

    //
    // Player online status listeners
    //

    /**
     * Add the permissions to the player
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void onPlayerLogin(
        @NotNull final PlayerLoginEvent event) {
        while (event.getPlayer().isOnline() && !this
            .isLoaded(event.getPlayer().getUniqueId())) { // block until it is loaded
        }
        final BukkitPlayer bukkitPlayer = getPlayer(event.getPlayer());
        final CraftHumanEntity craftHumanEntity = (CraftHumanEntity) event.getPlayer();
        try {
            FIELD_PERM.setAccessible(true);
            final PermissibleBase oldBase = (PermissibleBase) FIELD_PERM.get(craftHumanEntity);
            final PermissibleBase customBase = new CustomPermissibleBase(this.plugin, bukkitPlayer,
                event.getPlayer(), oldBase);
            FIELD_PERM.set(craftHumanEntity, customBase);
            plugin.getLogger().info(String.format("Injected custom PermissibleBase into player %s",
                event.getPlayer().getName()));
        } catch (final Throwable e) {
            plugin.getLogger().severe(String.format("Failed to replace PermissibleBase: %s",
                e.getMessage()));
            e.printStackTrace();
        }
        bukkitPlayer.updateEffectivePermissions();
    }

    /**
     * Remove the player from the registry
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void onPlayerQuit(
        @NotNull final PlayerQuitEvent event) {
        this.removeInternally(event.getPlayer());
    }

    /**
     * Remove the player from the registry
     */
    @EventHandler(priority = EventPriority.HIGHEST) public void onPlayerKick(
        @NotNull final PlayerKickEvent event) {
        this.removeInternally(event.getPlayer());
    }

}
