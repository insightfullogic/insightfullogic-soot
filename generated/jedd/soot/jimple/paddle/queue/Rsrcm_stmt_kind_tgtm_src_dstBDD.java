package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Rsrcm_stmt_kind_tgtm_src_dstBDD extends Rsrcm_stmt_kind_tgtm_src_dst {
    private final jedd.internal.RelationContainer bdd =
      new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() },
                                          new PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v(), V1.v(), V2.v() },
                                          ("private <soot.jimple.paddle.bdddomains.srcm:soot.jimple.padd" +
                                           "le.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.ji" +
                                           "mple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.kin" +
                                           "d:soot.jimple.paddle.bdddomains.KD, soot.jimple.paddle.bdddo" +
                                           "mains.tgtm:soot.jimple.paddle.bdddomains.MT, soot.jimple.pad" +
                                           "dle.bdddomains.src:soot.jimple.paddle.bdddomains.V1, soot.ji" +
                                           "mple.paddle.bdddomains.dst:soot.jimple.paddle.bdddomains.V2>" +
                                           " bdd at /home/research/ccl/olhota/soot-trunk/src/soot/jimple" +
                                           "/paddle/queue/Rsrcm_stmt_kind_tgtm_src_dstBDD.jedd:31,12-64"));
    
    void add(final jedd.internal.RelationContainer tuple) { bdd.eqUnion(tuple); }
    
    public Rsrcm_stmt_kind_tgtm_src_dstBDD(final jedd.internal.RelationContainer bdd, String name) {
        this(name);
        add(new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v(), kind.v(), srcm.v(), stmt.v(), tgtm.v() },
                                                new PhysicalDomain[] { V1.v(), V2.v(), KD.v(), MS.v(), ST.v(), MT.v() },
                                                ("add(bdd) at /home/research/ccl/olhota/soot-trunk/src/soot/ji" +
                                                 "mple/paddle/queue/Rsrcm_stmt_kind_tgtm_src_dstBDD.jedd:33,13" +
                                                 "0-133"),
                                                bdd));
    }
    
    Rsrcm_stmt_kind_tgtm_src_dstBDD(String name) {
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
                      new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v(), kind.v(), srcm.v(), stmt.v(), tgtm.v() },
                                                          new PhysicalDomain[] { V1.v(), V2.v(), KD.v(), MS.v(), ST.v(), MT.v() },
                                                          ("bdd.iterator(new jedd.Attribute[...]) at /home/research/ccl/" +
                                                           "olhota/soot-trunk/src/soot/jimple/paddle/queue/Rsrcm_stmt_ki" +
                                                           "nd_tgtm_src_dstBDD.jedd:45,25-28"),
                                                          bdd).iterator(new Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() });
                    bdd.eq(jedd.internal.Jedd.v().falseBDD());
                }
                Object[] components = (Object[]) it.next();
                return new Tuple((SootMethod) components[0],
                                 (Unit) components[1],
                                 (Kind) components[2],
                                 (SootMethod) components[3],
                                 (VarNode) components[4],
                                 (VarNode) components[5]);
            }
            
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    public jedd.internal.RelationContainer get() {
        final jedd.internal.RelationContainer ret =
          new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() },
                                              new PhysicalDomain[] { MS.v(), ST.v(), KD.v(), MT.v(), V1.v(), V2.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcm:soot.jimple.paddle.bdddo" +
                                               "mains.MS, soot.jimple.paddle.bdddomains.stmt:soot.jimple.pad" +
                                               "dle.bdddomains.ST, soot.jimple.paddle.bdddomains.kind:soot.j" +
                                               "imple.paddle.bdddomains.KD, soot.jimple.paddle.bdddomains.tg" +
                                               "tm:soot.jimple.paddle.bdddomains.MT, soot.jimple.paddle.bddd" +
                                               "omains.src:soot.jimple.paddle.bdddomains.V1, soot.jimple.pad" +
                                               "dle.bdddomains.dst:soot.jimple.paddle.bdddomains.V2> ret = b" +
                                               "dd; at /home/research/ccl/olhota/soot-trunk/src/soot/jimple/" +
                                               "paddle/queue/Rsrcm_stmt_kind_tgtm_src_dstBDD.jedd:55,61-64"),
                                              bdd);
        bdd.eq(jedd.internal.Jedd.v().falseBDD());
        return new jedd.internal.RelationContainer(new Attribute[] { src.v(), dst.v(), kind.v(), srcm.v(), stmt.v(), tgtm.v() },
                                                   new PhysicalDomain[] { V1.v(), V2.v(), KD.v(), MS.v(), ST.v(), MT.v() },
                                                   ("return ret; at /home/research/ccl/olhota/soot-trunk/src/soot" +
                                                    "/jimple/paddle/queue/Rsrcm_stmt_kind_tgtm_src_dstBDD.jedd:57" +
                                                    ",8-14"),
                                                   ret);
    }
    
    public boolean hasNext() {
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(bdd), jedd.internal.Jedd.v().falseBDD());
    }
}
