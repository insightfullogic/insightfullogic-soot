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

import soot.tagkit.*;
import soot.baf.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.*;
import soot.util.*;
import java.util.*;
import java.io.*;
import soot.toolkits.scalar.*;
import soot.options.*;


/**
 *   Abstract base class that models the body (code attribute) of a Java method.
 *   Classes that implement an Intermediate Representation for a method body should subclass it.
 *   In particular the classes GrimpBody, JimpleBody and BafBody all extend this
 *   class. This class provides methods that are common to any IR, such as methods
 *   to get the body's units (statements), traps, and locals.
 *   
 *  @see soot.grimp.GrimpBody
 *  @see soot.jimple.JimpleBody
 *  @see soot.baf.BafBody
 */
public abstract class Body extends AbstractHost implements Serializable
{
    /** The method associated with this Body. */
    protected transient SootMethod method = null;

    /** The chain of locals for this Body. */
    protected Chain localChain = new HashChain();

    /** The chain of traps for this Body. */
    protected Chain trapChain = new HashChain();

    /** The chain of units for this Body. */
    protected PatchingChain unitChain = new PatchingChain(new HashChain());

    /** Creates a deep copy of this Body. */
    abstract public Object clone();

    /** Creates a Body associated to the given method.  Used by subclasses during initialization. 
     *  Creation of a Body is triggered by e.g. Jimple.v().newBody(options).
     */
    protected Body(SootMethod m) 
    {       
        this.method = m;
    }

    /** Creates an extremely empty Body.  The Body is not associated to any method. */
    protected Body() 
    {               
    }

    /** 
     * Returns the method associated with this Body. 
     * @return the method that owns this body.
     */
    public SootMethod getMethod()
    {
        if(method == null)
            throw new RuntimeException("no method associated w/ body");
        return method;
    }


    /** 
     * Sets the method associated with this Body. 
     * @param method the method that owns this body.
     * 
     */    
    public void setMethod(SootMethod method)
    {
        this.method = method;
    }
    
    /** Returns the number of locals declared in this body. */
    public int getLocalCount()
    {
        return localChain.size();
    }

    /** Copies the contents of the given Body into this one. */
    public void importBodyContentsFrom(Body b)
    {
        HashMap bindings = new HashMap();

        Iterator it = b.getUnits().iterator();

        // Clone units in body's statement list 
        while(it.hasNext()) {
            Unit original = (Unit) it.next();
            Unit copy = (Unit) original.clone();
             
            // Add cloned unit to our unitChain.
            unitChain.addLast(copy);

            // Build old <-> new map to be able to patch up references to other units 
            // within the cloned units. (these are still refering to the original
            // unit objects).
            bindings.put(original, copy);
        }

        // Clone trap units.
        it = b.getTraps().iterator();
        while(it.hasNext()) {
            Trap original = (Trap) it.next();
            Trap copy = (Trap) original.clone();
            
            // Add cloned unit to our trap list.
            trapChain.addLast(copy);

            // Store old <-> new mapping.
            bindings.put(original, copy);
        }

        
        // Clone local units.
        it = b.getLocals().iterator();
        while(it.hasNext()) {
            Value original = (Value) it.next();
            Value copy = (Value) original.clone();
            
            // Add cloned unit to our trap list.
            localChain.addLast(copy);

            // Build old <-> new mapping.
            bindings.put(original, copy);
        }
        


        // Patch up references within units using our (old <-> new) map.
        it = getAllUnitBoxes().iterator();
        while(it.hasNext()) {
            UnitBox box = (UnitBox) it.next();
            Unit newObject, oldObject = box.getUnit();
            
            // if we have a reference to an old object, replace it 
            // it's clone.
            if( (newObject = (Unit)  bindings.get(oldObject)) != null )
                box.setUnit(newObject);
                
        }        



        // backpatching all local variables.
        it = getUseBoxes().iterator();
        while(it.hasNext()) {
            ValueBox vb = (ValueBox) it.next();
            if(vb.getValue() instanceof Local) 
                vb.setValue((Value) bindings.get(vb.getValue()));
        }
        it = getDefBoxes().iterator();
        while(it.hasNext()) {
            ValueBox vb = (ValueBox) it.next();
            if(vb.getValue() instanceof Local) 
                vb.setValue((Value) bindings.get(vb.getValue()));
        }
    }
    
    /** Verifies a few sanity conditions on the contents on this body. */
    public void validate()
    {
        validateLocals();
        validateTraps();
        validateUnitBoxes();
        if (Options.v().debug())
            validateUses();
    }

