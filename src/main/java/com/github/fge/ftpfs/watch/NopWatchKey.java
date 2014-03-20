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

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.Watchable;
import java.util.Collections;
import java.util.List;

public final class NopWatchKey
    implements WatchKey
{
    private boolean valid = true;

    @Override
    public boolean isValid()
    {
        return valid;
    }

    @Override
    public List<WatchEvent<?>> pollEvents()
    {
        return Collections.emptyList();
    }

    @Override
    public boolean reset()
    {
        final boolean ret = valid;
        valid = false;
        return ret;
    }

    @Override
    public void cancel()
    {
        valid = false;
    }

    @Override
    public Watchable watchable()
    {
        return NopWatchable.INSTANCE;
    }
}
