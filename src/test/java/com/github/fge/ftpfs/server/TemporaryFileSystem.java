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

package com.github.fge.ftpfs.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

public final class TemporaryFileSystem
{
    private final Path baseDir;

    public TemporaryFileSystem()
        throws IOException
    {
        final Set<PosixFilePermission> permissions
            = PosixFilePermissions.fromString("rwxr-xr-x");
        baseDir = Files.createTempDirectory("java7-ftp-fs",
            PosixFilePermissions.asFileAttribute(permissions));
    }

    public Path getBaseDir()
    {
        return baseDir;
    }

    public void createDirs(final String dirs, final String perms)
        throws IOException
    {
        final FileAttribute<?> attrs = fromString(perms);
        final Path path = Paths.get(dirs);
        if (path.isAbsolute())
            throw new IllegalArgumentException();
        final Path dst = baseDir.resolve(path);
        Files.createDirectories(dst, attrs);
    }

    public void createFile(final String name, final String perms)
        throws IOException
    {
        final FileAttribute<?> attrs = fromString(perms);
        final Path path = Paths.get(name);
        if (path.isAbsolute())
            throw new IllegalArgumentException();
        final Path dst = baseDir.resolve(path);
        Files.createFile(dst, attrs);
    }

    public void delete()
        throws IOException
    {
        Files.walkFileTree(baseDir, new DeletionFileVisitor());
    }

    private static FileAttribute<?> fromString(final String perms)
    {
        final Set<PosixFilePermission> permissions
            = PosixFilePermissions.fromString(perms);
        return PosixFilePermissions.asFileAttribute(permissions);
    }
}
