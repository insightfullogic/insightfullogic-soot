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


package soot.jimple.toolkits.typing;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import java.io.*;

/**
 * This class resolves the type of local variables.
 **/
public class TypeResolver
{
  /** Reference to the class hierarchy **/
  private ClassHierarchy hierarchy;

  /** All type variable instances **/
  private final List typeVariableList = new ArrayList();

  /** Hashtable: [TypeNode or Local] -> TypeVariable **/
  private final Map typeVariableMap = new HashMap();

  private final JimpleBody stmtBody;

  final TypeNode NULL;
  private final TypeNode OBJECT;

  private static final boolean DEBUG = false;
  // flag for J2ME library
  private final static boolean J2ME = soot.Main.isJ2ME();


  // categories for type variables (solved = hard, unsolved = soft)
  private List unsolved;
  private List solved;

  // parent categories for unsolved type variables
  private List single_soft_parent;
  private List single_hard_parent;
  private List multiple_parents;

  // child categories for unsolved type variables
  private List single_child_not_null;
  private List single_null_child;
  private List multiple_children;

  public ClassHierarchy hierarchy()
  {
    return hierarchy;
  }

  public TypeNode typeNode(Type type)
  {
    return hierarchy.typeNode(type);
  }

  /** Get type variable for the given local. **/
  TypeVariable typeVariable(Local local)
  {
    TypeVariable result = (TypeVariable) typeVariableMap.get(local);

    if(result == null)
      {
	int id = typeVariableList.size();
	typeVariableList.add(null);

	result = new TypeVariable(id, this);

	typeVariableList.set(id, result);
	typeVariableMap.put(local, result);
	
	if(DEBUG)
	  {
	    System.out.println("[LOCAL VARIABLE \"" + local + "\" -> " + id + "]");
	  }
      }
    
    return result;
  }

  /** Get type variable for the given type node. **/
  public TypeVariable typeVariable(TypeNode typeNode)
  {
    TypeVariable result = (TypeVariable) typeVariableMap.get(typeNode);

    if(result == null)
      {
	int id = typeVariableList.size();
	typeVariableList.add(null);

	result = new TypeVariable(id, this, typeNode);

	typeVariableList.set(id, result);
	typeVariableMap.put(typeNode, result);
      }

    return result;
  }

  /** Get type variable for the given soot class. **/
  public TypeVariable typeVariable(SootClass sootClass)
  {
    return typeVariable(hierarchy.typeNode(sootClass.getType()));
  }

  /** Get type variable for the given type. **/
  public TypeVariable typeVariable(Type type)
  {
    return typeVariable(hierarchy.typeNode(type));
  }

  /** Get new type variable **/
  public TypeVariable typeVariable()
  {
    int id = typeVariableList.size();
    typeVariableList.add(null);
    
    TypeVariable result = new TypeVariable(id, this);
    
    typeVariableList.set(id, result);
    
    return result;
  }

  private TypeResolver(JimpleBody stmtBody, Scene scene)
  {
    this.stmtBody = stmtBody;
    hierarchy = ClassHierarchy.classHierarchy(scene);

    OBJECT = hierarchy.OBJECT;
    NULL = hierarchy.NULL;
    typeVariable(OBJECT);
    typeVariable(NULL);
    
    // hack for J2ME library, reported by Stephen Cheng 
    if (!J2ME) {
      typeVariable(hierarchy.CLONEABLE);
      typeVariable(hierarchy.SERIALIZABLE);
    }
  }

