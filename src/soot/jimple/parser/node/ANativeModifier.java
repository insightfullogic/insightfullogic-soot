package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class ANativeModifier extends PModifier
{
    private TNative _native_;

    public ANativeModifier()
    {
    }

    public ANativeModifier(
        TNative _native_)
    {
        setNative(_native_);

    }
    public Object clone()
    {
        return new ANativeModifier(
            (TNative) cloneNode(_native_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANativeModifier(this);
    }

    public TNative getNative()
    {
        return _native_;
    }

    public void setNative(TNative node)
    {
        if(_native_ != null)
        {
            _native_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _native_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_native_);
    }

    void removeChild(Node child)
    {
        if(_native_ == child)
        {
            _native_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_native_ == oldChild)
        {
            setNative((TNative) newChild);
            return;
        }

    }
}
