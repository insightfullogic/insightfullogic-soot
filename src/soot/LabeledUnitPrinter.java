/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

/**
* UnitPrinter implementation for representations that have labelled stmts,
* such as Jimple, Grimp, and Baf
*/
public abstract class LabeledUnitPrinter extends AbstractUnitPrinter {
    /** branch targets **/
    protected Map labels;
    /** for unit references in Phi nodes **/
    protected Map references;

    public LabeledUnitPrinter( Body b ) {
        createLabelMaps(b);
    }

    public Map labels() { return labels; }
    public Map references() { return references; }

    public abstract void literal( String s );
    public abstract void method( SootMethod m );
    public abstract void fieldRef( SootField f );
    public abstract void identityRef( IdentityRef r );
    public abstract void type( Type t );

    public void unitRef( Unit u, boolean branchTarget ) {
        String oldIndent = getIndent();
        
        // normal case, ie labels
        if(branchTarget){
            setIndent(labelIndent);
            handleIndent();
            setIndent(oldIndent);
            String label = (String) labels.get( u );
            if( label == null || label.equals( "<unnamed>" ) )
                label = "[?= "+u+"]";
            output.append(label);
        }
        // refs to control flow predecessors (for Shimple)
        else{
            String ref = (String) references.get( u );

            if(startOfLine){
                String newIndent = "(" + ref + ")" +
                    indent.substring(ref.length() + 2);
                setIndent(newIndent);
                handleIndent();
                setIndent(oldIndent);
            }
            else
                output.append(ref);
        }
    }
    
    private void createLabelMaps(Body body) {
        Chain units = body.getUnits();

        labels = new HashMap(units.size() * 2 + 1, 0.7f);
        references = new HashMap(units.size() * 2 + 1, 0.7f);
        
        // Create statement name table
        {
            Iterator boxIt = body.getAllUnitBoxes().iterator();

            Set labelStmts = new HashSet();
            Set refStmts = new HashSet();
            
            // Build labelStmts and refStmts
            {
                while (boxIt.hasNext()) {
                    UnitBox box = (UnitBox) boxIt.next();
                    Unit stmt = (Unit) box.getUnit();

                    if(box.isBranchTarget())
                        labelStmts.add(stmt);
                    else
                        refStmts.add(stmt);
                }

            }

            // Traverse the stmts and assign a label if necessary
            {
                int labelCount = 0;
                int refCount = 0;
                
                Iterator stmtIt = units.iterator();

                while (stmtIt.hasNext()) {
                    Unit s = (Unit) stmtIt.next();

                    if (labelStmts.contains(s)) 
                        labels.put(s, "label" + (labelCount++));

                    if (refStmts.contains(s))
                        references.put(s, Integer.toString(refCount++));
                }
            }
        }
    }

    protected String labelIndent = "     ";
}

