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

package soot.util.queue;

/** A queue of Object's. One can add objects to the queue, and they are
 * later read by a QueueReader. One can create arbitrary numbers of
 * QueueReader's for a queue, and each one receives all the Object's that
 * are added. Only objects that have not been read by all the QueueReader's
 * are kept. A QueueReader only receives the Object's added to the queue
 * <b>after</b> the QueueReader was created.
 * @author Ondrej Lhotak
 */
public class QueueReader implements java.util.Iterator
{ 
    private Object[] q;
    private int index;
    QueueReader( Object[] q, int index ) {
        this.q = q;
        this.index = index;
    }
    /** Returns (and removes) the next object in the queue, or null if
     * there are none. */
    public final Object next() {
        if( q[index] == null ) return null;
        if( index == q.length - 1 ) {
            q = (Object[]) q[index];
            index = 0;
            if( q[index] == null ) return null;
        }
        return q[index++];
    }

    /** Returns true iff there is currently another object in the queue. */
    public final boolean hasNext() {
        if (q[index] == null) return false;
        if (index == q.length - 1) {
            q = (Object[]) q[index];
            index = 0;
            if (q[index] == null) return false;
        }
        return true;
    }

    public final void remove() {
        throw new UnsupportedOperationException();
    }

    public final Object clone() {
        return new QueueReader( q, index );
    }
}


