/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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





package soot.baf;

import soot.*;
import soot.util.*;
import java.util.*;

class BafLocal implements Local
{
    String name;
    Type type;

    int fixedHashCode;
    boolean isHashCodeChosen;
        
    BafLocal(String name, Type t)
    {
        this.name = name;
        this.type = t;
    }

    public Object clone()
    {
        return new BafLocal(name, type);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type t)
    {
        this.type = t;
    }

    public String toString()
    {
        return getName();
    }

    public String toBriefString()
    {
        return toString();
    }
    
    public List getUseBoxes()
    {
        return AbstractUnit.emptyList;
    }

    public void apply(Switch s)
    {
        throw new RuntimeException("invalid case switch");
    }
}
