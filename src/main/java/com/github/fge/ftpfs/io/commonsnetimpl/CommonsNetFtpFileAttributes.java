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

package com.github.fge.ftpfs.io.commonsnetimpl;

import org.apache.commons.net.ftp.FTPFile;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public final class CommonsNetFtpFileAttributes
    implements BasicFileAttributes
{
    private final FTPFile ftpFile;

    public CommonsNetFtpFileAttributes(final FTPFile ftpFile)
    {
        this.ftpFile = ftpFile;
    }

    @Override
    public FileTime lastModifiedTime()
    {
        return FileTime.fromMillis(ftpFile.getTimestamp().getTimeInMillis());
    }

    @Override
    public FileTime lastAccessTime()
    {
        return lastModifiedTime();
    }

    @Override
    public FileTime creationTime()
    {
        return lastModifiedTime();
    }

    @Override
    public boolean isRegularFile()
    {
        return ftpFile.isFile();
    }

    @Override
    public boolean isDirectory()
    {
        return ftpFile.isDirectory();
    }

    @Override
    public boolean isSymbolicLink()
    {
        return false;
    }

    @Override
    public boolean isOther()
    {
        return false;
    }

    @Override
    public long size()
    {
        return ftpFile.getSize();
    }

    @Override
    public Object fileKey()
    {
        return null;
    }
}
