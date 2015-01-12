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

package com.github.fge.ftpfs.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class FtpFsTest
{
    @Test(expectedExceptions = NullPointerException.class,
            expectedExceptionsMessageRegExp = "uri cannot be null")
    public void cannotSubmitNullRootURI()
    {
        FtpFs.normalizeAndCheckRoot(null);
    }

    @Test(expectedExceptions = NullPointerException.class,
            expectedExceptionsMessageRegExp = "uri cannot be null")
    public void cannotSubmitNullNonRootURI()
    {
        FtpFs.normalizeAndCheckNonRoot(null);
    }

    @Test(expectedExceptions = UnsupportedOperationException.class,
            expectedExceptionsMessageRegExp = "\\Qsubpaths are not supported (yet?)\\E")
    public void rootUriMustNotHavePath()
    {
        FtpFs.normalizeAndCheckRoot(URI.create("ftp://foo/bar"));
    }

    private Iterator<Object[]> getInvalidURIs()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { URI.create("foo"), "uri must be absolute" });
        list.add(new Object[] { URI.create("http://slashdot.org"), "uri scheme must be \"ftp\"" });
        list.add(new Object[] { URI.create("ftp://foo:bar@host"), "uri must not contain user info" });
        list.add(new Object[] { URI.create("ftp:/foo"), "uri must have a hostname" });

        return list.iterator();
    }

    private void incorrectUrisAreRejected(URI uri, String expectedMessage)
    {
        try {
            FtpFs.normalizeAndCheckRoot(uri);
            fail("No exception thrown, but expected an IllegalArgumentException with message: " + expectedMessage);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), expectedMessage);
        }
    }

    @Test(dataProvider = "getInvalidURIs")
    public void incorrectRootUrisAreRejected(URI uri, String expectedMessage)
    {
        incorrectUrisAreRejected(uri, expectedMessage);
    }

    @Test(dataProvider = "getInvalidURIs")
    public void incorrectNonRootUrisAreRejected(URI uri, String expectedMessage)
    {
        incorrectUrisAreRejected(uri, expectedMessage);
    }

    @DataProvider
    public Iterator<Object[]> getRootURIs()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "ftp://foo", "ftp://foo" });
        list.add(new Object[] { "Ftp://foo", "ftp://foo" });
        list.add(new Object[] { "ftp://fOO", "ftp://foo" });
        list.add(new Object[] { "FTP://Foo", "ftp://foo" });

        return list.iterator();
    }

    @Test(dataProvider = "getRootURIs")
    public void rootUrisAreCorrectlyNormalized(final String orig,
        final String normalized)
    {
        final URI uri = URI.create(orig);
        assertEquals(FtpFs.normalizeAndCheckRoot(uri).toString(), normalized);
    }

    @DataProvider
    public Iterator<Object[]> getNonRootURIs()
    {
        final List<Object[]> list = new ArrayList<>();

        for (Iterator<Object[]> it = getRootURIs(); it.hasNext();) {
            list.add(it.next());
        }

        list.add(new Object[] { "ftp://foo/bar", "ftp://foo" });

        return list.iterator();
    }

    @Test(dataProvider = "getRootURIs")
    public void nonRootUrisAreCorrectlyNormalized(final String orig,
        final String normalized)
    {
        final URI uri = URI.create(orig);
        assertEquals(FtpFs.normalizeAndCheckRoot(uri).toString(), normalized);
    }
}
