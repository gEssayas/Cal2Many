package hh.AST.syntaxtree;

import java.util.List;

import hh.common.translator.VisitorStm;
public class ConsumeToken extends CStatement {
  public PortHH p;
  public List<Identifier> ids  = new java.util.ArrayList<Identifier>();
  public FormalList is;
  public Exp rep;

  //for Trans goto
  public ConsumeToken(PortHH ap,  List<Identifier> aids,Exp arep) {
	    p=ap; ids=aids; rep=arep; 
	  }
  public ConsumeToken(PortHH ap,  FormalList ais,Exp arep) {
	    p=ap; is=ais; rep=arep; 
	  }

  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
