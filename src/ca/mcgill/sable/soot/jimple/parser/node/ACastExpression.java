package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ACastExpression extends PExpression
{
    private TLParen _lParen_;
    private PNonvoidType _nonvoidType_;
    private TRParen _rParen_;
    private PLocalName _localName_;

    public ACastExpression()
    {
    }

    public ACastExpression(
        TLParen _lParen_,
        PNonvoidType _nonvoidType_,
        TRParen _rParen_,
        PLocalName _localName_)
    {
        setLParen(_lParen_);

        setNonvoidType(_nonvoidType_);

        setRParen(_rParen_);

        setLocalName(_localName_);

    }
    public Object clone()
    {
        return new ACastExpression(
            (TLParen) cloneNode(_lParen_),
            (PNonvoidType) cloneNode(_nonvoidType_),
            (TRParen) cloneNode(_rParen_),
            (PLocalName) cloneNode(_localName_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseACastExpression(this);
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

    public PNonvoidType getNonvoidType()
    {
        return _nonvoidType_;
    }

    public void setNonvoidType(PNonvoidType node)
    {
        if(_nonvoidType_ != null)
        {
            _nonvoidType_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _nonvoidType_ = node;
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

    public PLocalName getLocalName()
    {
        return _localName_;
    }

    public void setLocalName(PLocalName node)
    {
        if(_localName_ != null)
        {
            _localName_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _localName_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_lParen_)
            + toString(_nonvoidType_)
            + toString(_rParen_)
            + toString(_localName_);
    }

    void removeChild(Node child)
    {
        if(_lParen_ == child)
        {
            _lParen_ = null;
            return;
        }

        if(_nonvoidType_ == child)
        {
            _nonvoidType_ = null;
            return;
        }

        if(_rParen_ == child)
        {
            _rParen_ = null;
            return;
        }

        if(_localName_ == child)
        {
            _localName_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_lParen_ == oldChild)
        {
            setLParen((TLParen) newChild);
            return;
        }

        if(_nonvoidType_ == oldChild)
        {
            setNonvoidType((PNonvoidType) newChild);
            return;
        }

        if(_rParen_ == oldChild)
        {
            setRParen((TRParen) newChild);
            return;
        }

        if(_localName_ == oldChild)
        {
            setLocalName((PLocalName) newChild);
            return;
        }

    }
}
