package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class X1PStatement extends XPStatement
{
    private XPStatement _xPStatement_;
    private PStatement _pStatement_;

    public X1PStatement()
    {
    }

    public X1PStatement(
        XPStatement _xPStatement_,
        PStatement _pStatement_)
    {
        setXPStatement(_xPStatement_);
        setPStatement(_pStatement_);
    }

    public Object clone()
    {
        throw new RuntimeException("Unsupported Operation");
    }

    public void apply(Switch sw)
    {
        throw new RuntimeException("Switch not supported.");
    }

    public XPStatement getXPStatement()
    {
        return _xPStatement_;
    }

    public void setXPStatement(XPStatement node)
    {
        if(_xPStatement_ != null)
        {
            _xPStatement_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _xPStatement_ = node;
    }

    public PStatement getPStatement()
    {
        return _pStatement_;
    }

    public void setPStatement(PStatement node)
    {
        if(_pStatement_ != null)
        {
            _pStatement_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _pStatement_ = node;
    }

    void removeChild(Node child)
    {
        if(_xPStatement_ == child)
        {
            _xPStatement_ = null;
        }

        if(_pStatement_ == child)
        {
            _pStatement_ = null;
        }
    }

    void replaceChild(Node oldChild, Node newChild)
    {
    }

    public String toString()
    {
        return "" +
            toString(_xPStatement_) +
            toString(_pStatement_);
    }
}
