package hh.common.translator;


import hh.AST.syntaxtree.*;
public interface VisitorDataParallel<T,Env> { 
	
	public T visit(VectorOps n, Env e);
	
	}
