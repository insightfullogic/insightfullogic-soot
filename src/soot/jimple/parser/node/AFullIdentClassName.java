package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class AFullIdentClassName extends PClassName
{
    private TFullIdentifier _fullIdentifier_;

    public AFullIdentClassName()
    {
    }

    public AFullIdentClassName(
        TFullIdentifier _fullIdentifier_)
    {
        setFullIdentifier(_fullIdentifier_);

    }
    public Object clone()
    {
        return new AFullIdentClassName(
            (TFullIdentifier) cloneNode(_fullIdentifier_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFullIdentClassName(this);
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

    public String toString()
    {
        return ""
            + toString(_fullIdentifier_);
    }

    void removeChild(Node child)
    {
        if(_fullIdentifier_ == child)
        {
            _fullIdentifier_ = null;
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

    }
}
