package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AArrayRef extends PArrayRef
{
    private TIdentifier _identifier_;
    private PFixedArrayDescriptor _fixedArrayDescriptor_;

    public AArrayRef()
    {
    }

    public AArrayRef(
        TIdentifier _identifier_,
        PFixedArrayDescriptor _fixedArrayDescriptor_)
    {
        setIdentifier(_identifier_);

        setFixedArrayDescriptor(_fixedArrayDescriptor_);

    }
    public Object clone()
    {
        return new AArrayRef(
            (TIdentifier) cloneNode(_identifier_),
            (PFixedArrayDescriptor) cloneNode(_fixedArrayDescriptor_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAArrayRef(this);
    }

    public TIdentifier getIdentifier()
    {
        return _identifier_;
    }

    public void setIdentifier(TIdentifier node)
    {
        if(_identifier_ != null)
        {
            _identifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _identifier_ = node;
    }

    public PFixedArrayDescriptor getFixedArrayDescriptor()
    {
        return _fixedArrayDescriptor_;
    }

    public void setFixedArrayDescriptor(PFixedArrayDescriptor node)
    {
        if(_fixedArrayDescriptor_ != null)
        {
            _fixedArrayDescriptor_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _fixedArrayDescriptor_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_identifier_)
            + toString(_fixedArrayDescriptor_);
    }

    void removeChild(Node child)
    {
        if(_identifier_ == child)
        {
            _identifier_ = null;
            return;
        }

        if(_fixedArrayDescriptor_ == child)
        {
            _fixedArrayDescriptor_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_identifier_ == oldChild)
        {
            setIdentifier((TIdentifier) newChild);
            return;
        }

        if(_fixedArrayDescriptor_ == oldChild)
        {
            setFixedArrayDescriptor((PFixedArrayDescriptor) newChild);
            return;
        }

    }
}
