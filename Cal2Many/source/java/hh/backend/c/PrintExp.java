package hh.backend.c;

import hh.AST.syntaxtree.*;
import hh.common.translator.VisitorExp;
import hh.simplenet.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;




public class PrintExp implements VisitorExp<Object,Object> {

	private String indent = "";
	private String comint = "";
	private boolean duplicatVar,oldval,isGlobal;
	public ExpList globExp= new ExpList();
	public IdentifierList globId= new IdentifierList(); 
	public HashSet<String> hsInclude = new HashSet<String>();
	//	public String cCode ="";
	public String sInclude ="";
	public String instName="";
	public String typeName="";
	public ArrayList<Channel> cInput = new ArrayList<Channel>();
	public ArrayList<Channel> cOutput = new ArrayList<Channel>();

	public HashMap<String, ArrayList<String>> inputCons;
	public HashMap<String, ArrayList<String>> outputCons;
	public boolean printPort;
	
	public Object visit(And n, Object env) {
		return "(" + (String)n.e1.accept(this,env) + " && " + (String)n.e2.accept(this,env) +")";
	}

	// Exp e1,e2;
	public Object visit(BitAnd n, Object env) {
		return "(" + (String)n.e1.accept(this,env) + " & " + (String)n.e2.accept(this,env) +")";
	}


	// Exp e1,e2;
	public Object visit(BitNot n, Object env) {
		return "( ~ " + (String)n.e.accept(this,env) +")";
	}

	// Exp e1,e2;
	public Object visit(BitOr n, Object env) {

		return "(" + (String)n.e1.accept(this,env) + " | " + (String)n.e2.accept(this,env) +")";
	}

	@Override
	public Object visit(BitXOr n, Object env) {
		return "(" + (String)n.e1.accept(this,env) + " ^ " + (String)n.e2.accept(this,env) +")";
	}
	public Object visit(BooleanLiteral n, Object env) {
		return n.value.toString();
	}

	// Exp e;
	// identifier i;
	// ExpList el;
	public Object visit(Call n, Object env) {
		String cCode ="";
		cCode = cCode + (String)n.e.accept(this,env);
		cCode = cCode +(".") + n.i.s;

		cCode = cCode +("(");
		for ( int i = 0; i < n.el.size(); i++ ) {
			cCode = cCode + (String)n.el.get(i).accept(this,env);
			if ( i+1 < n.el.size() ) { cCode = cCode +(", "); }
		}
		cCode = cCode +")";
		return cCode;
	}


	@Override
	public Object visit(CharLiteral n, Object env) {
		return ""+n.c;
	}

	// Exp e1,e2;
	public Object visit(Divide n, Object env) {
		if(n.e1 instanceof IntegerLiteral && n.e2 instanceof IntegerLiteral)
			return ""+ (((IntegerLiteral)n.e1).i / ((IntegerLiteral)n.e2).i);

		return "(" + (String)n.e1.accept(this,env) + " / " + (String)n.e2.accept(this,env) +")";
	}
	public Object visit(DoubleLiteral n, Object env) {
		return ""+n.d;
	}
	public Object visit(Equal n, Object env) {
			return "(" + (String)n.e1.accept(this,env) + " == " + (String)n.e2.accept(this,env) +")";
	}
	
	public Object visit(ExpIndexer n, Object env) {
		String cCode ="";
		//cCode = cCode +("[");

		cCode = cCode +n.structure.accept(this, env);
		//for(Exp ex: n.location)
		{
			cCode = cCode +("[");
			n.location.accept(this, env);
			cCode = cCode +("]");

		}

		return cCode;
	}

	@Override
	public Object visit(ExpList n, Object e) {
		String cCode ="";
		// TODO Auto-generated method stub
		return cCode;
	}

	// Type t;
	// Identifier i;
	public Object visit(FunctionCall n, Object env){
		String cCode ="";
		int i;
		n.i.accept(this,env);
		cCode = cCode +("(");
		for(i=0; i<n.exs.size(); i++){
			cCode = cCode + (String)n.exs.get(i).accept(this,env);

			if(i<n.exs.size()-1)
				cCode = cCode +(", ");			

		}
		cCode = cCode +(")");
		printPort = false;	
		return cCode;
	}

	@Override
	public Object visit(GenIntegers n, Object env) {
		String cCode ="";

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
		if (env instanceof VarDeclList){
			VarDeclList varss = (VarDeclList)env;
			for(VarDecl vars: varss){
				if(vars instanceof VarDeclSimp)
					var = ((VarDeclSimp)vars).i.s;
				else
					var ="i_0";
				cCode = cCode +"\nfor( int " +var ;
				cCode = cCode +(" = ");
				cCode = cCode + (String)n.e1.accept(this,env);
				cCode = cCode +("; " + var +"<=");
				cCode = cCode + (String)n.e2.accept(this,env);
				cCode = cCode +"; "+ var+" ++)\n";

			}
		}
		else{
			var ="i_0";
			cCode = cCode +"\nfor( int " +var ;
			cCode = cCode +(" = ");
			cCode = cCode + (String)n.e1.accept(this,env);
			cCode = cCode +("; " + var +"<=");
			cCode = cCode + (String)n.e2.accept(this,env);
			cCode = cCode +"; "+ var+" ++)\n";

		}


		return var;
	}

	@Override
	public Object visit(GreaterOrEqual n, Object env) {
		return "(" + (String)n.e1.accept(this,env) + " >= " + (String)n.e2.accept(this,env) +")";
	}

	public Object visit(GreaterThan n, Object env) {
		return "(" + (String)n.e1.accept(this,env) + " > " + (String)n.e2.accept(this,env) +")";
	}

