package hh.AST.syntaxtree;
import hh.common.translator.VisitorActor;

public class ProcedureDecl {
  public Type t;
  public Identifier i;
  public FormalList fl;
  public VarDeclList vl;
  public CStatement sl;
  public Exp e;

  public ProcedureDecl(Type at, Identifier ai, FormalList afl, VarDeclList avl, 
                    CStatement asl, Exp ae) {
    t=at; i=ai; fl=afl; vl=avl; sl=asl; e=ae;
  }
 


    public <T,E>T accept(VisitorActor<T,E> v, E env) {
	return v.visit(this,env);
  }
}
