package hh.AST.syntaxtree;
import hh.common.translator.VisitorType;

import java.util.Map;


public class ListType extends Type {
	
	
	public Exp len;
	public Type t;
	/*public Map<String, Exp> mval;
	public Map<String, Type> mtype;
	
	public ListType(Map<String, Exp> amval,Map<String, Type> amtype){
		mval=amval; mtype=amtype;
	}*/
	public ListType(Type at, Exp alen){
		len=alen; t=at;
	}
    public <T,E>T accept(VisitorType<T,E> v, E env) {
	return v.visit(this,env);
  }
}
