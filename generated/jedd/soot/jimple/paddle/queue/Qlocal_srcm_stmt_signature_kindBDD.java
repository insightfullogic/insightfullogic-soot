package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qlocal_srcm_stmt_signature_kindBDD extends Qlocal_srcm_stmt_signature_kind {
    public Qlocal_srcm_stmt_signature_kindBDD(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Local _local, SootMethod _srcm, Unit _stmt, NumberedString _signature, Kind _kind) {
        this.add(new jedd.internal.RelationContainer(new Attribute[] { local.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                                     new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() },
                                                     ("this.add(jedd.internal.Jedd.v().literal(new java.lang.Object" +
                                                      "[...], new jedd.Attribute[...], new jedd.PhysicalDomain[...]" +
                                                      ")) at /home/olhotak/soot-trunk/src/soot/jimple/paddle/queue/" +
                                                      "Qlocal_srcm_stmt_signature_kindBDD.jedd:34,8-11"),
                                                     jedd.internal.Jedd.v().literal(new Object[] { _local, _srcm, _stmt, _signature, _kind },
                                                                                    new Attribute[] { local.v(), srcm.v(), stmt.v(), signature.v(), kind.v() },
                                                                                    new PhysicalDomain[] { V1.v(), T1.v(), ST.v(), H2.v(), FD.v() })));
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rlocal_srcm_stmt_signature_kindBDD reader = (Rlocal_srcm_stmt_signature_kindBDD) it.next();
            reader.add(new jedd.internal.RelationContainer(new Attribute[] { kind.v(), stmt.v(), srcm.v(), local.v(), signature.v() },
                                                           new PhysicalDomain[] { FD.v(), ST.v(), T1.v(), V1.v(), H2.v() },
                                                           ("reader.add(in) at /home/olhotak/soot-trunk/src/soot/jimple/p" +
                                                            "addle/queue/Qlocal_srcm_stmt_signature_kindBDD.jedd:39,12-18"),
                                                           in));
        }
    }
    
    public Rlocal_srcm_stmt_signature_kind reader(String rname) {
        Rlocal_srcm_stmt_signature_kind ret = new Rlocal_srcm_stmt_signature_kindBDD(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
