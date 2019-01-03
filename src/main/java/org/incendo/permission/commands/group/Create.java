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
import org.incendo.permission.Permissions;
import org.incendo.permission.config.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * /perms group create
 */
@CommandDeclaration(command = "create", aliases = "c", usage = "/perms group create <name> [parent]",
    permission = "incendoperms.use.group.create", description = "Create a new group")
public final class Create extends Command {

    private final Permissions permissions;

    public Create(@NotNull final Permissions permissions) {
        this.permissions = permissions;
        withArgument("name", new StringParser(), "Group name");
    }

    @Override public boolean onCommand(@NotNull final CommandInstance instance) {
        final CommandCaller caller = instance.getCaller();
        final String groupName = instance.getString("name");
        final String parentName;
        if (instance.getArguments().length > 1) {
            parentName = instance.getArguments()[1];
        } else {
            parentName = null;
        }
        if (permissions.getGroupByName(groupName).isPresent()) {
            caller.message(Messages.GROUP_ALREADY_EXISTS);
        } else {
            Group parent = null;
            if (parentName != null && !permissions.getGroupByName(parentName).isPresent()) {
                caller.message(String.format(Messages.GROUP_NOT_FOUND, parentName));
                return true;
            } else if (parentName != null) {
                parent = permissions.getGroupByName(parentName).get();
            }
            final Group group = new Group(groupName.toLowerCase(Locale.ENGLISH), parent);
            if (permissions.registerGroup(group)) {
                caller.message(Messages.GROUP_CREATED);
            } else {
                caller.message(Messages.GROUP_NOT_CREATED);
            }
        }
        return true;
    }
}
