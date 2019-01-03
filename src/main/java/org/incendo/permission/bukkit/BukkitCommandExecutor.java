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

package org.incendo.permission.bukkit;

import com.google.common.base.Preconditions;
import com.intellectualsites.commands.CommandResult;
import com.intellectualsites.commands.callers.CommandCaller;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.permission.commands.PermissionCommand;
import org.incendo.permission.config.Messages;
import org.jetbrains.annotations.NotNull;

import static com.intellectualsites.commands.CommandHandlingOutput.*;
import static com.intellectualsites.commands.CommandHandlingOutput.ARGUMENT_ERROR;

/**
 * Bukkit command executor
 */
@RequiredArgsConstructor
final class BukkitCommandExecutor implements CommandExecutor {

    private final PermissionCommand permissionCommand;
    private final BukkitPlayerRegistry playerRegistry;

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command,
        @NotNull final String label, @NotNull String[] args) {
        Preconditions.checkNotNull(sender, "sender");

        final CommandCaller caller;
        if (sender instanceof Player) {
            caller = playerRegistry.getPlayer((Player) sender);
        } else {
            caller = playerRegistry.getConsole();
        }

        // We need to turn the arguments into a single string
        final StringBuilder stringBuilder = new StringBuilder();

        if (args.length == 0) {
            args = new String[] { "help" }; // force help command
        }

        for (int i = 0; i < args.length; i++) {
            stringBuilder.append(args[i]);
            if ((i + 1) < args.length) {
                stringBuilder.append(" ");
            }
        }

        final CommandResult result = permissionCommand.getCommandManager().
            handle(caller, stringBuilder.toString());
        this.handle(result);

        // We handle usage messages ourselves
        return true;
    }

    void handle(@NotNull final CommandResult result) {
        switch (result.getCommandResult()) {
            case NOT_PERMITTED: {
                result.getCaller().message(Messages.NOT_PERMITTED);
            } break;
            case CALLER_OF_WRONG_TYPE: {
                if (result.getCaller() instanceof BukkitPlayer) {
                    result.getCaller().message(Messages.ONLY_CONSOLE);
                } else {
                    result.getCaller().message(Messages.ONLY_PLAYER);
                }
            } break;
            case ERROR: {
                result.getCaller().message(Messages.COMMAND_ERROR_OCCURRED);
            } break;
            case NOT_FOUND: {
                if (result.getClosestMatch() != null) {
                    result.getCaller().message(String.format(Messages.COMMAND_DID_YOU_MEAN,
                        result.getClosestMatch().getCommand()));
                } else {
                    result.getCaller().message(Messages.COMMAND_NOT_FOUND);
                }
            } break;
            case WRONG_USAGE: {
                result.getCaller().message(String.format(Messages.COMMAND_WRONG_USAGE,
                    result.getCommand().getUsage()));
            } break;
            case ARGUMENT_ERROR: {
                result.getCaller().message(Messages.COMMAND_ARGUMENT);
            } break;
            default: break;
        }
    }

}
