package hh.AST.syntaxtree;


import hh.common.translator.VisitorExp;
public class Negate extends Exp {
  public Exp e;
  
  public Negate(Exp ae) {
    e=ae; 
  }


  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
