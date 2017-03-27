package hh.AST.syntaxtree;
import java.util.HashSet;
import java.util.Set;

import hh.common.translator.VisitorActor;

public class Profile_Statement {
	  public Set<String> defs = new HashSet<>();
	  public Set<String> uses = new HashSet<>();
	  public int Num_ops;
	  public int Weight_ops;
	  
	  public Profile_Statement(Set<String> adefs, Set<String> auses,int aNum_ops,int aWeight_ops) {
	    defs = adefs; uses = auses; Num_ops = aNum_ops; Weight_ops = aWeight_ops;
	  }

}
//___ParDeclType [] 	typeParameters;
//___ParDeclValue [] valueParameters;
//___DeclType []		typeDecls;
//___DeclVar [] 		varDecls;
//___Expression      body;
//___TypeExpr		returnTypeExpr;
