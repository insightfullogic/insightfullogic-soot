package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TThrows extends Token
{
    public TThrows()
    {
        super.setText("throws");
    }

    public TThrows(int line, int pos)
    {
        super.setText("throws");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TThrows(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTThrows(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TThrows text.");
    }
}
