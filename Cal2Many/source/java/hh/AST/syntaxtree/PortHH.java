package hh.AST.syntaxtree;

import hh.common.translator.VisitorExp;
public class PortHH extends Exp{
  public Type t;
  public Identifier i;
  public boolean isRead;
  
  public PortHH(Type at, Identifier ai) {
    t=at; i=ai; isRead = false;
  }


  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
