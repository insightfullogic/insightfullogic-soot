package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrcc_src_dstc_dstBDD extends Rsrcc_src_dstc_dst {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                          new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcc:soot.jimple.padd" +
                                           "le.bdddomains.C1, soot.jimple.paddle.bdddomains.src:soot.jim" +
                                           "ple.paddle.bdddomains.V1, soot.jimple.paddle.bdddomains.dstc" +
                                           ":soot.jimple.paddle.bdddomains.C2, soot.jimple.paddle.bdddom" +
                                           "ains.dst:soot.jimple.paddle.bdddomains.V2> bdd at /home/rese" +
                                           "arch/ccl/olhota/soot-trunk/src/soot/jimple/paddle/queue/Rsrc" +
                                           "c_src_dstc_dstBDD.jedd:31,12-46"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rsrcc_src_dstc_dstBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        add(new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v(), dstc.v(), srcc.v() },
                                                new PhysicalDomain[] { V1.v(), V2.v(), C2.v(), C1.v() },
                                                ("add(bdd) at /home/research/ccl/olhota/soot-trunk/src/soot/ji" +
                                                 "mple/paddle/queue/Rsrcc_src_dstc_dstBDD.jedd:33,102-105"),
                                                bdd));
    }
    
    Rsrcc_src_dstc_dstBDD(String name) {
        super(name);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
    }
    
    public Iterator iterator() {
        ;
        return new Iterator() {
            private Iterator it;
            
            public boolean hasNext() {
                if (it != null && it.hasNext()) return true;
                if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD()))
                    return true;
                return false;
            }
            
            public Object next() {
                if (it == null || !it.hasNext()) {
                    it =
                      new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v(), dstc.v(), srcc.v() },
                                                          new PhysicalDomain[] { V1.v(), V2.v(), C2.v(), C1.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/research/ccl/" +
                                                           "olhota/soot-trunk/src/soot/jimple/paddle/queue/Rsrcc_src_dst" +
                                                           "c_dstBDD.jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((Context) components[0],
                                 (VarNode) components[1],
                                 (Context) components[2],
                                 (VarNode) components[3]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() },
                                              new PhysicalDomain[] { C1.v(), V1.v(), C2.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.src:soot.jimple.padd" +
                                               "le.bdddomains.V1, soot.jimple.paddle.bdddomains.dstc:soot.ji" +
                                               "mple.paddle.bdddomains.C2, soot.jimple.paddle.bdddomains.dst" +
                                               ":soot.jimple.paddle.bdddomains.V2> ret = bdd; at /home/resea" +
                                               "rch/ccl/olhota/soot-trunk/src/soot/jimple/paddle/queue/Rsrcc" +
                                               "_src_dstc_dstBDD.jedd:55,43-46"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v(), dstc.v(), srcc.v() },
                                                   new PhysicalDomain[] { V1.v(), V2.v(), C2.v(), C1.v() },
                                                   ("return ret; at /home/research/ccl/olhota/soot-trunk/src/soot" +
                                                    "/jimple/paddle/queue/Rsrcc_src_dstc_dstBDD.jedd:57,8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
