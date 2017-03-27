package hh.AST.syntaxtree;


import hh.common.translator.VisitorExp;
public class Not extends Exp {
  public Exp e;
  
  public Not(Exp ae) {
    e=ae; 
  }


  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
