package hh.AST.syntaxtree;
import hh.common.translator.VisitorStm;

public abstract class CStatement {
    public abstract <T,E>T accept(VisitorStm<T,E> v, E env);
}