  public static void resolve(JimpleBody stmtBody, Scene scene)
  {
    if(DEBUG)
      {
	System.out.println(stmtBody.getMethod());
      }

    try
      {
	TypeResolver resolver = new TypeResolver(stmtBody, scene);
	resolver.resolve_step_1();
      }
    catch(TypeException e1)
      {
	if(DEBUG)
	  {
	    e1.printStackTrace();
	    System.out.println("Step 1 Exception-->" + e1.getMessage());
	  }
	
	try
	  {
	    TypeResolver resolver = new TypeResolver(stmtBody, scene);
	    resolver.resolve_step_2();
	  }
	catch(TypeException e2)
	  {
	    if(DEBUG)
	      {
		e2.printStackTrace();
		System.out.println("Step 2 Exception-->" + e2.getMessage());
	      }
	
	    try
	    {
	      TypeResolver resolver = new TypeResolver(stmtBody, scene);
	      resolver.resolve_step_3();
	    }
	    catch(TypeException e3)
	    {
	      StringWriter st = new StringWriter();
	      PrintWriter pw = new PrintWriter(st);
	      e3.printStackTrace(pw);
	      pw.close();
	      throw new RuntimeException(st.toString());
	    }
	  }
      }

    soot.jimple.toolkits.typing.integer.TypeResolver.resolve(stmtBody);
  }
  
  private void debug_vars(String message)
  {
    if(DEBUG)
      {
	int count = 0;
	System.out.println("**** START:" + message);
	for(Iterator i = typeVariableList.iterator(); i.hasNext(); )
	  {
	    TypeVariable var = (TypeVariable) i.next();
	    System.out.println(count++ + " " + var);
	  }
	System.out.println("**** END:" + message);
      }
  }

  private void debug_body()
  {
    if(DEBUG)
      {
	System.out.println("-- Body Start --");
	for(Iterator i = stmtBody.getUnits().iterator(); i.hasNext();)
	  {
	    Stmt stmt = (Stmt) i.next();
	    System.out.println(stmt);
	  }
	System.out.println("-- Body End --");
      }
  }

  private void resolve_step_1() throws TypeException
  {
    //    remove_spurious_locals();

    collect_constraints_1_2();
    debug_vars("constraints");

    compute_array_depth();
    propagate_array_constraints();
    debug_vars("arrays");

    merge_primitive_types();
    debug_vars("primitive");

    merge_connected_components();
    debug_vars("components");

    remove_transitive_constraints();
    debug_vars("transitive");

    merge_single_constraints();
    debug_vars("single");

    assign_types_1_2();
    debug_vars("assign");

    check_constraints();
  }

  private void resolve_step_2() throws TypeException
  {
    debug_body();
    split_new();
    debug_body();

    collect_constraints_1_2();
    debug_vars("constraints");

    compute_array_depth();
    propagate_array_constraints();
    debug_vars("arrays");

    merge_primitive_types();
    debug_vars("primitive");

    merge_connected_components();
    debug_vars("components");

    remove_transitive_constraints();
    debug_vars("transitive");

    merge_single_constraints();
    debug_vars("single");

    assign_types_1_2();
    debug_vars("assign");

    check_constraints();
  }

  private void resolve_step_3() throws TypeException
  {
    collect_constraints_3();
    compute_approximate_types();
    assign_types_3();
    check_and_fix_constraints();
  }

  private void collect_constraints_1_2()
  {
    ConstraintCollector collector = new ConstraintCollector(this, true);

    for(Iterator i = stmtBody.getUnits().iterator(); i.hasNext();)
      {
	Stmt stmt = (Stmt) i.next();
	if(DEBUG)
	  {
	    System.out.print("stmt: ");
	  }
	collector.collect(stmt, stmtBody);
	if(DEBUG)
	  {
	    System.out.println(stmt);
	  }
      }
  }

  private void collect_constraints_3()
  {
    ConstraintCollector collector = new ConstraintCollector(this, false);

    for(Iterator i = stmtBody.getUnits().iterator(); i.hasNext();)
      {
	Stmt stmt = (Stmt) i.next();
	if(DEBUG)
	  {
	    System.out.print("stmt: ");
	  }
	collector.collect(stmt, stmtBody);
	if(DEBUG)
	  {
	    System.out.println(stmt);
	  }
      }
  }

