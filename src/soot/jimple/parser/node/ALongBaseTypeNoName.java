package soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class ALongBaseTypeNoName extends PBaseTypeNoName
{
    private TLong _long_;

    public ALongBaseTypeNoName()
    {
    }

    public ALongBaseTypeNoName(
        TLong _long_)
    {
        setLong(_long_);

    }
    public Object clone()
    {
        return new ALongBaseTypeNoName(
            (TLong) cloneNode(_long_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALongBaseTypeNoName(this);
    }

    public TLong getLong()
    {
        return _long_;
    }

    public void setLong(TLong node)
    {
        if(_long_ != null)
        {
            _long_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _long_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_long_);
    }

    void removeChild(Node child)
    {
        if(_long_ == child)
        {
            _long_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_long_ == oldChild)
        {
            setLong((TLong) newChild);
            return;
        }

    }
}
