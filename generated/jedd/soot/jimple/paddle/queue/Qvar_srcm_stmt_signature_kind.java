package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qvar_srcm_stmt_signature_kind {
    public Qvar_srcm_stmt_signature_kind(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(VarNode _var, SootMethod _srcm, Unit _stmt, NumberedString _signature, Kind _kind);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rvar_srcm_stmt_signature_kind reader(String rname);
    
    public Rvar_srcm_stmt_signature_kind revreader(String rname) { return reader(rname); }
    
    public void add(Rvar_srcm_stmt_signature_kind.Tuple in) {
        add(in.var(), in.srcm(), in.stmt(), in.signature(), in.kind());
    }
}
