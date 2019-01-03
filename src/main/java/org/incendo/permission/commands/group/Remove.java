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

import java.util.Optional;

/**
 * /perms group remove
 */
@CommandDeclaration(command = "remove", aliases = "a", description = "Remove a permission from a group",
    permission = "incendoperms.use.group.remove", usage = "/perms group remove <group> <permission>")
public final class Remove extends Command {

    private final Permissions permissions;

    public Remove(@NotNull final Permissions permissions) {
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
        final Optional<Permission> permissionOptional = group.getPermissionByName(permission);
        if (permissionOptional.isPresent()) {
            group.removePermission(permissionOptional.get());
            caller.message(Messages.PERMISSION_REMOVED);
        } else {
            caller.message(Messages.PERMISSION_NOT_FOUND);
        }
        return true;
    }
}
