package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ADivBinop extends PBinop
{
    private TDiv _div_;

    public ADivBinop()
    {
    }

    public ADivBinop(
        TDiv _div_)
    {
        setDiv(_div_);

    }
    public Object clone()
    {
        return new ADivBinop(
            (TDiv) cloneNode(_div_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADivBinop(this);
    }

    public TDiv getDiv()
    {
        return _div_;
    }

    public void setDiv(TDiv node)
    {
        if(_div_ != null)
        {
            _div_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _div_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_div_);
    }

    void removeChild(Node child)
    {
        if(_div_ == child)
        {
            _div_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_div_ == oldChild)
        {
            setDiv((TDiv) newChild);
            return;
        }

    }
}
