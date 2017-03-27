package hh.AST.syntaxtree;
import java.util.HashSet;
import java.util.Set;

import hh.common.translator.VisitorActor;

public class Profile_FunctionDecl {
  public String name;
  public Set<String> defs = new HashSet<>();
  public Set<String> uses = new HashSet<>();
  public int Num_ops;
  public int Weight_ops;
  public int data_size;
 
  public Profile_FunctionDecl(String aname, Set<String> adefs, Set<String> auses, int aNum_ops,int aWeight_ops, int adata_size) {
	    name = aname; defs = adefs; uses = auses;
	    Num_ops = aNum_ops; Weight_ops = aWeight_ops; data_size = adata_size; 
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
