package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class AMethodMember extends PMember
{
    private final LinkedList _modifier_ = new TypedLinkedList(new Modifier_Cast());
    private PType _type_;
    private PName _name_;
    private TLParen _lParen_;
    private PParameterList _parameterList_;
    private TRParen _rParen_;
    private PThrowsClause _throwsClause_;
    private PMethodBody _methodBody_;

    public AMethodMember()
    {
    }

    public AMethodMember(
        List _modifier_,
        PType _type_,
        PName _name_,
        TLParen _lParen_,
        PParameterList _parameterList_,
        TRParen _rParen_,
        PThrowsClause _throwsClause_,
        PMethodBody _methodBody_)
    {
        {
            Object temp[] = _modifier_.toArray();
            for(int i = 0; i < temp.length; i++)
            {
                this._modifier_.add(temp[i]);
            }
        }

        setType(_type_);

        setName(_name_);

        setLParen(_lParen_);

        setParameterList(_parameterList_);

        setRParen(_rParen_);

        setThrowsClause(_throwsClause_);

        setMethodBody(_methodBody_);

    }

    public AMethodMember(
        XPModifier _modifier_,
        PType _type_,
        PName _name_,
        TLParen _lParen_,
        PParameterList _parameterList_,
        TRParen _rParen_,
        PThrowsClause _throwsClause_,
        PMethodBody _methodBody_)
    {
        if(_modifier_ != null)
        {
            while(_modifier_ instanceof X1PModifier)
            {
                this._modifier_.addFirst(((X1PModifier) _modifier_).getPModifier());
                _modifier_ = ((X1PModifier) _modifier_).getXPModifier();
            }
            this._modifier_.addFirst(((X2PModifier) _modifier_).getPModifier());
        }

        setType(_type_);

        setName(_name_);

        setLParen(_lParen_);

        setParameterList(_parameterList_);

        setRParen(_rParen_);

        setThrowsClause(_throwsClause_);

        setMethodBody(_methodBody_);

    }
    public Object clone()
    {
        return new AMethodMember(
            cloneList(_modifier_),
            (PType) cloneNode(_type_),
            (PName) cloneNode(_name_),
            (TLParen) cloneNode(_lParen_),
            (PParameterList) cloneNode(_parameterList_),
            (TRParen) cloneNode(_rParen_),
            (PThrowsClause) cloneNode(_throwsClause_),
            (PMethodBody) cloneNode(_methodBody_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAMethodMember(this);
    }

    public LinkedList getModifier()
    {
        return _modifier_;
    }

    public void setModifier(List list)
    {
        Object temp[] = list.toArray();
        for(int i = 0; i < temp.length; i++)
        {
            _modifier_.add(temp[i]);
        }
    }

    public PType getType()
    {
        return _type_;
    }

    public void setType(PType node)
    {
        if(_type_ != null)
        {
            _type_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _type_ = node;
    }

    public PName getName()
    {
        return _name_;
    }

    public void setName(PName node)
    {
        if(_name_ != null)
        {
            _name_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _name_ = node;
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

    public PParameterList getParameterList()
    {
        return _parameterList_;
    }

    public void setParameterList(PParameterList node)
    {
        if(_parameterList_ != null)
        {
            _parameterList_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _parameterList_ = node;
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

    public PThrowsClause getThrowsClause()
    {
        return _throwsClause_;
    }

    public void setThrowsClause(PThrowsClause node)
    {
        if(_throwsClause_ != null)
        {
            _throwsClause_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _throwsClause_ = node;
    }

    public PMethodBody getMethodBody()
    {
        return _methodBody_;
    }

    public void setMethodBody(PMethodBody node)
    {
        if(_methodBody_ != null)
        {
            _methodBody_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _methodBody_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_modifier_)
            + toString(_type_)
            + toString(_name_)
            + toString(_lParen_)
            + toString(_parameterList_)
            + toString(_rParen_)
            + toString(_throwsClause_)
            + toString(_methodBody_);
    }

    void removeChild(Node child)
    {
        if(_modifier_.remove(child))
        {
            return;
        }

        if(_type_ == child)
        {
            _type_ = null;
            return;
        }

        if(_name_ == child)
        {
            _name_ = null;
            return;
        }

        if(_lParen_ == child)
        {
            _lParen_ = null;
            return;
        }

        if(_parameterList_ == child)
        {
            _parameterList_ = null;
            return;
        }

        if(_rParen_ == child)
        {
            _rParen_ = null;
            return;
        }

        if(_throwsClause_ == child)
        {
            _throwsClause_ = null;
            return;
        }

        if(_methodBody_ == child)
        {
            _methodBody_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        for(ListIterator i = _modifier_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set(newChild);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        if(_type_ == oldChild)
        {
            setType((PType) newChild);
            return;
        }

        if(_name_ == oldChild)
        {
            setName((PName) newChild);
            return;
        }

        if(_lParen_ == oldChild)
        {
            setLParen((TLParen) newChild);
            return;
        }

        if(_parameterList_ == oldChild)
        {
            setParameterList((PParameterList) newChild);
            return;
        }

        if(_rParen_ == oldChild)
        {
            setRParen((TRParen) newChild);
            return;
        }

        if(_throwsClause_ == oldChild)
        {
            setThrowsClause((PThrowsClause) newChild);
            return;
        }

        if(_methodBody_ == oldChild)
        {
            setMethodBody((PMethodBody) newChild);
            return;
        }

    }

    private class Modifier_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PModifier node = (PModifier) o;

            if((node.parent() != null) &&
                (node.parent() != AMethodMember.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != AMethodMember.this))
            {
                node.parent(AMethodMember.this);
            }

            return node;
        }
    }
}
