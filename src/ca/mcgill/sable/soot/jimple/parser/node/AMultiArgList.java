package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class AMultiArgList extends PArgList
{
    private PImmediate _immediate_;
    private TComma _comma_;
    private PArgList _argList_;

    public AMultiArgList()
    {
    }

    public AMultiArgList(
        PImmediate _immediate_,
        TComma _comma_,
        PArgList _argList_)
    {
        setImmediate(_immediate_);

        setComma(_comma_);

        setArgList(_argList_);

    }
    public Object clone()
    {
        return new AMultiArgList(
            (PImmediate) cloneNode(_immediate_),
            (TComma) cloneNode(_comma_),
            (PArgList) cloneNode(_argList_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAMultiArgList(this);
    }

    public PImmediate getImmediate()
    {
        return _immediate_;
    }

    public void setImmediate(PImmediate node)
    {
        if(_immediate_ != null)
        {
            _immediate_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _immediate_ = node;
    }

    public TComma getComma()
    {
        return _comma_;
    }

    public void setComma(TComma node)
    {
        if(_comma_ != null)
        {
            _comma_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _comma_ = node;
    }

    public PArgList getArgList()
    {
        return _argList_;
    }

    public void setArgList(PArgList node)
    {
        if(_argList_ != null)
        {
            _argList_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _argList_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_immediate_)
            + toString(_comma_)
            + toString(_argList_);
    }

    void removeChild(Node child)
    {
        if(_immediate_ == child)
        {
            _immediate_ = null;
            return;
        }

        if(_comma_ == child)
        {
            _comma_ = null;
            return;
        }

        if(_argList_ == child)
        {
            _argList_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_immediate_ == oldChild)
        {
            setImmediate((PImmediate) newChild);
            return;
        }

        if(_comma_ == oldChild)
        {
            setComma((TComma) newChild);
            return;
        }

        if(_argList_ == oldChild)
        {
            setArgList((PArgList) newChild);
            return;
        }

    }
}
