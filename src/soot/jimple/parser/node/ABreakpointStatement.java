package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class ABreakpointStatement extends PStatement
{
    private TBreakpoint _breakpoint_;
    private TSemicolon _semicolon_;

    public ABreakpointStatement()
    {
    }

    public ABreakpointStatement(
        TBreakpoint _breakpoint_,
        TSemicolon _semicolon_)
    {
        setBreakpoint(_breakpoint_);

        setSemicolon(_semicolon_);

    }
    public Object clone()
    {
        return new ABreakpointStatement(
            (TBreakpoint) cloneNode(_breakpoint_),
            (TSemicolon) cloneNode(_semicolon_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseABreakpointStatement(this);
    }

    public TBreakpoint getBreakpoint()
    {
        return _breakpoint_;
    }

    public void setBreakpoint(TBreakpoint node)
    {
        if(_breakpoint_ != null)
        {
            _breakpoint_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _breakpoint_ = node;
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
            + toString(_breakpoint_)
            + toString(_semicolon_);
    }

    void removeChild(Node child)
    {
        if(_breakpoint_ == child)
        {
            _breakpoint_ = null;
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
        if(_breakpoint_ == oldChild)
        {
            setBreakpoint((TBreakpoint) newChild);
            return;
        }

        if(_semicolon_ == oldChild)
        {
            setSemicolon((TSemicolon) newChild);
            return;
        }

    }
}
