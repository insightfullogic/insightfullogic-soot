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
import soot.baf.*;
import soot.util.*;
import java.util.*;

public class JLookupSwitchStmt extends AbstractStmt 
    implements LookupSwitchStmt, ConvertToBaf
{
    UnitBox defaultTargetBox;
    ValueBox keyBox;
    /** List of lookup values from the corresponding bytecode instruction,
     * represented as IntConstants. */
    List lookupValues;
    protected UnitBox[] targetBoxes;

    List stmtBoxes;

    // This method is necessary to deal with constructor-must-be-first-ism.
    private static UnitBox[] getTargetBoxesArray(List targets)
    {
        UnitBox[] targetBoxes = new UnitBox[targets.size()];

        for(int i = 0; i < targetBoxes.length; i++)
            targetBoxes[i] = Jimple.v().newStmtBox((Stmt) targets.get(i));

        return targetBoxes;
    }


    private static UnitBox[] unitBoxListToArray(List targets) {
        UnitBox[] targetBoxes = new UnitBox[targets.size()];
        
        for(int i = 0; i < targetBoxes.length; i++)
            targetBoxes[i] = (UnitBox) targets.get(i);
        return targetBoxes;
    }

    /** Constructs a new JLookupSwitchStmt. lookupValues should be a list of IntConst s. */ 
    public JLookupSwitchStmt(Value key, List lookupValues, List targets, Unit defaultTarget)
    {
        this(Jimple.v().newImmediateBox(key),
             lookupValues, getTargetBoxesArray(targets),
             Jimple.v().newStmtBox(defaultTarget));
    }

    /** Constructs a new JLookupSwitchStmt. lookupValues should be a list of IntConst s. */     
    public JLookupSwitchStmt(Value key, List lookupValues, List targets, UnitBox defaultTarget)
    {
        this(Jimple.v().newImmediateBox(key),
             lookupValues, unitBoxListToArray(targets),
             defaultTarget);
    }


    public Object clone() 
    {
        int lookupValueCount = lookupValues.size();
        List clonedLookupValues = new ArrayList(lookupValueCount);

        for( int i = 0; i < lookupValueCount ;i++) {
            clonedLookupValues.add(i, IntConstant.v(getLookupValue(i)));
        }
        
        return new JLookupSwitchStmt(getKey(), clonedLookupValues, getTargets(), getDefaultTarget());
    }


    protected JLookupSwitchStmt(ValueBox keyBox, List lookupValues, 
                                UnitBox[] targetBoxes, 
                                UnitBox defaultTargetBox)
    {
        this.keyBox = keyBox;
        this.defaultTargetBox = defaultTargetBox;
        this.targetBoxes = targetBoxes;

        this.lookupValues = new ArrayList();
        this.lookupValues.addAll(lookupValues);

        // Build up stmtBoxes
        {
            stmtBoxes = new ArrayList();

            for(int i = 0; i < targetBoxes.length; i++)
                stmtBoxes.add(targetBoxes[i]);

            stmtBoxes.add(defaultTargetBox);
            stmtBoxes = Collections.unmodifiableList(stmtBoxes);
        }
    }

    protected String toString(boolean isBrief, Map stmtToName, String indentation)
    {
        StringBuffer buffer = new StringBuffer();
        String endOfLine = (indentation.equals("")) ? " " : StringTools.lineSeparator;
        
        buffer.append(indentation + Jimple.v().LOOKUPSWITCH + "(" + ((isBrief) ? ((ToBriefString) keyBox.getValue()).toBriefString() :
            keyBox.getValue().toString()) + ")" + endOfLine);
            
        buffer.append(indentation + "{" + endOfLine);
        
        for(int i = 0; i < lookupValues.size(); i++)
        {
            buffer.append(indentation + "    " +  Jimple.v().CASE + " " + lookupValues.get(i) + ": " +  Jimple.v().GOTO + " " + 
                (String) stmtToName.get(getTarget(i)) + ";" + endOfLine);
        }

        buffer.append(indentation + "    " +  Jimple.v().DEFAULT + ": " +  Jimple.v().GOTO +
                      " " + (String) stmtToName.get(getDefaultTarget()) + ";" + endOfLine);
        buffer.append(indentation + "}");

        return buffer.toString();
    }
    
    public void toString(UnitPrinter up)
    {
        up.literal(Jimple.v().LOOKUPSWITCH);
        up.literal("(");
        keyBox.toString(up);
        up.literal(")");
        up.newline();
        up.literal("{");
        up.newline();
        for(int i = 0; i < lookupValues.size(); i++) {
            up.literal("    ");
            up.literal(Jimple.v().CASE);
            up.literal(" ");
            up.constant((Constant)lookupValues.get(i));
            up.literal(": ");
            up.literal(Jimple.v().GOTO);
            up.literal(" ");
            targetBoxes[i].toString(up);
            up.literal(";");
            up.newline();
        }
        
        up.literal("    ");
        up.literal(Jimple.v().DEFAULT);
        up.literal(": ");
        up.literal(Jimple.v().GOTO);
        up.literal(" ");
        defaultTargetBox.toString(up);
        up.literal(";");
        up.newline();
        up.literal("}");
    }

    public Unit getDefaultTarget()
    {
        return defaultTargetBox.getUnit();
    }

    public void setDefaultTarget(Unit defaultTarget)
    {
        defaultTargetBox.setUnit(defaultTarget);
    }

    public UnitBox getDefaultTargetBox()
    {
        return defaultTargetBox;
    }

    public Value getKey()
    {
        return keyBox.getValue();
    }

    public void setKey(Value key)
    {
        keyBox.setValue(key);
    }

    public ValueBox getKeyBox()
    {
        return keyBox;
    }

    public void setLookupValues(List lookupValues)
    {
        this.lookupValues = new ArrayList();
        this.lookupValues.addAll(lookupValues);
    }

    public void setLookupValue(int index, int value)
    {
        this.lookupValues.set(index, IntConstant.v(value));
    }


    public int getLookupValue(int index)
    {
        return ((IntConstant)lookupValues.get(index)).value;
    }

    public  List getLookupValues()
    {
        return Collections.unmodifiableList(lookupValues);
    }

    public int getTargetCount()
    {
        return targetBoxes.length;
    }

    public Unit getTarget(int index)
    {
        return targetBoxes[index].getUnit();
    }

    public UnitBox getTargetBox(int index)
    {
        return targetBoxes[index];
    }

    public void setTarget(int index, Unit target)
    {
        targetBoxes[index].setUnit(target);
    }

    public List getTargets()
    {
        List targets = new ArrayList();

        for(int i = 0; i < targetBoxes.length; i++)
            targets.add(targetBoxes[i].getUnit());

        return targets;
    }

    public void setTargets(Unit[] targets)
    {
        for(int i = 0; i < targets.length; i++)
            targetBoxes[i].setUnit(targets[i]);
    }

    public List getUseBoxes()
    {
        List list = new ArrayList();

        list.addAll(keyBox.getValue().getUseBoxes());
        list.add(keyBox);

        return list;
    }

    public List getUnitBoxes()
    {
        return stmtBoxes;
    }

    public void apply(Switch sw)
    {
      ((StmtSwitch) sw).caseLookupSwitchStmt(this);
    }
    
    public void convertToBaf(JimpleToBafContext context, List out)
    {
        ArrayList targetPlaceholders = new ArrayList();

        ((ConvertToBaf)(getKey())).convertToBaf(context, out);

        for (int i = 0; i < targetBoxes.length; i++)
        {
            targetPlaceholders.add(Baf.v().newPlaceholderInst
                                   (getTarget(i)));
        }
	
	Unit u;
        out.add(u = Baf.v().newLookupSwitchInst
                (Baf.v().newPlaceholderInst(getDefaultTarget()),
                 getLookupValues(), targetPlaceholders));

	Unit currentUnit = this;

	Iterator it = currentUnit.getTags().iterator();	
	while(it.hasNext()) {
	    u.addTag((Tag) it.next());
	}
	

    }




    

    public boolean fallsThrough(){return false;}
    public boolean branches(){return true;}

}

