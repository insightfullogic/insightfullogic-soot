package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import soot.jimple.parser.analysis.*;

public final class TLengthof extends Token
{
    public TLengthof()
    {
        super.setText("lengthof");
    }

    public TLengthof(int line, int pos)
    {
        super.setText("lengthof");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TLengthof(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTLengthof(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TLengthof text.");
    }
}
