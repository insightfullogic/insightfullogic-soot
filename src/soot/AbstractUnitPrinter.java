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
import java.util.*;

/**
* Partial default UnitPrinter implementation.
*/
public abstract class AbstractUnitPrinter implements UnitPrinter {
    public void setPositionTagger( AttributesUnitPrinter pt ) {
        this.pt = pt;
        pt.setUnitPrinter( this );
    }
    public AttributesUnitPrinter getPositionTagger() {
        return pt;
    }
    public void startUnit( Unit u ) {
		handleIndent();
		if( pt != null ) pt.startUnit( u );
	}
    public void endUnit( Unit u ) {
		if( pt != null ) pt.endUnit( u );
	}
    public void startUnitBox( UnitBox ub ) { handleIndent(); }
    public void endUnitBox( UnitBox ub ) {}
    public void startValueBox( ValueBox vb ) {
        handleIndent();
        if( pt != null ) pt.startValueBox( vb );
    }
    public void endValueBox( ValueBox vb ) {
        if( pt != null ) pt.endValueBox( vb );
    }

    public void noIndent() { startOfLine = false; }
    public void incIndent() { indent = indent + "    "; }
    public void decIndent() {
        if( indent.length() >= 4 ) indent = indent.substring(4);
    }
    public abstract void literal( String s );
    public abstract void type( Type t ); 
    public abstract void method( SootMethod m );
    public abstract void fieldRef( SootField f );
    public abstract void identityRef( IdentityRef r );
    public abstract void unitRef( Unit u );

    public void newline() {
        output.append("\n");
        startOfLine = true;
        if( pt != null ) pt.newline();
    }
    public void local( Local l ) { 
        handleIndent();
        output.append( l.getName() );
    }
    public void constant( Constant c ) {
        handleIndent();
        output.append( c.toString() );
    }
    public String toString() {
        String ret = output.toString();
        output = new StringBuffer();
        return ret;
    }
    public StringBuffer output() {
        return output;
    }

    protected void handleIndent() {
        if( startOfLine ) output.append( indent );
        startOfLine = false;
    }

    protected boolean startOfLine = true;
    protected String indent = "        ";
    protected StringBuffer output = new StringBuffer();
    protected AttributesUnitPrinter pt;
}

