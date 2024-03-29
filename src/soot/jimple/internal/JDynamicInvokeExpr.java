/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
 * Copyright (C) 2004 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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
import soot.baf.Baf;
import soot.jimple.*;
import soot.tagkit.Tag;
import soot.util.Switch;

import java.util.*;
@SuppressWarnings({"serial","unchecked","rawtypes"})
public class JDynamicInvokeExpr extends AbstractInvokeExpr  implements DynamicInvokeExpr, ConvertToBaf
{
    public JDynamicInvokeExpr(SootMethodRef methodRef, List args)
    {
    	if(!methodRef.getSignature().startsWith("<java.dyn.InvokeDynamic: "))
    		throw new RuntimeException("Receiver type of JDynamicInvokeExpr must be java.dyn.InvokeDynamic!");
    	
        this.methodRef = methodRef;
        this.argBoxes = new ValueBox[args.size()];

        for(int i = 0; i < args.size(); i++)
        {
        	this.argBoxes[i] = Jimple.v().newImmediateBox((Value) args.get(i));	
        }
    }
    
    public Object clone() 
    {
        ArrayList clonedArgs = new ArrayList(getArgCount());

        for(int i = 0; i < getArgCount(); i++) {
            clonedArgs.add(i, getArg(i));
        }
        
        return new  JDynamicInvokeExpr(methodRef, clonedArgs);
    }
    
    public boolean equivTo(Object o)
    {
        if (o instanceof JDynamicInvokeExpr)
        {
            JDynamicInvokeExpr ie = (JDynamicInvokeExpr)o;
            if (!(getMethod().equals(ie.getMethod()) && 
                    argBoxes.length == ie.argBoxes.length))
                return false;
            for (ValueBox element : argBoxes)
				if (!(element.getValue().equivTo(element.getValue())))
                    return false;
            return true;
        }
        return false;
    }

    /** Returns a hash code for this object, consistent with structural equality. */
    public int equivHashCode() 
    {
        return getMethod().equivHashCode() * 17;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(Jimple.DYNAMICINVOKE + " " + methodRef.getSignature() + "(");

        for(int i = 0; i < argBoxes.length; i++)
        {
            if(i != 0)
                buffer.append(", ");

            buffer.append(argBoxes[i].getValue().toString());
        }

        buffer.append(")");

        return buffer.toString();
    }
    
    public void toString(UnitPrinter up)
    {
        up.literal(Jimple.DYNAMICINVOKE);
        up.literal(" ");
        up.methodRef(methodRef);
        up.literal("(");
        
        for(int i = 0; i < argBoxes.length; i++)
        {
            if(i != 0)
                up.literal(", ");
                
            argBoxes[i].toString(up);
        }

        up.literal(")");
    }

    
    public void apply(Switch sw)
    {
        ((ExprSwitch) sw).caseDynamicInvokeExpr(this);
    }
    
    public List getUseBoxes()
    {
        List list = new ArrayList();

        for (ValueBox element : argBoxes) {
            list.addAll(element.getValue().getUseBoxes());
            list.add(element);
        }

        return list;
    }
    
    public void convertToBaf(JimpleToBafContext context, List<Unit> out)
    {
    	for (ValueBox element : argBoxes) {
    		((ConvertToBaf)(element.getValue())).convertToBaf(context, out);
    	}

    	Unit u;
    	out.add(u = Baf.v().newDynamicInvokeInst(methodRef));

    	Unit currentUnit = context.getCurrentUnit();

    	Iterator it = currentUnit.getTags().iterator();	
    	while(it.hasNext()) {
    		u.addTag((Tag) it.next());
    	}
    }
}
