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






package soot.jimple.internal;

import soot.*;
import soot.jimple.*;
import soot.baf.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

public class JIdentityStmt extends AbstractDefinitionStmt 
    implements IdentityStmt
{
    public JIdentityStmt(Value local, Value identityValue)
    {
        this(Jimple.v().newLocalBox(local),
             Jimple.v().newIdentityRefBox(identityValue));
    }

    protected JIdentityStmt(ValueBox localBox, ValueBox identityValueBox)
    {
        this.leftBox = localBox; this.rightBox = identityValueBox;

        defBoxes = new ArrayList();
        defBoxes.add(leftBox);
        defBoxes = Collections.unmodifiableList(defBoxes);
    }

    public Object clone()
    {
        return new JIdentityStmt(Jimple.cloneIfNecessary(getLeftOp()), Jimple.cloneIfNecessary(getRightOp()));
    }

    protected String toString(boolean isBrief, Map stmtToName, String indentation)
    {
        if(isBrief)
        {
            return indentation + ((ToBriefString) leftBox.getValue()).toBriefString() + " := " + 
                ((ToBriefString) rightBox.getValue()).toBriefString();
        }
        else
            return indentation + leftBox.getValue().toString() + " := " + rightBox.getValue().toString();
    }

    public void setLeftOp(Value local)
    {
        leftBox.setValue(local);
    }

    public void setRightOp(Value identityRef)
    {
        rightBox.setValue(identityRef);
    }

    public void apply(Switch sw)
    {
        ((StmtSwitch) sw).caseIdentityStmt(this);
    }    
    
    public void convertToBaf(JimpleToBafContext context, List out)
    {
        Value currentRhs = getRightOp();
        Value newRhs;
        
        if(currentRhs instanceof ThisRef)
            newRhs = Baf.v().newThisRef((RefType)((ThisRef) currentRhs).getType());
        else if(currentRhs instanceof ParameterRef)
            newRhs = Baf.v().newParameterRef(((ParameterRef)currentRhs).getType(), ((ParameterRef) currentRhs).getIndex());
        else if(currentRhs instanceof CaughtExceptionRef)
            { out.add(Baf.v().newStoreInst(RefType.v(), context.getBafLocalOfJimpleLocal((Local) getLeftOp()))); return; }
        else
            throw new RuntimeException("Don't know how to convert unknown rhs");

        out.add(Baf.v().newIdentityInst(context.getBafLocalOfJimpleLocal((Local) getLeftOp()), newRhs));
    }


}





