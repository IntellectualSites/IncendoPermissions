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
