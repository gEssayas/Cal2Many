package hh.AST.syntaxtree;



import hh.common.translator.VisitorStm;
public class Lable extends CStatement {
  public String s;
  
  public Lable(String as) {
    s=as; ;
  }

  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
