package hh.AST.syntaxtree;



import hh.common.translator.VisitorStm;
public class While extends CStatement {
  public Exp e;
  public CStatement s;

  public While(Exp ae, CStatement as) {
    e=ae; s=as; 
  }

  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
