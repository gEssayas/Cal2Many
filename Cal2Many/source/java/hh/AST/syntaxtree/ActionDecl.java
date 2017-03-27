package hh.AST.syntaxtree;
import hh.common.translator.VisitorActor;


public class ActionDecl {
  public Type t;
  public Identifier i;
  public VarDeclList vl;
  public CStatement sl;  
  public PortList input, output;
  public ActionFiringCond afc;


public ActionDecl(Type at, Identifier ai, VarDeclList avl,  CStatement asl) {
  t=at; i=ai;  vl=avl; sl=asl;  afc= new ActionFiringCond();
}

public ActionDecl(Type at, PortList ainput, PortList aoutput,Identifier ai, VarDeclList avl, CStatement asl) {
	input= ainput; output=aoutput;
t=at; i=ai;  vl=avl;  sl=asl; 
afc= new ActionFiringCond();
}

public ActionDecl(Type at, PortList ainput, PortList aoutput,Identifier ai, VarDeclList avl, 
		  CStatement asl, ActionFiringCond aafc) {
	input= ainput; output=aoutput;
t=at; i=ai;  vl=avl;  sl=asl;  
afc = aafc;
}



    public <T,E>T accept(VisitorActor<T,E> v, E env) {
	return v.visit(this,env);
  }
}
