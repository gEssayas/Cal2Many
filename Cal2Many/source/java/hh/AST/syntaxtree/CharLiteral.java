package hh.AST.syntaxtree;


import hh.common.translator.VisitorExp;
public class CharLiteral extends Exp {
  public char c;

  public CharLiteral(char ac) {
    c=ac;
  }


  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
