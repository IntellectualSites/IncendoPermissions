package org.incendo.permission.database;

import org.incendo.permission.Group;
import org.incendo.permission.player.PlayerDAO;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public abstract class PermissionDatabase {

    public abstract void loadGroups();
    public abstract void saveGroups(@NotNull final Collection<Group> groups);

    public abstract PlayerDAO getPlayer(@NotNull final UUID uuid);
    public abstract void savePlayer(@NotNull final UUID uuid, @NotNull final PlayerDAO playerDAO);

}
