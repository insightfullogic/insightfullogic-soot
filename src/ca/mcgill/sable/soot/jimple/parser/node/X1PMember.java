package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class X1PMember extends XPMember
{
    private XPMember _xPMember_;
    private PMember _pMember_;

    public X1PMember()
    {
    }

    public X1PMember(
        XPMember _xPMember_,
        PMember _pMember_)
    {
        setXPMember(_xPMember_);
        setPMember(_pMember_);
    }

    public Object clone()
    {
        throw new RuntimeException("Unsupported Operation");
    }

    public void apply(Switch sw)
    {
        throw new RuntimeException("Switch not supported.");
    }

    public XPMember getXPMember()
    {
        return _xPMember_;
    }

    public void setXPMember(XPMember node)
    {
        if(_xPMember_ != null)
        {
            _xPMember_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _xPMember_ = node;
    }

    public PMember getPMember()
    {
        return _pMember_;
    }

    public void setPMember(PMember node)
    {
        if(_pMember_ != null)
        {
            _pMember_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _pMember_ = node;
    }

    void removeChild(Node child)
    {
        if(_xPMember_ == child)
        {
            _xPMember_ = null;
        }

        if(_pMember_ == child)
        {
            _pMember_ = null;
        }
    }

    void replaceChild(Node oldChild, Node newChild)
    {
    }

    public String toString()
    {
        return "" +
            toString(_xPMember_) +
            toString(_pMember_);
    }
}
