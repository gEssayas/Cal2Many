package hh.AST.syntaxtree;
import hh.common.translator.VisitorType;



public abstract class Type {
    public abstract <T,E>T accept(VisitorType<T,E> v, E env);
}
