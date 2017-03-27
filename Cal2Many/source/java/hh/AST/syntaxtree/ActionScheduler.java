package hh.AST.syntaxtree;
import hh.common.translator.VisitorActor;

public class ActionScheduler {
public PortList input, output;

  public Identifier i;
  public VarDeclList vars;
  public StatementList sl;

  public ActionScheduler(Identifier ai,  PortList ainput, PortList aoutput, VarDeclList avars, StatementList asl) {
     i=ai; input=ainput; output=aoutput; vars=avars;sl=asl;
  }
 


    public <T,E>T accept(VisitorActor<T,E> v, E env) {
	return v.visit(this,env);
  }
}
