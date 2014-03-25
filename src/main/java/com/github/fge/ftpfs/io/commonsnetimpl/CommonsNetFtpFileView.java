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

import com.github.fge.ftpfs.io.FtpFileView;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.nio.file.AccessMode;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public final class CommonsNetFtpFileView
    implements FtpFileView
{
    private static final int[] ACCESS_TYPES = {
        FTPFile.USER_ACCESS, FTPFile.GROUP_ACCESS, FTPFile.WORLD_ACCESS
    };
    private static final Map<Integer, AccessMode> ACCESS_MAP;

    static {
        final Map<Integer, AccessMode> map = new HashMap<>();
        map.put(FTPFile.READ_PERMISSION, AccessMode.READ);
        map.put(FTPFile.WRITE_PERMISSION, AccessMode.WRITE);
        map.put(FTPFile.EXECUTE_PERMISSION, AccessMode.EXECUTE);
        ACCESS_MAP = Collections.unmodifiableMap(map);
    }

    private final FTPFile ftpFile;

    public CommonsNetFtpFileView(final FTPFile ftpFile)
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

    @Override
    public Collection<AccessMode> getAccess()
    {
        final EnumSet<AccessMode> ret = EnumSet.noneOf(AccessMode.class);
        next:
        for (final int type: ACCESS_TYPES)
            for (final Map.Entry<Integer, AccessMode> entry:
                ACCESS_MAP.entrySet())
                if (ftpFile.hasPermission(type, entry.getKey())) {
                    ret.add(entry.getValue());
                    continue next;
                }

        return EnumSet.copyOf(ret);
    }
}
