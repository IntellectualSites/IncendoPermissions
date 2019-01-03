package org.incendo.permission.commands.group;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import com.intellectualsites.commands.callers.CommandCaller;
import com.intellectualsites.commands.parser.impl.StringParser;
import org.incendo.permission.Group;
import org.incendo.permission.Permission;
import org.incendo.permission.Permissions;
import org.incendo.permission.config.Messages;
import org.jetbrains.annotations.NotNull;

/**
 * /perms group add
 */
@CommandDeclaration(command = "add", aliases = "a", description = "Add a permission to a group",
    permission = "incendoperms.use.group.create", usage = "/perms group add <group> <permission> [...flags]")
public final class Add extends Command {

    private final Permissions permissions;

    public Add(@NotNull final Permissions permissions) {
        this.permissions = permissions;
        this.withArgument("group", new StringParser(), "The group name");
        this.withArgument("permission", new StringParser(), "Permission node");
    }

    @Override public boolean onCommand(final CommandInstance instance) {
        final CommandCaller caller = instance.getCaller();
        final String groupName = instance.getString("group");
        final String permission = instance.getString("permission");
        final Group group = this.permissions.getGroupByName(groupName).orElse(null);
        if (group == null) {
            caller.message(String.format(Messages.GROUP_NOT_FOUND, groupName));
            return true;
        }
        final Permission permissionObject = Permission.of(permission);
        group.addPermission(permissionObject);
        caller.message(Messages.PERMISSION_ADDED);
        return true;
    }
}
