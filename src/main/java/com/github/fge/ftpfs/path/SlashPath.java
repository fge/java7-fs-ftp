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

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
 * <p>A path is considered <strong>absolute</strong> if it begins with a {@code
 * /}. It is considered <strong>normalized</strong> if there are not dot path
 * elements, and there are no parent path elements {@code ..} after a non parent
 * path element (that is, {@code ..} is normalized, but {@code a/..} isn't).</p>
 *
 * <p>This class also contains methods to resolve and relativize paths to one
 * another, and to normalize paths.</p>
 *
 * @see Path
 */
public final class SlashPath
    implements Comparable<SlashPath>, Iterable<String>
{
    public static final SlashPath ROOT
        = new SlashPath(Collections.<String>emptyList(), true);
    public static final SlashPath EMPTY
        = new SlashPath(Collections.<String>emptyList(), false);

    private static final String SELF = ".";
    private static final String PARENT = "..";

    private static final Pattern SLASHES = Pattern.compile("/+");

    private final List<String> components;
    private final String asString;

    private final boolean absolute;
    private final boolean normalized;

    private SlashPath(final List<String> components, final boolean absolute)
    {
        this.components = Collections.unmodifiableList(components);
        this.absolute = absolute;
        normalized = isNormalized(components);
        asString = toString(absolute, components);
    }

    /**
     * Static factory method
     *
     * @param input the path as a string
     * @return a new path
     * @throws NullPointerException input is null
     */
    public static SlashPath fromString(final String input)
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
        return new SlashPath(components, absolute);
    }

    /**
     * Is this path absolute?
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
     * @return true if the path is normalized
     */
    public boolean isNormalized()
    {
        return normalized;
    }

    /**
     * Resolve this path against another path
     *
     * @param other the other path
     * @return the resolved path
     * @see Path#resolve(Path)
     */
    public SlashPath resolve(final SlashPath other)
    {
        if (other.absolute)
            return other;
        if (other.components.isEmpty())
            return this;
        final List<String> list = new ArrayList<>(components);
        list.addAll(other.components);
        return new SlashPath(list, absolute);
    }

    /**
     * Normalize this path
     *
     * <p>Normalization consists of removing all dots and simplifying parent
     * entries where possible.</p>
     *
     * <p>For instance, normalizing {@code a/b/../c/.} returns {@code a/c}.</p>
     *
     * @return a normalized path
     * @see Path#normalize()
     */
    public SlashPath normalize()
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

        return new SlashPath(new ArrayList<>(deque), absolute);
    }

    /*
     * FIXME: the results of native Unix Path's relativization is most bizarre
     * (buggy?) if any one is not normalized; here we only take normalized paths
     *
     * Also, it refuses to relativize if one path is absolute and the other
     * relative.
     *
     * We mimic this behaviour here.
     */

    /**
     * Relativize a path against the current path
     *
     * <p>Note that this does <strong>not</strong> behave the way {@link
     * Path#relativize(Path)} does; in particular, this method
     * will always normalize both paths before calculating the relative path.
     * </p>
     *
     * @param other the path to relativize against
     * @return the relativized path
     * @throws IllegalArgumentException one path is absolute and the other is
     * not
     *
     * @see Path#relativize(Path)
     */
    public SlashPath relativize(final SlashPath other)
    {
        if (absolute ^ other.absolute)
            throw new IllegalArgumentException("both paths must be either " +
                "relative or absolute");

        final SlashPath src = normalized ? this : normalize();
        final SlashPath dst = other.normalized ? other
            : other.normalize();

        if (src.equals(dst))
            return new SlashPath(Collections.<String>emptyList(), false);

        final List<String> list = new ArrayList<>();

        final ListIterator<String> srcIterator = src.components.listIterator();
        final ListIterator<String> dstIterator = dst.components.listIterator();

        String srcComponent, dstComponent;

        /*
         * FIRST STEP
         *
         * While both iterators are not empty, grab the next component of both.
         *
         * If both are equal, nothing to do. If there is a difference, it means
         * we need to go to the parent directory, and advance only the source
         * iterator.
         */
        while (srcIterator.hasNext() && dstIterator.hasNext()) {
            srcComponent = srcIterator.next();
            dstComponent = dstIterator.next();
            if (srcComponent.equals(dstComponent))
                continue;
            list.add(PARENT);
            dstIterator.previous();
        }

        /*
         * SECOND STEP
         *
         * When we are here, either the source or the destination iterator is
         * empty.
         *
         * Add the remaining components of the destination iterator (if any) to
         * reach the path, then go up as many levels as there are remaining
         * components in the source iterator.
         */
        while (dstIterator.hasNext())
            list.add(dstIterator.next());
        for (; srcIterator.hasNext(); srcIterator.next())
            list.add(PARENT);
        return new SlashPath(list, false);
    }

    /**
     * Get the number of path elements in this path
     *
     * @return the number of path elements; 0 if path is empty or {@code /}
     *
     * @see Path#getNameCount()
     */
    public int getNameCount()
    {
        return components.size();
    }

    public SlashPath getName(final int index)
    {
        if (components.isEmpty())
            throw new IllegalArgumentException("path has no elements");
        if (index < 0 || index >= components.size())
            throw new IllegalArgumentException("invalid index " + index);
        return SlashPath.fromString(components.get(index));
    }

    public SlashPath getLastName()
    {
        return components.isEmpty() ? null : getName(components.size() - 1);
    }

    // TODO: getRoot()?
    public SlashPath getParent()
    {
        if (components.isEmpty())
            return null;
        if (components.size() == 1)
            return absolute ? ROOT : null;
        return new SlashPath(components.subList(0, components.size() - 1),
            absolute);
    }

    public SlashPath subpath(final int start, final int end)
    {
        if (start < 0 || start >= components.size())
            throw new IllegalArgumentException("illegal start index");
        if (end <= start)
            throw new IllegalArgumentException("end index must be greater " +
                "than start index");
        if (end > components.size())
            throw new IllegalArgumentException("illegal end index");
        return new SlashPath(components.subList(start, end), false);
    }

    public boolean startsWith(final SlashPath other)
    {
        if (asString.equals(other.asString))
            return true;
        if (absolute ^ other.absolute)
            return false;
        if (other.components.isEmpty())
            return false;
        return Collections.indexOfSubList(components, other.components) == 0;
    }

    public boolean endsWith(final SlashPath other)
    {
        if (asString.equals(other.asString))
            return true;
        if (other.absolute)
            return false;
        if (other.components.isEmpty())
            return false;
        final int expected = components.size() - other.components.size();
        if (expected < 0)
            return false;
        final int actual
            = Collections.lastIndexOfSubList(components, other.components);
        return expected == actual;
    }

    @Override
    public int compareTo(final SlashPath o)
    {
        return asString.compareTo(o.asString);
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
        final SlashPath other = (SlashPath) obj;
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
        boolean seenNonParent = false;

        for (final String component: components) {
            if (SELF.equals(component))
                return false;
            if (!PARENT.equals(component)) {
                seenNonParent = true;
                continue;
            }
            if (seenNonParent)
                return false;
        }

        return true;
    }
}
