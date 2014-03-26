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

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * A wrapped FTP data connection
 */
public final class FtpInputStream
    extends InputStream
{
    private final FtpAgent agent;
    private final InputStream stream;

    /**
     * Constructor
     *
     * @param agent the agent to use
     * @param stream the FTP data connection as a stream
     */
    public FtpInputStream(@Nonnull final FtpAgent agent,
        @Nonnull final InputStream stream)
    {
        this.agent = Objects.requireNonNull(agent, "agent is null");
        this.stream = Objects.requireNonNull(stream, "input stream is null");
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

    /**
     * Close the input stream
     *
     * <p>This first closes the underlying data stream, then checks the
     * FTP transfer status before finally closing the agent.</p>
     *
     * @throws IOException failure to close the stream, or FTP command did not
     * complete properly
     * @see FtpAgent#completeTransfer()
     */
    @Override
    public void close()
        throws IOException
    {
        try {
            stream.close();
        } catch (IOException ignored) {
        }
        try {
            agent.completeTransfer();
        } catch (IOException ignored) {
        }
        agent.close();
    }
}
