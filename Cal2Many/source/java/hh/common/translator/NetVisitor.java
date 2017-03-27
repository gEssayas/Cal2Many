package hh.common.translator;


import hh.AST.syntaxtree.*;
import hh.simplenet.*;

public interface NetVisitor<T,Env> {
    public T visit(Channel n, Env e);
    public T visit(Entity n, Env e);
    public T visit(FlatNetwork n, Env e);
    public T visit(SimpEntityExpr n, Env e);
    public T visit(Structure n, Env e);
    public T visit(EntityPort entityPort, Env e);   
}
