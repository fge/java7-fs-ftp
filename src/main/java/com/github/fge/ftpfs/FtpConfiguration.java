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
import org.apache.commons.net.ftp.FTP;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
public final class FtpConfiguration
{
    private final String hostname;
    private final int port;
    private final String username;
    private final String password;
    private final SlashPath basePath;

    public static Builder newBuilder()
    {
        return new Builder();
    }

    private FtpConfiguration(final Builder builder)
    {
        hostname = builder.hostname;
        port = builder.port;
        username = builder.username;
        password = builder.password;
        basePath = builder.basePath;
    }

    public String getHostname()
    {
        return hostname;
    }

    public int getPort()
    {
        return port;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public SlashPath getBasePath()
    {
        return basePath;
    }

    public static final class Builder
    {
        private static final int MIN_PORT = 0;
        private static final int MAX_PORT = 65535;

        private String hostname;
        private int port = FTP.DEFAULT_PORT;
        private String username = "anonymous";
        // lftp sends lftp@ as a password and it works pretty well, so...
        private String password = "java7fsftp@";
        private SlashPath basePath = SlashPath.ROOT;

        private Builder()
        {
        }

        public Builder setHostname(@Nonnull final String hostname)
        {
            this.hostname = Objects.requireNonNull(hostname,
                "hostname cannot be null");
            return this;
        }

        public Builder setPort(final int port)
        {
            if (port < MIN_PORT || port > MAX_PORT)
                throw new IllegalArgumentException("illegal port number "
                    + port);
            this.port = port;
            return this;
        }

        public Builder setUsername(final String username)
        {
            this.username = Objects.requireNonNull(username,
                "username cannot be null");
            return this;
        }

        public Builder setPassword(final String password)
        {
            this.password = Objects.requireNonNull(password,
                "password cannot be null");
            return this;
        }

        public Builder setBasePath(final String path)
        {
            Objects.requireNonNull(path, "base path cannot be null");
            basePath = SlashPath.fromString(path);
            if (!basePath.isAbsolute())
                throw new IllegalArgumentException("base path must be " +
                    "absolute");
            return this;
        }

        public FtpConfiguration build()
        {
            Objects.requireNonNull(hostname, "no hostname has been provided");
            return new FtpConfiguration(this);
        }
    }
}
