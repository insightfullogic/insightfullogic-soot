/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.toolkits.pointer;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.*;
import java.util.*;
import soot.util.*;
import soot.jimple.spark.*;

/** Generates side-effect information from a PointsToAnalysis. */
public class SideEffectAnalysis {
    PointsToAnalysis pa;
    CallGraph cg;
    Map methodToNTReadSet = new HashMap();
    Map methodToNTWriteSet = new HashMap();
    int rwsetcount = 0;
    TransitiveTargets tt;

    public void findNTRWSets( SootMethod method ) {
	if( methodToNTReadSet.containsKey( method )
	    && methodToNTWriteSet.containsKey( method ) ) return;
	
	MethodRWSet read = new MethodRWSet();
	MethodRWSet write = new MethodRWSet();
	for( Iterator sIt = method.retrieveActiveBody().getUnits().iterator(); sIt.hasNext(); ) {
	    final Stmt s = (Stmt) sIt.next();
            read.union( ntReadSet( method, s ) );
            write.union( ntWriteSet( method, s ) );
	}
	methodToNTReadSet.put( method, read );
	methodToNTWriteSet.put( method, write );
	SootClass c = method.getDeclaringClass();
	if( !c.isApplicationClass() ) {
	    method.releaseActiveBody();
	}
    }

    public RWSet nonTransitiveReadSet( SootMethod method ) {
	findNTRWSets( method );
	return (RWSet) methodToNTReadSet.get( method );
    }

    public RWSet nonTransitiveWriteSet( SootMethod method ) {
	findNTRWSets( method );
	return (RWSet) methodToNTWriteSet.get( method );
    }

    public SideEffectAnalysis( PointsToAnalysis pa, CallGraph cg ) {
	this.pa = pa;
	this.cg = cg;
        this.tt = new TransitiveTargets( cg );
    }

    private RWSet ntReadSet( SootMethod method, Stmt stmt ) {
	if( stmt instanceof AssignStmt ) {
	    AssignStmt a = (AssignStmt) stmt;
	    Value r = a.getRightOp();
	    return addValue( r, method, stmt );
	}
        return null;
    }
    public RWSet readSet( SootMethod method, Stmt stmt ) {
	RWSet ret = null;
        Iterator targets = tt.iterator( stmt );
        while( targets.hasNext() ) {
            SootMethod target = (SootMethod) targets.next();
            if( target.isNative() ) {
                if( ret == null ) ret = new SiteRWSet();
                ret.setCallsNative();
            } else if( target.isConcrete() ) {
                if( ret == null ) ret = new SiteRWSet();
                ret.union( nonTransitiveReadSet( target ) );
            }
        }
        if( ret == null ) return ntReadSet( method, stmt );
        ret.union( ntReadSet( method, stmt ) );
        return ret;
    }

    private RWSet ntWriteSet( SootMethod method, Stmt stmt ) {
        if( stmt instanceof AssignStmt ) {
	    AssignStmt a = (AssignStmt) stmt;
	    Value l = a.getLeftOp();
	    return addValue( l, method, stmt );
	}
        return null;
    }
    public RWSet writeSet( SootMethod method, Stmt stmt ) {
	RWSet ret = null;
        Iterator targets = tt.iterator( stmt );
        while( targets.hasNext() ) {
            SootMethod target = (SootMethod) targets.next();
            if( target.isNative() ) {
                if( ret == null ) ret = new SiteRWSet();
                ret.setCallsNative();
            } else if( target.isConcrete() ) {
                if( ret == null ) ret = new SiteRWSet();
                ret.union( nonTransitiveWriteSet( target ) );
            }
	}
        if( ret == null ) return ntWriteSet( method, stmt );
        ret.union( ntWriteSet( method, stmt ) );
	return ret;
    }

    protected RWSet addValue( Value v, SootMethod m, Stmt s ) {
	RWSet ret = null;
	if( v instanceof InstanceFieldRef ) {
	    InstanceFieldRef ifr = (InstanceFieldRef) v;
	    PointsToSet base = pa.reachingObjects( (Local) ifr.getBase() );
	    ret = new StmtRWSet();
	    ret.addFieldRef( base, ifr.getField() );
	} else if( v instanceof StaticFieldRef ) {
	    StaticFieldRef sfr = (StaticFieldRef) v;
	    ret = new StmtRWSet();
	    ret.addGlobal( sfr.getField() );
	} else if( v instanceof ArrayRef ) {
	    ArrayRef ar = (ArrayRef) v;
	    PointsToSet base = pa.reachingObjects( (Local) ar.getBase() );
	    ret = new StmtRWSet();
	    ret.addFieldRef( base, PointsToAnalysis.ARRAY_ELEMENTS_NODE );
	}
	return ret;
    }

    public String toString() {
        return "SideEffectAnalysis: PA="+pa+" CG="+cg;
    }
}

