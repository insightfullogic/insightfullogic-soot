package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class ACmpeqBinop extends PBinop
{
    private TCmpeq _cmpeq_;

    public ACmpeqBinop()
    {
    }

    public ACmpeqBinop(
        TCmpeq _cmpeq_)
    {
        setCmpeq(_cmpeq_);

    }
    public Object clone()
    {
        return new ACmpeqBinop(
            (TCmpeq) cloneNode(_cmpeq_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseACmpeqBinop(this);
    }

    public TCmpeq getCmpeq()
    {
        return _cmpeq_;
    }

    public void setCmpeq(TCmpeq node)
    {
        if(_cmpeq_ != null)
        {
            _cmpeq_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _cmpeq_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_cmpeq_);
    }

    void removeChild(Node child)
    {
        if(_cmpeq_ == child)
        {
            _cmpeq_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_cmpeq_ == oldChild)
        {
            setCmpeq((TCmpeq) newChild);
            return;
        }

    }
}
