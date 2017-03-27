package hh.common.translator;


import hh.AST.syntaxtree.*;
import hh.simplenet.*;

public interface VisitorStm<T,Env> {
	public T visit(Assign n, Env e);
	public T visit(Block n, Env e);
	public T visit(Break n, Env e);
	public T visit(ConsumeToken n, Env e);
	public T visit(ElseIf n, Env e);
	public T visit(FieldAssign n, Env e);
	public T visit(For n, Env e);
	public T visit(ForEach n, Env e);
	public T visit(GoTo n, Env e);
	public T visit(If n, Env e);
	public T visit(Lable n, Env e);
	public T visit(ListAssign n, Env e);
	public T visit(Print n, Env e);
	public T visit(ReadToken n, Env e);
	public T visit(SendToken n, Env e);
	public T visit(StatementCall n, Env e);
	public T visit(SwitchCase n, Env e);
	public T visit(VarDeclComp n, Env e);
	public T visit(VarDeclSimp n, Env e);
	public T visit(While n, Env e);
    
    
}
