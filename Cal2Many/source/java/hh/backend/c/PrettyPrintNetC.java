package hh.backend.c;

import hh.AST.syntaxtree.*;
import hh.common.translator.VisitorActor;
import hh.common.translator.VisitorExp;
import hh.common.translator.VisitorStm;
import hh.common.translator.NetVisitor;
import hh.common.translator.VisitorType;
import hh.simplenet.*;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




public class PrettyPrintNetC implements
NetVisitor<Object,Object>,VisitorActor<Object,Object>, VisitorType<Object,Object>, VisitorStm<Object,Object>, VisitorExp<Object,Object>
{

	private String indent = "";
	private boolean duplicatVar,oldval,isGlobal;
	public ExpList globExp= new ExpList();
	public IdentifierList globId= new IdentifierList(); 
	public Formal genFr;
	public ArrayList<String> actorNames = new ArrayList<String>(); 
	public ArrayList<String> actorTypes = new ArrayList<String>(); 
	public String cCode ="";
	public ArrayList<Channel> cInput = new ArrayList<Channel>();
	public ArrayList<Channel> cOutput = new ArrayList<Channel>();

	private void DisplayListComprehension(ListComprehension n,	Object env) {
		// TODO Auto-generated method stub
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
				System.out.println(iels);				
				exEls.add(exs.get(iels));
				exseles--;
			}
			}

		}
		if(cmps.size()>1){
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
		

		cCode = cCode + var;
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
		if(exEls.size()>0 && n.compGen==null){
							for(int i=0; i<exEls.size();i++){
								cCode = cCode +"  "+var+"["+i+"] = ";
								exEls.get(i).accept(this,env);
								cCode = cCode +";\n";
			
							}
							}

	}

	// Type t;
	// Identifier i;

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
		
		return null;
		}

	@Override
	public Object visit(ActionScheduler n, Object e) {
		// TODO Auto-generated method stub
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
		return null;}


	// Identifier i;
	// Exp e;
	public Object visit(Assign n, Object env) {
		n.i.accept(this,env);
		cCode = cCode +(" = ");
		if(n.e==null)
			System.out.print(cCode);
		n.e.accept(this,env);
		cCode = cCode +(";");
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
		cCode = cCode+ "(~(";
		n.e1.accept(this, env);
		cCode = cCode +"&";
		n.e2.accept(this, env);
		cCode = cCode+ ") | (~";
		n.e1.accept(this, env);
		cCode = cCode+ " & ~";
		n.e2.accept(this, env);
		cCode = cCode+ "))";		return null;
	}

	// StatementList sl;
	public Object visit(Block n, Object env) {
		cCode = cCode +("{ \n");
		String old = indent;
		indent = indent + "  ";
		for ( int i = 0; i < n.sl.size(); i++ ) {
			cCode = cCode +(indent);
			n.sl.get(i).accept(this,env);
			cCode = cCode +("\n");
		}
		indent = old;
		cCode = cCode +(indent+"} ");
		return null;}

	public Object visit(BooleanType n, Object env) {
		cCode = cCode +("boolean");
		return null;}

	public Object visit(BooleanLiteral n, Object env) {
		if(n.value){
			cCode = cCode +( "true");
		}
		else{
			cCode = cCode +( "true");
		}
		return null;}

	// Exp e;
	// identifier i;
	// ExpList el;
	public Object visit(Call n, Object env) {
	
		return null;}

	public Object visit(Channel n, Object env) {
        int isTopPort=0;
	/*	 
		if(n.p1!=null)
			if((boolean)n.p1.accept(this, env))
				isTopPort=1;
		cCode = cCode +("_");
		if(n.p2!=null)
			if((boolean)n.p2.accept(this, env))
				isTopPort=2;
		
*/
		return isTopPort;
	}

	public Object visit(ConsumeToken n, Object env){
		for(Formal i: n.is){
			cCode = cCode +(indent);
			i.accept(this,env);
			cCode = cCode +(" = ");
			cCode = cCode +("ConsumeToken(&");
			n.p.accept(this,env);
			cCode = cCode +(", ");
			n.rep.accept(this, env);
			cCode = cCode +(");\n");

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
	public Object visit(ElseIf n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}
	public Object visit(Entity n, Object env) {

		cCode= cCode +" // Params for actor ";		
		n.it.accept(this, env);

		cCode= cCode +" instance  ";	

		n.i.accept(this, env);	
		cCode= cCode +"\n int ";	
		n.i.accept(this, env);
		cCode= cCode +"_State=0;\n";
//		for(VarDecl v : n.parms){
//			v.accept(this, n);
//		}
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
		// TODO Auto-generated method stub
		//cCode = cCode +("[");

		n.structure.accept(this, env);
//		for(Exp ex: n.location)
		{
			cCode = cCode +("[");
			n.location.accept(this, env);
			cCode = cCode +("]");

		}

		return null;
	}

	public Object visit(FlatNetwork n, Object env) {
		Integer chanType=0;
		cCode= cCode + "// ********* Generate Code form flatten NL ";
		n.it.accept(this, env);
		cCode= cCode +".c *********/\n";
		for(Entity ent : n.entities){
			cCode= cCode + "\n#include \""+ent.i.s+".h\"";
			actorNames.add(ent.i.s);
			actorTypes.add(ent.it.s);
			
		}

		
		cCode= cCode + "\n#include \"include\\calChannel.h\"\n#include <stdio.h>";

		if( !(n.fls.isEmpty())){			
			for ( int i = 0; i < n.fls.size(); i++ ) {
				n.fls.get(i).accept(this,env);
				if (i+1 < n.fls.size()) { cCode = cCode +(", "); }
			}
		}

		cCode= cCode +"\n//Input Ports ";		
		for(PortHH in: n.input){
			in.accept(this, env);
		}
		cCode= cCode +"\n//Output Ports";	

		for(PortHH out: n.output){
			out.accept(this, env);
		}
		for(VarDecl vr: n.vl){
			vr.accept(this, env);
		}

		for(Entity ent : n.entities){
		//	ent.accept(this, env);
		}
		int chNo=0;
		for(Channel ch : n.chs){
			cCode = cCode + "\nfifo ch" + chNo++;
			chanType=(Integer)ch.accept(this, env);
			cCode = cCode + ";";
			if(chanType == (Integer)1)
				cInput.add(ch);
			else if(chanType == (Integer)2)
				cOutput.add(ch);			
		}
		
		for(Entity ent : n.entities){					
		//	cCode = cCode + "FILE *"+ent.it.s +";\n";			
		}

		cCode= cCode +"\nint main(){\n   ";
		
		for(Entity ent : n.entities){					
			//cCode = cCode + ent.it.s +" = fopen(\"C:\\\\KalSi\\\\mpeg4c\\\\C_Schedule\\\\" + ent.it.s+".txt\", \"w\");\n";			
		}
		chNo=0;
		for(Channel ch : n.chs){
			cCode = cCode + "\ninit_fifo(&ch" + chNo;
			chanType=(Integer)ch.accept(this, env);
			cCode = cCode + ");";
			cCode = cCode + " // printf(\"" + chNo++;
			chanType=(Integer)ch.accept(this, env);
			cCode = cCode + "\\n\");";
		}
		int ich=0;
		for(Channel inCh: cInput){
			cCode= cCode +"	 fromFileToQueue(\"in"+ ++ich +".txt\",&";
			inCh.accept(this, env);
			cCode= cCode +");\n";
		}
		cCode= cCode +"    int run=0;\n    int no_run = 4096, run =0;\n 	while(run <=no_run){\n";

		for(Entity ent : n.entities){					
			cCode= cCode +"	    Scheduler_"+ent.i.s + "();\n";
			
		}
		for(Entity ent : n.entities){					
		//	cCode = cCode + "fclose("+ent.it.s +");\n";			
		}

		chNo=0;
		cCode= cCode +"	    run++;\n    }\n";
		for(Channel inCh: cOutput){
			cCode= cCode +"	 print_fifo(&ch" + chNo++;
			inCh.accept(this, env);
			cCode= cCode +");\n";
		}
		//PrintWriter writerActorh = new PrintWriter("C:\\NewBoard\\actors.h");
		String actorsH="#ifndef __ACTORS_H__\n#define __ACTORS_H__";
		for(String acs: actorNames)
			actorsH = actorsH + "\n#include \""+acs+".h\"";
		//writerActorh.println(actorsH);					
		//writerActorh.close();

		actorsH = actorsH + "typedef struct{\n";
		for(int i=0; i<actorNames.size();i++)
			actorsH = actorsH + "\t" + actorTypes.get(i)+"*\t" +actorNames.get(i)+";\n";
		actorsH = actorsH + "}actors_t;\n#endif";
			
		
		
		//return  actorsH +"epiphany*/" +cCode +"}";
		return  cCode +"}";
	}

	// Type t;
	// Identifier i;
	public Object visit(Formal n, Object env) {
		if(n.t!=null)
			n.t.accept(this,env);
		else
			cCode = cCode +(indent +"int");
		cCode = cCode +(" ");
		n.i.accept(this,env);
		return null;}

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

		return null;
	}

	public Object visit(FunctionDecl n, Object env) {
	
		return null;}

	@Override
	public Object visit(GenIntegers n, Object env) {
		FormalList genFrs= new FormalList();
		String var="";
		if(env instanceof FormalList)
			genFrs=(FormalList)env;
		if (genFrs.size()>0){
			for(Formal genFr: genFrs){
				cCode = cCode +("for(");
				genFr.accept(this, env);
				cCode = cCode +(" = ");
				n.e1.accept(this,env);
				cCode = cCode +("- 1; " + genFr.i.s +"<=");
				n.e2.accept(this,env);
				cCode = cCode +" - 1; "+ genFr.i.s+" ++)\n";
				var=genFr.i.s;
			}
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
	
		return null;}

	// String s;
	public Object visit(Identifier n, Object env) {
		cCode = cCode +(n.s.replace('.','_')); 
		return null;
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
		return null;}

	// String s;
	public Object visit(IdentifierType n, Object env) {
		cCode = cCode +(n.s);
		return null;}

	// Exp e;
	// Statement s1,s2;
	public Object visit(If n, Object env) {
		String old = indent;
		indent = indent + "  ";
		cCode = cCode +("if (");
		n.e.accept(this,env);
		cCode = cCode +(") \n");
		cCode = cCode +(indent);
		n.s1.accept(this,env);
		cCode = cCode +("\n");
		if(n.s2 !=null){
			cCode = cCode +(old + "else \n");
			cCode = cCode +(indent);
			n.s2.accept(this,env);
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
		n.e2.accept(this,env);
		cCode = cCode +(")");
		return null;}

	// int i;
	public Object visit(IntegerLiteral n, Object env) {
		cCode = cCode +(""+ n.i);
		return null;}

	public Object visit(IntegerType n, Object env) {
		cCode = cCode +("int");
		/*if(n.mval != null){
			Map<String, Exp> mval = n.mval;
			for(Map.Entry<String, Exp> entry : mval.entrySet()){
				cCode = cCode +("("+entry.getKey() + "=");
				entry.getValue().accept(this,env);				
				cCode = cCode +(")");
			}
			}*/
		return null;}


	@Override
	public Object visit(Lable n, Object e) {

		cCode = cCode +(n.s+":");
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
		// TODO Auto-generated method stub
		n.i.accept(this,env);
		for(Exp e: n.es){
			cCode = cCode +("[");
			e.accept(this,env);
			cCode = cCode +("] ");
		}
		cCode = cCode +("= ");
		n.e.accept(this,env);
		cCode = cCode +(";");

		return null;
	}

	public Object visit(ListCompGen n, Object env){
		//cCode = cCode +("ListCompGenFiliter " + n.colExp.toString());
		//		if(n.vars!=null)
		//			for(Formal f: n.vars){			
		//				genFr= f;//
		//				//f.accept(this,env);
		//			}
		//	cCode = cCode +(" n.vars ListCompGenFiliter ");
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
		//	cCode = cCode +(" \nn.fils ListCompGenFiliter");





		return var;

	}

	public Object visit(ListComprehension n, Object env){
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
		// Identifier i;
		// Exp e1,e2;

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
	n.i.accept(this,env);
	return null;
}
	// Exp e;
	public Object visit(Print n, Object env) {
		ExpList prExs= new ExpList();
			cCode = cCode +("printf(\"");	

		for(int i=0; i<n.es.size();i++){
			if(n.es.get(i) instanceof StringLiteral)
				n.es.get(i).accept(this,env);
			else{// if(n.es.get(i) instanceof IntegerLiteral){
				cCode=cCode + "%d";
				prExs.add(n.es.get(i));
			}
//			else if(n.es.get(i) instanceof BoolLiteral){
//				cCode=cCode + "%d";
//				prExs.add(n.es.get(i));
//			}
				
			if(i<n.es.size()-1)
				cCode = cCode +"";
			else
				if(n.isln)
					cCode = cCode +"\\n\"";
				else
					cCode = cCode +"\"";			
		}
		for(Exp ex:prExs){
			cCode = cCode +",";
			ex.accept(this, env);
		}
		cCode = cCode +");";
			
		return null;}
	public Object visit(ProcedureDecl n, Object env) {
	
		return null;}
	public Object visit(ReadToken n, Object env){
		for(Formal i: n.is){
			cCode = cCode +(indent);
			i.accept(this,env);
			cCode = cCode +(" = ");
			cCode = cCode +("ReadToken(&");
			n.p.accept(this,env);
			cCode = cCode +(", ");
			n.rep.accept(this, env);
			cCode = cCode +(");\n");

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

		if(duplicatVar){
			for(int i=0; i< n.e.size(); i++) {
				if(n.olds.get(i)==true){
					oldval=true;
					cCode = cCode +(indent); 
					n.e.get(i).accept(this,env);
					cCode = cCode +(" = ");
					oldval=false;
					n.e.get(i).accept(this,env);
					cCode = cCode +(";\n");
				}
			}    	

		}
		else
			for(int i=0; i< n.e.size(); i++) {
				cCode = cCode +(indent+"SendToken(&"); 
				n.p.accept(this,env);
				cCode = cCode +(", ");
				oldval =n.olds.get(i);
				n.e.get(i).accept(this,env);
				cCode = cCode +(", ");
				n.rep.accept(this, env);
				cCode = cCode +(");\n");
			}    	
		return null;
	}
	// MainClass m;
	// ClassDeclList cl;
	public Object visit(SEQ_Actor n, Object env) {
		
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
		n.i.accept(this,env);
		/*cCode = cCode +(".");
		n.i.accept(this,env);*/
		cCode = cCode +("(");
		for ( int i = 0; i < n.el.size(); i++ ) {
			n.el.get(i).accept(this,env);
			if ( i+1 < n.el.size() ) { cCode = cCode +(", "); }
		}
		cCode = cCode +(");");
		return null;}

	public Object visit(StringLiteral n, Object env) {
		cCode = cCode +("\""+n.s+"\"");
		return null;}

	public Object visit(Structure n, Object env) {
		cCode = cCode +("Structure");
		int chno=0;
		for(Channel ch : n.chs){
			cCode = cCode + "ch" + chno++ + "//";
			ch.accept(this, env);
		}
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
		cCode = cCode +("");

		return null;}

	public Object visit(VarDeclComp n, Object env) {

		cCode = cCode +" \n";
//		n.i.accept(this,env);

		if(n.t != null)
			n.t.accept(this,n.i.s);
		else
			cCode = cCode +(indent + "int");
		if(n.e == null )
			cCode = cCode +(";\n");
		else if ((isGlobal	&& (n.e instanceof BooleanLiteral || n.e instanceof IntegerLiteral)) || !isGlobal){
			cCode = cCode +(" = ");
			n.e.accept(this,env);
			cCode = cCode +(";\n");
		}
		else{
			cCode = cCode +(";\n");
			globId.add(n.i);
			globExp.add(n.e);
		}


		return null;
	}

	public Object visit(VarDeclSimp n, Object env) {
//		String entName="";
//		
//		if(env instanceof Entity)
//			entName=((Entity)env).i.s+"_";

		if(n.t != null)
			n.t.accept(this,env);
		else if(n.e!=null){
			if (n.e instanceof BooleanLiteral)
			cCode = cCode +(indent + "boolean");
		else if (n.e instanceof IntegerLiteral)
			cCode = cCode +(indent + "int");
		}
		else
			cCode =cCode + "int";


		cCode = cCode +" ";
		n.i.accept(this,env);
		if(n.e == null )
			cCode = cCode +(";\n");
		else if ((isGlobal	&& (n.e instanceof BooleanLiteral || n.e instanceof IntegerLiteral)) || !isGlobal){
			cCode = cCode +(" = ");
			n.e.accept(this,env);
			cCode = cCode +(";\n");
		}
		else{
			cCode = cCode +(";\n");
			globId.add(n.i);
			globExp.add(n.e);
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
		cCode = cCode +(") ");
		n.s.accept(this,env);
		indent = old;
		return null;}

	@Override
	public Object visit(CharLiteral n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ListConc n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StringConc n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(SwitchCase n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(For n, Object e) {
		// TODO Auto-generated method stub
		return n;
	}

	@Override
	public Object visit(FloatType n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ForEach n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Break break1, Object e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(TestInputPort n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(TestOutputPort n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ExpList n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(MultipleOps n, Object e) {
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
