package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class TTo extends Token
{
    public TTo()
    {
        super.setText("to");
    }

    public TTo(int line, int pos)
    {
        super.setText("to");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TTo(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTTo(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TTo text.");
    }
}
