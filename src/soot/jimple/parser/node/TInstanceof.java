package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class TInstanceof extends Token
{
    public TInstanceof()
    {
        super.setText("instanceof");
    }

    public TInstanceof(int line, int pos)
    {
        super.setText("instanceof");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TInstanceof(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTInstanceof(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TInstanceof text.");
    }
}
