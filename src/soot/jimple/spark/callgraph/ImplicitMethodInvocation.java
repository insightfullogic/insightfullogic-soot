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

package soot.jimple.spark.callgraph;
import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;
import soot.jimple.toolkits.callgraph.*;


/** Given a method, returns the set of methods that may be invoked implicitly
 * by the VM for that method.
 * @author Ondrej Lhotak
 */
public class ImplicitMethodInvocation
{ 
    public ImplicitMethodInvocation( Singletons.Global g ) {}
    public static ImplicitMethodInvocation v() { return G.v().ImplicitMethodInvocation(); }

    final NumberedString sigMain = Scene.v().getSubSigNumberer().
        findOrAdd( "void main(java.lang.String[])" );
    final NumberedString sigFinalize = Scene.v().getSubSigNumberer().
        findOrAdd( "void finalize()" );
    final NumberedString sigExit = Scene.v().getSubSigNumberer().
        findOrAdd( "void exit()" );
    final NumberedString sigClinit = Scene.v().getSubSigNumberer().
        findOrAdd( "void <clinit>()" );
    final NumberedString sigStart = Scene.v().getSubSigNumberer().
        findOrAdd( "void start()" );
    final NumberedString sigRun = Scene.v().getSubSigNumberer().
        findOrAdd( "void run()" );
    final NumberedString sigObjRun = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Object run()" );
    final NumberedString sigForName = Scene.v().getSubSigNumberer().
        findOrAdd( "java.lang.Class forName(java.lang.String)" );
    final RefType clPrivilegedAction = RefType.v("java.security.PrivilegedAction");
    final RefType clPrivilegedExceptionAction = RefType.v("java.security.PrivilegedExceptionAction");
    final RefType clRunnable = RefType.v("java.lang.Runnable");
    private final void addMethod( NumberedSet set, SootClass cls, NumberedString methodSubSig ) {
        if( cls.declaresMethod( methodSubSig ) ) {
            set.add( cls.getMethod( methodSubSig ) );
        }
    }
    private final void addMethod( NumberedSet set, String methodSig ) {
        if( Scene.v().containsMethod( methodSig ) ) {
            set.add( Scene.v().getMethod( methodSig ) );
        }
    }
    private final void addEdge( List edges, SootMethod src, Stmt stmt, SootClass cls, NumberedString methodSubSig, int type ) {
        if( cls.declaresMethod( methodSubSig ) ) {
            edges.add(
                new Edge( src, stmt, cls.getMethod( methodSubSig ), type ) );
        }
    }
    private final void addEdge( List edges, SootMethod src, Stmt stmt, String methodSig, int type ) {
        if( Scene.v().containsMethod( methodSig ) ) {
            edges.add(
                new Edge( src, stmt, Scene.v().getMethod( methodSig ), type ) );
        }
    }
    public NumberedSet getEntryPoints() {
        NumberedSet ret = new NumberedSet( Scene.v().getMethodNumberer() );
        addMethod( ret, Scene.v().getMainClass(), sigMain );
        addMethod( ret, Scene.v().getMainClass(), sigClinit );
        addMethod( ret, "<java.lang.System: void initializeSystemClass()>" );
        addMethod( ret, "<java.lang.ThreadGroup: void <init>()>");
        addMethod( ret, "<java.lang.ThreadGroup: void remove(java.lang.Thread)>");
        addMethod( ret, "<java.lang.ThreadGroup: void uncaughtException(java.lang.Thread,java.lang.Throwable)>");
        addMethod( ret, "<java.lang.System: void loadLibrary(java.lang.String)>");
        addMethod( ret, "<java.lang.ClassLoader: java.lang.Class loadClassInternal(java.lang.String)>");
        addMethod( ret, "<java.lang.ClassLoader: void checkPackageAccess(java.lang.Class,java.security.ProtectionDomain)>");
        addMethod( ret, "<java.lang.ClassLoader: void addClass(java.lang.Class)>");
        addMethod( ret, "<java.lang.ClassLoader: long findNative(java.lang.ClassLoader,java.lang.String)>");
        addMethod( ret, "<java.security.PrivilegedActionException: void <init>(java.lang.Exception)>");
        addMethod( ret, "<java.lang.ref.Finalizer: void register(java.lang.Object)>");
        addMethod( ret, "<java.lang.ref.Finalizer: void runFinalizer()>");
        addMethod( ret, "<java.lang.String: byte[] getBytes()>");
        return ret;
    }
    public List getImplicitTargets( SootMethod source, boolean verbose ) {
        final List ret = new ArrayList();
        final SootClass scl = source.getDeclaringClass();
        if( source.isNative() ) return ret;
        if( source.getSubSignature().indexOf( "<init>" ) >= 0 ) {
            addEdge( ret, source, null, scl, sigFinalize, Edge.FINALIZE );
            FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
            if( fh.canStoreType( scl.getType(), clPrivilegedAction )
            ||  fh.canStoreType( scl.getType(), clPrivilegedExceptionAction ) ) {
                addEdge( ret, source, null, scl, sigObjRun, Edge.PRIVILEGED );
            }
            if( fh.canStoreType( scl.getType(), clRunnable ) ) {
                addEdge( ret, source, null, scl, sigExit, Edge.EXIT );
            }
        }
        Body b = source.retrieveActiveBody();
        boolean warnedAlready = false;
        for( Iterator sIt = b.getUnits().iterator(); sIt.hasNext(); ) {
            final Stmt s = (Stmt) sIt.next();
            if( s.containsInvokeExpr() ) {
                InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();
                if( ie.getMethod().getSignature().equals( "<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>" ) ) {
                    if( !warnedAlready ) {
                        if( verbose ) {
                            G.v().out.println( "Warning: call to "+
                                "java.lang.reflect.Method: invoke() from "+source+
                                "; graph will be incomplete!" );
                            warnedAlready = true;
                        }
                    }
                }
                if( ie.getMethod().getNumberedSubSignature() == sigForName ) {
                    Value name = ie.getArg(0);
                    if( name instanceof StringConstant ) {
                        String cls = ((StringConstant) name ).value;
                        if( cls.charAt(0) != '[' ) {
                            if( !Scene.v().containsClass( cls ) ) {
                                if( verbose ) {
                                    G.v().out.println( "Warning: Class "+cls+" is"+
                                        " a dynamic class, and you did not specify"+
                                        " it as such; graph will be incomplete!" );
                                }
                            } else {
                                SootClass sootcls = Scene.v().getSootClass( cls );
                                if( !sootcls.isApplicationClass() ) {
                                    sootcls.setLibraryClass();
                                }
                                addEdge( ret, source, s, scl, sigClinit, Edge.CLINIT );
                            }
                        }
                    } else {
                        if( verbose ) {
                            G.v().out.println( "Warning: Method "+source+
                                    " is reachable, and calls Class.forName on a"+
                                    " non-constant String; graph will be incomplete!" );
                        }
                    }
                }
                addEdge( ret, source, s, ie.getMethod().getDeclaringClass(),
                        sigClinit, Edge.CLINIT );
            }
            if( s.containsFieldRef() ) {
                FieldRef fr = (FieldRef) s.getFieldRef();
                if( fr instanceof StaticFieldRef ) {
                    SootClass cl = fr.getField().getDeclaringClass();
                    addEdge( ret, source, s, cl, sigClinit, Edge.CLINIT );
                }
            }
            if( s instanceof AssignStmt ) {
                Value rhs = ((AssignStmt)s).getRightOp();
                if( rhs instanceof NewExpr ) {
                    NewExpr r = (NewExpr) rhs;
                    addEdge( ret, source, s, r.getBaseType().getSootClass(),
                            sigClinit, Edge.CLINIT );
                } else if( rhs instanceof NewArrayExpr || rhs instanceof NewMultiArrayExpr ) {
                    Type t = rhs.getType();
                    if( t instanceof ArrayType ) t = ((ArrayType)t).baseType;
                    if( t instanceof RefType ) {
                        addEdge( ret, source, s, ((RefType) t).getSootClass(),
                                sigClinit, Edge.CLINIT );
                    }
                }
            }
        }
        return ret;
    }
}


