/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002, 2003 Ondrej Lhotak
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

package soot.jimple.paddle;
import soot.jimple.*;
import soot.*;
import soot.util.*;
import soot.toolkits.scalar.Pair;
import java.util.*;

/** Class implementing builder parameters (this decides
 * what kinds of nodes should be built for each kind of Soot value).
 * @author Ondrej Lhotak
 */
public abstract class MethodNodeFactory extends AbstractJimpleValueSwitch {
    public abstract SootMethod method();

    protected NodeFactory gnf;
    protected NodeManager nm;
    public MethodNodeFactory() {
        gnf = PaddleScene.v().nodeFactory();
        nm = PaddleScene.v().nodeManager();
    }

    public Node getNode( Value v ) {
        v.apply( this );
        return getNode();
    }
    /** Adds the edges required for this statement to the graph. */
    final public void handleStmt( Stmt s ) {
	if( s.containsInvokeExpr() ) {
	    return;
	}
	s.apply( new AbstractStmtSwitch() {
	    final public void caseAssignStmt(AssignStmt as) {
                Value l = as.getLeftOp();
                Value r = as.getRightOp();
		if( !( l.getType() instanceof RefLikeType ) ) return;
		l.apply( MethodNodeFactory.this );
		Node dest = getNode();
		r.apply( MethodNodeFactory.this );
		Node src = getNode();
		addEdge( src, dest );
	    }
	    final public void caseReturnStmt(ReturnStmt rs) {
		if( !( rs.getOp().getType() instanceof RefLikeType ) ) return;
		rs.getOp().apply( MethodNodeFactory.this );
                Node retNode = getNode();
                addEdge( retNode, caseRet() );
	    }
	    final public void caseIdentityStmt(IdentityStmt is) {
		if( !( is.getLeftOp().getType() instanceof RefLikeType ) ) return;
		is.getLeftOp().apply( MethodNodeFactory.this );
		Node dest = getNode();
		is.getRightOp().apply( MethodNodeFactory.this );
		Node src = getNode();
		addEdge( src, dest );
	    }
	    final public void caseThrowStmt(ThrowStmt ts) {
		ts.getOp().apply( MethodNodeFactory.this );
		addEdge( getNode(), gnf.caseThrow() );
	    }
	} );
    }
    final public Node getNode() {
	return (Node) getResult();
    }
    final public Node caseThis() {
	VarNode ret = nm.makeLocalVarNode(
		    new Pair( method(), PointsToAnalysis.THIS_NODE ),
		    method().getDeclaringClass().getType(), method() );
        return ret;
    }

    final public Node caseParm( int index ) {
        VarNode ret = nm.makeLocalVarNode(
                    new Pair( method(), new Integer( index ) ),
                    method().getParameterType( index ), method() );
        return ret;
    }

    final public Node caseRet() {
        VarNode ret = nm.makeLocalVarNode(
                    Parm.v( method(), PointsToAnalysis.RETURN_NODE ),
                    method().getReturnType(), method() );
        return ret;
    }
    final public Node caseArray( VarNode base ) {
	return nm.makeFieldRefNode( base, ArrayElement.v() );
    }
    /* End of public methods. */
    /* End of package methods. */

