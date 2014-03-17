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

    public boolean isAbsolute()
    {
        return absolute;
    }

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
