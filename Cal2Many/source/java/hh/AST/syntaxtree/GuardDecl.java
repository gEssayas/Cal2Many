package hh.AST.syntaxtree;
import hh.common.translator.VisitorActor;

public class GuardDecl {
  public Type t;
  public Identifier i;
  public VarDeclList vl;
  public StatementList sl;
  public ExpList es;

  public GuardDecl(Type at, Identifier ai,  VarDeclList avl, 
                    StatementList asl, ExpList aes) {
    t=at; i=ai;  vl=avl; sl=asl; es=aes;
  }
 


    public <T,E>T accept(VisitorActor<T,E> v, E env) {
	return v.visit(this,env);
  }
}
