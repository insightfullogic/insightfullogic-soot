/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2000 Etienne Gagnon.  All rights reserved.
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


package soot.jimple.toolkits.typing.integer;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

class ConstraintCollector extends AbstractStmtSwitch
{
  private TypeResolver resolver;
  private boolean uses;  // if true, include use contraints
  
  private JimpleBody stmtBody;

  public ConstraintCollector(TypeResolver resolver, boolean uses)
  {
    this.resolver = resolver;
    this.uses = uses;
  }

  public void collect(Stmt stmt, JimpleBody stmtBody)
  {
    this.stmtBody = stmtBody;
    stmt.apply(this);
  }
  private void handleInvokeExpr(InvokeExpr ie)
  {
    if(!uses)
      return;

    if(ie instanceof InterfaceInvokeExpr)
      {
	InterfaceInvokeExpr invoke = (InterfaceInvokeExpr) ie;
	SootMethod method = invoke.getMethod();
	int count = invoke.getArgCount();
	
	for(int i = 0; i < count; i++)
	  {
	    if(invoke.getArg(i) instanceof Local)
	      {
		Local local = (Local) invoke.getArg(i);
		
		if(local.getType() instanceof IntegerType)
		  {
		    TypeVariable localType = resolver.typeVariable(local);
		    
		    localType.addParent(resolver.typeVariable(method.getParameterType(i)));
		  }
	      }
	  }
      }
    else if(ie instanceof SpecialInvokeExpr)
      {
	SpecialInvokeExpr invoke = (SpecialInvokeExpr) ie;
	SootMethod method = invoke.getMethod();
	int count = invoke.getArgCount();
	
	for(int i = 0; i < count; i++)
	  {
	    if(invoke.getArg(i) instanceof Local)
	      {
		Local local = (Local) invoke.getArg(i);
		
		if(local.getType() instanceof IntegerType)
		  {
		    TypeVariable localType = resolver.typeVariable(local);
		    
		    localType.addParent(resolver.typeVariable(method.getParameterType(i)));
		  }
	      }
	  }
      }
    else if(ie instanceof VirtualInvokeExpr)
      {
	VirtualInvokeExpr invoke = (VirtualInvokeExpr) ie;
	SootMethod method = invoke.getMethod();
	int count = invoke.getArgCount();
	
	for(int i = 0; i < count; i++)
	  {
	    if(invoke.getArg(i) instanceof Local)
	      {
		Local local = (Local) invoke.getArg(i);
		
		if(local.getType() instanceof IntegerType)
		  {
		    TypeVariable localType = resolver.typeVariable(local);
		    
		    localType.addParent(resolver.typeVariable(method.getParameterType(i)));
		  }
	      }
	  }
      }
    else if(ie instanceof StaticInvokeExpr)
      {
	StaticInvokeExpr invoke = (StaticInvokeExpr) ie;
	SootMethod method = invoke.getMethod();
	int count = invoke.getArgCount();
	
	for(int i = 0; i < count; i++)
	  {
	    if(invoke.getArg(i) instanceof Local)
	      {
		Local local = (Local) invoke.getArg(i);
		
		if(local.getType() instanceof IntegerType)
		  {
		    TypeVariable localType = resolver.typeVariable(local);
		    
		    localType.addParent(resolver.typeVariable(method.getParameterType(i)));
		  }
	      }
	  }
      }
    else
      {
	throw new RuntimeException("Unhandled invoke expression type: " + ie.getClass());
      }
  }

  public void caseBreakpointStmt(BreakpointStmt stmt)
  {
    // Do nothing
  }

  public void caseInvokeStmt(InvokeStmt stmt)
  {
    handleInvokeExpr((InvokeExpr) stmt.getInvokeExpr());
  }