  private void compute_array_depth() throws TypeException
  {
    compute_approximate_types();

    TypeVariable[] vars = new TypeVariable[typeVariableList.size()];
    vars = (TypeVariable[]) typeVariableList.toArray(vars);

    for(int i = 0; i < vars.length; i++)
      {
	vars[i].fixDepth();
      }
  }

  private void propagate_array_constraints()
  {
    // find max depth
    int max = 0;
    for(Iterator i = typeVariableList.iterator(); i.hasNext(); )
      {
	TypeVariable var = (TypeVariable) i.next();
	int depth = var.depth();

	if(depth > max)
	  {
	    max = depth;
	  }
      }

    if(max > 1) {
      // hack for J2ME library, reported by Stephen Cheng 
      if (!J2ME) {
	typeVariable(ArrayType.v(RefType.v("java.lang.Cloneable"), max - 1));
	typeVariable(ArrayType.v(RefType.v("java.io.Serializable"), max - 1));
      }
    }

    // create lists for each array depth
    LinkedList[] lists = new LinkedList[max + 1];
    for(int i = 0; i <= max; i++)
      {
	lists[i] = new LinkedList();
      }

    // initialize lists
    for(Iterator i = typeVariableList.iterator(); i.hasNext(); )
      {
	TypeVariable var = (TypeVariable) i.next();
	int depth = var.depth();
	
	lists[depth].add(var);
      }

    // propagate constraints, starting with highest depth
    for(int i = max; i >= 0; i--)
      {
	for(Iterator j = typeVariableList.iterator(); j.hasNext(); )
	  {
	    TypeVariable var = (TypeVariable) j.next();

	    var.propagate();
	  }
      }
  }

  private void merge_primitive_types() throws TypeException
  {
    // merge primitive types with all parents/children
    compute_solved();

    for(Iterator i = solved.iterator(); i.hasNext();)
      {
	TypeVariable var = (TypeVariable) i.next();

	if(var.type().type() instanceof IntType ||
	   var.type().type() instanceof LongType ||
	   var.type().type() instanceof FloatType ||
	   var.type().type() instanceof DoubleType)
	  {
	    List parents;
	    List children;
	    boolean finished;

	    do
	      {
		finished = true;

		parents = var.parents();
		if(parents.size() != 0)
		  {
		    finished = false;
		    for(Iterator j = parents.iterator(); j.hasNext(); )
		      {
			if(DEBUG)
			  {
			    System.out.print(".");
			  }
	
			TypeVariable parent = (TypeVariable) j.next();

			var = var.union(parent);
		      }
		  }
		
		children = var.children();
		if(children.size() != 0)
		  {
		    finished = false;
		    for(Iterator j = children.iterator(); j.hasNext(); )
		      {
			if(DEBUG)
			  {
			    System.out.print(".");
			  }
	
			TypeVariable child = (TypeVariable) j.next();

			var = var.union(child);
		      }
		  }
	      }
	    while(!finished);
	  }
      }
  }

  private void merge_connected_components() throws TypeException
  {
    refresh_solved();
    List list = new LinkedList();
    list.addAll(solved);
    list.addAll(unsolved);
    
    StronglyConnectedComponents.merge(list);
  }
  
  private void remove_transitive_constraints() throws TypeException
  {
    refresh_solved();
    List list = new LinkedList();
    list.addAll(solved);
    list.addAll(unsolved);

    for(Iterator i = list.iterator(); i.hasNext(); )
      {
	TypeVariable var = (TypeVariable) i.next();
	
	var.removeIndirectRelations();
      }
  }

