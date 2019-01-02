package org.incendo.permission.bukkit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.incendo.permission.player.PermissionPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class used to interact with Bukkit objects
 */
@RequiredArgsConstructor
final class BukkitUtil {

    private final BukkitPlayerRegistry playerRegistry;

    public PermissionPlayer getPlayer(@NotNull final Player bukkitPlayer) {
        // TODO: Make this acutally do something
    }

}
