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

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.permission.Permissions;

/**
 * The main class for the Bukkit implementation of the plugin
 */
@SuppressWarnings("unused")
public final class PermissionPlugin extends JavaPlugin {

    @Getter private final BukkitPlayerRegistry playerRegistry = new BukkitPlayerRegistry();
    @Getter private Permissions permissions;

    @Override public void onEnable() {
        this.permissions = new Permissions(this.getDataFolder());
        //
        // Initialize command handlers
        //
        this.getCommand("permissions").setExecutor(new BukkitCommandExecutor(this.permissions.getMainCommand(), this.playerRegistry));
        this.getCommand("permissions").setTabCompleter(new BukkitTabCompleter(this.permissions.getMainCommand()));
        //
        // Register listeners
        //
        this.getServer().getPluginManager().registerEvents(playerRegistry, this);
        // TODO: Load permissions
    }

    @Override public void onDisable() {
        // TODO: Save permissions
    }
}
