package soot.javaToJimple;

public class CastInsertionVisitor extends polyglot.visit.AscriptionVisitor {

    public CastInsertionVisitor(polyglot.frontend.Job job, polyglot.types.TypeSystem ts, polyglot.ast.NodeFactory nf) {
        super(job, ts, nf);
    }

    public polyglot.ast.Expr ascribe(polyglot.ast.Expr e, polyglot.types.Type toType) {
        //System.out.println("cast ins vis: expr: "+e);
        polyglot.types.Type fromType = e.type();

        //System.out.println("e type: "+fromType);
        //System.out.println("to type: "+toType);
        if (toType == null){
            return e;
        }
        if (toType.isVoid()) {
            return e;
        }
        
        polyglot.util.Position p = e.position();

        if (toType.equals(fromType)){
            return e;
        }


        if (toType.isPrimitive() && fromType.isPrimitive()) {
            polyglot.ast.Expr newExpr = nf.Cast(p, nf.CanonicalTypeNode(p, toType), e).type(toType);
            return newExpr;
        }
        
        return e;
        
    }
    
    public polyglot.ast.Node leaveCall(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor v) throws polyglot.types.SemanticException {
    
        n = super.leaveCall(old, n, v);

        return n;
    }
}
