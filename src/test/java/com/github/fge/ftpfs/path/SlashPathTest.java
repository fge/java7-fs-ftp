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

public final class SlashPathTest
{
    @Test
    public void constructorRefusesNullArguments()
    {
        try {
            SlashPath.fromString(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), "null argument is not allowed");
        }
    }

    @DataProvider
    public Iterator<Object[]> pathInputs()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "", "" });
        list.add(new Object[] { "/", "/" });
        list.add(new Object[] { "//", "/" });
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
        final SlashPath path = SlashPath.fromString(input);

        assertEquals(path.toString(), expected);
    }

    @Test(dataProvider = "pathInputs")
    public void hashCodeAndEqualsWork(final String first,
        final String second)
    {
        final SlashPath p1 = SlashPath.fromString(first);
        final SlashPath p2 = SlashPath.fromString(second);
        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void basicEqualsHashCodeContractIsRespected()
    {
        final SlashPath path = SlashPath.fromString("foo");
        assertTrue(path.equals(path));
        assertFalse(path.equals(null));
        assertFalse(path.equals(new Object()));
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
        list.add(new Object[] { "foo", false, true });
        return list.iterator();
    }

    @Test(dataProvider = "absoluteAndNormalizedTests")
    public void absoluteAndNormalizedPathsAreDetectedAccurately(
        final String input, final boolean absolute, final boolean normalized
    )
    {
        final SlashPath path = SlashPath.fromString(input);
        assertEquals(path.isAbsolute(), absolute);
        assertEquals(path.isNormalized(), normalized);
    }

    @Test
    public void getNameThrowsIAEOnEmptyPath()
    {
        try {
            SlashPath.fromString("").getName(0);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "path has no elements");
        }
    }

    @DataProvider
    public Iterator<Object[]> getNameData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "/a/b/c/d", 4, 2, "c", "d" });
        list.add(new Object[] { "/a", 1, 0, "a", "a" });
        list.add(new Object[] { "/a/..", 2, 0, "a", ".." });
        list.add(new Object[] { "/a/../.", 3, 0, "a", "." });

        return list.iterator();
    }

    @Test(dataProvider = "getNameData")
    public void getNameAndLastNameWorkCorrectly(final String input,
        final int nameCount, final int index, final String name,
        final String lastName)
    {
        final SlashPath path = SlashPath.fromString(input);
        final SlashPath component = SlashPath.fromString(name);
        final SlashPath last = SlashPath.fromString(lastName);

        assertEquals(path.getNameCount(), nameCount);
        assertEquals(path.getName(index), component);
        assertEquals(path.getLastName(), last);
    }

    @DataProvider
    public Iterator<Object[]> getIllegalNameData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "/1/2/3", -1, "invalid index -1" });
        list.add(new Object[] { "/1/2/3", 3, "invalid index 3" });
        list.add(new Object[] { "/", 0, "path has no elements" });
        list.add(new Object[] { "/", -1, "path has no elements" });
        list.add(new Object[] { "", 0, "path has no elements" });
        return list.iterator();
    }


    @Test(dataProvider = "getIllegalNameData")
    public void getNameWithIllegalArgumentsThrowsIAE(final String input,
        final int index, final String message)
    {
        final SlashPath path = SlashPath.fromString(input);

        try {
            path.getName(index);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), message);
        }
    }

    @DataProvider
    public Iterator<Object[]> getParentData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "", null });
        list.add(new Object[] { "/", null });
        list.add(new Object[] { "/a", "/" });
        list.add(new Object[] { "a", null });
        list.add(new Object[] { "/a/b", "/a" });
        list.add(new Object[] { "/a/b/..", "/a/b" });

        return list.iterator();
    }

    @Test(dataProvider = "getParentData")
    public void getParentWorks(final String input, final String output)
    {
        final SlashPath path = SlashPath.fromString(input);
        final SlashPath expected = output == null ? null
            : SlashPath.fromString(output);

        assertEquals(path.getParent(), expected);
    }

    private static final SlashPath TESTPATH = SlashPath.fromString("/a/b/c");

    @DataProvider
    public Iterator<Object[]> getIllegalSubpathData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { -1, 2, "start index (-1) must not be negative" });
        list.add(new Object[] { 3, 2, "end index (2) must not be less than start index (3)" });
        list.add(new Object[] { 0, -1, "end index (-1) must not be less than start index (0)" });
        list.add(new Object[] { 0, 4, "end index (4) must not be greater than size (3)" });
        return list.iterator();
    }

    @Test(dataProvider = "getIllegalSubpathData")
    public void illegalSubpathIndicesThrowIAE(final int start, final int end,
        final String message)
    {
        try {
            TESTPATH.subpath(start, end);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), message);
        }
    }

    @DataProvider
    public Iterator<Object[]> getSubpathData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "x", 0, 1, "x" });
        list.add(new Object[] { "/x", 0, 1, "x" });
        list.add(new Object[] { "/a/b/c/d/e/f", 2, 6, "c/d/e/f" });
        list.add(new Object[] { "/a/b/c/d/e/f", 0, 6, "a/b/c/d/e/f" });
        list.add(new Object[] { "/a/b/c/d/e/f", 1, 4, "b/c/d" });
        list.add(new Object[] { "/a/b/c/d/e/f", 2, 2, "" });
        return list.iterator();
    }

    @Test(dataProvider = "getSubpathData")
    public void subpathWorksAsIntended(final String orig, final int start,
        final int end, final String ret)
    {
        final SlashPath path = SlashPath.fromString(orig);
        final SlashPath expected = SlashPath.fromString(ret);

        assertEquals(path.subpath(start, end), expected);
    }

    @DataProvider
    public Iterator<Object[]> getStartsWithData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "/foo", "", false });
        list.add(new Object[] { "foo", "", false });
        list.add(new Object[] { "/foo", "foo", false });
        list.add(new Object[] { "foo", "/foo", false });
        list.add(new Object[] { "/foo", "/foo", true });
        list.add(new Object[] { "foo", "foo", true });
        list.add(new Object[] { "foo/bar/baz", "foo", true });
        list.add(new Object[] { "foo", "foo/bar/baz", false });
        list.add(new Object[] { "foo/..", "foo", true });
        list.add(new Object[] { "/", "/", true });
        return list.iterator();
    }

    @Test(dataProvider = "getStartsWithData")
    public void startsWithWorksCorrectly(final String orig,
        final String against, final boolean expected)
    {
        final SlashPath me = SlashPath.fromString(orig);
        final SlashPath him = SlashPath.fromString(against);

        assertEquals(me.startsWith(him), expected);
    }

    @DataProvider
    public Iterator<Object[]> getEndsWithData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "foo", "/foo", false });
        list.add(new Object[] { "/foo", "foo", true });
        list.add(new Object[] { "foo", "foo", true });
        list.add(new Object[] { "/foo", "/foo", true });
        list.add(new Object[] { "/foo/bar", "/bar", false });
        list.add(new Object[] { "/foo/bar", "bar", true });
        list.add(new Object[] { "foo/bar", "bar", true });
        list.add(new Object[] { "foo/bar", "/bar", false });
        list.add(new Object[] { "/a/b/c/d/e/f", "e/f", true });
        list.add(new Object[] { "/a/b/c/d/e/f", "e/fg", false });
        return list.iterator();
    }

    @Test(dataProvider = "getEndsWithData")
    public void endsWithWorksCorrectly(final String orig,
        final String against, final boolean expected)
    {
        final SlashPath me = SlashPath.fromString(orig);
        final SlashPath him = SlashPath.fromString(against);

        assertEquals(me.endsWith(him), expected);
    }
}