	public Object visit(Identifier n, Object env) {
		return (n.s.replace('.','_'));
	}

	// String s;
	public Object visit(IdentifierExp n, Object env) {
		String cCode ="";
		if(n.s.startsWith("$old$"))
			if(oldval)
				cCode = cCode +("old_"+n.s.substring(5));
			else
				cCode = cCode +(n.s.substring(5));
		else  		
			cCode = cCode +(n.s.replace('.','_'));
		return cCode;}


	// Exp e;
	// Statement s1,s2;
	// Exp c,e1,e2;
	public Object visit(IfExp n, Object env){
		String cCode ="";
		cCode = cCode +("(");
		n.c.accept(this,env);
		cCode = cCode +(" ? ");
		cCode = cCode + (String)n.e1.accept(this,env);
		cCode = cCode +(" : ");
		if(n.e2!=null)
			cCode = cCode + (String)n.e2.accept(this,env);
		else
			cCode = cCode + "0";
		cCode = cCode +(")");
		return cCode;
	}

	// int i;
	public Object visit(IntegerLiteral n, Object env) {
		return ""+n.i;
	}

	public Object visit(IntegerType n, Object env) {
		String cCode ="";

		String sSize;
		if(n.size<=8)
			sSize =  "8_t";

		else if(n.size<=16)
			sSize = "16_t";

		else if(n.size<=32)
			sSize =  "32_t";

		else //if(n.size<=64)
			sSize =  "64_t";
		cCode = cCode +"int"+sSize;
		return cCode;
	}

	// Exp e1,e2;
	public Object visit(LeftShift n, Object env) {
		return "(" + (String)n.e1.accept(this,env) + " << " + (String)n.e2.accept(this,env) +")";
	}

	public Object visit(LessOrEqual n, Object env) {
			return "(" + (String)n.e1.accept(this,env) + " <= " + (String)n.e2.accept(this,env) +")";
	}




	// Exp e1,e2;
	public Object visit(LessThan n, Object env) {
		return "(" + (String)n.e1.accept(this,env) + " < " + (String)n.e2.accept(this,env) +")";
	}

	@Override

	public Object visit(ListCompGen n, Object env){
		String cCode ="";


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
		String cCode ="";
		//DisplayListComprehension(n,env);

		return n;
	}
	@Override
	public Object visit(ListConc n, Object env) {
		String cCode ="";

		cCode = cCode + "\n// ListConc \n";
		cCode = cCode + (String)n.e1.accept(this,"temp");
		cCode = cCode + "\n// =  ListConc \n";

		cCode = cCode + (String)n.e2.accept(this,env);

		return cCode;
	}

	// Exp e1,e2;
	public Object visit(Minus n, Object env) {
		if(n.e1 instanceof IntegerLiteral && n.e2 instanceof IntegerLiteral)
			return ""+ (((IntegerLiteral)n.e1).i - ((IntegerLiteral)n.e2).i);
		return "(" + (String)n.e1.accept(this,env) + " - " + (String)n.e2.accept(this,env) +")";
	}

	public Object visit(Negate n, Object env) {
		return "( - " + (String)n.e.accept(this,env)+")";
	}
	// Exp e;
	public Object visit(NewArray n, Object env) {
		String cCode ="";
		cCode = cCode +("new int [");
		cCode = cCode + (String)n.e.accept(this,env);
		cCode = cCode +("]");
		return cCode;
	}
	// Identifier i;
		// Exp e1,e2;


	// Exp e;
	public Object visit(Not n, Object env) {
		return "(! " + (String)n.e.accept(this,env) +")";
	}


	public Object visit(NotEqual n, Object env) {
		return "(" + (String)n.e1.accept(this,env) + " != " + (String)n.e2.accept(this,env) +")";
	}
	// Exp e1,e2;
	public Object visit(Or n, Object env) {
		return "(" + (String)n.e1.accept(this,env) + " || " + (String)n.e2.accept(this,env) +")";
	}
	// Exp e1,e2;
	public Object visit(Plus n, Object env) {
		if(n.e1 instanceof IntegerLiteral && n.e2 instanceof IntegerLiteral)
			return ""+ (((IntegerLiteral)n.e1).i + ((IntegerLiteral)n.e2).i);

		return "(" + (String)n.e1.accept(this,env) + "  " + (String)n.e2.accept(this,env) +")";
	}
	public Object visit(PortHH n, Object env){
		String cCode ="";

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
	// Exp e1,e2;
	public Object visit(RightShift n, Object env) {
		return "(" + (String)n.e1.accept(this,env) + " >> " + (String)n.e2.accept(this,env) +")";
	}



	@Override
	public Object visit(StringConc n, Object env) {
		//TODO 
		String cCode ="";
		return cCode;
	}


	public Object visit(StringLiteral n, Object env) {
		//TODO
		return "\""+n.s+"\"";
	}


	public Object visit(TestInputPort n, Object env) {
		String cCode ="";
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

		return cCode;
	}


	@Override
	public Object visit(TestOutputPort n, Object env) {
		String cCode ="";
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

		return cCode;
	}


	// Exp e1,e2;
	public Object visit(Times n, Object env) {

		if(n.e1 instanceof IntegerLiteral && n.e2 instanceof IntegerLiteral)
			return ""+ (((IntegerLiteral)n.e1).i * ((IntegerLiteral)n.e2).i);

		return "(" + (String)n.e1.accept(this,env) + " * " + (String)n.e2.accept(this,env) +")";
	}


	@Override
	public Object visit(MultipleOps n, Object e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(SimpEntityExpr n, Object e) {
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





}
