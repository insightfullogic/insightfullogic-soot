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

import soot.tagkit.*;
import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;
import soot.baf.*;

public class JInvokeStmt extends AbstractStmt implements InvokeStmt
{
    ValueBox invokeExprBox;

    public JInvokeStmt(Value c)
    {
        this(Jimple.v().newInvokeExprBox(c));
    }

    protected JInvokeStmt(ValueBox invokeExprBox)
    {
        this.invokeExprBox = invokeExprBox;
    }

 
    public Object clone() 
    {
        return new JInvokeStmt(Jimple.cloneIfNecessary(getInvokeExpr()));
    }

    public boolean containsInvokeExpr()
    {
        return true;
    }

    protected String toString(boolean isBrief, Map stmtToName, String indentation)
    {
        if(isBrief)
            return indentation + ((ToBriefString) invokeExprBox.getValue()).toBriefString();
        else
            return indentation + invokeExprBox.getValue().toString();
    }
    
    public void setInvokeExpr(Value invokeExpr)
    {
        invokeExprBox.setValue(invokeExpr);
    }

    public Value getInvokeExpr()
    {
        return invokeExprBox.getValue();
    }

    public ValueBox getInvokeExprBox()
    {
        return invokeExprBox;
    }

    public List getUseBoxes()
    {
        List list = new ArrayList();

        list.addAll(invokeExprBox.getValue().getUseBoxes());
        list.add(invokeExprBox);

        return list;
    }

    public void apply(Switch sw)
    {
        ((StmtSwitch) sw).caseInvokeStmt(this);
    }
   
    public void convertToBaf(JimpleToBafContext context, List out)
    {
        InvokeExpr ie = (InvokeExpr) getInvokeExpr();
        
	context.setCurrentUnit(this);
	
        ((ConvertToBaf) ie).convertToBaf(context, out);
        if(!ie.getMethod().getReturnType().equals(VoidType.v()))
        {
            Unit u = Baf.v().newPopInst(ie.getMethod().getReturnType());
            out.add(u);

	    Iterator it = getTags().iterator();
	    while(it.hasNext()) {
		u.addTag((Tag) it.next());
	    }
	}
    }    

    public boolean fallsThrough() {return true;}
    public boolean branches() {return false;}

}
