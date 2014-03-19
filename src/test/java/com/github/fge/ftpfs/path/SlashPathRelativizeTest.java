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

package com.github.fge.ftpfs.path;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class SlashPathRelativizeTest
{
    @DataProvider
    public Iterator<Object[]> relativizeData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "/a/b", "/a/b/c/d", "c/d" });
        list.add(new Object[] { "/a/b/c/d", "/a/b", "../.."});
        list.add(new Object[] { "/a/b", "/a/b", ""});
        list.add(new Object[] { "/a/b", "/a/c", "../c"});
        list.add(new Object[] { "a/b", "a/c", "../c"});
        list.add(new Object[] { "a/b", "c", "../../c"});
        list.add(new Object[] { "../a/b", "../c", "../../c"});

        return list.iterator();
    }

    @Test(dataProvider = "relativizeData")
    public void relativizationWorksAsExpected(final String srcpath,
        final String dstpath, final String relpath)
    {
        final SlashPath src = SlashPath.fromString(srcpath);
        final SlashPath dst = SlashPath.fromString(dstpath);
        final SlashPath rel = SlashPath.fromString(relpath);

        assertEquals(src.relativize(dst), rel);
        assertEquals(src.relativize(src.resolve(rel)), rel);
    }

    @Test
    public void pathsMustBothBeAbsoluteOrRelative()
    {
        final SlashPath p1 = SlashPath.fromString("/abs");
        final SlashPath p2 = SlashPath.fromString("rel");

        try {
            p1.relativize(p2);
            fail("No exception thrown!");
        } catch (IllegalArgumentException ignored) {
            assertTrue(true);
        }

        try {
            p2.relativize(p1);
            fail("No exception thrown!");
        } catch (IllegalArgumentException ignored) {
            assertTrue(true);
        }
    }
}
