package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ACmpgeBinop extends PBinop
{
    private TCmpge _cmpge_;

    public ACmpgeBinop()
    {
    }

    public ACmpgeBinop(
        TCmpge _cmpge_)
    {
        setCmpge(_cmpge_);

    }
    public Object clone()
    {
        return new ACmpgeBinop(
            (TCmpge) cloneNode(_cmpge_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseACmpgeBinop(this);
    }

    public TCmpge getCmpge()
    {
        return _cmpge_;
    }

    public void setCmpge(TCmpge node)
    {
        if(_cmpge_ != null)
        {
            _cmpge_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _cmpge_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_cmpge_);
    }

    void removeChild(Node child)
    {
        if(_cmpge_ == child)
        {
            _cmpge_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_cmpge_ == oldChild)
        {
            setCmpge((TCmpge) newChild);
            return;
        }

    }
}
