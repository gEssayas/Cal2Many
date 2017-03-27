package hh.AST.syntaxtree;



import hh.common.translator.VisitorStm;
public class GoTo extends CStatement {
  public String s;
  
  
  public GoTo(String as) {
    s=as; 
  }

  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
