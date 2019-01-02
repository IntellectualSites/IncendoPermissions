package org.incendo.permission.bukkit;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandManager;
import com.intellectualsites.commands.parser.Parserable;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.incendo.permission.player.PermissionPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
final class BukkitPlayer extends PermissionPlayer {

    private final UUID uuid;
    private Player player; // Lazy loaded

    private Player getPlayer() {
        if (player == null) {
            player = Bukkit.getPlayer(uuid);
        }
        return player;
    }

    @Override public void message(@NotNull final String s) {
        this.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&' , s));
    }

    @Override public PermissionPlayer getSuperCaller() {
        return this;
    }

    @Override public boolean hasAttachment(final String s) {
        return false;
    }

    @Override public void sendRequiredArgumentsList(CommandManager commandManager, Command command,
        Collection<Parserable> collection, String s) {
        // TODO: Implement this
    }

}
