package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class AFileBody extends PFileBody
{
    private TLBrace _lBrace_;
    private final LinkedList _member_ = new TypedLinkedList(new Member_Cast());
    private TRBrace _rBrace_;

    public AFileBody()
    {
    }

    public AFileBody(
        TLBrace _lBrace_,
        List _member_,
        TRBrace _rBrace_)
    {
        setLBrace(_lBrace_);

        {
            Object temp[] = _member_.toArray();
            for(int i = 0; i < temp.length; i++)
            {
                this._member_.add(temp[i]);
            }
        }

        setRBrace(_rBrace_);

    }

    public AFileBody(
        TLBrace _lBrace_,
        XPMember _member_,
        TRBrace _rBrace_)
    {
        setLBrace(_lBrace_);

        if(_member_ != null)
        {
            while(_member_ instanceof X1PMember)
            {
                this._member_.addFirst(((X1PMember) _member_).getPMember());
                _member_ = ((X1PMember) _member_).getXPMember();
            }
            this._member_.addFirst(((X2PMember) _member_).getPMember());
        }

        setRBrace(_rBrace_);

    }
    public Object clone()
    {
        return new AFileBody(
            (TLBrace) cloneNode(_lBrace_),
            cloneList(_member_),
            (TRBrace) cloneNode(_rBrace_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFileBody(this);
    }

    public TLBrace getLBrace()
    {
        return _lBrace_;
    }

    public void setLBrace(TLBrace node)
    {
        if(_lBrace_ != null)
        {
            _lBrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _lBrace_ = node;
    }

    public LinkedList getMember()
    {
        return _member_;
    }

    public void setMember(List list)
    {
        Object temp[] = list.toArray();
        for(int i = 0; i < temp.length; i++)
        {
            _member_.add(temp[i]);
        }
    }

    public TRBrace getRBrace()
    {
        return _rBrace_;
    }

    public void setRBrace(TRBrace node)
    {
        if(_rBrace_ != null)
        {
            _rBrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _rBrace_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_lBrace_)
            + toString(_member_)
            + toString(_rBrace_);
    }

    void removeChild(Node child)
    {
        if(_lBrace_ == child)
        {
            _lBrace_ = null;
            return;
        }

        if(_member_.remove(child))
        {
            return;
        }

        if(_rBrace_ == child)
        {
            _rBrace_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_lBrace_ == oldChild)
        {
            setLBrace((TLBrace) newChild);
            return;
        }

        for(ListIterator i = _member_.listIterator(); i.hasNext();)
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

        if(_rBrace_ == oldChild)
        {
            setRBrace((TRBrace) newChild);
            return;
        }

    }

    private class Member_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PMember node = (PMember) o;

            if((node.parent() != null) &&
                (node.parent() != AFileBody.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != AFileBody.this))
            {
                node.parent(AFileBody.this);
            }

            return node;
        }
    }
}
