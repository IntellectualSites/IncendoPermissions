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

package org.incendo.permission;

import com.google.common.base.Preconditions;
import com.intellectualsites.configurable.ConfigurationFactory;
import lombok.Getter;
import org.incendo.permission.commands.PermissionCommand;
import org.incendo.permission.config.Messages;
import org.incendo.permission.config.PermissionConfig;
import org.incendo.permission.database.PermissionDatabase;
import org.incendo.permission.database.YamlPermissionDatabase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * Permissions main file. Get an instance of this via your implementations plugin file
 */
public final class Permissions {

    @Getter private final PermissionCommand mainCommand;
    @Getter private final File folder;
    @Getter private final PermissionDatabase database;
    private final Collection<Group> groups = new HashSet<>();

    public Permissions(@NotNull final File folder) {
        Preconditions.checkNotNull(folder, "folder");
        this.folder = folder;

        final File configFolder = new File(this.folder, "config");
        if (!configFolder.exists()) {
            if (!configFolder.mkdir()) {
                throw new IllegalStateException("Failed to create configuration directory");
            }
        }

        this.mainCommand = new PermissionCommand(this);
        // Load configurations
        ConfigurationFactory.load(Messages.class, configFolder).get();
        ConfigurationFactory.load(PermissionConfig.class, configFolder).get();
        // Create/load database
        switch (PermissionConfig.databaseType.toLowerCase(Locale.ENGLISH)) {
            case "yaml": {
                this.database = new YamlPermissionDatabase(this, folder);
            } break;
            default: {
                throw new IllegalArgumentException(String.format("Unknown database type: %s",
                    PermissionConfig.databaseType));
            }
        }
        // Load groups from database
        this.database.loadGroups();
        // check if the default group is loaded, otherwise insert it
        if (!getGroupByName("default").isPresent()) {
            final Group defaultGroup = new Group("default", null);
            this.groups.add(defaultGroup);
        }
    }

    @NotNull public Optional<Group> getGroupByName(@NotNull final String name) {
        Preconditions.checkNotNull(name, "name");
        for (final Group group : this.groups) {
            if (group.getName().equalsIgnoreCase(name)) {
                return Optional.of(group);
            }
        }
        return Optional.empty();
    }

    public boolean registerGroup(@NotNull final Group group) {
        Preconditions.checkNotNull(group, "group");
        if (getGroupByName(group.getName()).isPresent()) {
            return false;
        }
        return this.groups.add(group);
    }

    @NotNull public Collection<Group> getGroups() {
        return Collections.unmodifiableCollection(this.groups);
    }

}
