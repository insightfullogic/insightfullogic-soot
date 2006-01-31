/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2004-2006 Nomair A. Naeem
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


package soot.dava;

import soot.*;
import java.util.*;
import soot.util.*;
import soot.grimp.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.jimple.internal.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.SET.*;
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.AST.*;
import soot.dava.toolkits.base.misc.*;
import soot.dava.toolkits.base.finders.*;
import soot.dava.toolkits.base.renamer.*;
import soot.dava.toolkits.base.AST.analysis.*;
import soot.dava.toolkits.base.AST.structuredAnalysis.*;
import soot.dava.toolkits.base.AST.traversals.*;
import soot.dava.toolkits.base.AST.transformations.*;


public class DavaBody extends Body
{
    private Map pMap;
    private HashSet consumedConditions, thisLocals;
    private IterableSet synchronizedBlockFacts, exceptionFacts, monitorFacts, packagesUsed;
    private Local controlLocal;
    private InstanceInvokeExpr constructorExpr; //holds constructorUnit.getInvokeExpr
    private Unit constructorUnit; //holds a stmt (this.init<>)
    private List caughtrefs;

    /**
     *  Construct an empty DavaBody 
     */
             
    DavaBody(SootMethod m)
    {
        super(m);

	pMap = new HashMap();
	consumedConditions = new HashSet();
	thisLocals = new HashSet();
	synchronizedBlockFacts = new IterableSet();
	exceptionFacts = new IterableSet();
	monitorFacts = new IterableSet();
	packagesUsed = new IterableSet();
	caughtrefs = new LinkedList();

	controlLocal = null;
	constructorExpr = null;
    }

    public Unit get_ConstructorUnit()
    {
	return constructorUnit;
    }

    public List get_CaughtRefs()
    {
	return caughtrefs;
    }

    public InstanceInvokeExpr get_ConstructorExpr()
    {
	return constructorExpr;
    }


    public void set_ConstructorExpr(InstanceInvokeExpr expr)
    {
	constructorExpr=expr;
    }

    public void set_ConstructorUnit(Unit s)
    {
	constructorUnit=s;
    }

    public Map get_ParamMap()
    {
	return pMap;
    }


    public void set_ParamMap(Map map)
    {
	pMap=map;
    }

    public HashSet get_ThisLocals()
    {
	return thisLocals;
    }

    public Local get_ControlLocal()
    {
	if (controlLocal == null) {
	    controlLocal = new JimpleLocal( "controlLocal", IntType.v());
	    getLocals().add( controlLocal);
	}

	return controlLocal;
    }

    public Set get_ConsumedConditions()
    {
	return consumedConditions;
    }

    public void consume_Condition( AugmentedStmt as)
    {
	consumedConditions.add( as);
    }

    public Object clone(){
        Body b = Dava.v().newBody(getMethod());
        b.importBodyContentsFrom(this);
        return b;
    }

    public IterableSet get_SynchronizedBlockFacts()
    {
	return synchronizedBlockFacts;
    }

    public IterableSet get_ExceptionFacts()
    {
	return exceptionFacts;
    }

    public IterableSet get_MonitorFacts()
    {
	return monitorFacts;
    }

    public IterableSet get_PackagesUsed()
    {
	return packagesUsed;
    }



    /**
     * Constructs a DavaBody from the given Body.
     */
    
