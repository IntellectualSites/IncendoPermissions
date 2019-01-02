package org.incendo.permission.config;

import com.intellectualsites.configurable.ConfigurationImplementation;
import com.intellectualsites.configurable.annotations.ConfigSection;
import com.intellectualsites.configurable.annotations.Configuration;

@Configuration(name = "config", implementation = ConfigurationImplementation.YAML)
public class PermissionConfig {

    public static String databaseType = "yaml";

    @ConfigSection
    public static class MySQL {

        public static String username = "username";
        public static String password = "password";
        public static String database = "database";

        public static String host = "127.0.0.1";
        public static int    port = 3306;
    }

}
