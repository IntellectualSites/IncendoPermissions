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

package org.incendo.permission.commands;

import com.intellectualsites.commands.CommandManager;
import lombok.Getter;
import org.incendo.permission.Permissions;
import org.jetbrains.annotations.NotNull;

/**
 * Main command handler
 */
public final class PermissionCommand {

    private final Permissions permissions;
    @Getter private final CommandManager commandManager;

    public PermissionCommand(@NotNull final Permissions permissions) {
        this.permissions = permissions;
        this.commandManager = new CommandManager();
        this.commandManager.getManagerOptions().setPrintStacktrace(false);
        this.commandManager.getManagerOptions().setUseAdvancedPermissions(false);
        this.commandManager.getManagerOptions().setRequirePrefix(false);
        this.commandManager.getManagerOptions().setUsageFormat("");
        // Add sub-commands
        this.commandManager.createCommand(new GroupCommand(permissions));
    }

}
