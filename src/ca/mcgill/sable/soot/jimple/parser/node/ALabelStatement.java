package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class ALabelStatement extends PStatement
{
    private PLabelName _labelName_;
    private TColon _colon_;

    public ALabelStatement()
    {
    }

    public ALabelStatement(
        PLabelName _labelName_,
        TColon _colon_)
    {
        setLabelName(_labelName_);

        setColon(_colon_);

    }
    public Object clone()
    {
        return new ALabelStatement(
            (PLabelName) cloneNode(_labelName_),
            (TColon) cloneNode(_colon_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALabelStatement(this);
    }

    public PLabelName getLabelName()
    {
        return _labelName_;
    }

    public void setLabelName(PLabelName node)
    {
        if(_labelName_ != null)
        {
            _labelName_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _labelName_ = node;
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

    public String toString()
    {
        return ""
            + toString(_labelName_)
            + toString(_colon_);
    }

    void removeChild(Node child)
    {
        if(_labelName_ == child)
        {
            _labelName_ = null;
            return;
        }

        if(_colon_ == child)
        {
            _colon_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_labelName_ == oldChild)
        {
            setLabelName((PLabelName) newChild);
            return;
        }

        if(_colon_ == oldChild)
        {
            setColon((TColon) newChild);
            return;
        }

    }
}
