package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class X2PModifier extends XPModifier
{
    private PModifier _pModifier_;

    public X2PModifier()
    {
    }

    public X2PModifier(
        PModifier _pModifier_)
    {
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
            toString(_pModifier_);
    }
}
