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

public final class SlashDelimitedPathNormalizeTest
{
    @DataProvider
    public Iterator<Object[]> resolveData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "foo", "foo" } );
        list.add(new Object[] { "foo/.", "foo" } );
        list.add(new Object[] { "foo/..", "" } );
        list.add(new Object[] { "foo", "foo" } );
        list.add(new Object[] { "foo/.", "foo" } );
        list.add(new Object[] { "foo/..", "" } );
        list.add(new Object[] { "/foo/..", "/" } );
        list.add(new Object[] { "/foo/../bar/./baz/.", "/bar/baz" } );
        list.add(new Object[] { "a/b/./d/e/../../c/.", "a/b/c"} );
        list.add(new Object[] { "/..", "/.." } );
        list.add(new Object[] { "..", ".." } );
        list.add(new Object[] { "../.", ".." } );
        list.add(new Object[] { "../../a", "../../a"});
        list.add(new Object[] { "../a/./b/../c", "../a/c"});

        return list.iterator();
    }

    @Test(dataProvider = "resolveData")
    public void pathNormalizeWorks(final String input, final String result)
    {
        final SlashDelimitedPath orig = SlashDelimitedPath.fromString(input);
        final SlashDelimitedPath expected
            = SlashDelimitedPath.fromString(result);
        assertEquals(orig.normalize(), expected);
        assertTrue(expected.isNormalized());
    }
}
