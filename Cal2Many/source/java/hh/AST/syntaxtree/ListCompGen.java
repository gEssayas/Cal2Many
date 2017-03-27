package hh.AST.syntaxtree;

import hh.common.translator.VisitorExp;
public class ListCompGen extends Exp {
  public ExpList fils;
  public Exp colExp;
  public VarDeclList vars;
   
  public ListCompGen(VarDeclList avars, Exp acolExp, ExpList afils) {
	 vars=avars; colExp=acolExp; fils=afils;
  }
  
  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
