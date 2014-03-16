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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public abstract class FTPPath
    implements Path
{
    private final FTPFileSystem fs;

    protected FTPPath(final FTPFileSystem fs)
    {
        this.fs = fs;
    }

    @Override
    public final FileSystem getFileSystem()
    {
        return fs;
    }

    @Override
    public final File toFile()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public final WatchKey register(final WatchService watcher,
        final WatchEvent.Kind<?>[] events,
        final WatchEvent.Modifier... modifiers)
        throws IOException
    {
        throw new IOException("operation not supported");
    }

    @Override
    public final WatchKey register(final WatchService watcher,
        final WatchEvent.Kind<?>... events)
        throws IOException
    {
        throw new IOException("operation not supported");
    }

    @Override
    public final Iterator<Path> iterator()
    {
        return null;
    }

    @Override
    public final int compareTo(final Path other)
    {
        return 0;
    }
}
