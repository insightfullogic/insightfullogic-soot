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
class SlowAvailableExpressionsAnalysis extends ForwardFlowAnalysis
{
    Map unitToGenerateSet;
    Map unitToPreserveSet;
    Map rhsToContainingStmt;
    private HashMap valueToEquivValue;

    FlowSet emptySet;
    
    public SlowAvailableExpressionsAnalysis(DirectedGraph dg)
    {
        super(dg);

        UnitGraph g = (UnitGraph)dg;

        /* we need a universe of all of the expressions. */
        Iterator unitsIt = g.getBody().getUnits().iterator();
        ArrayList exprs = new ArrayList();

        // Consider "a + b".  containingExprs maps a and b (object equality) both to "a + b" (equivalence).
        HashMap containingExprs = new HashMap();

        // maps a Value to its EquivalentValue.
        valueToEquivValue = new HashMap();

        // maps an rhs to its containing stmt.  object equality in rhs.
        rhsToContainingStmt = new HashMap();

        HashMap equivValToSiblingList = new HashMap();

        // Create the set of all expressions, and a map from values to their containing expressions.
        while (unitsIt.hasNext())
        {
            Stmt s = (Stmt)unitsIt.next();

            if (s instanceof AssignStmt)
            {
                Value v = ((AssignStmt)s).getRightOp();
                rhsToContainingStmt.put(v, s);
                EquivalentValue ev = (EquivalentValue)valueToEquivValue.get(v);
                if (ev == null)
                {
                    ev = new EquivalentValue(v);
                    valueToEquivValue.put(v, ev);
                }

                Chain sibList = null;
                if (equivValToSiblingList.get(ev) == null)
                    { sibList = new ArrayChain(); equivValToSiblingList.put(ev, sibList); }
                else
                    sibList = (Chain)equivValToSiblingList.get(ev);
                
                if (!sibList.contains(v)) sibList.add(v);

                if (!(v instanceof Expr))
                    continue;

                if (!exprs.contains(v))
                {
                    exprs.add(v);

                    // Add map values for contained objects.
                    Iterator it = v.getUseBoxes().iterator();
                    while (it.hasNext())
                    {
                        Value o = ((ValueBox)it.next()).getValue();
                        EquivalentValue eo = (EquivalentValue)valueToEquivValue.get(o);
                        if (eo == null)
                        {
                            eo = new EquivalentValue((Value)o);
                            valueToEquivValue.put(o, eo);
                        }

                        if (equivValToSiblingList.get(eo) == null)
                            { sibList = new ArrayChain(); equivValToSiblingList.put(eo, sibList); }
                        else
                            sibList = (Chain)equivValToSiblingList.get(eo);
                        if (!sibList.contains(o)) sibList.add(o);

                        Chain l = null;
                        if (containingExprs.containsKey(eo))
                            l = (HashChain)containingExprs.get(eo);
                        else
                        {
                            l = new ArrayChain();
                            containingExprs.put(eo, l);
                        }

                        if (!l.contains(ev))
                            l.add(ev);
                    }
                }
            }
        }

        FlowUniverse exprUniv = new FlowUniverse(exprs.toArray());
        emptySet = new ArrayPackedSet(exprUniv);

        // Create preserve sets.
        {
            unitToPreserveSet = new HashMap(g.size() * 2 + 1, 0.7f);

            Iterator unitIt = g.iterator();

            while(unitIt.hasNext())
            {
                BoundedFlowSet killSet = new ArrayPackedSet(exprUniv);
                Unit s = (Unit) unitIt.next();

                // We need to do more!  In particular handle invokeExprs, etc.

                // For each def (say of x), kill the set of exprs containing x.
                Iterator boxIt = s.getDefBoxes().iterator();

                while(boxIt.hasNext())
                {
                    ValueBox box = (ValueBox) boxIt.next();
                    Value v = box.getValue();
                    EquivalentValue ev = (EquivalentValue)valueToEquivValue.get(v);

                    HashChain c = (HashChain)containingExprs.get(ev);
                    if (c != null)
                    {
                        Iterator it = c.iterator();
                        while (it.hasNext())
                        {
                            // Add all siblings of it.next().
                            EquivalentValue container = (EquivalentValue)it.next();
                            Iterator sibListIt = ((Chain)equivValToSiblingList.get(container)).iterator();
                            while (sibListIt.hasNext())
                                killSet.add(sibListIt.next(), killSet);
                        }
                    }
                }

                // Store complement
                    killSet.complement(killSet);
                    unitToPreserveSet.put(s, killSet);
            }
        }

        // Create generate sets
        {
            unitToGenerateSet = new HashMap(g.size() * 2 + 1, 0.7f);

            Iterator unitIt = g.iterator();

            while(unitIt.hasNext())
            {
                Unit s = (Unit) unitIt.next();

                BoundedFlowSet genSet = new ArrayPackedSet(exprUniv);
                // In Jimple, expressions only occur as the RHS of an AssignStmt.
                if (s instanceof AssignStmt)

                {
                    AssignStmt as = (AssignStmt)s;
                    if (as.getRightOp() instanceof Expr)
                    {
                        // canonical rep of as.getRightOp();
                        Value gen = as.getRightOp();

                        boolean cantAdd = false;
                        if (gen instanceof NewExpr || 
                               gen instanceof NewArrayExpr || 
                               gen instanceof NewMultiArrayExpr)
                            cantAdd = true;
                        if (gen instanceof InvokeExpr)
                            cantAdd = true;

                        // Whee, double negative!
                        if (!cantAdd)
                            genSet.add(gen, genSet);
                    }
                }

                // remove the kill set
                genSet.intersection((FlowSet)unitToPreserveSet.get(s), genSet);

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

        // Perform kill
            in.intersection((FlowSet) unitToPreserveSet.get(unit), out);

        // Perform generation
            out.union((FlowSet) unitToGenerateSet.get(unit), out);
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