  private void merge_single_constraints() throws TypeException
  {
    boolean finished = false;
    boolean modified = false;
    while(true)
      {
	categorize();
	
	if(single_child_not_null.size() != 0)
	  {
	    finished = false;
	    modified = true;
	    
	    for(Iterator i = single_child_not_null.iterator(); i.hasNext(); )
	      {
		TypeVariable var = (TypeVariable) i.next();

		if(single_child_not_null.contains(var))
		  {
		    TypeVariable child = (TypeVariable) var.children().get(0);
		
		    var = var.union(child);
		  }
	      }
	  }

	if(finished)
	  {
	    if(single_soft_parent.size() != 0)
	      {
		finished = false;
		modified = true;
		
		for(Iterator i = single_soft_parent.iterator(); i.hasNext(); )
		  {
		    TypeVariable var = (TypeVariable) i.next();
		    
		    if(single_soft_parent.contains(var))
		      {
			TypeVariable parent = (TypeVariable) var.parents().get(0);
			
			var = var.union(parent);
		      }
		  }
	      }
	    
	    if(single_hard_parent.size() != 0)
	      {
		finished = false;
		modified = true;
		
		for(Iterator i = single_hard_parent.iterator(); i.hasNext(); )
		  {
		    TypeVariable var = (TypeVariable) i.next();
		    
		    if(single_hard_parent.contains(var))
		      {
			TypeVariable parent = (TypeVariable) var.parents().get(0);
			
			debug_vars("union single parent\n " + var + "\n " + parent);
			var = var.union(parent);
		      }
		  }
	      }

	    if(single_null_child.size() != 0)
	      {
		finished = false;
		modified = true;
		
		for(Iterator i = single_null_child.iterator(); i.hasNext(); )
		  {
		    TypeVariable var = (TypeVariable) i.next();
		    
		    if(single_null_child.contains(var))
		      {
			TypeVariable child = (TypeVariable) var.children().get(0);
			
			var = var.union(child);
		      }
		  }
	      }
	    
	    if(finished)
	      {
		break;
	      }
	    
	    continue;
	  }
	
	if(modified)
	  {
	    modified = false;
	    continue;
	  }

	finished = true;
	
      multiple_children:
	for(Iterator i = multiple_children.iterator(); i.hasNext(); )
	  {
	    TypeVariable var = (TypeVariable) i.next();
	    TypeNode lca = null;
	    List children_to_remove = new LinkedList();
	    
	    var.fixChildren();
	    
	    for(Iterator j = var.children().iterator(); j.hasNext(); )
	      {
		TypeVariable child = (TypeVariable) j.next();
		TypeNode type = child.type();

		if(type != null && type.isNull())
		  {
		    var.removeChild(child);
		  }
		else if(type != null && type.isClass())
		  {
		    children_to_remove.add(child);
		    
		    if(lca == null)
		      {
			lca = type;
		      }
		    else
		      {
			lca = lca.lcaIfUnique(type);

			if(lca == null)
			  {
			    if(DEBUG)
			      {
				System.out.println
				  ("==++==" +
				   stmtBody.getMethod().getDeclaringClass().getName() + "." + 
				   stmtBody.getMethod().getName());
			      }
			    
			    continue multiple_children;
			  }
		      }
		  }
	      }
	    
	    if(lca != null)
	      {
		for(Iterator j = children_to_remove.iterator(); j.hasNext();)
		  {
		    TypeVariable child = (TypeVariable) j.next();
		    var.removeChild(child);
		  }

		var.addChild(typeVariable(lca));
	      }
	  }
	
	for(Iterator i = multiple_parents.iterator(); i.hasNext(); )
	  {
	    TypeVariable var = (TypeVariable) i.next();
	    LinkedList hp = new LinkedList(); // hard parents
	    
	    var.fixParents();
	    
	    for(Iterator j = var.parents().iterator(); j.hasNext(); )
	      {
		TypeVariable parent = (TypeVariable) j.next();
		TypeNode type = parent.type();
		
		if(type != null)
		  {
		    for(Iterator k = hp.iterator(); k.hasNext();)
		      {
			TypeVariable otherparent = (TypeVariable) k.next();
			TypeNode othertype = otherparent.type();
			
			if(type.hasDescendant(othertype))
			  {
			    var.removeParent(parent);
			    type = null;
			    break;
			  }
			
			if(type.hasAncestor(othertype))
			  {
			    var.removeParent(otherparent);
			    k.remove();
			  }
		      }
		    
		    if(type != null)
		      {
			hp.add(parent);
		      }
		  }
	      }
	  }
      }
  }

