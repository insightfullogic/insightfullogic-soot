package soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class AIntBaseType extends PBaseType
{
    private TInt _int_;

    public AIntBaseType()
    {
    }

    public AIntBaseType(
        TInt _int_)
    {
        setInt(_int_);

    }
    public Object clone()
    {
        return new AIntBaseType(
            (TInt) cloneNode(_int_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAIntBaseType(this);
    }

    public TInt getInt()
    {
        return _int_;
    }

    public void setInt(TInt node)
    {
        if(_int_ != null)
        {
            _int_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _int_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_int_);
    }

    void removeChild(Node child)
    {
        if(_int_ == child)
        {
            _int_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_int_ == oldChild)
        {
            setInt((TInt) newChild);
            return;
        }

    }
}
