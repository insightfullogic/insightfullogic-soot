package soot.jimple.toolkits.transaction;

import java.util.*;

import soot.*;
import soot.util.Chain;
import soot.jimple.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.spark.pag.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.toolkits.mhp.*;
import soot.toolkits.mhp.pegcallgraph.*;
import soot.toolkits.mhp.findobject.*;
import soot.toolkits.mhp.stmt.*;
import soot.tagkit.LineNumberTag;


public class TransactionTransformer extends SceneTransformer
{
    public TransactionTransformer(Singletons.Global g){}
    public static TransactionTransformer v() 
	{ 
		return G.v().soot_jimple_toolkits_transaction_TransactionTransformer();
	}
	
	boolean optionPrintGraph = false;
	boolean optionPrintTable = false;
	boolean optionPrintDebug = false;
	
	UnsynchronizedMhpAnalysis mhp;

    protected void internalTransform(String phaseName, Map options)
	{
    	Map methodToFlowSet = new HashMap();

		optionPrintGraph = PhaseOptions.getBoolean( options, "print-graph" );
		optionPrintTable = PhaseOptions.getBoolean( options, "print-table" );
		optionPrintDebug = PhaseOptions.getBoolean( options, "print-debug" );
		
    	// For all methods, run the intraprocedural analysis (transaction finder)
    	Iterator runAnalysisClassesIt = Scene.v().getApplicationClasses().iterator();
    	while (runAnalysisClassesIt.hasNext()) 
    	{
    	    SootClass appClass = (SootClass) runAnalysisClassesIt.next();
    	    Iterator methodsIt = appClass.getMethods().iterator();
    	    while (methodsIt.hasNext())
    	    {
    	    	SootMethod method = (SootMethod) methodsIt.next();
				if(method.isConcrete())
				{
	    	    	Body b = method.retrieveActiveBody();
    		    	
    	    		// run the intraprocedural analysis
    				TransactionAnalysis ta = new TransactionAnalysis(new ExceptionalUnitGraph(b), b, optionPrintDebug);
    				Chain units = b.getUnits();
    				Unit lastUnit = (Unit) units.getLast();
    				FlowSet fs = (FlowSet) ta.getFlowBefore(lastUnit);
    			
    				// add the results to the list of results
    				methodToFlowSet.put(method, fs);
				}
    	    }
    	}
    	
    	// Create a composite list of all transactions
    	List AllTransactions = new Vector();
    	Collection AllFlowSets = methodToFlowSet.values();
    	Iterator fsIt = AllFlowSets.iterator();
    	while(fsIt.hasNext())
    	{
    		FlowSet fs = (FlowSet) fsIt.next();
    		List fList = fs.toList();
    		for(int i = 0; i < fList.size(); i++)
    			AllTransactions.add(((TransactionFlowPair) fList.get(i)).tn);
    	}

		// Assign Names To Transactions
		assignNamesToTransactions(AllTransactions);
    	
    	// *** Complete the Read/Write Sets ***
    	// Use TransactionAwareSideEffectAnalysis
    	// As is, this breaks atomicity: inner transaction's effects are visible
    	// as soon as the inner transaction completes. ("open nesting")
    	// As long as we're using "synchronized" keyword, this behavior is implied, so OK!
    	TransactionAwareSideEffectAnalysis tasea = 
    		new TransactionAwareSideEffectAnalysis(
    				Scene.v().getPointsToAnalysis(), 
    				Scene.v().getCallGraph(), AllTransactions);
    	Iterator tnIt = AllTransactions.iterator();
    	while(tnIt.hasNext())
    	{
    		Transaction tn = (Transaction) tnIt.next();
			Body b = tn.method.retrieveActiveBody();
			UnitGraph g = new ExceptionalUnitGraph(b);
			LocalDefs sld = new SmartLocalDefs(g, new SimpleLiveLocals(g));
    		Iterator invokeIt = tn.invokes.iterator();
    		while(invokeIt.hasNext())
    		{
    			Stmt stmt = (Stmt) invokeIt.next();
    			
    			RWSet stmtRead = tasea.transactionalReadSet(tn.method, stmt, sld);
    			if(stmtRead != null)
	    			tn.read.union(stmtRead);
    			
    			RWSet stmtWrite = tasea.transactionalWriteSet(tn.method, stmt, sld);
				if(stmtWrite != null)
					tn.write.union(stmtWrite);
			}
    	}
    	
    	// *** Find Stray Reads/Writes *** (DISABLED)
    	
    	// add external data races as one-line transactions
    	// note that finding them isn't that hard (though it is time consuming)
    	// however, actually adding entermonitor & exitmonitor statements is rather complex... must deal with exception handling!
    	// For all methods, run the intraprocedural analysis (transaction finder)
    	// Note that these will only be transformed if they are either added to
    	// methodToFlowSet or if a loop and new body transformer are used for methodToStrayRWSet
/*    	Map methodToStrayRWSet = new HashMap();
    	Iterator runRWFinderClassesIt = Scene.v().getApplicationClasses().iterator();
    	while (runRWFinderClassesIt.hasNext()) 
    	{
    	    SootClass appClass = (SootClass) runRWFinderClassesIt.next();
    	    Iterator methodsIt = appClass.getMethods().iterator();
    	    while (methodsIt.hasNext())
    	    {
    	    	SootMethod method = (SootMethod) methodsIt.next();
//    	    	G.v().out.println(method.toString());
    	    	Body b = method.retrieveActiveBody();
    	    	
    	    	// run the interprocedural analysis
    			PTFindStrayRW ptfrw = new PTFindStrayRW(new ExceptionalUnitGraph(b), b, AllTransactions);
    			Chain units = b.getUnits();
    			Unit firstUnit = (Unit) units.iterator().next();
    			FlowSet fs = (FlowSet) ptfrw.getFlowBefore(firstUnit);
    			
    			// add the results to the list of results
    			methodToStrayRWSet.put(method, fs);
    	    }
    	}
//*/    	

		// *** Build May Happen In Parallel Info ***
		mhp = new UnsynchronizedMhpAnalysis();

    	// *** Calculate locking scheme ***
    	// Search for data dependencies between transactions, and split them into disjoint sets
    	int nextGroup = 1;
    	Iterator tnIt1 = AllTransactions.iterator();
    	while(tnIt1.hasNext())
    	{
    		Transaction tn1 = (Transaction) tnIt1.next();
    		
    		// if this transaction has somehow already been marked for deletion
    		if(tn1.setNumber == -1)
    			continue;
    		
    		// if this transaction is empty
    		if(tn1.read.size() == 0 && tn1.write.size() == 0)
    		{
    			// this transaction has no effect except on locals... we don't need it!
    			tn1.setNumber = -1; // AKA delete the transactional region (but don't really so long as we are using
    								// the synchronized keyword in our language... because java guarantees memory
    								// barriers at certain points in synchronized blocks)
    		}
    		else
    		{
	        	Iterator tnIt2 = AllTransactions.iterator();
	    		while(tnIt2.hasNext())
	    		{
	    			Transaction tn2 = (Transaction) tnIt2.next();
	    			
	    			// check if it's us!
//	    			if(tn1 == tn2)
//	    				continue;
	    			
	    			// check if this transactional region is going to be deleted
	    			if(tn2.setNumber == -1)
	    				continue;

	    			// check if they're already marked as having an interference
	    			// NOTE: this results in a sound grouping, but a badly 
	    			//       incomplete dependency graph. If the dependency 
	    			//       graph is to be analyzed, we cannot do this
//	    			if(tn1.setNumber > 0 && tn1.setNumber == tn2.setNumber)
//	    				continue;
	    			
	    			// check if these two transactions can't ever be in parallel
	    			if(!mayHappenInParallel(tn1, tn2))
	    				continue;

	    			// check for RW or WW data dependencies.
	    			if(tn1.write.hasNonEmptyIntersection(tn2.write) ||
	    					tn1.write.hasNonEmptyIntersection(tn2.read) ||
	    					tn1.read.hasNonEmptyIntersection(tn2.write))
	    			{
	    				// Determine the size of the intersection for GraphViz output
	    				CodeBlockRWSet rw = tn1.write.intersection(tn2.write);
	    				rw.union(tn1.write.intersection(tn2.read));
	    				rw.union(tn1.read.intersection(tn2.write));
	    				int size = rw.size();
	    				
	    				// Record this 
	    				tn1.edges.add(new DataDependency(tn2, size, rw));
//	    				tn2.edges.add(new DataDependency(tn1, size, rw)); // will be added in opposite direction later
	    				
	    				// if tn1 already is in a group
	    				if(tn1.setNumber > 0)
	    				{
	    					// if tn2 is NOT already in a group
	    					if(tn2.setNumber == 0)
	    					{
	    						tn2.setNumber = tn1.setNumber;
	    					}
	    					// if tn2 is already in a group
	    					else if(tn2.setNumber > 0)
	    					{
	    						if(tn1 != tn2) // if they are equal, then they are already in the same group!
	    						{
			    					int setToDelete = tn2.setNumber;
			    					int setToExpand = tn1.setNumber;
				    	        	Iterator tnIt3 = AllTransactions.iterator();
				    	    		while(tnIt3.hasNext())
				    	    		{
				    	    			Transaction tn3 = (Transaction) tnIt3.next();
				    	    			if(tn3.setNumber == setToDelete)
				    	    			{
				    	    				tn3.setNumber = setToExpand;
				    	    			}
				    	    		}
				    	    	}
	    					}
	    				}
	    				// if tn1 is NOT already in a group
	    				else if(tn1.setNumber == 0)
	    				{
	    					// if tn2 is NOT already in a group
	    					if(tn2.setNumber == 0)
 		    				{
		    					tn1.setNumber = tn2.setNumber = nextGroup;
		    					nextGroup++;
		    				}
	    					// if tn2 is already in a group
	    					else if(tn2.setNumber > 0)
	    					{
	    						tn1.setNumber = tn2.setNumber;
	    					}
	    				}
	    			}
	    		}
	    		// If, after comparing to all other transactions, we have no group:
	    		if(tn1.setNumber == 0)
	    		{
//	    			if(mayHappenInParallel(tn1, tn1))
//	    			{
//	    				tn1.setNumber = nextGroup;
//	    				nextGroup++;
//	    			}
//	    			else
//	    			{
	    				tn1.setNumber = -1; // delete transactional region
//	    			}
	    		}	    			
    		}
    	}
    	
    	// Create an array of RW sets, one for each transaction group
    	// In each set, put the union of all RW Dependencies in the group
    	RWSet rws[] = new CodeBlockRWSet[nextGroup - 1];
    	for(int i = 0; i < nextGroup - 1; i++)
    		rws[i] = new CodeBlockRWSet();
    	Iterator tnIt8 = AllTransactions.iterator();
    	while(tnIt8.hasNext())
    	{
    		Transaction tn = (Transaction) tnIt8.next();
    		Iterator EdgeIt = tn.edges.iterator();
    		while(EdgeIt.hasNext())
    		{
    			DataDependency dd = (DataDependency) EdgeIt.next();
	    		rws[tn.setNumber - 1].union(dd.rw);
    		}
    	}

		// For each transaction group, find out if all RW Dependencies are POSSIBLY fields of the same object
		
		
		
		// then check if they are DEFINITELY fields of the same object
		// LIF (Local Info Flow)
		
		
		
		// If, for all transactions in a group, all RW dependencies are definitely fields of the same object,
		// then that object should be used for synchronization
		
		

		// Print topological graph in format of graphviz package
		if(optionPrintGraph)
		{
			G.v().out.println("[transaction-graph] strict graph transactions {\n[transaction-graph] start=1;");		
			Iterator tnIt6 = AllTransactions.iterator();
			while(tnIt6.hasNext())
			{
				Transaction tn = (Transaction) tnIt6.next();
				Iterator tnedgeit = tn.edges.iterator();
				G.v().out.println("[transaction-graph] " + tn.name + " [name=\"" + tn.method.toString() + "\"];");
				while(tnedgeit.hasNext())
				{
					DataDependency edge = (DataDependency) tnedgeit.next();
					Transaction tnedge = edge.other;
					G.v().out.println("[transaction-graph] " + tn.name + " -- " + tnedge.name + " [color=" + (edge.size > 5 ? (edge.size > 50 ? "black" : "blue") : "black") + " style=" + (edge.size > 50 ? "dashed" : "solid") + " exactsize=" + edge.size + "];");
				}			
			}
			G.v().out.println("[transaction-graph] }");
		}

		// Print table of transaction information
		if(optionPrintTable)
		{
			G.v().out.println("[transaction-table] ");
			Iterator tnIt7 = AllTransactions.iterator();
			while(tnIt7.hasNext())
			{
				Transaction tn = (Transaction) tnIt7.next();
				G.v().out.println("[transaction-table] Transaction " + tn.name);
				G.v().out.println("[transaction-table] Where: " + tn.method.getDeclaringClass().toString() + ":" + tn.method.toString() + ":  ");
				G.v().out.println("[transaction-table] Prep : " + (tn.prepStmt == null ? "none" : tn.prepStmt.toString()));
				G.v().out.println("[transaction-table] Begin: " + tn.begin.toString());
				G.v().out.print("[transaction-table] End  : " + tn.ends.toString() + " \n");
				G.v().out.println("[transaction-table] Size : " + tn.units.size());
				if(tn.read.size() < 100)
					G.v().out.print("[transaction-table] Read : " + tn.read.size() + "\n[transaction-table] " + 
						tn.read.toString().replaceAll("\\[", "     : [").replaceAll("\n", "\n[transaction-table] ") + 
						(tn.read.size() == 0 ? "\n[transaction-table] " : ""));
				else
					G.v().out.print("[transaction-table] Read : " + tn.read.size() + "  \n[transaction-table] ");
				if(tn.write.size() < 100)
					G.v().out.print("Write: " + tn.write.size() + "\n[transaction-table] " + 
						tn.write.toString().replaceAll("\\[", "     : [").replaceAll("\n", "\n[transaction-table] ") + 
						(tn.write.size() == 0 ? "\n[transaction-table] " : "")); // label provided by previous print statement
				else
					G.v().out.print("Write: " + tn.write.size() + "\n[transaction-table] "); // label provided by previous print statement
				G.v().out.print("Edges: (" + tn.edges.size() + ") "); // label provided by previous print statement
				Iterator tnedgeit = tn.edges.iterator();
				while(tnedgeit.hasNext())
					G.v().out.print(((DataDependency)tnedgeit.next()).other.name + " ");
				G.v().out.println("\n[transaction-table] Group: " + tn.setNumber + "\n[transaction-table] ");
			}
			
			G.v().out.print("[transaction-table] Group Summaries\n[transaction-table] ");
			for(int i = 0; i < nextGroup - 1; i++)
    		{
    			G.v().out.print("Group" + (i + 1) + "\n[transaction-table] " + 
    				rws[i].toString().replaceAll("\\[", "     : [").replaceAll("\n", "\n[transaction-table] ") + 
					(rws[i].size() == 0 ? "\n[transaction-table] " : ""));
	    	}
			G.v().out.println("");
		}

    	// For all methods, run the transformer (Pessimistic Transaction Tranformation)
    	// BEGIN AWFUL HACK
		TransactionBodyTransformer.addedGlobalLockObj = new boolean[nextGroup];
		for(int i = 0; i < nextGroup; i++)
			TransactionBodyTransformer.addedGlobalLockObj[i] = false;
		// END AWFUL HACK
    	Iterator doTransformClassesIt = Scene.v().getApplicationClasses().iterator();
    	while (doTransformClassesIt.hasNext()) 
    	{
    	    SootClass appClass = (SootClass) doTransformClassesIt.next();
    	    Iterator methodsIt = appClass.getMethods().iterator();
    	    while (methodsIt.hasNext())
    	    {
    	    	SootMethod method = (SootMethod) methodsIt.next();
				if(method.isConcrete())
				{
	    	    	Body b = method.getActiveBody();
    	    	
    		    	FlowSet fs = (FlowSet) methodToFlowSet.get(method);
    	    	
   	    			TransactionBodyTransformer.v().setDetails(fs, nextGroup);
   	    			TransactionBodyTransformer.v().internalTransform(b,phaseName, options); 
				}
    	    }
    	}
	}
    
