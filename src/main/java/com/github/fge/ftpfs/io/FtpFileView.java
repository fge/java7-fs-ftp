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

import java.nio.file.AccessMode;
import java.nio.file.attribute.BasicFileAttributeView;
import java.util.Collection;

/**
 * A convenience interface over {@link BasicFileAttributeView}
 *
 * <p>This interface extends {@link BasicFileAttributeView} to provide an
 * easier access to basic {@link AccessMode} privileges.</p>
 */
public interface FtpFileView
    extends BasicFileAttributeView
{
    Collection<AccessMode> getAccess();
}
