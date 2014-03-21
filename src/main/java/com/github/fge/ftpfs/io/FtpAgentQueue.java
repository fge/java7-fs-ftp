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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class FtpAgentQueue
{
    private final BlockingQueue<FtpAgent> agents;

    public FtpAgentQueue(final FtpAgentProvider provider, final int maxAgents)
    {
        agents = new ArrayBlockingQueue<>(maxAgents);
        for (int i = 0; i < maxAgents; i++)
            agents.add(provider.get());
    }

    public FtpAgent getAgent()
        throws InterruptedException
    {
        return agents.take();
    }

    public void pushBack(final FtpAgent agent)
    {
        agents.add(agent);
    }
}
