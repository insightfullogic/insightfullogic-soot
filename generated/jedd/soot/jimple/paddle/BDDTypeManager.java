package soot.jimple.paddle;

import soot.*;
import soot.util.*;
import soot.jimple.paddle.queue.*;
import soot.jimple.paddle.bdddomains.*;
import java.util.*;
import jedd.*;

public class BDDTypeManager extends AbsTypeManager {
    BDDTypeManager(Rvar_method_type locals,
                   Rvar_type globals,
                   Robj_method_type localallocs,
                   Robj_type globalallocs,
                   BDDHierarchy fh) {
        super(locals, globals, localallocs, globalallocs);
        this.fh = fh;
    }
    
    final jedd.internal.RelationContainer result =
      new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                          new PhysicalDomain[] { V1.v(), H1.v() },
                                          ("<soot.jimple.paddle.bdddomains.var, soot.jimple.paddle.bdddo" +
                                           "mains.obj> result = jedd.internal.Jedd.v().falseBDD() at /ho" +
                                           "me/olhotak/soot-trunk2/src/soot/jimple/paddle/BDDTypeManager" +
                                           ".jedd:37,4-14"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    final jedd.internal.RelationContainer allVars =
      new jedd.internal.RelationContainer(new Attribute[] { var.v(), type.v() },
                                          new PhysicalDomain[] { V1.v(), T2.v() },
                                          ("<soot.jimple.paddle.bdddomains.var, soot.jimple.paddle.bdddo" +
                                           "mains.type> allVars = jedd.internal.Jedd.v().falseBDD() at /" +
                                           "home/olhotak/soot-trunk2/src/soot/jimple/paddle/BDDTypeManag" +
                                           "er.jedd:38,4-15"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    final jedd.internal.RelationContainer allObjs =
      new jedd.internal.RelationContainer(new Attribute[] { obj.v(), type.v() },
                                          new PhysicalDomain[] { H1.v(), T1.v() },
                                          ("<soot.jimple.paddle.bdddomains.obj, soot.jimple.paddle.bdddo" +
                                           "mains.type> allObjs = jedd.internal.Jedd.v().falseBDD() at /" +
                                           "home/olhotak/soot-trunk2/src/soot/jimple/paddle/BDDTypeManag" +
                                           "er.jedd:39,4-15"),
                                          jedd.internal.Jedd.v().falseBDD());
    
    public void update() {
        if (fh == null) {
            result.eq(jedd.internal.Jedd.v().trueBDD());
            return;
        }
        final jedd.internal.RelationContainer newVars =
          new jedd.internal.RelationContainer(new Attribute[] { var.v(), type.v() },
                                              new PhysicalDomain[] { V1.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.var:soot.jimple.paddle.bdddom" +
                                               "ains.V1, soot.jimple.paddle.bdddomains.type:soot.jimple.padd" +
                                               "le.bdddomains.T2> newVars; at /home/olhotak/soot-trunk2/src/" +
                                               "soot/jimple/paddle/BDDTypeManager.jedd:46,20-27"));
        newVars.eq(jedd.internal.Jedd.v().project(locals.get(), new PhysicalDomain[] { T1.v() }));
        newVars.eqUnion(globals.get());
        allVars.eqUnion(newVars);
        final jedd.internal.RelationContainer newObjs =
          new jedd.internal.RelationContainer(new Attribute[] { obj.v(), type.v() },
                                              new PhysicalDomain[] { H1.v(), T1.v() },
                                              ("<soot.jimple.paddle.bdddomains.obj:soot.jimple.paddle.bdddom" +
                                               "ains.H1, soot.jimple.paddle.bdddomains.type:soot.jimple.padd" +
                                               "le.bdddomains.T1> newObjs; at /home/olhotak/soot-trunk2/src/" +
                                               "soot/jimple/paddle/BDDTypeManager.jedd:51,20-27"));
        newObjs.eq(jedd.internal.Jedd.v().replace(jedd.internal.Jedd.v().project(localallocs.get(),
                                                                                 new PhysicalDomain[] { T1.v() }),
                                                  new PhysicalDomain[] { T2.v() },
                                                  new PhysicalDomain[] { T1.v() }));
        newObjs.eqUnion(jedd.internal.Jedd.v().replace(globalallocs.get(),
                                                       new PhysicalDomain[] { T2.v() },
                                                       new PhysicalDomain[] { T1.v() }));
        allObjs.eqUnion(newObjs);
        final jedd.internal.RelationContainer subtypeRelation =
          new jedd.internal.RelationContainer(new Attribute[] { subt.v(), supt.v() },
                                              new PhysicalDomain[] { T1.v(), T2.v() },
                                              ("<soot.jimple.paddle.bdddomains.subt:soot.jimple.paddle.bdddo" +
                                               "mains.T1, soot.jimple.paddle.bdddomains.supt:soot.jimple.pad" +
                                               "dle.bdddomains.T2> subtypeRelation = fh.subtypeRelation(); a" +
                                               "t /home/olhotak/soot-trunk2/src/soot/jimple/paddle/BDDTypeMa" +
                                               "nager.jedd:56,21-36"),
                                              fh.subtypeRelation());
        result.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(subtypeRelation),
                                                                                                                 newVars,
                                                                                                                 new PhysicalDomain[] { T2.v() })),
                                                      allObjs,
                                                      new PhysicalDomain[] { T1.v() }));
        result.eqUnion(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().compose(jedd.internal.Jedd.v().read(subtypeRelation),
                                                                                                                 allVars,
                                                                                                                 new PhysicalDomain[] { T2.v() })),
                                                      newObjs,
                                                      new PhysicalDomain[] { T1.v() }));
    }
    
    public BitVector get(Type type) { throw new RuntimeException("Not implemented"); }
    
    public jedd.internal.RelationContainer get() {
        this.update();
        return new jedd.internal.RelationContainer(new Attribute[] { var.v(), obj.v() },
                                                   new PhysicalDomain[] { V2.v(), H1.v() },
                                                   ("return jedd.internal.Jedd.v().replace(result, new jedd.Physi" +
                                                    "calDomain[...], new jedd.PhysicalDomain[...]); at /home/olho" +
                                                    "tak/soot-trunk2/src/soot/jimple/paddle/BDDTypeManager.jedd:6" +
                                                    "5,8-14"),
                                                   jedd.internal.Jedd.v().replace(result,
                                                                                  new PhysicalDomain[] { V1.v() },
                                                                                  new PhysicalDomain[] { V2.v() }));
    }
    
    public boolean castNeverFails(Type from, Type to) {
        if (fh == null) return true;
        if (to == null) return true;
        if (to == from) return true;
        if (from == null) return false;
        if (to.equals(from)) return true;
        if (to instanceof AnySubType) throw new RuntimeException("oops from=" + from + " to=" + to);
        return !jedd.internal.Jedd.v().equals(jedd.internal.Jedd.v().read(jedd.internal.Jedd.v().falseBDD()),
                                              jedd.internal.Jedd.v().intersect(jedd.internal.Jedd.v().read(fh.subtypeRelation()),
                                                                               jedd.internal.Jedd.v().literal(new Object[] { from, to },
                                                                                                              new Attribute[] { subt.v(), supt.v() },
                                                                                                              new PhysicalDomain[] { T1.v(), T2.v() })));
    }
    
    private BDDHierarchy fh;
}
