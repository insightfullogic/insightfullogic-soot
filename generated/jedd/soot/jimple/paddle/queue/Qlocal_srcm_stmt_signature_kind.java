package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qlocal_srcm_stmt_signature_kind {
    public Qlocal_srcm_stmt_signature_kind(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(Local _local, SootMethod _srcm, Unit _stmt, NumberedString _signature, Kind _kind);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rlocal_srcm_stmt_signature_kind reader(String rname);
    
    public Rlocal_srcm_stmt_signature_kind revreader(String rname) { return this.reader(rname); }
}
