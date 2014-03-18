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

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;

public final class FTPFileSystem
    extends FileSystem
{
    private final FTPFileSystemProvider provider;

    public FTPFileSystem(FTPFileSystemProvider provider)
    {
        this.provider = provider;
    }

    @Override
    public FileSystemProvider provider()
    {
        return provider;
    }

    @Override
    public void close()
        throws IOException
    {

    }

    @Override
    public boolean isOpen()
    {
        return false;
    }

    @Override
    public boolean isReadOnly()
    {
        return false;
    }

    @Override
    public String getSeparator()
    {
        return "/";
    }

    @Override
    public Iterable<Path> getRootDirectories()
    {
        return null;
    }

    @Override
    public Iterable<FileStore> getFileStores()
    {
        return null;
    }

    @Override
    public Set<String> supportedFileAttributeViews()
    {
        return null;
    }

    @Override
    public Path getPath(String first, String... more)
    {
        return null;
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern)
    {
        return null;
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService()
    {
        return null;
    }

    @Override
    public WatchService newWatchService()
        throws IOException
    {
        return null;
    }
}
