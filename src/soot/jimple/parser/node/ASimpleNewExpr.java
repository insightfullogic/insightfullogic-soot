package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class ASimpleNewExpr extends PNewExpr
{
    private TNew _new_;
    private PBaseType _baseType_;

    public ASimpleNewExpr()
    {
    }

    public ASimpleNewExpr(
        TNew _new_,
        PBaseType _baseType_)
    {
        setNew(_new_);

        setBaseType(_baseType_);

    }
    public Object clone()
    {
        return new ASimpleNewExpr(
            (TNew) cloneNode(_new_),
            (PBaseType) cloneNode(_baseType_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseASimpleNewExpr(this);
    }

    public TNew getNew()
    {
        return _new_;
    }

    public void setNew(TNew node)
    {
        if(_new_ != null)
        {
            _new_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _new_ = node;
    }

    public PBaseType getBaseType()
    {
        return _baseType_;
    }

    public void setBaseType(PBaseType node)
    {
        if(_baseType_ != null)
        {
            _baseType_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _baseType_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_new_)
            + toString(_baseType_);
    }

    void removeChild(Node child)
    {
        if(_new_ == child)
        {
            _new_ = null;
            return;
        }

        if(_baseType_ == child)
        {
            _baseType_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_new_ == oldChild)
        {
            setNew((TNew) newChild);
            return;
        }

        if(_baseType_ == oldChild)
        {
            setBaseType((PBaseType) newChild);
            return;
        }

    }
}
