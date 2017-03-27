package hh.AST.syntaxtree;

public class ActorFSM {
  public VarDeclList vl;
  public StatementList stms;
  public ExpList conds;

public ActorFSM(VarDeclList avl,StatementList astms, ExpList aconds){
  vl=avl; stms = astms; conds=aconds;
}

}
