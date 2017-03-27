package hh.AST.syntaxtree;

import hh.common.translator.VisitorExp;
public class VectorOps extends Exp {
    public String operator;
	public Exp op1,op2, len;
  
  
  public VectorOps(String aoperator, Exp aop1, Exp aop2,Exp alen) { 
    operator = aoperator; op1 = aop1; op2 =aop2; len = alen;
  }

  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
