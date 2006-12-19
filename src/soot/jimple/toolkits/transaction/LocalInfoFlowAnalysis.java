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

// LocalInfoFlowAnalysis written by Richard L. Halpert, 2006-12-04

public class LocalInfoFlowAnalysis extends ForwardFlowAnalysis
{
	// Provided by client
	Map stmtToLocal;
	Set useStmts;
	Collection useLocals;
	List boundaryStmts;

	// Calculated by flow analysis
	List redefStmts;
	Map firstUseToAliasSet;
	
	EqualLocalsAnalysis el;
	
	public LocalInfoFlowAnalysis(UnitGraph g)
	{
		super(g);
		
		useStmts = null;
		useLocals = null;
		boundaryStmts = null;

		redefStmts = null;
		firstUseToAliasSet = null;
		
		// analysis is done on-demand, not now

		this.el= new EqualLocalsAnalysis(g); // also on-demand
	}

	public boolean mustPointToSameObj(Stmt firstStmt, Local firstLocal, Stmt secondStmt, Local secondLocal)
	{
		Map stmtToLocal = new HashMap();
		stmtToLocal.put(firstStmt, firstLocal);
		stmtToLocal.put(secondStmt, secondLocal);
		return mustPointToSameObj(stmtToLocal, new ArrayList());
	}
	
	public boolean mustPointToSameObj(Stmt firstStmt, Local firstLocal, Stmt secondStmt, Local secondLocal, List boundaryStmts)
	{
		Map stmtToLocal = new HashMap();
		stmtToLocal.put(firstStmt, firstLocal);
		stmtToLocal.put(secondStmt, secondLocal);
		return mustPointToSameObj(stmtToLocal, boundaryStmts);
	}
	
	public boolean mustPointToSameObj(Map stmtToLocal)
	{
		return mustPointToSameObj(stmtToLocal, new ArrayList());
	}
	
	public boolean mustPointToSameObj(Map stmtToLocal, List boundaryStmts)
	{// You may optionally specify start and end statements... for if you're interested only in a certain part of the method
		this.stmtToLocal = stmtToLocal;
		this.useStmts = stmtToLocal.keySet();
		this.useLocals = stmtToLocal.values();
		this.boundaryStmts = boundaryStmts;
		this.redefStmts = new ArrayList();
		this.firstUseToAliasSet = new HashMap();

//		G.v().out.println("Checking for Locals " + useLocals + " in these statements: " + useStmts);

		doAnalysis();

		// If any redefinition reaches any use statement, return false
		Iterator useIt = useStmts.iterator();
		while(useIt.hasNext())
		{
			Unit u = (Unit) useIt.next();
			Stmt s = (Stmt) u;
			FlowSet fs = (FlowSet) getFlowBefore(u);
			Iterator redefIt = redefStmts.iterator();
			while(redefIt.hasNext())
			{
				if(fs.contains((Stmt) redefIt.next()))
				{
//					G.v().out.print("LIF = false ");
					return false;
				}
			}
			List aliases = null;
			Iterator fsIt = fs.iterator();
			while(fsIt.hasNext())
			{
				Object o = fsIt.next();
				if( o instanceof List )
					aliases = (List) o;
			}
			if( aliases != null && !aliases.contains(new EquivalentValue((Value) stmtToLocal.get(u))) )
			{
//				G.v().out.print("LIF = false ");
				return false;
			}
		}
//		G.v().out.print("LIF = true ");
		return true;
	}
	
	public Map getFirstUseToAliasSet()
	{
		return firstUseToAliasSet;
	}

