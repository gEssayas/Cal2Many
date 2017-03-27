package hh.AST.syntaxtree;


import hh.common.translator.VisitorStm;
public class Block extends CStatement {
  public StatementList sl;

  public Block(StatementList asl) {
    sl=asl;
  }


  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
