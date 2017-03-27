package hh.AST.syntaxtree;

import hh.common.translator.VisitorExp;
public class IfExp extends Exp {
  public Exp c,e1,e2;
  
  public IfExp(Exp ac,Exp ae1, Exp ae2) { 
    c=ac; e1=ae1; e2=ae2;
  }

  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
