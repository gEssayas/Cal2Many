package hh.common.translator;


import hh.AST.syntaxtree.*;
import hh.simplenet.*;

public interface CStatementVisitor<T,Env> {
	public T visit(ActionDecl break1, Env e);
	public T visit(ActionScheduler n, Env e);
	public T visit(ListConc n, Env e);

    public T visit(And n, Env e);    
    public T visit(FieldAssign n, Env e);
    
   
    
   /* 
    public T visit(MainClass n, Env e);
    public T visit(ClassDeclSimple n, Env e);
    public T visit(ClassDeclExtends n, Env e);*/

    public T visit(Assign n, Env e);
    public T visit(BitAnd n, Env e);
    public T visit(BitNot n, Env e);
    public T visit(BitOr n, Env e);
    public T visit(BitXOr n, Env e);
    public T visit(Block n, Env e);
    public T visit(BooleanType n, Env e);


    
    public T visit(BooleanLiteral n, Env e);
    public T visit(Call n, Env e);
    public T visit(Channel n, Env e);
    public T visit(ConsumeToken n, Env e);
    public T visit(Divide n, Env e);
    public T visit(CharLiteral n, Env e);
    public T visit(DoubleLiteral n, Env e);

    
    
    public T visit(ElseIf n, Env e);
    public T visit(Entity n, Env e);
    public T visit(EntityPort n, Env e);
    public T visit(Equal n, Env e);
    public T visit(ExpIndexer n, Env e);
    public T visit(FlatNetwork n, Env e);
    public T visit(FloatType n, Env e);
    public T visit(Formal n, Env e);
    public T visit(FunctionCall n, Env e);
    public T visit(FunctionDecl n, Env e);
    public T visit(GenIntegers n, Env e);
    public T visit(GoTo n, Env e);
    
    public T visit(GreaterOrEqual n, Env e);
    public T visit(GreaterThan n, Env e);
    public T visit(GuardDecl n, Env e);
    public T visit(Identifier n, Env e);
    public T visit(IdentifierExp n, Env e);
    public T visit(IdentifierType n, Env e);

    public T visit(If n, Env e);
    public T visit(IfExp n, Env e);
    public T visit(IntegerLiteral n, Env e);
    public T visit(IntegerType n, Env e);
    public T visit(Lable n, Env e);
    public T visit(LeftShift n, Env e);
    public T visit(LessOrEqual n, Env e);
    public T visit(LessThan n, Env e);
    public T visit(ListAssign n, Env e);
    public T visit(ListCompGen n, Env e);
    public T visit(ListComprehension n, Env e);
    
    
    public T visit(ListType n, Env e);
    public T visit(Minus n, Env e);
    public T visit(Negate n, Env e);
    public T visit(NewArray n, Env e);
    public T visit(Not n, Env e);
    public T visit(NotEqual n, Env e);
    public T visit(Or n, Env e);
    public T visit(Plus n, Env e);

    
    public T visit(PortHH n, Env e);
    public T visit(Print n, Env e);
    public T visit(ProcedureDecl n, Env e);
    public T visit(ReadToken n, Env e);
    public T visit(RightShift n, Env e);
    public T visit(SendToken n, Env e);

    public T visit(SEQ_Actor n, Env e);
    public T visit(SimpEntityExpr n, Env e);
    public T visit(StatementCall n, Env e);
    public T visit(StringConc n, Env e);
    public T visit(StringLiteral n, Env e);
    public T visit(Structure n, Env e);
    public T visit(SwitchCase n, Env e);

    public T visit(Break break1, Env e);    
    
    public T visit(Times n, Env e);
    public T visit(NullType n, Env e);
    public T visit(VarDeclComp n, Env e);
    public T visit(VarDeclSimp n, Env e);
    public T visit(While n, Env e);
    public T visit(For n, Env e);
    public T visit(ForEach n, Env e);
    
    public T visit(TestInputPort n, Env e);
    public T visit(TestOutputPort n, Env e);

    
    
}
