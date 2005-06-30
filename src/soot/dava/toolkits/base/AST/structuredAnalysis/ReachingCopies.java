/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Nomair A. Naeem
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


package soot.dava.toolkits.base.AST.structuredAnalysis;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.javaRep.*;
import soot.dava.internal.AST.*;



/*
  ReachingCopies
  Step 1:
          Set of pairs where each pair has the form (a,b) indicating a statement a=b
  Step 2:
          A copy statement (a=b) reaches a statement s if all paths leading to s have a copy
	  statemet a=b and the values of a and b are not changed between the copy statement and the statement s.
  Step 3:
          Forward Analysis
  Step 4:
         Intersection
  Step 5:
         x = expr
	 kill = { all pairs containing x in left or right position}
	 
	 if expr is a local , y
	 gen = (x,y) 

  Step 6:
         newInitialFlow: No copies are available. an empty flow set
*/

public class ReachingCopies extends StructuredAnalysis{
    final DavaFlowSet NOPATH = new DavaFlowSet();

    public ReachingCopies(Object analyze){
	super();
	setMergeType();
	DavaFlowSet temp = (DavaFlowSet)process(analyze,new DavaFlowSet());
    }


    public void setMergeType(){
	MERGETYPE=INTERSECTION;
    }

    public Object newInitialFlow(){
	return new DavaFlowSet();
    }
    
    public Object cloneFlowSet(Object flowSet){
	if(flowSet instanceof DavaFlowSet){
	    return ((DavaFlowSet)flowSet).clone();
	}
	else
	    throw new RuntimeException("cloneFlowSet not implemented for other flowSet types");
    }




    /*
     * By construction conditions never have assignment statements.
     * Hence processing a condition has no effect on this analysis
     */
    public Object processUnaryBinaryCondition(ASTUnaryBinaryCondition cond, Object input){
	if(!(input instanceof DavaFlowSet)){
	    throw new RuntimeException("processCondition is not implemented for other flowSet types");
	}
	return input;
    }


    /*
     * By construction the synchronized Local is a Value and can definetly not have an assignment stmt
     * Processing a synch local has no effect on this analysis
     */
    public Object processSynchronizedLocal(Local local,Object input){
	if(!(input instanceof DavaFlowSet)){
	    throw new RuntimeException("processCondition is not implemented for other flowSet types");
	}
	return input;
    }



    /*
     * The switch key is stored as a value and hence can never have an assignment stmt
     * Processing the switch key has no effect on the analysis
     */
    public Object processSwitchKey(Value key,Object input){
	if(!(input instanceof DavaFlowSet)){
	    throw new RuntimeException("processCondition is not implemented for other flowSet types");
	}
	return input;
    }










    /*
     * This method internally invoked by the process method decides which Statement
     * specialized method to call
     */
    public Object processStatement(Stmt s, Object input){
	if(!(input instanceof DavaFlowSet)){
	    throw new RuntimeException("processStatement is not implemented for other flowSet types");
	}
	DavaFlowSet inSet = (DavaFlowSet)input;

	/*
	  If this path will not be taken return no path straightaway
	*/
	if(inSet == NOPATH){
	    return inSet;
	}

	if(s instanceof DefinitionStmt){
	    DavaFlowSet toReturn = (DavaFlowSet)cloneFlowSet(inSet);
	    // x = expr;
	    //check if expr is a local in which case this is a copy
	    Value leftOp = ((DefinitionStmt)s).getLeftOp();
	    Value rightOp = ((DefinitionStmt)s).getRightOp();


	    if(leftOp instanceof Local){
		// KILL any available copy with local since it has been redefined
		kill(toReturn,(Local)leftOp);
	    }//leftop is a local


	    if(leftOp instanceof Local && rightOp instanceof Local){
		//this is a copy statement
		//  GEN
		gen(toReturn,(Local)leftOp,(Local)rightOp);
	    }
	    return toReturn;
	}
	else if(s instanceof DAbruptStmt){
	    DAbruptStmt abStmt = (DAbruptStmt)s;

	    //see if its a break or continue
	    if(!(abStmt.is_Continue()|| abStmt.is_Break())){
		//DAbruptStmt is of only two kinds
		throw new RuntimeException("Found a DAbruptStmt which is neither break nor continue!!");
	    }		    
		
	    String label = abStmt.getLabel().toString();
	    DavaFlowSet temp = NOPATH;
		
	    if(abStmt.is_Continue()){
		//System.out.println("invoking addtocontinueList");
		temp.addToContinueList(label,inSet);
		//System.out.println("addtocontinueList returned");
	    }
	    else if (abStmt.is_Break()){
		//System.out.println("invoking addtobreakList");
		temp.addToBreakList(label,inSet);
		//System.out.println("addtobreakList returned");
	    }
	    return temp;
	}
	else{
	    /*
	      Analysis cares only about break and continue stmts along with DEFINTION STMT
	    */
	    return input;
	}
    }


    public void gen(DavaFlowSet in, Local left, Local right){
	LocalPair localp = new LocalPair(left,right);
	//System.out.println("Generating "+localp.toString());
	in.add(localp);
    }
    
    public void kill(DavaFlowSet in, Local redefined){
	// kill any previous localpairs which have the redefined Local in the left OR right position
	List list = in.toList();
	Iterator listIt = list.iterator();
	while(listIt.hasNext()){
	    LocalPair tempPair = (LocalPair)listIt.next();
	    if(tempPair.contains(redefined)){
		//need to kill this from the list
		//System.out.println("Killing "+tempPair.toString());
		in.remove(tempPair);
	    }
	}
    }

    /*
     * Wrapper method to get before set of an ASTNode or Statement
     * which gives us the reaching copies at this point
     */
    public DavaFlowSet getReachingCopies(Object node){
	//get the before set for this node
	Object beforeSet=getBeforeSet(node);

	if(beforeSet == null){
	    throw new RuntimeException("Could not get reaching copies of node/stmt");
	}
	if(!(beforeSet instanceof DavaFlowSet)){
	    throw new RuntimeException("Reaching def set is not a Dava FlowSet");
	}

	DavaFlowSet beforeSetReaching = (DavaFlowSet)beforeSet;
	//Get all reachingCopies

	/* the list that toList of this object contains elements of 
	 * type LocalPair (a,b) which means this is a copy
	 * stmt of the form a=b
	 */

	return beforeSetReaching;
    }

}