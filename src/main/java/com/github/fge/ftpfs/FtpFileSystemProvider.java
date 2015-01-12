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

import com.github.fge.ftpfs.io.FtpAgent;
import com.github.fge.ftpfs.io.FtpAgentFactory;
import com.github.fge.ftpfs.io.FtpAgentQueue;
import com.github.fge.ftpfs.io.FtpFileView;
import com.github.fge.ftpfs.util.AttributeUtil;
import com.github.fge.ftpfs.util.BasicFileAttributesEnum;
import com.github.fge.ftpfs.util.FtpFs;

import javax.annotation.concurrent.GuardedBy;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class FtpFileSystemProvider
    extends FileSystemProvider
{
    private static final int MAX_AGENTS = 5;

    private final FtpAgentFactory agentFactory;

    @GuardedBy("fileSystems")
    private final Map<URI, FtpFileSystem> fileSystems = new HashMap<>();
    @GuardedBy("fileSystems")
    private final Map<FtpFileSystem, FtpAgentQueue> agentQueues
        = new HashMap<>();

    public FtpFileSystemProvider(final FtpAgentFactory agentFactory)
    {
        this.agentFactory = agentFactory;
    }

    @Override
    public String getScheme()
    {
        return "ftp";
    }

    @Override
    public FileSystem newFileSystem(final URI uri, final Map<String, ?> env)
        throws IOException
    {
        final URI normalized = FtpFs.normalizeAndCheckRoot(uri);

        final FtpConfiguration.Builder builder = FtpConfiguration.newBuilder()
            .setHostname(normalized.getHost());

        if (normalized.getPort() != -1)
            builder.setPort(normalized.getPort());

        @SuppressWarnings("unchecked")
        final Map<String, String> params = (Map<String, String>) env;

        final String username = params.get("username");
        final String password = params.get("password");

        if (username != null)
            builder.setUsername(username);
        if (password != null)
            builder.setPassword(password);

        final FtpConfiguration cfg = builder.build();

        synchronized (fileSystems) {
            if (fileSystems.containsKey(normalized))
                throw new FileSystemAlreadyExistsException();

            final FtpFileSystem fs = new FtpFileSystem(this, normalized);
            final FtpAgentQueue agentQueue
                = new FtpAgentQueue(agentFactory, cfg, MAX_AGENTS);

            fileSystems.put(normalized, fs);
            agentQueues.put(fs, agentQueue);

            return fs;
        }
    }

    @Override
    public InputStream newInputStream(final Path path,
        final OpenOption... options)
        throws IOException
    {
        final FtpAgentQueue queue = agentQueues.get(path.getFileSystem());
        return queue.getAgent().getInputStream(path);
    }

    @Override
    public OutputStream newOutputStream(final Path path,
        final OpenOption... options)
        throws IOException
    {
        throw new IllegalStateException();
    }

    @Override
    public FileSystem getFileSystem(final URI uri)
    {
        synchronized (fileSystems) {
            final FileSystem ret = fileSystems.get(uri);
            if (ret == null)
                throw new FileSystemNotFoundException();
            return ret;
        }
    }

    @Override
    public Path getPath(final URI uri)
    {
        final URI normalized = FtpFs.normalizeAndCheckNonRoot(uri);
        final URI rel = normalized.relativize(uri);
        if (!rel.isAbsolute()) { // found
            final FtpFileSystem fs;
            synchronized (fileSystems) {
                fs = fileSystems.get(normalized);
            }
            if (fs != null)
                return fs.getPath(rel.toString());
        }

        throw new FileSystemNotFoundException("The file system for " + uri
                + " does not exist and cannot be created automatically");
    }

    @Override
    public SeekableByteChannel newByteChannel(final Path path,
        final Set<? extends OpenOption> options,
        final FileAttribute<?>... attrs)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(final Path dir,
        final DirectoryStream.Filter<? super Path> filter)
        throws IOException
    {
        // FIXME...
        final FtpFileSystem fs = (FtpFileSystem) dir.getFileSystem();
        final FtpAgentQueue queue = agentQueues.get(fs);
        try (
            final FtpAgent agent = queue.getAgent();
        ) {
            final String absPath = dir.toRealPath().toString();
            final Iterator<String> names
                = agent.getDirectoryNames(absPath).iterator();
            return new DirectoryStream<Path>()
            {
                @Override
                public Iterator<Path> iterator()
                {
                    return new Iterator<Path>()
                    {
                        @Override
                        public boolean hasNext()
                        {
                            return names.hasNext();
                        }

                        @Override
                        public Path next()
                        {
                            if (!hasNext())
                                throw new NoSuchElementException();
                            return fs.getPath(names.next());
                        }

                        @Override
                        public void remove()
                        {
                            throw new UnsupportedOperationException();
                        }
                    };
                }

                @Override
                public void close()
                    throws IOException
                {
                }
            };
        }
    }

    @Override
    public void createDirectory(final Path dir, final FileAttribute<?>... attrs)
        throws IOException
    {
        throw new IllegalStateException();
    }

    @Override
    public void delete(final Path path)
        throws IOException
    {
        throw new IllegalStateException();
    }

    @Override
    public void copy(final Path source, final Path target,
        final CopyOption... options)
        throws IOException
    {
        throw new IllegalStateException();
    }

    @Override
    public void move(final Path source, final Path target,
        final CopyOption... options)
        throws IOException
    {
        throw new IllegalStateException();
    }

    @Override
    public boolean isSameFile(final Path path, final Path path2)
        throws IOException
    {
        return path.toRealPath().equals(path2.toRealPath());
    }

    @Override
    public boolean isHidden(final Path path)
        throws IOException
    {
        if (path.getNameCount() == 0)
            return false;
        final String name = path.getFileName().toString();
        return !(".".equals(name) || "..".equals(name)) && name.startsWith(".");
    }

    @Override
    public FileStore getFileStore(final Path path)
        throws IOException
    {
        return path.getFileSystem().getFileStores().iterator().next();
    }

    @Override
    public void checkAccess(final Path path, final AccessMode... modes)
        throws IOException
    {
        final FtpAgentQueue queue = agentQueues.get(path.getFileSystem());
        final String name = path.toRealPath().toString();
        final List<AccessMode> modeList = Arrays.asList(modes);
        try (
            final FtpAgent agent = queue.getAgent();
        ) {
            final FtpFileView view = agent.getFileView(name);
            if (!view.getAccess().containsAll(modeList))
                throw new AccessDeniedException(name);
        }
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(final Path path,
        final Class<V> type, final LinkOption... options)
    {
        if (type != BasicFileAttributeView.class)
            return null;
        final FtpAgentQueue queue = agentQueues.get(path.getFileSystem());
        final String name;
        try {
            name = path.toRealPath(options).toString();
        } catch (IOException ignored) {
            return null;
        }
        try (
            final FtpAgent agent = queue.getAgent();
        ) {
            final FtpFileView view = agent.getFileView(name);
            return type.isAssignableFrom(view.getClass()) ? type.cast(view)
                : null;
        } catch (IOException e) {
            // FIXME
            return null;
        }
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path,
        final Class<A> type, final LinkOption... options)
        throws IOException
    {
        final FtpAgentQueue queue = agentQueues.get(path.getFileSystem());
        final String name = path.toRealPath().toString();
        try (
            final FtpAgent agent = queue.getAgent();
        ) {
            final BasicFileAttributes attributes
                = agent.getFileView(name).readAttributes();
            return type.isAssignableFrom(attributes.getClass())
                ? type.cast(attributes) : null;
        }
    }

    @Override
    public Map<String, Object> readAttributes(final Path path,
        final String attributes, final LinkOption... options)
        throws IOException
    {
        final Set<BasicFileAttributesEnum> set
            = AttributeUtil.getAttributes(attributes);
        final FtpAgentQueue queue = agentQueues.get(path.getFileSystem());
        final String file = path.toRealPath().toString();
        final BasicFileAttributes attrs;

        try (
            final FtpAgent agent = queue.getAgent();
        ) {
            attrs = agent.getFileView(file).readAttributes();
        }

        final Map<String, Object> ret = new HashMap<>();
        for (final BasicFileAttributesEnum attr: set)
            ret.put(attr.toString(), attr.getValue(attrs));

        return ret;
    }

    @Override
    public void setAttribute(final Path path, final String attribute,
        final Object value, final LinkOption... options)
        throws IOException
    {
        throw new IllegalStateException();
    }

    void unregister(final FtpFileSystem fs) {
        synchronized (fileSystems) {
            final URI uri = fs.getUri();
            final FtpAgentQueue queue = agentQueues.get(fs);
            fileSystems.remove(uri);
            try {
                queue.close();
            } catch (IOException ignored) {
            }
            agentQueues.remove(fs);
        }
    }
}
