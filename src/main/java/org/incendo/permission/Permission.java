package org.incendo.permission;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

@RequiredArgsConstructor
public final class Permission {

    private final String name;
    private final PermissionNode[] nodes;
    @Getter private final boolean inverted;
    @Getter private final boolean priority;

    /**
     * Create a new permission from a permission string
     *
     * @param string Permission string to compile into a permission
     * @return compiled permission
     */
    @NotNull @Contract("_ -> new") public static Permission of(@NotNull String string) {
        Preconditions.checkNotNull(string, "string");

        final boolean inverted = !string.isEmpty() && (string.startsWith("-") || (string.length() >= 2
            && string.charAt(1) == '-'));
        final boolean priority = !string.isEmpty() && (string.startsWith("!") || (string.length() >= 2
            && string.charAt(1) == '!'));

        final String rawString;
        if ((inverted && !priority) || (!inverted && priority)) {
            rawString = string.toLowerCase(Locale.ENGLISH).substring(1);
        } else if (inverted /* && priority <-- implied*/ ) {
            rawString = string.toLowerCase(Locale.ENGLISH).substring(2);
        } else {
            rawString = string.toLowerCase(Locale.ENGLISH);
        }

        final String[] parts = string.split("\\.");
        final PermissionNode[] nodes = new PermissionNode[parts.length];
        for (int i = 0; i < parts.length; i++) {
            nodes[i] = PermissionNode.of(parts[i]);
        }
        return new Permission(rawString, nodes, inverted, priority);
    }

    @Contract(pure = true) @NotNull public String getRawName() {
        return this.name;
    }

    @Contract(pure = true) public boolean getValue() {
        return !this.inverted;
    }

    public boolean matches(@NotNull final String test) {
        final String[] parts = test.split("\\.");

        boolean wildcard = false;
        for (int i = 0; i < parts.length; i++) {
            PermissionNode node = null;
            if (i < nodes.length) {
                node = nodes[i];
                wildcard = node.isWildcard();
            }
            // no current node, and the last node wasn't a wildcard
            // or the current node doesn't match
            if ((node == null && !wildcard) || (node != null && !node.matches(parts[i]))) {
                return false;
            }
        }

        return true;
    }

    @NotNull @Override public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (inverted) {
            builder.append("-");
        }
        if (priority) {
            builder.append("!");
        }
        for (int i = 0; i < nodes.length; i++) {
            builder.append(nodes[i].toString());
            if ((i + 1) < nodes.length) {
                builder.append(".");
            }
        }
        return builder.toString();
    }

}
