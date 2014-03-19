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

import static org.testng.Assert.assertEquals;

public final class SlashPathResolveTest
{
    @DataProvider
    public Iterator<Object[]> resolveData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "/foo", "/bar", "/bar" });
        list.add(new Object[] { "/foo", "/", "/" });
        list.add(new Object[] { "foo", "/", "/" });
        list.add(new Object[] { "/foo", "/bar", "/bar" });
        list.add(new Object[] { "", "/bar", "/bar" });
        list.add(new Object[] { "", "/..", "/.." });
        list.add(new Object[] { ".", "..", "./.." });
        list.add(new Object[] { "", "", "" });
        list.add(new Object[] { "", "a/b", "a/b" });
        list.add(new Object[] { "./d", "a/b", "./d/a/b" });
        list.add(new Object[] { "d", "/a/b", "/a/b" });

        return list.iterator();
    }

    @Test(dataProvider = "resolveData")
    public void pathResolveWorks(final String first, final String second,
        final String result)
    {
        final SlashPath p1 = SlashPath.fromString(first);
        final SlashPath p2 = SlashPath.fromString(second);
        final SlashPath expected
            = SlashPath.fromString(result);

        assertEquals(p1.resolve(p2), expected);
    }
}
