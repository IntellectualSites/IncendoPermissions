package org.incendo.permission.database;

import com.google.common.base.Preconditions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.incendo.permission.Group;
import org.incendo.permission.Permission;
import org.incendo.permission.Permissions;
import org.incendo.permission.player.PlayerDAO;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public final class YamlPermissionDatabase extends PermissionDatabase {

    private final Permissions permissionsInstance;
    private final File groupFile;
    private final File playerFolder;
    private final YamlConfiguration groupConfiguration;

    public YamlPermissionDatabase(@NotNull final Permissions permissionsInstance, @NotNull final File baseFolder) {
        Preconditions.checkNotNull(permissionsInstance, "permissions instance");
        Preconditions.checkNotNull(baseFolder, "folder");
        this.permissionsInstance = permissionsInstance;
        this.groupFile = new File(baseFolder, "groups.yml");
        try {
            if (!groupFile.exists() && !groupFile.createNewFile()) {
                throw new RuntimeException(String.format("Could not create %s/groups.yml",
                    baseFolder.getPath()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.playerFolder = new File(baseFolder, "players");
        if ((!playerFolder.exists() || !playerFolder.isDirectory()) && !playerFolder.mkdir()) {
            throw new RuntimeException(String.format("Could not create %s/players", baseFolder.getPath()));
        }
        this.groupConfiguration = YamlConfiguration.loadConfiguration(this.groupFile);
    }

    @Override public void loadGroups() {
        if (!this.groupConfiguration.contains("groups")) {
            return;
        }
        final Set<String> groupNames = this.groupConfiguration.getConfigurationSection("groups")
            .getKeys(false);

        final Collection<String> added = new ArrayList<>();

        do {
            for (final String groupName : groupNames) {
                final ConfigurationSection configuration = this.groupConfiguration.getConfigurationSection(groupName);
                final String parent = configuration.getString("parent", null);
                if (parent != null && !added.contains(parent)) {
                    // We need to wait until the parent has loaded
                    continue;
                }
                final Collection<Permission> permissions =
                    configuration.getStringList("permissions").stream().map(Permission::of).collect(Collectors.toSet());
                final Map<String, String> properties = new HashMap<>();
                if (configuration.contains("properties")) {
                    final ConfigurationSection propertySection = configuration.getConfigurationSection("properties");
                    for (final String key : propertySection.getKeys(false)) {
                        properties.put(key, propertySection.getString(key, ""));
                    }
                }
                final Group group = new Group(groupName, parent == null ? null :
                    permissionsInstance.getGroupByName(parent).orElse(null));
                group.setPermissions(permissions);
                group.setProperties(properties);
                permissionsInstance.registerGroup(group);
                added.add(groupName);
            }
        } while (added.size() < groupNames.size());
    }

    @Override public void saveGroups(@NotNull Collection<Group> groups) {
        // TODO: Implement
    }

    @Override public PlayerDAO getPlayer(@NotNull UUID uuid) {
        final File file = new File(playerFolder, String.format("%s.yml", uuid.toString()));
        if (!file.exists()) {
            return PlayerDAO.EMPTY;
        }
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        final Collection<Permission> permissions = configuration.getStringList("permissions").stream()
            .map(Permission::of).collect(Collectors.toSet());
        final Collection<Group> groups = configuration.getStringList("groups").stream()
            .map(this.permissionsInstance::getGroupByName).filter(Optional::isPresent)
            .map(Optional::get).collect(Collectors.toSet());
        final Map<String, String> properties = new HashMap<>();
        if (configuration.contains("properties")) {
            final ConfigurationSection propertySection = configuration.getConfigurationSection("properties");
            for (final String key : propertySection.getKeys(false)) {
                properties.put(key, propertySection.getString(key, ""));
            }
        }
        return new PlayerDAO(groups, permissions, properties);
    }

    @Override public void savePlayer(@NotNull UUID uuid, @NotNull PlayerDAO playerDAO) {
        // TODO: Implement
    }

}
