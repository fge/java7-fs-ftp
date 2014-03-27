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

import com.github.fge.ftpfs.FtpConfiguration;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertSame;

public final class FtpAgentQueueTest
{
    private FtpAgentFactory agentFactory;
    private FtpAgent agent1, agent2, agent3;
    private FtpConfiguration cfg;

    @BeforeMethod
    public void initMocks()
    {
        agentFactory = mock(FtpAgentFactory.class);
        agent1 = mock(FtpAgent.class);
        agent2 = mock(FtpAgent.class);
        agent3 = mock(FtpAgent.class);
        cfg = FtpConfiguration.newBuilder().setHostname("foo").build();
    }

    @Test
    public void agentsAreNotCreatedOnInit()
    {
        final int maxAgents = 3;
        new FtpAgentQueue(agentFactory, cfg, maxAgents);
        verify(agentFactory, never()).get(any(FtpAgentQueue.class),
            any(FtpConfiguration.class));
    }

    @Test(dependsOnMethods = "agentsAreNotCreatedOnInit")
    public void agentsAreTakenInOrder()
        throws IOException
    {
        final int maxAgents = 3;
        final FtpAgentQueue queue = new FtpAgentQueue(agentFactory, cfg,
            maxAgents);
        when(agentFactory.get(same(queue), same(cfg)))
            .thenReturn(agent1).thenReturn(agent2).thenReturn(agent3);
        final InOrder inOrder = inOrder(agentFactory, agent1, agent2, agent3);
        FtpAgent agent;
        agent = queue.getAgent();
        assertSame(agent, agent1);
        agent = queue.getAgent();
        assertSame(agent, agent2);
        agent = queue.getAgent();
        assertSame(agent, agent3);
        inOrder.verify(agentFactory, times(maxAgents))
            .get(same(queue), same(cfg));
        inOrder.verify(agent1).connect();
        inOrder.verify(agent2).connect();
        inOrder.verify(agent3).connect();
        inOrder.verifyNoMoreInteractions();
    }

    @Test(dependsOnMethods = "agentsAreTakenInOrder")
    public void deadAgentsAreScrapped()
        throws IOException
    {
        final int maxAgents = 3;
        final FtpAgentQueue queue = new FtpAgentQueue(agentFactory, cfg,
            maxAgents);
        when(agent1.isDead()).thenReturn(true);
        when(agentFactory.get(same(queue), same(cfg)))
            .thenReturn(agent1).thenReturn(agent2);

        final FtpAgent agent = queue.getAgent();
        assertSame(agent, agent2);
        verify(agent1, never()).connect();
        verify(agentFactory, times(maxAgents + 1))
            .get(same(queue), same(cfg));
    }

    @Test(dependsOnMethods = "agentsAreTakenInOrder")
    public void closingQueueDisconnectsAgents()
        throws IOException
    {
        final int maxAgents = 3;
        final FtpAgentQueue queue = new FtpAgentQueue(agentFactory, cfg,
            maxAgents);
        when(agentFactory.get(same(queue), same(cfg)))
            .thenReturn(agent1).thenReturn(agent2).thenReturn(agent3);

        final InOrder inOrder = inOrder(agent1, agent2, agent3);

        queue.pushBack(queue.getAgent());
        queue.close();
        inOrder.verify(agent2).disconnect();
        inOrder.verify(agent3).disconnect();
        inOrder.verify(agent1).disconnect();
    }

    @Test
    public void allAgentsDisconnectEvenOnIOException()
        throws IOException
    {
        final int maxAgents = 3;
        final FtpAgentQueue queue = new FtpAgentQueue(agentFactory, cfg,
            maxAgents);
        when(agentFactory.get(same(queue), same(cfg)))
            .thenReturn(agent1).thenReturn(agent2).thenReturn(agent3);

        final InOrder inOrder = inOrder(agent1, agent2, agent3);
        final IOException e = new IOException();
        doThrow(e).when(agent2).disconnect();

        queue.pushBack(queue.getAgent());
        try {
            queue.close();
        } catch (IOException actual) {
            assertSame(actual, e);
        }
        inOrder.verify(agent2).disconnect();
        inOrder.verify(agent3).disconnect();
        inOrder.verify(agent1).disconnect();
    }
}
