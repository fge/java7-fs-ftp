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

import java.io.IOException;
import java.io.InputStream;

public final class FtpInputStream
    extends InputStream
{
    private final FtpAgent agent;
    private final InputStream stream;

    public FtpInputStream(final FtpAgent agent, final InputStream stream)
    {
        this.agent = agent;
        this.stream = stream;
    }

    @Override
    public int read()
        throws IOException
    {
        return stream.read();
    }

    @Override
    public int read(final byte[] b)
        throws IOException
    {
        return stream.read(b);
    }

    @Override
    public int read(final byte[] b, final int off, final int len)
        throws IOException
    {
        return stream.read(b, off, len);
    }

    @Override
    public long skip(final long n)
        throws IOException
    {
        return stream.skip(n);
    }

    @Override
    public int available()
        throws IOException
    {
        return stream.available();
    }

    @Override
    public synchronized void mark(final int readlimit)
    {
        stream.mark(readlimit);
    }

    @Override
    public synchronized void reset()
        throws IOException
    {
        stream.reset();
    }

    @Override
    public boolean markSupported()
    {
        return stream.markSupported();
    }

    @Override
    public void close()
        throws IOException
    {
        stream.close();
        agent.completeTransfer();
        agent.close();
    }
}
