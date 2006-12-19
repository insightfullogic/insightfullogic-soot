package soot.jimple.toolkits.transaction;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.toolkits.mhp.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.jimple.toolkits.callgraph.*;
import soot.tagkit.*;
import soot.jimple.internal.*;
import soot.jimple.*;
import soot.jimple.spark.sets.*;
import soot.jimple.spark.pag.*;
import soot.toolkits.scalar.*;

// EqualLocalsAnalysis written by Richard L. Halpert, 2006-12-04
// Finds equal/equavalent/aliasing locals to a given local at a given statement, on demand
// The answer provided is occasionally suboptimal (but correct) in the event where
// a _re_definition of the given local causes it to become equal to existing locals.

public class CommonAncestorValueAnalysis extends BackwardFlowAnalysis
{
	Map unitToAliasSet;
	Stmt s;
	
	public CommonAncestorValueAnalysis(UnitGraph g)
	{
		super(g);
		
		unitToAliasSet = null;
		s = null;
		
		// analysis is done on-demand, not now
	}

	/** Returns a list of EquivalentLocals that must always be equal to l at s */
	public List getCommonAncestorValuesOf(Map unitToAliasSet, Stmt s)
	{
		this.unitToAliasSet = unitToAliasSet;
		this.s = s;

		doAnalysis();

		List ancestorList = new ArrayList();
		ancestorList.addAll(((FlowSet) getFlowAfter((Unit) s)).toList());

		return ancestorList;
	}

	protected void merge(Object in1, Object in2, Object out)
	{
		FlowSet inSet1 = (FlowSet) in1;
		FlowSet inSet2 = (FlowSet) in2;
		FlowSet outSet = (FlowSet) out;
		
		inSet1.intersection(inSet2, outSet);
//		inSet1.union(inSet2, outSet);
	}
	
	protected void flowThrough(Object inValue, Object unit,
			Object outValue)
	{
		FlowSet in  = (FlowSet) inValue;
		FlowSet out = (FlowSet) outValue;
		Stmt stmt = (Stmt) unit;
		
		in.copy(out);

		// get list of definitions at this unit
		List newDefs = new ArrayList();
		Iterator newDefBoxesIt = stmt.getDefBoxes().iterator();
		while( newDefBoxesIt.hasNext() )
		{
			newDefs.add( new EquivalentValue( ((ValueBox) newDefBoxesIt.next()).getValue()) );
		}
		
		// If the local of interest was defined in this statement, then we must
		// generate a new list of aliases to it starting here
		if( unitToAliasSet.keySet().contains(stmt) )
		{
			out.clear();
			List aliases = (List) unitToAliasSet.get(stmt);
			Iterator aliasIt = aliases.iterator();
			while(aliasIt.hasNext())
				out.add( aliasIt.next() );
		}
		else if( stmt instanceof DefinitionStmt )
		{
			Iterator newDefsIt = newDefs.iterator();
			while(newDefsIt.hasNext())
				out.remove( newDefsIt.next() );
		}

//		G.v().out.println(stmt + " HAS ALIASES in" + in + " out" + out);
	}
	
	protected void copy(Object source, Object dest)
	{
		
		FlowSet sourceSet = (FlowSet) source;
		FlowSet destSet   = (FlowSet) dest;
		
		sourceSet.copy(destSet);
		
	}
	
	protected Object entryInitialFlow()
	{
		return new ArraySparseSet(); // should be a full set, not an empty one
	}
	
	protected Object newInitialFlow()
	{
		return new ArraySparseSet(); // should be a full set, not an empty one
	}	
}

