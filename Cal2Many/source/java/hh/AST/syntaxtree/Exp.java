package hh.AST.syntaxtree;
import hh.common.translator.VisitorExp;



public abstract class Exp {
    public abstract <T,E>T accept(VisitorExp<T,E> v, E env);
}
