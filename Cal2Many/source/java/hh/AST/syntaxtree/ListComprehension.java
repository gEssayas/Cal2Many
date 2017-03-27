package hh.AST.syntaxtree;

import hh.common.translator.VisitorExp;
public class ListComprehension extends Exp {
  public ExpList eles;
  public ListOfListCompGen compGen;  
  
 
  public ListComprehension(ExpList aeles,ListOfListCompGen acompGen){
	  eles=aeles; compGen=acompGen;
  }

  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
