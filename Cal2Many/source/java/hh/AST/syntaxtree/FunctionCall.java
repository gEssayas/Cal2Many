package hh.AST.syntaxtree;


import hh.common.translator.VisitorExp;
public class FunctionCall extends Exp {
  public IdentifierExp i;
  public ExpList exs;
  
  public FunctionCall(IdentifierExp ai, ExpList aexs) { 
    i=ai; exs=aexs;
  }


  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
