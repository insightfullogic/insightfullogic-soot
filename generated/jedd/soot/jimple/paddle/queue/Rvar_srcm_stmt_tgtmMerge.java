package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rvar_srcm_stmt_tgtmMerge extends Rvar_srcm_stmt_tgtm {
    void add(final jedd.internal.RelationContainer tuple) { throw new RuntimeException(); }
    
    private Rvar_srcm_stmt_tgtm in1;
    
    private Rvar_srcm_stmt_tgtm in2;
    
    public Rvar_srcm_stmt_tgtmMerge(Rvar_srcm_stmt_tgtm in1, Rvar_srcm_stmt_tgtm in2) {
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
        return new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), stmt.v(), var.v(), tgtm.v() },
                                                   new PhysicalDomain[] { MS.v(), ST.v(), V1.v(), MT.v() },
                                                   ("return jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().r" +
                                                    "ead(in1.get()), in2.get()); at /tmp/olhotak/soot-trunk/src/s" +
                                                    "oot/jimple/paddle/queue/Rvar_srcm_stmt_tgtmMerge.jedd:52,8-1" +
                                                    "4"),
                                                   jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(in1.get()),
                                                                                in2.get()));
    }
    
    public boolean hasNext() { return in1.hasNext() || in2.hasNext(); }
}
