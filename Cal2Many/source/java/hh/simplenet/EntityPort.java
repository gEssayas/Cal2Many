package hh.simplenet;
import hh.AST.syntaxtree.*;
import hh.common.translator.NetVisitor;



public class EntityPort {
	public IdentifierType t;
	public Identifier i,p;
	
	

	public EntityPort(IdentifierType at,Identifier ai, Identifier ap) {
		t=at;i=ai;p=ap;
	}


	public <T,E>T accept(NetVisitor<T,E> v, E env) {
		return v.visit(this,env);
	}
}