    /** Verifies that each Local of getUseAndDefBoxes() is in this body's locals Chain. */
    public void validateLocals()
    {
        Iterator it;
        it = getUseBoxes().iterator();
        while(it.hasNext()){
            validateLocal( (ValueBox) it.next() );
        }
        it = getDefBoxes().iterator();
        while(it.hasNext()){
            validateLocal( (ValueBox) it.next() );
        }
    }
    private void validateLocal( ValueBox vb ) {
        Value value;
        if( (value = vb.getValue()) instanceof Local) {
            if(!localChain.contains(value))
                throw new RuntimeException("Local not in chain : "+value);                
        }
    }

    /** Verifies that the begin, end and handler units of each trap are in this body. */
    public void validateTraps()
    {
        Iterator it = getTraps().iterator();
        while (it.hasNext())
        {
            Trap t = (Trap)it.next();
            if (!unitChain.contains(t.getBeginUnit()))
                throw new RuntimeException("begin not in chain");

            if (!unitChain.contains(t.getEndUnit()))
                throw new RuntimeException("end not in chain");

            if (!unitChain.contains(t.getHandlerUnit()))
                throw new RuntimeException("handler not in chain");
        }
    }

    /** Verifies that the UnitBoxes of this Body all point to a Unit contained within this body. */
    public void validateUnitBoxes()
    {
        Iterator it = getAllUnitBoxes().iterator();
        while (it.hasNext())
        {
            UnitBox ub = (UnitBox)it.next();
            if (!unitChain.contains(ub.getUnit()))
                throw new RuntimeException
                    ("Unitbox points outside unitChain! to unit : "+ub.getUnit());
        }
    }

    /** Verifies that each use in this Body has a def. */
    public void validateUses()
    {
        LocalDefs ld = new SimpleLocalDefs(new CompleteUnitGraph(this));

        Iterator unitsIt = getUnits().iterator();
        while (unitsIt.hasNext())
        {
            Unit u = (Unit) unitsIt.next();
            Iterator useBoxIt = u.getUseBoxes().iterator();
            while (useBoxIt.hasNext())
            {
                Value v = ((ValueBox)useBoxIt.next()).getValue();
                if (v instanceof Local)
                {
                    // This throws an exception if there is
                    // no def already; we check anyhow.
                    List l = ld.getDefsOfAt((Local)v, u);
                    if (l.size() == 0)
                        throw new RuntimeException("no defs for value!");
                }
            }
        }
    }

    /** Returns a backed chain of the locals declared in this Body. */
    public Chain getLocals() {return localChain;} 

    /** Returns a backed view of the traps found in this Body. */
    public Chain getTraps() {return trapChain;}

    /** Return LHS of the first identity stmt assigning from \@this. **/
    public Local getThisLocal()
    {
        Iterator unitsIt = getUnits().iterator();

        while (unitsIt.hasNext())
        {
            Stmt s = (Stmt)unitsIt.next();
            if (s instanceof IdentityStmt &&
                ((IdentityStmt)s).getRightOp() instanceof ThisRef)
                return (Local)(((IdentityStmt)s).getLeftOp());
        }

        throw new RuntimeException("couldn't find identityref!");
    }

    /** Return LHS of the first identity stmt assigning from \@parameter i. **/
    public Local getParameterLocal(int i)
    {
        Iterator unitsIt = getUnits().iterator();
        while (unitsIt.hasNext())
        {
            Stmt s = (Stmt)unitsIt.next();
            if (s instanceof IdentityStmt && 
                ((IdentityStmt)s).getRightOp() instanceof ParameterRef)
            {
                IdentityStmt is = (IdentityStmt)s;
                ParameterRef pr = (ParameterRef)is.getRightOp();
                if (pr.getIndex() == i)
                    return (Local)is.getLeftOp();
            }
        }

        throw new RuntimeException("couldn't find parameterref!");
    }

    /**
     *  Returns the Chain of Units that make up this body. The units are
     *  returned as a PatchingChain. The client can then manipulate the chain,
     *  adding and removing units, and the changes will be reflected in the body.  
     *  Since a PatchingChain is returned the client need <i>not</i> worry about removing exception
     *  boundary units or otherwise corrupting the chain.
     * 
     *  @return the units in this Body 
     *
     *  @see PatchingChain
     *  @see Unit
     */
    public PatchingChain getUnits() 
    {
        return unitChain;
    }

