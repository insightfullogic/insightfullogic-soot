package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AConstantImmediate extends PImmediate
{
    private PConstant _constant_;

    public AConstantImmediate()
    {
    }

    public AConstantImmediate(
        PConstant _constant_)
    {
        setConstant(_constant_);

    }
    public Object clone()
    {
        return new AConstantImmediate(
            (PConstant) cloneNode(_constant_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAConstantImmediate(this);
    }

    public PConstant getConstant()
    {
        return _constant_;
    }

    public void setConstant(PConstant node)
    {
        if(_constant_ != null)
        {
            _constant_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _constant_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_constant_);
    }

    void removeChild(Node child)
    {
        if(_constant_ == child)
        {
            _constant_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_constant_ == oldChild)
        {
            setConstant((PConstant) newChild);
            return;
        }

    }
}
