/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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

package soot.jimple.spark.solver;
import soot.jimple.spark.*;
import soot.jimple.spark.pag.*;
import soot.jimple.spark.sets.*;
import soot.jimple.spark.internal.*;
import soot.*;
import soot.util.*;
import java.util.*;

/** Propagates points-to sets along pointer assignment graph using a relevant
 * aliases.
 * @author Ondrej Lhotak
 */

public final class PropAlias extends Propagator {
    protected final Set varNodeWorkList = new TreeSet();
    protected Set aliasWorkList;
    protected Set fieldRefWorkList = new HashSet();

    public PropAlias( PAG pag ) { this.pag = pag; }
    /** Actually does the propagation. */
    public final void propagate() {
        new TopoSorter( pag, false ).sort();
        for( Iterator frIt = pag.loadSources().iterator(); frIt.hasNext(); ) {
            final FieldRefNode fr = (FieldRefNode) frIt.next();
            fieldToBase.put( fr.getField(), fr.getBase() );
        }
        for( Iterator frIt = pag.storeInvSources().iterator(); frIt.hasNext(); ) {
            final FieldRefNode fr = (FieldRefNode) frIt.next();
            fieldToBase.put( fr.getField(), fr.getBase() );
        }
	for( Iterator it = pag.allocSources().iterator(); it.hasNext(); ) {
	    handleAllocNode( (AllocNode) it.next() );
	}

        boolean verbose = pag.getOpts().verbose();
	do {
            if( verbose ) {
                System.out.println( "Worklist has "+varNodeWorkList.size()+
                        " nodes." );
            }
            aliasWorkList = new HashSet();
            while( !varNodeWorkList.isEmpty() ) {
                VarNode src = (VarNode) varNodeWorkList.iterator().next();
                varNodeWorkList.remove( src );
                aliasWorkList.add( src );
		handleVarNode( src );
            }
            if( verbose ) {
                System.out.println( "Now handling field references" );
            }

            for( Iterator srcIt = aliasWorkList.iterator(); srcIt.hasNext(); ) {

                final VarNode src = (VarNode) srcIt.next();
                for( Iterator srcFrIt = src.getAllFieldRefs().iterator(); srcFrIt.hasNext(); ) {
                    final FieldRefNode srcFr = (FieldRefNode) srcFrIt.next();
                    SparkField field = srcFr.getField();
                    for( Iterator dstIt = fieldToBase.get( field ).iterator(); dstIt.hasNext(); ) {
                        final VarNode dst = (VarNode) dstIt.next();
                        if( src.getP2Set().hasNonEmptyIntersection(
                                    dst.getP2Set() ) ) {
                            FieldRefNode dstFr = dst.dot( field );
                            aliasEdges.put( srcFr, dstFr );
                            aliasEdges.put( dstFr, srcFr );
                            fieldRefWorkList.add( srcFr );
                            fieldRefWorkList.add( dstFr );
                            makeP2Set( dstFr ).addAll( 
                                    srcFr.getP2Set().getOldSet(), null );
                            makeP2Set( srcFr ).addAll( 
                                    dstFr.getP2Set().getOldSet(), null );
                        }
                    }
                }
            }
            /*
            for( Iterator fieldIt = fieldToBase.keySet().iterator(); fieldIt.hasNext(); ) {
                final SparkField field = (SparkField) fieldIt.next();
                Set baseSet = fieldToBase.get( field );
                for( Iterator srcIt = baseSet.iterator(); srcIt.hasNext(); ) {
                    final VarNode src = (VarNode) srcIt.next();
                    FieldRefNode srcFr = src.dot( field );
                    Set aliasDests = aliasEdges.get( srcFr );
                    for( Iterator dstIt = baseSet.iterator(); dstIt.hasNext(); ) {
                        final VarNode dst = (VarNode) dstIt.next();
                        FieldRefNode dstFr = dst.dot( field );
                        if( !aliasDests.contains( dstFr ) ) {
                            if( src.getP2Set().hasNonEmptyIntersection( dst.getP2Set() ) ) {
                                aliasEdges.put( srcFr, dstFr );
                                aliasEdges.put( dstFr, srcFr );
                                makeP2Set( dstFr ).addAll( 
                                        srcFr.getP2Set().getOldSet(), null );
                                makeP2Set( srcFr ).addAll( 
                                        dstFr.getP2Set().getOldSet(), null );
                            }
                        }
                        if( dst == src ) break;
                    }
                }
            }
            */
            for( Iterator srcIt = fieldRefWorkList.iterator(); srcIt.hasNext(); ) {
                final FieldRefNode src = (FieldRefNode) srcIt.next();
                for( Iterator dstIt = aliasEdges.get( src ).iterator(); dstIt.hasNext(); ) {
                    final FieldRefNode dst = (FieldRefNode) dstIt.next();
                    makeP2Set( dst ).addAll( src.getP2Set().getNewSet(), null );
                }
                src.getP2Set().flushNew();
            }
            fieldRefWorkList = new HashSet();
            for( Iterator srcIt = pag.loadSources().iterator(); srcIt.hasNext(); ) {
                final FieldRefNode src = (FieldRefNode) srcIt.next();
                PointsToSetInternal set = getP2Set( src ).getNewSet();
                if( set.isEmpty() ) continue;
                Node[] targets = pag.loadLookup( src );
                for( int i = 0; i < targets.length; i++ ) {
                    VarNode target = (VarNode) targets[i];
                    if( target.makeP2Set().addAll( set, null ) ) {
                        varNodeWorkList.add( target );
                    }
                }
                getP2Set( src ).flushNew();
            }
	} while( !varNodeWorkList.isEmpty() );
    }