  private void assign_types_1_2() throws TypeException
  {
    for(Iterator i = stmtBody.getLocals().iterator(); i.hasNext(); )
      {
	Local local = (Local) i.next();
	TypeVariable var = typeVariable(local);
	
	if(var == null)
	  {
	    local.setType(RefType.v("java.lang.Object"));
	  }
	else if (var.depth() == 0)
	  {
	    if(var.type() == null)
	      {
		TypeVariable.error("Type Error(5):  Variable without type");
	      }
	    else
	      {
		local.setType(var.type().type());
	      }
	  }
	else
	  {
	    TypeVariable element = var.element();
	    
	    for(int j = 1; j < var.depth(); j++)
	      {
		element = element.element();
	      }

	    if(element.type() == null)
	      {
		TypeVariable.error("Type Error(6):  Array variable without base type");
	      }
	    else if(element.type().type() instanceof NullType)
	      {
		local.setType(NullType.v());
	      }
	    else
	      {
		Type t = element.type().type();
		if(t instanceof IntType)
		  {
		    local.setType(var.approx().type());
		  }
		else
		  {
		    local.setType(ArrayType.v(t, var.depth()));
		  }
	      }
	  }
	
	if(DEBUG)
	  {
	    if((var != null) &&
	       (var.approx() != null) &&
	       (var.approx().type() != null) &&
	       (local != null) &&
	       (local.getType() != null) &&
	       !local.getType().equals(var.approx().type()))
	      {
		System.out.println("local: " + local + ", type: " + local.getType() + ", approx: " + var.approx().type());
	      }
	  }
      }
  }

  private void assign_types_3() throws TypeException
  {
    for(Iterator i = stmtBody.getLocals().iterator(); i.hasNext(); )
      {
	Local local = (Local) i.next();
	TypeVariable var = typeVariable(local);
	
	if(var == null ||
	   var.approx() == null ||
	   var.approx().type() == null)
	  {
	    local.setType(RefType.v("java.lang.Object"));
	  }
	else
	  {
	    local.setType(var.approx().type());
	  }
      }
  }

  private void check_constraints() throws TypeException
  {
    ConstraintChecker checker = new ConstraintChecker(this, false);
    StringBuffer s = null;

    if(DEBUG)
      {
	s = new StringBuffer("Checking:\n");
      }

    for(Iterator i = stmtBody.getUnits().iterator(); i.hasNext();)
      {
	Stmt stmt = (Stmt) i.next();
	if(DEBUG)
	  {
	    s.append(" " + stmt + "\n");
	  }
	try
	  {
	    checker.check(stmt, stmtBody);
	  }
	catch(TypeException e)
	  {
	    if(DEBUG)
	      {
		System.out.println(s);
	      }
	    throw e;
	  }
      }
  }

  private void check_and_fix_constraints() throws TypeException
  {
    ConstraintChecker checker = new ConstraintChecker(this, true);
    StringBuffer s = null;
    PatchingChain units = stmtBody.getUnits();
    Stmt[] stmts = new Stmt[units.size()];
    units.toArray(stmts);

    if(DEBUG)
      {
	s = new StringBuffer("Checking:\n");
      }

    for(int i = 0; i < stmts.length; i++)
      {
	Stmt stmt = stmts[i];

	if(DEBUG)
	  {
	    s.append(" " + stmt + "\n");
	  }
	try
	  {
	    checker.check(stmt, stmtBody);
	  }
	catch(TypeException e)
	  {
	    if(DEBUG)
	      {
		System.out.println(s);
	      }
	    throw e;
	  }
      }
  }

