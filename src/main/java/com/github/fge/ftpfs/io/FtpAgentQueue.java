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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class FtpAgentQueue
    implements Closeable
{
    private final BlockingQueue<FtpAgent> agents;
    private final FtpConfiguration cfg;

    public FtpAgentQueue(final FtpAgentFactory provider,
        final FtpConfiguration cfg, final int maxAgents)
    {
        agents = new ArrayBlockingQueue<>(maxAgents);
        this.cfg = cfg;
        for (int i = 0; i < maxAgents; i++)
            agents.add(provider.get(this, cfg));
    }

    public FtpAgent getAgent()
        throws IOException
    {
        try {
            return agents.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted!", e);
        }
    }

    public void pushBack(final FtpAgent agent)
    {
        agents.add(agent);
    }

    @Override
    public void close()
        throws IOException
    {
        IOException toThrow = null;

        final List<FtpAgent> list = new ArrayList<>();
        agents.drainTo(list);
        for (final FtpAgent agent: list)
            try {
                agent.disconnect();
            } catch (IOException e) {
                if (toThrow == null)
                    toThrow = e;
            }

        if (toThrow != null)
            throw toThrow;
    }
}
