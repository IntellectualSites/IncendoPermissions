package org.incendo.permission.bukkit;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.permission.Permissions;

@SuppressWarnings("unused")
public final class PermissionPlugin extends JavaPlugin {

    @Getter private final Permissions permissions = new Permissions();
    @Getter private final BukkitPlayerRegistry playerRegistry = new BukkitPlayerRegistry();

    @Override public void onEnable() {
        //
        // Initialize command handlers
        //
        getCommand("permissions").setExecutor(new BukkitCommandExecutor(this.permissions.getMainCommand()));
        getCommand("permissions").setTabCompleter(new BukkitTabCompleter(this.permissions.getMainCommand()));
        //
        // Register listeners
        //
        getServer().getPluginManager().registerEvents(playerRegistry, this);
    }

    @Override public void onDisable() {
    }
}
