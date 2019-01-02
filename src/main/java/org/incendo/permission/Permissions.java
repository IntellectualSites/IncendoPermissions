package org.incendo.permission;

import lombok.Getter;
import org.incendo.permission.commands.PermissionCommand;

public final class Permissions {

    @Getter
    private final PermissionCommand mainCommand;

    public Permissions() {
        this.mainCommand = new PermissionCommand(this);
    }

}
