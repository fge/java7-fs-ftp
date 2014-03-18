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

import com.github.fge.ftpfs.path.SlashDelimitedPath;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public final class SlashDelimitedPathTest
{
    @Test
    public void constructorRefusesNullArguments()
    {
        try {
            SlashDelimitedPath.fromString(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "null argument is not allowed");
        }
    }

    @DataProvider
    public Iterator<Object[]> pathInputs()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "foo", "foo" });
        list.add(new Object[] { "foo/", "foo"});
        list.add(new Object[] { "foo//", "foo"});
        list.add(new Object[] { "foo/.", "foo/."});
        list.add(new Object[] { "foo//.", "foo/."});
        list.add(new Object[] { "//foo/", "/foo" });
        list.add(new Object[] { "/foo//bar/..//", "/foo/bar/.." });

        return list.iterator();
    }

    @Test(dataProvider = "pathInputs")
    public void constructorRemovesExtraSlashes(final String input,
        final String expected)
    {
        final SlashDelimitedPath path = SlashDelimitedPath.fromString(input);

        assertEquals(path.toString(), expected);
    }

    @DataProvider
    public Iterator<Object[]> absoluteAndNormalizedTests()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "", false, true });
        list.add(new Object[] { "/", true, true });
        list.add(new Object[] { ".", false, false });
        list.add(new Object[] { "/foo/bar", true, true });
        list.add(new Object[] { "/foo/..", true, false });
        list.add(new Object[] { "/foo/.", true, false });
        return list.iterator();
    }

    @Test(dataProvider = "absoluteAndNormalizedTests")
    public void absoluteAndNormalizedPathsAreDetectedAccurately(
        final String input, final boolean absolute, final boolean normalized
    )
    {
        final SlashDelimitedPath path = SlashDelimitedPath.fromString(input);
        assertEquals(path.isAbsolute(), absolute);
        assertEquals(path.isNormalized(), normalized);
    }
}