    // OK, these ones are public, but they really shouldn't be; it's just
    // that Java requires them to be, because they override those other
    // public methods.
    final public void caseArrayRef( ArrayRef ar ) {
    	caseLocal( (Local) ar.getBase() );
	setResult( caseArray( (VarNode) getNode() ) );
    }
    final public void caseCastExpr( CastExpr ce ) {
	Pair castPair = new Pair( ce, PointsToAnalysis.CAST_NODE );
	ce.getOp().apply( this );
	Node opNode = getNode();
	Node castNode = nm.makeLocalVarNode( castPair, ce.getCastType(), method() );
	addEdge( opNode, castNode );
	setResult( castNode );
    }
    final public void caseCaughtExceptionRef( CaughtExceptionRef cer ) {
	setResult( gnf.caseThrow() );
    }
    final public void caseInstanceFieldRef( InstanceFieldRef ifr ) {
	if( PaddleScene.v().options().field_based() || PaddleScene.v().options().vta() ) {
	    setResult( nm.makeGlobalVarNode( 
			ifr.getField(), 
			ifr.getField().getType() ) );
	} else {
	    setResult( nm.makeLocalFieldRefNode( 
			ifr.getBase(), 
			ifr.getBase().getType(),
			ifr.getField(),
                        method() ) );
	}
    }
    final public void caseLocal( Local l ) {
	setResult( nm.makeLocalVarNode( l,  l.getType(), method() ) );
    }
    final public void caseNewArrayExpr( NewArrayExpr nae ) {
        setResult( nm.makeAllocNode( nae, nae.getType(), method() ) );
    }
    final public void caseNewExpr( NewExpr ne ) {
        if( PaddleScene.v().options().merge_stringbuffer() 
        && ne.getType().equals( RefType.v("java.lang.StringBuffer" ) ) ) {
            setResult( nm.makeAllocNode( ne.getType(), ne.getType(), null ) );
        } else {
            setResult( nm.makeAllocNode( ne, ne.getType(), method() ) );
        }
    }
    final public void caseNewMultiArrayExpr( NewMultiArrayExpr nmae ) {
        ArrayType type = (ArrayType) nmae.getType();
        AllocNode prevAn = nm.makeAllocNode(
            new Pair( nmae, new Integer( type.numDimensions ) ), type, method() );
        VarNode prevVn = nm.makeLocalVarNode( prevAn, prevAn.getType(), null );
        addEdge( prevAn, prevVn );
        setResult( prevAn );
        while( true ) {
            Type t = type.getElementType();
            if( !( t instanceof ArrayType ) ) break;
            type = (ArrayType) t;
            AllocNode an = nm.makeAllocNode(
                new Pair( nmae, new Integer( type.numDimensions ) ), type, method() );
            VarNode vn = nm.makeLocalVarNode( an, an.getType(), null );
            addEdge( an, vn );
            addEdge( vn, nm.makeFieldRefNode( prevVn, ArrayElement.v() ) );
            prevAn = an;
            prevVn = vn;
        }
    }
    final public void caseParameterRef( ParameterRef pr ) {
	setResult( caseParm( pr.getIndex() ) );
    }
    final public void caseStaticFieldRef( StaticFieldRef sfr ) {
	setResult( nm.makeGlobalVarNode( 
		    sfr.getField(), 
		    sfr.getField().getType() ) );
    }
    final public void caseStringConstant( StringConstant sc ) {
        AllocNode stringConstant;
        if( PaddleScene.v().options().string_constants()
        || Scene.v().containsClass(sc.value) 
        || ( sc.value.length() > 0 && sc.value.charAt(0) == '[' ) ) {
            stringConstant = nm.makeStringConstantNode( sc.value );
        } else {
            stringConstant = nm.makeAllocNode(
                PointsToAnalysis.STRING_NODE,
                RefType.v( "java.lang.String" ), null );
        }
        VarNode stringConstantLocal = nm.makeGlobalVarNode(
            stringConstant,
            RefType.v( "java.lang.String" ) );
        addEdge( stringConstant, stringConstantLocal );
        setResult( stringConstantLocal );
    }
    final public void caseThisRef( ThisRef tr ) {
	setResult( caseThis() );
    }
    final public void caseNullConstant( NullConstant nr ) {
	setResult( null );
    }
    final public void defaultCase( Object v ) {
	throw new RuntimeException( "failed to handle "+v );
    }
    public void addMiscEdges() {
        // Add node for parameter (String[]) in main method
        if( method().getSubSignature().equals( SootMethod.getSubSignature( "main", new SingletonList( ArrayType.v(RefType.v("java.lang.String"), 1) ), VoidType.v() ) ) ) {
            addEdge( gnf.caseArgv(), caseParm(0) );
        }

        if( method().getSignature().equals(
                    "<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.String)>" ) ) {
            addEdge( gnf.caseMainThread(), caseThis() );
            addEdge( gnf.caseMainThreadGroup(), caseParm( 0 ) );
        }

        if( method().getSubSignature().equals(
            "java.lang.Class loadClass(java.lang.String)" ) ) {
            SootClass c = method().getDeclaringClass();
outer:      do {
                while( !c.getName().equals( "java.lang.ClassLoader" ) ) {
                    if( !c.hasSuperclass() ) {
                        break outer;
                    }
                    c = c.getSuperclass();
                }
                addEdge( gnf.caseDefaultClassLoader(),
                        caseThis() );
                addEdge( gnf.caseMainClassNameString(),
                        caseParm(0) );
            } while( false );
        }
    }
    private void addEdge( Node src, Node dst ) {
        gnf.addEdge( src, dst );
    }
}

