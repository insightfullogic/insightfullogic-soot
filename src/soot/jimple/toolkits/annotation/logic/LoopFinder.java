/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

package soot.jimple.toolkits.annotation.logic;

import soot.*;
import soot.toolkits.graph.*;
import soot.jimple.*;

import java.util.*;

import soot.toolkits.scalar.*;

public class LoopFinder extends BodyTransformer {

    private UnitGraph g;

    private HashMap<Stmt, List<Stmt>> loops;

    public Map<Stmt, List<Stmt>> loops(){
        return loops;
    }
    
    protected void internalTransform (Body b, String phaseName, Map options){
    
        g = new ExceptionalUnitGraph(b);
        DominatorAnalysis a = new DominatorAnalysis(g);
        
        loops = new HashMap<Stmt, List<Stmt>>();
        
        Iterator stmtsIt = b.getUnits().iterator();
        while (stmtsIt.hasNext()){
            Stmt s = (Stmt)stmtsIt.next();

            List succs = g.getSuccsOf(s);
            FlowSet dominaters = (FlowSet)a.getFlowAfter(s);

            ArrayList<Stmt> backEdges = new ArrayList<Stmt>();

            Iterator succsIt = succs.iterator();
            while (succsIt.hasNext()){
                Stmt succ = (Stmt)succsIt.next();
                if (dominaters.contains(succ)){
                    backEdges.add(succ);
                }
            }

            Iterator<Stmt> headersIt = backEdges.iterator();
            while (headersIt.hasNext()){
                Stmt header = headersIt.next();
                List<Stmt> loopBody = getLoopBodyFor(header, s);

                // for now just print out loops as sets of stmts
                //System.out.println("FOUND LOOP: Header: "+header+" Body: "+loopBody);
                if (loops.containsKey(header)){
                    // merge bodies
                    List<Stmt> lb1 = loops.get(header);
                    loops.put(header, union(lb1, loopBody));
                }
                else {
                    loops.put(header, loopBody);
                }
            }
        }

    }
    

    private List<Stmt> getLoopBodyFor(Stmt header, Stmt node){
    
        ArrayList<Stmt> loopBody = new ArrayList<Stmt>();
        Stack stack = new Stack();

        loopBody.add(header);
        stack.push(node);

        while (!stack.isEmpty()){
            Stmt next = (Stmt)stack.pop();
            if (!loopBody.contains(next)){
                // add next to loop body
                loopBody.add(0, next);
                // put all preds of next on stack
                Iterator it = g.getPredsOf(next).iterator();
                while (it.hasNext()){
                    stack.push(it.next());
                }
            }
        }

        return loopBody;
    }

    private List<Stmt> union(List<Stmt> l1, List<Stmt> l2){
        Iterator<Stmt> it = l2.iterator();
        while (it.hasNext()){
            Stmt next = it.next();
            if (!l1.contains(next)){
                l1.add(next);
            }
        }
        return l1;
    }
}
