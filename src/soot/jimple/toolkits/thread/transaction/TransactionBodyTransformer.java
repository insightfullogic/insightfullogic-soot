package soot.jimple.toolkits.thread.transaction;

import java.util.*;
import soot.*;
import soot.util.Chain;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.toolkits.scalar.*;

public class TransactionBodyTransformer extends BodyTransformer
{
    private static TransactionBodyTransformer instance = new TransactionBodyTransformer();
    private TransactionBodyTransformer() {}

    public static TransactionBodyTransformer v() { return instance; }

//    private FlowSet fs;
//    private int maxLockObjs;
//    private boolean[] useGlobalLock;
    
//    public void setDetails(FlowSet fs, int maxLockObjs, boolean[] useGlobalLock)
//    {
//    	this.fs = fs;
//    	this.maxLockObjs = maxLockObjs;
//    	this.useGlobalLock = useGlobalLock;
//    }
    
    public static boolean[] addedGlobalLockObj = null;
    private static boolean addedGlobalLockDefs = false;
	private static int throwableNum = 0; // doesn't matter if not reinitialized to 0
    
    protected void internalTransform(Body b, String phase, Map opts)
    {
    	throw new RuntimeException("Not Supported");
    }
    
    protected void internalTransform(Body b, FlowSet fs, List groups)
	{
		// 
		JimpleBody j = (JimpleBody) b;
		SootMethod thisMethod = b.getMethod();
    	PatchingChain units = b.getUnits();
		Iterator unitIt = units.iterator();
		Unit firstUnit = (Unit) j.getFirstNonIdentityStmt();
		Unit lastUnit = (Unit) units.getLast();
		
		// Objects of synchronization, plus book keeping
		Local[] lockObj = new Local[groups.size()];
		boolean[] addedLocalLockObj = new boolean[groups.size()];
		SootField[] globalLockObj = new SootField[groups.size()];
		for(int i = 1; i < groups.size(); i++)
		{
			lockObj[i] = Jimple.v().newLocal("lockObj" + i, RefType.v("java.lang.Object"));
			addedLocalLockObj[i] = false;
			globalLockObj[i] = null;
		}
		
		// Make sure a main routine exists.  We will insert some code into it.
//        if (!Scene.v().getMainClass().declaresMethod("void main(java.lang.String[])"))
//            throw new RuntimeException("couldn't find main() in mainClass");
        
        // Add all global lock objects to the main class if not yet added.
        // Get references to them if they do already exist.
  		for(int i = 1; i < groups.size(); i++)
   		{
   			TransactionGroup tnGroup = (TransactionGroup) groups.get(i);
// 			if( useGlobalLock[i - 1] )
			if( !tnGroup.useDynamicLock && !tnGroup.useLocksets )
   			{
	   			if( !addedGlobalLockObj[i] )
	            {
	            	// Add globalLockObj field if possible...
	            	
	       			// Avoid name collision... if it's already there, then just use it!
	            	try
	            	{
	            		globalLockObj[i] = Scene.v().getMainClass().getFieldByName("globalLockObj" + i);
	            		// field already exists
	            	}
	            	catch(RuntimeException re)
	            	{
	            		// field does not yet exist (or, as a pre-existing error, there is more than one field by this name)
		            	globalLockObj[i] = new SootField("globalLockObj" + i, RefType.v("java.lang.Object"), 
		                                      Modifier.STATIC | Modifier.PUBLIC);
		            	Scene.v().getMainClass().addField(globalLockObj[i]);
					}

	            	addedGlobalLockObj[i] = true;
	            }
	            else
	            {
	            	globalLockObj[i] = Scene.v().getMainClass().getFieldByName("globalLockObj" + i);
				}
			}
		}
   		
   		// If the current method is the clinit method of the main class, for each global lock object,
   		// add a local lock object and assign it a new object.  Copy the new 
   		// local lock object into the global lock object for use by other fns.
        if(!addedGlobalLockDefs)// thisMethod.getSubSignature().equals("void <clinit>()") && thisMethod.getDeclaringClass() == Scene.v().getMainClass())
        {
        	// Either get or add the <clinit> method to the main class
        	SootClass mainClass = Scene.v().getMainClass();
        	SootMethod clinitMethod = null;
        	JimpleBody clinitBody = null;
        	Stmt firstStmt = null;
        	boolean addingNewClinit = !mainClass.declaresMethod("void <clinit>()");
        	if(addingNewClinit)
        	{
        		clinitMethod = new SootMethod("<clinit>", new ArrayList(), VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
        		clinitBody = Jimple.v().newBody(clinitMethod);
        		clinitMethod.setActiveBody(clinitBody);
        		mainClass.addMethod(clinitMethod);
        	}
        	else
        	{
				clinitMethod = mainClass.getMethod("void <clinit>()");
				clinitBody = (JimpleBody) clinitMethod.getActiveBody();
				firstStmt = clinitBody.getFirstNonIdentityStmt();
        	}
        	PatchingChain clinitUnits = clinitBody.getUnits();
        	
    		for(int i = 1; i < groups.size(); i++)
    		{
    			TransactionGroup tnGroup = (TransactionGroup) groups.get(i);
//    			if( useGlobalLock[i - 1] )
				if( !tnGroup.useDynamicLock && !tnGroup.useLocksets )
    			{
					// add local lock obj
//	    			addedLocalLockObj[i] = true;
					clinitBody.getLocals().add(lockObj[i]); // TODO: add name conflict avoidance code
		            
		            // assign new object to lock obj
		            Stmt newStmt = Jimple.v().newAssignStmt(lockObj[i],
							Jimple.v().newNewExpr(RefType.v("java.lang.Object")));
					if(addingNewClinit)
						clinitUnits.add(newStmt);
					else
						clinitUnits.insertBeforeNoRedirect(newStmt, firstStmt);

					// initialize new object
		            SootClass objectClass = Scene.v().loadClassAndSupport("java.lang.Object");
		            RefType type = RefType.v(objectClass);
		            SootMethod initMethod = objectClass.getMethod("void <init>()");
		            Stmt initStmt = Jimple.v().newInvokeStmt(
		            				Jimple.v().newSpecialInvokeExpr(lockObj[i], 
		            					initMethod.makeRef(), Collections.EMPTY_LIST));
		            if(addingNewClinit)
			            clinitUnits.add(initStmt);
			        else
			        	clinitUnits.insertBeforeNoRedirect(initStmt, firstStmt);
			        
			        // copy new object to global static lock object (for use by other fns)
			        Stmt assignStmt = Jimple.v().newAssignStmt(
	       							  Jimple.v().newStaticFieldRef(globalLockObj[i].makeRef()), lockObj[i]);
		        	if(addingNewClinit)
			        	clinitUnits.add(assignStmt);
			        else
			        	clinitUnits.insertBeforeNoRedirect(assignStmt, firstStmt);
	       		}
    		}
    		if(addingNewClinit)
	    		clinitUnits.add(Jimple.v().newReturnVoidStmt());
    		addedGlobalLockDefs = true;
        }
		
		int tempNum = 1;
		// Iterate through all of the transactions in the current method
		Iterator fsIt = fs.iterator();
		while(fsIt.hasNext())
		{
			Transaction tn = ((TransactionFlowPair) fsIt.next()).tn;
			if(tn.setNumber == -1)
				continue; // this tn should be deleted... for now just skip it!

			Local clo = null; // depends on type of locking
			LockRegion clr = null; // current lock region
			int lockNum = 0;
			boolean moreLocks = true;
			while(moreLocks)
			{
				// If this method does not yet have a reference to the lock object
				// needed for this transaction, then create one.
				if( tn.group.useDynamicLock )
				{
					if(!addedLocalLockObj[tn.setNumber])
					{
						b.getLocals().add(lockObj[tn.setNumber]);
						addedLocalLockObj[tn.setNumber] = true;
					}
					if(tn.lockObject instanceof Ref)
					{
						Stmt assignLocalLockStmt = Jimple.v().newAssignStmt(lockObj[tn.setNumber], tn.lockObject);
						if(tn.wholeMethod)
							units.insertBeforeNoRedirect(assignLocalLockStmt, (Stmt) firstUnit);
						else
							units.insertBefore(assignLocalLockStmt, (Stmt) tn.entermonitor);
						clo = lockObj[tn.setNumber];
					}
					else if(tn.lockObject instanceof Local)
						clo = (Local) tn.lockObject;
					else
						throw new RuntimeException("Unknown type of lock (" + tn.lockObject + "): expected Ref or Local");
					clr = tn;
					moreLocks = false;
				}
				else if( tn.group.useLocksets )
				{
					Value lock = (Value) tn.lockset.get(lockNum);
					
					if( lock instanceof EquivalentValue ) // unwrap equivalent value
						lock = ((EquivalentValue)lock).getValue();
						
					if(!addedLocalLockObj[tn.setNumber])
					{
						b.getLocals().add(lockObj[tn.setNumber]);
						addedLocalLockObj[tn.setNumber] = true;
					}
					if(lock instanceof InstanceFieldRef)
					{
						Stmt assignLocalLockStmt = Jimple.v().newAssignStmt(lockObj[tn.setNumber], lock);
						if(tn.entermonitor != null)
							units.insertBefore(assignLocalLockStmt, (Stmt) tn.entermonitor);
						else
							units.insertBeforeNoRedirect(assignLocalLockStmt, (Stmt) tn.beginning);
						clo = lockObj[tn.setNumber];
					}
					else if(lock instanceof StaticFieldRef)
					{
						StaticFieldRef sfrLock = (StaticFieldRef) lock;
						SootField sfrField = sfrLock.getField();
						SootClass sfrClass = sfrField.getDeclaringClass();
						
						// add a static lock object to that class
						// use it here
						
						Stmt assignLocalLockStmt = Jimple.v().newAssignStmt(lockObj[tn.setNumber], lock);
						if(tn.entermonitor != null)
							units.insertBefore(assignLocalLockStmt, (Stmt) tn.entermonitor);
						else
							units.insertBeforeNoRedirect(assignLocalLockStmt, (Stmt) tn.beginning);
						clo = lockObj[tn.setNumber];
					}
					else if(lock instanceof Local)
						clo = (Local) lock;
					else
						throw new RuntimeException("Unknown type of lock (" + lock + "): expected Ref or Local");

					if(lockNum + 1 >= tn.lockset.size())
						moreLocks = false;
					else
						moreLocks = true;

					if( lockNum > 0 )
					{
						LockRegion nlr = new LockRegion();

						nlr.beginning = clr.beginning;
						for(Iterator earlyEndsIt = clr.earlyEnds.iterator(); earlyEndsIt.hasNext(); )
						{
							Pair earlyEnd = (Pair) earlyEndsIt.next(); // <early end, early exitmonitor>
							Stmt earlyExitmonitor = (Stmt) earlyEnd.getO2();
							nlr.earlyEnds.add(new Pair(earlyExitmonitor, null)); // <early exitmonitor, null>
						}
						if(clr.end != null)
						{
							Stmt endExitmonitor = (Stmt) clr.end.getO2();
							nlr.after = endExitmonitor;
						}
						
						clr = nlr;
					}
					else
						clr = tn;
				}
				else // global lock
				{
					if(!addedLocalLockObj[tn.setNumber])
						b.getLocals().add(lockObj[tn.setNumber]);
					addedLocalLockObj[tn.setNumber] = true;
					Stmt assignLocalLockStmt = Jimple.v().newAssignStmt(lockObj[tn.setNumber],
									Jimple.v().newStaticFieldRef(globalLockObj[tn.setNumber].makeRef()));
					if(tn.wholeMethod)
						units.insertBeforeNoRedirect(assignLocalLockStmt, firstUnit);
					else
						units.insertBefore(assignLocalLockStmt, (Stmt) tn.entermonitor);
					clo = lockObj[tn.setNumber];
					clr = tn;
					moreLocks = false;
				}
				
				// Add synchronization code
				// For transactions from synchronized methods, use synchronizeSingleEntrySingleExitBlock()
				// to add all necessary code (including ugly exception handling)
				// For transactions from synchronized blocks, simply replace the
				// monitorenter/monitorexit statements with new ones		
				if(true)
				{
					// Remove old prep stmt
					if( clr.prepStmt != null )
					{
						units.remove(clr.prepStmt);
					}
					
					// Reuse old entermonitor or insert new one, and insert prep
					Stmt newEntermonitor = Jimple.v().newEnterMonitorStmt(clo);
					if( clr.entermonitor != null )
					{
						units.insertBefore(newEntermonitor, clr.entermonitor);
						// redirectTraps(b, clr.entermonitor, newEntermonitor); // EXPERIMENTAL
						units.remove(clr.entermonitor);
						clr.entermonitor = newEntermonitor;

						// units.insertBefore(newEntermonitor, newPrep);
						// clr.prepStmt = newPrep;
					}
					else
					{
						units.insertBeforeNoRedirect(newEntermonitor, clr.beginning);
						clr.entermonitor = newEntermonitor;
						
						// units.insertBefore(newEntermonitor, newPrep);
						// clr.prepStmt = newPrep;
					}
					
					// For each early end, reuse or insert exitmonitor stmt
					List newEarlyEnds = new ArrayList();
					for(Iterator earlyEndsIt = clr.earlyEnds.iterator(); earlyEndsIt.hasNext(); )
					{
						Pair end = (Pair) earlyEndsIt.next();
						Stmt earlyEnd = (Stmt) end.getO1();
						Stmt exitmonitor = (Stmt) end.getO2();
						
						Stmt newExitmonitor = Jimple.v().newExitMonitorStmt(clo);
						if( exitmonitor != null )
						{
							units.insertBefore(newExitmonitor, exitmonitor);
							// redirectTraps(b, exitmonitor, newExitmonitor); // EXPERIMENTAL
							units.remove(exitmonitor);
							newEarlyEnds.add(new Pair(earlyEnd, newExitmonitor));
						}
						else
						{
							units.insertBefore(newExitmonitor, earlyEnd);
							newEarlyEnds.add(new Pair(earlyEnd, newExitmonitor));
						}
					}
					clr.earlyEnds = newEarlyEnds;
					
					// If fallthrough end, reuse or insert goto and exit
					if( clr.after != null )
					{
						Stmt newExitmonitor = Jimple.v().newExitMonitorStmt(clo);
						if( clr.end != null )
						{
							Stmt exitmonitor = (Stmt) clr.end.getO2();

							units.insertBefore(newExitmonitor, exitmonitor);
							// redirectTraps(b, exitmonitor, newExitmonitor); // EXPERIMENTAL
							units.remove(exitmonitor);
							clr.end = new Pair(clr.end.getO1(), newExitmonitor);
						}
						else
						{
							units.insertBefore(newExitmonitor, clr.after); // steal jumps to end, send them to monitorexit
							Stmt newGotoStmt = Jimple.v().newGotoStmt(clr.after);
							units.insertBeforeNoRedirect(newGotoStmt, clr.after);
							clr.end = new Pair(newGotoStmt, newExitmonitor);
						}
					}
					
					// If exceptional end, reuse it, else insert it and traps
					Stmt newExitmonitor = Jimple.v().newExitMonitorStmt(clo);
					if( clr.exceptionalEnd != null )
					{
						Stmt exitmonitor = (Stmt) clr.exceptionalEnd.getO2();
						
						units.insertBefore(newExitmonitor, exitmonitor);
						// redirectTraps(b, exitmonitor, newExitmonitor); // EXPERIMENTAL
						units.remove(exitmonitor);
						clr.exceptionalEnd = new Pair(clr.end.getO1(), newExitmonitor);
					}
					else
					{
						// insert after the last end
						Stmt lastEnd = null;
						if( clr.end != null )
						{
							lastEnd = (Stmt) clr.end.getO1();
						}
						else
						{
							for(Iterator earlyEndsIt = clr.earlyEnds.iterator(); earlyEndsIt.hasNext(); )
							{
								Pair earlyEnd = (Pair) earlyEndsIt.next();
								Stmt end = (Stmt) earlyEnd.getO1();
								if( lastEnd == null || units.follows(end, lastEnd) )
									lastEnd = end;
							}
						}
						if(lastEnd == null)
							throw new RuntimeException("Lock Region has no ends!  Where should we put the exception handling???");

						// Add throwable
						Local throwableLocal = Jimple.v().newLocal("throwableLocal" + (throwableNum++), RefType.v("java.lang.Throwable"));
						b.getLocals().add(throwableLocal);
						// Add stmts
						Stmt newCatch = Jimple.v().newIdentityStmt(throwableLocal, Jimple.v().newCaughtExceptionRef());
						units.insertAfter(newCatch, lastEnd);
						units.insertAfter(newExitmonitor, newCatch);
						Stmt newThrow = Jimple.v().newThrowStmt(throwableLocal);
						units.insertAfter(newThrow, newExitmonitor);
						// Add traps
						SootClass throwableClass = Scene.v().loadClassAndSupport("java.lang.Throwable");
						b.getTraps().addLast(Jimple.v().newTrap(throwableClass, clr.beginning, lastEnd, newCatch));
						b.getTraps().addLast(Jimple.v().newTrap(throwableClass, newExitmonitor, newThrow, newCatch));
						clr.exceptionalEnd = new Pair(newThrow, newExitmonitor);
					}
				}
/*
				else if(tn.wholeMethod)
				{
					thisMethod.setModifiers( thisMethod.getModifiers() & ~ (Modifier.SYNCHRONIZED) ); // remove synchronized modifier for this method
					synchronizeSingleEntrySingleExitBlock(b, (Stmt) firstUnit, (Stmt) lastUnit, (Local) clo);
				}
				else if(lockNum > 0)
				{
					// don't have all the info to do this right yet
//					synchronizeSingleEntrySingleExitBlock(b, (Stmt) tnbodystart, (Stmt) tnbodyend, (Local) clo);
				}
				else
				{
					if(tn.entermonitor == null) 
						G.v().out.println("ERROR: Transaction has no beginning statement: " + tn.method.toString());
						
					// Deal with entermonitor
					Stmt newBegin = Jimple.v().newEnterMonitorStmt(clo);
					units.insertBefore(newBegin, tn.entermonitor);
					redirectTraps(b, tn.entermonitor, newBegin);
					units.remove(tn.entermonitor);
					
					// Deal with exitmonitors
					// early
					Iterator endsIt = tn.earlyEnds.iterator();
					while(endsIt.hasNext())
					{
						Pair end = (Pair) endsIt.next();
						Stmt sEnd = (Stmt) end.getO2();
						Stmt newEnd = Jimple.v().newExitMonitorStmt(clo);
						units.insertBefore(newEnd, sEnd);
						redirectTraps(b, sEnd, newEnd);
						units.remove(sEnd);
					}
					// exceptional
					Stmt sEnd = (Stmt) tn.exceptionalEnd.getO2();
					Stmt newEnd = Jimple.v().newExitMonitorStmt(clo);
					units.insertBefore(newEnd, sEnd);
					redirectTraps(b, sEnd, newEnd);
					units.remove(sEnd);
					// fallthrough
					sEnd = (Stmt) tn.end.getO2();
					newEnd = Jimple.v().newExitMonitorStmt(clo);
					units.insertBefore(newEnd, sEnd);
					redirectTraps(b, sEnd, newEnd);
					units.remove(sEnd);
				}
*/
				
				// Replace calls to notify() with calls to notifyAll()
				// Replace base object with appropriate lockobj
				lockNum++;
			}
			
			// deal with waits and notifys
			{
				Iterator notifysIt = tn.notifys.iterator();
				while(notifysIt.hasNext())
				{
					Stmt sNotify = (Stmt) notifysIt.next();
					Stmt newNotify = 
						Jimple.v().newInvokeStmt(
	           				Jimple.v().newVirtualInvokeExpr(
	       						(Local) clo,
	       						sNotify.getInvokeExpr().getMethodRef().declaringClass().getMethod("void notifyAll()").makeRef(), 
	       						Collections.EMPTY_LIST));
		            units.insertBefore(newNotify, sNotify);
					redirectTraps(b, sNotify, newNotify);
					units.remove(sNotify);
				}

				// Replace base object of calls to wait with appropriate lockobj
				Iterator waitsIt = tn.waits.iterator();
				while(waitsIt.hasNext())
				{
					Stmt sWait = (Stmt) waitsIt.next();
					((InstanceInvokeExpr) sWait.getInvokeExpr()).setBase(clo); // WHAT IF THIS IS THE WRONG LOCK IN A PAIR OF NESTED LOCKS???
	//				Stmt newWait = 
	//					Jimple.v().newInvokeStmt(
	//         				Jimple.v().newVirtualInvokeExpr(
	//       						(Local) clo,
	//       						sWait.getInvokeExpr().getMethodRef().declaringClass().getMethod("void wait()").makeRef(), 
	//       						Collections.EMPTY_LIST));
	//	            units.insertBefore(newWait, sWait);
	//				redirectTraps(b, sWait, newWait);
	//				units.remove(sWait);
				}
			}
		}
	}
	
	public void synchronizeSingleEntrySingleExitBlock(Body b, Stmt start, Stmt end, Local lockObj)
	{
		PatchingChain units = b.getUnits();
		
		// <existing local defs>
		
		// add a throwable to local vars
		Local throwableLocal = Jimple.v().newLocal("throwableLocal" + (throwableNum++), RefType.v("java.lang.Throwable"));
		b.getLocals().add(throwableLocal);
		
		// <existing identity refs>
		
		// add entermonitor statement and label0
//		Unit label0Unit = start;
		Unit labelEnterMonitorStmt = Jimple.v().newEnterMonitorStmt(lockObj);
		units.insertBeforeNoRedirect(labelEnterMonitorStmt, start); // steal jumps to start, send them to monitorenter
		
		// <existing code body>	check for return statements
		List returnUnits = new ArrayList();
		if(start != end)
		{
			Iterator bodyIt = units.iterator(start, end);
			while(bodyIt.hasNext())
			{
				Stmt bodyStmt = (Stmt) bodyIt.next();
				if(bodyIt.hasNext()) // ignore the end unit
				{
					if( bodyStmt instanceof ReturnStmt ||
						bodyStmt instanceof ReturnVoidStmt)
					{
						returnUnits.add(bodyStmt);
					}
				}
			}
		}
		
		// add normal flow and labels
		Unit labelExitMonitorStmt = (Unit) Jimple.v().newExitMonitorStmt(lockObj);
		units.insertBefore(labelExitMonitorStmt, end); // steal jumps to end, send them to monitorexit
//		end = (Stmt) units.getSuccOf(end);
		Unit label1Unit = (Unit) Jimple.v().newGotoStmt(end);
		units.insertBeforeNoRedirect(label1Unit, end);
//		end = (Stmt) units.getSuccOf(end);
		
		// add exceptional flow and labels
		Unit label2Unit = (Unit) Jimple.v().newIdentityStmt(throwableLocal, Jimple.v().newCaughtExceptionRef());
		units.insertBeforeNoRedirect(label2Unit, end);
//		end = (Stmt) units.getSuccOf(end);
		Unit label3Unit = (Unit) Jimple.v().newExitMonitorStmt(lockObj);
		units.insertBeforeNoRedirect(label3Unit, end);
//		end = (Stmt) units.getSuccOf(end);
		Unit label4Unit = (Unit) Jimple.v().newThrowStmt(throwableLocal);
		units.insertBeforeNoRedirect(label4Unit, end);
//		end = (Stmt) units.getSuccOf(end);
		
		// <existing end statement>

		Iterator returnIt = returnUnits.iterator();
		while(returnIt.hasNext())
		{
			Stmt bodyStmt = (Stmt) returnIt.next();
			units.insertBefore(Jimple.v().newExitMonitorStmt(lockObj), bodyStmt); // TODO: WHAT IF IT'S IN A NESTED TRANSACTION???
//			Stmt placeholder = Jimple.v().newNopStmt();
//			units.insertAfter(Jimple.v().newNopStmt(), label4Unit);
//			bodyStmt.redirectJumpsToThisTo(placeholder);
//			units.insertBefore(Jimple.v().newGotoStmt(placeholder), bodyStmt);
//			units.remove(bodyStmt);
//			units.swapWith(placeholder, bodyStmt);
			
//			units.swapWith
		}
		
		// add exception routing table
		Unit label0Unit = (Unit) units.getSuccOf(labelEnterMonitorStmt);
		SootClass throwableClass = Scene.v().loadClassAndSupport("java.lang.Throwable");
		b.getTraps().addLast(Jimple.v().newTrap(throwableClass, label0Unit, label1Unit, label2Unit));
		b.getTraps().addLast(Jimple.v().newTrap(throwableClass, label3Unit, label4Unit, label2Unit));

	}
	
	public void redirectTraps(Body b, Unit oldUnit, Unit newUnit)
	{
		Chain traps = b.getTraps();
		Iterator trapsIt = traps.iterator();
		while(trapsIt.hasNext())
		{
			AbstractTrap trap = (AbstractTrap) trapsIt.next();
			if(trap.getHandlerUnit() == oldUnit)
				trap.setHandlerUnit(newUnit);
			if(trap.getBeginUnit() == oldUnit)
				trap.setBeginUnit(newUnit);
			if(trap.getEndUnit() == oldUnit)
				trap.setEndUnit(newUnit);
		}
	}
}
