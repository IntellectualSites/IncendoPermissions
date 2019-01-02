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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

final class BukkitPlayerRegistry implements Listener  {

    private final Map<UUID, BukkitPlayer> playerMap = new HashMap<>();
    @Getter  private final BukkitConsole console = new BukkitConsole();

    private void addPlayer(@NotNull final UUID uuid) {
        final BukkitPlayer player = new BukkitPlayer(uuid);
        this.playerMap.put(uuid, player);
    }

    private void removePlayer(@NotNull final UUID uuid) {
        this.playerMap.remove(uuid);
    }

    BukkitPlayer getPlayer(@NotNull final Player bukkitPlayer) {
        final UUID uuid = bukkitPlayer.getUniqueId();
        if (!this.playerMap.containsKey(uuid)) {
            throw new IllegalStateException(String.format("No BukkitPlayer with UUID \"%s\" is loaded.", uuid.toString()));
        }
        return this.playerMap.get(uuid);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerLogin(final AsyncPlayerPreLoginEvent event) {
        Preconditions.checkNotNull(event.getUniqueId());
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }
        this.addPlayer(event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.removePlayer(event.getPlayer().getUniqueId());
    }

}
