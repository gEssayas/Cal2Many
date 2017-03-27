package hh.AST.syntaxtree;

import java.util.ArrayList;



import hh.common.translator.VisitorExp;
public class TestInputPort extends Exp {
  public PortHH p;
  public Exp available_tokens;
  public TestInputPort(PortHH ap, Exp aavailable_tokens) {
    p=ap; available_tokens=aavailable_tokens;
  }

  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
