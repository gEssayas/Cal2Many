package hh.common.translator;


import hh.AST.syntaxtree.*;
import hh.simplenet.SimpEntityExpr;
public interface VisitorExp<T,Env> {
	
	public T visit(VectorOps n, Env e);
	public T visit(MatrixOps n, Env e);

	
	public T visit(SimpEntityExpr n, Env e);
	
	public T visit(And n, Env e);
	public T visit(BitAnd n, Env e);
	public T visit(BitNot n, Env e);
	public T visit(BitOr n, Env e);
	public T visit(BitXOr n, Env e);
	public T visit(BooleanLiteral n, Env e);
	public T visit(Call n, Env e);
	public T visit(CharLiteral n, Env e);
	public T visit(Divide n, Env e);
	public T visit(DoubleLiteral n, Env e);
	public T visit(Equal n, Env e);
	public T visit(ExpIndexer n, Env e);
	public T visit(ExpList n, Env e);
	public T visit(FunctionCall n, Env e);
	public T visit(GenIntegers n, Env e);
	public T visit(GreaterOrEqual n, Env e);
	public T visit(GreaterThan n, Env e);
	public T visit(IdentifierExp n, Env e);
	public T visit(IfExp n, Env e);
	public T visit(IntegerLiteral n, Env e);
	public T visit(LeftShift n, Env e);
	public T visit(LessOrEqual n, Env e);
	public T visit(LessThan n, Env e);
	public T visit(ListCompGen n, Env e);
	public T visit(ListComprehension n, Env e);
	public T visit(ListConc n, Env e);
	public T visit(Minus n, Env e);
    public T visit(MultipleOps n, Env e);
	public T visit(Negate n, Env e);
	public T visit(NewArray n, Env e);
	public T visit(Not n, Env e);
	public T visit(NotEqual n, Env e);
	public T visit(Or n, Env e);
	public T visit(Plus n, Env e);
	public T visit(PortHH n, Env e);
	public T visit(RightShift n, Env e);
	public T visit(StringConc n, Env e);
	public T visit(StringLiteral n, Env e);
	public T visit(TestInputPort n, Env e);
	public T visit(TestOutputPort n, Env e);
	public T visit(Times n, Env e);    
}
