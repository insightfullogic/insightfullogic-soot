package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class AQuotedNonvoidType extends PNonvoidType
{
    private TQuotedName _quotedName_;
    private final LinkedList _arrayBrackets_ = new TypedLinkedList(new ArrayBrackets_Cast());

    public AQuotedNonvoidType()
    {
    }

    public AQuotedNonvoidType(
        TQuotedName _quotedName_,
        List _arrayBrackets_)
    {
        setQuotedName(_quotedName_);

        {
            Object temp[] = _arrayBrackets_.toArray();
            for(int i = 0; i < temp.length; i++)
            {
                this._arrayBrackets_.add(temp[i]);
            }
        }

    }

    public AQuotedNonvoidType(
        TQuotedName _quotedName_,
        XPArrayBrackets _arrayBrackets_)
    {
        setQuotedName(_quotedName_);

        if(_arrayBrackets_ != null)
        {
            while(_arrayBrackets_ instanceof X1PArrayBrackets)
            {
                this._arrayBrackets_.addFirst(((X1PArrayBrackets) _arrayBrackets_).getPArrayBrackets());
                _arrayBrackets_ = ((X1PArrayBrackets) _arrayBrackets_).getXPArrayBrackets();
            }
            this._arrayBrackets_.addFirst(((X2PArrayBrackets) _arrayBrackets_).getPArrayBrackets());
        }

    }
    public Object clone()
    {
        return new AQuotedNonvoidType(
            (TQuotedName) cloneNode(_quotedName_),
            cloneList(_arrayBrackets_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAQuotedNonvoidType(this);
    }

    public TQuotedName getQuotedName()
    {
        return _quotedName_;
    }

    public void setQuotedName(TQuotedName node)
    {
        if(_quotedName_ != null)
        {
            _quotedName_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _quotedName_ = node;
    }

    public LinkedList getArrayBrackets()
    {
        return _arrayBrackets_;
    }

    public void setArrayBrackets(List list)
    {
        Object temp[] = list.toArray();
        for(int i = 0; i < temp.length; i++)
        {
            _arrayBrackets_.add(temp[i]);
        }
    }

    public String toString()
    {
        return ""
            + toString(_quotedName_)
            + toString(_arrayBrackets_);
    }

    void removeChild(Node child)
    {
        if(_quotedName_ == child)
        {
            _quotedName_ = null;
            return;
        }

        if(_arrayBrackets_.remove(child))
        {
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_quotedName_ == oldChild)
        {
            setQuotedName((TQuotedName) newChild);
            return;
        }

        for(ListIterator i = _arrayBrackets_.listIterator(); i.hasNext();)
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

    }

    private class ArrayBrackets_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PArrayBrackets node = (PArrayBrackets) o;

            if((node.parent() != null) &&
                (node.parent() != AQuotedNonvoidType.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != AQuotedNonvoidType.this))
            {
                node.parent(AQuotedNonvoidType.this);
            }

            return node;
        }
    }
}
