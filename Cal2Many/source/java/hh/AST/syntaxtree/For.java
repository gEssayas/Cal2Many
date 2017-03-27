package hh.AST.syntaxtree;



import hh.common.translator.VisitorStm;
public class For extends CStatement {
  public IdentifierExp i;
  public Exp init,con,step;
  public CStatement s;
  

  public For(IdentifierExp ai,Exp ainit,Exp acon, Exp astep, CStatement as) {
    i=ai; init=ainit; con=acon; step=astep; s=as; 
  }

  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
