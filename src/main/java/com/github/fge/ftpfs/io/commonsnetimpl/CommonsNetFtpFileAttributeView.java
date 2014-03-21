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

import com.github.fge.ftpfs.io.FtpFileAttributeView;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public final class CommonsNetFtpFileAttributeView
    implements FtpFileAttributeView
{
    private final FTPFile ftpFile;

    public CommonsNetFtpFileAttributeView(final FTPFile ftpFile)
    {
        this.ftpFile = ftpFile;
    }

    @Override
    public String name()
    {
        return ftpFile.getName();
    }

    @Override
    public BasicFileAttributes readAttributes()
        throws IOException
    {
        return new CommonsNetFtpFileAttributes(ftpFile);
    }

    @Override
    public void setTimes(final FileTime lastModifiedTime,
        final FileTime lastAccessTime, final FileTime createTime)
        throws IOException
    {
        throw new IllegalStateException();
    }
}
