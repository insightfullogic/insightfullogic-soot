/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch, Feng Qian
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
 * Modified by the Sable Research Group and others 1997-2002.
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.jimple.toolkits.scalar.pre;
import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.toolkits.scalar.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;

/** 
 * Computes the earliest points for the given expressions.<br>
 * This basicly finds the highest point in the flow-graph where we can put each
 * computation, without introducing new computations on any path.<br>
 * More technically: A computation is earliest, if at the current point the
 * computation is down-safe, and if either:
 * <ul>
 * <li> any of the predecessors is not transparent, or 
 * <li> if any predecessors is not "safe" (ie. the insertion of
 * this computation would insert it on a path, where it was not before).
 * </ul><br>
 * Intuitively: If the one predecessor is not transparent, we can't push the
 * computation further up. If one of the predecessor is not safe, we would
 * introduce a new computation on its path. Hence we can't push further up.<p>
 * Note that this computation is linear in the number of units, as we don't 
 * need to find any fixed-point.
 *
 * This implementation follows Knoop's PLDI 1992 paper.
 *
 * @see DownSafetyAnalysis
 */
public class EarliestnessComputation extends ForwardFlowAnalysis{
  private DownSafetyAnalysis downSafe;
  private SideEffectTester sideEffect;
  private BoundedFlowSet set;

  private Map unitToNotTrans;
  

  /**
   * given an UpSafetyAnalysis and a DownSafetyAnalysis, performs the
   * earliest-computation.<br>
   * the naive side-effect tester will be used to decide if a node is
   * transparent.
   *
   * @param unitGraph the Unitgraph we'll work on.
   * @param upSafe (unused) a UpSafetyAnalysis of <code>unitGraph</code>.
   * @param downSafe a DownSafetyAnalysis of <code>unitGraph</code>
   */
  public EarliestnessComputation(UnitGraph unitGraph, 
				 UpSafetyAnalysis upSafe,
				 DownSafetyAnalysis downSafe) {
    this(unitGraph, upSafe, downSafe, new NaiveSideEffectTester());
  }

  /**
   * given an UpSafetyAnalysis and a DownSafetyAnalysis, performs the
   * earliest-computation.<br>
   *
   * @param unitGraph the Unitgraph we'll work on.
   * @param upSafe (unused) a UpSafetyAnalysis of <code>unitGraph</code>
   * @param downSafe a DownSafetyAnalysis of <code>unitGraph</code>
   * @param sideEffect the SideEffectTester that will tell if a node is
   * transparent or not.
   */
  public EarliestnessComputation(UnitGraph unitGraph,
				 UpSafetyAnalysis upSafe,
				 DownSafetyAnalysis downSafe, 
				 SideEffectTester sideEffect) {
    this(unitGraph, upSafe, downSafe, sideEffect, new BoundedArraySparseSet());
  }
  
  /**
   * given an UpSafetyAnalysis and a DownSafetyAnalysis, performs the
   * earliest-computation.<br>
   * allows to share sets over multiple analyses (set-operations are usually
   * more efficient, if the sets come from the same source).
   *
   * @param unitGraph the Unitgraph we'll work on.
   * @param upSafe (unused) a UpSafetyAnalysis of <code>unitGraph</code>
   * @param downSafe a DownSafetyAnalysis of <code>unitGraph</code>
   * @param sideEffect the SideEffectTester that will tell if a node is
   * transparent or not.
   * @param set the shared set.
   */
  public EarliestnessComputation(UnitGraph unitGraph,
				 UpSafetyAnalysis upSafe,
				 DownSafetyAnalysis downSafe, 
				 SideEffectTester sideEffect, 
				 FlowSet fullset) {
    super(unitGraph);

    this.sideEffect = sideEffect;
    this.downSafe   = downSafe;
    this.set        = (BoundedFlowSet)fullset;

    // pre-compute ~TRANSLoc
    this.unitToNotTrans = new HashMap(unitGraph.size() + 1, 0.7f);
    Iterator unitIt = unitGraph.iterator();
    while (unitIt.hasNext()) {
      Unit u = (Unit)unitIt.next();
      FlowSet s = (FlowSet)set.topSet();
      Iterator exprIt = s.iterator();
      while (exprIt.hasNext()) {
	EquivalentValue equiVal = (EquivalentValue)exprIt.next();
	Value avail = equiVal.getValue();
	if (avail instanceof FieldRef) {
	  if (sideEffect.unitCanWriteTo(u, avail))
	    exprIt.remove();
	} else {
	  Iterator usesIt = avail.getUseBoxes().iterator();

	  // iterate over uses in each avail.
	  while (usesIt.hasNext()) {
	    Value use = ((ValueBox)usesIt.next()).getValue();

	    if (sideEffect.unitCanWriteTo(u, use)) {                
	      exprIt.remove();
	      break;
	    }
	  }
	}
      }   
      
      this.unitToNotTrans.put(u, s);
    }
    doAnalysis();
  }

  protected Object newInitialFlow(){
    return set.emptySet();
  }

  // same effect as call customizeInitialFlowGraph...
  protected Object entryInitialFlow(){
    return set.topSet();
  }

  protected void flowThrough(Object inValue,
			     Object unit,
			     Object outValue) {
    FlowSet in = (FlowSet)inValue;
    FlowSet out = (FlowSet)outValue;

    in.copy(out);

    // remove down-safe
    FlowSet downSafeSet = (FlowSet)this.downSafe.getFlowAfter(unit);
    out.difference(downSafeSet);

    // add ~TRANSloc
    FlowSet notTransSet = (FlowSet)this.unitToNotTrans.get(unit);
    out.union(notTransSet);
  }

  protected void merge(Object in1, Object in2, Object out){
    FlowSet inSet1 = (FlowSet)in1;
    FlowSet inSet2 = (FlowSet)in2;

    FlowSet outSet = (FlowSet)out;

    inSet1.union(inSet2, outSet);
  }

  protected void copy(Object source, Object dest){
    FlowSet sourceSet = (FlowSet)source;
    FlowSet destSet = (FlowSet)dest;

    sourceSet.copy(destSet);
  }
}