    DavaBody(Body body)
    {
        this( body.getMethod());

	//System.out.println("\n\n\nDecompiling method"+body.getMethod().toString());

	Dava.v().log( "\nstart method " + body.getMethod().toString());

	// copy and "convert" the grimp representation
	copy_Body( body);
	
	// prime the analysis
        AugmentedStmtGraph asg = new AugmentedStmtGraph( new BriefUnitGraph( this), new TrapUnitGraph( this));
	//System.out.println(asg.toString());

	ExceptionFinder.v().preprocess( this, asg);
	SETNode SET = new SETTopNode( asg.get_ChainView());

	while (true) {
	    try {
		CycleFinder.v().find( this, asg, SET);
		IfFinder.v().find( this, asg, SET);
		SwitchFinder.v().find( this, asg, SET);
		SynchronizedBlockFinder.v().find( this, asg, SET);
		ExceptionFinder.v().find( this, asg, SET);
		SequenceFinder.v().find( this, asg, SET);
		LabeledBlockFinder.v().find( this, asg, SET);
		AbruptEdgeFinder.v().find( this, asg, SET);
	    }
	    catch (RetriggerAnalysisException rae) {
		SET = new SETTopNode( asg.get_ChainView());
		consumedConditions = new HashSet();
		continue;
	    }
	    break;
	}

	MonitorConverter.v().convert( this);
	ThrowNullConverter.v().convert( this);



	ASTNode AST = SET.emit_AST();
	
	// get rid of the grimp representation, put in the new AST
	getTraps().clear();
	getUnits().clear();
	getUnits().addLast( AST);

	// perform transformations on the AST	
	do {
	    G.v().ASTAnalysis_modified = false;

	    AST.perform_Analysis( UselessTryRemover.v());
	    // AST.perform( UselessLabeledBlockRemover.v());
	    // AST.perform( UselessBreakRemover.v());

	} while (G.v().ASTAnalysis_modified);


	/*
	  Nomair A Naeem 10-MARCH-2005

	  IT IS ESSENTIAL TO CALL THIS METHOD
	  This method initializes the locals of the current method being processed
	  Failure to invoke this method here will result in no locals being printed out
	*/
	if(AST instanceof ASTMethodNode){
	    ((ASTMethodNode)AST).storeLocals(this);

	    /*
	     * January 12th, 2006
	     * Deal with the super() problem before continuing
	     */
	    AST.apply(new SuperFirstStmtHandler((ASTMethodNode)AST));
	    
	}



	//all analyses should be invoked from within the analyzeAST method
	//the reason being that if superFirstStmtHandler creates a new method
	//you can invoke the analyzeAST method directly on it
	analyzeAST(AST);
	Dava.v().log( "end method " + body.getMethod().toString());
	//System.out.println("\nEND Decompiling method"+body.getMethod().toString());

    }



    public void analyzeAST(ASTNode AST){
	/*
	 * Nomair A. Naeem
	 * tranformations on the AST
	 * Any AST Transformations added should be added to the applyASTAnalyses method
	 * unless we are want to delay the analysis till for example THE LAST THING DONE
	 */
	applyASTAnalyses(AST);
	
	
	/*
	 * Nomair A. Naeem
	 * apply structural flow analyses now
	 *
	 */
	applyStructuralAnalyses(AST);


	/*
	 * Renamer
	 * It might be worthwhile to invoke a method applyRenameAnalyses which could do the analyses and 
	 * subsequenct changes
	 */
	//AST.apply(new infoGatheringAnalysis(this));
	


	/*
	  In the end check 
	  1, if there are labels which can be safely removed
	  2, int temp; temp=0 to be converted to int temp=0;
	*/
	//AST.apply(new ExtraLabelNamesRemover());

    }	


    


