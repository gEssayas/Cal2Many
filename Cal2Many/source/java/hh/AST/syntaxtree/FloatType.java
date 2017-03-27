package hh.AST.syntaxtree;
import hh.common.translator.VisitorType;

import java.util.Map;


public class FloatType extends Type {
	
	public Map<String, Exp> mval;
	public int size;
	public FloatType(int asize){
		mval=null;
		size=asize;
	}
//	public IntegerType(Map<String, Exp> amval){
//		mval=amval;
//	}
	
    public <T,E>T accept(VisitorType<T,E> v, E env) {
	return v.visit(this,env);
  }
}
