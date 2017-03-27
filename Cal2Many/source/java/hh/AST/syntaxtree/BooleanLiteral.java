package hh.AST.syntaxtree;



import hh.common.translator.VisitorExp;
public class BooleanLiteral extends Exp {
	
	public Boolean value;
	
	public BooleanLiteral(Boolean avalue){
		value=avalue;
	}

  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
