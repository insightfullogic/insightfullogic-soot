package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AProtectedModifier extends PModifier
{
    private TProtected _protected_;

    public AProtectedModifier()
    {
    }

    public AProtectedModifier(
        TProtected _protected_)
    {
        setProtected(_protected_);

    }
    public Object clone()
    {
        return new AProtectedModifier(
            (TProtected) cloneNode(_protected_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAProtectedModifier(this);
    }

    public TProtected getProtected()
    {
        return _protected_;
    }

    public void setProtected(TProtected node)
    {
        if(_protected_ != null)
        {
            _protected_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _protected_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_protected_);
    }

    void removeChild(Node child)
    {
        if(_protected_ == child)
        {
            _protected_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_protected_ == oldChild)
        {
            setProtected((TProtected) newChild);
            return;
        }

    }
}
