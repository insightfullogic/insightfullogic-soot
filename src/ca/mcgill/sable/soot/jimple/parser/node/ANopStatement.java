package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ANopStatement extends PStatement
{
    private TNop _nop_;
    private TSemicolon _semicolon_;

    public ANopStatement()
    {
    }

    public ANopStatement(
        TNop _nop_,
        TSemicolon _semicolon_)
    {
        setNop(_nop_);

        setSemicolon(_semicolon_);

    }
    public Object clone()
    {
        return new ANopStatement(
            (TNop) cloneNode(_nop_),
            (TSemicolon) cloneNode(_semicolon_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANopStatement(this);
    }

    public TNop getNop()
    {
        return _nop_;
    }

    public void setNop(TNop node)
    {
        if(_nop_ != null)
        {
            _nop_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _nop_ = node;
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
            + toString(_nop_)
            + toString(_semicolon_);
    }

    void removeChild(Node child)
    {
        if(_nop_ == child)
        {
            _nop_ = null;
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
        if(_nop_ == oldChild)
        {
            setNop((TNop) newChild);
            return;
        }

        if(_semicolon_ == oldChild)
        {
            setSemicolon((TSemicolon) newChild);
            return;
        }

    }
}
