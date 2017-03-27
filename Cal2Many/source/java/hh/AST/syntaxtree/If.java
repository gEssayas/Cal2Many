package hh.AST.syntaxtree;



import hh.common.translator.VisitorStm;
public class If extends CStatement {
  public Exp e;
  public CStatement s1,s2;

  public If(Exp ae, CStatement as1, CStatement as2) {
    e=ae; s1=as1; s2=as2;
  }


  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
