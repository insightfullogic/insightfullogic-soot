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

import soot.baf.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.*;
import soot.util.*;
import java.util.*;
import java.io.*;
import soot.toolkits.scalar.*;


/**
 *   Abstract base class that models the body of a Java method.
 *   Classes that implement an Intermediate Representation for a method body should subclass it.
 *   In particular the classes GrimpBody, JimpleBody and BafBody all extend this
 *   class. This class provides methods that are common to any IR of body, such as methods
 *   to get the body's statements, traps, locals, ...
 *   
 *  @see soot.grimp.GrimpBody
 *  @see soot.jimple.JimpleBody
 *  @see soot.baf.BafBody
 */
public abstract class Body
{
    protected SootMethod method = null;

    protected Chain localChain = new HashChain();
    protected Chain trapChain = new HashChain();
    protected PatchingChain unitChain = new PatchingChain(new HashChain());

    abstract public Object clone();

    /** Used by subclasses during initialization. 
     *  Creation of a Body is triggered by e.g. Jimple.v().newBody(options).
     */
    protected Body(SootMethod m) 
    {       
        this.method = m;
    }

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
    
    /**  @return the number of locals declared in this body */          
    public int getLocalCount()
    {
        return localChain.size();
    }


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
        it = getUnitBoxes().iterator();
        while(it.hasNext()) {
            UnitBox box = (UnitBox) it.next();
            Unit newObject, oldObject = box.getUnit();
            
            // if we have a reference to an old object, replace it 
            // it's clone.
            if( (newObject = (Unit)  bindings.get(oldObject)) != null )
                box.setUnit(newObject);
                
        }        



