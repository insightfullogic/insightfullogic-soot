package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TReturn extends Token
{
    public TReturn()
    {
        super.setText("return");
    }

    public TReturn(int line, int pos)
    {
        super.setText("return");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TReturn(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTReturn(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TReturn text.");
    }
}
