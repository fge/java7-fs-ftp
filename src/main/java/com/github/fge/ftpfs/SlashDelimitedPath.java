/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.ftpfs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Utility class holding a slash-delimited path
 *
 * <p>This class mimicks the JDK's implementation of Oracle's/OpenJDK's {@code
 * UnixPath}. That is:</p>
 *
 * <ul>
 *     <li>trailing slashes are removed from the input (ie, {@code foo/} becomes
 *     {@code foo};</li>
 *     <li>extra slashes are removed (ie, {@code //foo/..//bar} becomes {@code
 *     /foo/../bar}).</li>
 * </ul>
 *
 * <p>It also provides utility methods to tell whether the path is absolute
 * (ie, begins with a {@code /}) and normalized (ie, there is no {@code .} or
 * {@code ..} in path components).</p>
 */
public final class SlashDelimitedPath
{
    private static final char SLASH = '/';

    private static final String SELF = ".";
    private static final String PARENT = "..";

    private static final Pattern SLASHES = Pattern.compile("/+");

    private final String asString;
    private final List<String> components = new ArrayList<>();

    private final boolean absolute;

    public SlashDelimitedPath(final String input)
    {
        Objects.requireNonNull(input, "null argument is not allowed");
        absolute = input.charAt(0) == SLASH;

        final StringBuilder sb = new StringBuilder();

        for (final String component: SLASHES.split(input))  {
            if (component.isEmpty())
                continue;
            components.add(component);
            sb.append(SLASH).append(component);
        }

        if (!absolute)
            sb.deleteCharAt(0);
        asString = sb.toString();
    }

    /**
     * Is this path absolute?
     *
     * <p>A path is absolute only if it begins with a {@code /}.</p>
     *
     * @return true if the path is absolute
     */
    public boolean isAbsolute()
    {
        return absolute;
    }

    /**
     * Is this path normalized?
     *
     * <p>A path is normalized only if it has no {@code .} or {@code ..} path
     * elements.</p>
     *
     * @return true if the path is normalized
     */
    public boolean isNormalized()
    {
        for (final String component: components)
            if (SELF.equals(component) || PARENT.equals(component))
                return false;
        return true;
    }

    @Override
    public String toString()
    {
        return asString;
    }
}