    public boolean mayHappenInParallel(Transaction tn1, Transaction tn2)
    {
    	return mhp.mayHappenInParallel(tn1.method, tn2.method);
    }
    
    public void assignNamesToTransactions(List AllTransactions)
    {
       	// Give each method a unique, deterministic identifier (based on an alphabetical sort by method name)
    	List methodNamesTemp = new ArrayList();
    	Iterator tnIt5 = AllTransactions.iterator();
    	while (tnIt5.hasNext()) 
    	{
    	    Transaction tn1 = (Transaction) tnIt5.next();
    	    String mname = tn1.method.getSignature(); //tn1.method.getSignature() + "." + tn1.method.getName();
    	    if(!methodNamesTemp.contains(mname))
    	    	methodNamesTemp.add(mname);
		}
		String methodNames[] = new String[1];
		methodNames = (String []) methodNamesTemp.toArray(methodNames);
		Arrays.sort(methodNames);

		// Identify each transaction with its method's identifier
		// this matrix is <# method names> wide and <max txns possible in one method> + 1 tall
		// might be possible to calculate a smaller size for this matrix
		int identMatrix[][] = new int[methodNames.length][Transaction.nextIDNum - methodNames.length + 2];
		for(int i = 0; i < methodNames.length; i++)
		{
			for(int j = 0; j < Transaction.nextIDNum - methodNames.length + 1; j++)
			{
				if(j != 0)
					identMatrix[i][j] = 50000;
				else
					identMatrix[i][j] = 0;
			}
		}
    	Iterator tnIt0 = AllTransactions.iterator();
    	while(tnIt0.hasNext())
    	{
    		Transaction tn1 = (Transaction) tnIt0.next();
			int methodNum = Arrays.binarySearch(methodNames, tn1.method.getSignature());
			identMatrix[methodNum][0]++;
			identMatrix[methodNum][identMatrix[methodNum][0]] = tn1.IDNum;
    	}
    	
    	for(int j = 0; j < methodNames.length; j++)
    	{
    		identMatrix[j][0] = 0; // set the counter to 0 so it sorts out (into slot 0).
    		Arrays.sort(identMatrix[j]); // sort this subarray
		}
		
    	Iterator tnIt4 = AllTransactions.iterator();
    	while(tnIt4.hasNext())
    	{
    		Transaction tn1 = (Transaction) tnIt4.next();
			int methodNum = Arrays.binarySearch(methodNames, tn1.method.getSignature());
			int tnNum = Arrays.binarySearch(identMatrix[methodNum], tn1.IDNum) - 1;
    		tn1.name = "m" + (methodNum < 10? "00" : (methodNum < 100? "0" : "")) + methodNum + "n" + (tnNum < 10? "0" : "") + tnNum;
    	}
	}	

}

