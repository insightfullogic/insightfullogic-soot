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

package soot.jimple.toolkits.callgraph;
import soot.*;
import soot.util.*;
import java.util.*;
import soot.util.queue.*;


/** Keeps track of the methods transitively reachable from the specified
 * entry points through the given call graph edges.
 * @author Ondrej Lhotak
 */
public class ReachableMethods
{ 
    private CallGraph cg;
    private List entryPoints = new ArrayList(); 
    private QueueReader edgeSource;
    private ChunkedQueue reachables = new ChunkedQueue();
    private NumberedSet set = new NumberedSet( Scene.v().getMethodNumberer() );
    private QueueReader unprocessedMethods;
    private QueueReader allReachables = reachables.reader();
    private Filter filter;
    public ReachableMethods( CallGraph graph, Iterator entryPoints ) {
        this( graph, entryPoints, null );
    }
    public ReachableMethods( CallGraph graph, Iterator entryPoints, Filter filter ) {
        this.filter = filter;
        this.cg = graph;
        addMethods( entryPoints );
        unprocessedMethods = reachables.reader();
        this.edgeSource = graph.listener();
    }
    public ReachableMethods( CallGraph graph, Collection entryPoints ) {
    	this(graph, entryPoints.iterator());
    }
    private void addMethods( Iterator methods ) {
        while( methods.hasNext() )
            addMethod( (SootMethod) methods.next() );
    }
    private void addMethod( SootMethod m ) {
            if( set.add( m ) ) reachables.add( m );
    }
    /** Causes the QueueReader objects to be filled up with any methods
     * that have become reachable since the last call. */
    public void update() {
        while(true) {
            Edge e = (Edge) edgeSource.next();
            if( e == null ) break;
            if( set.contains( e.src() ) ) addMethod( e.tgt() );
        }
        while(true) {
            SootMethod m = (SootMethod) unprocessedMethods.next();
            if( m == null ) break;
            Iterator targets = cg.targetsOf( m );
            if( filter != null ) targets = filter.wrap( targets );
            addMethods( new Targets( targets ) );
        }
    }
    /** Returns a QueueReader object containing all methods found reachable
     * so far, and which will be informed of any new methods that are later
     * found to be reachable. */
    public QueueReader listener() {
        return (QueueReader) allReachables.clone();
    }
    /** Returns a QueueReader object which will contain ONLY NEW methods
     * which will be found to be reachable, but not those that have already
     * been found to be reachable.
     */
    public QueueReader newListener() {
        return reachables.reader();
    }
    /** Returns true iff method is reachable. */
    public boolean contains( SootMethod m ) {
        return set.contains( m );
    }
}


