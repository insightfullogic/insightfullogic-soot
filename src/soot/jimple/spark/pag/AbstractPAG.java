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

package soot.jimple.spark.pag;
import java.util.*;
import soot.jimple.*;
import soot.jimple.spark.*;
import soot.*;
import soot.jimple.spark.sets.*;
import soot.jimple.spark.solver.OnFlyCallGraph;
import soot.jimple.spark.internal.*;
import soot.jimple.spark.builder.*;
import soot.util.*;
import soot.util.queue.*;
import soot.options.*;
import soot.tagkit.*;
import soot.jimple.toolkits.callgraph.*;

/** Pointer assignment graph.
 * @author Ondrej Lhotak
 */
public abstract class AbstractPAG implements PointsToAnalysis {
    public AbstractPAG( AbstractSparkOptions opts ) {
        this.opts = opts;
        if( opts.add_tags() ) {
            nodeToTag = new HashMap();
        }
    }

    /** Returns the set of objects pointed to by instance field f
     * of the objects pointed to by l. */
    public PointsToSet reachingObjects( Local l, SootField f ) {
        return reachingObjects( reachingObjects(l), f );
    }

    private void addNodeTag( Node node, SootMethod m ) {
        if( nodeToTag != null ) {
            Tag tag;
            if( m == null ) {
                tag = new StringTag( node.toString() );
            } else {
                tag = new LinkTag( node.toString(), m, m.getDeclaringClass().getName() );
            }
            nodeToTag.put( node, tag );
        }
    }
    public AllocNode makeAllocNode( Object newExpr, Type type, SootMethod m ) {
        if( opts.types_for_sites() || opts.vta() ) newExpr = type;
	AllocNode ret = (AllocNode) valToAllocNode.get( newExpr );
	if( ret == null ) {
	    valToAllocNode.put( newExpr, ret = new AllocNode( this, newExpr, type, m ) );
            newAllocNodes.add( ret );
            addNodeTag( ret, m );
	} else if( !( ret.getType().equals( type ) ) ) {
	    throw new RuntimeException( "NewExpr "+newExpr+" of type "+type+
		    " previously had type "+ret.getType() );
	}
	return ret;
    }
    public AllocNode makeStringConstantNode( String s ) {
        if( opts.types_for_sites() || opts.vta() )
            return makeAllocNode( RefType.v( "java.lang.String" ),
                    RefType.v( "java.lang.String" ), null );
        StringConstantNode ret = (StringConstantNode) valToAllocNode.get( s );
	if( ret == null ) {
	    valToAllocNode.put( s, ret = new StringConstantNode( this, s ) );
            newAllocNodes.add( ret );
            addNodeTag( ret, null );
	}
	return ret;
    }
    public AllocNode makeClassConstantNode( String s ) {
        if( opts.types_for_sites() || opts.vta() )
            return makeAllocNode( RefType.v( "java.lang.Class" ),
                    RefType.v( "java.lang.Class" ), null );
        ClassConstantNode ret = (ClassConstantNode) valToAllocNode.get( "$$"+s );
	if( ret == null ) {
	    valToAllocNode.put( "$$"+s, ret = new ClassConstantNode( this, s ) );
            newAllocNodes.add( ret );
            addNodeTag( ret, null );
	}
	return ret;
    }

    ChunkedQueue newAllocNodes = new ChunkedQueue();
    public QueueReader allocNodeListener() { return newAllocNodes.reader(); }

