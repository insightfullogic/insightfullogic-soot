package soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class AClassFileType extends PFileType
{
    private TClass _theclass_;

    public AClassFileType()
    {
    }

    public AClassFileType(
        TClass _theclass_)
    {
        setTheclass(_theclass_);

    }
    public Object clone()
    {
        return new AClassFileType(
            (TClass) cloneNode(_theclass_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAClassFileType(this);
    }

    public TClass getTheclass()
    {
        return _theclass_;
    }

    public void setTheclass(TClass node)
    {
        if(_theclass_ != null)
        {
            _theclass_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _theclass_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_theclass_);
    }

    void removeChild(Node child)
    {
        if(_theclass_ == child)
        {
            _theclass_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_theclass_ == oldChild)
        {
            setTheclass((TClass) newChild);
            return;
        }

    }
}