    /**
     * Returns the result of iterating through all Units in this body
     * and querying them for their UnitBoxes.  All UnitBoxes thus
     * found are returned.  Branching Units and statements which use
     * PhiExpr will have UnitBoxes; a UnitBox contains a Unit that is
     * either a target of a branch or is being used as a pointer to
     * the end of a CFG block.
     *
     * <p> This method is typically used for pointer patching, eg when
     * the unit chain is cloned.
     *
     * @return A list of all the UnitBoxes held by this body's units.
     * @see UnitBox
     * @see #getUnitBoxes(boolean)
     * @see Unit#getUnitBoxes()
     * @see soot.shimple.PhiExpr#getUnitBoxes()
     **/
    public List getAllUnitBoxes() 
    {
        ArrayList unitBoxList = new ArrayList();
        {
	    Iterator it = unitChain.iterator();
	    while(it.hasNext()) {
		Unit item = (Unit) it.next();
		unitBoxList.addAll(item.getUnitBoxes());  
	    }
	}
        
	{
	    Iterator it = trapChain.iterator();
	    while(it.hasNext()) {
		Trap item = (Trap) it.next();
		unitBoxList.addAll(item.getUnitBoxes());  
	    }
        }

	{
	    Iterator it = getTags().iterator();
	    while(it.hasNext()) {
		Tag t = (Tag) it.next();
		if( t instanceof CodeAttribute) 		    
		    unitBoxList.addAll(((CodeAttribute) t).getUnitBoxes());
	    }
	}
	
        return unitBoxList;
    }

    /**
     * If branchTarget is true, returns the result of iterating
     * through all branching Units in this body and querying them for
     * their UnitBoxes. These UnitBoxes contain Units that are the
     * target of a branch.  This is useful for, say, labeling blocks
     * or updating the targets of branching statements.
     *
     * <p> If branchTarget is false, returns the result of iterating
     * through the non-branching Units in this body and querying them
     * for their UnitBoxes.  Any such UnitBoxes (typically from
     * PhiExpr) contain a Unit that indicates the end of a CFG block.
     *   
     * @return a list of all the UnitBoxes held by this body's
     * branching units.
     *     
     * @see UnitBox
     * @see #getAllUnitBoxes()
     * @see Unit#getUnitBoxes()
     * @see soot.shimple.PhiExpr#getUnitBoxes()
     **/
    public List getUnitBoxes(boolean branchTarget) 
    {
        ArrayList unitBoxList = new ArrayList();
        {
	    Iterator it = unitChain.iterator();
	    while(it.hasNext()) {
		Unit item = (Unit) it.next();
                if(branchTarget){
                    if(item.branches())
                        unitBoxList.addAll(item.getUnitBoxes());
                }
                else{
                    if(!item.branches())
                        unitBoxList.addAll(item.getUnitBoxes());
                }
	    }
	}

	{
	    Iterator it = trapChain.iterator();
	    while(it.hasNext()) {
		Trap item = (Trap) it.next();
		unitBoxList.addAll(item.getUnitBoxes());  
	    }
        }

	{
	    Iterator it = getTags().iterator();
	    while(it.hasNext()) {
		Tag t = (Tag) it.next();
		if( t instanceof CodeAttribute) 		    
		    unitBoxList.addAll(((CodeAttribute) t).getUnitBoxes());
	    }
	}
	
        return unitBoxList;
    }


    /**
     *   Returns the result of iterating through all Units in this
     *   body and querying them for ValueBoxes used. 
     *   All of the ValueBoxes found are then returned as a List.
     *
     *   @return a list of all the ValueBoxes for the Values used this body's units.
     *     
     *   @see Value
     *   @see Unit#getUseBoxes
     *   @see ValueBox
     *   @see Value
     *
     */   
    public List getUseBoxes()
    {
        ArrayList useBoxList = new ArrayList();
        
        Iterator it = unitChain.iterator();
        while(it.hasNext()) {
            Unit item = (Unit) it.next();
            useBoxList.addAll(item.getUseBoxes());  
        }
        return useBoxList;
    }


    /**
     *   Returns the result of iterating through all Units in this
     *   body and querying them for ValueBoxes defined.
     *   All of the ValueBoxes found are then returned as a List.
     *
     *   @return a list of all the ValueBoxes for Values defined by this body's units.
     *     
     *   @see Value
     *   @see Unit#getDefBoxes
     *   @see ValueBox
     *   @see Value
     */   
    public List getDefBoxes()
    {
        ArrayList defBoxList = new ArrayList();
        
        Iterator it = unitChain.iterator();
        while(it.hasNext()) {
            Unit item = (Unit) it.next();
            defBoxList.addAll(item.getDefBoxes());  
        }
        return defBoxList;
    }

     /**
     *   Returns a list of boxes corresponding to Values 
     * either used or defined in any unit of this Body.
     *
     *   @return a list of ValueBoxes for held by the body's Units.
     *     
     *   @see Value
     *   @see Unit#getUseAndDefBoxes
     *   @see ValueBox
     *   @see Value
     */       
    public List getUseAndDefBoxes()
    {        
        ArrayList useAndDefBoxList = new ArrayList();
        
        Iterator it = unitChain.iterator();
        while(it.hasNext()) {
            Unit item = (Unit) it.next();
            useAndDefBoxList.addAll(item.getUseBoxes());  
            useAndDefBoxList.addAll(item.getDefBoxes());  
        }
        return useAndDefBoxList;
    }



}








