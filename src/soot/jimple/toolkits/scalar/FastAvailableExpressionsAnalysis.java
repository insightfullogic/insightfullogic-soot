/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrick Lam
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
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;

// future work: fieldrefs.

/** Implements an available expressions analysis on local variables. 
 * The current implementation is slow but correct.
 * A better implementation would use an implicit universe and
 * the kill rule would be computed on-the-fly for each statement. */
class FastAvailableExpressionsAnalysis extends ForwardFlowAnalysis
{
    Map unitToGenerateSet;
    Map unitToPreserveSet;
    Map rhsToContainingStmt;
    private HashMap valueToEquivValue;

    FlowSet emptySet;
    
    public FastAvailableExpressionsAnalysis(DirectedGraph dg)
    {
        super(dg);

        UnitGraph g = (UnitGraph)dg;

        /* we need a universe of all of the expressions. */
        emptySet = new ArraySparseSet();

        // Create generate sets
        {
            unitToGenerateSet = new HashMap(g.size() * 2 + 1, 0.7f);

            Iterator unitIt = g.iterator();

            while(unitIt.hasNext())
            {
                Unit s = (Unit) unitIt.next();

                FlowSet genSet = new ArraySparseSet();
                // In Jimple, expressions only occur as the RHS of an AssignStmt.
                if (s instanceof AssignStmt)

                {
                    AssignStmt as = (AssignStmt)s;
                    if (as.getRightOp() instanceof Expr)
                    {
                        Value gen = as.getRightOp();

                        boolean cantAdd = false;
                        if (gen instanceof NewExpr || 
                               gen instanceof NewArrayExpr || 
                               gen instanceof NewMultiArrayExpr)
                            cantAdd = true;
                        if (gen instanceof InvokeExpr || gen instanceof FieldRef)
                            cantAdd = true;

                        // Whee, double negative!
                        if (!cantAdd)
                            genSet.add(gen, genSet);
                    }
                }

                unitToGenerateSet.put(s, genSet);
            }
        }

        doAnalysis();
    }

    protected Object newInitialFlow()
    {
        BoundedFlowSet out = (BoundedFlowSet)emptySet.clone();
        out.complement(out);
        return out;
    }

    protected void customizeInitialFlowGraph()
    {
        // Initialize heads to {}
        Iterator it = graph.getHeads().iterator();
        while (it.hasNext())
        {
            Object head = it.next();
            unitToBeforeFlow.put(head, emptySet.clone());
        }
    }

    protected void flowThrough(Object inValue, Directed unit, Object outValue)
    {
        FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

        // Perform generation
            out.union((FlowSet) unitToGenerateSet.get(unit), out);

        // Perform kill.
	    Unit u = (Unit)unit;
	    Iterator defIt = u.getDefBoxes().iterator();
	    List toRemove = new ArrayList();

	    List l = ((FlowSet)out).toList();
	    while (defIt.hasNext())
	    {
		Value def = (Value)defIt.next();
		ListIterator it = l.listIterator();

		// iterate over things in out set.
	    availIteration:
		while (it.hasNext())
		{
		    Expr avail = (Expr)it.next();
		    Iterator usesIt = avail.getUseBoxes().iterator();

		    while (usesIt.hasNext())
		    {
			Value use = (Value)usesIt.next();
			if (use.equals(def))
			{
			    out.remove(avail, out);

			    // prevent re-iterating over this guy.
			    it.remove();
			    break availIteration;
			}
		    }
		}
	    }
    }

    protected void merge(Object in1, Object in2, Object out)
    {
        FlowSet inSet1 = (FlowSet) in1,
            inSet2 = (FlowSet) in2;

        FlowSet outSet = (FlowSet) out;

        inSet1.intersection(inSet2, outSet);
    }
    
    protected void copy(Object source, Object dest)
    {
        FlowSet sourceSet = (FlowSet) source,
            destSet = (FlowSet) dest;
            
        sourceSet.copy(destSet);
    }
}

