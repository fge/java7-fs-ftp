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

package com.github.fge.ftpfs.watch;

import java.io.IOException;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.TimeUnit;

public enum NopWatchService
    implements WatchService
{
    INSTANCE;

    @Override
    public void close()
        throws IOException
    {
    }

    @Override
    public WatchKey poll()
    {
        return null;
    }

    @Override
    public WatchKey poll(final long timeout, final TimeUnit unit)
        throws InterruptedException
    {
        unit.sleep(timeout);
        return null;
    }

    @Override
    public WatchKey take()
        throws InterruptedException
    {
        return null;
    }
}
