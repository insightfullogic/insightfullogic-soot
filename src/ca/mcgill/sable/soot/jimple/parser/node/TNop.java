package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TNop extends Token
{
    public TNop()
    {
        super.setText("nop");
    }

    public TNop(int line, int pos)
    {
        super.setText("nop");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TNop(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTNop(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TNop text.");
    }
}
