package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class X1PModifier extends XPModifier
{
    private XPModifier _xPModifier_;
    private PModifier _pModifier_;

    public X1PModifier()
    {
    }

    public X1PModifier(
        XPModifier _xPModifier_,
        PModifier _pModifier_)
    {
        setXPModifier(_xPModifier_);
        setPModifier(_pModifier_);
    }

    public Object clone()
    {
        throw new RuntimeException("Unsupported Operation");
    }

    public void apply(Switch sw)
    {
        throw new RuntimeException("Switch not supported.");
    }

    public XPModifier getXPModifier()
    {
        return _xPModifier_;
    }

    public void setXPModifier(XPModifier node)
    {
        if(_xPModifier_ != null)
        {
            _xPModifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _xPModifier_ = node;
    }

    public PModifier getPModifier()
    {
        return _pModifier_;
    }

    public void setPModifier(PModifier node)
    {
        if(_pModifier_ != null)
        {
            _pModifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _pModifier_ = node;
    }

    void removeChild(Node child)
    {
        if(_xPModifier_ == child)
        {
            _xPModifier_ = null;
        }

        if(_pModifier_ == child)
        {
            _pModifier_ = null;
        }
    }

    void replaceChild(Node oldChild, Node newChild)
    {
    }

    public String toString()
    {
        return "" +
            toString(_xPModifier_) +
            toString(_pModifier_);
    }
}
