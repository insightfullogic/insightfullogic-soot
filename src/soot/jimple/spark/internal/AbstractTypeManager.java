/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot.jimple.spark.internal;
import soot.jimple.spark.*;
import soot.jimple.spark.pag.*;
import soot.*;
import soot.util.*;
import java.util.Iterator;
import soot.util.queue.*;
import soot.Type;
import soot.options.SparkOptions;

/** A map of bit-vectors representing subtype relationships.
 * @author Ondrej Lhotak
 */
public abstract class AbstractTypeManager {
    abstract public void clearTypeMask();
    final public boolean castNeverFails( Type src, Type dst ) {
        if( fh == null ) return true;
        if( dst == null ) return true;
        if( dst == src ) return true;
        if( src == null ) return false;
        if( dst.equals( src ) ) return true;
        if( src instanceof NullType ) return true;
        if( src instanceof AnySubType ) return true;
        if( dst instanceof NullType ) return false;
        if( dst instanceof AnySubType ) throw new RuntimeException( "oops src="+src+" dst="+dst );
        return fh.canStoreType( src, dst );
    }
    abstract public void makeTypeMask( AbstractPAG pag );

    public void setFastHierarchy( FastHierarchy fh ) { this.fh = fh; }
    public FastHierarchy getFastHierarchy() { return fh; }

    protected FastHierarchy fh = null;
    protected AbstractPAG pag;
    protected QueueReader allocNodeListener = null;
}

