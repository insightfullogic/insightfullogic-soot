package soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class ANullConstant extends PConstant
{
    private TNull _null_;

    public ANullConstant()
    {
    }

    public ANullConstant(
        TNull _null_)
    {
        setNull(_null_);

    }
    public Object clone()
    {
        return new ANullConstant(
            (TNull) cloneNode(_null_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANullConstant(this);
    }

    public TNull getNull()
    {
        return _null_;
    }

    public void setNull(TNull node)
    {
        if(_null_ != null)
        {
            _null_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _null_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_null_);
    }

    void removeChild(Node child)
    {
        if(_null_ == child)
        {
            _null_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_null_ == oldChild)
        {
            setNull((TNull) newChild);
            return;
        }

    }
}
