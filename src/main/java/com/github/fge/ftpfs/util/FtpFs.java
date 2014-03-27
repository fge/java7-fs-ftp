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

package com.github.fge.ftpfs.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public final class FtpFs
{
    private FtpFs()
    {
    }

    public static URI normalizeAndCheck(final URI uri)
    {
        Objects.requireNonNull(uri, "uri cannot be null");
        checkThat(uri.isAbsolute(), "uri must be absolute");
        checkThat("ftp".equalsIgnoreCase(uri.getScheme()),
            "uri scheme must be \"ftp\"");
        checkThat(uri.getUserInfo() == null, "uri must not contain user info");
        checkThat(uri.getHost() != null, "uri must have a hostname");
        if (uri.getPath() != null && !uri.getPath().isEmpty())
            throw new UnsupportedOperationException("subpaths are not supported"
                + " (yet?)");
        try {
            return new URI("ftp", null, uri.getHost().toLowerCase(),
                uri.getPort(), null, null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException("How did I get there??", e);
        }
    }

    private static void checkThat(final boolean condition, final String msg)
    {
        if (!condition)
            throw new IllegalArgumentException(msg);
    }
}
