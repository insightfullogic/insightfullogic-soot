package ca.mcgill.sable.soot.jimple.parser.node;

import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

public final class TTransient extends Token
{
    public TTransient()
    {
        super.setText("transient");
    }

    public TTransient(int line, int pos)
    {
        super.setText("transient");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TTransient(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTTransient(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TTransient text.");
    }
}