  public void caseAssignStmt(AssignStmt stmt)
  {
    Value l = stmt.getLeftOp();
    Value r = stmt.getRightOp();

    TypeVariable left = null;
    TypeVariable right = null;

    //******** LEFT ********

    if(l instanceof ArrayRef)
      {
	ArrayRef ref = (ArrayRef) l;
	ArrayType base = (ArrayType) ((Local) ref.getBase()).getType();
	Value index = ref.getIndex();
	
	if(uses)
	  {
	    if((base.numDimensions == 1) &&
	       (base.baseType instanceof IntegerType))
	      {
		left = resolver.typeVariable(base.baseType);
	      }
	    
	    if(index instanceof Local)
	      {
		resolver.typeVariable((Local) index).addParent(resolver.INT);
	      }
	  }
      }
    else if(l instanceof Local)
      {
	if(((Local) l).getType() instanceof IntegerType)
	  {
	    left = resolver.typeVariable((Local) l);
	  }
      }
    else if(l instanceof InstanceFieldRef)
      {
	if(uses)
	  {
	    InstanceFieldRef ref = (InstanceFieldRef) l;
	    
	    Type fieldType = ref.getField().getType();
	    
	    if(fieldType instanceof IntegerType)
	      {
		left = resolver.typeVariable(ref.getField().getType());
	      }
	  }
      }
    else if(l instanceof StaticFieldRef)
      {
	if(uses)
	  {
	    StaticFieldRef ref = (StaticFieldRef) l;
	    
	    Type fieldType = ref.getField().getType();
	    
	    if(fieldType instanceof IntegerType)
	      {
		left = resolver.typeVariable(ref.getField().getType());
	      }
	  }
      }
    else
      {
	throw new RuntimeException("Unhandled assignment left hand side type: " + l.getClass());
      }

    //******** RIGHT ********

    if(r instanceof ArrayRef)
      {
	ArrayRef ref = (ArrayRef) r;
	ArrayType base = (ArrayType) ((Local) ref.getBase()).getType();
	Value index = ref.getIndex();
	
	if((base.numDimensions == 1) &&
	   (base.baseType instanceof IntegerType))
	  {
	    right = resolver.typeVariable(base.baseType);
	  }
	
	if(uses)
	  {
	    if(index instanceof Local)
	      {
		resolver.typeVariable((Local) index).addParent(resolver.INT);
	      }
	  }
      }
    else if(r instanceof DoubleConstant)
      {
      }
    else if(r instanceof FloatConstant)
      {
      }
    else if(r instanceof IntConstant)
      {
	int value  = ((IntConstant) r).value;
	
	if(value < -32768)
	  {
	    right = resolver.INT;
	  }
	else if(value < -128)
	  {
	    right = resolver.SHORT;
	  }
	else if(value < 0)
	  {
	    right = resolver.BYTE;
	  }
	else if(value < 2)
	  {
	    right = resolver.R0_1;
	  }
	else if(value < 128)
	  {
	    right = resolver.R0_127;
	  }
	else if(value < 32768)
	  {
	    right = resolver.R0_32767;
	  }
	else if(value < 65536)
	  {
	    right = resolver.CHAR;
	  }
	else
	  {
	    right = resolver.INT;
	  }
      }
    else if(r instanceof LongConstant)
      {
      }
    else if(r instanceof NullConstant)
      {
      }
    else if(r instanceof StringConstant)
      {
      }
    else if(r instanceof BinopExpr)
      {
	//******** BINOP EXPR ********
	
	BinopExpr be = (BinopExpr) r;

	Value lv = be.getOp1();
	Value rv = be.getOp2();
	
	TypeVariable lop = null;
	TypeVariable rop = null;

	//******** LEFT ********
	if(lv instanceof Local)
	  {
	    if(((Local) lv).getType() instanceof IntegerType)
	      {
		lop = resolver.typeVariable((Local) lv);
	      }
	  }
	else if(lv instanceof DoubleConstant)
	  {
	  }
	else if(lv instanceof FloatConstant)
	  {
	  }
	else if(lv instanceof IntConstant)
	  {
	    int value  = ((IntConstant) lv).value;
	    
	    if(value < -32768)
	      {
		lop = resolver.INT;
	      }
	    else if(value < -128)
	      {
		lop = resolver.SHORT;
	      }
	    else if(value < 0)
	      {
		lop = resolver.BYTE;
	      }
	    else if(value < 2)
	      {
		lop = resolver.R0_1;
	      }
	    else if(value < 128)
	      {
		lop = resolver.R0_127;
	      }
	    else if(value < 32768)
	      {
		lop = resolver.R0_32767;
	      }
	    else if(value < 65536)
	      {
		lop = resolver.CHAR;
	      }
	    else
	      {
		lop = resolver.INT;
	      }
	  }
	else if(lv instanceof LongConstant)
	  {
	  }
	else if(lv instanceof NullConstant)
	  {
	  }
	else if(lv instanceof StringConstant)
	  {
	  }
	else
	  {
	    throw new RuntimeException("Unhandled binary expression left operand type: " + lv.getClass());
	  }
	
	//******** RIGHT ********
	if(rv instanceof Local)
	  {
	    if(((Local) rv).getType() instanceof IntegerType)
	      {
		rop = resolver.typeVariable((Local) rv);
	      }
	  }
	else if(rv instanceof DoubleConstant)
	  {
	  }
	else if(rv instanceof FloatConstant)
	  {
	  }
	else if(rv instanceof IntConstant)
	  {
	    int value  = ((IntConstant) rv).value;
	    
	    if(value < -32768)
	      {
		rop = resolver.INT;
	      }
	    else if(value < -128)
	      {
		rop = resolver.SHORT;
	      }
	    else if(value < 0)
	      {
		rop = resolver.BYTE;
	      }
	    else if(value < 2)
	      {
		rop = resolver.R0_1;
	      }
	    else if(value < 128)
	      {
		rop = resolver.R0_127;
	      }
	    else if(value < 32768)
	      {
		rop = resolver.R0_32767;
	      }
	    else if(value < 65536)
	      {
		rop = resolver.CHAR;
	      }
	    else
	      {
		rop = resolver.INT;
	      }
	  }
	else if(rv instanceof LongConstant)
	  {
	  }
	else if(rv instanceof NullConstant)
	  {
	  }
	else if(rv instanceof StringConstant)
	  {
	  }
	else
	  {
	    throw new RuntimeException("Unhandled binary expression right operand type: " + rv.getClass());
	  }
	
	if((be instanceof AddExpr) ||
	   (be instanceof SubExpr) ||
	   (be instanceof DivExpr) ||
	   (be instanceof RemExpr) ||
	   (be instanceof MulExpr))
	  {
	    if(lop != null && rop != null)
	      {
		if(uses)
		  {
		    if(lop.type() == null)
		      {
			lop.addParent(resolver.INT);
		      }
		    
		    if(rop.type() == null)
		      {
			rop.addParent(resolver.INT);
		      }
		  }
		
		right = resolver.INT;
	      }
	  }
	else if((be instanceof AndExpr) ||
		(be instanceof OrExpr) ||
		(be instanceof XorExpr))
	  {
	    if(lop != null && rop != null)
	      {
		TypeVariable common = resolver.typeVariable();
		rop.addParent(common);
		lop.addParent(common);
		
		right = common;
	      }
	  }
	else if(be instanceof ShlExpr)
	  {
	    if(uses)
	      {
		if(lop != null && lop.type() == null)
		  {
		    lop.addParent(resolver.INT);
		  }
		
		if(rop.type() == null)
		  {
		    rop.addParent(resolver.INT);
		  }
	      }
	    
	    right = (lop == null) ? null : resolver.INT;
	  }
	else if((be instanceof ShrExpr) ||
		(be instanceof UshrExpr))
	  {
	    if(uses)
	      {
		if(lop != null && lop.type() == null)
		  {
		    lop.addParent(resolver.INT);
		  }
		
		if(rop.type() == null)
		  {
		    rop.addParent(resolver.INT);
		  }
	      }
	    
	    right = lop;
	  }
	else if((be instanceof CmpExpr) ||
		(be instanceof CmpgExpr) ||
		(be instanceof CmplExpr))
	  {
	    right = resolver.BYTE;
	  }
	else if((be instanceof EqExpr) ||
		(be instanceof GeExpr) ||
		(be instanceof GtExpr) ||
		(be instanceof LeExpr) ||
		(be instanceof LtExpr) ||
		(be instanceof NeExpr))
	  {
	    if(uses)
	      {
		TypeVariable common = resolver.typeVariable();
		rop.addParent(common);
		lop.addParent(common);
	      }
	    
	    right = resolver.BOOLEAN;
	  }
	else
	  {
	    throw new RuntimeException("Unhandled binary expression type: " + be.getClass());
	  }
      }
    else if(r instanceof CastExpr)
      {
	CastExpr ce = (CastExpr) r;

	if(ce.getCastType() instanceof IntegerType)
	  {
	    right = resolver.typeVariable(ce.getCastType());
	  }
      }
    else if(r instanceof InstanceOfExpr)
      {
	InstanceOfExpr ioe = (InstanceOfExpr) r;
	
	right = resolver.BOOLEAN;
      }
    else if(r instanceof InvokeExpr)
      {
	InvokeExpr ie = (InvokeExpr) r;

	handleInvokeExpr(ie);
	
	if(ie.getMethod().getReturnType() instanceof IntegerType)
	  {
	    right = resolver.typeVariable(ie.getMethod().getReturnType());
	  }
      }
    else if(r instanceof NewArrayExpr)
      {
	NewArrayExpr nae = (NewArrayExpr) r;

	if(uses)
	  {
	    Value size = nae.getSize();
	    if(size instanceof Local)
	      {
		TypeVariable var = resolver.typeVariable((Local) size);
		var.addParent(resolver.INT);
	      }
	  }
      }
    else if(r instanceof NewExpr)
      {
      }
    else if(r instanceof NewMultiArrayExpr)
      {
	NewMultiArrayExpr nmae = (NewMultiArrayExpr) r;

	if(uses)
	  {
	    for(int i = 0; i < nmae.getSizeCount(); i++)
	      {
		Value size = nmae.getSize(i);
		if(size instanceof Local)
		  {
		    TypeVariable var = resolver.typeVariable((Local) size);
		    var.addParent(resolver.INT);
		  }
	      }
	  }
      }
    else if(r instanceof LengthExpr)
      {
	LengthExpr le = (LengthExpr) r;
	
	right = resolver.INT;
      }
    else if(r instanceof NegExpr)
      {
	NegExpr ne = (NegExpr) r;

	if(ne.getOp() instanceof Local)
	  {
	    Local local = (Local) ne.getOp();

	    if(local.getType() instanceof IntegerType)
	      {
		if(uses)
		  {
		    resolver.typeVariable(local).addParent(resolver.INT);
		  }
		
		TypeVariable v = resolver.typeVariable();
		v.addChild(resolver.BYTE);
		v.addChild(resolver.typeVariable(local));
		right = v;
	      }
	  }
	else if(ne.getOp() instanceof DoubleConstant)
	  {
	  }
	else if(ne.getOp() instanceof FloatConstant)
	  {
	  }
	else if(ne.getOp() instanceof IntConstant)
	  {
	    int value  = ((IntConstant) ne.getOp()).value;
	    
	    if(value < -32768)
	      {
		right = resolver.INT;
	      }
	    else if(value < -128)
	      {
		right = resolver.SHORT;
	      }
	    else if(value < 0)
	      {
		right = resolver.BYTE;
	      }
	    else if(value < 2)
	      {
		right = resolver.BYTE;
	      }
	    else if(value < 128)
	      {
		right = resolver.BYTE;
	      }
	    else if(value < 32768)
	      {
		right = resolver.SHORT;
	      }
	    else if(value < 65536)
	      {
		right = resolver.INT;
	      }
	    else
	      {
		right = resolver.INT;
	      }
	  }
	else if(ne.getOp() instanceof LongConstant)
	  {
	  }
	else
	  {
	    throw new RuntimeException("Unhandled neg expression operand type: " + ne.getOp().getClass());
	  }
      }
    else if(r instanceof Local)
      {
	Local local = (Local) r;
	
	if(local.getType() instanceof IntegerType)
	  {
	    right = resolver.typeVariable(local);
	  }
      }
    else if(r instanceof InstanceFieldRef)
      {
	InstanceFieldRef ref = (InstanceFieldRef) r;

	if(ref.getField().getType() instanceof IntegerType)
	  {
	    right = resolver.typeVariable(ref.getField().getType());
	  }
      }
    else if(r instanceof StaticFieldRef)
      {
	StaticFieldRef ref = (StaticFieldRef) r;

	if(ref.getField().getType() instanceof IntegerType)
	  {
	    right = resolver.typeVariable(ref.getField().getType());
	  }
      }
    else
      {
	throw new RuntimeException("Unhandled assignment right hand side type: " + r.getClass());
      }

    if(left != null && right != null &&
       (left.type() == null || right.type() == null))
      {
	right.addParent(left);
      }
  }

