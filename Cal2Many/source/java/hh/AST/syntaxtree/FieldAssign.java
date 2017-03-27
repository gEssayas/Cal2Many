package hh.AST.syntaxtree;


import hh.common.translator.VisitorStm;
public class FieldAssign extends CStatement {
  public Identifier i;
  public ExpList es;
  public Exp e1;
  public Exp e2;

  public FieldAssign(Identifier ai, ExpList aes, Exp ae) {
    i=ai; es=aes; e1=ae;
    e2=null;
  }


  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
