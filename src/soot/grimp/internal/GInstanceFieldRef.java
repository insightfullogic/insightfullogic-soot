/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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






package soot.grimp.internal;

import soot.*;
import soot.grimp.*;
import soot.jimple.internal.*;
import soot.util.*;
import java.util.*;
import soot.jimple.*;
import soot.grimp.*;
import soot.jimple.internal.*;

public class GInstanceFieldRef extends AbstractInstanceFieldRef
    implements InstanceFieldRef, Precedence
{
    public GInstanceFieldRef(Value base, SootField field)
    {
        super(Grimp.v().newObjExprBox(base), field);
    }

    private String toString(Value op, String opString, String rightString)
    {
        String leftOp = opString;

        if (op instanceof Precedence && 
            ((Precedence)op).getPrecedence() < getPrecedence()) 
            leftOp = "(" + leftOp + ")";
        return leftOp + rightString;
    }

    public String toString()
    {
        return toString(getBase(), getBase().toString(),
                        "." + getField().getSignature());
    }

    public int getPrecedence()
    {
        return 950;
    }
    
    public Object  clone() 
    {
        return new GInstanceFieldRef(Grimp.cloneIfNecessary(getBase()), 
            getField());
    }

    
}
