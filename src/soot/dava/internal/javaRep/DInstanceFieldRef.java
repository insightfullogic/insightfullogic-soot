package soot.dava.internal.javaRep;

import soot.*;
import java.util.*;
import soot.grimp.internal.*;

public class DInstanceFieldRef extends GInstanceFieldRef 
{
    private HashSet thisLocals;

    public DInstanceFieldRef( Value base, SootField field, HashSet thisLocals)
    {
	super( base, field);

	this.thisLocals = thisLocals;
    }

    public void toString( UnitPrinter up ) {
        if( thisLocals.contains(getBase()) ) {
            up.fieldRef( getField() );
        } else {
            super.toString( up );
        }
    }

    public String toString()
    {
	if (thisLocals.contains( getBase())) 
	    return getField().getName();

	return super.toString();
    }

    public Object clone()
    {
	return new DInstanceFieldRef( getBase(), getField(), thisLocals);
    }
}
