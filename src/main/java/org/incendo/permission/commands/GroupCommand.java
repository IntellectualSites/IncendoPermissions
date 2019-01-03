package org.incendo.permission.commands;

import com.intellectualsites.commands.Command;
import com.intellectualsites.commands.CommandDeclaration;
import com.intellectualsites.commands.CommandInstance;
import org.incendo.permission.Permissions;
import org.incendo.permission.commands.group.Create;
import org.jetbrains.annotations.NotNull;

@CommandDeclaration(command = "group", aliases = "g", usage = "/prms group", permission = "incendoperms.use.group")
public class GroupCommand extends Command {

    public GroupCommand(@NotNull final Permissions permissions) {
        this.createCommand(new Create(permissions));
    }

    @Override public boolean onCommand(@NotNull final CommandInstance instance) {
        // TODO: Send help menu
        return true;
    }
}
