package hh.AST.syntaxtree;

import hh.common.translator.VisitorStm;
public class Assign extends CStatement {
  public Identifier i;
  public Exp e;
  public Type varT;
  public Assign(Identifier ai, Exp ae, Type avarT) {
    i=ai; e=ae; varT = avarT;
  }

  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