    /** Finds the GlobalVarNode for the variable value, or returns null. */
    public GlobalVarNode findGlobalVarNode( Object value ) {
        if( opts.rta() ) {
            value = null;
        }
	return (GlobalVarNode) valToGlobalVarNode.get( value );
    }
    /** Finds the LocalVarNode for the variable value, or returns null. */
    public LocalVarNode findLocalVarNode( Object value ) {
        if( opts.rta() ) {
            value = null;
        } else if( value instanceof Local ) {
            return (LocalVarNode) localToNodeMap.get( (Local) value );
        }
	return (LocalVarNode) valToLocalVarNode.get( value );
    }
    /** Finds or creates the GlobalVarNode for the variable value, of type type. */
    public GlobalVarNode makeGlobalVarNode( Object value, Type type ) {
        if( opts.rta() ) {
            value = null;
            type = RefType.v("java.lang.Object");
        }
        GlobalVarNode ret = (GlobalVarNode) valToGlobalVarNode.get( value );
        if( ret == null ) {
            valToGlobalVarNode.put( value, 
                    ret = new GlobalVarNode( this, value, type ) );
            addNodeTag( ret, null );
        } else if( !( ret.getType().equals( type ) ) ) {
            throw new RuntimeException( "Value "+value+" of type "+type+
                    " previously had type "+ret.getType() );
        }
	return ret;
    }
    /** Finds or creates the LocalVarNode for the variable value, of type type. */
    public LocalVarNode makeLocalVarNode( Object value, Type type, SootMethod method ) {
        if( opts.rta() ) {
            value = null;
            type = RefType.v("java.lang.Object");
            method = null;
        } else if( value instanceof Local ) {
            Local val = (Local) value;
            if( val.getNumber() == 0 ) Scene.v().getLocalNumberer().add(val);
            LocalVarNode ret = (LocalVarNode) localToNodeMap.get( val );
            if( ret == null ) {
                localToNodeMap.put( (Local) value,
                    ret = new LocalVarNode( this, value, type, method ) );
                addNodeTag( ret, method );
            } else if( !( ret.getType().equals( type ) ) ) {
                throw new RuntimeException( "Value "+value+" of type "+type+
                        " previously had type "+ret.getType() );
            }
            return ret;
        }
        LocalVarNode ret = (LocalVarNode) valToLocalVarNode.get( value );
        if( ret == null ) {
            valToLocalVarNode.put( value, 
                    ret = new LocalVarNode( this, value, type, method ) );
            addNodeTag( ret, method );
        } else if( !( ret.getType().equals( type ) ) ) {
            throw new RuntimeException( "Value "+value+" of type "+type+
                    " previously had type "+ret.getType() );
        }
	return ret;
    }
    /** Finds the ContextVarNode for base variable value and context
     * context, or returns null. */
    public ContextVarNode findContextVarNode( Object baseValue, Object context ) {
	LocalVarNode base = findLocalVarNode( baseValue );
	if( base == null ) return null;
	return base.context( context );
    }
    /** Finds or creates the ContextVarNode for base variable baseValue and context
     * context, of type type. */
    public ContextVarNode makeContextVarNode( Object baseValue, Type baseType,
	    Object context, SootMethod method ) {
	LocalVarNode base = makeLocalVarNode( baseValue, baseType, method );
        return makeContextVarNode( base, context );
    }
    /** Finds or creates the ContextVarNode for base variable base and context
     * context, of type type. */
    public ContextVarNode makeContextVarNode( LocalVarNode base, Object context ) {
	ContextVarNode ret = base.context( context );
	if( ret == null ) {
	    ret = new ContextVarNode( this, base, context );
            addNodeTag( ret, base.getMethod() );
	}
	return ret;
    }
    /** Finds the FieldRefNode for base variable value and field
     * field, or returns null. */
    public FieldRefNode findLocalFieldRefNode( Object baseValue, SparkField field ) {
	VarNode base = findLocalVarNode( baseValue );
	if( base == null ) return null;
	return base.dot( field );
    }
    /** Finds the FieldRefNode for base variable value and field
     * field, or returns null. */
    public FieldRefNode findGlobalFieldRefNode( Object baseValue, SparkField field ) {
	VarNode base = findGlobalVarNode( baseValue );
	if( base == null ) return null;
	return base.dot( field );
    }
    /** Finds or creates the FieldRefNode for base variable baseValue and field
     * field, of type type. */
    public FieldRefNode makeLocalFieldRefNode( Object baseValue, Type baseType,
	    SparkField field, SootMethod method ) {
	VarNode base = makeLocalVarNode( baseValue, baseType, method );
        return makeFieldRefNode( base, field );
    }
    /** Finds or creates the FieldRefNode for base variable baseValue and field
     * field, of type type. */
    public FieldRefNode makeGlobalFieldRefNode( Object baseValue, Type baseType,
	    SparkField field ) {
	VarNode base = makeGlobalVarNode( baseValue, baseType );
        return makeFieldRefNode( base, field );
    }
    /** Finds or creates the FieldRefNode for base variable base and field
     * field, of type type. */
    public FieldRefNode makeFieldRefNode( VarNode base, SparkField field ) {
	FieldRefNode ret = base.dot( field );
	if( ret == null ) {
	    ret = new FieldRefNode( this, base, field );
	    if( base instanceof LocalVarNode ) {
	    	addNodeTag( ret, ((LocalVarNode) base).getMethod() );
	    } else {
	    	addNodeTag( ret, null );
	    }
	}
	return ret;
    }
    /** Finds the AllocDotField for base AllocNode an and field
     * field, or returns null. */
    public AllocDotField findAllocDotField( AllocNode an, SparkField field ) {
	return an.dot( field );
    }
    /** Finds or creates the AllocDotField for base variable baseValue and field
     * field, of type t. */
    public AllocDotField makeAllocDotField( AllocNode an, SparkField field ) {
	AllocDotField ret = an.dot( field );
	if( ret == null ) {
	    ret = new AllocDotField( this, an, field );
	}
	return ret;
    }

