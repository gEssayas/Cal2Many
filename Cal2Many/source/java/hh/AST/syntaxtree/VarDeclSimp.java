package hh.AST.syntaxtree;

import hh.common.translator.VisitorStm;



public class VarDeclSimp extends VarDecl{
  public Type t;
  public Identifier i;
  public Exp e;
  public boolean isAssignable;
  
  
  public VarDeclSimp(Type at, Identifier ai, Exp ex,boolean aisAssignable) {
	   t=at; i=ai; e=ex; isAssignable =aisAssignable;
  }


    public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
