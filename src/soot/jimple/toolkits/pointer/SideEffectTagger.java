package soot.jimple.toolkits.pointer;
import soot.tagkit.*;
import soot.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.*;
import java.io.*;

public class SideEffectTagger extends BodyTransformer
{ 
    public SideEffectTagger( Singletons.Global g ) {}
    public static SideEffectTagger v() { return G.v().SideEffectTagger(); }

    public int numRWs = 0;
    public int numWRs = 0;
    public int numRRs = 0;
    public int numWWs = 0;
    public int numNatives = 0;
    public Date startTime = null;
    boolean optionNaive = false;
    private CallGraph cg;
    
    protected class UniqueRWSets {
	protected ArrayList l = new ArrayList();
	RWSet getUnique( RWSet s ) {
	    if( s == null ) return s;
	    for( Iterator retIt = l.iterator(); retIt.hasNext(); ) {
	        final RWSet ret = (RWSet) retIt.next();
		if( ret.isEquivTo( s ) ) return ret;
	    }
	    l.add( s );
	    return s;
	}
	Iterator iterator() {
	    return l.iterator();
	}
	short indexOf( RWSet s ) {
	    short i = 0;
	    for( Iterator retIt = l.iterator(); retIt.hasNext(); ) {
	        final RWSet ret = (RWSet) retIt.next();
		if( ret.isEquivTo( s ) ) return i;
		i++;
	    }
	    return -1;
	}
    }

    protected void initializationStuff( String phaseName ) {
        G.v().Union_factory = new UnionFactory() {
	    //ReallyCheapRasUnion ru =  new ReallyCheapRasUnion();
	    //public Union newUnion() { return new RasUnion(); }
	    public Union newUnion() { return new MemoryEfficientRasUnion(); }
	};

	if( startTime == null ) {
	    startTime = new Date();
	}
        cg = Scene.v().getCallGraph();
    }
    protected Object keyFor( Stmt s ) {
	if( s.containsInvokeExpr() ) {
	    if( optionNaive ) throw new RuntimeException( "shouldn't get here" );
            Iterator it = cg.targetsOf( s );
	    if( !it.hasNext() ) {
		return Collections.EMPTY_LIST;
	    }
            ArrayList ret = new ArrayList();
            while( it.hasNext() ) {
                ret.add( it.next() );
            }
            return ret;
	} else {
	    return s;
	}
    }
    protected void internalTransform(Body body, String phaseName, Map options)
    {
	initializationStuff( phaseName );
	SideEffectAnalysis sea = Scene.v().getSideEffectAnalysis();
	optionNaive = PhaseOptions.getBoolean( options, "naive" );
	if( !optionNaive ) {
	    sea.findNTRWSets( body.getMethod() );
	}
	HashMap stmtToReadSet = new HashMap();
	HashMap stmtToWriteSet = new HashMap();
	UniqueRWSets sets = new UniqueRWSets();
	boolean justDoTotallyConservativeThing = 
	    body.getMethod().getName().equals( "<clinit>" );
	for( Iterator stmtIt = body.getUnits().iterator(); stmtIt.hasNext(); ) {
	    final Stmt stmt = (Stmt) stmtIt.next();
	    if( justDoTotallyConservativeThing 
	    || ( optionNaive && stmt.containsInvokeExpr() ) ) {
		stmtToReadSet.put( stmt, sets.getUnique( new FullRWSet() ) );
		stmtToWriteSet.put( stmt, sets.getUnique( new FullRWSet() ) );
		continue;
	    }
	    Object key = keyFor( stmt );
	    if( !stmtToReadSet.containsKey( key ) ) {
		stmtToReadSet.put( key,
		    sets.getUnique( sea.readSet( body.getMethod(), stmt ) ) );
		stmtToWriteSet.put( key,
		    sets.getUnique( sea.writeSet( body.getMethod(), stmt ) ) );
	    }
	}
	DependenceGraph graph = new DependenceGraph();
	for( Iterator outerIt = sets.iterator(); outerIt.hasNext(); ) {
	    final RWSet outer = (RWSet) outerIt.next();

	    for( Iterator innerIt = sets.iterator(); innerIt.hasNext(); ) {

	        final RWSet inner = (RWSet) innerIt.next();
		if( inner == outer ) break;
		if( outer.hasNonEmptyIntersection( inner ) ) {
                    //G.v().out.println( "inner set is: "+inner );
                    //G.v().out.println( "outer set is: "+outer );
		    graph.addEdge( sets.indexOf( outer ), sets.indexOf( inner ) );
		}
	    }
	}
        body.getMethod().addTag( graph );
	for( Iterator stmtIt = body.getUnits().iterator(); stmtIt.hasNext(); ) {
	    final Stmt stmt = (Stmt) stmtIt.next();
	    Object key;
	    if( optionNaive && stmt.containsInvokeExpr() ) {
		key = stmt;
	    } else {
		key = keyFor( stmt );
	    }
	    RWSet read = (RWSet) stmtToReadSet.get( key );
	    RWSet write = (RWSet) stmtToWriteSet.get( key );
	    if( read != null || write != null ) {
		DependenceTag tag = new DependenceTag();
		if( read != null && read.getCallsNative() ) {
		    tag.setCallsNative();
		    numNatives++;
		} else if( write != null && write.getCallsNative() ) {
		    tag.setCallsNative();
		    numNatives++;
		}
		tag.setRead( sets.indexOf( read ) );
		tag.setWrite( sets.indexOf( write ) );
                stmt.addTag( tag );

		// The loop below is just for calculating stats.
                /*
		if( !justDoTotallyConservativeThing ) {
		    for( Iterator innerIt = body.getUnits().iterator(); innerIt.hasNext(); ) {
		        final Stmt inner = (Stmt) innerIt.next();
			Object ikey;
			if( optionNaive && inner.containsInvokeExpr() ) {
			    ikey = inner;
			} else {
			    ikey = keyFor( inner );
			}
			RWSet innerRead = (RWSet) stmtToReadSet.get( ikey );
			RWSet innerWrite = (RWSet) stmtToWriteSet.get( ikey );
			if( graph.areAdjacent( sets.indexOf( read ),
				    sets.indexOf( innerWrite ) ) ) numRWs++;
			if( graph.areAdjacent( sets.indexOf( write ),
				    sets.indexOf( innerRead ) ) ) numWRs++;
			if( inner == stmt ) continue;
			if( graph.areAdjacent( sets.indexOf( write ),
				    sets.indexOf( innerWrite ) ) ) numWWs++;
			if( graph.areAdjacent( sets.indexOf( read ),
				    sets.indexOf( innerRead ) ) ) numRRs++;
		    }
		}
                */
	    }
	}
    }
}


