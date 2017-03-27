package hh.common.translator;


import hh.AST.syntaxtree.*;
import hh.simplenet.*;

public interface VisitorActor<T,Env> {

	
	public T visit(ActionDecl n, Env e);
	public T visit(ActionFiringCond n, Env e);
	public T visit(ActionScheduler n, Env e);
	public T visit(ActorFSM n, Env e);
	public T visit(Formal n, Env e);
	public T visit(FunctionDecl n, Env e);
	public T visit(GuardDecl n, Env e);
	public T visit(ProcedureDecl n, Env e);
	public T visit(SEQ_Actor n, Env e);
	public T visit(Profile_Actor n, Env e);
	public T visit(Profile_ActionDecl n, Env e);
	public T visit(Profile_ActionScheduler n, Env e);
	public T visit(Profile_FunctionDecl n, Env e);
	public T visit(Profile_ProcedureDecl n, Env e);
}