  private void compute_approximate_types() throws TypeException
  {
    TreeSet workList = new TreeSet();

    for(Iterator i = typeVariableList.iterator(); i.hasNext(); )
      {
	TypeVariable var = (TypeVariable) i.next();

	if(var.type() != null)
	  {
	    workList.add(var);
	  }
      }

    TypeVariable.computeApprox(workList);

    for(Iterator i = typeVariableList.iterator(); i.hasNext(); )
      {
	TypeVariable var = (TypeVariable) i.next();

	if(var.approx() == NULL)
	  {
	    var.union(typeVariable(NULL));
	  }
	else if (var.approx() == null)
	  {
	    var.union(typeVariable(NULL));
	  }
      }
  }
  
  private void compute_solved()
  {
    Set unsolved_set = new TreeSet();
    Set solved_set = new TreeSet();
    
    for(Iterator i = typeVariableList.iterator(); i.hasNext(); )
      {
	TypeVariable var = (TypeVariable) i.next();
	
	if(var.depth() == 0)
	  {
	    if(var.type() == null)
	      {
		unsolved_set.add(var);
	      }
	    else
	      {
		solved_set.add(var);
	      }
	  }
      }

    solved = new LinkedList(solved_set);
    unsolved = new LinkedList(unsolved_set);
  }

  private void refresh_solved() throws TypeException
  {
    Set unsolved_set = new TreeSet();
    Set solved_set = new TreeSet(solved);
    
    for(Iterator i = unsolved.iterator(); i.hasNext(); )
      {
	TypeVariable var = (TypeVariable) i.next();
	
	if(var.depth() == 0)
	  {
	    if(var.type() == null)
	      {
		unsolved_set.add(var);
	      }
	    else
	      {
		solved_set.add(var);
	      }
	  }
      }

    solved = new LinkedList(solved_set);
    unsolved = new LinkedList(unsolved_set);
    
    // validate();
  }

  private void categorize() throws TypeException
  {
    refresh_solved();
   
    single_soft_parent = new LinkedList();
    single_hard_parent = new LinkedList();
    multiple_parents = new LinkedList();
    single_child_not_null = new LinkedList();
    single_null_child = new LinkedList();
    multiple_children = new LinkedList();
    
    for(Iterator i = unsolved.iterator(); i .hasNext(); )
      {
	TypeVariable var = (TypeVariable) i.next();
	
	// parent category
	{
	  List parents = var.parents();
	  int size = parents.size();
	  
	  if(size == 0)
	    {
	      var.addParent(typeVariable(OBJECT));
	      single_soft_parent.add(var);
	    }
	  else if(size == 1)
	    {
	      TypeVariable parent = (TypeVariable) parents.get(0);
	      
	      if(parent.type() == null)
		{
		  single_soft_parent.add(var);
		}
	      else
		{
		  single_hard_parent.add(var);
		}
	    }
	  else
	    {
	      multiple_parents.add(var);
	    }
	}
	
	// child category
	{
	  List children = var.children();
	  int size = children.size();
	  
	  if(size == 0)
	    {
	      var.addChild(typeVariable(NULL));
	      single_null_child.add(var);
	    }
	  else if(size == 1)
	    {
	      TypeVariable child = (TypeVariable) children.get(0);
	      
	      if(child.type() == NULL)
		{
		  single_null_child.add(var);
		}
	      else
		{
		  single_child_not_null.add(var);
		}
	    }
	  else
	    {
	      multiple_children.add(var);
	    }
	}
      }
  }

  private void validate() throws TypeException
  {
    for(Iterator i = solved.iterator(); i.hasNext(); )
      {
	TypeVariable var = (TypeVariable) i.next();
	
	try
	  {
	    var.validate();
	  }
	catch(TypeException e)
	  {
	    debug_vars("Error while validating");
	    throw(e);
	  }
      }
  }

