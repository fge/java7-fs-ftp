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

public final class FTPPathTest
{
    private static final URI URI1 = URI.create("ftp://my.site/sub/path");
    private static final SlashPath PATH1 = SlashPath.fromString("/foo/bar");


    private FileSystem fs;

    @BeforeClass
    public void init()
    {
        fs = mock(FileSystem.class);
    }

    @Test
    public void pathKnowsWhatItsFileSystemIs()
    {
        final FTPPath path = new FTPPath(fs, URI1, PATH1);
        assertSame(path.getFileSystem(), fs);
    }

    @Test
    public void pathsWithDifferentFileSystemsNeverStartWithOneAnother()
    {
        final SlashPath slashPath1 = SlashPath.fromString("/a/b");
        final SlashPath slashPath2 = SlashPath.fromString("/a");
        final FileSystem fs2 = mock(FileSystem.class);
        final FTPPath path1 = new FTPPath(fs, URI1, slashPath1);
        final FTPPath path2 = new FTPPath(fs2, URI1, slashPath2);
        assertFalse(path1.startsWith(path2));
    }

    @Test
    public void pathsWithDifferentFileSystemsNeverEndWithOneAnother()
    {
        final SlashPath slashPath1 = SlashPath.fromString("/a/b");
        final SlashPath slashPath2 = SlashPath.fromString("b");
        final FileSystem fs2 = mock(FileSystem.class);
        final FTPPath path1 = new FTPPath(fs, URI1, slashPath1);
        final FTPPath path2 = new FTPPath(fs2, URI1, slashPath2);
        assertFalse(path1.endsWith(path2));
    }

    @Test
    public void resolveFailsIfProvidersAreDifferent()
    {
        final FileSystem fs2 = mock(FileSystem.class);
        final FileSystemProvider provider1 = mock(FileSystemProvider.class);
        final FileSystemProvider provider2 = mock(FileSystemProvider.class);
        when(fs.provider()).thenReturn(provider1);
        when(fs2.provider()).thenReturn(provider2);

        final SlashPath slashPath1 = SlashPath.fromString("/a/b");
        final SlashPath slashPath2 = SlashPath.fromString("b");

        final FTPPath path1 = new FTPPath(fs, URI1, slashPath1);
        final FTPPath path2 = new FTPPath(fs2, URI1, slashPath2);

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
        final FileSystem fs2 = mock(FileSystem.class);
        final FileSystemProvider provider1 = mock(FileSystemProvider.class);
        final FileSystemProvider provider2 = mock(FileSystemProvider.class);
        when(fs.provider()).thenReturn(provider1);
        when(fs2.provider()).thenReturn(provider2);

        final SlashPath slashPath1 = SlashPath.fromString("/a/b");
        final SlashPath slashPath2 = SlashPath.fromString("b");

        final FTPPath path1 = new FTPPath(fs, URI1, slashPath1);
        final FTPPath path2 = new FTPPath(fs2, URI1, slashPath2);

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
        final FileSystem fs2 = mock(FileSystem.class);
        final FileSystemProvider provider1 = mock(FileSystemProvider.class);
        final FileSystemProvider provider2 = mock(FileSystemProvider.class);
        when(fs.provider()).thenReturn(provider1);
        when(fs2.provider()).thenReturn(provider2);

        final SlashPath slashPath1 = SlashPath.fromString("/a/b");
        final SlashPath slashPath2 = SlashPath.fromString("b");

        final FTPPath path1 = new FTPPath(fs, URI1, slashPath1);
        final FTPPath path2 = new FTPPath(fs2, URI1, slashPath2);

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
        final FTPPath path = new FTPPath(fs, URI1, slashPath);
        final URI expected = URI.create(t);

        assertEquals(path.toUri(), expected);
    }
}
