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
 * License along with cl library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot;
import soot.jimple.*;
import java.util.*;

/**
* UnitPrinter implementation for normal (full) Jimple, Grimp, and Baf
*/
public class BriefUnitPrinter extends NormalUnitPrinter {
    BriefUnitPrinter( Map labels, String indent ) {
        super(labels, indent);
    }

    private boolean baf;
    public void startUnit( Unit u ) {
        super.startUnit(u);
        if( u instanceof Stmt ) {
            baf = false;
        } else {
            baf = true;
        }
    }

    public void method( SootMethod m ) {
        if( !baf && m.isStatic() ){
            output.append( m.getDeclaringClass().getName() );
            literal(".");
        }
        output.append( m.getName() );
    }
    public void fieldRef( SootField f ) { 
        if( baf || f.isStatic() ){
            output.append( f.getDeclaringClass().getName() );
            literal(".");
        }
        output.append(f.getName());
    }
    public void identityRef( IdentityRef r ) {
        if( r instanceof ThisRef ) {
            literal("@this");
        } else if( r instanceof ParameterRef ) {
            ParameterRef pr = (ParameterRef) r;
            literal("@parameter"+pr.getIndex());
        } else if( r instanceof CaughtExceptionRef ) {
            literal("@caughtexception");
        } else throw new RuntimeException();
    }

    private boolean eatSpace = false;
    public void literal( String s ) {
        if( eatSpace && s.equals(" ") ) {
            eatSpace = false;
            return;
        }
        eatSpace = false;
        if( !baf ) {
            if( false
            ||  s.equals( Jimple.v().STATICINVOKE )
            ||  s.equals( Jimple.v().VIRTUALINVOKE )
            ||  s.equals( Jimple.v().INTERFACEINVOKE )
              ) {
                eatSpace = true;
                return;
            }
        }
        super.literal(s);
    }
}


