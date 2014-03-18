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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
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
    implements Iterable<String>
{
    private static final SlashDelimitedPath ROOT
        = new SlashDelimitedPath(Collections.<String>emptyList(), true, true);
    private static final SlashDelimitedPath EMPTY
        = new SlashDelimitedPath(Collections.<String>emptyList(), false, true);

    protected static final char SLASH = '/';

    protected static final String SELF = ".";
    protected static final String PARENT = "..";

    private static final Pattern SLASHES = Pattern.compile("/+");

    protected final List<String> components;
    private final String asString;

    protected final boolean absolute;
    protected final boolean normalized;

    private SlashDelimitedPath(final List<String> components,
        final boolean absolute, final boolean normalized)
    {
        this.components = Collections.unmodifiableList(components);
        this.absolute = absolute;
        this.normalized = normalized;
        asString = toString(absolute, components);
    }

    public static SlashDelimitedPath fromString(final String input)
    {
        Objects.requireNonNull(input, "null argument is not allowed");

        if (input.isEmpty())
            return EMPTY;

        String s = input;
        final boolean absolute = s.charAt(0) == '/';
        if (absolute)
            s = SLASHES.matcher(input).replaceFirst("");

        if (s.isEmpty())
            return ROOT;

        final List<String> components = Arrays.asList(SLASHES.split(s));
        final boolean normalized = isNormalized(components);
        return new SlashDelimitedPath(components, absolute, normalized);
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
        return normalized;
    }

    public SlashDelimitedPath resolve(final SlashDelimitedPath other)
    {
        if (other.absolute)
            return other;
        if (other.components.isEmpty())
            return this;
        final List<String> list = new ArrayList<>(components);
        list.addAll(other.components);
        return new SlashDelimitedPath(list, absolute, isNormalized(list));
    }

    public SlashDelimitedPath normalize()
    {
        if (normalized)
            return this;
        final Deque<String> deque = new ArrayDeque<>();
        int nrComponents = 0;
        boolean isParent;
        for (final String component: components) {
            if (SELF.equals(component))
                continue;
            isParent = PARENT.equals(component);
            if (isParent && nrComponents > 0) {
                deque.pollLast();
                nrComponents--;
                continue;
            }
            deque.add(component);
            if (!isParent)
                nrComponents++;
        }

        return new SlashDelimitedPath(new ArrayList<>(deque), absolute, true);
    }

    @Override
    public Iterator<String> iterator()
    {
        return components.iterator();
    }

    @Override
    public int hashCode()
    {
        return asString.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final SlashDelimitedPath other = (SlashDelimitedPath) obj;
        return asString.equals(other.asString);
    }

    @Override
    public String toString()
    {
        return asString;
    }

    private static String toString(final boolean absolute,
        final List<String> list)
    {
        final String s = absolute ? "/" : "";
        if (list.isEmpty())
            return s;
        final StringBuilder sb = new StringBuilder(s).append(list.get(0));
        final int size = list.size();

        for (int i = 1; i < size; i++)
            sb.append('/').append(list.get(i));

        return sb.toString();
    }

    private static boolean isNormalized(final List<String> components)
    {
        /*
         * Note: Paths.get("").normalize() throws an
         * ArrayIndexOutOfBoundsException; we don't
         */
        if (components.isEmpty())
            return true;

        final int size = components.size();
        int lastParent = -1, lastNonParent = -1;
        String component;

        for (int index = 0; index < size; index++) {
            component = components.get(index);
            if (SELF.equals(component))
                return false;
            if (PARENT.equals(component))
                lastParent = index;
            else
                lastNonParent = index;
        }

        return lastParent == -1 || lastNonParent == -1
            || lastNonParent > lastParent;
    }
}
