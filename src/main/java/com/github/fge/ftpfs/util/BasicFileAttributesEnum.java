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

package com.github.fge.ftpfs.util;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public enum BasicFileAttributesEnum
{
    CREATION_TIME("creationTime")
    {
        @Override
        public Object getValue(final BasicFileAttributes attributes)
        {
            return attributes.creationTime();
        }
    },
    FILE_KEY("fileKey")
    {
        @Override
        public Object getValue(final BasicFileAttributes attributes)
        {
            return attributes.fileKey();
        }
    },
    IS_DIRECTORY("isDirectory")
    {
        @Override
        public Object getValue(final BasicFileAttributes attributes)
        {
            return attributes.isDirectory();
        }
    },
    IS_REGULAR_FILE("isRegularFile")
    {
        @Override
        public Object getValue(final BasicFileAttributes attributes)
        {
            return attributes.isRegularFile();
        }
    },
    IS_SYMBOLIC_LINK("isSymbolicLink")
    {
        @Override
        public Object getValue(final BasicFileAttributes attributes)
        {
            return attributes.isSymbolicLink();
        }
    },
    IS_OTHER("isOther")
    {
        @Override
        public Object getValue(final BasicFileAttributes attributes)
        {
            return attributes.isOther();
        }
    },
    LAST_ACCESS_TIME("lastAccessTime")
    {
        @Override
        public Object getValue(final BasicFileAttributes attributes)
        {
            return attributes.lastAccessTime();
        }
    },
    LAST_MODIFIED_TIME("lastModifiedTime")
    {
        @Override
        public Object getValue(final BasicFileAttributes attributes)
        {
            return attributes.lastModifiedTime();
        }
    },
    SIZE("size")
    {
        @Override
        public Object getValue(final BasicFileAttributes attributes)
        {
            return attributes.size();
        }

    };

    private static final Map<String, BasicFileAttributesEnum> REVERSE_MAP;

    static {
        REVERSE_MAP = new HashMap<>();

        for (final BasicFileAttributesEnum value: values())
            REVERSE_MAP.put(value.name, value);
    }

    public static BasicFileAttributesEnum forName(final String name)
    {
        return REVERSE_MAP.get(name);
    }

    private final String name;

    public abstract Object getValue(final BasicFileAttributes attributes);

    BasicFileAttributesEnum(final String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "basic:" + name;
    }
}
