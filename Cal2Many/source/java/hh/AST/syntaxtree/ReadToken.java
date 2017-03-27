package hh.AST.syntaxtree;

import java.util.ArrayList;
import java.util.List;


import hh.common.translator.VisitorStm;
public class ReadToken extends CStatement {
  public PortHH p;
  public FormalList is;
  public List<Identifier> ids =new ArrayList<Identifier>();
  public Exp rep; 

  public ReadToken(PortHH ap, FormalList ais,Exp arep) {
    p=ap; is=ais; rep=arep;
  }
  public ReadToken(PortHH ap,  List<Identifier> aids,Exp arep) {
	    p=ap; ids=aids; rep=arep; 
  }

  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
