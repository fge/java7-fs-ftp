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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class FtpConfigurationTest
{
    private FtpConfiguration.Builder builder;

    @BeforeMethod
    public void initBuilder()
    {
        builder = FtpConfiguration.newBuilder();
    }

    @Test
    public void cannotBuildWithoutHostname()
    {
        try {
            builder.build();
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "no hostname has been provided");
        }
    }

    @Test
    public void cannotProvideNullHostname()
    {
        try {
            builder.setHostname(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "hostname cannot be null");
        }
    }

    @Test
    public void cannotProvideIllegalPort()
    {
        try {
            builder.setPort(-1);
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "illegal port number -1");
        }

        try {
            builder.setPort(65536);
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "illegal port number 65536");
        }
    }

    @Test
    public void cannotProvideNullUsername()
    {
        try {
            builder.setUsername(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "username cannot be null");
        }
    }

    @Test
    public void cannotProvideNullPassword()
    {
        try {
            builder.setPassword(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "password cannot be null");
        }
    }
}
