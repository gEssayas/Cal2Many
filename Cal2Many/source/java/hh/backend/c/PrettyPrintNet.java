package hh.backend.c;

import hh.AST.syntaxtree.*;
import hh.common.translator.VisitorActor;
import hh.common.translator.VisitorExp;
import hh.common.translator.VisitorStm;
import hh.common.translator.NetVisitor;
import hh.common.translator.VisitorType;
import hh.simplenet.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




public class PrettyPrintNet implements 
NetVisitor<Object,Object>,VisitorActor<Object,Object>, VisitorType<Object,Object>, VisitorStm<Object,Object>, VisitorExp<Object,Object>


{

	private String indent = "";
	private boolean duplicatVar,oldval,isGlobal;
    public ExpList globExp= new ExpList();
    public IdentifierList globId= new IdentifierList(); 
    public Formal genFr;
    
    public String netCode ="";
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
					System.out.println(iels);				
					exEls.add(exs.get(iels));
					exseles--;
				}
			}

		}
		//TODO was only > 
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


			netCode = netCode + var;
			for(int is=vars.size()-1;is>=0;is--){
				String sIndex =vars.get(is);
				netCode = netCode +  "[" +sIndex +"]";
			}
			netCode = netCode + " = ";
			if(exEls.size()>0)
				exEls.get(0).accept(this, env);
			else
				netCode = netCode +"0";
			netCode = netCode +";";
		}
		if(exEls.size()>0 && n.compGen==null){
			for(int i=0; i<exEls.size();i++){
				netCode = netCode +"  "+var+"["+i+"] = ";
				exEls.get(i).accept(this,env);
				netCode = netCode +";\n";

			}
		}

	}

	// Type t;
	// Identifier i;

    public void PrintIntegrs(GenIntegers gI, Identifier id, String type, Object env){
		//netCode = netCode +(type + id.s + ";\nfor("+ id.s +"=");
		gI.e1.accept(this, env);
		//netCode = netCode +("; "+id.s +" <=" );
		gI.e2.accept(this, env);
		//netCode = netCode +("; " +id.s +" ++){\n");
		
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
		// Exp e1,e2;
	public Object visit(And n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" && ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}

	// Identifier i;
	// Exp e1,e2;
	public Object visit(FieldAssign n, Object env) {
		n.i.accept(this,env);
		//netCode = netCode +("[");
		n.e1.accept(this,env);
		//netCode = netCode +("] = ");
		n.e2.accept(this,env);
		//netCode = netCode +(";");
		return null;}

	// Exp e;

	// Identifier i;
	// Exp e;
	public Object visit(Assign n, Object env) {
		n.i.accept(this,env);
		//netCode = netCode +(" = ");
		n.e.accept(this,env);
		//netCode = netCode +(";");
		return null;}

	// Exp e1,e2;
	public Object visit(BitAnd n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" & ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}

	// Exp e1,e2;
	public Object visit(BitNot n, Object env) {
		//netCode = netCode +("( ~ ");
		n.e.accept(this,env);
		//netCode = netCode +(")");
		return null;}
	

	// Exp e1,e2;
	public Object visit(BitOr n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" | ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}

	@Override
	public Object visit(BitXOr n, Object env) {
		//netCode = netCode+ "(~(";
		n.e1.accept(this, env);
		//netCode = netCode +"&";
		n.e2.accept(this, env);
		//netCode = netCode+ ") | (~";
		n.e1.accept(this, env);
		//netCode = netCode+ " & ~";
		n.e2.accept(this, env);
		//netCode = netCode+ "))";
		return null;
	}

	// StatementList sl;
	public Object visit(Block n, Object env) {
		//netCode = netCode +("{ \n");
		String old = indent;
		indent = indent + "  ";
		for ( int i = 0; i < n.sl.size(); i++ ) {
			//netCode = netCode +(indent);
			n.sl.get(i).accept(this,env);
			//netCode = netCode +("\n");
		}
		indent = old;
		//netCode = netCode +(indent+"} ");
		return null;}

	public Object visit(BooleanType n, Object env) {
		//netCode = netCode +("boolean");
		return null;}

	public Object visit(BooleanLiteral n, Object env) {
		if(n.value){
			//netCode = netCode +( "true");
			netCode = netCode +( "true");
		}
		else{
			netCode = netCode +( "false");
		}
		return null;}

	// Exp e;
	// identifier i;
	// ExpList el;
	public Object visit(Call n, Object env) {

		return null;}

	public Object visit(Channel n, Object env) {
			if(n.p1!=null)
				n.p1.accept(this, env);   
	        netCode = netCode +(" --> ");
	        if(n.p2!=null)
			n.p2.accept(this, env);
			netCode = netCode +("\n");
			return null;
			}

	public Object visit(ConsumeToken n, Object env){
	
		return null;
	}

	// Exp e1,e2;
	public Object visit(Divide n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" / ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}

	public Object visit(DoubleLiteral n, Object env) {
		//netCode = netCode +(""+n.d);
		return null;}

	@Override
	public Object visit(ElseIf n, Object e) {
		return null;
	}
	public Object visit(Entity n, Object env) {
		
		n.it.accept(this, env);
		netCode = netCode +(" ");
		
	
		n.i.accept(this, env);	
	netCode = netCode +("( ");
		for(VarDecl v : n.parms){
			v.accept(this, env);
			netCode = netCode +(", ");
		}
		netCode = netCode +(" )\n");
			return null;
	}

	public Object visit(EntityPort n, Object env) {
		netCode = netCode +(n.t.s + "  ");
	    if(n.i.s !=null)
	    	netCode = netCode +(n.i.s+".");        
	    netCode = netCode +(n.p.s);
		return null;
		}

	public Object visit(Equal n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" == ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}


	@Override
	public Object visit(ExpIndexer n, Object env) {
		// TODO Auto-generated method stub
		////netCode = netCode +("[");

		n.structure.accept(this, env);
//        for(Exp ex: n.location)
        {
    		//netCode = netCode +("[");
        	n.location.accept(this, env);
    		//netCode = netCode +("]");

        }
		
		return null;
	}

	public Object visit(FlatNetwork n, Object env) {
		netCode = netCode +("Network ");
		n.it.accept(this, env);
		
		netCode = netCode +("(");
		
		if( !(n.fls.isEmpty())){
			
			for ( int i = 0; i < n.fls.size(); i++ ) {
				n.fls.get(i).accept(this,env);
				if (i+1 < n.fls.size()) { netCode = netCode +(", "); }
			}
		}
		netCode = netCode +(")" );
			
		for(PortHH in: n.input){
			in.accept(this, env);
			netCode = netCode +(" ");
			}
		netCode = netCode +(" ==> ");
	
		for(PortHH out: n.output){
			out.accept(this, env);
		netCode = netCode +(" ");
		}
	netCode = netCode +(" : \n \nVar\n\n");
		for(VarDecl vr: n.vl)
			vr.accept(this, env);
		netCode = netCode +("\nEntities\n\n");
	
		for(Entity ent : n.entities)
			ent.accept(this, env);
		netCode = netCode +("\nStruct\n\n");
		for(Channel ch : n.chs)
			ch.accept(this, env);
		
		
	return netCode ;
				}

	// Type t;
	// Identifier i;
	public Object visit(Formal n, Object env) {
		if(n.t!=null)
		n.t.accept(this,env);
		else
			//netCode = netCode +(indent +"int");
		//netCode = netCode +(" ");
		n.i.accept(this,env);
		return null;}

	public Object visit(FunctionCall n, Object env){
	
		return null;
	}

	public Object visit(FunctionDecl n, Object env) {

		return null;}

	@Override
	public Object visit(GenIntegers n, Object env) {
		if (genFr != null){
		//netCode = netCode +("for(");
		genFr.accept(this, env);
		//netCode = netCode +(" = ");
		n.e1.accept(this,env);
		//netCode = netCode +("; " + genFr.i.s +"<=");
		n.e2.accept(this,env);
		//netCode = netCode +("; "+ genFr.i.s+" ++)\n");
		}
		else
		{
			//netCode = netCode +("for(");
			//genFr.accept(this, env);
			//netCode = netCode +(" = ");
			n.e1.accept(this,env);
			//netCode = netCode +("; " + "genFr" +"<=");
			n.e2.accept(this,env);
			//netCode = netCode +("; "+ "genFr"+" ++)\n");
			}
			
		return null;
	}

	@Override
	public Object visit(GoTo n, Object e) {
		//netCode = netCode +("goto " +n.s+";\n");
		return null;
	}

	public Object visit(GreaterOrEqual n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" >= ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}

	public Object visit(GreaterThan n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" > ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
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
		//netCode = netCode +(n.s.replace('.','_')); 
		netCode = netCode +(n.s.replace('.','_')); 
		return null;
	}

	// String s;
	public Object visit(IdentifierExp n, Object env) {
		if(n.s.startsWith("$old$"))
			if(oldval){
				//netCode = netCode +("old_"+n.s.substring(5));
			}
			else{
				//netCode = netCode +(n.s.substring(5));
			}
		else  {		
			//netCode = netCode +(n.s.replace('.','_'));
		}
		return null;}

	// String s;
	public Object visit(IdentifierType n, Object env) {
		//netCode = netCode +(n.s);
		netCode = netCode +(n.s);
		return null;}

	// Exp e;
	// Statement s1,s2;
	public Object visit(If n, Object env) {
		String old = indent;
		indent = indent + "  ";
		//netCode = netCode +("if (");
		n.e.accept(this,env);
		//netCode = netCode +(") \n");
		//netCode = netCode +(indent);
		n.s1.accept(this,env);
		//netCode = netCode +("\n");
		if(n.s2 !=null){
			//netCode = netCode +(old + "else \n");
			//netCode = netCode +(indent);
			n.s2.accept(this,env);
		}
		indent=old;
		return null;}

	// Exp c,e1,e2;
	public Object visit(IfExp n, Object env){
		//netCode = netCode +("(");
		n.c.accept(this,env);
		//netCode = netCode +(" ? ");
		n.e1.accept(this,env);
		//netCode = netCode +(" : ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}

	// int i;
	public Object visit(IntegerLiteral n, Object env) {
		//netCode = netCode +(""+ n.i);
		netCode = netCode +(""+ n.i);
		return null;}

	public Object visit(IntegerType n, Object env) {
		//netCode = netCode +("int");
		/*if(n.mval != null){
			Map<String, Exp> mval = n.mval;
			for(Map.Entry<String, Exp> entry : mval.entrySet()){
				//netCode = netCode +("("+entry.getKey() + "=");
				entry.getValue().accept(this,env);				
				//netCode = netCode +(")");
			}
			}*/
		return null;}


	@Override
	public Object visit(Lable n, Object e) {
		
		//netCode = netCode +(n.s+":");
		return null;
	}

	// Exp e1,e2;
	public Object visit(LeftShift n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" << ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}

	public Object visit(LessOrEqual n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" <= ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}

	// Exp e1,e2;
	public Object visit(LessThan n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" < ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}

	@Override
	public Object visit(ListAssign n, Object env) {
		// TODO Auto-generated method stub
		n.i.accept(this,env);
		for(Exp e: n.es){
		//netCode = netCode +("[");
		e.accept(this,env);
		//netCode = netCode +("] ");
		}
		//netCode = netCode +("= ");
		n.e.accept(this,env);
		//netCode = netCode +(";");

		return null;
	}

	public Object visit(ListCompGen n, Object env){
		String var="";
		if(n.colExp!=null)
			if(n.colExp instanceof GenIntegers)
				var=(String)n.colExp.accept(this,n.vars);
			else
				n.colExp.accept(this, env);
		//	netCode = netCode +("\n n.colExp ListCompGenFiliter");
		if(n.fils != null)
			for(Exp ex: n.fils)
				ex.accept(this,n.vars);

		
		
		return var;
		
		

	}

	public Object visit(ListComprehension n, Object env){
////		//netCode = netCode +("Visit ListComprehension " + n.eles.size());
//		if(n.eles !=null){
//			//netCode = netCode +("{");
//			for(int i=0; i<n.eles.size();i++){
//				
//				n.eles.get(i).accept(this,env);
//				if(i<n.eles.size()-1){
//					//netCode = netCode +(", ");
//				}
//				
//			}
//			//netCode = netCode +("\n};");
//		}
//		if(n.compGen !=null)
//			for(ListCompGen cg : n.compGen)
//				cg.accept(this,env);
//		if(n.eles !=null && n.compGen != null){
//			
//		}		
//	//	//netCode = netCode +("end Visit ListComprehension " + n.genFil.size());
		DisplayListComprehension(n,env);

		return null;
	}




	public Object visit(ListType n, Object env) {
		////netCode = netCode +("List\n");
		if(n.t instanceof ListType)
		{
		n.t.accept(this, env);
		}
		//netCode = netCode +(" [");
		n.len.accept(this, env);
		//netCode = netCode +("]");
		
	/*	//netCode = netCode +("List");
		if(n.mval != null){
			Map<String, Exp> mv = n.mval;
			for(Map.Entry<String, Exp> entry : mv.entrySet()){
				//netCode = netCode +("("+entry.getKey() + "=");
				entry.getValue().accept(this,env);				
				//netCode = netCode +(")");
			}
			}
		if(n.mtype != null){
			Map<String, Type> mt = n.mtype;
			for(Map.Entry<String, Type> entry : mt.entrySet()){
				//netCode = netCode +("("+entry.getKey() + "=");
				entry.getValue().accept(this,env);				
				//netCode = netCode +(")");
			}
			}*/
		return null;}
	// Exp e1,e2;
	public Object visit(Minus n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" - ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}


	
	public Object visit(Negate n, Object env) {
		netCode = netCode +("( - ");
		n.e.accept(this,env);
		netCode = netCode +(") ");

		return null;}
	// Exp e;
	public Object visit(NewArray n, Object env) {
		//netCode = netCode +("new int [");
		n.e.accept(this,env);
		//netCode = netCode +("]");
		return null;}

	// Exp e;
	public Object visit(Not n, Object env) {
		//netCode = netCode +("(! ");
		n.e.accept(this,env);
		//netCode = netCode +(")");
		return null;}
	public Object visit(NotEqual n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" != ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}
	// Exp e1,e2;
	public Object visit(Or n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" || ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}
	

	// Exp e1,e2;
	public Object visit(Plus n, Object env) {
		//netCode = netCode +( "(");
		n.e1.accept(this,env);
		//netCode = netCode +(" + ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}

	
	public Object visit(PortHH n, Object env){
		n.i.accept(this,env);
		return null;
	}
	// Exp e;
	public Object visit(Print n, Object env) {

		return null;}
	public Object visit(ProcedureDecl n, Object env) {

		return null;}
	
	public Object visit(ReadToken n, Object env){
		for(Formal i: n.is){
			//netCode = netCode +(indent);
			i.accept(this,env);
			//netCode = netCode +(" = ");
			//netCode = netCode +("ReadToken(&");
			n.p.accept(this,env);
			//netCode = netCode +(", ");
			n.rep.accept(this, env);
			//netCode = netCode +(");\n");

		}    	
		return null;
	}
	// Exp e1,e2;
	public Object visit(RightShift n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" >> ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}
	public Object visit(SendToken n, Object env){

		if(duplicatVar){
			for(int i=0; i< n.e.size(); i++) {
				if(n.olds.get(i)==true){
					oldval=true;
					//netCode = netCode +(indent); 
					n.e.get(i).accept(this,env);
					//netCode = netCode +(" = ");
					oldval=false;
					n.e.get(i).accept(this,env);
					//netCode = netCode +(";\n");
				}
			}    	

		}
		else
			for(int i=0; i< n.e.size(); i++) {
				//netCode = netCode +(indent+"SendToken(&"); 
				n.p.accept(this,env);
				//netCode = netCode +(", ");
				oldval =n.olds.get(i);
				n.e.get(i).accept(this,env);
				//netCode = netCode +(", ");
				n.rep.accept(this, env);
				//netCode = netCode +(");\n");
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
	netCode = netCode +(" ( ");
	for(VarDecl vr : n.vl)
		vr.accept(this, env);
	netCode = netCode +(");\n");
	return null;
	}

public Object visit(StatementCall n, Object env) {

	return null;}

	public Object visit(StringLiteral n, Object env) {
		//netCode = netCode +("\""+n.s+"\"");
				return null;}

	public Object visit(Structure n, Object env) {
		netCode = netCode +("Structure");
		for(Channel ch : n.chs)
			ch.accept(this, env);
				return null;}


	// Exp e1,e2;
	public Object visit(Times n, Object env) {
		//netCode = netCode +("(");
		n.e1.accept(this,env);
		//netCode = netCode +(" * ");
		n.e2.accept(this,env);
		//netCode = netCode +(")");
		return null;}

	public Object visit(NullType n, Object env) {
			//netCode = netCode +("");
					
				return null;}

	public Object visit(VarDeclComp n, Object env) {
         if(n.t instanceof ListType){
			ListType lt= (ListType)n.t;
			if(!(lt.t instanceof ListType))
			   lt.t.accept(this, env);
		}

		//netCode = netCode +(" \n");
		n.i.accept(this,env);

		if(n.t != null)
			n.t.accept(this,env);
		else
			//netCode = netCode +(indent + "int");
		
		if(n.e == null ){
			//netCode = netCode +(";\n");
		}
		else if ((isGlobal	&& (n.e instanceof BooleanLiteral || n.e instanceof IntegerLiteral)) || !isGlobal){
			//netCode = netCode +(" = ");
			netCode = netCode +(" = ");
				n.e.accept(this,env);
				//netCode = netCode +(";\n");
				}
			else{
				//netCode = netCode +(";\n");
				globId.add(n.i);
				globExp.add(n.e);
			}
		
			
		return null;
	}

	public Object visit(VarDeclSimp n, Object env) {

		if(n.t != null)
			n.t.accept(this,env);
		else if (n.e instanceof BooleanLiteral){
			//netCode = netCode +(indent + "boolean");
		}
		else if (n.e instanceof IntegerLiteral)
			//netCode = netCode +(indent + "int");
		
			
		//netCode = netCode +(" ");
		n.i.accept(this,env);
		if(n.e == null ){
			//netCode = netCode +(";\n");
		}
		else if ((isGlobal	&& (n.e instanceof BooleanLiteral || n.e instanceof IntegerLiteral)) || !isGlobal){
			//netCode = netCode +(" = ");
			netCode = netCode +(" = ");
				n.e.accept(this,env);
				//netCode = netCode +(";\n");
				}
			else{
				//netCode = netCode +(";\n");
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
		//netCode = netCode +("while (");
		n.e.accept(this,env);
		//netCode = netCode +(") ");
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
	//	cCode = cCode +"     break;\n";
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
	public Object visit(ActionFiringCond n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ActionScheduler n, Object e) {
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
