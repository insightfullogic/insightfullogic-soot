/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot;

import soot.util.*;
import java.util.*;

/** A wrapper object for a pack of optimizations.
 * Provides chain-like operations, except that the key is the phase name. */
public class RadioScenePack extends ScenePack
{
    public RadioScenePack(String name) {
        super(name);
    }

    protected void internalApply()
    {
        LinkedList enableds = new LinkedList();

        for( Iterator tIt = this.iterator(); tIt.hasNext(); ) {

            final Transform t = (Transform) tIt.next();
            Map opts = PackManager.v().getPhaseOptions( t );
            if( !PackManager.getBoolean( opts, "enabled" ) ) continue;
            enableds.add( t );
        }
        if( enableds.size() == 0 ) {
            G.v().out.println( "Exactly one phase in the pack "+getPhaseName()+
                    " must be enabled. Currently, none of them are." );
            throw new CompilationDeathException( CompilationDeathException.COMPILATION_ABORTED );
        }
        if( enableds.size() > 1 ) {
            G.v().out.println( "Only one phase in the pack "+getPhaseName()+
                    " may be enabled. The following are enabled currently: " );
            for( Iterator tIt = enableds.iterator(); tIt.hasNext(); ) {
                final Transform t = (Transform) tIt.next();
                G.v().out.println( "  "+t.getPhaseName() );
            }
            throw new CompilationDeathException( CompilationDeathException.COMPILATION_ABORTED );
        }
        for( Iterator tIt = enableds.iterator(); tIt.hasNext(); ) {
            final Transform t = (Transform) tIt.next();
            t.apply();
        }
    }
}