    private void applyASTAnalyses(ASTNode AST){
	/*
	  Nomair A. Naeem
	  Transformations on the AST
	*/
	//The BooleanConditionSimplification changes flag==false to just flag
	AST.apply(new BooleanConditionSimplification());
	AST.apply(new VoidReturnRemover(this));
	AST.apply(new DecrementIncrementStmtCreation());


	boolean flag=true;
	int times=0;
	
	if(flag){
	    // perform transformations on the AST	
	    do {
		//System.out.println("ITERATION");
		G.v().ASTTransformations_modified = false;
		times++;
		
		AST.apply(new AndAggregator());
		/*
		  The OrAggregatorOne internally calls UselessLabelFinder which sets the label to null
		  Always apply a UselessLabeledBlockRemover in the end to remove such labeled blocks
		*/
		AST.apply(new OrAggregatorOne());
		
		/*
		  Note OrAggregatorTwo should always be followed by an emptyElseRemover 
		  since orAggregatorTwo can create empty else bodies and the ASTIfElseNode 
		  can be replaced by ASTIfNodes
		  OrAggregator has two patterns see the class for them
		*/
		
		AST.apply(new OrAggregatorTwo());
		AST.apply(new OrAggregatorFour());
		
		
		/*
		  ASTCleaner currently does the following tasks:
		  1, Remove empty Labeled Blocks UselessLabeledBlockRemover
		  2, convert ASTIfElseNodes with empty else bodies to ASTIfNodes
		  3, Apply OrAggregatorThree
		*/
		AST.apply(new ASTCleaner());
		
		
		/*
		  PushLabeledBlockIn should not be called unless we are sure
		  that all labeledblocks have non null labels.
		  A good way of ensuring this is to run the ASTCleaner directly
		  before calling this
		*/
		AST.apply(new PushLabeledBlockIn());
		
		AST.apply(new LoopStrengthener());
		
		
		/*
		  Pattern two carried out in OrAggregatorTwo restricts some patterns in for loop creation.
		  Pattern two was implemented to give loopStrengthening a better chance
		  SEE IfElseBreaker
		*/
		AST.apply(new ASTCleanerTwo());
		
		
		AST.apply(new ForLoopCreator());
		
	    } while (G.v().ASTTransformations_modified);
	    //System.out.println("The AST trasnformations has run"+times);
	}
	
	/*
	  ClosestAbruptTargetFinder should be reinitialized everytime there is a change to the AST
	  This is utilized internally by the DavaFlowSet implementation to handle Abrupt Implicit Stmts
	*/
	AST.apply(ClosestAbruptTargetFinder.v());
	
    }

    private void applyStructuralAnalyses(ASTNode AST){
	//TESTING REACHING DEFS
	//ReachingDefs defs = new ReachingDefs(AST);
	//AST.apply(new tester(true,defs));
	
	//TESTING REACHING COPIES
	//ReachingCopies copies = new ReachingCopies(AST);
	//AST.apply(new tester(true,copies));
	
	//TESTING ASTUSESANDDEFS
	//AST.apply(new ASTUsesAndDefs(AST));
	
	/*
	  Structural flow analyses.....
	*/
	
	CopyPropagation prop = new CopyPropagation(AST);
	AST.apply(prop);

	//copy propagation should be followed by LocalVariableCleaner to get max effect
	AST.apply(new LocalVariableCleaner(AST));
       	
    }




    /*
     * This is the very very very last thing done before outputting Decompiled files
     * Only add here something if there is no way of doing it beforehand
     */

    public void lastAnalyses(){
	ASTNode AST = (ASTNode)this.getUnits().getFirst();

	System.out.println("\nLast analyses for"+this.getMethod().getSubSignature());

	FinalFieldDefinition finalDefinition = new FinalFieldDefinition((ASTMethodNode)AST);
	//29th Jan 2006
	//make sure when recompiling there is no variable might not be initialized error
	//AST.apply(new FinalFieldInitializer());


    }

















    public void write(String temp){
	try{
	    soot.dava.Dava.w.write(temp);
	    soot.dava.Dava.w.flush();
	}catch(Exception e){
	    throw new RuntimeException("exception while writing");
	}
    }






    /*
     *  Copy and patch a GrimpBody so that it can be used to output Java.
     */

