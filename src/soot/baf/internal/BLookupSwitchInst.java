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

package soot.baf.internal;
import soot.jimple.Constant;
import soot.util.*;
import java.util.*;
import soot.*;
import soot.baf.*;

public class BLookupSwitchInst extends AbstractInst implements LookupSwitchInst
{
    UnitBox defaultTargetBox;
    List lookupValues;
    UnitBox[] targetBoxes;
    List unitBoxes;

    public BLookupSwitchInst(Unit defaultTarget, 
                             List lookupValues, List targets)
    {
        this.defaultTargetBox = Baf.v().newInstBox(defaultTarget); 

        this.targetBoxes = new UnitBox[targets.size()];

        for(int i = 0; i < targetBoxes.length; i++)
            this.targetBoxes[i] = Baf.v().newInstBox((Unit) targets.get(i));

        this.lookupValues = new ArrayList();
        this.lookupValues.addAll(lookupValues);

        // Build up unitBoxes
        {
            unitBoxes = new ArrayList();

            for(int i = 0; i < targetBoxes.length; i++)
                unitBoxes.add(targetBoxes[i]);

            unitBoxes.add(defaultTargetBox);
            unitBoxes = Collections.unmodifiableList(unitBoxes);
        }
    }

    public Object clone() 
    {        
        List list = new ArrayList();
        for(int i =0; i< targetBoxes.length; i++) {

            list.add(targetBoxes[i].getUnit());
        }

        
        return new  BLookupSwitchInst(defaultTargetBox.getUnit(), lookupValues, list);
    }


    public int getInCount()
    {
        return 1;
    }

    public int getInMachineCount()
    {
        return 1;
    }
    
    public int getOutCount()
    {
        return 0;
    }

    public int getOutMachineCount()
    {
        return 0;
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

    public void setLookupValues(List lookupValues)
    {
        this.lookupValues = new ArrayList();
        this.lookupValues.addAll(lookupValues);
    }

    public void setLookupValue(int index, int value)
    {
        this.lookupValues.set(index, new Integer(value));
    }

    public int getLookupValue(int index)
    {
        return ((Integer) lookupValues.get(index)).intValue();
    }

    public  List getLookupValues()
    {
        return Collections.unmodifiableList(lookupValues);
    }

    public int getTargetCount() { return targetBoxes.length; }
    
    public Unit getTarget(int index)
    {
        return targetBoxes[index].getUnit();
    }

    public void setTarget(int index, Unit target)
    {
        targetBoxes[index].setUnit(target);
    }

    public void setTargets(List targets)
    {
        for(int i = 0; i < targets.size(); i++)
            targetBoxes[i].setUnit((Unit) targets.get(i));
    }

    public UnitBox getTargetBox(int index)
    {
        return targetBoxes[index];
    }

    public List getTargets()
    {
        List targets = new ArrayList();

        for(int i = 0; i < targetBoxes.length; i++)
            targets.add(targetBoxes[i].getUnit());

        return targets;
    }

    public String getName() { return "lookupswitch"; }

    protected String toString(boolean isBrief, Map unitToName, String indentation)
    {
        StringBuffer buffer = new StringBuffer();
        String endOfLine = (indentation.equals("")) ? " " : StringTools.lineSeparator;
        
        buffer.append(indentation + "lookupswitch" + endOfLine);
            
        buffer.append(indentation + "{" + endOfLine);
        
        for(int i = 0; i < lookupValues.size(); i++)
        {
            buffer.append(indentation + "    case " + lookupValues.get(i) + ": goto " + 
                (String) unitToName.get(getTarget(i)) + ";" + endOfLine);
        }

        buffer.append(indentation + "    default: goto " + (String) unitToName.get(getDefaultTarget()) + ";" + endOfLine);
        buffer.append(indentation + "}");

        return buffer.toString();
    }

    public void toString(UnitPrinter up) {
        up.literal( "lookupswitch" );
        up.newline();
        up.literal("{");
        up.newline();
        
        for(int i = 0; i < lookupValues.size(); i++)
        {
            up.literal("    case ");
            up.constant( (Constant) lookupValues.get(i) );
            up.literal(": goto ");
            targetBoxes[i].toString(up);
            up.literal(";");
            up.newline();
        }

        up.literal("    default: goto ");
        defaultTargetBox.toString(up);
        up.literal(";");
        up.newline();
        up.literal("}");
    }

    public List getUnitBoxes()
    {
        return unitBoxes;
    }

    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseLookupSwitchInst(this);
    }

    public boolean fallsThrough()
    {
        return false;
    }
    public boolean branches()
    {
        return true;
    }


}
