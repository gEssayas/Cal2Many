package hh.AST.syntaxtree;
import hh.common.Pair;
import hh.common.translator.VisitorActor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




public class Profile_Actor {
	public IdentifierType it;
	public PortList input, output;
	public List<Pair<String,Integer>> afls = new ArrayList<>(); // properti
	public List<Pair<String,Integer>> vl= new ArrayList<>(); // state vars
	public Pair<Integer, Integer> globExps = null; // exps in state vars init
	public List<Profile_FunctionDecl> funs = new ArrayList<>();
	public List<Profile_ProcedureDecl> pros = new ArrayList<>();
	public List<Profile_ActionDecl> acs= new ArrayList<>();
	public Profile_ActionScheduler actionScheduler;

	 
	public Profile_Actor(IdentifierType ait, PortList in, PortList out, 
			List<Pair<String,Integer>> aafls,
			List<Pair<String,Integer>> avl, 
			Pair<Integer, Integer> globExpSize, 
			List<Profile_FunctionDecl> afuns,
			List<Profile_ProcedureDecl> apros,
			List<Profile_ActionDecl> aacs,  
			Profile_ActionScheduler aac) {

		it=ait;   input=in; output = out; 
		afls=aafls; vl=avl; globExps = globExpSize; 
		funs=afuns; pros=apros; 
		acs=aacs;  actionScheduler=aac;		
	}
	public <T,E>T accept(VisitorActor<T,E> v, E env) {
		return v.visit(this,env);
	}
}
