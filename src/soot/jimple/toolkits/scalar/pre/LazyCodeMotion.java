/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
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


package soot.jimple.toolkits.scalar.pre;
import soot.jimple.toolkits.graph.*;
import soot.jimple.toolkits.scalar.*;
import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;

/** 
 * performs a partial redundancy elimination (= code motion). This is done, by
 * introducing helper-vars, that store an already computed value, or if a
 * compuation only arrives partially (not from all predecessors) inserts a new
 * computation on these paths.
 * afterwards).<br>
 * In order to catch every redundant expression, this transformation must be
 * done on a graph without critical edges. Therefore the first thing we do, is
 * removing them. A subsequent pass can then easily remove the synthetic nodes
 * we have introduced.<br>
 * The term "lazy" refers to the fact, that we move computations only if
 * necessary.
 *
 * @see soot.jimple.toolkits.graph.CriticalEdgeRemover
 */
public class LazyCodeMotion extends BodyTransformer {
  private static final String PREFIX = "$lcm";

  private static LazyCodeMotion instance = new LazyCodeMotion();
  private LazyCodeMotion() {}

  public static LazyCodeMotion v() { return instance; }

  public String getDeclaredOptions() {
    return super.getDeclaredOptions() + " safe unroll";
  }

  // safe is one out of "safe" "medium" "unsafe"
  public String getDefaultOptions() { return "safe:medium unroll:true"; }
        
