package soot.jimple.paddle;

import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import soot.*;

public class BDDReachableMethods extends AbsReachableMethods {
    private final jedd.internal.RelationContainer reachables =
      new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                          new jedd.PhysicalDomain[] { C2.v(), MS.v() },
                                          ("private <soot.jimple.paddle.bdddomains.ctxt, soot.jimple.pad" +
                                           "dle.bdddomains.method> reachables at /home/research/ccl/olho" +
                                           "ta/soot-jedd/src/soot/jimple/paddle/BDDReachableMethods.jedd" +
                                           ":30,12-26"));
    
    private AbsCallGraph cg;
    
    private Rctxt_method newMethods;
    
    BDDReachableMethods(Rsrcc_srcm_stmt_kind_tgtc_tgtm edgesIn,
                        Rctxt_method methodsIn,
                        Qctxt_method out,
                        AbsCallGraph cg) {
        super(edgesIn, methodsIn, out);
        this.cg = cg;
        newMethods = out.reader("bddrm");
    }
    
    public boolean update() {
        boolean change = false;
        if (methodsIn != null) {
            final jedd.internal.RelationContainer newMethodsIn =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                                  new jedd.PhysicalDomain[] { C2.v(), MS.v() },
                                                  ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                                   "mains.C2, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                                   "addle.bdddomains.MS> newMethodsIn = jedd.internal.Jedd.v().r" +
                                                   "eplace(methodsIn.get(), new jedd.PhysicalDomain[...], new je" +
                                                   "dd.PhysicalDomain[...]); at /home/research/ccl/olhota/soot-j" +
                                                   "edd/src/soot/jimple/paddle/BDDReachableMethods.jedd:42,27-39"),
                                                  jedd.internal.Jedd.v().replace(methodsIn.get(),
                                                                                 new jedd.PhysicalDomain[] { C1.v() },
                                                                                 new jedd.PhysicalDomain[] { C2.v() }));
            newMethodsIn.eqMinus(reachables);
            if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newMethodsIn),
                                               jedd.internal.Jedd.v().falseBDD()))
                change = true;
            reachables.eqUnion(newMethodsIn);
            out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), ctxt.v() },
                                                        new jedd.PhysicalDomain[] { MS.v(), C1.v() },
                                                        ("out.add(jedd.internal.Jedd.v().replace(newMethodsIn, new jed" +
                                                         "d.PhysicalDomain[...], new jedd.PhysicalDomain[...])) at /ho" +
                                                         "me/research/ccl/olhota/soot-jedd/src/soot/jimple/paddle/BDDR" +
                                                         "eachableMethods.jedd:46,12-15"),
                                                        jedd.internal.Jedd.v().replace(newMethodsIn,
                                                                                       new jedd.PhysicalDomain[] { C2.v() },
                                                                                       new jedd.PhysicalDomain[] { C1.v() })));
        }
        final jedd.internal.RelationContainer newEdges =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { srcc.v(), srcm.v(), stmt.v(), kind.v(), tgtc.v(), tgtm.v() },
                                              new jedd.PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v(), C2.v(), MT.v() },
                                              ("<soot.jimple.paddle.bdddomains.srcc:soot.jimple.paddle.bdddo" +
                                               "mains.C1, soot.jimple.paddle.bdddomains.srcm:soot.jimple.pad" +
                                               "dle.bdddomains.MS, soot.jimple.paddle.bdddomains.stmt:soot.j" +
                                               "imple.paddle.bdddomains.ST, soot.jimple.paddle.bdddomains.ki" +
                                               "nd:soot.jimple.paddle.bdddomains.KD, soot.jimple.paddle.bddd" +
                                               "omains.tgtc:soot.jimple.paddle.bdddomains.C2, soot.jimple.pa" +
                                               "ddle.bdddomains.tgtm:soot.jimple.paddle.bdddomains.MT> newEd" +
                                               "ges = jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().rea" +
                                               "d(edgesIn.get()), jedd.internal.Jedd.v().replace(reachables," +
                                               " new jedd.PhysicalDomain[...], new jedd.PhysicalDomain[...])" +
                                               ", new jedd.PhysicalDomain[...]); at /home/research/ccl/olhot" +
                                               "a/soot-jedd/src/soot/jimple/paddle/BDDReachableMethods.jedd:" +
                                               "49,45-53"),
                                              jedd.internal.Jedd.v().join(jedd.internal.Jedd.v().read(edgesIn.get()),
                                                                          jedd.internal.Jedd.v().replace(reachables,
                                                                                                         new jedd.PhysicalDomain[] { C2.v() },
                                                                                                         new jedd.PhysicalDomain[] { C1.v() }),
                                                                          new jedd.PhysicalDomain[] { C1.v(), MS.v() }));
        newEdges.eqUnion(cg.edgesOutOf(newMethods).get());
        while (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newEdges),
                                              jedd.internal.Jedd.v().falseBDD())) {
            final jedd.internal.RelationContainer newTargets =
              new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                                  new jedd.PhysicalDomain[] { C2.v(), MT.v() },
                                                  ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                                   "mains.C2, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                                   "addle.bdddomains.MT> newTargets = jedd.internal.Jedd.v().pro" +
                                                   "ject(newEdges, new jedd.PhysicalDomain[...]); at /home/resea" +
                                                   "rch/ccl/olhota/soot-jedd/src/soot/jimple/paddle/BDDReachable" +
                                                   "Methods.jedd:55,27-37"),
                                                  jedd.internal.Jedd.v().project(newEdges,
                                                                                 new jedd.PhysicalDomain[] { C1.v(), MS.v(), ST.v(), KD.v() }));
            newTargets.eqMinus(jedd.internal.Jedd.v().replace(reachables,
                                                              new jedd.PhysicalDomain[] { MS.v() },
                                                              new jedd.PhysicalDomain[] { MT.v() }));
            out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), ctxt.v() },
                                                        new jedd.PhysicalDomain[] { MS.v(), C1.v() },
                                                        ("out.add(jedd.internal.Jedd.v().replace(newTargets, new jedd." +
                                                         "PhysicalDomain[...], new jedd.PhysicalDomain[...])) at /home" +
                                                         "/research/ccl/olhota/soot-jedd/src/soot/jimple/paddle/BDDRea" +
                                                         "chableMethods.jedd:58,12-15"),
                                                        jedd.internal.Jedd.v().replace(newTargets,
                                                                                       new jedd.PhysicalDomain[] { MT.v(), C2.v() },
                                                                                       new jedd.PhysicalDomain[] { MS.v(), C1.v() })));
            if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newTargets),
                                               jedd.internal.Jedd.v().falseBDD()))
                change = true;
            reachables.eqUnion(jedd.internal.Jedd.v().replace(newTargets,
                                                              new jedd.PhysicalDomain[] { MT.v() },
                                                              new jedd.PhysicalDomain[] { MS.v() }));
            newEdges.eq(cg.edgesOutOf(newMethods).get());
        }
        return change;
    }
    
    boolean add(MethodOrMethodContext m) {
        final jedd.internal.RelationContainer newM =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), MS.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.MS> newM = jedd.internal.Jedd.v().literal(n" +
                                               "ew java.lang.Object[...], new jedd.Attribute[...], new jedd." +
                                               "PhysicalDomain[...]); at /home/research/ccl/olhota/soot-jedd" +
                                               "/src/soot/jimple/paddle/BDDReachableMethods.jedd:66,23-27"),
                                              jedd.internal.Jedd.v().literal(new Object[] { m.context(), m.method() },
                                                                             new jedd.Attribute[] { ctxt.v(), method.v() },
                                                                             new jedd.PhysicalDomain[] { C2.v(), MS.v() }));
        final jedd.internal.RelationContainer newReachables =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), MS.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.MS> newReachables = jedd.internal.Jedd.v()." +
                                               "union(jedd.internal.Jedd.v().read(reachables), newM); at /ho" +
                                               "me/research/ccl/olhota/soot-jedd/src/soot/jimple/paddle/BDDR" +
                                               "eachableMethods.jedd:67,23-36"),
                                              jedd.internal.Jedd.v().union(jedd.internal.Jedd.v().read(reachables),
                                                                           newM));
        if (!jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(newReachables), reachables)) {
            reachables.eq(newReachables);
            out.add(new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), ctxt.v() },
                                                        new jedd.PhysicalDomain[] { MS.v(), C1.v() },
                                                        ("out.add(jedd.internal.Jedd.v().replace(newM, new jedd.Physic" +
                                                         "alDomain[...], new jedd.PhysicalDomain[...])) at /home/resea" +
                                                         "rch/ccl/olhota/soot-jedd/src/soot/jimple/paddle/BDDReachable" +
                                                         "Methods.jedd:70,12-15"),
                                                        jedd.internal.Jedd.v().replace(newM,
                                                                                       new jedd.PhysicalDomain[] { C2.v() },
                                                                                       new jedd.PhysicalDomain[] { C1.v() })));
            return true;
        }
        return false;
    }
    
    int size() {
        return (int)
                 new jedd.internal.RelationContainer(new jedd.Attribute[] { method.v(), ctxt.v() },
                                                     new jedd.PhysicalDomain[] { MS.v(), C2.v() },
                                                     ("reachables.size() at /home/research/ccl/olhota/soot-jedd/src" +
                                                      "/soot/jimple/paddle/BDDReachableMethods.jedd:76,21-31"),
                                                     reachables).size();
    }
    
    boolean contains(MethodOrMethodContext m) {
        final jedd.internal.RelationContainer newM =
          new jedd.internal.RelationContainer(new jedd.Attribute[] { ctxt.v(), method.v() },
                                              new jedd.PhysicalDomain[] { C2.v(), MS.v() },
                                              ("<soot.jimple.paddle.bdddomains.ctxt:soot.jimple.paddle.bdddo" +
                                               "mains.C2, soot.jimple.paddle.bdddomains.method:soot.jimple.p" +
                                               "addle.bdddomains.MS> newM = jedd.internal.Jedd.v().literal(n" +
                                               "ew java.lang.Object[...], new jedd.Attribute[...], new jedd." +
                                               "PhysicalDomain[...]); at /home/research/ccl/olhota/soot-jedd" +
                                               "/src/soot/jimple/paddle/BDDReachableMethods.jedd:79,23-27"),
                                              jedd.internal.Jedd.v().literal(new Object[] { m.context(), m.method() },
                                                                             new jedd.Attribute[] { ctxt.v(), method.v() },
                                                                             new jedd.PhysicalDomain[] { C2.v(), MS.v() }));
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(reachables),
                                                                                                           newM)),
                                              jedd.internal.Jedd.v().falseBDD());
    }
}
