package hh.AST.syntaxtree;

public class ActionFiringCond {
  public VarDeclList vl;
  public StatementList initStms;
  public StatementList stmsAftertoken;
  public ExpList conds;

public ActionFiringCond(VarDeclList avl,StatementList astms, StatementList astmsAftertoken,ExpList aconds){
  vl=avl; initStms = astms; stmsAftertoken =astmsAftertoken; conds=aconds;
}
public ActionFiringCond(){
	vl=new VarDeclList(); initStms = new StatementList(); stmsAftertoken= new StatementList(); conds = new ExpList();
}
}