    private void copy_Body( Body body)
    {
        if (!(body instanceof GrimpBody))
            throw new RuntimeException("You can only create a DavaBody from a GrimpBody!");

        GrimpBody grimpBody = (GrimpBody) body;

        /*
	 *  Import body contents from Grimp.
	 */
 
        {        
            HashMap bindings = new HashMap();
	    HashMap reverse_binding = new HashMap();
    
            Iterator it = grimpBody.getUnits().iterator();
	    
            // Clone units in body's statement list 
            while (it.hasNext()) {
                Unit original = (Unit) it.next();                                
                Unit copy = (Unit) original.clone();
                
                // Add cloned unit to our unitChain.
                getUnits().addLast(copy);
    
                // Build old <-> new map to be able to patch up references to other units 
                // within the cloned units. (these are still refering to the original
                // unit objects).
                bindings.put(original, copy);
		reverse_binding.put( copy, original);
            }	    

	    // patch up the switch statments
	    it = getUnits().iterator();
	    while (it.hasNext()) {
		Unit u = (Unit) it.next();
		Stmt s = (Stmt)u;
		
		if (s instanceof TableSwitchStmt) {
		    TableSwitchStmt ts = (TableSwitchStmt) s;

		    TableSwitchStmt original_switch = (TableSwitchStmt) reverse_binding.get(u);
		    ts.setDefaultTarget( (Unit) bindings.get( original_switch.getDefaultTarget()));

		    LinkedList new_target_list = new LinkedList();

		    int target_count = ts.getHighIndex() - ts.getLowIndex() + 1;
		    for (int i=0; i<target_count; i++) 
			new_target_list.add( (Unit) bindings.get( original_switch.getTarget( i)));
		    ts.setTargets( new_target_list);

		}
		if (s instanceof LookupSwitchStmt) {
		    LookupSwitchStmt ls = (LookupSwitchStmt) s;

		    LookupSwitchStmt original_switch = (LookupSwitchStmt) reverse_binding.get(u);
		    ls.setDefaultTarget( (Unit) bindings.get( original_switch.getDefaultTarget()));

                    Unit[] new_target_list = new Unit[ original_switch.getTargetCount()];
                    for (int i = 0; i < original_switch.getTargetCount(); i++)
                        new_target_list[i] = (Unit) (bindings.get( original_switch.getTarget(i)));
                    ls.setTargets( new_target_list);

		    ls.setLookupValues( original_switch.getLookupValues());
		}
	    }
    
            // Clone locals.
            it = grimpBody.getLocals().iterator();
            while(it.hasNext()) {
                Local original = (Local) it.next();

                Value copy = Dava.v().newLocal(original.getName(), original.getType());
                
                getLocals().addLast(copy);
    
                // Build old <-> new mapping.
                bindings.put(original, copy);
            }
            
    
            // Patch up references within units using our (old <-> new) map.
            it = getAllUnitBoxes().iterator();
            while(it.hasNext()) {
                UnitBox box = (UnitBox) it.next();
                Unit newObject, oldObject = box.getUnit();
                
                // if we have a reference to an old object, replace it 
                // it's clone.
                if( (newObject = (Unit) bindings.get(oldObject)) != null )
                    box.setUnit(newObject);
            }        
    
            // backpatch all local variables.
            it = getUseAndDefBoxes().iterator();
            while(it.hasNext()) {
                ValueBox vb = (ValueBox) it.next();
                if(vb.getValue() instanceof Local) 
                    vb.setValue((Value) bindings.get(vb.getValue()));
            }
        
	    // clone the traps 
	    Iterator trit = grimpBody.getTraps().iterator();
	    while (trit.hasNext()) {

		Trap originalTrap = (Trap) trit.next();
		Trap cloneTrap    = (Trap) originalTrap.clone();

		Unit handlerUnit = (Unit) bindings.get( originalTrap.getHandlerUnit());

		cloneTrap.setHandlerUnit( handlerUnit);
		cloneTrap.setBeginUnit( (Unit) bindings.get( originalTrap.getBeginUnit()));
		cloneTrap.setEndUnit( (Unit) bindings.get( originalTrap.getEndUnit()));

		getTraps().add( cloneTrap);
	    }
	}

	/*
	 *  Add one level of indirection to "if", "switch", and exceptional control flow.
	 *  This allows for easy handling of breaks, continues and exceptional loops.
	 */
	{
	    PatchingChain units = getUnits();
	    
	    Iterator it = units.snapshotIterator();
	    while (it.hasNext()) {
		Unit u = (Unit) it.next();
		Stmt s = (Stmt) u;

		if (s instanceof IfStmt) {
		    IfStmt ifs = (IfStmt) s;

		    JGotoStmt jgs = new JGotoStmt( (Unit) units.getSuccOf( u));
		    units.insertAfter( jgs, u);

		    JGotoStmt jumper = new JGotoStmt( (Unit) ifs.getTarget());
		    units.insertAfter( jumper, jgs);
		    ifs.setTarget( (Unit) jumper);
		}

		else if (s instanceof TableSwitchStmt) {
		    TableSwitchStmt tss = (TableSwitchStmt) s;

		    int targetCount = tss.getHighIndex() - tss.getLowIndex() + 1;
		    for (int i=0; i<targetCount; i++) {
			JGotoStmt jgs = new JGotoStmt( (Unit) tss.getTarget( i));
			units.insertAfter( jgs, tss);
			tss.setTarget( i, (Unit) jgs);
		    }

		    JGotoStmt jgs = new JGotoStmt( (Unit) tss.getDefaultTarget());
		    units.insertAfter( jgs, tss);
		    tss.setDefaultTarget( (Unit) jgs);
		}

		else if (s instanceof LookupSwitchStmt) {
		    LookupSwitchStmt lss = (LookupSwitchStmt) s;
		    
		    for (int i=0; i<lss.getTargetCount(); i++) {
			JGotoStmt jgs = new JGotoStmt( (Unit) lss.getTarget( i));
			units.insertAfter( jgs, lss);
			lss.setTarget( i, (Unit) jgs);
		    }

		    JGotoStmt jgs = new JGotoStmt( (Unit) lss.getDefaultTarget());
		    units.insertAfter( jgs, lss);
		    lss.setDefaultTarget( (Unit) jgs);
		}
	    }

	    it = getTraps().iterator();
	    while (it.hasNext()) {
		Trap t = (Trap) it.next();
		
		JGotoStmt jgs = new JGotoStmt( (Unit) t.getHandlerUnit());
		units.addLast( jgs);
		t.setHandlerUnit( (Unit) jgs);
	    }
	}


	/*
	 *  Fix up the grimp representations of statements so they can be compiled as java.
	 */

	{
	    Iterator it = getLocals().iterator();
	    while (it.hasNext()) {
		Type t = ((Local) it.next()).getType();

		if (t instanceof RefType) {
		    RefType rt = (RefType) t;

		    addPackage( rt.getSootClass().getJavaPackageName());
		}
	    }

	    it = getUnits().iterator();
	    while (it.hasNext()) {
		Unit u = (Unit) it.next();		
		Stmt s = (Stmt) u;

		if (s instanceof IfStmt)
		    javafy( ((IfStmt) s).getConditionBox());

                else if (s instanceof ThrowStmt)
                    javafy( ((ThrowStmt) s).getOpBox() );

		else if (s instanceof TableSwitchStmt)
		    javafy( ((TableSwitchStmt) s).getKeyBox());

		else if (s instanceof LookupSwitchStmt)
		    javafy( ((LookupSwitchStmt) s).getKeyBox());

		else if (s instanceof MonitorStmt)
		    javafy( ((MonitorStmt) s).getOpBox());

		else if (s instanceof DefinitionStmt) {
		    DefinitionStmt ds = (DefinitionStmt) s;

		    javafy( ds.getRightOpBox());
		    javafy( ds.getLeftOpBox());
		    
		    if (ds.getRightOp() instanceof IntConstant) 
			ds.getRightOpBox().setValue( DIntConstant.v( ((IntConstant) ds.getRightOp()).value, ds.getLeftOp().getType()));
		}

		else if (s instanceof ReturnStmt) {
		    ReturnStmt rs = (ReturnStmt) s;
		    
		    if (rs.getOp() instanceof IntConstant)
			rs.getOpBox().setValue( DIntConstant.v( ((IntConstant) rs.getOp()).value, body.getMethod().getReturnType()));
		    else 
			javafy( rs.getOpBox());
		}

		else if (s instanceof InvokeStmt)
		    javafy( ((InvokeStmt) s).getInvokeExprBox());
	    }
	}


	/*
	 *  Convert references to "this" and parameters.
	 */
	
	{
	    Iterator ucit = getUnits().iterator();
	    while (ucit.hasNext()) {
		Stmt s = (Stmt) ucit.next();
		
		if (s instanceof IdentityStmt) {
		    IdentityStmt ids = (IdentityStmt) s;
		    Value ids_rightOp = ids.getRightOp();
		    Value ids_leftOp  = ids.getLeftOp();
		    
		    if ((ids_leftOp instanceof Local) && (ids_rightOp instanceof ThisRef)) {
			Local thisLocal = (Local) ids_leftOp;
			
			thisLocals.add( thisLocal);
			thisLocal.setName( "this");
		    }
		}

		if (s instanceof DefinitionStmt) {
		    DefinitionStmt ds = (DefinitionStmt) s;
		    Value rightOp = ds.getRightOp();

		    if (rightOp instanceof ParameterRef)
			pMap.put( new Integer( ((ParameterRef) rightOp).getIndex()), ds.getLeftOp());

		    if (rightOp instanceof CaughtExceptionRef)
			caughtrefs.add( ds.getLeftOp());
		}
	    }
	}


	/*
	 *  Fix up the calls to other constructors.  Note, this is seriously underbuilt.
	 */

	{
	    Iterator ucit = getUnits().iterator();
	    while (ucit.hasNext()) {
		Stmt s = (Stmt) ucit.next();

		if (s instanceof InvokeStmt) {
		    
		    InvokeStmt ivs = (InvokeStmt) s;
		    Value ie = ivs.getInvokeExpr();
			
		    if (ie instanceof InstanceInvokeExpr) {
			
			InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
			Value base = iie.getBase();
			
			if ((base instanceof Local) && (((Local) base).getName().equals( "this"))) {
			    SootMethodRef m = iie.getMethodRef();
			    String name = m.name();

			    if ((name.equals( SootMethod.constructorName)) || (name.equals( SootMethod.staticInitializerName))) {

				if (constructorUnit != null)
				    throw new RuntimeException( "More than one candidate for constructor found.");

				constructorExpr = iie;
				constructorUnit = (Unit) s;
			    }
			}
		    }
		}
	    }
	}
    }



