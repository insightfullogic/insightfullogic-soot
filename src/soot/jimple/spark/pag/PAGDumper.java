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

package soot.jimple.spark.pag;
import java.util.*;
import soot.jimple.spark.*;
import soot.*;
import soot.jimple.spark.sets.*;
import java.io.*;
import soot.jimple.spark.solver.TopoSorter;
import soot.jimple.spark.sets.PointsToSetInternal;

/** Dumps a pointer assignment graph to a file.
 * @author Ondrej Lhotak
 */
public class PAGDumper {
    public PAGDumper( PAG pag ) {
        this.pag = pag;
        this.fh = Scene.v().getOrMakeFastHierarchy();
    }
    public void dumpPointsToSets() {
        try {
            final PrintWriter file = new PrintWriter(
                    new FileOutputStream( "solution" ) );
            file.println( "Solution:" );
            for( Iterator vnIt = pag.allVarNodes().iterator(); vnIt.hasNext(); ) {
                final VarNode vn = (VarNode) vnIt.next();
                if( vn.getReplacement() != vn ) {
                    System.out.println( "Skipping merged node" );
                    continue;
                }
                PointsToSetInternal p2set = vn.getP2Set();
                if( p2set == null ) continue;
                p2set.forall( new P2SetVisitor() {
                public final void visit( Node n ) {
                        try {
                            dumpNode( vn, file );
                            file.print( " " );
                            dumpNode( n, file );
                            file.println( "" );
                        } catch( IOException e ) {
                            throw new RuntimeException( "Couldn't dump solution."+e );
                        }
                    }
                } );
            }
            file.close();
        } catch( IOException e ) {
            throw new RuntimeException( "Couldn't dump solution."+e );
        }
    }
    public void dump() {
        try {
            PrintWriter file = new PrintWriter(
                new FileOutputStream( "pag" ) );
            String[] labels = { "Allocations:", "Assignments:", "Loads:",
                "Stores:" };

            if( pag.getOpts().topoSort() ) {
                new TopoSorter( pag, false ).sort();
            }
            file.println( "Allocations:" );
            for( Iterator nIt = pag.allocSources().iterator(); nIt.hasNext(); ) {
                final AllocNode n = (AllocNode) nIt.next();
                if( n.getReplacement() != n ) continue;
                Node[] succs = pag.allocLookup( n );
                for( int i = 0; i < succs.length; i++ ) {
                    dumpNode( n, file );
                    file.print( " ");
                    dumpNode( succs[i], file );
                    file.println( "");
                }
            }

            file.println( "Assignments:" );
            for( Iterator nIt = pag.simpleSources().iterator(); nIt.hasNext(); ) {
                final VarNode n = (VarNode) nIt.next();
                if( n.getReplacement() != n ) continue;
                Node[] succs = pag.simpleLookup( n );
                for( int i = 0; i < succs.length; i++ ) {
                    dumpNode( n, file );
                    file.print( " ");
                    dumpNode( succs[i], file );
                    file.println( "");
                }
            }
            
            file.println( "Loads:" );
            for( Iterator nIt = pag.loadSources().iterator(); nIt.hasNext(); ) {
                final FieldRefNode n = (FieldRefNode) nIt.next();
                Node[] succs = pag.loadLookup( n );
                for( int i = 0; i < succs.length; i++ ) {
                    dumpNode( n, file );
                    file.print( " ");
                    dumpNode( succs[i], file );
                    file.println( "");
                }
            }
            file.println( "Stores:" );
            for( Iterator nIt = pag.storeSources().iterator(); nIt.hasNext(); ) {
                final VarNode n = (VarNode) nIt.next();
                if( n.getReplacement() != n ) continue;
                Node[] succs = pag.storeLookup( n );
                for( int i = 0; i < succs.length; i++ ) {
                    dumpNode( n, file );
                    file.print( " ");
                    dumpNode( succs[i], file );
                    file.println( "");
                }
            }
            if( pag.getOpts().dumpTypes() ) {
                dumpTypes( file );
            }
            file.close();
        } catch( IOException e ) {
            throw new RuntimeException( "Couldn't dump PAG."+e );
        }
    }


    /* End of public methods. */
    /* End of package methods. */