    public abstract boolean doAddSimpleEdge( VarNode from, VarNode to );
    public boolean addSimpleEdge( VarNode from, VarNode to ) {
	boolean ret = false;
        if( doAddSimpleEdge( from, to ) ) {
            edgeQueue.add( from );
            edgeQueue.add( to );
            ret = true;
        }
        if( opts.simple_edges_bidirectional() ) {
            if( doAddSimpleEdge( to, from ) ) {
                edgeQueue.add( to );
                edgeQueue.add( from );
                ret = true;
            }
        }
        return ret;
    }

    public abstract boolean doAddStoreEdge( VarNode from, FieldRefNode to );
    public boolean addStoreEdge( VarNode from, FieldRefNode to ) {
        if( !opts.rta() ) {
            if( doAddStoreEdge( from, to ) ) {
                edgeQueue.add( from );
                edgeQueue.add( to );
                return true;
            }
        }
        return false;
    }

    public abstract boolean doAddLoadEdge( FieldRefNode from, VarNode to );
    public boolean addLoadEdge( FieldRefNode from, VarNode to ) {
        if( !opts.rta() ) {
            if( doAddLoadEdge( from, to ) ) {
                edgeQueue.add( from );
                edgeQueue.add( to );
                return true;
            }
        }
        return false;
    }

    public abstract boolean doAddAllocEdge( AllocNode from, VarNode to ); 
    public boolean addAllocEdge( AllocNode from, VarNode to ) {
        FastHierarchy fh = typeManager.getFastHierarchy();
        if( fh == null || to.getType() == null 
        || fh.canStoreType( from.getType(), to.getType() ) ) {
            if( doAddAllocEdge( from, to ) ) {
                edgeQueue.add( from );
                edgeQueue.add( to );
                return true;
            }
        }
        return false;
    }

    /** Adds an edge to the graph, returning false if it was already there. */
    public final boolean addEdge( Node from, Node to ) {
        from = from.getReplacement();
        to = to.getReplacement();
	if( from instanceof VarNode ) {
	    if( to instanceof VarNode ) {
                return addSimpleEdge( (VarNode) from, (VarNode) to );
	    } else {
                return addStoreEdge( (VarNode) from, (FieldRefNode) to );
	    }
	} else if( from instanceof FieldRefNode ) {
            return addLoadEdge( (FieldRefNode) from, (VarNode) to );

	} else {
            return addAllocEdge( (AllocNode) from, (VarNode) to );
	}
    }

    protected ChunkedQueue edgeQueue = new ChunkedQueue();
    public QueueReader edgeReader() { return edgeQueue.reader(); }

    public int getNumAllocNodes() {
        return allocNodeNumberer.size();
    }
    public AbstractTypeManager getTypeManager() {
        return typeManager;
    }

    public void setOnFlyCallGraph( OnFlyCallGraph ofcg ) { this.ofcg = ofcg; }
    public OnFlyCallGraph getOnFlyCallGraph() { return ofcg; }
    public OnFlyCallGraph ofcg() { return ofcg; }
    /** Adds the base of a dereference to the list of dereferenced 
     * variables. */
    public void addDereference( VarNode base ) {
        dereferences.add( base );
    }

    /** Returns list of dereferences variables. */
    public List getDereferences() {
        return dereferences;
    }

    public Map getNodeTags() {
        return nodeToTag;
    }

    public P2SetFactory getSetFactory() {
        throw new RuntimeException();
    }

