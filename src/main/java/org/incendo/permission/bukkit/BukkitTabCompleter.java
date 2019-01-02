package org.incendo.permission.bukkit;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.incendo.permission.commands.PermissionCommand;

import java.util.List;

/**
 * Bukkit tab completer
 */
@RequiredArgsConstructor
class BukkitTabCompleter implements TabCompleter {

    private final PermissionCommand permissionCommand;

    @Override public List<String> onTabComplete(CommandSender sender, Command command, String alias,
        String[] args) {
        return null;
    }
}
