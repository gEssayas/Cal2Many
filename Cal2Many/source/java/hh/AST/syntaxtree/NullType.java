package hh.AST.syntaxtree;
import hh.common.translator.VisitorType;


public class NullType extends Type {
    public <T,E>T accept(VisitorType<T,E> v, E env) {
	return v.visit(this,env);
  }
}