    /*
    public void dumpNumbersOfEdges() {
        Map[] maps = { simple, alloc, store, load,
            simpleInv, allocInv, storeInv, loadInv };
        String[] names = { "simple", "alloc", "store", "load",
            "simpleInv", "allocInv", "storeInv", "loadInv" };
        for( int i = 0; i < maps.length; i++ ) {
            Map m = maps[i];
            int size = 0;
            for( Iterator it = m.keySet().iterator(); it.hasNext(); ) {
                size += lookup( m, it.next() ).length;
            }
            G.v().out.println( ""+names[i]+" "+size );
        }
        G.v().out.println( "valToVarNodeSize: "+valToVarNode.size() );
    }
    */
    public abstract Iterator simpleSourcesIterator();
    public abstract Iterator allocSourcesIterator();
    public abstract Iterator storeSourcesIterator();
    public abstract Iterator loadSourcesIterator();
    public abstract Iterator simpleInvSourcesIterator();
    public abstract Iterator allocInvSourcesIterator();
    public abstract Iterator storeInvSourcesIterator();
    public abstract Iterator loadInvSourcesIterator();

    private Numberer allocNodeNumberer = new Numberer();
    public Numberer getAllocNodeNumberer() { return allocNodeNumberer; }
    private Numberer varNodeNumberer = new Numberer();
    public Numberer getVarNodeNumberer() { return varNodeNumberer; }
    private Numberer fieldRefNodeNumberer = new Numberer();
    public Numberer getFieldRefNodeNumberer() { return fieldRefNodeNumberer; }
    private Numberer allocDotFieldNodeNumberer = new Numberer();
    public Numberer getAllocDotFieldNodeNumberer() { return allocDotFieldNodeNumberer; }


    /** Returns SparkOptions for this graph. */
    public AbstractSparkOptions getOpts() { return opts; }

    final public void addCallTarget( Edge e ) {
        if( !e.passesParameters() ) return;
        AbstractMethodPAG srcmpag = AbstractMethodPAG.v( this, e.src() );
        AbstractMethodPAG tgtmpag = AbstractMethodPAG.v( this, e.tgt() );
        if( e.isExplicit() || e.kind() == Edge.THREAD ) {
            addCallTarget( srcmpag, tgtmpag, (Stmt) e.srcUnit(),
                           e.srcCtxt(), e.tgtCtxt() );
        } else {
            switch( e.kind() ) {
                case Edge.PRIVILEGED:
                    // Flow from first parameter of doPrivileged() invocation
                    // to this of target, and from return of target to the
                    // return of doPrivileged()

                    InvokeExpr ie = e.srcStmt().getInvokeExpr();

                    Node parm = srcmpag.nodeFactory().getNode( ie.getArg(0) );
                    parm = srcmpag.parameterize( parm, e.srcCtxt() );
                    parm = parm.getReplacement();

                    Node thiz = tgtmpag.nodeFactory().caseThis();
                    thiz = tgtmpag.parameterize( thiz, e.tgtCtxt() );
                    thiz = thiz.getReplacement();

                    addEdge( parm, thiz );

                    if( e.srcUnit() instanceof AssignStmt ) {
                        AssignStmt as = (AssignStmt) e.srcUnit();

                        Node ret = tgtmpag.nodeFactory().caseRet();
                        ret = tgtmpag.parameterize( ret, e.tgtCtxt() );
                        ret = ret.getReplacement();

                        Node lhs = srcmpag.nodeFactory().getNode(as.getLeftOp());
                        lhs = srcmpag.parameterize( lhs, e.srcCtxt() );
                        lhs = lhs.getReplacement();

                        addEdge( ret, lhs );
                    }
                    break;

                case Edge.EXIT:
                case Edge.FINALIZE:
                    Node srcThis = srcmpag.nodeFactory().caseThis();
                    srcThis = srcmpag.parameterize( srcThis, e.srcCtxt() );
                    srcThis = srcThis.getReplacement();

                    Node tgtThis = tgtmpag.nodeFactory().caseThis();
                    tgtThis = tgtmpag.parameterize( tgtThis, e.tgtCtxt() );
                    tgtThis = tgtThis.getReplacement();

                    addEdge( srcThis, tgtThis );
                    break;

                case Edge.NEWINSTANCE:
                    Stmt s = (Stmt) e.srcUnit();
                    InstanceInvokeExpr iie = (InstanceInvokeExpr) s.getInvokeExpr();

                    Node cls = srcmpag.nodeFactory().getNode( iie.getBase() );
                    cls = srcmpag.parameterize( cls, e.srcCtxt() );
                    cls = cls.getReplacement();
                    Node newObject = nodeFactory.caseNewInstance( (VarNode) cls );

                    Node initThis = tgtmpag.nodeFactory().caseThis();
                    initThis = tgtmpag.parameterize( initThis, e.tgtCtxt() );
                    initThis = initThis.getReplacement();

                    addEdge( newObject, initThis );
                    break;
                default:
                    throw new RuntimeException( "Unhandled edge "+e );
            }
        }
    }

    
    /** Adds method target as a possible target of the invoke expression in s.
     * If target is null, only creates the nodes for the call site,
     * without actually connecting them to any target method.
     **/
    final public void addCallTarget( AbstractMethodPAG srcmpag,
                                     AbstractMethodPAG tgtmpag,
                                     Stmt s,
                                     Object srcContext,
                                     Object tgtContext ) {
        MethodNodeFactory srcnf = srcmpag.nodeFactory();
        MethodNodeFactory tgtnf = tgtmpag.nodeFactory();
        InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();
        int numArgs = ie.getArgCount();
        for( int i = 0; i < numArgs; i++ ) {
            Value arg = ie.getArg( i );
            if( !( arg.getType() instanceof RefLikeType ) ) continue;
            if( arg instanceof NullConstant ) continue;

            Node argNode = srcnf.getNode( arg );
            argNode = srcmpag.parameterize( argNode, srcContext );
            argNode = argNode.getReplacement();

            Node parm = tgtnf.caseParm( i );
            parm = tgtmpag.parameterize( parm, tgtContext );
            parm = parm.getReplacement();

            addEdge( argNode, parm );
        }
        if( ie instanceof InstanceInvokeExpr ) {
            InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;

            Node baseNode = srcnf.getNode( iie.getBase() );
            baseNode = srcmpag.parameterize( baseNode, srcContext );
            baseNode = baseNode.getReplacement();

            Node thisRef = tgtnf.caseThis();
            thisRef = tgtmpag.parameterize( thisRef, tgtContext );
            thisRef = thisRef.getReplacement();
            addEdge( baseNode, thisRef );
        }
        if( s instanceof AssignStmt ) {
            Value dest = ( (AssignStmt) s ).getLeftOp();
            if( dest.getType() instanceof RefLikeType && !(dest instanceof NullConstant) ) {

                Node destNode = srcnf.getNode( dest );
                destNode = srcmpag.parameterize( destNode, srcContext );
                destNode = destNode.getReplacement();

                Node retNode = tgtnf.caseRet();
                retNode = tgtmpag.parameterize( retNode, tgtContext );
                retNode = retNode.getReplacement();

                addEdge( retNode, destNode );
            }
        }
    }
    /* End of public methods. */

