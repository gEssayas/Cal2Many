package hh.AST.syntaxtree;


import hh.common.translator.VisitorExp;
public class DoubleLiteral extends Exp {
  public double d;

  public DoubleLiteral(double ad) {
    d=ad;
  }


  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