	protected void merge(Object in1, Object in2, Object out)
	{
		FlowSet inSet1 = (FlowSet) in1;
		FlowSet inSet2 = (FlowSet) in2;
		FlowSet outSet = (FlowSet) out;
		
		
		inSet1.union(inSet2, outSet);
		List aliases1 = null;
		List aliases2 = null;
		Iterator outIt = outSet.iterator();
		while(outIt.hasNext())
		{
			Object o = outIt.next();
			if( o instanceof List )
			{
				if(aliases1 == null)
					aliases1 = (List) o;
				else
					aliases2 = (List) o;
			}
		}
		if(aliases1 != null && aliases2 != null)
		{
			outSet.remove(aliases2);
			Iterator aliasIt = aliases1.iterator();
			while(aliasIt.hasNext())
			{
				Object o = aliasIt.next();
				if(!aliases2.contains(o))
					aliasIt.remove();
			}
		}
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
		while(newDefBoxesIt.hasNext())
		{
			newDefs.add( ((ValueBox) newDefBoxesIt.next()).getValue() );
		}
		
		// check if any locals of interest were redefined here
		Iterator useLocalsIt = useLocals.iterator();
		while(useLocalsIt.hasNext())
		{
			Local useLocal = (Local) useLocalsIt.next();
			if( newDefs.contains(useLocal) ) // if a relevant local was (re)def'd here
			{
				Iterator outIt = out.iterator();
				while(outIt.hasNext())
				{
					Object o = outIt.next();
					if( o instanceof Stmt )
					{
						Stmt s = (Stmt) o;
						if(stmtToLocal.get(s) == useLocal) // if a use of this local exists in the flow set
							redefStmts.add(stmt); // mark this as an active redef stmt
					}
				}
			}
		}

		// if this is a redefinition statement, flow it forwards
		if( redefStmts.contains(stmt) )
		{
			out.add(stmt);
		}
		
		// if this is a boundary statement, clear everything but aliases from the flow set
		if( boundaryStmts.contains(stmt) )
		{
			// find the alias entry in the flow set
			List aliases = null;
			Iterator outIt = out.iterator();
			while(outIt.hasNext())
			{
				Object o = outIt.next();
				if( o instanceof List )
					aliases = (List) o;
			}
			
			// clear the flow set, and add aliases back in
			out.clear();
			if(aliases != null)
				out.add(aliases);
		}
		
		// if this is a use statement (of interest), flow it forward
		// if it's the first use statement, get an alias list
		if( useStmts.contains(stmt) )
		{
			if(out.size() == 0)
			{
				// Add a list of aliases to the used value
				Local l = (Local) stmtToLocal.get(stmt);
				List aliasList = el.getCopiesOfAt(l, stmt);
				if(aliasList.size() == 0)
					aliasList.add(l); // covers the case of this or a parameter, where getCopiesOfAt doesn't seem to work right now
				List newAliasList = new ArrayList();
				newAliasList.addAll(aliasList);
				firstUseToAliasSet.put(stmt, newAliasList);
//				G.v().out.println("Aliases of " + l + " at " + stmt + " are " + aliasList);
				out.add(aliasList);
			}
			out.add(stmt);
		}
		
		// update the alias list if this is a definition statement
		if( stmt instanceof DefinitionStmt )
		{
			List aliases = null;
			Iterator outIt = out.iterator();
			while(outIt.hasNext())
			{
				Object o = outIt.next();
				if( o instanceof List )
					aliases = (List) o;
			}
			if( aliases != null )
			{
				if( aliases.contains( new EquivalentValue(((DefinitionStmt)stmt).getRightOp()) ) )
				{
					Iterator newDefsIt = newDefs.iterator();
					while(newDefsIt.hasNext())
						aliases.add( new EquivalentValue( (Value) newDefsIt.next() ) );
				}
				else
				{
					Iterator newDefsIt = newDefs.iterator();
					while(newDefsIt.hasNext())
						aliases.remove( new EquivalentValue( (Value) newDefsIt.next() ) );
				}
			}
		}
			
	}
	
	protected void copy(Object source, Object dest)
	{
		
		FlowSet sourceSet = (FlowSet) source;
		FlowSet destSet   = (FlowSet) dest;
		
		sourceSet.copy(destSet);
		
	}
	
	protected Object entryInitialFlow()
	{
		return new ArraySparseSet();
	}
	
	protected Object newInitialFlow()
	{
		return new ArraySparseSet();
	}	
}