    /*
     *  The following set of routines takes care of converting the syntax of single grimp 
     *  statements to java.
     */


    private void javafy( ValueBox vb) 
    {
	Value v = vb.getValue();

	if (v instanceof Expr)
	    javafy_expr( vb);
	else if (v instanceof Ref)
	    javafy_ref( vb);
	else if (v instanceof Local)
	    javafy_local( vb);
	else if (v instanceof Constant)
	    javafy_constant( vb);
    }

    private void javafy_expr( ValueBox vb)
    {
	Expr e = (Expr) vb.getValue();

	if (e instanceof BinopExpr)
	    javafy_binop_expr( vb);
	else if (e instanceof UnopExpr)
	    javafy_unop_expr( vb);
	else if (e instanceof CastExpr)
	    javafy_cast_expr( vb);
	else if (e instanceof NewArrayExpr)
	    javafy_newarray_expr( vb);
	else if (e instanceof NewMultiArrayExpr)
	    javafy_newmultiarray_expr( vb);
	else if (e instanceof InstanceOfExpr)
	    javafy_instanceof_expr( vb);
	else if (e instanceof InvokeExpr)
	    javafy_invoke_expr( vb);
	else if (e instanceof NewExpr)
	    javafy_new_expr( vb);
    }


    private void javafy_ref( ValueBox vb)
    {
	Ref r = (Ref) vb.getValue();

	if (r instanceof StaticFieldRef) {
            SootFieldRef fieldRef = ((StaticFieldRef) r).getFieldRef();
            addPackage(fieldRef.declaringClass().getJavaPackageName());
	    vb.setValue( new DStaticFieldRef( fieldRef, getMethod().getDeclaringClass().getName()));
        } else if (r instanceof ArrayRef) {
	    ArrayRef ar = (ArrayRef) r;

	    javafy( ar.getBaseBox());
	    javafy( ar.getIndexBox());
	}

	else if (r instanceof InstanceFieldRef) {
	    InstanceFieldRef ifr = (InstanceFieldRef) r;

	    javafy( ifr.getBaseBox());

	    vb.setValue( new DInstanceFieldRef( ifr.getBase(), ifr.getFieldRef(), thisLocals));
	}

	else if (r instanceof ThisRef) {
	    ThisRef tr = (ThisRef) r;
	    
	    vb.setValue( new DThisRef( (RefType) tr.getType()));
	}
    }
    
