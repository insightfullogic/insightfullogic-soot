/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Feng Qian
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

package soot.jimple.toolkits.annotation.profiling;

import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.util.*;

import soot.tagkit.*;

import java.util.*;

public class ProfilingGenerator extends BodyTransformer
{
    public ProfilingGenerator( Singletons.Global g ) {}
    public static ProfilingGenerator v() { return G.v().ProfilingGenerator(); }

    public String mainSignature = "void main(java.lang.String[])";

    //    private String mainSignature = "long runBenchmark(java.lang.String[])";

    public String getDeclaredOptions()
    {
	return super.getDeclaredOptions()+" enable notmainentry";
    }

    public void internalTransform(Body body, String phaseName, Map options)
    {
        boolean enable = PackManager.getBoolean(options, "enable");

	if (!enable)
	    return;

	boolean notmainentry = PackManager.getBoolean(options, "notmainentry");
	if (notmainentry)
	    mainSignature = "long runBenchmark(java.lang.String[])";

	{
	    SootMethod m = body.getMethod();

	    SootClass counterClass = Scene.v().loadClassAndSupport("MultiCounter");
	    SootMethod reset = counterClass.getMethod("void reset()") ;
	    SootMethod report = counterClass.getMethod("void report()") ;
	    
	    boolean isMainMethod= m.getSubSignature().equals(mainSignature);
	    
	    Chain units = body.getUnits();

	    if (isMainMethod)
	    {
	        units.addFirst(Jimple.v().newInvokeStmt(
			       Jimple.v().newStaticInvokeExpr(reset)));		
	    }

	    Iterator stmtIt = body.getUnits().snapshotIterator();
	    while (stmtIt.hasNext())
	    {
	        Stmt stmt = (Stmt)stmtIt.next();

		if (stmt instanceof InvokeStmt)
		{
		    InvokeExpr iexpr = (InvokeExpr)
		      ((InvokeStmt)stmt).getInvokeExpr() ;
		
		    if (iexpr instanceof StaticInvokeExpr)
		    {
		        SootMethod tempm = ((StaticInvokeExpr)iexpr).getMethod() ;
			
			if (tempm.getSignature().equals(
				"<java.lang.System: void exit(int)>"))
			{
			    units.insertBefore (Jimple.v().newInvokeStmt( 
				    Jimple.v().newStaticInvokeExpr(report)), stmt) ;

			}
		    }
		}
		else
		if (isMainMethod
		    && (  stmt instanceof ReturnStmt 
			 || stmt instanceof ReturnVoidStmt))
		{
		    units.insertBefore(Jimple.v().newInvokeStmt(
			    Jimple.v().newStaticInvokeExpr(report)), stmt);				 
		}
	    }
	}
    }
}
