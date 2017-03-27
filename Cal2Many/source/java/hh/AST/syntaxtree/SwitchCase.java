package hh.AST.syntaxtree;

import hh.common.translator.VisitorStm;
public class SwitchCase extends CStatement {
	public Exp exCase;
	public ExpList cases;
	public StatementList sts;
  //for Trans goto
    public SwitchCase(Exp aexCase, ExpList acases, StatementList asts) {
    	exCase=aexCase; cases=acases; sts=asts;
	  }

  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
