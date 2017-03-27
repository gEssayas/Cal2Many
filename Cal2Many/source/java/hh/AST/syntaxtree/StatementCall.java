package hh.AST.syntaxtree;



import hh.common.translator.VisitorStm;
public class StatementCall extends CStatement {
  public IdentifierExp i;
  public ExpList el;
  
  public StatementCall(IdentifierExp ai, ExpList ael) {
    i=ai; el=ael;
  }

  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
