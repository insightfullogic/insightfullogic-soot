package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AStaticInvokeExpr extends PInvokeExpr
{
    private TStaticinvoke _staticinvoke_;
    private PMethodSignature _methodSignature_;
    private TLParen _lParen_;
    private PArgList _argList_;
    private TRParen _rParen_;

    public AStaticInvokeExpr()
    {
    }

    public AStaticInvokeExpr(
        TStaticinvoke _staticinvoke_,
        PMethodSignature _methodSignature_,
        TLParen _lParen_,
        PArgList _argList_,
        TRParen _rParen_)
    {
        setStaticinvoke(_staticinvoke_);

        setMethodSignature(_methodSignature_);

        setLParen(_lParen_);

        setArgList(_argList_);

        setRParen(_rParen_);

    }
    public Object clone()
    {
        return new AStaticInvokeExpr(
            (TStaticinvoke) cloneNode(_staticinvoke_),
            (PMethodSignature) cloneNode(_methodSignature_),
            (TLParen) cloneNode(_lParen_),
            (PArgList) cloneNode(_argList_),
            (TRParen) cloneNode(_rParen_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAStaticInvokeExpr(this);
    }

    public TStaticinvoke getStaticinvoke()
    {
        return _staticinvoke_;
    }

    public void setStaticinvoke(TStaticinvoke node)
    {
        if(_staticinvoke_ != null)
        {
            _staticinvoke_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _staticinvoke_ = node;
    }

    public PMethodSignature getMethodSignature()
    {
        return _methodSignature_;
    }

    public void setMethodSignature(PMethodSignature node)
    {
        if(_methodSignature_ != null)
        {
            _methodSignature_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _methodSignature_ = node;
    }

    public TLParen getLParen()
    {
        return _lParen_;
    }

    public void setLParen(TLParen node)
    {
        if(_lParen_ != null)
        {
            _lParen_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _lParen_ = node;
    }

    public PArgList getArgList()
    {
        return _argList_;
    }

    public void setArgList(PArgList node)
    {
        if(_argList_ != null)
        {
            _argList_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _argList_ = node;
    }

    public TRParen getRParen()
    {
        return _rParen_;
    }

    public void setRParen(TRParen node)
    {
        if(_rParen_ != null)
        {
            _rParen_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _rParen_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_staticinvoke_)
            + toString(_methodSignature_)
            + toString(_lParen_)
            + toString(_argList_)
            + toString(_rParen_);
    }

    void removeChild(Node child)
    {
        if(_staticinvoke_ == child)
        {
            _staticinvoke_ = null;
            return;
        }

        if(_methodSignature_ == child)
        {
            _methodSignature_ = null;
            return;
        }

        if(_lParen_ == child)
        {
            _lParen_ = null;
            return;
        }

        if(_argList_ == child)
        {
            _argList_ = null;
            return;
        }

        if(_rParen_ == child)
        {
            _rParen_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_staticinvoke_ == oldChild)
        {
            setStaticinvoke((TStaticinvoke) newChild);
            return;
        }

        if(_methodSignature_ == oldChild)
        {
            setMethodSignature((PMethodSignature) newChild);
            return;
        }

        if(_lParen_ == oldChild)
        {
            setLParen((TLParen) newChild);
            return;
        }

        if(_argList_ == oldChild)
        {
            setArgList((PArgList) newChild);
            return;
        }

        if(_rParen_ == oldChild)
        {
            setRParen((TRParen) newChild);
            return;
        }

    }
}
