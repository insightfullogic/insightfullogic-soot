/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.toolkits.scalar;

import soot.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;
import soot.options.*;
import soot.toolkits.graph.interaction.*;



/**
 *   Abstract class that provides the fixed point iteration functionality
 *   required by all BackwardFlowAnalyses.
 *  
 */
public abstract class BackwardFlowAnalysis extends FlowAnalysis
{
    /** Construct the analysis from a DirectedGraph representation of a Body. */
    public BackwardFlowAnalysis(DirectedGraph graph)
    {
        super(graph);
    }

    protected boolean isForward()
    {
        return false;
    }

    protected void doAnalysis()
    {
        LinkedList changedUnits = new LinkedList();
        HashSet changedUnitsSet = new HashSet();

        // Set initial Flows and nodes to visit.
        {
            Iterator it = graph.iterator();

            while(it.hasNext())
            {
                Object s = it.next();

                /* although only a DFS could really give a good result, it is
                 * probably wiser to do begin at the bottom for
                 * Backwards-analysis*/
                // Starting at the tails, though, probably slows things down.
                changedUnits.addFirst(s);
                changedUnitsSet.add(s);

                unitToBeforeFlow.put(s, newInitialFlow());
                unitToAfterFlow.put(s, newInitialFlow());
            }
        }

        // Feng Qian: March 07, 2002
        // init entry points
        {
            Iterator it = graph.getTails().iterator();
            
            while (it.hasNext()) {
                Object s = it.next();
                // this is a backward flow analysis
                unitToAfterFlow.put(s, entryInitialFlow());
            }
        }

        // Perform fixed point flow analysis
        {
            Object previousBeforeFlow = newInitialFlow();

            while(!changedUnits.isEmpty())
            {
                Object beforeFlow;
                Object afterFlow;

                Object s = changedUnits.removeFirst();

                changedUnitsSet.remove(s);

                copy(unitToBeforeFlow.get(s), previousBeforeFlow);

                // Compute and store afterFlow
                {
                    List succs = graph.getSuccsOf(s);

                    afterFlow =  unitToAfterFlow.get(s);

                    if(succs.size() == 1)
                        copy(unitToBeforeFlow.get(succs.get(0)), afterFlow);
                    else if(succs.size() != 0)
                    {
                        Iterator succIt = succs.iterator();

                        copy(unitToBeforeFlow.get(succIt.next()), afterFlow);

                        while(succIt.hasNext())
                        {
                            Object otherBranchFlow = unitToBeforeFlow.get(succIt.next());
                            Object newAfterFlow = newInitialFlow();
                            merge(afterFlow, otherBranchFlow, newAfterFlow);
                            copy(newAfterFlow, afterFlow);
                        }
                    }
                }

                // Compute beforeFlow and store it.
                {
                    beforeFlow = unitToBeforeFlow.get(s);
                    if (Options.v().interactive_mode()){
                        Object savedFlow = newInitialFlow();
                        copy(afterFlow, savedFlow);
                        FlowInfo fi = new FlowInfo(savedFlow, s, false);
                        InteractionHandler.v().handleAfterAnalysisEvent(fi);
                    }
                    flowThrough(afterFlow, s, beforeFlow);
                    if (Options.v().interactive_mode()){
                        Object bSavedFlow = newInitialFlow();
                        copy(beforeFlow, bSavedFlow);
                        FlowInfo fi = new FlowInfo(bSavedFlow, s, true);
                        InteractionHandler.v().handleBeforeAnalysisEvent(fi);
                    }
                }

                // Update queue appropriately
                    if(!beforeFlow.equals(previousBeforeFlow))
                    {
                        Iterator predIt = graph.getPredsOf(s).iterator();

                        while(predIt.hasNext())
                        {
                            Object pred = predIt.next();
                            
                            if(!changedUnitsSet.contains(pred))
                            {
                                changedUnitsSet.add(pred);
                                changedUnits.addLast(pred);
                            }
                        }
                    }
            }
        }
    }
}



