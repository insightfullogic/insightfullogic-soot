/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrice Pominville
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


package soot.jimple.parser;

import soot.baf.*;
import soot.*;
import soot.jimple.*;

import soot.jimple.parser.parser.*;
import soot.jimple.parser.lexer.*;
import soot.jimple.parser.node.*;
import soot.jimple.parser.analysis.*;

import java.io.*;
import java.util.*;


/* 
   Walks a jimple AST and extracts the fields, and method signatures and produces 
   a new squeleton SootClass instance.   
*/   
public class SkeletonExtractorWalker extends Walker
{
           
    public SkeletonExtractorWalker(SootResolver aResolver, SootClass aSootClass) 
    {	
	super(aSootClass, aResolver);
    }

    public SkeletonExtractorWalker(SootResolver aResolver) 
    {	
	super(aResolver);
    }


    public void caseAFile(AFile node)
    {
	inAFile(node);
        {
            Object temp[] = node.getModifier().toArray();
            for(int i = 0; i < temp.length; i++)
            {
                ((PModifier) temp[i]).apply(this);
            }
        }
        if(node.getFileType() != null)
        {
            node.getFileType().apply(this);
        }
        if(node.getClassName() != null)
        {
            node.getClassName().apply(this);
        }
	

	String className = (String) mProductions.pop();
	
	if(mSootClass == null) {
	    mSootClass = new SootClass(className);
	} else {
	    if(!className.equals(mSootClass.getName()))
		throw new RuntimeException("expected:  " + className + ", but got: " + mSootClass.getName());
	}
	
        if(node.getExtendsClause() != null)
        {
            node.getExtendsClause().apply(this);
        }
        if(node.getImplementsClause() != null)
        {
            node.getImplementsClause().apply(this);
        }
        if(node.getFileBody() != null)
	{
            node.getFileBody().apply(this);
	}
        outAFile(node);	
    }


    public void outAFile(AFile node)
    {		
	List implementsList = null;
	String superClass = null;

	String classType = null;
	
	if(node.getImplementsClause() != null) {	   
	    implementsList = (List) mProductions.pop();
	}
	if(node.getExtendsClause() != null) {
	    superClass = (String) mProductions.pop();
	}
	classType = (String) mProductions.pop();
		
	int modifierFlags = processModifiers(node.getModifier());

	
	if(classType.equals("interface"))
	    modifierFlags |= Modifier.INTERFACE;	

	mSootClass.setModifiers(modifierFlags);
		
	if(superClass != null) {
	    mSootClass.setSuperclass(mResolver.getResolvedClass(superClass));
	}
	
	if(implementsList != null) {
	    Iterator implIt = implementsList.iterator();
	    while(implIt.hasNext()) {
		SootClass interfaceClass = mResolver.getResolvedClass((String) implIt.next());
		mSootClass.addInterface(interfaceClass);
	    }
	}
	
	mProductions.push(mSootClass);
	
	/* xxx take this junk out of the scene; then delete the following
	Iterator it = Scene.v().getClassesToResolve().iterator();	
	*/
    } 



    /*
      member =
      {field}  modifier* type name semicolon |
      {method} modifier* type name l_paren parameter_list? r_paren throws_clause? method_body;
    */    
    
       
    public void caseAMethodMember(AMethodMember node)
    {
	inAMethodMember(node);
	{
	    Object temp[] = node.getModifier().toArray();
	    for(int i = 0; i < temp.length; i++)
		{
		    ((PModifier) temp[i]).apply(this);
		}
	}
	if(node.getType() != null)
	    {
		node.getType().apply(this);
	    }
	if(node.getName() != null)
	    {
		node.getName().apply(this);
	    }
	if(node.getLParen() != null)
	    {
		node.getLParen().apply(this);
	    }
	if(node.getParameterList() != null)
	    {
		node.getParameterList().apply(this);
	    }
	if(node.getRParen() != null)
	    {
		node.getRParen().apply(this);
	    }
	if(node.getThrowsClause() != null)
	    {
		node.getThrowsClause().apply(this);
	    }
	/*if(node.getMethodBody() != null)
	  {
	  node.getMethodBody().apply(this);
	  }*/
	outAMethodMember(node);
    }
      public void outAMethodMember(AMethodMember node)
    {
	int modifier = 0;
	Type type;
	String name;
	List parameterList = null;
	List throwsClause = null;
	JimpleBody methodBody = null;

	/* if(node.getMethodBody() instanceof AFullMethodBody)
	    methodBody = (JimpleBody) mProductions.pop();
	*/
	
	if(node.getThrowsClause() != null)
	    throwsClause = (List) mProductions.pop();
	
	if(node.getParameterList() != null) {
	    parameterList = (List) mProductions.pop();
	}
	else {
	    parameterList = new ArrayList();
	} 

	Object o = mProductions.pop();


	name = (String) o;
	type = (Type) mProductions.pop();
	modifier = processModifiers(node.getModifier());

	SootMethod method;

	if(throwsClause != null)
	    method =  new SootMethod(name, parameterList, type, modifier, throwsClause);
	else 	    
	    method =  new SootMethod(name, parameterList, type, modifier);

	mSootClass.addMethod(method);	
    }


    /*
      throws_clause =
      throws class_name_list;
    */    
    public void outAThrowsClause(AThrowsClause node)
    {
	List l = (List) mProductions.pop();
	Iterator it = l.iterator();
	List exceptionClasses = new ArrayList(l.size());
      
	while(it.hasNext()) {	 	  
	    String className = (String) it.next();
	  
	    exceptionClasses.add(mResolver.getResolvedClass(className));
	}

	mProductions.push(exceptionClasses);
    }
} 
