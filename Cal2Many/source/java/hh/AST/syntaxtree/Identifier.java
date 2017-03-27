package hh.AST.syntaxtree;
import hh.common.translator.VisitorType;




public class Identifier {
  public String s;

  public Identifier(String as) { 
    s=as;
  }


  public <T,E>T accept(VisitorType<T,E> v, E env) {
	return v.visit(this,env);
  }
  
	 
  public String toString(){
    return s;
  }
}
