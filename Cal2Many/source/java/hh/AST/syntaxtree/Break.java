package hh.AST.syntaxtree;


import hh.common.translator.VisitorStm;
public class Break extends CStatement {

  public Break() {
	  
  }
  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
