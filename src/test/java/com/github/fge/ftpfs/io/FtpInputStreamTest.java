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

import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class FtpInputStreamTest
{
    private InputStream stream;
    private FtpAgent agent;

    @BeforeMethod
    public void init()
    {
        stream = mock(InputStream.class);
        agent = mock(FtpAgent.class);
    }

    @Test
    public void nullAgentIsNotAllowed()
    {
        try {
            new FtpInputStream(null, null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "agent is null");
        }
    }

    @Test
    public void nullInputStreamIsNotAllowed()
    {
        try {
            new FtpInputStream(agent, null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "input stream is null");
        }
    }

    @Test
    public void closingIsDoneInOrder()
        throws IOException
    {
        final FtpInputStream in = new FtpInputStream(agent, stream);
        final InOrder inOrder = inOrder(agent, stream);

        in.close();

        inOrder.verify(stream).close();
        inOrder.verify(agent).completeTransfer();
        inOrder.verify(agent).close();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void closingProceedsEvenIfStreamFailsToClose()
        throws IOException
    {
        final FtpInputStream in = new FtpInputStream(agent, stream);
        final InOrder inOrder = inOrder(agent, stream);

        doThrow(new IOException()).when(stream).close();

        try {
            in.close();
        } catch (IOException ignored) {
        } finally {
            inOrder.verify(stream).close();
            inOrder.verify(agent).completeTransfer();
            inOrder.verify(agent).close();
            inOrder.verifyNoMoreInteractions();
        }
    }

    @Test
    public void closingProceedsEventIfTransferDoesNotCompleteProperly()
        throws IOException
    {
        final FtpInputStream in = new FtpInputStream(agent, stream);
        final InOrder inOrder = inOrder(agent, stream);

        doThrow(new IOException()).when(agent).completeTransfer();

        try {
            in.close();
        } catch (IOException ignored) {
        } finally {
            inOrder.verify(stream).close();
            inOrder.verify(agent).completeTransfer();
            inOrder.verify(agent).close();
            inOrder.verifyNoMoreInteractions();
        }

    }
}
