package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class TByte extends Token
{
    public TByte()
    {
        super.setText("byte");
    }

    public TByte(int line, int pos)
    {
        super.setText("byte");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TByte(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTByte(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TByte text.");
    }
}
