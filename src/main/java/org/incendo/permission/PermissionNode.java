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
import com.google.common.collect.Maps;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ToString(of = "rawNode")
@EqualsAndHashCode(of = "rawNode")
public class PermissionNode {

    private static final Map<String, PermissionNode> NODE_CACHE = Maps.newHashMap();

    private final String rawNode;
    private final Pattern pattern;

    private PermissionNode(@NotNull final String rawNode, @Nullable final Pattern pattern) {
        Preconditions.checkNotNull(rawNode, "raw node");
        this.rawNode = rawNode;
        this.pattern = pattern;
    }

    /**
     * Create a new {@link PermissionNode} from a given {@link String}
     *
     * @param string String to create the node from
     * @return created node
     */
    @NotNull public static PermissionNode of(@NotNull String string) {
        Preconditions.checkNotNull(string, "string");
        // check if the node is a true wildcard node
        if (string.equals("*")) {
            return new WildcardNode();
        }
        // make the string lowercase, as permission nodes are case independent
        string = string.toLowerCase(Locale.ENGLISH);
        // check the node cache
        if (NODE_CACHE.containsKey(string)) {
            return NODE_CACHE.get(string);
        }
        final String originalString = string;
        // replace wildcard expressions
        if (string.contains("*")) {
            string = string.replaceAll("\\*", "\\[0-9a-z\\]\\+");
        }
        // compile the pattern and create a new node
        final Pattern pattern = Pattern.compile(String.format("^%s+", string));
        final PermissionNode node = new PermissionNode(originalString, pattern);
        // store the node to the cache
        NODE_CACHE.put(originalString, node);
        // return the created node
        return node;
    }

    /**
     * Test whether the given string matches with the node
     * or not
     *
     * @param testString String to test
     * @return {@code true} if the string matches, else {@code false}
     */
    public boolean matches(@NotNull final String testString) {
        Preconditions.checkNotNull(testString, "string");
        final Matcher matcher = pattern.matcher(testString.toLowerCase(Locale.ENGLISH));
        return matcher.matches();
    }

    /**
     * Check whether this permission node is a true wildcard, i.e a "*" node
     *
     * @return {@code true} if the node is a true wildcard node
     */
    public boolean isWildcard() {
        return false;
    }

    /**
     * A true wildcard node, i.e "*"
     */
    public static final class WildcardNode extends PermissionNode {

        private WildcardNode() {
            super("*",null);
        }

        @Contract(pure = true) @Override public boolean matches(@NotNull String testString) {
            return true;
        }

        @Contract(pure = true) @Override public boolean isWildcard() {
            return true;
        }
    }

}
