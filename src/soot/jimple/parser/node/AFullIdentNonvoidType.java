package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class AFullIdentNonvoidType extends PNonvoidType
{
    private TFullIdentifier _fullIdentifier_;
    private final LinkedList _arrayBrackets_ = new TypedLinkedList(new ArrayBrackets_Cast());

    public AFullIdentNonvoidType()
    {
    }

    public AFullIdentNonvoidType(
        TFullIdentifier _fullIdentifier_,
        List _arrayBrackets_)
    {
        setFullIdentifier(_fullIdentifier_);

        {
            Object temp[] = _arrayBrackets_.toArray();
            for(int i = 0; i < temp.length; i++)
            {
                this._arrayBrackets_.add(temp[i]);
            }
        }

    }

    public AFullIdentNonvoidType(
        TFullIdentifier _fullIdentifier_,
        XPArrayBrackets _arrayBrackets_)
    {
        setFullIdentifier(_fullIdentifier_);

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
        return new AFullIdentNonvoidType(
            (TFullIdentifier) cloneNode(_fullIdentifier_),
            cloneList(_arrayBrackets_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFullIdentNonvoidType(this);
    }

    public TFullIdentifier getFullIdentifier()
    {
        return _fullIdentifier_;
    }

    public void setFullIdentifier(TFullIdentifier node)
    {
        if(_fullIdentifier_ != null)
        {
            _fullIdentifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _fullIdentifier_ = node;
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
            + toString(_fullIdentifier_)
            + toString(_arrayBrackets_);
    }

    void removeChild(Node child)
    {
        if(_fullIdentifier_ == child)
        {
            _fullIdentifier_ = null;
            return;
        }

        if(_arrayBrackets_.remove(child))
        {
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_fullIdentifier_ == oldChild)
        {
            setFullIdentifier((TFullIdentifier) newChild);
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
                (node.parent() != AFullIdentNonvoidType.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != AFullIdentNonvoidType.this))
            {
                node.parent(AFullIdentNonvoidType.this);
            }

            return node;
        }
    }
}
