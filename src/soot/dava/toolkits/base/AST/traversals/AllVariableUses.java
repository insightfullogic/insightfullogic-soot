/* Soot - a J*va Optimization Framework
 * Copyright (C) 2006 Nomair A. Naeem (nomair.naeem@mail.mcgill.ca)
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

/*
 * Maintained by: Nomair A. Naeem
 */

/*
 * CHANGE LOG:  * 30th January 2006, Class created since FinalFieldDefinition wants 
 *                info about uses of a particular field in a method. Writing a general 
 *                analysis which finds all uses of Locals and SootFields
 *
 */




/*
  Need to be very clear when a local can be used
  It can be used in the following places:
  a, a conditional in if, ifelse, while , do while, for condition     TICK
  b, in the for init or update    TICK
  c, in a switch choice           TICK
  d, in a syncrhnoized block       TICK
  d, in a statement                TICK


  Need to be very clear when a SootField can be used
  It can be used in the following places:

  a, NOT used inside a Synchronized Block ........ HOWEVER ADD IT SINCE I DONT SEE WHY THIS RESTRICTION EXISTS!!!  TICK
  b, CAN BE USED in a condition            TICK
  c, CAN BE USED in the for init for update   TICK
  d, CAN BE USED in a switch             TICK
  e, CAN BE USED in a stmt    TICK
*/



package soot.dava.toolkits.base.AST.traversals;

import soot.*;
import java.util.*;
import soot.util.*;
import soot.jimple.*;
import soot.dava.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;
//import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.AST.analysis.*;
//import soot.dava.toolkits.base.AST.structuredAnalysis.*;




public class AllVariableUses extends DepthFirstAdapter{
    ASTMethodNode methodNode;

    //List declaredLocals;
    //Chain declaredFields;

    HashMap localsToUses;
    HashMap fieldsToUses;

    public AllVariableUses(ASTMethodNode node){
	super();
	this.methodNode=node;
	init();
    }

    public AllVariableUses(boolean verbose, ASTMethodNode node){
	super(verbose);
	this.methodNode=node;
	init();
    }

    

    public void init(){

	localsToUses = new HashMap();
	fieldsToUses = new HashMap();

	/*
	 //get all local variables declared in this method
	declaredLocals = methodNode.getDeclaredLocals();

	//if no locals dont bother set to null
	if(declaredLocals.size()==0)
	    declaredLocals=null;
	


	//store all fields


	DavaBody davaBody = methodNode.getDavaBody();
	SootMethod sootMethod = davaBody.getMethod();
	SootClass sootClass = sootMethod.getDeclaringClass();

	declaredFields = sootClass.getFields();
	
	//if no fields dont bother set to null
	if(declaredFields.size()==0)
	    declaredFields=null;
	*/

       
    }



    /*
     * Notice as things stand synchblocks cant have the use of a SootField
     */
    public void inASTSynchronizedBlockNode(ASTSynchronizedBlockNode node){
	Local local = node.getLocal();

	addLocalUse(local,node);
    }




    /*
      The key in a switch stmt can be a local or a SootField or a value
      which can contain Locals or SootFields

      Hence the some what indirect approach
    */
    public void inASTSwitchNode(ASTSwitchNode node){
	Value val = (Value)node.get_Key();
	List localUses = new ArrayList();
	List fieldUses = new ArrayList();

	if(val instanceof Local){
	    localUses.add((Local)val);
	    System.out.println("Added "+val+" to local uses for switch");
	}
	else if(val instanceof FieldRef){
	    fieldUses.add((FieldRef)val);
	    System.out.println("Added "+val+" to field uses for switch");
	}
	else{
	    List useBoxes = val.getUseBoxes();

	    List localsOrFieldRefs= getUsesFromBoxes(useBoxes);
	    Iterator it = localsOrFieldRefs.iterator();

	    while(it.hasNext()){
		Value temp = (Value)it.next();
		if(temp instanceof Local){
		    localUses.add((Local)temp);
		    System.out.println("Added "+temp+" to local uses for switch");
		}
		else if(temp instanceof FieldRef){
		    fieldUses.add((FieldRef)temp);
		    System.out.println("Added "+temp+" to field uses for switch");
		}
	    }
	}

	//localuses stores Locals used
	Iterator it = localUses.iterator();
	while(it.hasNext()){
	    Local local = (Local)it.next();

	    addLocalUse(local,node);
	}//end of going through all locals uses in switch key



	//fieldUses stores FieldRef
	it = fieldUses.iterator();
	while(it.hasNext()){
	   FieldRef field = (FieldRef)it.next();
	   SootField sootField = field.getField();

	    addFieldUse(sootField,node);
	}//end of going through all FieldRef uses in switch key
    }




