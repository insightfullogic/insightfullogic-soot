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
import soot.*;
import java.util.*;

/** Checks points-to sets with pointer assignment graph to make sure everything
 * has been correctly propagated.
 * @author Ondrej Lhotak
 */

public class Checker {
    public Checker( PAG pag ) { this.pag = pag; }
    /** Actually does the propagation. */
    public void check() {
	for( Iterator it = pag.allocSources().iterator(); it.hasNext(); ) {
	    handleAllocNode( (AllocNode) it.next() );
	}
        for( Iterator it = pag.simpleSources().iterator(); it.hasNext(); ) {
            handleSimples( (VarNode) it.next() );
        }
        for( Iterator it = pag.loadSources().iterator(); it.hasNext(); ) {
            handleLoads( (FieldRefNode) it.next() );
        }
        for( Iterator it = pag.storeSources().iterator(); it.hasNext(); ) {
            handleStores( (VarNode) it.next() );
        }
    }

    /* End of public methods. */
    /* End of package methods. */

    protected void checkAll( final Node container, PointsToSetInternal nodes,
            final Node upstream ) {
        nodes.forall( new P2SetVisitor() {
        public final void visit( Node n ) {
                checkNode( container, n, upstream );
            }
        } );
    }
    protected void checkNode( Node container, Node n, Node upstream ) {
        if( container.getReplacement() != container )
            throw new RuntimeException( "container "+container+" is illegal" );
        if( upstream.getReplacement() != upstream )
            throw new RuntimeException( "upstream "+upstream+" is illegal" );
        PointsToSetInternal p2set = container.getP2Set();
        FastHierarchy fh = PointsToSetInternal.getFastHierarchy();
        if( !container.getP2Set().contains( n ) 
                && ( fh == null || container.getType() == null ||
                fh.canStoreType( n.getType(), container.getType() ) ) ) {
            System.out.println( "Check failure: "+container+" does not have "+n
                    +"; upstream is "+upstream );
        }
    }
    protected void handleAllocNode( AllocNode src ) {
	Node[] targets = pag.allocLookup( src );
	for( int i = 0; i < targets.length; i++ ) {
            checkNode( targets[i], src, src );
	}
    }

    protected void handleSimples( VarNode src ) {
	PointsToSetInternal srcSet = src.getP2Set();
	if( srcSet.isEmpty() ) return;
	final Node[] simpleTargets = pag.simpleLookup( src );
	for( int i = 0; i < simpleTargets.length; i++ ) {
            checkAll( simpleTargets[i], srcSet, src );
	}
    }

    protected void handleStores( final VarNode src ) {
	final PointsToSetInternal srcSet = src.getP2Set();
	if( srcSet.isEmpty() ) return;
	Node[] storeTargets = pag.storeLookup( src );
	for( int i = 0; i < storeTargets.length; i++ ) {
            final FieldRefNode fr = (FieldRefNode) storeTargets[i];
            final SparkField f = fr.getField();
            fr.getBase().getP2Set().forall( new P2SetVisitor() {
            public final void visit( Node n ) {
                    AllocDotField nDotF = pag.makeAllocDotField( 
                        (AllocNode) n, f );
                    checkAll( nDotF, srcSet, src );
                }
            } );
	}
    }

    protected void handleLoads( final FieldRefNode src ) {
	final Node[] loadTargets = pag.loadLookup( src );
        final SparkField f = src.getField();
        src.getBase().getP2Set().forall( new P2SetVisitor() {
        public final void visit( Node n ) {
                AllocDotField nDotF = ((AllocNode)n).dot( f );
                if( nDotF == null ) return;
                PointsToSetInternal set = nDotF.getP2Set();
                if( set.isEmpty() ) return;
                for( int i = 0; i < loadTargets.length; i++ ) {
                    VarNode target = (VarNode) loadTargets[i];
                    checkAll( target, set, src );
                }
            }
        } );
    }

    protected PAG pag;
}



