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


package soot;

import soot.*;
import java.util.*;
import soot.util.*;
import soot.jimple.*;
import soot.toolkits.graph.*;

/** This interface describes a Statement Printer; implementers must
  * provide a <code>printStatementsInBody</code> method, which writes
  * out a JimpleBody to a PrintWriter.  */
public interface StmtPrinter
{
    /** Prints the given <code>JimpleBody</code> to the specified <code>PrintWriter</code>. */
    public void printStatementsInBody
        (Body body, java.io.PrintWriter out, 
         boolean isPrecise, boolean isNumbered);

    /* Dumps the body to the given PrintWriter in a robust way.
     * Should work even if the body is badly broken! */
    public void printDebugStatementsInBody
        (Body b, java.io.PrintWriter out, boolean isPrecise);
}


