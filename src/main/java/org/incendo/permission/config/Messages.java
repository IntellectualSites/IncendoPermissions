package org.incendo.permission.config;

import com.intellectualsites.configurable.ConfigurationImplementation;
import com.intellectualsites.configurable.annotations.Configuration;

@Configuration(name = "messages", implementation = ConfigurationImplementation.YAML)
public class Messages {

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

}
