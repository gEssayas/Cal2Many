package hh.AST.syntaxtree;


import hh.common.translator.VisitorExp;
public class StringLiteral extends Exp {
  public String s;

  public StringLiteral(String as) {
    s=as;
  }


  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
