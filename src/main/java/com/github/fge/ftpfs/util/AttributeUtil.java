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

import java.util.EnumSet;
import java.util.regex.Pattern;

public final class AttributeUtil
{
    private static final Pattern COMMA = Pattern.compile(",");
    private static final String ALL = "*";
    private static final String BASIC = "basic";
    private static final char COLON = ':';

    private AttributeUtil()
    {
    }

    public static EnumSet<BasicFileAttributesEnum> getAttributes(
        final String input)
    {
        final String[] attrs = COMMA.split(input);
        final EnumSet<BasicFileAttributesEnum> set
            = EnumSet.noneOf(BasicFileAttributesEnum.class);

        String type, name;
        int index;
        BasicFileAttributesEnum attr;

        for (final String attrName: attrs) {
            if (ALL.equals(attrName))
                return EnumSet.allOf(BasicFileAttributesEnum.class);
            index = attrName.indexOf(COLON);
            if (index == -1) {
                type = BASIC;
                name = attrName;
            } else {
                type = attrName.substring(0, index);
                name = attrName.substring(index + 1, attrName.length());
            }
            if (!BASIC.equals(type))
                throw new UnsupportedOperationException();
            if (ALL.equals(name))
                return EnumSet.allOf(BasicFileAttributesEnum.class);
            attr = BasicFileAttributesEnum.forName(name);
            if (attr == null)
                throw new UnsupportedOperationException();
            set.add(attr);
        }

        return EnumSet.copyOf(set);
    }
}
