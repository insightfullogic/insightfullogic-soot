/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.jimple.toolkits.scalar;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.util.*;
import soot.toolkits.graph.*;
import java.util.*;

public class NopEliminator extends BodyTransformer
{
    public NopEliminator( Singletons.Global g ) {}
    public static NopEliminator v() { return G.v().NopEliminator(); }

    /** Eliminates dead code in a linear fashion.  Complexity is linear 
        with respect to the statements.
    */
    
    protected void internalTransform(Body b, String phaseName, Map options)
    {
        JimpleBody body = (JimpleBody)b;
        
        if(Main.opts.verbose())
            G.v().out.println("[" + body.getMethod().getName() +
                "] Removing nops...");
                
        Chain units = body.getUnits();
        
        // Just do one trivial pass.
        {
            Iterator stmtIt = units.snapshotIterator();
            
            while(stmtIt.hasNext()) 
            {
                Stmt s = (Stmt) stmtIt.next();
                
                if(s instanceof NopStmt)
                    units.remove(s);
            }
        }
    }
}
