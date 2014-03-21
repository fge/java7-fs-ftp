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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.util.Iterator;
import java.util.List;

public abstract class FtpAgent
    implements Closeable
{
    private final FtpAgentQueue queue;

    protected FtpAgent(final FtpAgentQueue queue)
        throws IOException
    {
        this.queue = queue;
    }

    public abstract BasicFileAttributeView getAttributeView(final String name)
        throws IOException;

    protected abstract List<String> getDirectoryNames(final String dir)
        throws IOException;

    public final DirectoryStream<Path> getDirectoryStream(final Path path)
        throws IOException
    {
        final String dir = path.toRealPath().toString();
        final List<String> names = getDirectoryNames(dir);

        return new DirectoryStream<Path>()
        {
            @Override
            public Iterator<Path> iterator()
            {
                return new FtpDirectoryIterator(path, names);
            }

            @Override
            public void close()
                throws IOException
            {
                // no-op
            }
        };

    }

    protected abstract InputStream openInputStream(final String file)
        throws IOException;

    public final FtpInputStream getInputStream(final Path path)
        throws IOException
    {
        final InputStream stream
            = openInputStream(path.toAbsolutePath().toString());
        return new FtpInputStream(this, stream);
    }

    @Override
    public final void close()
        throws IOException
    {
        queue.pushBack(this);
    }

    private final class FtpDirectoryIterator
        implements Iterator<Path>
    {
        private final Path dir;
        private final Iterator<String> iterator;

        private FtpDirectoryIterator(final Path dir, final List<String> names)
        {
            this.dir = dir;
            iterator = names.iterator();
        }

        @Override
        public boolean hasNext()
        {
            return iterator.hasNext();
        }

        @Override
        public Path next()
        {
            return dir.resolve(iterator.next());
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