  public void caseIdentityStmt(IdentityStmt stmt)
  {
    Value l = stmt.getLeftOp();
    Value r = stmt.getRightOp();

    if(l instanceof Local)
      {
	if(((Local) l).getType() instanceof IntegerType)
	  {
	    TypeVariable left = resolver.typeVariable((Local) l);
	    
	    TypeVariable right = resolver.typeVariable(r.getType());
	    right.addParent(left);
	  }
      }
  }

  public void caseEnterMonitorStmt(EnterMonitorStmt stmt)
  {
  }

  public void caseExitMonitorStmt(ExitMonitorStmt stmt)
  {
  }

  public void caseGotoStmt(GotoStmt stmt)
  {
  }

  public void caseIfStmt(IfStmt stmt)
  {
    if(uses)
      {
	ConditionExpr cond = (ConditionExpr) stmt.getCondition();
	
	BinopExpr expr = (BinopExpr) cond;
	Value lv = expr.getOp1();
	Value rv = expr.getOp2();
	
	TypeVariable lop = null;
	TypeVariable rop = null;

	//******** LEFT ********
	if(lv instanceof Local)
	  {
	    if(((Local) lv).getType() instanceof IntegerType)
	      {
		lop = resolver.typeVariable((Local) lv);
	      }
	  }
	else if(lv instanceof DoubleConstant)
	  {
	  }
	else if(lv instanceof FloatConstant)
	  {
	  }
	else if(lv instanceof IntConstant)
	  {
	    int value  = ((IntConstant) lv).value;
	    
	    if(value < -32768)
	      {
		lop = resolver.INT;
	      }
	    else if(value < -128)
	      {
		lop = resolver.SHORT;
	      }
	    else if(value < 0)
	      {
		lop = resolver.BYTE;
	      }
	    else if(value < 2)
	      {
		lop = resolver.R0_1;
	      }
	    else if(value < 128)
	      {
		lop = resolver.R0_127;
	      }
	    else if(value < 32768)
	      {
		lop = resolver.R0_32767;
	      }
	    else if(value < 65536)
	      {
		lop = resolver.CHAR;
	      }
	    else
	      {
		lop = resolver.INT;
	      }
	  }
	else if(lv instanceof LongConstant)
	  {
	  }
	else if(lv instanceof NullConstant)
	  {
	  }
	else if(lv instanceof StringConstant)
	  {
	  }
	else
	  {
	    throw new RuntimeException("Unhandled binary expression left operand type: " + lv.getClass());
	  }
	
	//******** RIGHT ********
	if(rv instanceof Local)
	  {
	    if(((Local) rv).getType() instanceof IntegerType)
	      {
		rop = resolver.typeVariable((Local) rv);
	      }
	  }
	else if(rv instanceof DoubleConstant)
	  {
	  }
	else if(rv instanceof FloatConstant)
	  {
	  }
	else if(rv instanceof IntConstant)
	  {
	    int value  = ((IntConstant) rv).value;
	    
	    if(value < -32768)
	      {
		rop = resolver.INT;
	      }
	    else if(value < -128)
	      {
		rop = resolver.SHORT;
	      }
	    else if(value < 0)
	      {
		rop = resolver.BYTE;
	      }
	    else if(value < 2)
	      {
		rop = resolver.R0_1;
	      }
	    else if(value < 128)
	      {
		rop = resolver.R0_127;
	      }
	    else if(value < 32768)
	      {
		rop = resolver.R0_32767;
	      }
	    else if(value < 65536)
	      {
		rop = resolver.CHAR;
	      }
	    else
	      {
		rop = resolver.INT;
	      }
	  }
	else if(rv instanceof LongConstant)
	  {
	  }
	else if(rv instanceof NullConstant)
	  {
	  }
	else if(rv instanceof StringConstant)
	  {
	  }
	else
	  {
	    throw new RuntimeException("Unhandled binary expression right operand type: " + rv.getClass());
	  }

	if(rop != null && lop != null)
	  {
	    TypeVariable common = resolver.typeVariable();
	    rop.addParent(common);
	    lop.addParent(common);
	  }
      }
  }

