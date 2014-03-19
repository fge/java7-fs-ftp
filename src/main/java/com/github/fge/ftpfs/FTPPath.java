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

import com.github.fge.ftpfs.path.SlashPath;

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
    private static final SlashPath ROOT = SlashPath.fromString("/");

    private final FTPFileSystem fs;
    private final SlashPath path;

    public FTPPath(final FTPFileSystem fs, final SlashPath path)
    {
        this.fs = fs;
        this.path = path;
    }

    @Override
    public FileSystem getFileSystem()
    {
        return fs;
    }

    @Override
    public boolean isAbsolute()
    {
        return path.isAbsolute();
    }

    @Override
    public Path getRoot()
    {
        return isAbsolute() ? new FTPPath(fs, ROOT) : null;
    }

    @Override
    public Path getFileName()
    {
        return new FTPPath(fs, path.getLastName());
    }

    @Override
    public Path getParent()
    {
        return new FTPPath(fs, path.getParent());
    }

    @Override
    public int getNameCount()
    {
        return path.getNameCount();
    }

    @Override
    public Path getName(final int index)
    {
        return new FTPPath(fs, path.getName(index));
    }

    @Override
    public Path subpath(final int beginIndex, final int endIndex)
    {
        return new FTPPath(fs, path.subpath(beginIndex, endIndex));
    }

    @Override
    public boolean startsWith(final Path other)
    {
        if (!fs.equals(other.getFileSystem()))
            return false;
        final FTPPath otherPath = (FTPPath) other;
        return path.startsWith(otherPath.path);
    }

    @Override
    public boolean startsWith(final String other)
    {
        return startsWith(new FTPPath(fs, SlashPath.fromString(other)));
    }

    @Override
    public boolean endsWith(final Path other)
    {
        if (!fs.equals(other.getFileSystem()))
            return false;
        final FTPPath otherPath = (FTPPath) other;
        return path.endsWith(otherPath.path);
    }

    @Override
    public boolean endsWith(final String other)
    {
        return endsWith(new FTPPath(fs, SlashPath.fromString(other)));
    }

    @Override
    public Path normalize()
    {
        return path.isNormalized() ? this
            : fs.getPath(path.normalize().toString());
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
        // TODO: how do you do that?
        return null;
    }

    @Override
    public Path toRealPath(final LinkOption... options)
        throws IOException
    {
        // TODO: symlink support
        return toAbsolutePath();
    }

    @Override
    public File toFile()
    {
        return new File(path.toString());
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
        return new Iterator<Path>()
        {
            private final Iterator<String> it = path.iterator();

            @Override
            public boolean hasNext()
            {
                return it.hasNext();
            }

            @Override
            public Path next()
            {
                return fs.getPath(it.next());
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int compareTo(final Path other)
    {
        return path.toString().compareTo(other.toString());
    }
}
