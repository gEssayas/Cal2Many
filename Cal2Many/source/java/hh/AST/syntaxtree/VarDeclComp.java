package hh.AST.syntaxtree;

import hh.common.translator.VisitorStm;



public class VarDeclComp extends VarDecl{
  public Type t;
  public Identifier i;
  public Exp e;
  public boolean isAssignable;
  public ExpList es;
  
  public VarDeclComp(Type at, Identifier ai, Exp ex,boolean aisAssignable) {
    t=at; i=ai; e=ex; isAssignable =aisAssignable; es = new ExpList();
  }
  public VarDeclComp(Type at, Identifier ai,boolean aisAssignable, ExpList aes) {
	    t=at; i=ai; es=aes; isAssignable =aisAssignable;
	  }


    public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
