package soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class AFloatConstant extends PConstant
{
    private TMinus _minus_;
    private TFloatConstant _floatConstant_;

    public AFloatConstant()
    {
    }

    public AFloatConstant(
        TMinus _minus_,
        TFloatConstant _floatConstant_)
    {
        setMinus(_minus_);

        setFloatConstant(_floatConstant_);

    }
    public Object clone()
    {
        return new AFloatConstant(
            (TMinus) cloneNode(_minus_),
            (TFloatConstant) cloneNode(_floatConstant_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFloatConstant(this);
    }

    public TMinus getMinus()
    {
        return _minus_;
    }

    public void setMinus(TMinus node)
    {
        if(_minus_ != null)
        {
            _minus_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _minus_ = node;
    }

    public TFloatConstant getFloatConstant()
    {
        return _floatConstant_;
    }

    public void setFloatConstant(TFloatConstant node)
    {
        if(_floatConstant_ != null)
        {
            _floatConstant_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _floatConstant_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_minus_)
            + toString(_floatConstant_);
    }

    void removeChild(Node child)
    {
        if(_minus_ == child)
        {
            _minus_ = null;
            return;
        }

        if(_floatConstant_ == child)
        {
            _floatConstant_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_minus_ == oldChild)
        {
            setMinus((TMinus) newChild);
            return;
        }

        if(_floatConstant_ == oldChild)
        {
            setFloatConstant((TFloatConstant) newChild);
            return;
        }

    }
}
