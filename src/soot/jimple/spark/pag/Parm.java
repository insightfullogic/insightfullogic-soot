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
import soot.*;
import soot.jimple.spark.internal.Pair;
import soot.jimple.spark.*;

/** Represents a method parameter.
 * @author Ondrej Lhotak
 */
public class Parm implements SparkField {
    private int index;
    private SootMethod method;
    private static HashMap pairToElement = new HashMap();
    private Parm( SootMethod m, int i ) {
        index = i;
        method = m;
    }
    public static Parm v( SootMethod m, int index ) {
        Pair p = new Pair( m, new Integer(index) );
        Parm ret = (Parm) pairToElement.get( p );
        if( ret == null ) {
            pairToElement.put( p, ret = new Parm( m, index ) );
        }
        return ret;
    }
    public static final void delete() {
        pairToElement = null;
    }
    public String toString() {
        return "Parm "+index+" to "+method;
    }
}
