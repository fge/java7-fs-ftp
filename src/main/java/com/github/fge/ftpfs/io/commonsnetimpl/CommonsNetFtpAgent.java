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

package com.github.fge.ftpfs.io.commonsnetimpl;

import com.github.fge.ftpfs.io.AbstractFtpAgent;
import com.github.fge.ftpfs.io.FtpAgentQueue;
import com.github.fge.ftpfs.FtpConfiguration;
import com.github.fge.ftpfs.io.FtpFileView;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@NotThreadSafe
public final class CommonsNetFtpAgent
    extends AbstractFtpAgent
{
    private final FTPClient ftpClient;

    public CommonsNetFtpAgent(final FtpAgentQueue queue,
        final FtpConfiguration cfg)
    {
        super(queue, cfg);
        ftpClient = new FTPClient();
        ftpClient.setAutodetectUTF8(true);
    }

    @Override
    public FtpFileView getFileView(final String name)
        throws IOException
    {
        try {
            ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
            final FTPFile[] files = ftpClient.listFiles(name);
            if (files.length == 0)
                throw new NoSuchFileException(name);
            if (files.length == 1)
                return new CommonsNetFtpFileView(files[0]);
            for (final FTPFile file: files)
                if (".".equals(file.getName()))
                    return new CommonsNetFtpFileView(file);
            throw new IllegalStateException();
        } catch (FTPConnectionClosedException e) {
            status = Status.DEAD;
            throw new IOException("service unavailable", e);
        }
    }

    @Override
    public EnumSet<AccessMode> getAccess(final String name)
        throws IOException
    {
        try {
            ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
            final FTPFile[] files = ftpClient.listFiles(name);
            if (files.length == 0)
                throw new NoSuchFileException(name);
            if (files.length == 1)
                return calculateAccess(files[0]);
            for (final FTPFile file: files)
                if (".".equals(file.getName()))
                    return calculateAccess(file);
            throw new IllegalStateException();
        } catch (FTPConnectionClosedException e) {
            status = Status.DEAD;
            throw new IOException("service unavailable", e);
        }
    }

    @Override
    public List<String> getDirectoryNames(final String dir)
        throws IOException
    {
        try {
            ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
            final FTPFile[] files = ftpClient.listFiles(dir);
            if (files.length == 0)
                throw new NoSuchFileException(dir);
            if (files.length == 1)
                handleFailedDirectoryList(dir, files[0]);
            final List<String> ret = new ArrayList<>(files.length);
            String name;
            for (final FTPFile file: files) {
                name = file.getName();
                if (!(".".equals(name) || "..".equals(name)))
                    ret.add(name);
            }
            return ret;
        } catch (FTPConnectionClosedException e) {
            status = Status.DEAD;
            throw new IOException("service unavailable", e);
        }
    }

    @Override
    protected InputStream openInputStream(final String file)
        throws IOException
    {
        try {
            ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
            final FTPFile[] files = ftpClient.listFiles(file);
            if (files.length == 0)
                throw new AccessDeniedException(file);
            if (files.length > 1)
                throw new IOException(file + " is a directory");
            if (files[0].isDirectory())
                throw new AccessDeniedException(file);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            final InputStream ret = ftpClient.retrieveFileStream(file);
            if (ret == null)
                throw new IOException("cannot open stream to file (server " +
                    "reply " + ftpClient.getReplyCode());
            return ret;
        } catch (FTPConnectionClosedException e) {
            status = Status.DEAD;
            throw new IOException("service unavailable", e);
        }
    }

    @Override
    public void connect()
        throws IOException
    {
        if (status == Status.CONNECTED)
            return;
        try {
            ftpClient.connect(cfg.getHostname(), cfg.getPort());
            if (!ftpClient.login(cfg.getUsername(), cfg.getPassword()))
                throw new IOException("cannot login to server (server reply: "
                    + ftpClient.getReplyCode());
        } catch (FTPConnectionClosedException e) {
            status = Status.DEAD;
            throw new IOException("service unavailable", e);
        } catch (IOException e) {
            status = Status.DEAD;
            throw e;
        }
        status = Status.CONNECTED;
    }

    @Override
    public void disconnect()
        throws IOException
    {
        ftpClient.disconnect();
    }

    @Override
    public void completeTransfer()
        throws IOException
    {
        if (!ftpClient.completePendingCommand())
            throw new IOException("non finalized read from FTP server");
    }

    private static EnumSet<AccessMode> calculateAccess(final FTPFile file)
    {
        final EnumSet<AccessMode> ret = EnumSet.noneOf(AccessMode.class);
        if (file.hasPermission(FTPFile.USER_ACCESS, FTPFile.READ_PERMISSION))
            ret.add(AccessMode.READ);
        if (file.hasPermission(FTPFile.USER_ACCESS, FTPFile.WRITE_PERMISSION))
            ret.add(AccessMode.WRITE);
        if (file.hasPermission(FTPFile.USER_ACCESS, FTPFile.EXECUTE_PERMISSION))
            ret.add(AccessMode.EXECUTE);
        return ret;
    }

    private static void handleFailedDirectoryList(final String dir,
        final FTPFile file)
        throws FileSystemException
    {
        throw file.isDirectory() ? new AccessDeniedException(dir)
            : new NotDirectoryException(dir);
    }
}
