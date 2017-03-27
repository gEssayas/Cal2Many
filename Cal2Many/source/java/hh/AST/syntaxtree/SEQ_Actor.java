package hh.AST.syntaxtree;
import hh.common.translator.VisitorActor;

import java.util.ArrayList;
import java.util.HashMap;




public class SEQ_Actor {
	public HashMap<String, ArrayList<String>> inputConnections;
	public HashMap<String, ArrayList<String>> outputConnections;
	public IdentifierType it;
	public Identifier i;
	public FormalList afls; // properti
	public PortList input, output;
	public VarDeclList vl; // state vars
	public GuardDeclList grs;
	public FunctionDeclList funs;
	public ProcedureDeclList pros;
	public ActionDeclList acs;
	public ActionScheduler actionScheduler;
	public HashMap<String,Type> varTypes;

	 
	public SEQ_Actor(HashMap<String,Type> avarTypes,IdentifierType ait,Identifier ai, FormalList aafls,PortList in, PortList out, VarDeclList avl, 
			GuardDeclList agrs, ActionDeclList aacs, FunctionDeclList afuns,ProcedureDeclList apros,
			ActionScheduler aac,HashMap<String, ArrayList<String>> inputs,HashMap<String, ArrayList<String>> outputs) {

		varTypes = avarTypes;
		it=ait; i=ai; afls=aafls; input=in; output = out; vl=avl; acs=aacs;grs=agrs; funs=afuns; pros=apros; 
		actionScheduler=aac; outputConnections =outputs;
		inputConnections=inputs;
	}
	public SEQ_Actor(IdentifierType ait,Identifier ai, FormalList aafls,PortList in, PortList out, VarDeclList avl, 
			GuardDeclList agrs, ActionDeclList aacs, FunctionDeclList afuns,ProcedureDeclList apros,
			ActionScheduler aac,HashMap<String, ArrayList<String>> inputs,HashMap<String, ArrayList<String>> outputs) {

		it=ait; i=ai; afls=aafls; input=in; output = out; vl=avl; acs=aacs;grs=agrs; funs=afuns; pros=apros; 
		actionScheduler=aac; outputConnections =outputs;
		inputConnections=inputs;
	}


	public <T,E>T accept(VisitorActor<T,E> v, E env) {
		return v.visit(this,env);
	}
}