    /** Node uses this to notify PAG that n2 has been merged into n1. */
    void mergedWith( Node n1, Node n2 ) {
        throw new RuntimeException();
    }

    /* End of package methods. */

    protected AbstractSparkOptions opts;

    protected Map simple = new HashMap();
    protected Map load = new HashMap();
    protected Map store = new HashMap();
    protected Map alloc = new HashMap();

    protected Map simpleInv = new HashMap();
    protected Map loadInv = new HashMap();
    protected Map storeInv = new HashMap();
    protected Map allocInv = new HashMap();

    protected boolean addToMap( Map m, Node key, Node value ) {
	Object valueList = m.get( key );

	if( valueList == null ) {
	    m.put( key, valueList = new HashSet(4) );
	} else if( !(valueList instanceof Set) ) {
	    Node[] ar = (Node[]) valueList;
            Node[] newar = new Node[ar.length+1];
            for( int i = 0; i < ar.length; i++ ) {
                Node n = ar[i];
                if( n == value ) return false;
                newar[i] = n;
            }
            newar[ar.length] = value;
            m.put( key, newar );
            return true;
	}
	return ((Set) valueList).add( value );
    }
    private Map valToLocalVarNode = new HashMap(1000);
    private Map valToGlobalVarNode = new HashMap(1000);
    private Map valToAllocNode = new HashMap(1000);
    private OnFlyCallGraph ofcg;
    private ArrayList dereferences = new ArrayList();
    protected AbstractTypeManager typeManager;
    private LargeNumberedMap localToNodeMap = new LargeNumberedMap( Scene.v().getLocalNumberer() );
    public int maxFinishNumber = 0;
    private Map nodeToTag;
    private GlobalNodeFactory nodeFactory = new GlobalNodeFactory(this);
    public GlobalNodeFactory nodeFactory() { return nodeFactory; }

}

