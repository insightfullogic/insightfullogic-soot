package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class AThrowsClause extends PThrowsClause
{
    private TThrows _throws_;
    private PClassNameList _classNameList_;

    public AThrowsClause()
    {
    }

    public AThrowsClause(
        TThrows _throws_,
        PClassNameList _classNameList_)
    {
        setThrows(_throws_);

        setClassNameList(_classNameList_);

    }
    public Object clone()
    {
        return new AThrowsClause(
            (TThrows) cloneNode(_throws_),
            (PClassNameList) cloneNode(_classNameList_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAThrowsClause(this);
    }

    public TThrows getThrows()
    {
        return _throws_;
    }

    public void setThrows(TThrows node)
    {
        if(_throws_ != null)
        {
            _throws_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _throws_ = node;
    }

    public PClassNameList getClassNameList()
    {
        return _classNameList_;
    }

    public void setClassNameList(PClassNameList node)
    {
        if(_classNameList_ != null)
        {
            _classNameList_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _classNameList_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_throws_)
            + toString(_classNameList_);
    }

    void removeChild(Node child)
    {
        if(_throws_ == child)
        {
            _throws_ = null;
            return;
        }

        if(_classNameList_ == child)
        {
            _classNameList_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_throws_ == oldChild)
        {
            setThrows((TThrows) newChild);
            return;
        }

        if(_classNameList_ == oldChild)
        {
            setClassNameList((PClassNameList) newChild);
            return;
        }

    }
}
