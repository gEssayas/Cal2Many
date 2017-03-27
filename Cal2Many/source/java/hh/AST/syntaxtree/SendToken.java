package hh.AST.syntaxtree;

import java.util.List;



import hh.common.translator.VisitorStm;
public class SendToken extends CStatement {
  public PortHH p;
  public ExpList e;
  public List<Boolean> olds;
  public Exp rep;

  public SendToken(PortHH ap, ExpList ae, List<Boolean> aolds,Exp arep) {
    p=ap; e=ae; olds=aolds; rep=arep;
  }

  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
