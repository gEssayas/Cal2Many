package hh.AST.syntaxtree;

import java.util.ArrayList;



import hh.common.translator.VisitorExp;
public class TestOutputPort extends Exp {
  public PortHH p;
  public Exp available_room;
  public TestOutputPort(PortHH ap, Exp aavailable_room) {
    p=ap; available_room=aavailable_room; 
  }

  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
