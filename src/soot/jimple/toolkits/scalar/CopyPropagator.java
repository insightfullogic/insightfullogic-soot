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






package soot.jimple.toolkits.scalar;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;

public class CopyPropagator extends BodyTransformer
{
    private static CopyPropagator instance = new CopyPropagator();
    private CopyPropagator() {}

    public static CopyPropagator v() { return instance; }

    public String getDeclaredOptions() { return super.getDeclaredOptions() + " only-regular-locals only-stack-locals"; }
    
    /** Cascaded copy propagator.
    
        If it encounters situations of the form: A: a = ...; B: ... x = a; C:... use (x); 
        where a has only one definition, and x has only one definition (B), then it can 
        propagate immediately without checking between B and C for redefinitions
        of a (namely) A because they cannot occur.  In this case the propagator is global.
        
        Otherwise, if a has multiple definitions then it only checks for redefinitions of
        Propagates constants and copies in extended basic blocks. 
        
        Does not propagate stack locals when the "only-regular-locals" option is true.
    */
    protected void internalTransform(Body b, String phaseName, Map options)
    {
        StmtBody stmtBody = (StmtBody)b;
        boolean propagateStackLocals = !Options.getBoolean(options, "only-regular-locals");
        boolean propagateRegularLocals = !Options.getBoolean(options, "only-stack-locals");
    

        int fastCopyPropagationCount = 0;
        int slowCopyPropagationCount = 0;
        
        if(Main.opts.verbose())
            System.out.println("[" + stmtBody.getMethod().getName() +
                "] Propagating copies...");

        if(Main.opts.time())
            Timers.v().propagatorTimer.start();                
                
        Chain units = stmtBody.getUnits();

        Map localToDefCount = new HashMap();
        
        // Count number of definitions for each local.
        {
            Iterator stmtIt = units.iterator();
        
            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();
                
                if(s instanceof DefinitionStmt &&
                    ((DefinitionStmt) s).getLeftOp() instanceof Local)
                {
                    Local l = (Local) ((DefinitionStmt) s).getLeftOp();
                     
                    if(!localToDefCount.containsKey(l))
                        localToDefCount.put(l, new Integer(1));
                    else 
                        localToDefCount.put(l, new Integer(((Integer) localToDefCount.get(l)).intValue() + 1));
                }
                
            }
        }
        
//            ((JimpleBody) stmtBody).printDebugTo(new java.io.PrintWriter(System.out, true));
            
        CompleteUnitGraph graph = new CompleteUnitGraph(stmtBody);

        LocalDefs localDefs;
        
        localDefs = new SimpleLocalDefs(graph);

        LocalUses localUses = new SimpleLocalUses(graph, localDefs);

        // Perform a local propagation pass.
        {
            Iterator stmtIt = (new PseudoTopologicalOrderer()).newList(graph).iterator();

            while(stmtIt.hasNext())
            {
                Stmt stmt = (Stmt) stmtIt.next();
                Iterator useBoxIt = stmt.getUseBoxes().iterator();

                while(useBoxIt.hasNext())
                {
                    ValueBox useBox = (ValueBox) useBoxIt.next();

                    if(useBox.getValue() instanceof Local)
                    {
                        Local l = (Local) useBox.getValue();

                        if(!propagateStackLocals && l.getName().startsWith("$"))
                            continue;
       
                        if(!propagateRegularLocals && !l.getName().startsWith("$"))
                            continue;
                            
                        List defsOfUse = localDefs.getDefsOfAt(l, stmt);

                        if(defsOfUse.size() == 1)
                        {
                            DefinitionStmt def = (DefinitionStmt) defsOfUse.get(0);

                            if(def.getRightOp() instanceof Local)
                            {
                                Local m = (Local) def.getRightOp();

                                if(l != m)
                                {   
                                    Object dcObj = localToDefCount.get(m);

                                    if (dcObj == null)
                                        throw new RuntimeException("Variable " + m + " used without definition!");

                                    int defCount = ((Integer)dcObj).intValue();
                                    
                                    if(defCount == 0)
                                        throw new RuntimeException("Variable " + m + " used without definition!");
                                    else if(defCount == 1)
                                    {
                                        useBox.setValue(m);
                                        fastCopyPropagationCount++;
                                        continue;
                                    }

                                    List path = graph.getExtendedBasicBlockPathBetween(def, stmt);
                                    
                                    if(path == null)
                                    {
                                        // no path in the extended basic block
                                        continue;
                                    }
                                     
                                    Iterator pathIt = path.iterator();
                                    
                                    // Skip first node
                                        pathIt.next();
                                        
                                    // Make sure that m is not redefined along path
                                    {
                                        boolean isRedefined = false;
                                        
                                        while(pathIt.hasNext())
                                        {
                                            Stmt s = (Stmt) pathIt.next();
                                            
                                            if(stmt == s)
                                            {
                                                // Don't look at the last statement 
                                                // since it is evaluated after the uses
                                                
                                                break;
                                            }   
                                            if(s instanceof DefinitionStmt)
                                            {
                                                if(((DefinitionStmt) s).getLeftOp() == m)
                                                {
                                                    isRedefined = true;
                                                    break;
                                                }        
                                            }
                                        }
                                        
                                        if(isRedefined)
                                            continue;
                                            
                                    }
                                    
                                    useBox.setValue(m);
                                    slowCopyPropagationCount++;
                                }
                            }
                        }
                    }

                 }
            }
        }


        if(Main.opts.verbose())
            System.out.println("[" + stmtBody.getMethod().getName() +
                "]     Propagated: " +
                fastCopyPropagationCount + " fast copies  " +
                slowCopyPropagationCount + " slow copies");
     
        if(Main.opts.time())
            Timers.v().propagatorTimer.end();
    
    }
    
}






























