package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class ACaseStmt extends PCaseStmt
{
    private PCaseLabel _caseLabel_;
    private TColon _colon_;
    private PGotoStmt _gotoStmt_;

    public ACaseStmt()
    {
    }

    public ACaseStmt(
        PCaseLabel _caseLabel_,
        TColon _colon_,
        PGotoStmt _gotoStmt_)
    {
        setCaseLabel(_caseLabel_);

        setColon(_colon_);

        setGotoStmt(_gotoStmt_);

    }
    public Object clone()
    {
        return new ACaseStmt(
            (PCaseLabel) cloneNode(_caseLabel_),
            (TColon) cloneNode(_colon_),
            (PGotoStmt) cloneNode(_gotoStmt_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseACaseStmt(this);
    }

    public PCaseLabel getCaseLabel()
    {
        return _caseLabel_;
    }

    public void setCaseLabel(PCaseLabel node)
    {
        if(_caseLabel_ != null)
        {
            _caseLabel_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _caseLabel_ = node;
    }

    public TColon getColon()
    {
        return _colon_;
    }

    public void setColon(TColon node)
    {
        if(_colon_ != null)
        {
            _colon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _colon_ = node;
    }

    public PGotoStmt getGotoStmt()
    {
        return _gotoStmt_;
    }

    public void setGotoStmt(PGotoStmt node)
    {
        if(_gotoStmt_ != null)
        {
            _gotoStmt_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _gotoStmt_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_caseLabel_)
            + toString(_colon_)
            + toString(_gotoStmt_);
    }

    void removeChild(Node child)
    {
        if(_caseLabel_ == child)
        {
            _caseLabel_ = null;
            return;
        }

        if(_colon_ == child)
        {
            _colon_ = null;
            return;
        }

        if(_gotoStmt_ == child)
        {
            _gotoStmt_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_caseLabel_ == oldChild)
        {
            setCaseLabel((PCaseLabel) newChild);
            return;
        }

        if(_colon_ == oldChild)
        {
            setColon((TColon) newChild);
            return;
        }

        if(_gotoStmt_ == oldChild)
        {
            setGotoStmt((PGotoStmt) newChild);
            return;
        }

    }
}