    protected PAG pag;
    protected int fieldNum = 0;
    protected HashMap fieldMap = new HashMap();
    protected FastHierarchy fh;

    protected void dumpTypes( PrintWriter file ) throws IOException {
        HashSet declaredTypes = new HashSet();
        HashSet actualTypes = new HashSet();
        HashSet allFields = new HashSet();
        for( Iterator nIt = pag.allVarNodes().iterator(); nIt.hasNext(); ) {
            final Node n = (Node) nIt.next();
            Type t = n.getType();
            if( t != null ) declaredTypes.add( t );
        }
        for( Iterator nIt = pag.loadSources().iterator(); nIt.hasNext(); ) {
            final Node n = (Node) nIt.next();
            if( n.getReplacement() != n ) continue;
            Type t = n.getType();
            if( t != null ) declaredTypes.add( t );
            allFields.add( ((FieldRefNode) n ).getField() );
        }
        for( Iterator nIt = pag.storeInvSources().iterator(); nIt.hasNext(); ) {
            final Node n = (Node) nIt.next();
            if( n.getReplacement() != n ) continue;
            Type t = n.getType();
            if( t != null ) declaredTypes.add( t );
            allFields.add( ((FieldRefNode) n ).getField() );
        }
        for( Iterator nIt = pag.allocSources().iterator(); nIt.hasNext(); ) {
            final Node n = (Node) nIt.next();
            if( n.getReplacement() != n ) continue;
            Type t = n.getType();
            if( t != null ) actualTypes.add( t );
        }
        HashMap typeToInt = new HashMap();
        int nextint = 1;
        for( Iterator it = declaredTypes.iterator(); it.hasNext(); ) {
            typeToInt.put( it.next(), new Integer( nextint++ ) );
        }
        for( Iterator tIt = actualTypes.iterator(); tIt.hasNext(); ) {
            final Type t = (Type) tIt.next();
            if( !typeToInt.containsKey( t ) ) {
                typeToInt.put( t, new Integer( nextint++ ) );
            }
        }
        file.println( "Declared Types:" );
        for( Iterator declTypeIt = declaredTypes.iterator(); declTypeIt.hasNext(); ) {
            final Type declType = (Type) declTypeIt.next();
            for( Iterator actTypeIt = actualTypes.iterator(); actTypeIt.hasNext(); ) {
                final Type actType = (Type) actTypeIt.next();
                if( fh.canStoreType( actType, declType ) ) {
                    file.println( ""+typeToInt.get( declType )+" "+typeToInt.get( actType ) );
                }
            }
        }
        file.println( "Allocation Types:" );
        for( Iterator nIt = pag.allocSources().iterator(); nIt.hasNext(); ) {
            final Node n = (Node) nIt.next();
            if( n.getReplacement() != n ) continue;
            Type t = n.getType();
            dumpNode( n, file );
            if( t == null ) {
                throw new RuntimeException( "allocnode with null type" );
                //file.println( " 0" );
            } else {
                file.println( " "+typeToInt.get( t ) );
            }
        }
        file.println( "Variable Types:" );
        for( Iterator nIt = pag.allVarNodes().iterator(); nIt.hasNext(); ) {
            final Node n = (Node) nIt.next();
            if( n.getReplacement() != n ) continue;
            Type t = n.getType();
            dumpNode( n, file );
            if( t == null ) {
                file.println( " 0" );
            } else {
                file.println( " "+typeToInt.get( t ) );
            }
        }
    }
    protected int fieldToNum( SparkField f ) {
        Integer ret = (Integer) fieldMap.get( f );
        if( ret == null ) {
            ret = new Integer( ++ fieldNum );
            fieldMap.put( f, ret );
        }
        return ret.intValue();
    }
    protected void dumpNode( Node n, PrintWriter out ) throws IOException {
        if( n.getReplacement() != n ) throw new RuntimeException( "Attempt to dump collapsed node." );
        if( n instanceof FieldRefNode ) {
            FieldRefNode fn = (FieldRefNode) n;
            out.print( ""+fn.getBase().getId()+" "+fieldToNum( fn.getField() ) );
        } else if( pag.getOpts().topoSort() && n instanceof VarNode ) {
            out.print( ""+((VarNode) n).finishingNumber );
        } else {
            out.print( ""+n.getId() );
        }
    }

}

