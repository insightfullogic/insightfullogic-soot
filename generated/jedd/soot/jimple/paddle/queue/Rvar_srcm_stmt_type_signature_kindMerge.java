package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rvar_srcm_stmt_type_signature_kindMerge extends Rvar_srcm_stmt_type_signature_kind {
    void add(final jedd.internal.RelationContainer tuple) { throw new RuntimeException(); }
    
    private Rvar_srcm_stmt_type_signature_kind in1;
    
    private Rvar_srcm_stmt_type_signature_kind in2;
    
    public Rvar_srcm_stmt_type_signature_kindMerge(Rvar_srcm_stmt_type_signature_kind in1,
                                                   Rvar_srcm_stmt_type_signature_kind in2) {
        super(in1.name + "+" + in2.name);
        this.in1 = in1;
        this.in2 = in2;
    }
    
    public Iterator iterator() {
        ;
        final Iterator it1 = in1.iterator();
        final Iterator it2 = in2.iterator();
        return new Iterator() {
            public boolean hasNext() { return it1.hasNext() || it2.hasNext(); }
            
            public Object next() {
                if (it1.hasNext()) return it1.next();
                return it2.next();
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        return new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), stmt.v(), kind.v(), var.v(), signature.v(), type.v() },
                                                   new PhysicalDomain[] { MS.v(), ST.v(), KD.v(), V1.v(), SG.v(), T1.v() },
                                                   ("return jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().r" +
                                                    "ead(in1.get()), in2.get()); at /tmp/olhotak/soot-trunk/src/s" +
                                                    "oot/jimple/paddle/queue/Rvar_srcm_stmt_type_signature_kindMe" +
                                                    "rge.jedd:52,8-14"),
                                                   jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(in1.get()),
                                                                                in2.get()));
    }
    
    public boolean hasNext() { return in1.hasNext() || in2.hasNext(); }
}
