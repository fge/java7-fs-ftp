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
    @Test
    public void cannotSubmitNullURI()
    {
        try {
            FtpFs.normalizeAndCheck(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "uri cannot be null");
        }
    }

    @Test
    public void cannotSubmitNonAbsoluteURI()
    {
        try {
            FtpFs.normalizeAndCheck(URI.create("foo"));
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "uri must be absolute");
        }
    }

    @Test
    public void schemeOfUriMustBeFtp()
    {
        try {
            FtpFs.normalizeAndCheck(URI.create("http://slashdot.org"));
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "uri scheme must be \"ftp\"");
        }
    }

    @Test
    public void uriMustNotHaveUserInfo()
    {
        try {
            FtpFs.normalizeAndCheck(URI.create("ftp://foo:bar@host"));
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "uri must not contain user info");
        }
    }

    @Test
    public void uriMustIncludeHostname()
    {
        try {
            FtpFs.normalizeAndCheck(URI.create("ftp:/foo"));
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "uri must have a hostname");
        }
    }

    @Test
    public void uriMustNotHavePath()
    {
        try {
            FtpFs.normalizeAndCheck(URI.create("ftp://foo/bar"));
            fail("No exception thrown!!");
        } catch (UnsupportedOperationException e) {
            assertEquals(e.getMessage(), "subpaths are not supported (yet?)");
        }
    }

    @DataProvider
    public Iterator<Object[]> getURIs()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "ftp://foo", "ftp://foo" });
        list.add(new Object[] { "Ftp://foo", "ftp://foo" });
        list.add(new Object[] { "ftp://fOO", "ftp://foo" });
        list.add(new Object[] { "FTP://Foo", "ftp://foo" });

        return list.iterator();
    }

    @Test(dataProvider = "getURIs")
    public void urisAreCorrectlyNormalized(final String orig,
        final String normalized)
    {
        final URI uri = URI.create(orig);

        assertEquals(FtpFs.normalizeAndCheck(uri).toString(), normalized);
    }
}
