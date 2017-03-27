package hh.simplenet;
import hh.AST.syntaxtree.*;
import hh.common.translator.VisitorExp;



public class SimpEntityExpr extends Exp{
	public Identifier i;
	public VarDeclList vl; // state vars
	

	public SimpEntityExpr(Identifier ai, VarDeclList avl) {

		i=ai; vl=avl;
	}


	public <T,E>T accept(VisitorExp<T,E> v, E env) {
		return v.visit(this,env);
	}
}
