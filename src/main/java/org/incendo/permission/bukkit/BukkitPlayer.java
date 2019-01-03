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

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandManager;
import com.intellectualsites.commands.parser.Parserable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.incendo.permission.Permissions;
import org.incendo.permission.config.Messages;
import org.incendo.permission.player.PermissionPlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

final class BukkitPlayer extends PermissionPlayer {

    private Player player; // Lazy loaded

    BukkitPlayer(@NotNull final Permissions permissions, @NotNull final UUID uuid) {
        super(permissions, uuid);
    }

    Player getPlayer() {
        if (player == null) {
            player = Bukkit.getPlayer(this.getUuid());
        }
        return player;
    }

    @Override public void message(final String s) {
        this.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&' ,
           Messages.PREFIX + s));
    }

    @Contract(value = " -> this", pure = true) @Override public PermissionPlayer getSuperCaller() {
        return this;
    }

    @Override public boolean hasAttachment(final String s) {
        return getPlayer().hasPermission(s);
    }

    @Override public void sendRequiredArgumentsList(final CommandManager commandManager, final Command command,
        final Collection<Parserable> collection, final String s) {
        // TODO: Implement this
    }

}
