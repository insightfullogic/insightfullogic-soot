/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Feng Qian
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


package soot.jimple.toolkits.annotation.tags;

/** NullCheckTag contains the null pointer check information. 
 * The right third bit of a byte is used to represent whether
 * the null check is needed.
 */

public class NullCheckTag implements OneByteCodeTag
{
    private final static String NAME = "NullCheckTag";
    
    private byte value = 0;

    public NullCheckTag(boolean needCheck)
    {
    	if (needCheck)
	    value = 0x04;
    }

    public String getName()
    {
	return NAME;
    }

    public byte[] getValue()
    {
        byte[] bv = new byte[1];
	bv[0] = value;
	return bv;
    }

    public boolean needCheck()
    {
    	if (value == 0)
	    return false;
	else
	    return true;
    }

    public String toString()
    {
	return ((value==0)?"[Unknown]":"[NonNULL]");
    }
}