    private void javafy_local( ValueBox vb)
    {
    }
    
    private void javafy_constant( ValueBox vb)
    {
    }


    private void javafy_binop_expr( ValueBox vb)
    {
	BinopExpr boe = (BinopExpr) vb.getValue();

	ValueBox
	    leftOpBox = boe.getOp1Box(),
	    rightOpBox = boe.getOp2Box();
	Value 
	    leftOp = leftOpBox.getValue(),
	    rightOp = rightOpBox.getValue();


	if (rightOp instanceof IntConstant) {
	    if ((leftOp instanceof IntConstant) == false) {
		javafy( leftOpBox);
		leftOp = leftOpBox.getValue();

		if (boe instanceof ConditionExpr)
		    rightOpBox.setValue( DIntConstant.v( ((IntConstant) rightOp).value, leftOp.getType()));
		else
		    rightOpBox.setValue( DIntConstant.v( ((IntConstant) rightOp).value, null));
	    }
	}
	else if (leftOp instanceof IntConstant) {
	    javafy( rightOpBox);
	    rightOp = rightOpBox.getValue();

	    if (boe instanceof ConditionExpr)
		leftOpBox.setValue( DIntConstant.v( ((IntConstant) leftOp).value, rightOp.getType()));
	    else 
		leftOpBox.setValue( DIntConstant.v( ((IntConstant) leftOp).value, null));
	}
	else {
	    javafy( rightOpBox);
	    rightOp = rightOpBox.getValue();

	    javafy( leftOpBox);
	    leftOp = leftOpBox.getValue();
	}

	if (boe instanceof CmpExpr)
	    vb.setValue( new DCmpExpr( leftOp, rightOp));
	
	else if (boe instanceof CmplExpr)
	    vb.setValue( new DCmplExpr( leftOp, rightOp));
	
	else if (boe instanceof CmpgExpr) 
	    vb.setValue( new DCmpgExpr( leftOp, rightOp));
    }

