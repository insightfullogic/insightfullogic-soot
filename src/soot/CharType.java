/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */





package soot;

import soot.util.*;
import java.util.*;

/**
 *   Soot representation of the Java built-in type 'char'. Implemented as
 *   a singleton.
 */
public class CharType extends BaseType implements IntegerType
{
    private static CharType constant = new CharType();

    private CharType()
    {
    }

    /** @return this class's singleton object */
    public static CharType v()
    {
        return constant;
    }

    public boolean equals(Object t)
    {
        return this == t;
    }

    public String toString()
    {
        return "char";
    }

    public int hashCode()
    {
        return 0x739EA474;
    }
    
    public void apply(Switch sw)
    {
        ((TypeSwitch) sw).caseCharType(this);
    }
}
