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
import soot.util.*;
import java.util.*;
import soot.jimple.*;
import soot.jimple.internal.*;

abstract public class AbstractGrimpFloatBinopExpr
    extends AbstractFloatBinopExpr implements Precedence
{
    AbstractGrimpFloatBinopExpr(Value op1, Value op2)
    {
        this(Grimp.v().newArgBox(op1),
             Grimp.v().newArgBox(op2));
    }

    protected AbstractGrimpFloatBinopExpr(ValueBox op1Box, ValueBox op2Box)
    {
        this.op1Box = op1Box;
        this.op2Box = op2Box;
    }

    abstract public int getPrecedence();

    private String toString(Value op1, Value op2, 
                            String leftOp, String rightOp)
    {
        if (op1 instanceof Precedence && 
            ((Precedence)op1).getPrecedence() < getPrecedence()) 
            leftOp = "(" + leftOp + ")";

	if (op2 instanceof Precedence) {
	    int opPrec = ((Precedence) op2).getPrecedence(),
		myPrec = getPrecedence();
	    
	    if ((opPrec < myPrec) ||
		((opPrec == myPrec) && ((this instanceof SubExpr) || (this instanceof DivExpr))))		
		rightOp = "(" + rightOp + ")";
	}
					       
        return leftOp + getSymbol() + rightOp;
    }

    public String toString()
    {
        Value op1 = op1Box.getValue(), op2 = op2Box.getValue();
        String leftOp = op1.toString(), rightOp = op2.toString();

        return toString(op1, op2, leftOp, rightOp);
    }

    public String toBriefString()
    {
        Value op1 = op1Box.getValue(), op2 = op2Box.getValue();
        String leftOp = ((ToBriefString)op1).toBriefString(), 
            rightOp = ((ToBriefString)op2).toBriefString();

        return toString(op1, op2, leftOp, rightOp);
    }    
}