    /* End of public methods. */
    /* End of package methods. */

    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final boolean handleAllocNode( AllocNode src ) {
	boolean ret = false;
	Node[] targets = pag.allocLookup( src );
	for( int i = 0; i < targets.length; i++ ) {
	    if( targets[i].makeP2Set().add( src ) ) {
                varNodeWorkList.add( targets[i] );
                ret = true;
            }
	}
	return ret;
    }
    /** Propagates new points-to information of node src to all its
     * successors. */
    protected final boolean handleVarNode( final VarNode src ) {
	boolean ret = false;

        if( src.getReplacement() != src ) throw new RuntimeException(
                "Got bad node "+src+" with rep "+src.getReplacement() );

	final PointsToSetInternal newP2Set = src.getP2Set().getNewSet();
	if( newP2Set.isEmpty() ) return false;

	Node[] simpleTargets = pag.simpleLookup( src );
	for( int i = 0; i < simpleTargets.length; i++ ) {
	    if( simpleTargets[i].makeP2Set().addAll( newP2Set, null ) ) {
                varNodeWorkList.add( simpleTargets[i] );
                ret = true;
            }
	}

        Node[] storeTargets = pag.storeLookup( src );
        for( int i = 0; i < storeTargets.length; i++ ) {
            final FieldRefNode fr = (FieldRefNode) storeTargets[i];
            if( fr.makeP2Set().addAll( newP2Set, null ) ) {
                fieldRefWorkList.add( fr );
                ret = true;
            }
        }

	src.getP2Set().flushNew();
	return ret;
    }

    protected final PointsToSetInternal makeP2Set( FieldRefNode n ) {
        PointsToSetInternal ret = (PointsToSetInternal) loadSets.get(n);
        if( ret == null ) {
            ret = pag.getSetFactory().newSet( null, pag );
            loadSets.put( n, ret );
        }
        return ret;
    }

    protected final PointsToSetInternal getP2Set( FieldRefNode n ) {
        PointsToSetInternal ret = (PointsToSetInternal) loadSets.get(n);
        if( ret == null ) {
            return EmptyPointsToSet.v();
        }
        return ret;
    }

    protected PAG pag;
    protected MultiMap fieldToBase = new HashMultiMap();
    protected MultiMap aliasEdges = new HashMultiMap();
    protected Map loadSets = new HashMap();
}



