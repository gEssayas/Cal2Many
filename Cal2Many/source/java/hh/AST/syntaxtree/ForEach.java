package hh.AST.syntaxtree;

import hh.common.translator.VisitorStm;
public class ForEach extends CStatement {
  public CStatement s;
  public ListOfListCompGen compGen;  
  
 
  public ForEach(CStatement as,ListOfListCompGen acompGen){
	  s=as; compGen=acompGen;
  }

  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
