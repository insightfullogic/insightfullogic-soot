package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import java.util.*;

class WorklistScheduler extends Scheduler {
    int changed;
    HashSet worklist;
    HashSet previousWorklist;
    protected void wroteTo( VarNode v ) {
	worklist.add( v );
	changed++;
    }
    protected void wroteTo() {
	changed++;
    }
    protected void compute() {
	int iterNum = 0;
	worklist = new HashSet(4);
	computeAllocs();
	do {
	    System.out.println( "Iteration: "+(++iterNum) );
	    changed = 0;
	    previousWorklist = worklist;
	    worklist = new HashSet(4);
	    {
		for( Iterator fromIt = previousWorklist.iterator();
			fromIt.hasNext(); ) {
		    VarNode from = (VarNode) fromIt.next();
		    for( Iterator toIt = b.getSimple().get( from ).iterator();
			    toIt.hasNext(); ) {
			VarNode to = (VarNode) toIt.next();

			h.handleSimple( from, to );
		    }
		}
	    }
	    {
		for( Iterator fromIt = b.getLoads().keySet().iterator();
			fromIt.hasNext(); ) {
		    FieldRefNode from = (FieldRefNode) fromIt.next();
		    for( Iterator toIt = b.getLoads().get( from ).iterator();
			    toIt.hasNext(); ) {
			VarNode to = (VarNode) toIt.next();

			h.handleLoad( from, to );
		    }
		}
	    }
	    {
		for( Iterator fromIt = b.getStores().keySet().iterator();
			fromIt.hasNext(); ) {
		    VarNode from = (VarNode) fromIt.next();
		    for( Iterator toIt = b.getStores().get( from ).iterator();
			    toIt.hasNext(); ) {
			FieldRefNode to = (FieldRefNode) toIt.next();

			h.handleStore( from, to );
		    }
		}
	    }
	    System.out.println( "Changes: "+changed );
	    b.nextIter();
	} while( changed > 0 );
    }
    WorklistScheduler( Handler h ) {
	super( h );
    }
}