    private void javafy_unop_expr( ValueBox vb)
    {
	UnopExpr uoe = (UnopExpr) vb.getValue();

	javafy( uoe.getOpBox());

	if (uoe instanceof LengthExpr)
	    vb.setValue( new DLengthExpr( ((LengthExpr) uoe).getOp()));
	else if (uoe instanceof NegExpr)
	    vb.setValue( new DNegExpr( ((NegExpr) uoe).getOp()));
    }

    private void javafy_cast_expr( ValueBox vb)
    {
	CastExpr ce = (CastExpr) vb.getValue();

	javafy( ce.getOpBox());
    }

    private void javafy_newarray_expr( ValueBox vb)
    {
	NewArrayExpr nae = (NewArrayExpr) vb.getValue();

	javafy( nae.getSizeBox());
	vb.setValue( new DNewArrayExpr( nae.getBaseType(), nae.getSize()));
    }

    private void javafy_newmultiarray_expr( ValueBox vb)
    {
	NewMultiArrayExpr nmae = (NewMultiArrayExpr) vb.getValue();

	for (int i=0; i<nmae.getSizeCount(); i++)
	    javafy( nmae.getSizeBox( i));

	vb.setValue( new DNewMultiArrayExpr( nmae.getBaseType(), nmae.getSizes()));
    }

