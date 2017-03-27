package hh.AST.syntaxtree;

import hh.common.translator.VisitorExp;
public class MatrixOps extends Exp {
    public String operator;
	public Exp op1,op2, lenX,lenY;
  
  
  public MatrixOps(String aoperator, Exp aop1, Exp aop2,Exp alenX, Exp alenY) { 
    operator = aoperator; op1 = aop1; op2 =aop2; lenX = alenX; lenY = alenY;
  }

  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
