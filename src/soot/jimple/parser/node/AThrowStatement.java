package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class AThrowStatement extends PStatement
{
    private TThrow _throw_;
    private PImmediate _immediate_;
    private TSemicolon _semicolon_;

    public AThrowStatement()
    {
    }

    public AThrowStatement(
        TThrow _throw_,
        PImmediate _immediate_,
        TSemicolon _semicolon_)
    {
        setThrow(_throw_);

        setImmediate(_immediate_);

        setSemicolon(_semicolon_);

    }
    public Object clone()
    {
        return new AThrowStatement(
            (TThrow) cloneNode(_throw_),
            (PImmediate) cloneNode(_immediate_),
            (TSemicolon) cloneNode(_semicolon_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAThrowStatement(this);
    }

    public TThrow getThrow()
    {
        return _throw_;
    }

    public void setThrow(TThrow node)
    {
        if(_throw_ != null)
        {
            _throw_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _throw_ = node;
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

    public TSemicolon getSemicolon()
    {
        return _semicolon_;
    }

    public void setSemicolon(TSemicolon node)
    {
        if(_semicolon_ != null)
        {
            _semicolon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _semicolon_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_throw_)
            + toString(_immediate_)
            + toString(_semicolon_);
    }

    void removeChild(Node child)
    {
        if(_throw_ == child)
        {
            _throw_ = null;
            return;
        }

        if(_immediate_ == child)
        {
            _immediate_ = null;
            return;
        }

        if(_semicolon_ == child)
        {
            _semicolon_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_throw_ == oldChild)
        {
            setThrow((TThrow) newChild);
            return;
        }

        if(_immediate_ == oldChild)
        {
            setImmediate((PImmediate) newChild);
            return;
        }

        if(_semicolon_ == oldChild)
        {
            setSemicolon((TSemicolon) newChild);
            return;
        }

    }
}
