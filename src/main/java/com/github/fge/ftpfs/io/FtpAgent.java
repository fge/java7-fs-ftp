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

package com.github.fge.ftpfs.io;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.AccessMode;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;

public interface FtpAgent
    extends Closeable
{
    FtpFileView getFileView(final String name)
        throws IOException;

    EnumSet<AccessMode> getAccess(final String name)
        throws IOException;

    List<String> getDirectoryNames(final String dir)
        throws IOException;

    FtpInputStream getInputStream(final Path path)
        throws IOException;

    boolean isDead();

    void completeTransfer()
        throws IOException;

    void connect()
        throws IOException;

    void disconnect()
        throws IOException;
}