        // backpatching all local variables.
        it = getUseAndDefBoxes().iterator();
        while(it.hasNext()) {
            ValueBox vb = (ValueBox) it.next();
            if(vb.getValue() instanceof Local) 
                vb.setValue((Value) bindings.get(vb.getValue()));
        }
    }
    
    public void validate()
    {
        validateLocals();
        validateTraps();
        validateUnitBoxes();
    }

    public void validateLocals()
    {
        Iterator it = getUseAndDefBoxes().iterator();
        
        while(it.hasNext()){
            ValueBox vb = (ValueBox) it.next();
            Value value;
            if( (value = vb.getValue()) instanceof Local) {
                if(!localChain.contains(value))
                    throw new RuntimeException("not in chain");
                
            }
        }
    }

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

    public void validateUnitBoxes()
    {
        Iterator it = getUnitBoxes().iterator();
        while (it.hasNext())
        {
            UnitBox ub = (UnitBox)it.next();
            if (!unitChain.contains(ub.getUnit()))
                throw new RuntimeException("unitbox points outside unitChain!");
        }
    }


    /** @return a view of the locals declared in this Body */
    public Chain getLocals() {return localChain;} 

    /** @return a view of the traps founds in this Body */
    public Chain getTraps() {return trapChain;}


    /**
     *  Returns the Units  that make up this body. The units are
     *  returned as a PatchingChain. The client can then manipulate the chain,
     *  adding are removing units, and the changes will be reflected in the body.  
     *  Since a PatchingChain is returned the client need NOT worry about removing exception
     *  boundary units or otherwise corrupting the chain.
     * 
     *  @return the units in this Body 
     *
     *  @see PatchingChain
     *  @see Unit
     */
    public PatchingChain getUnits() {return unitChain;}
                 



    /**
     *   Goes through all the body's Units querying them for their UnitBoxes.
     *   All of the UnitBoxes found are then returned. Only branching Units
     *   will have UnitBoxes; a UnitBox contains a Unit that is a target of a 
     *   branch.
     *   @return a list of all the UnitBoxes held by this body's units.
     *     
     *   @see UnitBox
     *   @see Unit#getUnitBoxes
     *
     */
    public List getUnitBoxes() 
    {
        ArrayList unitBoxList = new ArrayList();
        
        Iterator it = unitChain.iterator();
        while(it.hasNext()) {
            Unit item = (Unit) it.next();
            unitBoxList.addAll(item.getUnitBoxes());  
        }
        
        it = trapChain.iterator();
        while(it.hasNext()) {
            Trap item = (Trap) it.next();
            unitBoxList.addAll(item.getUnitBoxes());  
        }
        
        return unitBoxList;
    }

    

    /**
     *  
     */





    /**
     *   Goes through all the body's Units querying them for the ValueBox
     *   for the Values each Unit uses.
     *   All of the ValueBoxes found are then returned as List.
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
     *   Goes through all this body's Units querying them for the ValueBoxes
     *   for the Values each Unit defines.
     *   All of the ValueBoxes found are then returned as List.
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
     *   Goes through all this body's Units querying them for the ValueBoxes
     *   for the Values each Unit defines and Uses.
     *   All of the ValueBoxes found are then returned as List.
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
            useAndDefBoxList.addAll(item.getUseAndDefBoxes());  
        }
        return useAndDefBoxList;
    }


    /**
     *   Prints out the method corresponding to this Body,( both declaration and body),
     *   in the textual format corresponding to the IR used to encode this body. Default
     *   printBodyOptions are used.
     *
     *   @param out a PrintWriter instance to print to. 
     *
     */
    public void printTo(java.io.PrintWriter out)
    {
        printTo(out, 0);
    }
    

    /**
     *   Prints out the method corresponding to this Body,( both declaration and body),
     *   in the textual format corresponding to the IR used to encode this body.
     *
     *   @param out a PrintWriter instance to print to.
     *   @param printBodyOptions options for printing.
     *
     *   @see PrintJimpleBodyOption
     */
    public void printTo(PrintWriter out, int printBodyOptions)
    {
      	
        boolean isPrecise = !PrintJimpleBodyOption.useAbbreviations(printBodyOptions);
        boolean isNumbered = PrintJimpleBodyOption.numbered(printBodyOptions);
        
        Map stmtToName = new HashMap(unitChain.size() * 2 + 1, 0.7f);


	String decl = getMethod().getDeclaration();


        out.println("    " + decl);        
	
	
        out.println("    {");


        // Print out local variables
        {
            Map typeToLocals = new DeterministicHashMap(this.getLocalCount() * 2 + 1, 0.7f);

            // Collect locals
            {
                Iterator localIt = this.getLocals().iterator();

                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();

                    List localList;
 
                    String typeName = (isPrecise) ? local.getType().toString() : local.getType().toBriefString();
                    
                    if(typeToLocals.containsKey(typeName))
                        localList = (List) typeToLocals.get(typeName);
                    else
                    {
                        localList = new ArrayList();
                        typeToLocals.put(typeName, localList);
                    }

                    localList.add(local);
                }
            }

            // Print locals
            {
                Iterator typeIt = typeToLocals.keySet().iterator();

                while(typeIt.hasNext())
                {
                    String type = (String) typeIt.next();

                    List localList = (List) typeToLocals.get(type);
                    Object[] locals = localList.toArray();
                    out.print("        "  + type + " ");
		    
                    for(int k = 0; k < locals.length; k++)
                    {
                        if(k != 0)
                            out.print(", ");

                        out.print(((Local) locals[k]).getName());
                    }

                    out.println(";");
                }
            }


            if(!typeToLocals.isEmpty())
                out.println();
        }

        // Print out statements
	printStatementsInBody(out, isPrecise, isNumbered);

	
        out.println("    }");
    }
    

    void printStatementsInBody(java.io.PrintWriter out, boolean isPrecise, boolean isNumbered)
    {

        Map stmtToName = new HashMap(unitChain.size() * 2 + 1, 0.7f);
        soot.toolkits.graph.UnitGraph unitGraph = new soot.toolkits.graph.BriefUnitGraph(this);


        // Create statement name table
        {
            Iterator boxIt = this.getUnitBoxes().iterator();

            Set labelStmts = new HashSet();

            // Build labelStmts
            {
                if(!isNumbered)
                    while(boxIt.hasNext())
                    {
                        UnitBox box = (UnitBox) boxIt.next();
                        Unit stmt = (Unit) box.getUnit();
    
                        labelStmts.add(stmt);
                    }
                else
                    labelStmts.addAll(unitChain);

            }

            // Traverse the stmts and assign a label if necessary
            {
                int labelCount = 0;

                Iterator stmtIt = unitChain.iterator();
		
		
                while(stmtIt.hasNext())
                {
                    Unit s = (Unit) stmtIt.next();

                    if(labelStmts.contains(s))
                    {
                        if(isNumbered)
                            stmtToName.put(s, new Integer(labelCount++).toString());
                        else
                            stmtToName.put(s, "label" + (labelCount++));
                    }
                }
            }
        }	


        
        Iterator unitIt = unitChain.iterator();
        Unit currentStmt = null, previousStmt;
        String indent = (isNumbered) ? "    " : "        ";
        
        while(unitIt.hasNext()) {
            
            previousStmt = currentStmt;
            currentStmt = (Unit) unitIt.next();
            
            // Print appropriate header.
                if(isNumbered)
                    out.print("  " + stmtToName.get(currentStmt) + ":");
                else            
                {
                    // Put an empty line if the previous node was a branch node, the current node is a join node
                    //   or the previous statement does not have this statement as a successor, or if
                    //   this statement has a label on it

		    if(currentStmt != unitChain.getFirst()) 
                        {       
                            if(unitGraph.getSuccsOf(previousStmt).size() != 1 ||
                               unitGraph.getPredsOf(currentStmt).size() != 1 ||
                               stmtToName.containsKey(currentStmt))
                                out.println();
                            else {
                                // Or if the previous node does not have this statement as a successor.
                                
                                List succs = unitGraph.getSuccsOf(previousStmt);
                                
                                if(succs.get(0) != currentStmt)
                                    out.println();
                            }
                        }
                    
                     if(stmtToName.containsKey(currentStmt))
			 out.println("     " + stmtToName.get(currentStmt) + ":");
                }
                   
              
		if(isPrecise)
		    out.print(currentStmt.toString(stmtToName, indent));
            else
                out.print(currentStmt.toBriefString(stmtToName, indent));

            out.print(";"); 
            out.println();
        }
	


        // Print out exceptions
        {
            Iterator trapIt = this.getTraps().iterator();

            if(trapIt.hasNext())
                out.println();

            while(trapIt.hasNext())
            {
                Trap trap = (Trap) trapIt.next();

                out.println("        .catch " + trap.getException().getName() + " from " +
                    stmtToName.get(trap.getBeginUnit()) + " to " + stmtToName.get(trap.getEndUnit()) +
                    " with " + stmtToName.get(trap.getHandlerUnit()) + ";");
            }
        }

    }



    /**
     *   Prints out the method corresponding to this Body,( both declaration and body),
     *   in the textual format corresponding to the IR used to encode this body. Includes
     *   extra debugging information.
     *
     *   @param out a PrintWriter instance to print to.
     *   @param printBodyOptions options for printing.
     *
     *   @see PrintJimpleBodyOption
     */
    public void printDebugTo(PrintWriter out, int printBodyOptions)
    {
        boolean isPrecise = !PrintJimpleBodyOption.useAbbreviations(printBodyOptions);
 

        Map stmtToName = new HashMap(unitChain.size() * 2 + 1, 0.7f);

        out.println("    " + getMethod().getDeclaration());        
        out.println("    {");


        // Print out local variables
        {
            Map typeToLocals = new DeterministicHashMap(this.getLocalCount() * 2 + 1, 0.7f);

            // Collect locals
            {
                Iterator localIt = this.getLocals().iterator();

                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();

                    List localList;
 
                    String typeName = (isPrecise) ? local.getType().toString() : local.getType().toBriefString();
                    
                    if(typeToLocals.containsKey(typeName))
                        localList = (List) typeToLocals.get(typeName);
                    else
                    {
                        localList = new ArrayList();
                        typeToLocals.put(typeName, localList);
                    }

                    localList.add(local);
                }
            }

            // Print locals
            {
                Iterator typeIt = typeToLocals.keySet().iterator();

                while(typeIt.hasNext())
                {
                    String type = (String) typeIt.next();

                    List localList = (List) typeToLocals.get(type);
                    Object[] locals = localList.toArray();

                    out.print("        " + type + " ");

                    for(int k = 0; k < locals.length; k++)
                    {
                        if(k != 0)
                            out.print(", ");

                        out.print(((Local) locals[k]).getName());
                    }

                    out.println(";");
                }
            }


            if(!typeToLocals.isEmpty())
                out.println();
        }

        // Print out statements
            printDebugStatementsInBody(out, isPrecise);

        out.println("    }");
    }

    void printDebugStatementsInBody(java.io.PrintWriter out, boolean isPrecise)
    {
        
        Map stmtToName = new HashMap(unitChain.size() * 2 + 1, 0.7f);

        // Create statement name table
        {
            Iterator boxIt = this.getUnitBoxes().iterator();

            Set labelStmts = new HashSet();

            // Build labelStmts
            {
                while(boxIt.hasNext())
                {
                    UnitBox box = (UnitBox) boxIt.next();
                    Unit stmt = (Unit) box.getUnit();

                    labelStmts.add(stmt);
                }
            }

            // Traverse the stmts and assign a label if necessary
            {
                int labelCount = 0;

                Iterator stmtIt = unitChain.iterator();

                while(stmtIt.hasNext())
                {
                    Unit s = (Unit) stmtIt.next();

                    if(labelStmts.contains(s))
                        stmtToName.put(s, "label" + (labelCount++));
                }
            }
        }

        
        Iterator unitIt = unitChain.iterator();
        Unit currentStmt = null, previousStmt;

        while(unitIt.hasNext()) {
            
            previousStmt = currentStmt;
            currentStmt = (Unit) unitIt.next();
            
            if(stmtToName.containsKey(currentStmt))
                out.println("     " + stmtToName.get(currentStmt) + ":");

            if(isPrecise)
                out.print(currentStmt.toString(stmtToName, "        "));
            else
                out.print(currentStmt.toBriefString(stmtToName, "        "));

            out.print(";"); 
            out.println();
        }

        // Print out exceptions
        {
            Iterator trapIt = this.getTraps().iterator();

            if(trapIt.hasNext())
                out.println();

            while(trapIt.hasNext())
            {
                Trap trap = (Trap) trapIt.next();

                out.println("        catch " + trap.getException().getName() + " from " +
                    stmtToName.get(trap.getBeginUnit()) + " to " + stmtToName.get(trap.getEndUnit()) +
                    " with " + stmtToName.get(trap.getHandlerUnit()) + ";");
            }
        }
    }
}
