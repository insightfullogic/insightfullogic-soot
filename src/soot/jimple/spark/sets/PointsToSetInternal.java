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

package soot.jimple.spark.sets;
import soot.jimple.spark.*;
import soot.jimple.spark.pag.Node;
import soot.*;
import java.util.*;

/** Abstract base class for implementations of points-to sets.
 * @author Ondrej Lhotak
 */
public abstract class PointsToSetInternal implements PointsToSet {
    /** Adds contents of other minus the contents of exclude into this set;
     * returns true if this set changed. */
    static private boolean warnedAlready = false;
    public boolean addAll( PointsToSetInternal other,
            final PointsToSetInternal exclude ) {
        if( other instanceof DoublePointsToSet ) {
            return addAll( other.getNewSet(), exclude )
                | addAll( other.getOldSet(), exclude );
        } else if( other instanceof EmptyPointsToSet ) {
            return false;
        } else if( exclude instanceof EmptyPointsToSet ) { 
            return addAll( other, null );
        }
        if( !warnedAlready ) {
            System.out.println( "Warning: using default implementation of addAll. You should implement a faster specialized implementation." );
            System.out.println( "this is of type "+getClass().getName() );
            System.out.println( "other is of type "+other.getClass().getName() );
            if( exclude == null ) {
                System.out.println( "exclude is null" );
            } else {
                System.out.println( "exclude is of type "+
                        exclude.getClass().getName() );
            }
            warnedAlready = true;
        }
        return other.forall( new P2SetVisitor() {
        public final void visit( Node n ) {
                if( exclude == null || !exclude.contains( n ) )
                    returnValue = add( n ) | returnValue;
            }
        } );
    }
    /** Calls v's visit method on all nodes in this set. */
    public abstract boolean forall( P2SetVisitor v );
    /** Adds n to this set, returns true if n was not already in this set. */
    public abstract boolean add( Node n );
    /** Returns set of newly-added nodes since last call to flushNew. */
    public PointsToSetInternal getNewSet() { return this; }
    /** Returns set of nodes already present before last call to flushNew. */
    public PointsToSetInternal getOldSet() { return EmptyPointsToSet.v(); }
    /** Sets all newly-added nodes to old nodes. */
    public void flushNew() {}
    /** Sets all nodes to newly-added nodes. */
    public void unFlushNew() {}
    /** Merges other into this set. */
    public void mergeWith( PointsToSetInternal other ) 
    { addAll( other, null ); }
    /** Returns true iff the set contains n. */
    public abstract boolean contains( Node n );

    public PointsToSetInternal( Type type ) { this.type = type; }

    public boolean hasNonEmptyIntersection( PointsToSet other ) {
        final PointsToSetInternal o = (PointsToSetInternal) other;
        return forall( new P2SetVisitor() {
            public void visit( Node n ) {
                if( o.contains( n ) ) returnValue = true;
            }
        } );
    }
    public Set possibleTypes() {
        final HashSet ret = new HashSet();
        forall( new P2SetVisitor() {
            public void visit( Node n ) {
                Type t = n.getType();
                if( t instanceof RefType ) {
                    RefType rt = (RefType) t;
                    if( rt.getSootClass().isAbstract() ) return;
                }
                ret.add( t );
            }
        } );
        return ret;
    }
    public Type getType() {
        return type;
    }
    public void setType( Type type ) {
        this.type = type;
    }
    public int size() {
        final int[] ret = new int[1];
        forall( new P2SetVisitor() {
            public void visit( Node n ) {
                ret[0]++;
            }
        } );
        return ret[0];
    }
    /* End of public methods. */
    /* End of package methods. */

    protected Type type;
}