  /**
   * performs the lazy code motion.
   */
  protected void internalTransform(Body b, String phaseName, Map options) {
    HashMap expToHelper = new HashMap();
    Chain unitChain = b.getUnits();
    String safe = Options.getString(options, "safe");
    boolean unroll = Options.getBoolean(options, "unroll");

    if(Main.isVerbose) System.out.println("[" + b.getMethod().getName() +
                                          "] Performing Lazy Code Motion...");

    if (unroll) new LoopConditionUnroller().transform(b, phaseName + ".lcu");

    CriticalEdgeRemover.v().transform(b, phaseName + ".cer");

    UnitGraph graph = new BriefUnitGraph(b);

    /* map each unit to its RHS. only take binary expressions */
    Map unitToEquivRhs = new UnitMap(b, graph.size() + 1, 0.7f) {
	protected Object mapTo(Unit unit) {
	  Value tmp = SootFilter.noInvokeRhs(unit);
	  Value tmp2 = SootFilter.binop(tmp);
	  if (tmp2 == null) tmp2 = SootFilter.concreteRef(tmp);
	  return SootFilter.equiVal(tmp2);
	}
      };

    /* same as before, but without exception-throwing expressions */
    Map unitToNoExceptionEquivRhs = new UnitMap(b, graph.size() + 1, 0.7f) {
	protected Object mapTo(Unit unit) {
	  Value tmp = SootFilter.binopRhs(unit);
	  tmp = SootFilter.noExceptionThrowing(tmp);
	  return SootFilter.equiVal(tmp);
	}
      };

    FlowUniverse universe = new CollectionFlowUniverse(unitToEquivRhs.values());
    BoundedFlowSet set = new BoundedArraySparseSet(universe);

    /* if a more precise sideeffect-tester comes out, please change it here! */
    SideEffectTester sideEffect = new NaiveSideEffectTester();
    UpSafetyAnalysis upSafe;
    DownSafetyAnalysis downSafe; 
    EarliestnessComputation earliest;
    DelayabilityAnalysis delay;
    NotIsolatedAnalysis notIsolated;
    LatestComputation latest;

    if ("safe".equals(safe))
      upSafe = new UpSafetyAnalysis(graph, unitToNoExceptionEquivRhs,
                                    sideEffect, set);
    else
      upSafe = new UpSafetyAnalysis(graph, unitToEquivRhs, sideEffect, set);

    if ("unsafe".equals(safe))
      downSafe = new DownSafetyAnalysis(graph, unitToEquivRhs, sideEffect, set);
    else {
      downSafe = new DownSafetyAnalysis(graph, unitToNoExceptionEquivRhs,
					sideEffect, set);
      /* we include the exception-throwing expressions at their uses */
      Iterator unitIt = unitChain.iterator();
      while (unitIt.hasNext()) {
	Unit currentUnit = (Unit)unitIt.next();
	Object rhs = unitToEquivRhs.get(currentUnit);
	if (rhs != null)
	  ((FlowSet)downSafe.getFlowBefore(currentUnit)).add(rhs);
      }
    }

    earliest = new EarliestnessComputation(graph, upSafe, downSafe, sideEffect,
                                           set);
    delay = new DelayabilityAnalysis(graph, earliest, unitToEquivRhs, set);
    latest = new LatestComputation(graph, delay, unitToEquivRhs, set);
    notIsolated = new NotIsolatedAnalysis(graph, latest, unitToEquivRhs, set);

    LocalCreation localCreation = new LocalCreation(b.getLocals(), PREFIX);

    /* debug */
    /*
    {
      System.out.println("========" + b.getMethod().getName());
      Iterator unitIt = unitChain.iterator();
      while (unitIt.hasNext()) {
	Unit currentUnit = (Unit) unitIt.next();
        Value equiVal = (Value)unitToEquivRhs.get(currentUnit);
        FlowSet latestSet = (FlowSet)latest.getFlowBefore(currentUnit);
        FlowSet notIsolatedSet =
          (FlowSet)notIsolated.getFlowAfter(currentUnit);
        FlowSet delaySet = (FlowSet)delay.getFlowBefore(currentUnit);
        FlowSet earlySet = ((FlowSet)earliest.getFlowBefore(currentUnit));
	FlowSet upSet = (FlowSet)upSafe.getFlowBefore(currentUnit);
	FlowSet downSet = (FlowSet)downSafe.getFlowBefore(currentUnit);
	System.out.println(currentUnit);
        System.out.println(" rh: " + equiVal);
        System.out.println(" up: " + upSet);
	System.out.println(" do: " + downSet);
        System.out.println(" is: " + notIsolatedSet);
	System.out.println(" ea: " + earlySet);
        System.out.println(" db: " + delaySet);
        System.out.println(" la: " + latestSet);
      }
    }
    */

    { /* insert the computations */
      Iterator unitIt = unitChain.snapshotIterator();
      while (unitIt.hasNext()) {
        Unit currentUnit = (Unit)unitIt.next();
        FlowSet latestSet = (FlowSet)latest.getFlowBefore(currentUnit);
        FlowSet notIsolatedSet =
          (FlowSet)notIsolated.getFlowAfter(currentUnit);
        FlowSet insertHere = (FlowSet)latestSet.clone();
        insertHere.intersection(notIsolatedSet, insertHere);
        Iterator insertIt = insertHere.iterator();
        while (insertIt.hasNext()) {
          EquivalentValue equiVal = (EquivalentValue)insertIt.next();
          /* get the unic helper-name for this expression */
          Local helper = (Local)expToHelper.get(equiVal);
          if (helper == null) {
            helper = localCreation.newLocal(equiVal.getType());
            expToHelper.put(equiVal, helper);
          }

          /* insert a new Assignment-stmt before the currentUnit */
          Value insertValue = Jimple.cloneIfNecessary(equiVal.getValue());
          Unit firstComp = Jimple.v().newAssignStmt(helper, insertValue);
          unitChain.insertBefore(firstComp, currentUnit);          
	  //	  System.out.print("x");
        }
      }
    }

    { /* replace old computations by the helper-vars */
      Iterator unitIt = unitChain.iterator();
      while (unitIt.hasNext()) {
        Unit currentUnit = (Unit)unitIt.next();
        EquivalentValue rhs = (EquivalentValue)unitToEquivRhs.get(currentUnit);
        if (rhs != null) {
          FlowSet latestSet = (FlowSet)latest.getFlowBefore(currentUnit);
          FlowSet notIsolatedSet =
            (FlowSet)notIsolated.getFlowAfter(currentUnit);
          if (!latestSet.contains(rhs) && notIsolatedSet.contains(rhs)) {
            Local helper = (Local)expToHelper.get(rhs);

	    try {
	      ((AssignStmt)currentUnit).setRightOp(helper);
	    } catch (RuntimeException e){
	      System.err.println("Error on "+b.getMethod().getName());
	      System.err.println(currentUnit.toString());

	      System.err.println(latestSet);
	      
	      System.err.println(notIsolatedSet);

	      throw e;
	    }
           
	    // System.out.print(".");
          }
        }
      }
    }
    if(Main.isVerbose)
      System.out.println("[" + b.getMethod().getName() +
                         "]     Lazy Code Motion done.");
  }
}
