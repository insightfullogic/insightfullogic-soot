package soot.jimple.parser.node;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.jimple.parser.analysis.*;

public final class TRParen extends Token
{
    public TRParen()
    {
        super.setText(")");
    }

    public TRParen(int line, int pos)
    {
        super.setText(")");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TRParen(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTRParen(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TRParen text.");
    }
}
