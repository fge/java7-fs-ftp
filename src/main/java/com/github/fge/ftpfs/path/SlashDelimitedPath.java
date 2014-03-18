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

package com.github.fge.ftpfs.path;

import java.util.Arrays;
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
public abstract class SlashDelimitedPath
{
    protected static final char SLASH = '/';

    protected static final String SELF = ".";
    protected static final String PARENT = "..";

    private static final Pattern SLASHES = Pattern.compile("/+");

    public static SlashDelimitedPath fromString(final String input)
    {
        Objects.requireNonNull(input, "null argument is not allowed");

        if (input.isEmpty())
            return EmptySlashDelimitedPath.INSTANCE;

        String s = input;
        final boolean absolute = s.charAt(0) == '/';
        if (absolute)
            s = SLASHES.matcher(input).replaceFirst("");

        if (s.isEmpty())
            return RootSlashDelimitedPath.INSTANCE;

        final List<String> components = Arrays.asList(SLASHES.split(s));
        final boolean normalized = !(components.contains(SELF)
            || components.contains(PARENT));

        return new FullSlashDelimitedPath(absolute, normalized, components);
    }

    /**
     * Is this path absolute?
     *
     * <p>A path is absolute only if it begins with a {@code /}.</p>
     *
     * @return true if the path is absolute
     */
    public abstract boolean isAbsolute();

    /**
     * Is this path normalized?
     *
     * <p>A path is normalized only if it has no {@code .} or {@code ..} path
     * elements.</p>
     *
     * @return true if the path is normalized
     */
    public abstract boolean isNormalized();

    @Override
    public final int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public final boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        return obj instanceof SlashDelimitedPath
            && toString().equals(obj.toString());
    }

    @Override
    public abstract String toString();
}