  /*
  private void remove_spurious_locals()
  {
    boolean repeat;

    do
      {
	CompleteUnitGraph graph = new CompleteUnitGraph(stmtBody);
	SimpleLocalDefs defs = new SimpleLocalDefs(graph);
	SimpleLocalUses uses = new SimpleLocalUses(graph, defs);
	PatchingChain units = stmtBody.getUnits();
	Stmt[] stmts = new Stmt[units.size()];
	HashSet deleted = new HashSet();

	repeat = false;
	units.toArray(stmts);
	
	for(int i = 0; i < stmts.length; i++)
	  {
	    Stmt stmt = stmts[i];
	    
	    if(stmt instanceof AssignStmt)
	      {
		AssignStmt assign1 = (AssignStmt) stmt;
		
		if(assign1.getLeftOp() instanceof Local)
		  {
		    List uselist = uses.getUsesOf(assign1);
		    
		    if(uselist.size() == 1)
		      {
			UnitValueBoxPair pair = (UnitValueBoxPair) uselist.get(0);
			
			List deflist = defs.getDefsOfAt((Local) pair.getValueBox().getValue(), pair.getUnit());
			
			if(deflist.size() == 1)
			  {
			    if(pair.getValueBox().canContainValue(assign1.getRightOp()))
			      {
				// This is definitely a spurious local!

				// Hmm.. use is in a deleted statement.  Must wait till next iteration.
				if(deleted.contains(pair.getUnit()))
				  {
				    repeat = true;
				    continue;
				  }
				
				pair.getValueBox().setValue(assign1.getRightOp());
				deleted.add(assign1);
				units.remove(assign1);
				stmtBody.getLocals().remove(assign1.getLeftOp());
				
			      }
			  }
		      }
		  }
	      }
	  }
      }
    while(repeat);
  }
  */

  private void split_new()
  {
    CompleteUnitGraph graph = new CompleteUnitGraph(stmtBody);
    SimpleLocalDefs defs = new SimpleLocalDefs(graph);
    // SimpleLocalUses uses = new SimpleLocalUses(graph, defs);
    PatchingChain units = stmtBody.getUnits();
    Stmt[] stmts = new Stmt[units.size()];

    units.toArray(stmts);
    
    for(int i = 0; i < stmts.length; i++)
      {
	Stmt stmt = stmts[i];

	if(stmt instanceof InvokeStmt)
	  {
	    InvokeStmt invoke = (InvokeStmt) stmt;
	    
	    if(invoke.getInvokeExpr() instanceof SpecialInvokeExpr)
	      {
		SpecialInvokeExpr special = (SpecialInvokeExpr) invoke.getInvokeExpr();
		
		if(special.getMethod().getName().equals("<init>"))
		  {
		    List deflist = defs.getDefsOfAt((Local) special.getBase(), invoke);
		    
		    while(deflist.size() == 1)
		      {
			Stmt stmt2 = (Stmt) deflist.get(0);
			
			if(stmt2 instanceof AssignStmt)
			  {
			    AssignStmt assign = (AssignStmt) stmt2;
			    
			    if(assign.getRightOp() instanceof Local)
			      {
				deflist = defs.getDefsOfAt((Local) assign.getRightOp(), assign);
				continue;
			      }
			    else if(assign.getRightOp() instanceof NewExpr)
			      {			
				// We split the local.
				//System.out.println("split: [" + assign + "] and [" + stmt + "]");
				Local newlocal = Jimple.v().newLocal("tmp", null);
				stmtBody.getLocals().add(newlocal);
				
				special.setBase(newlocal);
				
				units.insertAfter(Jimple.v().newAssignStmt(assign.getLeftOp(), newlocal), assign);
				assign.setLeftOp(newlocal);
			      }
			  }
			break;
		      }
		  }
	      }
	  }
      }
  }
}
