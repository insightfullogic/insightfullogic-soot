package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class AExtendsClause extends PExtendsClause
{
    private TExtends _extends_;
    private PClassName _className_;

    public AExtendsClause()
    {
    }

    public AExtendsClause(
        TExtends _extends_,
        PClassName _className_)
    {
        setExtends(_extends_);

        setClassName(_className_);

    }
    public Object clone()
    {
        return new AExtendsClause(
            (TExtends) cloneNode(_extends_),
            (PClassName) cloneNode(_className_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAExtendsClause(this);
    }

    public TExtends getExtends()
    {
        return _extends_;
    }

    public void setExtends(TExtends node)
    {
        if(_extends_ != null)
        {
            _extends_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _extends_ = node;
    }

    public PClassName getClassName()
    {
        return _className_;
    }

    public void setClassName(PClassName node)
    {
        if(_className_ != null)
        {
            _className_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _className_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_extends_)
            + toString(_className_);
    }

    void removeChild(Node child)
    {
        if(_extends_ == child)
        {
            _extends_ = null;
            return;
        }

        if(_className_ == child)
        {
            _className_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_extends_ == oldChild)
        {
            setExtends((TExtends) newChild);
            return;
        }

        if(_className_ == oldChild)
        {
            setClassName((PClassName) newChild);
            return;
        }

    }
}
