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

package org.incendo.permission.config;

import com.intellectualsites.configurable.ConfigurationImplementation;
import com.intellectualsites.configurable.annotations.Configuration;

@Configuration(name = "messages", implementation = ConfigurationImplementation.YAML)
public class Messages {

    public static String PREFIX = "&3&lIncendoPermissions > &r";

    //
    // Permission related errors
    //
    public static String NOT_PERMITTED = "&4Error: &cYou are not permitted to use this command";

    //
    // Wrong type errors
    //
    public static String ONLY_CONSOLE = "&4Error: &cThis command may only be executed from the console";
    public static String ONLY_PLAYER = "&4Error: &cThis command may only be executed by a player";

    //
    // Errors
    //
    public static String COMMAND_ERROR_OCCURRED = "&4An error occurred when executing the command";

    //
    // Wrong command
    //
    public static String COMMAND_DID_YOU_MEAN = "&4Error: &cThere is no such command. Did you mean: %s?";
    public static String COMMAND_NOT_FOUND = "&4Error: &cThere is no such command";
    public static String COMMAND_WRONG_USAGE = "&4Error: &cWrong usage. Command usage: %s";
    public static String COMMAND_ARGUMENT = "&4Error: &cWrong usage. See argument list";

    //
    // Group command messages
    //
    public static String GROUP_ALREADY_EXISTS = "&4Error: &cA group with that name already exists";
    public static String GROUP_NOT_FOUND = "&4Error: &cThere is not group with the name %s";
    public static String GROUP_CREATED = "&aThe group was successfully created!";
    public static String GROUP_NOT_CREATED = "&4Error: &cThe group could not be created";


    //
    // Permission related messages
    //
    public static String PERMISSION_ADDED = "&aThe permission was successfully added!";

}
