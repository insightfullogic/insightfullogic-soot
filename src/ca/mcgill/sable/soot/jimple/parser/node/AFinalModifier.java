package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AFinalModifier extends PModifier
{
    private TFinal _final_;

    public AFinalModifier()
    {
    }

    public AFinalModifier(
        TFinal _final_)
    {
        setFinal(_final_);

    }
    public Object clone()
    {
        return new AFinalModifier(
            (TFinal) cloneNode(_final_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFinalModifier(this);
    }

    public TFinal getFinal()
    {
        return _final_;
    }

    public void setFinal(TFinal node)
    {
        if(_final_ != null)
        {
            _final_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _final_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_final_);
    }

    void removeChild(Node child)
    {
        if(_final_ == child)
        {
            _final_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_final_ == oldChild)
        {
            setFinal((TFinal) newChild);
            return;
        }

    }
}
