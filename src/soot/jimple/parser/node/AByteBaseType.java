package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class AByteBaseType extends PBaseType
{
    private TByte _byte_;

    public AByteBaseType()
    {
    }

    public AByteBaseType(
        TByte _byte_)
    {
        setByte(_byte_);

    }
    public Object clone()
    {
        return new AByteBaseType(
            (TByte) cloneNode(_byte_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAByteBaseType(this);
    }

    public TByte getByte()
    {
        return _byte_;
    }

    public void setByte(TByte node)
    {
        if(_byte_ != null)
        {
            _byte_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _byte_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_byte_);
    }

    void removeChild(Node child)
    {
        if(_byte_ == child)
        {
            _byte_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_byte_ == oldChild)
        {
            setByte((TByte) newChild);
            return;
        }

    }
}
