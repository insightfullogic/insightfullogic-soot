package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class AStringConstant extends PConstant
{
    private TStringConstant _stringConstant_;

    public AStringConstant()
    {
    }

    public AStringConstant(
        TStringConstant _stringConstant_)
    {
        setStringConstant(_stringConstant_);

    }
    public Object clone()
    {
        return new AStringConstant(
            (TStringConstant) cloneNode(_stringConstant_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAStringConstant(this);
    }

    public TStringConstant getStringConstant()
    {
        return _stringConstant_;
    }

    public void setStringConstant(TStringConstant node)
    {
        if(_stringConstant_ != null)
        {
            _stringConstant_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _stringConstant_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_stringConstant_);
    }

    void removeChild(Node child)
    {
        if(_stringConstant_ == child)
        {
            _stringConstant_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_stringConstant_ == oldChild)
        {
            setStringConstant((TStringConstant) newChild);
            return;
        }

    }
}
