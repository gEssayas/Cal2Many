package hh.AST.syntaxtree;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hh.common.translator.VisitorActor;

public class Profile_ActionDecl {
/*	  public Type t;
	  public Identifier i;
	  public VarDeclSet vl;
	  public CStatement sl;  
  	  public PortList input, output;
	  public ActionFiringCond afc;*/
	
  public String name;
  public PortList input, output;
  public int data_size;
  public int Num_ops;
  public int Weight_ops;
  public Set<String> stateVarsInGaurd		= new HashSet<>();
  public Set<String> stateVarsReadOnly		= new HashSet<>();
  public Set<String> stateVarsWriteOnly		= new HashSet<>();
  public Set<String> stateVarsReadWrite		= new HashSet<>();
  public Set<String> allStateVars			= new HashSet<>();
  public Set<String> functions				= new HashSet<>();
//  public Set<String> functionsGaurd				= new HashSet<>();


  public Profile_ActionDecl(String aname,PortList ainput, PortList aoutput, 
		  int adata_size, int aNum_ops,int aWeight_ops,
		  Set<String> astateVarsInGaurd, Set<String> astateVarsReadOnly, Set<String> astateVarsWriteOnly,
		  Set<String> astateVarsReadWrite, Set<String> afunctions) {
    name = aname; Num_ops = aNum_ops; Weight_ops = aWeight_ops; data_size = adata_size;
    input = ainput; output = aoutput; stateVarsInGaurd = astateVarsInGaurd;
    stateVarsReadOnly = astateVarsReadOnly; stateVarsWriteOnly = astateVarsWriteOnly;
    stateVarsReadWrite = astateVarsReadWrite;
    functions = afunctions; 
       allStateVars.addAll(stateVarsInGaurd);
    allStateVars.addAll(stateVarsReadOnly);
    allStateVars.addAll(stateVarsWriteOnly);
    allStateVars.addAll(stateVarsReadWrite);
    
    for(PortHH p:input)
    	allStateVars.add(p.i.s);
    for(PortHH p:output)
    	allStateVars.add(p.i.s);

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
