/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Phong Co
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

import soot.util.*;
import soot.*;
import soot.toolkits.scalar.*;
import soot.jimple.*;
import java.io.*;
import java.util.*;
import soot.toolkits.graph.*;

public class ConstantPropagatorAndFolder extends BodyTransformer
{
    private static ConstantPropagatorAndFolder instance = new ConstantPropagatorAndFolder();
    private ConstantPropagatorAndFolder() {}

    public static ConstantPropagatorAndFolder v() { return instance; }

    static boolean debug = soot.Main.isInDebugMode;
    static boolean verbose = soot.Main.isVerbose;

    protected void internalTransform(Body b, String phaseName, Map options)
    {
        StmtBody stmtBody = (StmtBody)b;
        int numFolded = 0;
        int numPropagated = 0;

        if (verbose)
            System.out.println("[" + stmtBody.getMethod().getName() +
                               "] Propagating and folding constants...");

        Chain units = stmtBody.getUnits();
        CompleteUnitGraph unitGraph = new CompleteUnitGraph(stmtBody);
        LocalDefs localDefs;
        
        localDefs = new SimpleLocalDefs(unitGraph);

        // Perform a constant/local propagation pass.
        Iterator stmtIt = PseudoTopologicalOrderer.v().newList(unitGraph).iterator();

        // go through each use box in each statement
        while (stmtIt.hasNext()) {
            Stmt stmt = (Stmt) stmtIt.next();

            // propagation pass
            Iterator useBoxIt = stmt.getUseBoxes().iterator();
            ValueBox useBox;

            while (useBoxIt.hasNext()) {
                useBox = (ValueBox) useBoxIt.next();
                if (useBox.getValue() instanceof Local) {
                    Local local = (Local) useBox.getValue();
                    List defsOfUse = localDefs.getDefsOfAt(local, stmt);
                    if (defsOfUse.size() == 1) {
                        DefinitionStmt defStmt =
                            (DefinitionStmt) defsOfUse.get(0);
                        if (defStmt.getRightOp() instanceof NumericConstant) {
                            if (useBox.canContainValue(defStmt.getRightOp())) {
                                useBox.setValue(defStmt.getRightOp());
                                numPropagated++;
                            }
                        }
                    }
                }
            }
                
            // folding pass
            useBoxIt = stmt.getUseBoxes().iterator();

            while (useBoxIt.hasNext()) {
                useBox = (ValueBox) useBoxIt.next();
                Value value = useBox.getValue();
                if (!(value instanceof Constant)) {
                    if (Evaluator.isValueConstantValued(value)) {
                        Value constValue =
                            Evaluator.getConstantValueOf(value);
                        if (useBox.canContainValue(constValue)) {
                            useBox.setValue(constValue);
                            numFolded++;
                        }
                    }
                }
            }
        }

       if (verbose)
            System.out.println("[" + stmtBody.getMethod().getName() +
                "] Propagated: " + numPropagated + ", Folded:  " + numFolded);

    } // optimizeConstants

}
    




