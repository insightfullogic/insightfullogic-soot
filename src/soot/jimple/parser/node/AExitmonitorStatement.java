package soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class AExitmonitorStatement extends PStatement
{
    private TExitmonitor _exitmonitor_;
    private PImmediate _immediate_;
    private TSemicolon _semicolon_;

    public AExitmonitorStatement()
    {
    }

    public AExitmonitorStatement(
        TExitmonitor _exitmonitor_,
        PImmediate _immediate_,
        TSemicolon _semicolon_)
    {
        setExitmonitor(_exitmonitor_);

        setImmediate(_immediate_);

        setSemicolon(_semicolon_);

    }
    public Object clone()
    {
        return new AExitmonitorStatement(
            (TExitmonitor) cloneNode(_exitmonitor_),
            (PImmediate) cloneNode(_immediate_),
            (TSemicolon) cloneNode(_semicolon_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAExitmonitorStatement(this);
    }

    public TExitmonitor getExitmonitor()
    {
        return _exitmonitor_;
    }

    public void setExitmonitor(TExitmonitor node)
    {
        if(_exitmonitor_ != null)
        {
            _exitmonitor_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _exitmonitor_ = node;
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
            + toString(_exitmonitor_)
            + toString(_immediate_)
            + toString(_semicolon_);
    }

    void removeChild(Node child)
    {
        if(_exitmonitor_ == child)
        {
            _exitmonitor_ = null;
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
        if(_exitmonitor_ == oldChild)
        {
            setExitmonitor((TExitmonitor) newChild);
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
