package soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class AStaticModifier extends PModifier
{
    private TStatic _static_;

    public AStaticModifier()
    {
    }

    public AStaticModifier(
        TStatic _static_)
    {
        setStatic(_static_);

    }
    public Object clone()
    {
        return new AStaticModifier(
            (TStatic) cloneNode(_static_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAStaticModifier(this);
    }

    public TStatic getStatic()
    {
        return _static_;
    }

    public void setStatic(TStatic node)
    {
        if(_static_ != null)
        {
            _static_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _static_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_static_);
    }

    void removeChild(Node child)
    {
        if(_static_ == child)
        {
            _static_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_static_ == oldChild)
        {
            setStatic((TStatic) newChild);
            return;
        }

    }
}
