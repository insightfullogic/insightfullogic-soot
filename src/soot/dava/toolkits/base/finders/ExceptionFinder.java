package soot.dava.toolkits.base.finders;

import soot.*;
import java.util.*;
import soot.util.*;
import soot.dava.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.SET.*;
import soot.dava.internal.AST.*;

public class ExceptionFinder implements FactFinder
{
    private ExceptionFinder() {}
    private static ExceptionFinder instance = new ExceptionFinder();

    public static ExceptionFinder v()
    {
	return instance;
    }

    public void find( DavaBody body, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException
    {
	Iterator it = body.get_ExceptionFacts().iterator();
	while (it.hasNext()) {
	    ExceptionNode en = (ExceptionNode) it.next();

	    if (body.get_SynchronizedBlockFacts().contains( en))
		continue;

	    IteratorableSet fullBody = new IteratorableSet();

	    Iterator cit = en.get_CatchList().iterator();
	    while (cit.hasNext()) 
		fullBody.addAll( (IteratorableSet) cit.next());

	    fullBody.addAll( en.get_TryBody());

	    if (SET.nest( new SETTryNode( fullBody, en, asg, body)) == false)
		throw new RetriggerAnalysisException();
	}
    }

    public void preprocess( DavaBody body, AugmentedStmtGraph asg)
    {
	IteratorableSet enlist = new IteratorableSet();

	// Find the first approximation for all the try catch bodies.
	{
	    Iterator trapIt = body.getTraps().iterator();
	    while (trapIt.hasNext()) {
		Trap trap = (Trap) trapIt.next();
		Unit endUnit = trap.getEndUnit();

		// get the body of the try block as a raw read of the area of protection
		IteratorableSet tryBody = new IteratorableSet();
		
		Iterator btit = body.getUnits().iterator( trap.getBeginUnit());
		for (Unit u = (Unit) btit.next(); u != endUnit; u = (Unit) btit.next())
		    tryBody.add( asg.get_AugStmt( (Stmt) u));

		enlist.add( new ExceptionNode( tryBody, trap.getException(), asg.get_AugStmt( (Stmt) trap.getHandlerUnit())));
	    }
	}

	// Add in gotos that may escape the try body (created by the indirection introduced in DavaBody).
	{
	    Iterator enlit = enlist.iterator();
	    while (enlit.hasNext()) {
		ExceptionNode en = (ExceptionNode) enlit.next();
		IteratorableSet try_body = en.get_TryBody();
		
		Iterator tryIt = try_body.snapshotIterator();
		while (tryIt.hasNext()) {
		    AugmentedStmt tras = (AugmentedStmt) tryIt.next();
		    
		    Iterator ptIt = tras.cpreds.iterator();
		    while (ptIt.hasNext()) {
			AugmentedStmt pas = (AugmentedStmt) ptIt.next();
			Stmt ps = pas.get_Stmt();
			
			if ((try_body.contains( pas) == false) && (ps instanceof GotoStmt)) {
			    boolean add_it = true;
			    
			    Iterator cpit = pas.cpreds.iterator();
			    while (cpit.hasNext()) 
				if ((add_it = try_body.contains( cpit.next())) == false) 
				    break;
			    
			    if (add_it)
				en.add_TryStmt( pas);
			}
		    }
		}
	    }
	}

	// Split up the try blocks until they cause no nesting problems.
    splitLoop:
	while (true)
	{
	    // refresh the catch bodies
	    {
		Iterator enlit = enlist.iterator();
		while (enlit.hasNext())
		    ((ExceptionNode) enlit.next()).refresh_CatchBody( this);
	    }

	    // split for inter-exception nesting problems
	    {
		ExceptionNode[] ena = new ExceptionNode[ enlist.size()];
		Iterator enlit = enlist.iterator();
		for (int i=0; enlit.hasNext(); i++) 
		    ena[ i] = (ExceptionNode) enlit.next();
		
		for (int i=0; i<ena.length-1; i++) {
		    ExceptionNode eni = ena[i];
		    for (int j=i+1; j<ena.length; j++) {
			ExceptionNode enj = ena[j];
			
			IteratorableSet 
			    eniTryBody = eni.get_TryBody(),
			    enjTryBody = enj.get_TryBody();

			if ((eniTryBody.equals( enjTryBody) == false) && (eniTryBody.intersects( enjTryBody))) {

			    if ((eniTryBody.isSupersetOf( enj.get_Body())) ||
				(enjTryBody.isSupersetOf( eni.get_Body())))

				continue;

			    IteratorableSet newTryBody = eniTryBody.intersection( enjTryBody);

			    if (newTryBody.equals( enjTryBody))
				eni.splitOff_ExceptionNode( newTryBody, asg, enlist);
			    else 
				enj.splitOff_ExceptionNode( newTryBody, asg, enlist);

			    continue splitLoop;
			}
		    }
		}
	    }

	    // split for intra-try-body issues
	    {
		Iterator enlit = enlist.iterator();
		while (enlit.hasNext()) {
		    ExceptionNode en = (ExceptionNode) enlit.next();

		    // Get the try block entry points
		    IteratorableSet tryBody = en.get_TryBody();
		    LinkedList heads = new LinkedList();
		    Iterator trIt = tryBody.iterator();
		    while (trIt.hasNext()) {
			AugmentedStmt as = (AugmentedStmt) trIt.next();

			Iterator pit = as.cpreds.iterator();
			while (pit.hasNext()) 
			    if (tryBody.contains( pit.next()) == false) {
				heads.add( as);
				break;
			    }
		    }

		    HashSet touchSet = new HashSet();
		    touchSet.addAll( heads);

		    // Break up the try block for all the so-far detectable parts.
		    AugmentedStmt head = (AugmentedStmt) heads.removeFirst();
		    IteratorableSet subTryBlock = new IteratorableSet();
		    LinkedList worklist = new LinkedList();
		    
		    worklist.add( head);
		    
		    while (worklist.isEmpty() == false) {
			AugmentedStmt as = (AugmentedStmt) worklist.removeFirst();
			
			subTryBlock.add( as);
			Iterator sit = as.csuccs.iterator();
			while (sit.hasNext()) {
			    AugmentedStmt sas = (AugmentedStmt) sit.next();
			    
			    if ((tryBody.contains( sas) == false) || (touchSet.contains( sas)))
				continue;
			    
			    touchSet.add( sas);
			    
			    if (sas.get_Dominators().contains( head))
				worklist.add( sas);
			    else  
				heads.addLast( sas);
			}
		    }
		    
		    if (heads.isEmpty() == false) {
			en.splitOff_ExceptionNode( subTryBlock, asg, enlist);
			continue splitLoop;
		    }
		}   
	    }

	    break;
	}

	// Aggregate the try blocks.
	{
	    LinkedList reps = new LinkedList();
	    HashMap 
		hCode2bucket          = new HashMap(),
		tryBody2exceptionNode = new HashMap();
	    
	    Iterator enlit = enlist.iterator();
	    while (enlit.hasNext()) {
		ExceptionNode en = (ExceptionNode) enlit.next();

		int hashCode = 0;
		IteratorableSet curTryBody = en.get_TryBody();

		Iterator trit = curTryBody.iterator();
		while (trit.hasNext()) 
		    hashCode ^= trit.next().hashCode();
		Integer I = new Integer( hashCode);

		LinkedList bucket = (LinkedList) hCode2bucket.get( I);
		if (bucket == null) {
		    bucket = new LinkedList();
		    hCode2bucket.put( I, bucket);
		}

		ExceptionNode repExceptionNode = null;

		Iterator bit = bucket.iterator();
		while (bit.hasNext()) {
		    IteratorableSet bucketTryBody = (IteratorableSet) bit.next();
			
		    if (bucketTryBody.equals( curTryBody)) {
			repExceptionNode = (ExceptionNode) tryBody2exceptionNode.get( bucketTryBody);
			break;
		    }
		}
		    
		if (repExceptionNode == null) {
		    tryBody2exceptionNode.put( curTryBody, en);
		    bucket.add( curTryBody);
		    reps.add( en);
		}
		else 
		    repExceptionNode.add_CatchBody( en);
	    }

	    enlist.clear();
	    enlist.addAll( reps);
	}

	body.get_ExceptionFacts().clear();
	body.get_ExceptionFacts().addAll( enlist);
    }

    public IteratorableSet get_CatchBody( AugmentedStmt handlerAugmentedStmt) 
    {
	IteratorableSet catchBody = new IteratorableSet();
	LinkedList catchQueue = new LinkedList();
	
	catchBody.add( handlerAugmentedStmt);	    
	catchQueue.addAll( handlerAugmentedStmt.csuccs);
	
	while (catchQueue.isEmpty() == false) {
	    AugmentedStmt as = (AugmentedStmt) catchQueue.removeFirst();
	    
	    if (catchBody.contains( as))
		continue;
	    
	    if (as.get_Dominators().contains( handlerAugmentedStmt)) {
		catchBody.add( as);
		catchQueue.addAll( as.csuccs);
	    }
	}

	return catchBody;
    }
}