    public void inASTStatementSequenceNode(ASTStatementSequenceNode node){
	List statements = node.getStatements();
	Iterator it = statements.iterator();
	
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt s = as.get_Stmt();
	    //in the case of stmtts in a stmtt sequence each stmt is considered an entity
	    //compared to the case where these stmts occur within other constructs 
	    //where the node is the entity
	    checkStatementUses(s,s);
	}
    }






    /*
     * The init of a for loop can use a local/Sootfield
     * The condition of a for node can use a local/SootField
     * The update in a for loop can use a local/SootField
     *
     */
    public void inASTForLoopNode(ASTForLoopNode node){

	//checking uses in init
	List init = node.getInit();
	Iterator it = init.iterator();
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt s = as.get_Stmt();
	    checkStatementUses(s,node);
	}

	//checking uses in condition
	ASTCondition cond = node.get_Condition();
	checkConditionalUses(cond,node);

	
	//checking uses in update
	List update = node.getUpdate();
	it = update.iterator();
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt s = as.get_Stmt();
	    checkStatementUses(s,node);
	}
    }














    public void checkStatementUses(Stmt s,Object useNodeOrStatement){
	List useBoxes = s.getUseBoxes();

	//remeber getUsesFromBoxes returns both Locals and FieldRefs
	List uses= getUsesFromBoxes(useBoxes);


	Iterator it = uses.iterator();
	while(it.hasNext()){
	    Value temp = (Value)it.next();
	    if(temp instanceof Local){
		addLocalUse((Local)temp,useNodeOrStatement);
	    }
	    else if(temp instanceof FieldRef){
		FieldRef field = (FieldRef)temp;
		SootField sootField = field.getField();
		addFieldUse(sootField,useNodeOrStatement);
	    }
	}
    }





    /*
     * This method gets a list of all uses of locals/Sootfield in the condition
     * and stores a use by this node
     */
    public void checkConditionalUses(ASTCondition cond,ASTNode node){
	List useList = getUseList(cond);
		
	//System.out.println("FOR NODE with condition:"+cond+"USE list is:"+useList);

	//FOR EACH USE
	Iterator it = useList.iterator();
	while(it.hasNext()){
	    Value temp = (Value)it.next();
	    if(temp instanceof Local){
		addLocalUse((Local)temp,node);
	    }
	    else if(temp instanceof FieldRef){
		FieldRef field = (FieldRef)temp;
		SootField sootField = field.getField();
		addFieldUse(sootField,node);
	    }
	}//end of going through all locals uses in condition
    }





    /*
     * Given a unary/binary or aggregated condition this method is used
     * to find the locals/SootFields used in the condition
     * @param The condition to be checked for Local or FieldRef uses
     * @return a list containing all Locals and FieldRefs used in this condition
     */
    public List getUseList(ASTCondition cond){
	ArrayList useList = new ArrayList();
	if(cond instanceof ASTAggregatedCondition){
	    useList.addAll(getUseList(((ASTAggregatedCondition)cond).getLeftOp()));
	    useList.addAll(getUseList(((ASTAggregatedCondition)cond).getRightOp()));
	    return useList;
	}
	else if(cond instanceof ASTUnaryCondition){
	    //get uses from unary condition
	    List uses = new ArrayList();

	    Value val = ((ASTUnaryCondition)cond).getValue();
	    if(val instanceof Local || val instanceof FieldRef){
		uses.add(val);
	    }
	    else{
		List useBoxes = val.getUseBoxes();
		uses= getUsesFromBoxes(useBoxes);
	    }
	    return uses;
	}
	else if(cond instanceof ASTBinaryCondition){
	    //get uses from binaryCondition
	    Value val = ((ASTBinaryCondition)cond).getConditionExpr();
	    List useBoxes = val.getUseBoxes();
	    return getUsesFromBoxes(useBoxes);
	}
	else{
	    throw new RuntimeException("Method getUseList in ASTUsesAndDefs encountered unknown condition type");
	}
    }












    private void addLocalUse(Local local, Object obj){

	Object temp = localsToUses.get(local);
	List uses;
	if(temp == null)
	    uses = new ArrayList();
	else
	    uses = (ArrayList)temp;

	//add local to useList
	uses.add(obj);

	//update mapping
	localsToUses.put(local,uses);
    }






    private void addFieldUse(SootField field, Object obj){

	Object temp = fieldsToUses.get(field);
	List uses;
	if(temp == null)
	    uses = new ArrayList();
	else
	    uses = (ArrayList)temp;

	// add field to useList
	uses.add(obj);

	//update mapping
	fieldsToUses.put(field,uses);
    }






    /*
     * Method is used to strip away boxes from the actual values
     * only those are returned which are locals or FieldRefs
     */
    private List getUsesFromBoxes(List useBoxes){
	ArrayList toReturn = new ArrayList();
	Iterator it = useBoxes.iterator();
	while(it.hasNext()){
	    Value val =((ValueBox)it.next()).getValue();
	    if(val instanceof Local || val instanceof FieldRef)
		toReturn.add(val);
	}
	//System.out.println("VALUES:"+toReturn);
	return toReturn;
    }





    public List getUsesForField(SootField field){
	Object temp = fieldsToUses.get(field);
	if(temp == null)
	    return null;
	else
	    return (List)temp;
    }





    public List getUsesForLocal(Local local){
	Object temp = localsToUses.get(local);
	if(temp == null)
	    return null;
	else
	    return (List)temp;
    }

}