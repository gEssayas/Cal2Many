package hh.AST.syntaxtree;


import hh.common.translator.VisitorStm;
public class Print extends CStatement {
  public ExpList es;
  public boolean isln;

  public Print(ExpList aes, boolean aisln) {
    es=aes; isln=aisln;
  }

  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
