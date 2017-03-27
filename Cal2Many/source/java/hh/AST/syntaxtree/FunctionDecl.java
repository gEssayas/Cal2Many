package hh.AST.syntaxtree;
import hh.common.translator.VisitorActor;

public class FunctionDecl {
  public Type t;
  public Identifier i;
  public FormalList fl;
  public VarDeclList vl;
  public Exp e;

  public FunctionDecl(Type at, Identifier ai, FormalList afl, VarDeclList avl, 
                     Exp ae) {
    t=at; i=ai; fl=afl; vl=avl; e=ae;
  }
 


    public <T,E>T accept(VisitorActor<T,E> v, E env) {
	return v.visit(this,env);
  }
}
//___ParDeclType [] 	typeParameters;
//___ParDeclValue [] valueParameters;
//___DeclType []		typeDecls;
//___DeclVar [] 		varDecls;
//___Expression      body;
//___TypeExpr		returnTypeExpr;
