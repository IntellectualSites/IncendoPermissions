package org.incendo.permission.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import lombok.RequiredArgsConstructor;
import org.incendo.permission.Permissions;

/**
 * Main command handler
 */
@CommandDeclaration(command = "permissions", permission = "incendoperms.use", usage = "/permissions")
@RequiredArgsConstructor
public final class PermissionCommand extends Command {

    private final Permissions permissions;

    {
        getManagerOptions().setPrintStacktrace(false);
        getManagerOptions().setUseAdvancedPermissions(false);
        getManagerOptions().setRequirePrefix(false);
        getManagerOptions().setUsageFormat("");
        // Add this as the base command
        this.createCommand(this);
        // Add sub-commands
    }

}
