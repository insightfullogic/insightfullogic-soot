/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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


package soot.jimple.toolkits.scalar;

import soot.*;
import soot.util.*;
import soot.toolkits.scalar.*;
import java.util.*;

/** Represents information for flow analysis, adding a top element to a lattice.
 * A FlowSet is an element of a lattice; this lattice might be described by a FlowUniverse.
 * If add, remove, size, isEmpty, toList and contains are implemented, the lattice must be the powerset of some set.
 *
 */
public class ToppedSet implements FlowSet
{
    FlowSet underlyingSet;
    boolean isTop;

    public void setTop(boolean top) { isTop = top; }
    public boolean isTop() { return isTop; }

    public ToppedSet(FlowSet under)
    {
        underlyingSet = under;
    }

    public Object clone()
    {
        ToppedSet newSet = new ToppedSet((FlowSet)underlyingSet.clone());
        newSet.setTop(isTop());
        return newSet;
    }

    public void copy(FlowSet d)
    {
        ToppedSet dest = (ToppedSet)d;
        if (!isTop())
        {
            underlyingSet.copy(dest.underlyingSet);
            dest.setTop(false);
            return;
        }

        dest.setTop(true);
    }

    public void clear()
    {
        isTop = false;
        underlyingSet.clear();
    }

    public void union(FlowSet o, FlowSet d)
    {
        ToppedSet other = (ToppedSet)o;
        ToppedSet dest = (ToppedSet)d;

        if (isTop())
        {
            copy(dest);
            return;
        }

        if (other.isTop())
            other.copy(dest);
        else
        {
            underlyingSet.union(other.underlyingSet, 
                                dest.underlyingSet);
            dest.setTop(false);
        }
    }

    public void intersection(FlowSet o, FlowSet d)
    {
        if (isTop())
        {
            o.copy(d);
            return;
        }

        ToppedSet other = (ToppedSet)o, dest = (ToppedSet)d;

        if (other.isTop())
        {
            copy(dest);
            return;
        }
        else
        {
            underlyingSet.intersection(other.underlyingSet, 
                                       dest.underlyingSet);
            dest.setTop(false);
        }
    }

    public void difference(FlowSet o, FlowSet d)
    {
        ToppedSet other = (ToppedSet)o, dest = (ToppedSet)d;

        if (other.underlyingSet instanceof BoundedFlowSet)
        {
            FlowSet temp = (FlowSet)other.underlyingSet.clone();
            ((BoundedFlowSet)other.underlyingSet).complement(temp);
            temp.union(temp, dest.underlyingSet);
            return;
        }
        throw new RuntimeException("can't take difference!");
    }

    public boolean isEmpty()
    {
        if (isTop()) return false;
        return underlyingSet.isEmpty();
    }

    public int size()
    {
        if (isTop()) throw new UnsupportedOperationException();
        return underlyingSet.size();
    }

    public void add(Object obj, FlowSet d)
    {
        ToppedSet dest = (ToppedSet)d;

        if (isTop()) { copy(dest); return; }
        dest.underlyingSet.add(obj, 
                      dest.underlyingSet);
    }

    public void remove(Object obj, FlowSet d)
    {
        ToppedSet dest = (ToppedSet)d;

        if (isTop()) { copy(dest); return; }
        dest.underlyingSet.remove(obj, 
                                  dest.underlyingSet);
    }

    public boolean contains(Object obj)
    {
        if (isTop()) return true;
        return underlyingSet.contains(obj);
    }

    public List toList()
    {
        if (isTop()) throw new UnsupportedOperationException();
        return underlyingSet.toList();
    }

    public boolean equals(Object o)
    {
        if (!(o instanceof ToppedSet))
            return false;

        ToppedSet other = (ToppedSet)o;
        if (other.isTop() != isTop())
            return false;
        return underlyingSet.equals(other.underlyingSet);
    }

    public String toString()
    {
        if (isTop()) return "{TOP}"; else return underlyingSet.toString();
    }
}

