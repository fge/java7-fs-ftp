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
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public final class FTPPath
    implements Path
{
    @Override
    public FileSystem getFileSystem()
    {
        return null;
    }

    @Override
    public boolean isAbsolute()
    {
        return false;
    }

    @Override
    public Path getRoot()
    {
        return null;
    }

    @Override
    public Path getFileName()
    {
        return null;
    }

    @Override
    public Path getParent()
    {
        return null;
    }

    @Override
    public int getNameCount()
    {
        return 0;
    }

    @Override
    public Path getName(int index)
    {
        return null;
    }

    @Override
    public Path subpath(int beginIndex, int endIndex)
    {
        return null;
    }

    @Override
    public boolean startsWith(Path other)
    {
        return false;
    }

    @Override
    public boolean startsWith(String other)
    {
        return false;
    }

    @Override
    public boolean endsWith(Path other)
    {
        return false;
    }

    @Override
    public boolean endsWith(String other)
    {
        return false;
    }

    @Override
    public Path normalize()
    {
        return null;
    }

    @Override
    public Path resolve(Path other)
    {
        return null;
    }

    @Override
    public Path resolve(String other)
    {
        return null;
    }

    @Override
    public Path resolveSibling(Path other)
    {
        return null;
    }

    @Override
    public Path resolveSibling(String other)
    {
        return null;
    }

    @Override
    public Path relativize(Path other)
    {
        return null;
    }

    @Override
    public URI toUri()
    {
        return null;
    }

    @Override
    public Path toAbsolutePath()
    {
        return null;
    }

    @Override
    public Path toRealPath(LinkOption... options)
        throws IOException
    {
        return null;
    }

    @Override
    public File toFile()
    {
        return null;
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events,
        WatchEvent.Modifier... modifiers)
        throws IOException
    {
        return null;
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events)
        throws IOException
    {
        return null;
    }

    @Override
    public Iterator<Path> iterator()
    {
        return null;
    }

    @Override
    public int compareTo(Path other)
    {
        return 0;
    }
}
