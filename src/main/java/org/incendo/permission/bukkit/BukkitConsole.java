package org.incendo.permission.bukkit;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandManager;
import com.intellectualsites.commands.callers.CommandCaller;
import com.intellectualsites.commands.parser.Parserable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

final class BukkitConsole implements CommandCaller<BukkitConsole> {

    private final ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();

    @Override public void message(@NotNull final String s) {
        this.consoleCommandSender.sendMessage(ChatColor.stripColor(s));
    }

    @Override public BukkitConsole getSuperCaller() {
        return this;
    }

    @Override public boolean hasAttachment(String s) {
        return true;
    }

    @Override public void sendRequiredArgumentsList(CommandManager commandManager, Command command,
        Collection<Parserable> collection, String s) {
        // TODO: Implement this
    }
}
