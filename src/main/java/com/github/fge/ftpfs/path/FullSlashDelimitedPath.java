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

import java.util.Collections;
import java.util.List;

final class FullSlashDelimitedPath
    extends SlashDelimitedPath
{
    private final String asString;
    private final List<String> components;

    private final boolean absolute;
    private final boolean normalized;

    FullSlashDelimitedPath(final boolean absolute,
        final boolean normalized, final List<String> components)
    {
        this.absolute = absolute;
        this.normalized = normalized;

        final StringBuilder sb = new StringBuilder();
        if (absolute)
            sb.append('/');
        sb.append(components.get(0));

        for (int index = 1; index < components.size(); index++)
            sb.append(SLASH).append(components.get(index));

        asString = sb.toString();
        this.components = Collections.unmodifiableList(components);
    }

    @Override
    public boolean isAbsolute()
    {
        return absolute;
    }

    @Override
    public boolean isNormalized()
    {
        return normalized;
    }

    @Override
    public String toString()
    {
        return asString;
    }
}