  public void caseLookupSwitchStmt(LookupSwitchStmt stmt)
  {
    if(uses)
      {
	Value key = stmt.getKey();

	if(key instanceof Local)
	  {
	    resolver.typeVariable((Local) key).addParent(resolver.INT);
	  }
      }
  }

  public void caseNopStmt(NopStmt stmt)
  {
  }

  public void caseReturnStmt(ReturnStmt stmt)
  {
    if(uses)
      {
	if(stmt.getOp() instanceof Local)
	  {
	    if(((Local) stmt.getOp()).getType() instanceof IntegerType)
	      {
		resolver.typeVariable((Local) stmt.getOp()).
		  addParent(resolver.typeVariable(stmtBody.getMethod().getReturnType()));
	      }
	  }
      }
  }

  public void caseReturnVoidStmt(ReturnVoidStmt stmt)
  {
  }

  public void caseTableSwitchStmt(TableSwitchStmt stmt)
  {
    if(uses)
      {
	Value key = stmt.getKey();
	
	if(key instanceof Local)
	  {
	    resolver.typeVariable((Local) key).addParent(resolver.INT);
	  }
      }
  }

  public void caseThrowStmt(ThrowStmt stmt)
  {
  }

  public void defaultCase(Stmt stmt)
  {
    throw new RuntimeException("Unhandled statement type: " + stmt.getClass());
  }
}
