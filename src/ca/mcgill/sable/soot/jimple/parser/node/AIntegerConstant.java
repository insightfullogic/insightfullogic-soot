package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AIntegerConstant extends PConstant
{
    private TMinus _minus_;
    private TIntegerConstant _integerConstant_;

    public AIntegerConstant()
    {
    }

    public AIntegerConstant(
        TMinus _minus_,
        TIntegerConstant _integerConstant_)
    {
        setMinus(_minus_);

        setIntegerConstant(_integerConstant_);

    }
    public Object clone()
    {
        return new AIntegerConstant(
            (TMinus) cloneNode(_minus_),
            (TIntegerConstant) cloneNode(_integerConstant_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAIntegerConstant(this);
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

    public TIntegerConstant getIntegerConstant()
    {
        return _integerConstant_;
    }

    public void setIntegerConstant(TIntegerConstant node)
    {
        if(_integerConstant_ != null)
        {
            _integerConstant_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _integerConstant_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_minus_)
            + toString(_integerConstant_);
    }

    void removeChild(Node child)
    {
        if(_minus_ == child)
        {
            _minus_ = null;
            return;
        }

        if(_integerConstant_ == child)
        {
            _integerConstant_ = null;
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

        if(_integerConstant_ == oldChild)
        {
            setIntegerConstant((TIntegerConstant) newChild);
            return;
        }

    }
}
