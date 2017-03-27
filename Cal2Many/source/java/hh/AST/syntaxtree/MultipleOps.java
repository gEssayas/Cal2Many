package hh.AST.syntaxtree;
import java.util.List;



import hh.common.translator.VisitorExp;
public class MultipleOps extends Exp {
  public ExpList Operands;
  public List<String> Operations;
  
  public MultipleOps(List<String> aOperations,ExpList aOperands) {
	  Operands = aOperands; Operations =aOperations;
  }

  public <T,E>T accept(VisitorExp<T,E> v, E env) {
	return v.visit(this,env);
  }
}
