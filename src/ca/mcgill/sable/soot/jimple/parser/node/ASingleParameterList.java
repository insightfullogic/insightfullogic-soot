package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ASingleParameterList extends PParameterList
{
    private PParameter _parameter_;

    public ASingleParameterList()
    {
    }

    public ASingleParameterList(
        PParameter _parameter_)
    {
        setParameter(_parameter_);

    }
    public Object clone()
    {
        return new ASingleParameterList(
            (PParameter) cloneNode(_parameter_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseASingleParameterList(this);
    }

    public PParameter getParameter()
    {
        return _parameter_;
    }

    public void setParameter(PParameter node)
    {
        if(_parameter_ != null)
        {
            _parameter_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _parameter_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_parameter_);
    }

    void removeChild(Node child)
    {
        if(_parameter_ == child)
        {
            _parameter_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_parameter_ == oldChild)
        {
            setParameter((PParameter) newChild);
            return;
        }

    }
}
