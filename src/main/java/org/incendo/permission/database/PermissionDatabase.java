package org.incendo.permission.database;

import org.incendo.permission.Group;
import org.incendo.permission.player.PlayerDAO;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;

public abstract class PermissionDatabase {

    public abstract Collection<Group> getGroups();
    public abstract PlayerDAO getPlayer(@NotNull final UUID uuid);

}
