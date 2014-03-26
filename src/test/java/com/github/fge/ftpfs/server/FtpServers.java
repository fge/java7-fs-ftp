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

package com.github.fge.ftpfs.server;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;

import java.nio.file.Path;

public final class FtpServers
{
    private FtpServers()
    {
    }

    public static FtpServer createServer(final TemporaryFileSystem fs,
        final Listener listener)
    {
        final Path baseDir = fs.getBaseDir();
        final UserManager userManager
            = new InMemoryUserManagerFactory(baseDir).createUserManager();
        final FtpServerFactory serverFactory = new FtpServerFactory();
        serverFactory.setUserManager(userManager);
        serverFactory.addListener("default", listener);
        return serverFactory.createServer();
    }
}
