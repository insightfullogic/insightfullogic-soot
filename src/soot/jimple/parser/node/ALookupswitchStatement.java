package soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class ALookupswitchStatement extends PStatement
{
    private TLookupswitch _lookupswitch_;
    private TLParen _lParen_;
    private PImmediate _immediate_;
    private TRParen _rParen_;
    private TLBrace _lBrace_;
    private final LinkedList _caseStmt_ = new TypedLinkedList(new CaseStmt_Cast());
    private TRBrace _rBrace_;
    private TSemicolon _semicolon_;

    public ALookupswitchStatement()
    {
    }

    public ALookupswitchStatement(
        TLookupswitch _lookupswitch_,
        TLParen _lParen_,
        PImmediate _immediate_,
        TRParen _rParen_,
        TLBrace _lBrace_,
        List _caseStmt_,
        TRBrace _rBrace_,
        TSemicolon _semicolon_)
    {
        setLookupswitch(_lookupswitch_);

        setLParen(_lParen_);

        setImmediate(_immediate_);

        setRParen(_rParen_);

        setLBrace(_lBrace_);

        {
            Object temp[] = _caseStmt_.toArray();
            for(int i = 0; i < temp.length; i++)
            {
                this._caseStmt_.add(temp[i]);
            }
        }

        setRBrace(_rBrace_);

        setSemicolon(_semicolon_);

    }

    public ALookupswitchStatement(
        TLookupswitch _lookupswitch_,
        TLParen _lParen_,
        PImmediate _immediate_,
        TRParen _rParen_,
        TLBrace _lBrace_,
        XPCaseStmt _caseStmt_,
        TRBrace _rBrace_,
        TSemicolon _semicolon_)
    {
        setLookupswitch(_lookupswitch_);

        setLParen(_lParen_);

        setImmediate(_immediate_);

        setRParen(_rParen_);

        setLBrace(_lBrace_);

        if(_caseStmt_ != null)
        {
            while(_caseStmt_ instanceof X1PCaseStmt)
            {
                this._caseStmt_.addFirst(((X1PCaseStmt) _caseStmt_).getPCaseStmt());
                _caseStmt_ = ((X1PCaseStmt) _caseStmt_).getXPCaseStmt();
            }
            this._caseStmt_.addFirst(((X2PCaseStmt) _caseStmt_).getPCaseStmt());
        }

        setRBrace(_rBrace_);

        setSemicolon(_semicolon_);

    }
    public Object clone()
    {
        return new ALookupswitchStatement(
            (TLookupswitch) cloneNode(_lookupswitch_),
            (TLParen) cloneNode(_lParen_),
            (PImmediate) cloneNode(_immediate_),
            (TRParen) cloneNode(_rParen_),
            (TLBrace) cloneNode(_lBrace_),
            cloneList(_caseStmt_),
            (TRBrace) cloneNode(_rBrace_),
            (TSemicolon) cloneNode(_semicolon_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALookupswitchStatement(this);
    }

    public TLookupswitch getLookupswitch()
    {
        return _lookupswitch_;
    }

    public void setLookupswitch(TLookupswitch node)
    {
        if(_lookupswitch_ != null)
        {
            _lookupswitch_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _lookupswitch_ = node;
    }

    public TLParen getLParen()
    {
        return _lParen_;
    }

    public void setLParen(TLParen node)
    {
        if(_lParen_ != null)
        {
            _lParen_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _lParen_ = node;
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

    public TRParen getRParen()
    {
        return _rParen_;
    }

    public void setRParen(TRParen node)
    {
        if(_rParen_ != null)
        {
            _rParen_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _rParen_ = node;
    }

    public TLBrace getLBrace()
    {
        return _lBrace_;
    }

    public void setLBrace(TLBrace node)
    {
        if(_lBrace_ != null)
        {
            _lBrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _lBrace_ = node;
    }

    public LinkedList getCaseStmt()
    {
        return _caseStmt_;
    }

    public void setCaseStmt(List list)
    {
        Object temp[] = list.toArray();
        for(int i = 0; i < temp.length; i++)
        {
            _caseStmt_.add(temp[i]);
        }
    }

    public TRBrace getRBrace()
    {
        return _rBrace_;
    }

    public void setRBrace(TRBrace node)
    {
        if(_rBrace_ != null)
        {
            _rBrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _rBrace_ = node;
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
            + toString(_lookupswitch_)
            + toString(_lParen_)
            + toString(_immediate_)
            + toString(_rParen_)
            + toString(_lBrace_)
            + toString(_caseStmt_)
            + toString(_rBrace_)
            + toString(_semicolon_);
    }

    void removeChild(Node child)
    {
        if(_lookupswitch_ == child)
        {
            _lookupswitch_ = null;
            return;
        }

        if(_lParen_ == child)
        {
            _lParen_ = null;
            return;
        }

        if(_immediate_ == child)
        {
            _immediate_ = null;
            return;
        }

        if(_rParen_ == child)
        {
            _rParen_ = null;
            return;
        }

        if(_lBrace_ == child)
        {
            _lBrace_ = null;
            return;
        }

        if(_caseStmt_.remove(child))
        {
            return;
        }

        if(_rBrace_ == child)
        {
            _rBrace_ = null;
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
        if(_lookupswitch_ == oldChild)
        {
            setLookupswitch((TLookupswitch) newChild);
            return;
        }

        if(_lParen_ == oldChild)
        {
            setLParen((TLParen) newChild);
            return;
        }

        if(_immediate_ == oldChild)
        {
            setImmediate((PImmediate) newChild);
            return;
        }

        if(_rParen_ == oldChild)
        {
            setRParen((TRParen) newChild);
            return;
        }

        if(_lBrace_ == oldChild)
        {
            setLBrace((TLBrace) newChild);
            return;
        }

        for(ListIterator i = _caseStmt_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set(newChild);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        if(_rBrace_ == oldChild)
        {
            setRBrace((TRBrace) newChild);
            return;
        }

        if(_semicolon_ == oldChild)
        {
            setSemicolon((TSemicolon) newChild);
            return;
        }

    }

    private class CaseStmt_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PCaseStmt node = (PCaseStmt) o;

            if((node.parent() != null) &&
                (node.parent() != ALookupswitchStatement.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != ALookupswitchStatement.this))
            {
                node.parent(ALookupswitchStatement.this);
            }

            return node;
        }
    }
}
