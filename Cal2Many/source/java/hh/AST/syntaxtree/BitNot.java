package hh.AST.syntaxtree;


import hh.common.translator.VisitorExp;
public class BitNot extends Exp {
  public Exp e;
  
  public BitNot(Exp ae) {
    e=ae; 
  }


  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