    private void javafy_instanceof_expr( ValueBox vb)
    {
	InstanceOfExpr ioe = (InstanceOfExpr) vb.getValue();

	javafy( ioe.getOpBox());	
    }
    
    private void javafy_invoke_expr( ValueBox vb)
    {
	InvokeExpr ie = (InvokeExpr) vb.getValue();

	addPackage( ie.getMethodRef().declaringClass().getJavaPackageName());

	for (int i=0; i<ie.getArgCount(); i++) {
	    Value arg = ie.getArg( i);

	    if (arg instanceof IntConstant) 
		ie.getArgBox( i).setValue( DIntConstant.v( ((IntConstant) arg).value, ie.getMethodRef().parameterType( i)));

	    else 
		javafy( ie.getArgBox( i));
	}

	if (ie instanceof InstanceInvokeExpr) {
	    javafy( ((InstanceInvokeExpr) ie).getBaseBox());

	    if (ie instanceof VirtualInvokeExpr) {
		VirtualInvokeExpr vie = (VirtualInvokeExpr) ie;

		vb.setValue( new DVirtualInvokeExpr( vie.getBase(), vie.getMethodRef(), vie.getArgs(), thisLocals));
	    }
	    
	    else if (ie instanceof SpecialInvokeExpr) {
		SpecialInvokeExpr sie = (SpecialInvokeExpr) ie;
		
		vb.setValue( new DSpecialInvokeExpr( sie.getBase(), sie.getMethodRef(), sie.getArgs()));
	    }
	    
	    else if (ie instanceof InterfaceInvokeExpr) {
		InterfaceInvokeExpr iie = (InterfaceInvokeExpr) ie;
		
		vb.setValue( new DInterfaceInvokeExpr( iie.getBase(), iie.getMethodRef(), iie.getArgs()));
	    }

	    else 
		throw new RuntimeException( "InstanceInvokeExpr " + ie + " not javafied correctly");
	}

	else if (ie instanceof StaticInvokeExpr) {
	    StaticInvokeExpr sie = (StaticInvokeExpr) ie;	    
	    
	    if (sie instanceof NewInvokeExpr) {
		NewInvokeExpr nie = (NewInvokeExpr) sie;
		
		RefType rt = nie.getBaseType();
		addPackage( rt.getSootClass().getJavaPackageName());
		
		vb.setValue( new DNewInvokeExpr( (RefType) nie.getType(), nie.getMethodRef(), nie.getArgs()));
	    }
	    
	    else {
                SootMethodRef methodRef = sie.getMethodRef();
                addPackage(methodRef.declaringClass().getJavaPackageName());
		vb.setValue( new DStaticInvokeExpr( methodRef, sie.getArgs()));
            }
	}

	else 
	    throw new RuntimeException( "InvokeExpr " + ie + " not javafied correctly");
    }

    private void javafy_new_expr( ValueBox vb)
    {
	NewExpr ne = (NewExpr) vb.getValue();

	addPackage( ne.getBaseType().getSootClass().getJavaPackageName());
    }

    public void addPackage( String newPackage)
    {
	if (newPackage.equals( ""))
	    return;

	if (packagesUsed.contains( newPackage) == false)
	    packagesUsed.add( newPackage);
    }


}
