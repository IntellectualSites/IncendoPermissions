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
