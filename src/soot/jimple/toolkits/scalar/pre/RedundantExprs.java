/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrick Lam
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

package soot.jimple.toolkits.scalar.pre;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;

class RedundantExprs
{
    LatestExprs lat;
    IsolatedExprs iso;
    FlowUniverse uni;

    public RedundantExprs(BlockGraph g, LatestExprs lat, IsolatedExprs iso,
                                FlowUniverse uni)
    {
        this.lat = lat; this.iso = iso; this.uni = uni;
    }

    public BoundedFlowSet getRedundantExprsOf(Block b)
    {
        BoundedFlowSet res = iso.getIsolatedExprsAfter(b);
        res.union(lat.getLatestExprsBefore(b), res);
        res.complement(res);
        res.intersection(LocallyAnticipatableExprs.getAntLocExprsOf(b, uni), res);
        return res;
    }
}
