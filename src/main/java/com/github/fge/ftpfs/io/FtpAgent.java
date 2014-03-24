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
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.util.List;

public abstract class FtpAgent
    implements Closeable
{
    private final FtpAgentQueue queue;

    protected FtpAgent(final FtpAgentQueue queue)
        throws IOException
    {
        this.queue = queue;
    }

    public abstract BasicFileAttributeView getAttributeView(final String name)
        throws IOException;

    public abstract List<String> getDirectoryNames(final String dir)
        throws IOException;

    protected abstract InputStream openInputStream(final String file)
        throws IOException;

    public final FtpInputStream getInputStream(final Path path)
        throws IOException
    {
        final InputStream stream = openInputStream(path.toString());
        return new FtpInputStream(this, stream);
    }

    @Override
    public final void close()
        throws IOException
    {
        queue.pushBack(this);
    }

    protected abstract void disconnect()
        throws IOException;
}
