package hh.common.translator;


import hh.AST.syntaxtree.*;
import hh.simplenet.*;

public interface VisitorType<T,Env> {

	
	public T visit(BooleanType n, Env e);
	public T visit(FloatType n, Env e);
    public T visit(Identifier n, Env e);
	public T visit(IdentifierType n, Env e);
	public T visit(IntegerType n, Env e);
	public T visit(ListType n, Env e);
	public T visit(NullType n, Env e);

    
    
}
