package ca.mcgill.sable.soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TSynchronized extends Token
{
    public TSynchronized()
    {
        super.setText("synchronized");
    }

    public TSynchronized(int line, int pos)
    {
        super.setText("synchronized");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TSynchronized(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTSynchronized(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TSynchronized text.");
    }
}
