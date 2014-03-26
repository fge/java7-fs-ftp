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

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;

import java.nio.file.Path;
import java.util.Collections;

public final class  InMemoryUserManager
    implements UserManager
{
    public static final String USERNAME = "toto";
    public static final String PASSWORD = "leheros";

    private final BaseUser user;

    public InMemoryUserManager(final Path baseDir)
    {
        user = new BaseUser();
        user.setName(USERNAME);
        user.setPassword(PASSWORD);
        user.setHomeDirectory(baseDir.toString());
        final Authority auth = new ConcurrentLoginPermission(10, 10);
        user.setAuthorities(Collections.singletonList(auth));
    }

    @Override
    public User getUserByName(final String username)
        throws FtpException
    {
        return USERNAME.equals(username) ? user : null;
    }

    @Override
    public String[] getAllUserNames()
        throws FtpException
    {
        return new String[] { USERNAME };
    }

    @Override
    public void delete(final String username)
        throws FtpException
    {

    }

    @Override
    public void save(final User user)
        throws FtpException
    {

    }

    @Override
    public boolean doesExist(final String username)
        throws FtpException
    {
        return USERNAME.equals(username);
    }

    @Override
    public User authenticate(final Authentication authentication)
        throws AuthenticationFailedException
    {
        return user;
    }

    @Override
    public String getAdminName()
        throws FtpException
    {
        return null;
    }

    @Override
    public boolean isAdmin(final String username)
        throws FtpException
    {
        return false;
    }
}
