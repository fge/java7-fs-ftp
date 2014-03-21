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

package com.github.fge.ftpfs;

import com.github.fge.ftpfs.path.SlashPath;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.ProviderMismatchException;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class FtpPathTest
{
    private static final URI URI1 = URI.create("ftp://my.site/sub/path");
    private static final SlashPath PATH1 = SlashPath.fromString("/foo/bar");


    private FileSystem fs1;
    private FileSystem fs2;

    @BeforeClass
    public void init()
    {
        fs1 = mock(FileSystem.class);
        when(fs1.provider()).thenReturn(mock(FileSystemProvider.class));
        fs2 = mock(FileSystem.class);
        when(fs2.provider()).thenReturn(mock(FileSystemProvider.class));
    }

    @Test
    public void pathKnowsWhatItsFileSystemIs()
    {
        final FtpPath path = new FtpPath(fs1, URI1, PATH1);
        assertSame(path.getFileSystem(), fs1);
    }

    @Test
    public void pathsWithDifferentFileSystemsNeverStartWithOneAnother()
    {
        final SlashPath slashPath1 = SlashPath.fromString("/a/b");
        final SlashPath slashPath2 = SlashPath.fromString("/a");
        final FtpPath path1 = new FtpPath(fs1, URI1, slashPath1);
        final FtpPath path2 = new FtpPath(fs2, URI1, slashPath2);
        assertFalse(path1.startsWith(path2));
    }

    @Test
    public void pathsWithDifferentFileSystemsNeverEndWithOneAnother()
    {
        final SlashPath slashPath1 = SlashPath.fromString("/a/b");
        final SlashPath slashPath2 = SlashPath.fromString("b");
        final FtpPath path1 = new FtpPath(fs1, URI1, slashPath1);
        final FtpPath path2 = new FtpPath(fs2, URI1, slashPath2);
        assertFalse(path1.endsWith(path2));
    }

    @Test
    public void resolveFailsIfProvidersAreDifferent()
    {
        final SlashPath slashPath1 = SlashPath.fromString("/a/b");
        final SlashPath slashPath2 = SlashPath.fromString("b");

        final FtpPath path1 = new FtpPath(fs1, URI1, slashPath1);
        final FtpPath path2 = new FtpPath(fs2, URI1, slashPath2);

        try {
            path1.resolve(path2);
            fail("No exception thrown!");
        } catch (ProviderMismatchException ignored) {
            assertTrue(true);
        }
    }

    @Test
    public void resolveSiblingFailsIfProvidersAreDifferent()
    {
        final SlashPath slashPath1 = SlashPath.fromString("/a/b");
        final SlashPath slashPath2 = SlashPath.fromString("b");

        final FtpPath path1 = new FtpPath(fs1, URI1, slashPath1);
        final FtpPath path2 = new FtpPath(fs2, URI1, slashPath2);

        try {
            path1.resolveSibling(path2);
            fail("No exception thrown!");
        } catch (ProviderMismatchException ignored) {
            assertTrue(true);
        }
    }
    @Test
    public void relativizeFailsIfProvidersAreDifferent()
    {
        final SlashPath slashPath1 = SlashPath.fromString("/a/b");
        final SlashPath slashPath2 = SlashPath.fromString("b");

        final FtpPath path1 = new FtpPath(fs1, URI1, slashPath1);
        final FtpPath path2 = new FtpPath(fs2, URI1, slashPath2);

        try {
            path1.relativize(path2);
            fail("No exception thrown!");
        } catch (ProviderMismatchException ignored) {
            assertTrue(true);
        }
    }

    @DataProvider
    public Iterator<Object[]> getURIData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "a", "ftp://my.site/sub/path/a" });
        list.add(new Object[] { "/a", "ftp://my.site/sub/path/a" });
        list.add(new Object[] { "/a/b", "ftp://my.site/sub/path/a/b" });
        list.add(new Object[] { "../../a", "ftp://my.site/sub/path/a" });
        list.add(new Object[] { "../a/../c/d", "ftp://my.site/sub/path/c/d" });

        return list.iterator();
    }

    @Test(dataProvider = "getURIData")
    public void getURIWorks(final String s, final String t)
    {
        final SlashPath slashPath = SlashPath.fromString(s);
        final FtpPath path = new FtpPath(fs1, URI1, slashPath);
        final URI expected = URI.create(t);

        assertEquals(path.toUri(), expected);
    }

    @Test
    public void getRootRespectsContract()
    {
        final SlashPath rootSlashPath = SlashPath.fromString("/");
        final SlashPath slashPath1 = SlashPath.fromString("/a/b");
        final SlashPath slashPath2 = SlashPath.fromString("b");

        final FtpPath rootPath = new FtpPath(fs1, URI1, rootSlashPath);
        final FtpPath path1 = new FtpPath(fs1, URI1, slashPath1);
        final FtpPath path2 = new FtpPath(fs1, URI1, slashPath2);

        assertEquals(path1.getRoot(), rootPath);
        assertNull(path2.getRoot());
    }

    @Test
    public void getParentRespectsContract()
    {
        final SlashPath slashPath1 = SlashPath.fromString("/a/b");
        final SlashPath slashPath2 = SlashPath.fromString("/a");
        final SlashPath rootSlashPath = SlashPath.fromString("/");

        final FtpPath path1 = new FtpPath(fs1, URI1, slashPath1);
        final FtpPath path2 = new FtpPath(fs1, URI1, slashPath2);
        final FtpPath rootPath = new FtpPath(fs1, URI1, rootSlashPath);

        assertEquals(path1.getParent(), path2);
        assertEquals(path2.getParent(), rootPath);
        assertNull(rootPath.getParent());
    }

    @Test
    public void getFileNameRespectsContract()
    {
        final SlashPath rootSlashPath = SlashPath.fromString("/");

        final FtpPath rootPath = new FtpPath(fs1, URI1, rootSlashPath);

        assertNull(rootPath.getFileName());
    }

    @Test
    public void resolveRespectsContract()
    {
        final SlashPath slashPath1 = SlashPath.fromString("/a/b");
        final SlashPath slashPath2 = SlashPath.fromString("b");
        final SlashPath emptySlashPath = SlashPath.fromString("");

        final FtpPath path1 = new FtpPath(fs1, URI1, slashPath1);
        final FtpPath path2 = new FtpPath(fs1, URI1, slashPath2);
        final FtpPath emptyPath = new FtpPath(fs1, URI1, emptySlashPath);

        assertSame(path2.resolve(path1), path1,
            "resolving an absolute path should return other");
        assertSame(path2.resolve(emptyPath), path2,
            "resolving empty path should return this");
    }

    @Test
    public void resolveSiblingRespectsContract()
    {
        final SlashPath slashPath1 = SlashPath.fromString("/a");
        final SlashPath slashPath2 = SlashPath.fromString("/");
        final SlashPath emptySlashPath = SlashPath.fromString("");

        final FtpPath path1 = new FtpPath(fs1, URI1, slashPath1);
        final FtpPath path2 = new FtpPath(fs1, URI1, slashPath2);
        final FtpPath emptyPath = new FtpPath(fs1, URI1, emptySlashPath);

        assertSame(path2.resolveSibling(emptyPath), emptyPath,
            "path without a parent should return other");
        assertSame(path2.resolveSibling(path1), path1,
            "resolving an absolute path should return other");
        assertEquals(path1.resolveSibling(emptyPath), path2,
            "resolving empty path as sibling should return parent");
        assertEquals(path2.resolveSibling(emptyPath), emptyPath,
            "path without parent resolving empty should return empty");
    }

    @Test
    public void relativizeRespectsContract()
    {
        final SlashPath slashPath1 = SlashPath.fromString("/a/b");
        final SlashPath slashPath2 = SlashPath.fromString("b");
        final SlashPath emptySlashPath = SlashPath.fromString("");

        final FtpPath path1 = new FtpPath(fs1, URI1, slashPath1);
        final FtpPath path2 = new FtpPath(fs1, URI1, slashPath2);
        final FtpPath emptyPath = new FtpPath(fs1, URI1, emptySlashPath);

        assertEquals(path1.relativize(path1), emptyPath);
        assertEquals(path2.relativize(path2), emptyPath);
        try {
            path1.relativize(path2);
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}
