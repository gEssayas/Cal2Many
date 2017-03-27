package hh.AST.syntaxtree;


import hh.common.translator.VisitorStm;
public class ListAssign extends CStatement {
  public Exp i;
  public ExpList es;
  public Exp e;
  public ExpList len;
  public boolean isListCopy;
  
  public Type varT;

  public ListAssign(Exp ai, ExpList aes, Exp ae, ExpList alen, boolean aisListCopy, Type avarT) {
    i=ai; es=aes; e=ae; len = alen; isListCopy = aisListCopy; varT = avarT;
  }


  public <T,E>T accept(VisitorStm<T,E> v, E env) {
	return v.visit(this,env);
  }
}
