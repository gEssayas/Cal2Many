package hh.backend.c;

import hh.AST.syntaxtree.*;
import hh.common.translator.VisitorActor;
import hh.common.translator.VisitorExp;
import hh.common.translator.VisitorStm;
import hh.common.translator.VisitorType;
import hh.simplenet.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;




public class PrettyPrintCalC implements 
VisitorActor<Object,Object>, VisitorType<Object,Object>, VisitorStm<Object,Object>, VisitorExp<Object,Object>
{

	private String indent = "";
	private String comint = "";
	private boolean duplicatVar,oldval,isGlobal;
	public ExpList globExp= new ExpList();
	public IdentifierList globId= new IdentifierList(); 
	public HashSet<String> hsInclude = new HashSet<String>();
	public String cCode ="";
	public String sActionInsOuts ="";
	public String sActionInsOutsInts ="";
	public String sInclude ="";
	public String instName="";
	public String typeName="";
	public ArrayList<Channel> cInput = new ArrayList<Channel>();
	public ArrayList<Channel> cOutput = new ArrayList<Channel>();

	public HashMap<String, ArrayList<String>> inputCons;
	public HashMap<String, ArrayList<String>> outputCons;
	public boolean printPort;
    public int tmpInt =0;
	// MainClass m;
	// ClassDeclList cl;
	public Object visit(SEQ_Actor n, Object env) {
		instName=n.i.s;
		typeName = n.it.s;
		System.out.println(" ACtor namee" + instName);
		inputCons=n.inputConnections;
		outputCons=n.outputConnections;		
		int j;

		if( !(n.afls.isEmpty())){
			cCode = cCode +("\n\n/*(where are the formalsssssss \n");
			for ( int i = 0; i < n.afls.size(); i++ ) {
				cCode = cCode +("extern ");
				n.afls.get(i).accept(this,env);
				if (i+1 < n.afls.size()) { cCode = cCode +("; "); }
			}
			cCode = cCode +(";\n)*/\n"); 
		}
		ArrayList<String> multiConne = new ArrayList<String>();
		if(n.input!=null)
			if(n.input.size()>0){
				cCode = cCode +("//InputPorts \nextern fifo ");

				for( j=0;j<n.input.size()-1;j++){
					multiConne.clear();
					multiConne.addAll((ArrayList<String>) n.input.get(j).accept(this,env));
					for(String s:multiConne)
						cCode = cCode +s+ ", ";
				}
				multiConne.clear();
				multiConne.addAll((ArrayList<String>) n.input.get(j).accept(this,env));
				for( j=0;j<multiConne.size()-1;j++)
					cCode = cCode +multiConne.get(j)+ ", ";
				cCode = cCode + multiConne.get(j)+";";

			}
		if(n.output!=null)
			if(n.output.size()>0){

				cCode = cCode +("\n//OutputPorts\n extern fifo ");




				for( j=0;j<n.output.size()-1;j++){
					multiConne.clear();
					multiConne.addAll((ArrayList<String>) n.output.get(j).accept(this,env));
					for(String s:multiConne)
						cCode = cCode +s+ ", ";
				}
				multiConne.clear();
				multiConne.addAll((ArrayList<String>) n.output.get(j).accept(this,env));
				for( j=0;j<multiConne.size()-1;j++)
					cCode = cCode +multiConne.get(j)+ ", ";
				cCode = cCode + multiConne.get(j)+";";
			}	
		cCode = cCode +"\n";// extern int "+n.i.s +"_State;\n";
		isGlobal = true;
		for(VarDecl vr: n.vl){
			comint="";
			//			cCode = cCode +;
			vr.accept(this,"static ");
			cCode = cCode +("\n");

		}
		isGlobal = false;

		if(n.pros !=null)
			for(ProcedureDecl pro : n.pros){
				cCode = cCode +"static ";
				if(pro.t!=null)
					pro.t.accept(this,env);
				else 
					cCode = cCode +("void");
				cCode = cCode +(" ");
				pro.i.accept(this,env);
				cCode = cCode +(" (");
				if(pro.fl!=null)
					for ( int i = 0; i < pro.fl.size(); i++ ) {
						pro.fl.get(i).accept(this,env);
						if (i+1 < pro.fl.size()) { cCode = cCode +(", "); }
					}
				cCode = cCode +(");\n");
			}

		if(n.funs !=null)
			for(FunctionDecl fun : n.funs){
				if(fun.i.s.startsWith("Mop_"))
					continue;
				cCode = cCode +"static ";
				if(fun.t!=null)
					fun.t.accept(this,env);
				else 
					cCode = cCode +("int");
				cCode = cCode +(" ");
				fun.i.accept(this,env);
				cCode = cCode +(" (");
				if(fun.fl!=null)
					for ( int i = 0; i < fun.fl.size(); i++ ) {
						fun.fl.get(i).accept(this,env);
						if (i+1 < fun.fl.size()) { cCode = cCode +(", "); }
					}
				cCode = cCode +(");\n");
			}


		//	cCode = cCode + "static int (*next_state) (void) = " +typeName + "_State0;\n ";
		/*for(PortHH po:n.output)
			cCode = cCode + "extern FILE *"+ typeName+"_"+ po.i.s +";\n";*/
	//	cCode = cCode + "extern FILE *"+ typeName+";\n";
		Integer actionNo=0;
		for(ActionDecl ac: n.acs)
			ac.accept(this,actionNo++);

		if(n.funs !=null)
			for(FunctionDecl fun:n.funs)
				fun.accept(this,env);
		if(n.pros !=null)
			for(ProcedureDecl pro : n.pros)
				pro.accept(this,env);
		if(n.grs!=null)
			for(GuardDecl gr: n.grs)
				gr.accept(this,env);


		if(globId.size()!=0){

			cCode = cCode + "int int_state_vars_"+n.i.s+"=1;\nvoid initStateVars"+n.i.s+"() {\n";
			for( j=0; j< globId.size();j++){

				if(globExp.get(j) instanceof ListComprehension){
					//	ListComprehension gex =(ListComprehension)globExp.get(j);
					//	PrintListComp(globId.get(j),gex,env);
					//					globId.get(j).accept(this, env);
					//					cCode = cCode +" = ";

					globExp.get(j).accept(this,globId.get(j).s);
					//	cCode = cCode +  "\n DisplayListComprehension \n";
					comint="";


					//					DisplayListComprehension((ListComprehension)globExp.get(j),(Object)globId.get(j).s);



				}
				else {
					globId.get(j).accept(this, env);
					cCode = cCode +(" = ");
					globExp.get(j).accept(this, env);
					cCode = cCode +(";\n");
				}

			}
			//			for(int im=0; im<globVal.size();im++){
			//				globVal.
			//			}
			cCode = cCode +("\n}\n");

		}

		n.actionScheduler.accept(this, env);

		cCode = "//code from " +n.it.s + ".cal, instance "+n.i.s+"\n\n#include <complex.h>\n#include \""+n.i.s+".h\"\n#include \"include/calChannel.h\"\n\n"+ sInclude + cCode;
		return cCode;
	}


	private void DisplayListComprehension(ListComprehension n,	Object env) {
		String var=(String)env;
		ArrayList<String> vars= new ArrayList<String>();
		ArrayList<ListComprehension> cmps= new ArrayList<ListComprehension>();
		ExpList exEls= new ExpList();
		ListOfListCompGen gens = n.compGen;

		ExpList exs =n.eles;
		//	Exp exp=null;
		cmps.add(n);

		int exseles=exs.size();
		while(exseles>0){
			for(int iels=0; iels<exs.size();iels++){
				if(exs.get(iels) instanceof ListComprehension){
					ListComprehension lc =(ListComprehension)exs.get(iels);
					cmps.add(lc);
					exs=lc.eles;
					exseles=exs.size();
				}
				else{
					//					System.out.println(iels);				
					exEls.add(exs.get(iels));
					exseles--;
				}
			}

		}
		//TODO was only > 
		if(cmps.size()>0){
			for(int ic=cmps.size()-1;ic>=0;ic--)
			{
				ListComprehension lc=cmps.get(ic);
				if(lc.compGen !=null){
					for(int icg=0; icg <lc.compGen.size();icg++){
						if(lc.compGen.get(icg).colExp instanceof GenIntegers)
							vars.add((String)lc.compGen.get(icg).accept(this,env));
						else
							lc.compGen.get(icg).accept(this, env);
					}

				}
			}

			if(vars.size()>0){
				cCode = cCode +var;
				for(int is=vars.size()-1;is>=0;is--){
					String sIndex =vars.get(is);
					cCode = cCode +  "[" +sIndex +"]";
				}
				cCode = cCode + " = ";
				if(exEls.size()>0)
					exEls.get(0).accept(this, env);
				else
					cCode = cCode +"0";
				cCode = cCode +";";
			}
		}
		if(n.compGen!=null)
			if(n.compGen.size()>0){

			}
			else if(exEls.size()>0){
				for(int i=0; i<exEls.size();i++){
					cCode = cCode +"  "+var+"["+i+"] = ";
					exEls.get(i).accept(this,env);
					cCode = cCode +";\n";

				}
			}

	}



	// Type t;
	// Identifier i;

	private void PrintIntegrs(GenIntegers gI, ExpList fils, FormalList vars, Object env) {
		cCode = cCode +("int " + vars.get(0).i.s +", temp_" + vars.get(0).i.s + "=0;\nfor("+ vars.get(0).i.s +"=");
		gI.e1.accept(this, env);
		cCode = cCode +("; "+vars.get(0).i.s +" <=" );
		gI.e2.accept(this, env);
		cCode = cCode + "; " +vars.get(0).i.s +" ++){\n  "+ (String)env+ "[temp_" +vars.get(0).i.s + "] = ";
		vars.get(0).accept(this, env);

		cCode = cCode +"temp_" +vars.get(0).i.s + " ++;\n}\n  ";



	}
	public void PrintIntegrs(GenIntegers gI, Identifier id, String type, Object env){
		cCode = cCode +(type + id.s + ";\nfor("+ id.s +"=");
		gI.e1.accept(this, env);
		cCode = cCode +("; "+id.s +" <=" );
		gI.e2.accept(this, env);
		cCode = cCode +("; " +id.s +" ++){\n");

	}

	// Type t;
	// Identifier i;
	// PortDeclList input, output;
	// VarDeclList vl;
	// StatementList sl,oStms;
	// Exp e;
	public Object visit(ActionDecl n, Object env) {

		int j;
		String old = indent;
		sActionInsOuts = ""+(Integer)env;
		cCode = cCode +("\nstatic void ");
		//n.t.accept(this,env);
		cCode = cCode +" ";
		n.i.accept(this,env);
		cCode = cCode +" (";

		cCode = cCode +") { \n";
	//	cCode = cCode +"fprintf("+typeName+",\""+(Integer)env +"\");\n";
		indent = indent + "  ";
		duplicatVar=true;

		for ( int i = 0; i < n.vl.size(); i++ ) {

			cCode = cCode +(indent);
			if(n.vl.get(i) instanceof VarDeclSimp){
				if(((VarDeclSimp)n.vl.get(i)).t!=null)
					((VarDeclSimp)n.vl.get(i)).t.accept(this, env);
				else
					cCode = cCode +"int";
				cCode = cCode +" ";
				((VarDeclSimp)n.vl.get(i)).i.accept(this, env);
				cCode = cCode +";";
			}
			else{
				((VarDeclComp)n.vl.get(i)).t.accept(this, ((VarDeclComp)n.vl.get(i)).i.s);
				//	((VarDeclComp)n.vl.get(i)).i.accept(this, env);
				cCode = cCode +";";
			}
			cCode = cCode +("\n");
		}

		//	System.out.println("    // Input");


		//	System.out.println("    // Body");
         
		

		n.sl.accept(this, env);


		//write on out put port
		//	System.out.println("    // Output");
		duplicatVar=false;

		//	cCode = cCode +(indent + "return ");
		//	n.e.accept(this,env);
		indent = old;
		sActionInsOuts = "\""+ sActionInsOuts + "\\n\""+ sActionInsOutsInts;
		
	//	cCode = cCode +"fprintf("+typeName+",\"\\n\");\n";


		
		cCode = cCode +("  }");
		return null;}
	public Object visit(ActionScheduler n, Object env) {
		int j;
		cCode = cCode + "\n/****  ";
		if(n.input!=null)
			if(n.input.size()>0){
				cCode = cCode +("//InputPorts fifo ");

				for( j=0;j<n.input.size()-1;j++){
					n.input.get(j).accept(this,env);
					cCode = cCode +(", ");
				}

				n.input.get(j).accept(this,env);
				cCode = cCode +(";");
			}
		if(n.output!=null)
			if(n.output.size()>0){
				cCode = cCode +("\n//OutputPorts fifo ");

				for( j=0;j<n.output.size()-1;j++){
					n.output.get(j).accept(this,env);
					cCode = cCode +(", ");
				}

				n.output.get(j).accept(this,env);
				cCode = cCode +(";");
			}	

		cCode = cCode +"****/";
		cCode = cCode + "\nint ";
		n.i.accept(this, env);
		cCode = cCode + "(";
	//	cCode = cCode + "\nstatic void run(actor_"+typeName+" *self)";
		cCode = cCode +") {\n";
		if(globId.size()!=0){			
			cCode = cCode + "    if(int_state_vars_"+instName+"==1){\n" +
					"        int_state_vars_"+instName+"=0;\n" +
					"        initStateVars"+instName+"();}\n";
		}
		//		cCode = cCode +"" +
		//	//			"\nprintf(\"------.............  "+ instName+" ----------\\n\");\n"+
		//				"{\n";

		for(VarDecl v:n.vars){
			v.accept(this, env);
			cCode = cCode +"\n";
		}

		for(CStatement s:n.sl)
			s.accept(this,env);
		cCode = cCode + "\n}";
		return null;
	}

	// Exp e1,e2;
	public Object visit(And n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" && ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}

	// Identifier i;
	// Exp e1,e2;
	public Object visit(FieldAssign n, Object env) {

		n.i.accept(this,env);
		cCode = cCode +("[");
		n.e1.accept(this,env);
		cCode = cCode +("] = ");
		n.e2.accept(this,env);
		cCode = cCode +(";");

		return null;
	}



	// Identifier i;
	// Exp e;
	public Object visit(Assign n, Object env) {
		if(n.e instanceof ListComprehension)
			n.e.accept(this, n.i.s);
		else{
			n.i.accept(this,env);
			cCode = cCode +(" = ");		
			n.e.accept(this,env);
			cCode = cCode +(";");
		}
		return null;}

	// Exp e1,e2;
	public Object visit(BitAnd n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" & ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}


	// Exp e1,e2;
	public Object visit(BitNot n, Object env) {
		cCode = cCode +("( ~ ");
		n.e.accept(this,env);
		cCode = cCode +(")");
		return null;}

	// Exp e1,e2;
	public Object visit(BitOr n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" | ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}

	@Override
	public Object visit(BitXOr n, Object env) {

		cCode = cCode + "(";
		n.e1.accept(this, env);
		cCode = cCode + "^";
		n.e2.accept(this, env);
		cCode = cCode + ")";



		return null;
	}

	// StatementList sl;
	public Object visit(Block n, Object env) {
		//	cCode = cCode +("{ \n");
		String old = indent;
		indent = indent + "  ";
		for ( int i = 0; i < n.sl.size(); i++ ) {
			cCode = cCode +(indent);
			n.sl.get(i).accept(this,env);
			cCode = cCode +("\n");
		}
		indent = old;
		//		cCode = cCode +(indent+"}\n ");
		return null;}

	public Object visit(BooleanType n, Object env) {
		cCode = cCode +("_Bool");
		return null;}

	public Object visit(BooleanLiteral n, Object env) {
		if(n.value){
			cCode = cCode +( "true");
		}
		else{
			cCode = cCode +( "false");
		}
		return null;}

	// Exp e;
	// identifier i;
	// ExpList el;
	public Object visit(Call n, Object env) {
		n.e.accept(this,env);
		cCode = cCode +(".");
		n.i.accept(this,env);
		cCode = cCode +("(");
		for ( int i = 0; i < n.el.size(); i++ ) {
			n.el.get(i).accept(this,env);
			if ( i+1 < n.el.size() ) { cCode = cCode +(", "); }
		}
		cCode = cCode +(")");
		return null;}



	public Object visit(ConsumeToken n, Object env){

		ArrayList<String> conns= new ArrayList<String>();

		if(n.rep instanceof IntegerLiteral)
			if(((IntegerLiteral)n.rep).i>1){
				String index=n.p.i.s +"_ix";
				cCode = cCode + "\nint "+ index+" =0;\nfor(;"; 
				cCode = cCode + index+ "<"+((IntegerLiteral)n.rep).i+"; " +index +"++){\n       ";
				n.ids.get(0).accept(this,env);
				cCode = cCode +"["+index+"] = ";
				cCode = cCode +("ConsumeToken(&");
				conns.addAll((ArrayList<String>)n.p.accept(this,env));
				cCode = cCode +conns.get(0)+ ", 1);\n";
				//cCode = cCode +"fprintf("+typeName+",\" %d \","+n.ids.get(0).s+"["+index+"]);\n";
				cCode = cCode +"}";


			}
			else {
				for( Identifier i: n.ids){
					//	cCode = cCode +(indent);
					i.accept(this,env);
					cCode = cCode +(" = ");
					cCode = cCode +("ConsumeToken(&");
					conns.addAll((ArrayList<String>)n.p.accept(this,env));
					cCode = cCode +conns.get(0)+ ", 1);\n";
			//		cCode = cCode +"fprintf("+typeName+",\" %d \","+i.s+");\n";

				}    	
			}
		else
		{
			String index=n.p.i.s +"_ix";
			cCode = cCode + "\nint "+index+" =0; \nfor(; " + index+ "<";
			n.rep.accept(this, env);
            cCode = cCode + "; " +index +"++)\n       ";
			n.ids.get(0).accept(this,env);
			cCode = cCode +"["+index+"] = ";
			cCode = cCode +("ConsumeToken(&");
			conns.addAll((ArrayList<String>)n.p.accept(this,env));
			cCode = cCode +conns.get(0)+ ", 1);\n";
			
		//	cCode = cCode +"fprintf("+typeName+",\" %d \","+n.ids.get(0).s+"["+index+"]);\n";

		}
		return null;
	}

	// Exp e1,e2;
	public Object visit(Divide n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" / ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}

	public Object visit(DoubleLiteral n, Object env) {
		cCode = cCode +(""+n.d);
		return null;}
	@Override
	public Object visit(ElseIf n, Object env) {
		String old = indent;
		indent = indent + "  ";
		cCode = cCode +("else if (");
		n.e.accept(this,env);
		cCode = cCode +(") {\n");
		cCode = cCode +(indent);
		n.s1.accept(this,env);
		cCode = cCode +("}\n");
		if(n.s2 !=null){
			cCode = cCode +(old + "else {\n");
			cCode = cCode +(indent);
			n.s2.accept(this,env);
			cCode = cCode +"\n}";
		}
		//	
		indent=old;
		return null;}

	public Object visit(Entity n, Object env) {

		cCode= cCode +" // Params for actor ";		
		n.it.accept(this, env);

		cCode= cCode +" instance  ";	

		n.i.accept(this, env);	
		cCode= cCode +"\n int ";	
		n.i.accept(this, env);
		cCode= cCode +"_state=0;\n";
		for(VarDecl v : n.parms){
			v.accept(this, env);
		}
		return null;
	}

	public Object visit(EntityPort n, Object env) {
		//cCode = cCode +(n.t.s + "_");
		boolean isTopPort =false;
		if(n.i.s !=null)
			cCode = cCode +(n.i.s+"_"); 
		else{
			isTopPort=true;
			cCode= cCode +"top_";
		}

		cCode = cCode +(n.p.s);
		return isTopPort;
	}


	public Object visit(Equal n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" == ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}

	@Override
	public Object visit(ExpIndexer n, Object env) {
		//cCode = cCode +("[");

		n.structure.accept(this, env);
		//for(Exp ex: n.location)
		{
			cCode = cCode +("[");
			n.location.accept(this, env);
			cCode = cCode +("]");

		}

		return null;
	}

	// Type t;
	// Identifier i;
	public Object visit(Formal n, Object env) {

		if(n.t!=null)
			if(n.t instanceof ListType){
				n.t.accept(this,n.i.s);
				return "";
			}
			else
				cCode = cCode +(indent +"int");
		cCode = cCode +(" ");
		String s= (String)n.i.accept(this,env);
		return s;}

	public Object visit(FunctionCall n, Object env){
		int i;
		n.i.accept(this,env);
		cCode = cCode +("(");
		for(i=0; i<n.exs.size(); i++){
			n.exs.get(i).accept(this,env);

			if(i<n.exs.size()-1)
				cCode = cCode +(", ");			

		}
		cCode = cCode +(")");
		printPort = false;	
		return null;
	}

	public Object visit(FunctionDecl n, Object env) {
		if(n.i.s.startsWith("Mop_"))
			return null;
		String old = indent;
		cCode = cCode +(indent+"static ");
		if(n.t!=null)
			n.t.accept(this,env);
		else 
			cCode = cCode +("int");
		cCode = cCode +(" ");
		n.i.accept(this,env);
		cCode = cCode +(" (");
		if(n.fl!=null)
			for ( int i = 0; i < n.fl.size(); i++ ) {
				n.fl.get(i).accept(this,env);
				if (i+1 < n.fl.size()) { cCode = cCode +(", "); }
			}
		cCode = cCode +(") { \n");
		indent = indent + "  ";
		if(n.vl!=null)
			for ( int i = 0; i < n.vl.size(); i++ ) {
				cCode = cCode +(indent);
				n.vl.get(i).accept(this,env);
				cCode = cCode +("\n");
			}		


		if(n.e instanceof ListComprehension){		
			n.e.accept(this,n.i.s+"_ARRAY");		
		}
		else{		
			cCode = cCode +(indent + "return ");
			n.e.accept(this,env);
		}
		indent = old;
		cCode = cCode +(";\n");
		cCode = cCode +("  }\n");
		return null;}

	@Override
	public Object visit(GenIntegers n, Object env) {

		//TODO GENinteger
		//	    String s=(String)env;
		//		
		//		cCode = cCode +"for( " +s ;
		//		
		//		cCode = cCode +(" = ");
		//		n.e1.accept(this,env);
		//		cCode = cCode +("- 1; " + s +"<=");
		//		n.e2.accept(this,env);
		//		cCode = cCode +" - 1; "+ s+" ++)\n";



		FormalList genFrs= new FormalList();
		String var="";
		if(env instanceof FormalList){
			genFrs=(FormalList)env;
			if (genFrs.size()>0){
				for(Formal genFr: genFrs){
					cCode = cCode +("for(");
					genFr.accept(this, env);
					cCode = cCode +(" = ");
					n.e1.accept(this,env);
					cCode = cCode +("; " + genFr.i.s +"<=");
					n.e2.accept(this,env);
					cCode = cCode +"; "+ genFr.i.s+" ++)\n";
					var=genFr.i.s;
				}
			}
		}
		else if (env instanceof VarDeclList){
			VarDeclList varss = (VarDeclList)env;
			for(VarDecl vars: varss){
				if(vars instanceof VarDeclSimp)
					var = ((VarDeclSimp)vars).i.s;
				else
					var ="i_0";
				cCode = cCode +"\nfor( int " +var ;
				cCode = cCode +(" = ");
				n.e1.accept(this,env);
				cCode = cCode +("; " + var +"<=");
				n.e2.accept(this,env);
				cCode = cCode +"; "+ var+" ++)\n";

			}
		}
		else{
			var ="i_0";
			cCode = cCode +"\nfor( int " +var ;
			cCode = cCode +(" = ");
			n.e1.accept(this,env);
			cCode = cCode +("; " + var +"<=");
			n.e2.accept(this,env);
			cCode = cCode +"; "+ var+" ++)\n";

		}


		return var;
	}

	@Override
	public Object visit(GoTo n, Object e) {
		cCode = cCode +("goto " +n.s+";\n");
		return null;
	}

	public Object visit(GreaterOrEqual n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" >= ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}

	public Object visit(GreaterThan n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" > ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public Object visit(GuardDecl n, Object env) {
		String old = indent;
		cCode = cCode +(indent+"");
		if(n.t!=null)
			n.t.accept(this,env);
		else 
			cCode = cCode +("int");
		cCode = cCode +(" ");
		n.i.accept(this,env);
		cCode = cCode +(" () { \n");
		indent = indent + "  ";
		if(n.vl!=null)
			for ( int i = 0; i < n.vl.size(); i++ ) {
				cCode = cCode +(indent);
				n.vl.get(i).accept(this,env);
				cCode = cCode +("\n");
			}

		if(n.sl!=null)
			for ( int i = 0; i < n.sl.size(); i++ ) {
				indent = old+"  ";
				cCode = cCode +(indent);
				n.sl.get(i).accept(this,env);
				if ( i < n.sl.size() ) { cCode = cCode +("\n"); }
			}


		cCode = cCode +(indent + "return ");
		if(n.i.s.endsWith("One_Guard"))
			System.out.print("one is hear");
		for(int i=0; i < n.es.size(); i++){
			if(n.es.get(i) !=null)
				n.es.get(i).accept(this,env);
			if(i < n.es.size()-1)
				cCode = cCode +(" && ");
			else
				cCode = cCode +(";");

		}
		indent = old;
		cCode = cCode +(" \n}\n");
		return null;}

	// String s;
	public Object visit(Identifier n, Object env) {
		cCode = cCode +(n.s.replace('.','_')); 
		return n.s;
	}

	// String s;
	public Object visit(IdentifierExp n, Object env) {
		if(n.s.startsWith("$old$"))
			if(oldval)
				cCode = cCode +("old_"+n.s.substring(5));
			else
				cCode = cCode +(n.s.substring(5));
		else  		
			cCode = cCode +(n.s.replace('.','_'));
		return n.s;}

	// String s;
	public Object visit(IdentifierType n, Object env) {
		cCode = cCode +(n.s);
		return n.s;}

	// Exp e;
	// Statement s1,s2;
	public Object visit(If n, Object env) {
		String old = indent;
		indent = indent + "  ";
		cCode = cCode +("if (");
		n.e.accept(this,env);
		cCode = cCode +("){ \n");
		cCode = cCode +(indent);
		n.s1.accept(this,env);
		cCode = cCode +("}\n");
		if(n.s2 !=null){
			cCode = cCode +(old + "else {\n");
			cCode = cCode +(indent);
			n.s2.accept(this,env);
			cCode = cCode +("}\n");

		}
		indent=old;
		return null;}

	// Exp c,e1,e2;
	public Object visit(IfExp n, Object env){
		cCode = cCode +("(");
		n.c.accept(this,env);
		cCode = cCode +(" ? ");
		n.e1.accept(this,env);
		cCode = cCode +(" : ");
		if(n.e2!=null)
			n.e2.accept(this,env);
		else
			cCode = cCode + "0";
		cCode = cCode +(")");
		return null;}

	// int i;
	public Object visit(IntegerLiteral n, Object env) {
		cCode = cCode +(""+ n.i);
		return null;}


	public Object visit(IntegerType n, Object env) {

		String sSize;
		if(n.size<=8)
			sSize =  "8_t";

		else if(n.size<=16)
			sSize = "16_t";

		else if(n.size<=32)
			sSize =  "32_t";

		else //if(n.size<=64)
			sSize =  "64_t";
		sSize = "";

		/*if(isGlobal){
			if((n.size % 8)==0){
				cCode = cCode +"int" + sSize;
				return null;
			}
			else{
				cCode = cCode + "struct { int"+ sSize + " oddbit:"+n.size +";}";
				return (String)"oddbit";
			}
		}
		else*/
		cCode = cCode +"int"+sSize;
		return null;
	}

	@Override
	public Object visit(Lable n, Object e) {

		cCode = cCode +("\n"+n.s+":\n");
		return null;
	}

	// Exp e1,e2;
	public Object visit(LeftShift n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" << ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}

	public Object visit(LessOrEqual n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" <= ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}

	// Exp e1,e2;
	public Object visit(LessThan n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" < ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}

	@Override
	public Object visit(ListAssign n, Object env) {		
		cCode = cCode + indent;
	    if(n.e instanceof ListComprehension && n.i instanceof IdentifierExp){		
			n.e.accept(this,((IdentifierExp)n.i).s);		
		}
		else{
			//if(n.isListCopy)
				cCode = cCode +"\n//list copy is "+ n.isListCopy +"\n";
		n.i.accept(this, env);
		cCode = cCode + " = ";
			n.e.accept(this, env);
		cCode = cCode + ";";   
		if(n.len != null)
			cCode = cCode + "// for loop;";   

		}
		return null;
	}

	public Object visit(ListCompGen n, Object env){

		//		if(n.fils!=null)
		//			cCode = cCode +  "\n "+ comint +"//  ListCompGen colExp " + (n.colExp!=null) +" fils "+ n.fils.size()+"\n";
		//		else
		//			cCode = cCode +  "\n "+ comint +"//  ListCompGen colExp " + (n.colExp!=null) +"\n";
		//		comint = comint + "   ";
		//
		//		//cCode = cCode +("ListCompGenFiliter " + n.colExp.toString());
		//		//		if(n.vars!=null)
		//		//			for(Formal f: n.vars){			
		//		//				genFr= f;//
		//		//				//f.accept(this,env);
		//		//			}
		//		//	cCode = cCode +(" n.vars ListCompGenFiliter ");
		//		String var="";
		//		if(n.colExp!=null){
		//			cCode = cCode + "\n "+ comint +"//  n.colExp " + n.colExp.toString() +"\n";
		//			if(n.colExp instanceof GenIntegers){
		//				PrintIntegrs((GenIntegers)n.colExp,n.fils,n.vars,env);
		//				var=(String)n.colExp.accept(this,n.vars);
		//			}
		//			else
		//				n.colExp.accept(this, env);
		//			}
		//		else
		//		{
		//			if(n.fils != null)
		//			{
		//				cCode = cCode + "\n "+ comint +"// nn.fils ListCompGenFiliter "+n.fils.size()+"\n";			
		//				if(n.fils.size()>0)
		//					for(Exp ex: n.fils)
		//						ex.accept(this,n.vars);
		//				//		ExpList es=(ExpList)(env);
		//				//		if(es!=null)
		//				//		for(Exp e:es){
		//				//			e.accept(this, null);
		//				//		}
		//			}
		//			if(n.vars!=null){
		//				cCode = cCode + "\n "+ comint +"//n.vars \n";
		//				for(Formal f:n.vars)
		//
		//					f.accept(this, env);
		//				cCode = cCode + comint;
		//			}
		//		}
		//		//	cCode = cCode +(" \nn.fils ListCompGenFiliter");
		//
		//		cCode = cCode +"end--------\n n.colExp ListCompGenFiliter";
		//
		//
		//
		//


		String var="";
		if(n.colExp!=null)
			if(n.colExp instanceof GenIntegers)
				var=(String)n.colExp.accept(this,n.vars);
			else
				n.colExp.accept(this, env);
		//	cCode = cCode +("\n n.colExp ListCompGenFiliter");
		if(n.fils != null)
			for(Exp ex: n.fils)
				ex.accept(this,n.vars);



		return var;

	}




	public Object visit(ListComprehension n, Object env){
		//		cCode = cCode + "\n "+ comint +"// ListComprehension \n";
		//		comint = comint + "   ";
		//		String var=(String) env;
		//		String ss=instName;/* + " " +(String)env ;
		//
		//		//TODO LIST COMPH
		//		if((String)env !=null)
		//			System.out.print("\n"+ss)*/;
		//			if(n.eles!=null && n.compGen!=null){
		//				cCode = cCode +  "\n "+ comint +"//  Elements "+n.eles.size()+" " + (n.compGen.size())+"\n";
		//
		//
		//				for(int icg=0;icg<n.compGen.size();icg++){
		//					//					cCode = cCode +  var+"_cg_"+icg;
		//					//if(n.compGen.get(icg) instanceof GenInteger)
		//					cCode = cCode +"\n  copGen "+ n.compGen.get(icg);
		//							n.compGen.get(icg).accept(this,  var+"_cg_"+icg);
		//				}
		//
		//				for(Exp e: n.eles){
		//					if(! (e instanceof ListComprehension)){
		//
		//						cCode = cCode +"\n  ele  " +e.toString();
		//							e.accept(this, null);
		//
		//					}
		//
		//				}
		//			}
		//			else if(n.eles!=null)
		//				if(n.eles.size()>0){
		//					cCode = cCode +  "\n "+ comint +"//  Elements "+n.eles.size()+" " + (n.compGen!=null)+"\n";
		//					cCode = cCode + "int "+var+ "_temp"+" [] ={";
		//					for(int ie=0;ie<n.eles.size();ie++){
		//
		//						//if(! (n.eles.get(ie) instanceof ListComprehension))
		//						{
		//							n.eles.get(ie).accept(this, null);
		//							if(ie<n.eles.size()-1)
		//								cCode = cCode + ", ";
		//							else
		//								cCode = cCode +"};\n ";
		//						}
		//
		//					}
		//					cCode = cCode + "  assignarray("+var+", "+var+"_temp, "+n.eles.size()+");";
		//				}
		//
		//
		//			//		 if(var!=null)
		//			//	    	cCode = cCode + var + " = ";
		//
		//
		//			//			cCode = cCode +  "\n DisplayListComprehension \n";
		//			//
		//			//		
		DisplayListComprehension(n,env);

		return null;
	}
	public Object visit(ListType n, Object env) {


		ArrayList<Type> ts= new ArrayList<Type>();

		Type t=n;
		if(n.t!=null)
			if(n.t instanceof ListType){
				while(((ListType)t).t instanceof ListType){
					ts.add(t);
					t=((ListType)t).t;
				}
				ts.add(t);

				if(ts.size()>0){
					//ts.get(ts.size()-1).accept(this, env);
					((ListType)ts.get(ts.size()-1)).t.accept(this, env);
					cCode = cCode +" " +env;

					for(int it=0;it<ts.size();it++){
						if(ts.get(it) instanceof ListType){				
							cCode = cCode +"[";
							((ListType)ts.get(it)).len.accept(this, env);			
							cCode = cCode +("]");

						}
						else
							n.t.accept(this, env);

					}
				}
			}
			else {
				n.t.accept(this, env);

				cCode = cCode +" "+ env+" [";
				n.len.accept(this, env);
				cCode = cCode +("]");
				return null;
			}


		//cCode = cCode +("List\n");



		//		if(n.t instanceof ListType)


		/*	cCode = cCode +("List");
		if(n.mval != null){
			Map<String, Exp> mv = n.mval;
			for(Map.Entry<String, Exp> entry : mv.entrySet()){
				cCode = cCode +("("+entry.getKey() + "=");
				entry.getValue().accept(this,env);				
				cCode = cCode +(")");
			}
			}
		if(n.mtype != null){
			Map<String, Type> mt = n.mtype;
			for(Map.Entry<String, Type> entry : mt.entrySet()){
				cCode = cCode +("("+entry.getKey() + "=");
				entry.getValue().accept(this,env);				
				cCode = cCode +(")");
			}
			}*/
		return null;

	}



	// Exp e1,e2;
	public Object visit(Minus n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" - ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}


	public Object visit(Negate n, Object env) {
		cCode = cCode +("( - ");
		n.e.accept(this,env);
		cCode = cCode +(") ");

		return null;}
	// Exp e;
	public Object visit(NewArray n, Object env) {
		cCode = cCode +("new int [");
		n.e.accept(this,env);
		cCode = cCode +("]");
		return null;}

		// Exp e;
	public Object visit(Not n, Object env) {
		cCode = cCode +("(! ");
		n.e.accept(this,env);
		cCode = cCode +(")");
		return null;}
	public Object visit(NotEqual n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" != ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}
	// Exp e1,e2;
	public Object visit(Or n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" || ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}


	// Exp e1,e2;
	public Object visit(Plus n, Object env) {
		cCode = cCode +( "(");
		n.e1.accept(this,env);
		cCode = cCode +(" + ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}


	public Object visit(PortHH n, Object env){

		ArrayList<String> portName= new ArrayList<String>();

		if(inputCons!=null)
			if(inputCons.get(instName+"#"+ n.i.s)!=null)
				portName.addAll(inputCons.get(instName+"#"+ n.i.s));
		if(outputCons!=null && portName.size()==0)
			if(outputCons.get(instName+"#"+ n.i.s)!=null)
				portName.addAll(outputCons.get(instName+"#"+ n.i.s));
		if(portName.size()==0)
			portName.add(n.i.s);
		if(printPort)
			cCode = cCode + portName.get(0);
		return portName;
	}
	// Exp e;
	public Object visit(Print n, Object env) {
		if(! hsInclude.contains("stdio.h")){
			hsInclude.add("stdio.h");
			sInclude = sInclude + "#include <stdio.h>\n";
		}
		ExpList prExs= new ExpList();
		cCode = cCode +("printf(\"");	
		String sPrint="";
		for(int i=0; i<n.es.size();i++){
			if(n.es.get(i) instanceof StringLiteral)
				sPrint = sPrint + ((StringLiteral)n.es.get(i)).s;
			else{// if(n.es.get(i) instanceof IntegerLiteral){
				sPrint = sPrint + "%d";
				prExs.add(n.es.get(i));
			}
			//			else if(n.es.get(i) instanceof BoolLiteral){
			//				cCode=cCode + "%d";
			//				prExs.add(n.es.get(i));
			//			}

			if(i<n.es.size()-1)
				cCode = cCode + sPrint;
			else
				if(n.isln)
					cCode = cCode +sPrint+"\\n\"";
				else
					cCode = cCode +sPrint+"\"";			
		}
		for(Exp ex:prExs){
			cCode = cCode +",";
			ex.accept(this, env);
		}
		cCode = cCode +");";

		return null;}
	public Object visit(ProcedureDecl n, Object env) {
		indent = "   ";
		String old = indent;
		cCode = cCode +"\nstatic ";
		if(n.t!=null)
			n.t.accept(this,env);
		else 
			cCode = cCode +("void");
		cCode = cCode +(" ");
		n.i.accept(this,env);
		cCode = cCode +(" (");
		if(n.fl!=null)
			for ( int i = 0; i < n.fl.size(); i++ ) {
				n.fl.get(i).accept(this,env);
				if (i+1 < n.fl.size()) { cCode = cCode +(", "); }
			}
		cCode = cCode +(") { \n");
		indent = indent + "  ";	
		if(n.i.s.equals("idct1d"))
			System.out.print(cCode);

		if(n.vl!=null)
			for ( int i = 0; i < n.vl.size(); i++ ) {
				cCode = cCode +(indent);
				n.vl.get(i).accept(this,env);
				cCode = cCode +("\n");
			}
		if(n.sl!=null)
			n.sl.accept(this,env);
		if(n.e!=null){
			cCode = cCode +("\n" + indent + "return ");		
			n.e.accept(this,env);
			indent = old;
			cCode = cCode +(";");
		}
		cCode = cCode +("\n }\n");
		return null;}
	public Object visit(ReadToken n, Object env){
		ArrayList<String> conns = new ArrayList<String>();
		if(n.rep instanceof IntegerLiteral)
			if(((IntegerLiteral)n.rep).i>1){
				String index=n.p.i.s+"_ix";
				cCode = cCode + "\nint "+index+" =0; \nfor(; " + index+ "<"+((IntegerLiteral)n.rep).i+"; " +index +"++)";
				n.ids.get(0).accept(this,env);
				cCode = cCode +"["+index+"] = ";
				cCode = cCode +("ReadToken(&");
				conns.addAll((ArrayList<String>)n.p.accept(this,env));
				cCode = cCode +conns.get(0)+ ", 1);\n";
				return null;					
			}
		for(Identifier i: n.ids){
			//			cCode = cCode + "int ";
			i.accept(this,env);
			cCode = cCode +(" = ");
			cCode = cCode +("ReadToken(&");
			conns.addAll((ArrayList<String>)n.p.accept(this,env));
			cCode = cCode +conns.get(0)+ ", 1);\n";
			//			cCode = cCode +(", ");
			//			n.rep.accept(this, env);
			//			cCode = cCode +(");");

		}    	
		return null;
	}
	// Exp e1,e2;
	public Object visit(RightShift n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" >> ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}
	public Object visit(SendToken n, Object env){

		//		if(duplicatVar){
		//			for(int i=0; i< n.e.size(); i++) {
		//				if(n.olds.get(i)==true){
		//					oldval=true;
		//					cCode = cCode +(indent); 
		//					n.e.get(i).accept(this,env);
		//					cCode = cCode +(" = ");
		//					oldval=false;
		//					n.e.get(i).accept(this,env);
		//					cCode = cCode +(";\n");
		//				}
		//			}    	
		//
		//		}
		//		else
		ArrayList<String> conns= new ArrayList<String>();

		if(n.rep instanceof IntegerLiteral){
			if(((IntegerLiteral)n.rep).i != 1){
				String index=n.p.i.s +"_ix";
				cCode = cCode + "\nint "+index+" =0; \nfor(; " + index+ "<"+((IntegerLiteral)n.rep).i+"; " +index +"++){\n";
				conns.addAll((ArrayList<String>)n.p.accept(this,env));
				for(String conn: conns){
					cCode = cCode +"      SendToken(&"; 
					cCode = cCode +conn+ ", ";
					n.e.get(0).accept(this,env);
					
					cCode = cCode +"["+index+"], 1);\n";
				//	cCode = cCode +"fprintf("+typeName+",\" %d \","+((IdentifierExp)(n.e.get(0))).s+"["+index+"]);\n";
				}
				cCode = cCode +"}\n";

			}
			else{
				conns.addAll((ArrayList<String>)n.p.accept(this,env));
				for(int i=0; i< n.e.size(); i++) {
					if(conns.size()>1)
					{	
							String tmpvar="tmpvar_" + (tmpInt++) + "_"+ n.p.i.s;
						cCode = cCode + "int "+ tmpvar + " = ";						
						n.e.get(i).accept(this,env);
						cCode = cCode +";\n";
					//	cCode = cCode +"fprintf("+typeName+",\" %d \","+tmpvar+");\n";


						for(String conn: conns){
							cCode = cCode +"SendToken(&"; 
							cCode = cCode +conn+ ", " + tmpvar;
						//	n.e.get(i).accept(this,env);

							cCode = cCode +(", ");
							n.rep.accept(this, env);
							cCode = cCode +(");\n");
						}
					}
					else
					{/*
					String tmpvar="tmpvar_" + (tmpInt++) + "_"+ n.p.i.s;
					cCode = cCode + "int "+ tmpvar + " = ";						
					n.e.get(i).accept(this,env);
					cCode = cCode +";\n";*/
				//	cCode = cCode +"fprintf("+typeName+",\" %d \","+tmpvar+");\n";

						cCode = cCode +"SendToken(&"; 
						cCode = cCode +conns.get(0)+ ", " ;//
						n.e.get(i).accept(this,env);
						cCode = cCode +(", ");
						n.rep.accept(this, env);
						cCode = cCode +(");\n");

					}
				}    	
			}
		}
		else {
			String index=n.p.i.s +"_ix";
			cCode = cCode + "\nint "+index+" =0; \nfor(; " + index+ "<";
					n.rep.accept(this, env);
			cCode = cCode + "; " +index +"++){\n";
			conns.addAll((ArrayList<String>)n.p.accept(this,env));
			for(String conn: conns){
				cCode = cCode +"      SendToken(&"; 
				cCode = cCode +conn+ ", ";
				n.e.get(0).accept(this,env);
				cCode = cCode +"["+index+"], 0);\n";
			//	cCode = cCode +"fprintf("+typeName+",\" %d \","+((IdentifierExp)(n.e.get(0))).s+"["+index+"]);\n";

			}
			cCode = cCode +"}\n";


		}

		//		cCode = cCode + "\nprintf(\"\\n"+instName+ "    %d \"," ;
		//		n.e.get(0).accept(this,env);
		//		cCode = cCode + ");";
		return null;
	}

	public Object visit(SimpEntityExpr n, Object env) {
		n.i.accept(this, env);
		cCode = cCode +(" ( ");
		for(VarDecl vr : n.vl)
			vr.accept(this, env);
		cCode = cCode +(");\n");
		return null;
	}

	public Object visit(StatementCall n, Object env) {

		if (((IdentifierExp)n.i).s.equals("wait"))
			cCode = cCode + "return 0;";
		else{
			n.i.accept(this,env);
			cCode = cCode +("(");
			for ( int i = 0; i < n.el.size(); i++ ) {
				if(n.el.get(i) instanceof PortHH)
					break;
				n.el.get(i).accept(this,env);
				if ( i+1 < n.el.size() ) { cCode = cCode +", "; }
			}
			cCode = cCode +(");");
		}
		return null;}

	public Object visit(StringLiteral n, Object env) {
		cCode = cCode +"\""+n.s+"\"";
		return null;}

	
	public Object visit(SwitchCase n, Object env) {
		cCode = cCode + "\n switch (";
		n.exCase.accept(this, env);
		cCode = cCode +") {\n";
		for(int i=0;i<n.sts.size();i++){
			cCode = cCode + "   case ";
			n.cases.get(i).accept(this, env);
			cCode = cCode +":\n     ";
			n.sts.get(i).accept(this, env);
			//cCode = cCode +"     break;\n";
		}
		cCode = cCode +"}\n";

		return null;
	}


	// Exp e1,e2;
	public Object visit(Times n, Object env) {
		cCode = cCode +("(");
		n.e1.accept(this,env);
		cCode = cCode +(" * ");
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}

	public Object visit(NullType n, Object env) {
		cCode = cCode +("void");

		return null;}



	public Object visit(VarDeclComp n, Object env) {
		if(env instanceof String)
			cCode = cCode + (String)env;

		if(n.t != null)
			n.t.accept(this,n.i.s);
		else
			cCode = cCode +(indent + "int");


		if(n.es.size()>0){
			cCode = cCode + " = {";   
			if (!isGlobal)
				for(int i =0; i<n.es.size()-1;i++){
					n.es.get(i).accept(this, env);
					cCode = cCode + ",";    		   
				}
			n.es.get(n.es.size()-1).accept(this, env);
			cCode = cCode + "};";
			return null;

		}





		//	n.i.accept(this,env);


		if(n.e == null )
			cCode = cCode +(";\n");
		else {
			String sels =getElemen(n.e);
			if (!isGlobal){
				if(n.e instanceof ListComprehension || n.e instanceof ListConc){
					cCode = cCode + ";\n";
					n.e.accept(this,n.i.s);
				}
				else{
					cCode = cCode +" = ";
					n.e.accept(this,n.i.s);
					cCode = cCode +(";\n");
				}

			}
			else if(sels!=null)
				cCode = cCode +" =  {"+ sels+"};\n";
			else
			{
				cCode = cCode +(";\n");
				globId.add(n.i);
				globExp.add(n.e);
				//			n.e.accept(this,env);

			}
		}


		return null;
	}

	private String getElemen(Exp e) {
		String sels="";
		if(e instanceof ListComprehension){
			if(((ListComprehension) e).compGen!=null)
				if(((ListComprehension) e).compGen.size()>0)
					return null;


			int elSize =((ListComprehension) e).eles.size();
			for(int iex=0;iex <elSize;iex++){
				String com = ", ";
				if(iex== elSize-1)
					com ="";
				Exp elm = ((ListComprehension) e).eles.get(iex);
				if(elm instanceof IntegerLiteral)
					sels += ((IntegerLiteral) elm).i + com;
				else if(elm instanceof BooleanLiteral)
					sels += ((BooleanLiteral) elm).value + com;
				else if(elm instanceof Negate){
					if(((Negate) elm).e instanceof IntegerLiteral)
						sels += "(-"+((IntegerLiteral)((Negate) elm).e).i + ")" +com;
					else 
						return null;

				}
				else
					return null;

			}
			return sels;
		}



		return null;
	}


	public Object visit(VarDeclSimp n, Object env) {


		if(isGlobal &&  !n.isAssignable && n.e!=null){
			cCode = cCode + "#define "+n.i.s+" (";
			n.e.accept(this, env);
			cCode = cCode + ") ";

		} else {
			if(env instanceof String)
				cCode = cCode + (String)env;
			String oddbit=null;
			if(n.t != null){
				if(n.t instanceof IntegerType)
					if(isGlobal)
						oddbit = (String) n.t.accept(this,env);
					else
						n.t.accept(this,env);
				else
					n.t.accept(this,env);
			}

			else if(n.e!=null){
				if (n.e instanceof BooleanLiteral)
					cCode = cCode +(indent + "int");
				//	cCode = cCode +(indent + "boolean");
				else if (n.e instanceof IntegerLiteral)
					cCode = cCode +(indent + "int"); 
				else
					cCode = cCode +(indent + "int"); 
			}

			else
				cCode =cCode + "int";

			if(oddbit!=null)
				if(oddbit.equals("oddbit")){
					cCode = cCode +" Str_"+ n.i.s + ";\n";
					cCode = cCode +"#define " + n.i.s +" Str_" + n.i.s + ".oddbit";
				}
				else { 
					cCode = cCode +" ";
					n.i.accept(this,env);
				}
			else { 
				cCode = cCode +" ";
				n.i.accept(this,env);
			}

			if(n.e == null ){
				if(oddbit==null)
					cCode = cCode +(";");
			}
			else if ((n.e instanceof BooleanLiteral || n.e instanceof IntegerLiteral || !isGlobal) && oddbit==null){
				cCode = cCode +(" = ");
				n.e.accept(this,env);
				cCode = cCode +(";");
			}
			else{
				if(oddbit==null)
					cCode = cCode +(";");
				globId.add(n.i);
				globExp.add(n.e);
				//		n.e.accept(this,env);
			}
		}	

		return null;
	}



	// Exp e;
	// Statement s;
	public Object visit(While n, Object env) {
		String old=indent;
		indent = indent + "  ";
		cCode = cCode +("while (");
		n.e.accept(this,env);
		cCode = cCode +(") {");
		n.s.accept(this,env);
		cCode = cCode + "}\n";
		indent = old;
		return null;}


	@Override
	public Object visit(CharLiteral n, Object env) {
		cCode = cCode + n.c;
		return null;
	}


	@Override
	public Object visit(ListConc n, Object env) {

		cCode = cCode + "\n// ListConc \n";
		n.e1.accept(this, "temp");
		cCode = cCode + "\n// =  ListConc \n";

		n.e2.accept(this, env);

		return null;
	}


	@Override
	public Object visit(StringConc n, Object env) {
		return null;
	}


	@Override
	public Object visit(For n, Object env) {
		String old=indent;
		indent = indent + "  ";

		cCode = cCode + "int "+n.i.s+" = ";
		n.init.accept(this, env);
		cCode = cCode + ";\n  ";

		cCode = cCode + "for(; ";
		n.con.accept(this, env);
		cCode = cCode +"; "+n.i.s+ " = ";
		n.step.accept(this, env);
		cCode = cCode +"){\n";
		n.s.accept(this, env);
		cCode = cCode + "}\n";
		indent = old;
		return n;
	}


	@Override
	public Object visit(FloatType n, Object env) {
		cCode = cCode + "float";
		return null;
	}


	@Override
	public Object visit(ForEach n, Object env) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object visit(Break break1, Object env) {
		cCode = cCode +"     break;\n";
		return null;
	}


	@Override
	public Object visit(TestInputPort n, Object env) {
		List<String> conns= new ArrayList<>();
		conns.addAll((List<String>)n.p.accept(this,env));
		cCode = cCode +"TestInputPort(&"+conns.get(0)+" , ";
		n.available_tokens.accept(this, env);

		cCode = cCode+ ")";
		if(conns.size()>1){
			for(int i=1; i<conns.size();i++){
				cCode=cCode +" && \n\tTestInputPort(&"+conns.get(i)+" , ";
				n.available_tokens.accept(this, env);
				cCode = cCode + ")";
			}
		}

		return null;
	}


	@Override
	public Object visit(TestOutputPort n, Object env) {
		List<String> conns= new ArrayList<>();
		conns.addAll((List<String>)n.p.accept(this,env));
		cCode = cCode +"TestOutputPort(&"+conns.get(0)+" , ";
		n.available_room.accept(this, env);
		cCode = cCode + ")";

		/*	if(conns.size()>3){
				for(int i=1; i<conns.size();i++){
					cCode=cCode +" && \n\t(NumberOfElement(&"+conns.get(0)+") == NumberOfElement(&"+conns.get(i)+"))";				
				}
			}


		else */if(conns.size()>1){
			for(int i=1; i<conns.size();i++){
				cCode=cCode +" && \n\tTestOutputPort(&"+conns.get(i)+" , ";
				n.available_room.accept(this, env);
				cCode = cCode + ")";
			}
		}

		return null;
	}


	@Override
	public Object visit(MultipleOps n, Object env) {
		int i=0;
		for(; i<n.Operations.size();i++){
			n.Operands.get(i).accept(this, env);
			String op=n.Operations.get(i);
			if(op.length()<=2){
				if(op.equals("or"))
					cCode = cCode + " " + "||"  + " ";
				else if(op.equals("="))
					cCode = cCode + " " + "=="  + " ";
				else
					cCode = cCode + " " + n.Operations.get(i) + " ";
			}
			else{
				 if(op.equals("and"))
					cCode = cCode + " " + "&&"  + " ";
				else if(op.equals("bitor"))
					cCode = cCode + " " + "|"  + " ";
				else if(op.equals("bitand"))
					cCode = cCode + " " + "&"  + " ";
				else
					System.out.print(op);
				}
		
				
		}
		n.Operands.get(i).accept(this, env);		
		return null;
	}


	@Override
	public Object visit(ExpList n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}




	@Override
	public Object visit(ActionFiringCond n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object visit(ActorFSM n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object visit(VectorOps n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object visit(MatrixOps n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object visit(Profile_Actor n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object visit(Profile_ActionDecl n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object visit(Profile_ActionScheduler n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object visit(Profile_FunctionDecl n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object visit(Profile_ProcedureDecl n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}



}
