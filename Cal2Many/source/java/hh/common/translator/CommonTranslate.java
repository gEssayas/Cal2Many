package hh.common.translator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import hh.AST.syntaxtree.*;
import hh.common.Pair;
import hh.common.passes.*;
import net.opendf.ir.cal.*;
import net.opendf.ir.common.*;
import net.opendf.ir.common.ExprLiteral.Kind;
import net.opendf.ir.util.ImmutableEntry;
import net.opendf.ir.util.ImmutableList;

public class CommonTranslate  {

	public static HashMap<String,Type> VarsTypes = new HashMap<String, Type>();	
	public static HashMap<String,Type> VarsTypesProc = new HashMap<String, Type>();	
	public static int bCollectVars = -1;	
	public static HashMap<String,Boolean> IsUsedInGuardExps = new HashMap<String, Boolean>();	
	public static List<String> varIsUsedInGuardExps = new ArrayList<String>();	
	public static HashSet<String> hsActionVars = new HashSet<String>();
	public static VarDeclList actorParmVals = new VarDeclList(); 

	public static FunctionDeclList funList = new FunctionDeclList();
	public static ProcedureDeclList procList = new ProcedureDeclList();

	public static Type getType(TypeExpr t){
		TranslatCALAMCommon tCalAm = new TranslatCALAMCommon();
		return tCalAm.getType(t);
	}



	public static Exp getCExp(Expression ex){
		TranslatCALAMCommon tCalAm = new TranslatCALAMCommon();
		return tCalAm.getCExp(ex) ;
	}


	public static StatementList getCStatements(ImmutableList<Statement> statements){
		TranslatCALAMCommon tCalAm = new TranslatCALAMCommon();
		return tCalAm.getCStatementList(statements) ;
	}
	public static CStatement getCStatement(Statement statement){
		TranslatCALAMCommon tCalAm = new TranslatCALAMCommon();
		return tCalAm.getCStatement(statement) ;
	}


	public static VarDecl getVarDecl(DeclVar var){
		TranslatCALAMCommon tCalAm = new TranslatCALAMCommon();
		return(tCalAm.getVarDecl(var) );

	}
	public static VarDeclList getVarDecls(ImmutableList<DeclVar> vars){
		TranslatCALAMCommon tCalAm = new TranslatCALAMCommon();
		return tCalAm.getVarDeclList(vars) ;

	}
	public static List<Pair<String, Integer>> getVarDeclSizes(ImmutableList<DeclVar> vars){
		List<Pair<String, Integer>> varsSizes = new ArrayList<>();
		TranslatCALAMCommon tCalAm = new TranslatCALAMCommon();
		VarDeclList myvars = tCalAm.getVarDeclList(vars) ;
		for(VarDecl var :myvars){

			Type t =null;
			if(var instanceof VarDeclSimp){
				t = ((VarDeclSimp) var).t;
			}
			else if (var instanceof VarDeclComp){
				t = ((VarDeclComp) var).t;
			}

			int TypeSize = getTypeSize(t);


		}
		return varsSizes;
	}
	private static int getTypeSize(Type t) {
		if(t instanceof BooleanType){
			return 1;
		}
		else if(t instanceof FloatType){
			return 4;
		}
		else if(t instanceof IdentifierType){
			return 4; 
		}
		else if(t instanceof IntegerType){
			return 4;  
		}
		else if(t instanceof ListType){
			return  getTypeSize(((ListType)t).t);
		}
		else if(t instanceof NullType){
			return	4;	
		}




		return 0;
	}
	private static class TranslatCALAMCommon implements DeclVisitor<Object,Object>, ExpressionVisitor<Object,Object>,
	StatementVisitor<Object,Object>,  LValueVisitor<Object,Object>{
		public  SEQ_Actor cpm;

		//		StatementList amStms;

		public VarDeclList varList = new VarDeclList();


		public FormalList parmList;
		public boolean isOld;

		public  CStatement getCStatement(Statement statement){

			return (CStatement)statement.accept(this,null);
		}
		public Exp getCExp(Expression ex) {
			// TODO Auto-generated method stub
			return (Exp) ex.accept(this,null);
		}
		public VarDeclList getVarDeclList(ImmutableList<DeclVar> vars) {
			varList.clear();
			for (DeclVar dl : vars) {
				dl.accept(this, null);
				/*	if (dl.getIdentifier() != null) {
					System.out.println(dl.getName() + " What is going on");
				}*/

			}			
			return varList;
		}
		public VarDecl getVarDecl(DeclVar var) {
			varList.clear();
			var.accept(this, null);
			return(varList.get(0));
		}
		public  StatementList getCStatementList(ImmutableList<Statement> immutableList){
			StatementList cStms = new StatementList();
			for(Statement calstm : immutableList){
				//System.out.println("\n Statem In block............................... " + calstm.toString());
				cStms.add((CStatement)calstm.accept(this,null));
			}
			return cStms;
		}

		public ListOfListCompGen getGenrateFilter(ImmutableList<GeneratorFilter> immutableList){
			ListOfListCompGen  lcgfs = new ListOfListCompGen();
			for(GeneratorFilter gf :immutableList){
				ExpList fils= new ExpList();
				VarDeclList vars= new VarDeclList();

				if(gf.getFilters()!=null){
					//System.out.println("getFilters size " + gf.getFilters().size());
					for(Expression egf:gf.getFilters())
						fils.add((Exp)egf.accept(this,null));
				}

				if(gf.getVariables()!=null){
					//System.out.println(" gen getVariables " + gf.getVariables().size());
					for(DeclVar dvr: gf.getVariables()){
						vars.add(new VarDeclSimp(getType(dvr.getType()),new Identifier(dvr.getName()),null,true));
						//System.out.println(" gen getVar:- " + dvr.getName());
					}

				}
				lcgfs.add(new ListCompGen (vars,(Exp)gf.getCollectionExpr().accept(this,null),fils));
			}



			return lcgfs;
		}

		public GuardDeclList transferList(GuardDeclList vars){
			GuardDeclList vs =new GuardDeclList();
			for(GuardDecl v:vars)
				vs.add(v);				
			return vs;
		}
		public VarDeclList transferList(VarDeclList vars){
			VarDeclList vs =new VarDeclList();
			for(VarDecl v:vars)
				vs.add(v);				
			return vs;
		}


		public Type getType(TypeExpr t){
			if(t==null)
				return null;
			Map<String, Exp> emval = new HashMap<String,Exp>();
			Map<String, Type> emtype = new HashMap<String,Type>();
			if(t.getValueParameters() != null){
				ImmutableList<ImmutableEntry<String, Expression>> mval =  t.getValueParameters();
				for(ImmutableEntry<String, Expression> entry : mval){
					//System.out.println("------------key ---------->"+entry.getKey() + ", value "+entry.getValue()+" _length_ "+ mval.size());

					emval.put(entry.getKey(), (Exp)entry.getValue().accept(this,null));

				}

			}
			if(t.getTypeParameters() != null){
				ImmutableList<ImmutableEntry<String, TypeExpr>> mtype = t.getTypeParameters();
				for(ImmutableEntry<String, TypeExpr> entry : mtype){
					//System.out.println(".............key <::::::::::> "+entry.getKey() + ", value "+entry.getValue().getName()+" -length- "+ mtype.size());
					emtype.put(entry.getKey(), getType(entry.getValue()));
				}

			}

			//System.out.println(" type name " + t.getName());

			if(t.getName().equals("float")){return new FloatType(32);}
			else if(t.getName().equals("bool")){return new BooleanType();}
			else if (t.getName().equals("int")){
				Exp len=null;

				if(emval != null){
					//System.out.println(" emval.size " + emval.size());
					for(Map.Entry<String, Exp> entry : emval.entrySet())
						if(entry.getKey().matches("size"))
							len=entry.getValue();		
				}
				int isize=32;
				if(len instanceof IntegerLiteral)
					isize=((IntegerLiteral)len).i;
				else 
					if(len instanceof IdentifierExp){
						String sparm = ((IdentifierExp)len).s;				
						for(VarDecl vd:actorParmVals){
							if(vd instanceof VarDeclSimp){
								if(sparm.equals(((VarDeclSimp)vd).i.s))
									if(((VarDeclSimp)vd).e instanceof IntegerLiteral)
										isize =((IntegerLiteral)((VarDeclSimp)vd).e).i;

							}
						}
					}
				return (new IntegerType(isize));

			}
			else if(t.getName().equals("List")){

				Exp len=null;
				Type tt=null;
				if(emval != null){
					//System.out.println(" emval.size " + emval.size());
					for(Map.Entry<String, Exp> entry : emval.entrySet())
						if(entry.getKey().matches("size"))
							len=entry.getValue();		
				}

				if(emtype != null){
					//System.out.println(" emtype.size " + emtype.size());
					for(Map.Entry<String, Type> entry : emtype.entrySet())
						tt=entry.getValue();



				}
				return(new ListType(tt,len));}
			else return null;

		}

		public Object visitDeclEntity(DeclEntity d, Object p) {

			for( ParDeclValue pdv : d.getValueParameters()){
				//System.out.println(" getValueParameters   " + pdv.getName());
				//if( == null)
				parmList.add(new Formal(getType( pdv.getType()), new Identifier(pdv.getName())));
				//	varList.add(new VarDecl() /*new ParameterType()*/,new Identifier(pdv.getName()),null));

			}
			for(DeclType  dt : d.getTypeDecls()){
				//System.out.println("getType "+ dt.getName());
				dt.accept(this,p);
			}
			for(DeclVar dv : d.getVarDecls()){
				//System.out.println("getVarDecls "+ dv.getName() ); // variable declaration list 


				if(dv.getType()!=null)
					//System.out.println("  dv.getType() "+ dv.getType().getName() +" _is the type"); // variable declaration list 
					dv.accept(this,p);
			}
			//for(ParDeclType pdt : d.getTypeParameters())
			//System.out.println("//getTypeParam "+pdt.getName());
			return null;
		}

		@Override
		public Object visitDeclType(DeclType d, Object p) {

			//System.out.println("\n visitDeclType ");
			//d.accept(this);

			return null;
		}

		@Override
		public Object visitDeclVar(DeclVar dv, Object p) {


			//System.out.println("\nvisitDeclVar name "+ dv.getName() +" kind  " + dv.getKind() + "  -  ");
			if(dv.getInitialValue()!=null){
				if (dv.getInitialValue() instanceof ExprLambda){

					//System.out.println("ExprLambda " + dv.getName());
					dv.getInitialValue().accept(this,dv.getName());
					//					funList.add((FunctionDecl) dv.getInitialValue().accept(this,p));
					//					funList.add(processExprLambda(dv.getName(), (ExprLambda) dv.getInitialValue()));
				}
				else if(dv.getInitialValue() instanceof ExprProc)
				{
					dv.getInitialValue().accept(this,dv.getName());
				}else{	
					Type ttemp =getType(dv.getType());
					if(ttemp instanceof ListType)
						varList.add(new VarDeclComp(ttemp, new Identifier(dv.getName()),(Exp)dv.getInitialValue().accept(this,p),dv.isAssignable()));
					else
						varList.add(new VarDeclSimp(ttemp, new Identifier(dv.getName()),(Exp)dv.getInitialValue().accept(this,p),dv.isAssignable()));
				}
			}
			else {
				Type ttemp =getType(dv.getType());
				if(ttemp instanceof ListType)
					varList.add(new VarDeclComp(ttemp, new Identifier(dv.getName()),null,dv.isAssignable()));
				else
					varList.add(new VarDeclSimp(ttemp, new Identifier(dv.getName()),null,dv.isAssignable()));
			}
			//System.out.println("END visitDeclar");
			if(!varList.isEmpty()){
				VarDecl vr= varList.get(varList.size()-1);
				if(vr instanceof VarDeclSimp)
					VarsTypesProc.put(((VarDeclSimp)vr).i.s,((VarDeclSimp) vr).t);
				else
					VarsTypesProc.put(((VarDeclComp)vr).i.s,((VarDeclComp) vr).t);

			}

			return null;
		}

		@Override
		public Object visitExprApplication(ExprApplication e, Object p) {
			//System.out.println("\nvisit  ExprApplication");
			ImmutableList<Expression> exarg = e.getArgs();
			ExpList cexs = new ExpList();

			if(exarg.size()>2)
				System.out.println("\n  e.getargs over ExprApplication " + e.getFunction().toString());
			e.getFunction().accept(this,p);
			if (e.getFunction() instanceof ExprVariable){
				ExprVariable op= (ExprVariable)e.getFunction();		

				//System.out.println("\n  instanceof ExprVariable_____  " + op.getVariable().getName());

				if(op.getVariable().getName().equals("bitand"))      return    new   BitAnd((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
				else if(op.getVariable().getName().equals("lshift")) return 	new LeftShift((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
				else if(op.getVariable().getName().equals("rshift")) return    new   RightShift((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
				else if(op.getVariable().getName().equals("bitor"))  return    new   BitOr((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
				else if(op.getVariable().getName().equals("bitxor"))  return    new   BitXOr((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
				else if(op.getVariable().getName().equals("Integers"))  return    new   GenIntegers((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
				else if(op.getVariable().getName().equals("bitnot"))  return    new   BitNot((Exp)exarg.get(0).accept(this,null));

				else{
					for(Expression calex : exarg)
						cexs.add((Exp)calex.accept(this,p));

					return new FunctionCall((IdentifierExp)e.getFunction().accept(this,p),cexs);
				}

			}
			else if (e.getFunction() instanceof ExprField){
				for(Expression calex: exarg)
					cexs.add((Exp)calex.accept(this,p));
				return new FunctionCall((IdentifierExp)e.getFunction().accept(this,p),cexs);

			}
			/*	ExprField ee =(ExprField) e.getFunction();
				ExpList es = new ExpList();		
				ee.g
				if(ee.getName().equals("nextBoolean") )				
					return new FunctionCall(new IdentifierExp("Rand_" + ee.getName()),es);
				else if(ee.getName().equals("nextInt") )
				{
				//	es.add((Exp)exarg.get(0).accept(this,p) );
					return new FunctionCall(new IdentifierExp("Rand_" + ee.getName()),es);
				}
				else if(ee.getName().equals("nextDouble") )
				{
						return new FunctionCall(new IdentifierExp("Rand_" + ee.getName()),es);
				}
				else
					return new FunctionCall(new IdentifierExp(ee.getName()),es);
			}
			//return new FunctionCall(new IdentifierExp(ee.getName()),es);*/
			System.err.println("visitExprApplication " +e.toString());
			System.exit(0);
			return null;

		}

		@Override
		public Object visitExprBinaryOp(ExprBinaryOp e, Object p) {
			ImmutableList<String>      s = e.getOperations();
			ImmutableList<Expression> exarg = e.getOperands();
			//System.out.println("visitExprBinary " + s.get(0));
			if(exarg.size()>2){
				ExpList Operands = new ExpList();
				List<String> Operations = s;

				for(Expression ex:exarg)
					Operands.add((Exp)ex.accept(this,null));

				return new MultipleOps(Operations,Operands);


			}


			if(s.get(0).equals("+"))
				if(exarg.get(0) instanceof ExprList || exarg.get(1) instanceof ExprList)
					return    new ListConc((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));

				else if(exarg.get(0) instanceof ExprLiteral || exarg.get(1) instanceof ExprLiteral){
					if(exarg.get(0) instanceof ExprLiteral)
						if(((ExprLiteral)exarg.get(0)).getKind().equals(Kind.String))
							return    new StringConc((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
					if(exarg.get(1) instanceof ExprLiteral)
						if(((ExprLiteral)exarg.get(1)).getKind().equals(Kind.String))
							return    new StringConc((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
					return    new Plus((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));

				}
				else
					return    new Plus((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));


			else if(s.get(0).equals("-"))   return    new   Minus((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("*"))   return    new   Times((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("/"))   return    new   Divide((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("!="))  return    new   NotEqual((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("="))   return    new   Equal((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals(">"))   return    new   GreaterThan((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals(">="))  return    new   GreaterOrEqual((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("<"))   return    new   LessThan((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("<="))  return    new   LessOrEqual((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("or"))  return    new   Or((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("and")) return    new   And((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals(".."))  return    new   GenIntegers((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("&"))   return    new   BitAnd((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("<<"))  return 	  new   LeftShift((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals(">>"))  return    new   RightShift((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("|"))   return    new   BitOr((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("^"))   return    new   BitXOr((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else{
				System.err.println(s.get(0)+" ExprBinary not found");	
				return null;
			}
		}



		@Override
		public Object visitExprIf(ExprIf e, Object p) {

			//System.out.println("\nvisit  ExprIf");
			return  new IfExp((Exp) e.getCondition().accept(this,null), (Exp) e.getThenExpr().accept(this,null),(Exp)e.getElseExpr().accept(this,null));



		}


		@Override
		public Object visitExprIndexer(ExprIndexer e, Object p) {
			//System.out.println("\nvisit  ExprIndexer");
			Exp exs=null;	

			if(e.getIndex() != null)
				exs= (Exp)e.getIndex().accept(this,p);
			else
				return (Exp)e.getStructure().accept(this,p);
			return new ExpIndexer((Exp)e.getStructure().accept(this,p),exs);
		}

		@Override
		public Object visitExprInput(ExprInput e, Object p) {

			//System.out.println("\nvisit  ExprInput");


			return null;
		}

		@Override
		public Object visitExprLambda(ExprLambda e, Object p) {
			String name="_lambda";
			if(p instanceof String)
				name=(String)p;
			//if(instName.equals("DCPred") && name.equals("saturate"))
			//System.out.println("variabls how " +e.getTypeParameters().size());
			FormalList fls = new FormalList();
			VarDeclList vds = new VarDeclList();
			Exp ret_ex;
			if(e.getValueParameters() !=null)
				for(ParDeclValue pm: e.getValueParameters()){
					//System.out.println(" name :" +pm.getName());
					if(pm.getType()!=null)
						fls.add(new Formal (getType(pm.getType()),new Identifier(pm.getName())));
					else
						fls.add(new Formal (new IntegerType(32),new Identifier(pm.getName())));

				}

			if(e.getBody() instanceof ExprLet){
				vds=(VarDeclList)e.getBody().accept(this,"var");
				ret_ex = (Exp)e.getBody().accept(this,"exp");
			}
			else
				ret_ex = (Exp)e.getBody().accept(this,p);

			if(e.getTypeParameters()!=null)
				for(ParDeclType pdt: e.getTypeParameters()){
					//System.out.println(pdt.getName());
				}

			funList.add(new FunctionDecl(null,
					new Identifier(name),
					fls,//getparm
					vds,//getvardeclr
					ret_ex)
					);


			return new FunctionCall(new IdentifierExp(name),new ExpList());
		}

		@Override
		public Object visitExprLet(ExprLet e, Object p) {

			//System.out.println("\nvisit  ExprLet");
			if(p instanceof String){
				if(p.equals("var")){
					VarDeclList vds = new VarDeclList();
					for(DeclVar decvar: e.getVarDecls()){
						if(decvar.getInitialValue()!=null){
							Type ttemp=getType(decvar.getType());
							if( ttemp instanceof ListType)
								vds.add(new VarDeclComp(ttemp, new Identifier(decvar.getName()),(Exp)decvar.getInitialValue().accept(this,null),decvar.isAssignable()));
							else
								vds.add(new VarDeclSimp(ttemp, new Identifier(decvar.getName()),(Exp)decvar.getInitialValue().accept(this,null),decvar.isAssignable()));
						}else
							vds.add(new VarDeclSimp(getType(decvar.getType()), new Identifier(decvar.getName()),null,decvar.isAssignable()));
					}	
					return vds;
				}
				else
					return e.getBody().accept(this,p);

			}
			else
				return e.getBody().accept(this,p);
		}

		@Override
		public Object visitExprList(ExprList e, Object p) {

			//System.out.println("\nvisit  ExprList");
			ExpList cexs = new ExpList();
			if(e.getElements()!=null){
				//System.out.println(" getElements length " + instName+ e.getElements().size());
				for(Expression exp: e.getElements())
					cexs.add((Exp)exp.accept(this, p));

			}
			//System.out.println("_______________I am telling you___________");			

			if(e.getGenerators()!=null){
				return new ListComprehension(cexs,getGenrateFilter(e.getGenerators()));

				//col_exp = new ListComprehension(cexs,null, VarDeclList avars, Exp acolExp, ExpList afils)
			}
			else
				return new ListComprehension(cexs,null);



		}

		@Override
		public Object visitExprLiteral(ExprLiteral e, Object p) {

			//System.out.println("visit_ExprLiteral  = " + e.getKind());
			//	Null("Null"), True("True"), False("False"), Char(null), Integer(null), Real(null), String(null);

			switch ( e.getKind().ordinal()+1){
			case 1:
				//col_exp = 
				//System.out.println("  ExprLiteral   1 " + e.getKind());
				break;
			case 2:
				//System.out.println("  ExprLiteral   2 " + e.getKind());
				return  new BooleanLiteral(true);

			case 3:
				//System.out.println("  ExprLiteral   3 " + e.getKind());
				return  new BooleanLiteral(false);

			case 4:
				//System.out.println("  ExprLiteral   4 " + e.getKind());
				return new CharLiteral(e.getText().charAt(0));

			case 5:
				if(e.getText().startsWith("0x"))
					return  new IntegerLiteral(Integer.parseInt(e.getText().substring(2),16));
				else
					return  new IntegerLiteral(Integer.parseInt(e.getText()));

			case 6:
				//System.out.println("  ExprLiteral   6" + e.getKind());
				return  new DoubleLiteral(Double.parseDouble(e.getText()));				
			case 7:
				//System.out.println("  ExprLiteral   7" + e.getKind());
				return new StringLiteral(e.getText());
			}
			return null;
		}

		@Override
		public Object visitExprMap(ExprMap e, Object p) {
			//System.out.println("\nvisit  ExprMap");

			return null;
		}

		@Override
		public Object visitExprProc(ExprProc e, Object p) {

			//System.out.println("\nvisit  ExprProc ");
			FormalList fls = new FormalList();
			VarDeclList vds = new VarDeclList();
			Exp ret_ex;
			if(e.getValueParameters() !=null)
				for(ParDeclValue pm: e.getValueParameters()){
					//System.out.println(" name :" +pm.getName());
					if(pm.getType()!=null)
						//System.out.println(" Type :"  +pm.getType().getName());

						fls.add(new Formal (getType(pm.getType()),new Identifier(pm.getName())));

				}

			for(Formal fl:fls)
				VarsTypesProc.put(fl.i.s,fl.t);

			for(VarDecl vr:vds)
				if(vr instanceof VarDeclSimp)
					VarsTypesProc.put(((VarDeclSimp)vr).i.s,((VarDeclSimp) vr).t);
				else
					VarsTypesProc.put(((VarDeclComp)vr).i.s,((VarDeclComp) vr).t);

			VarsTypes.putAll(VarsTypesProc);

			procList.add(new ProcedureDecl(null, 
					new Identifier((String)p),
					fls,vds,
					(CStatement)e.getBody().accept(this,p),null));

			return null;
		}

		@Override
		public Object visitExprSet(ExprSet e, Object p) {

			//System.out.println("\nvisit  ExprSet");
			return null;

		}
		@Override
		public Object visitExprUnaryOp(ExprUnaryOp e, Object p) {

			//System.out.println("Unary Operator " + e.getOperation());


			if(e.getOperation().equals("bitnot"))return   new BitNot((Exp) e.getOperand().accept(this,p));
			else if(e.getOperation().equals("-")){
				Exp ex = (Exp) e.getOperand().accept(this,p);
				if(ex instanceof IntegerLiteral)
					return   new IntegerLiteral(-1 * ((IntegerLiteral)ex).i);
				else    if(ex instanceof DoubleLiteral)
					return   new DoubleLiteral(-1 * ((DoubleLiteral)ex).d);
				else
					return   new Negate((Exp) e.getOperand().accept(this,p));

			}
			else if(e.getOperation().equals("#"))return   e.getOperand().accept(this,"_length");
			// else if(e.getOperation().equals("negate"))return   new Negate((Exp) e.getOperand().accept(this,null));
			else if(e.getOperation().equals("not"))return   new Not((Exp) e.getOperand().accept(this,p));
			else return null;


		}

		@Override
		public Object visitExprVariable(ExprVariable e, Object p) {

			//System.out.println("\nvisit  ExprVariable "+ e.getVariable().getName());
			String name = e.getVariable().getName();

			if(name.startsWith("$old$")){

				//System.out.println(name +" -- yse it  is old " + name.subSequence(0,5) +" the var name is " + name.substring(5));
				isOld=true;
			}
			if (bCollectVars != -1)
				if (hsActionVars.contains(name)) {
					IsUsedInGuardExps.put(name, true);
					if(!varIsUsedInGuardExps.contains(name))
						varIsUsedInGuardExps.add(name);
					return new IdentifierExp(name + "_ac" + bCollectVars);
				}


			return new IdentifierExp(name);


		}

		public Object visitStmtAssignment(StmtAssignment s, Object p) {
			LValue left = s.getLValue();
			String varName ="";
			Exp exme =(Exp)s.getLValue().accept(this,p);
			ExpList eIndex = new ExpList();
			while(exme instanceof ExpIndexer){
				eIndex.add(((ExpIndexer)exme).location);
				exme = ((ExpIndexer)exme).structure;
			}
			if((exme instanceof IdentifierExp))
				varName = ((IdentifierExp)exme).s;
			if(varName.equals("")){
				if (left instanceof LValueIndexer || s.getExpression() instanceof ExprList){
					return new ListAssign((Exp)s.getLValue().accept(this,p),new ExpList(),(Exp)s.getExpression().accept(this,p),null, isOld, null);
				}
				if(left instanceof LValueField){

				}
				return new Assign(new Identifier(((LValueVariable)s.getLValue()).getVariable().getName()),(Exp)s.getExpression().accept(this,p), null);
			}
			else{
				if(VarsTypes.get(varName)!=null){
					if(!(VarsTypes.get(varName) instanceof ListType))
						return new Assign(new Identifier(varName),(Exp)s.getExpression().accept(this,p),VarsTypes.get(varName));
					else {
						int dims = getVarDims((ListType)VarsTypes.get(varName));
						boolean isListCopy = (eIndex.size()!=dims);
						if (left instanceof LValueVariable)
							return new ListAssign(new IdentifierExp(varName),new ExpList(),(Exp)s.getExpression().accept(this,p),null, isListCopy,VarsTypes.get(varName));
						else
							return new ListAssign((Exp)s.getLValue().accept(this,p),new ExpList(),(Exp)s.getExpression().accept(this,p),null, isListCopy,VarsTypes.get(varName));
					}
				}
				else{
					if (left instanceof LValueIndexer || s.getExpression() instanceof ExprList){
						return new ListAssign((Exp)s.getLValue().accept(this,p),new ExpList(),(Exp)s.getExpression().accept(this,p),null, isOld, null);
					}
					if(left instanceof LValueField){

					}
					return new Assign(new Identifier(((LValueVariable)s.getLValue()).getVariable().getName()),(Exp)s.getExpression().accept(this,p), null);
				}        	  



			}


		}


		private int getVarDims(ListType listType) {
			int dims=0;
			dims++;
			Type tmpT = listType;
			while(((ListType)tmpT).t instanceof ListType){
				dims++;			
				tmpT=((ListType)tmpT).t;
			}

			return dims;
		}
		@Override
		public Object visitStmtBlock(StmtBlock s, Object p) {

			//System.out.println("\nvisit  StmtBlock " + s.getStatements().size());
			StatementList sts = new StatementList();
			for(DeclVar decvar: s.getVarDecls()){
				if(decvar.getInitialValue()!=null){
					Type ttemp=getType(decvar.getType());
					if( ttemp instanceof ListType)
						sts.add(new VarDeclComp(ttemp, new Identifier(decvar.getName()),(Exp)decvar.getInitialValue().accept(this,null),decvar.isAssignable()));
					else
						sts.add(new VarDeclSimp(ttemp, new Identifier(decvar.getName()),(Exp)decvar.getInitialValue().accept(this,null),decvar.isAssignable()));
				}else
					sts.add(new VarDeclSimp(getType(decvar.getType()), new Identifier(decvar.getName()),null,decvar.isAssignable()));
			}	

			sts.addAll(getCStatementList(s.getStatements()));

			return new Block(sts);
		}

		@Override
		public Object visitStmtIf(StmtIf s, Object p) {
			//System.out.println("\nvisit StmtIf ");
			s.getCondition().accept(this,null);
			//System.out.println("\nvisit StmtIf Condition " + s.getCondition().toString());

			if(s.getElseBranch()!=null)
				return new If((Exp)s.getCondition().accept(this,p),(CStatement)s.getThenBranch().accept(this,p),(CStatement)s.getElseBranch().accept(this,p));
			else
				return new If((Exp)s.getCondition().accept(this,p),(CStatement)s.getThenBranch().accept(this,p),null);
		}

		@Override
		public Object visitStmtCall(StmtCall s, Object p) {
			//System.out.println("\nvisit  StmtCall "+ instName);
			ExpList args = new ExpList();
			for(Expression arg: s.getArgs())
				args.add((Exp) arg.accept(this,p));
			if (s.getProcedure().accept(this,p) instanceof IdentifierExp){
				IdentifierExp iex= (IdentifierExp)s.getProcedure().accept(this,p);
				if(iex.s.equals("println"))
					return new Print(args,true);
				else if (iex.s.equals("print"))
					return new Print(args,false);
				else
					return new StatementCall((IdentifierExp)s.getProcedure().accept(this,p),args);
			}
			else
				return new StatementCall((IdentifierExp)s.getProcedure().accept(this,p),args);
		}

		@Override
		public Object visitStmtOutput(StmtOutput s, Object p) {

			//System.out.println("\nvisit  StmtOutput");
			ImmutableList<Expression> amex =s.getValues();
			ExpList exs= new ExpList();
			ArrayList<Boolean> olds = new ArrayList<Boolean>();
			for(int i=0;i<amex.size();i++)
				exs.add((Exp) amex.get(i).accept(this,p));
			//			return (new SendToken( new Port(null,new Identifier (outputs.get(instName+"#"+s.getPort().getName()))),							
			//							exs,olds,new IntegerLiteral(s.getRepeat())));
			return (new SendToken( new PortHH(null,new Identifier (s.getPort().getName())),							
					exs,olds,new IntegerLiteral(s.getRepeat())));

		}

		@Override
		public Object visitStmtWhile(StmtWhile s, Object p) {
			//System.out.println("\nvisit StmtWhile ");
			return new While((Exp)s.getCondition().accept(this,p),(CStatement)s.getBody().accept(this,p));		
		}

		@Override
		public Object visitStmtForeach(StmtForeach s, Object p) {


			//For(IdentifierExp ai,Exp ainit,Exp acon, Exp astep, CStatement as)  
			//System.out.println("\nvisit  StmtForeach");
			ImmutableList<GeneratorFilter> gen = s.getGenerators();
			if(gen.get(0).getCollectionExpr() instanceof ExprBinaryOp){
				ExprBinaryOp colExp = (ExprBinaryOp)gen.get(0).getCollectionExpr(); 
				if(colExp.getOperations().get(0).equals("..")){

					return new For(new IdentifierExp(gen.get(0).getVariables().get(0).getName()),
							(Exp)colExp.getOperands().get(0).accept(this,p),
							new LessOrEqual(new IdentifierExp(gen.get(0).getVariables().get(0).getName()),(Exp)colExp.getOperands().get(1).accept(this,p)),
							new Plus(new IdentifierExp(gen.get(0).getVariables().get(0).getName()), new IntegerLiteral(1)),
							(CStatement)s.getBody().accept(this,p));
				}
			}

			return new For(null,null,null,null, (CStatement)s.getBody().accept(this,p));
		}

		@Override
		public Object visitStmtConsume(StmtConsume s, Object p) {
			//System.out.println("vissit StmtConsume " + instName);
			java.util.ArrayList<Identifier>  is = new java.util.ArrayList<Identifier>();
			for(String id : s.getVariabls()){
				if(id.matches("_"))
					is.add(new Identifier("temp_" +100* Math.random()));
				else 
					is.add(new Identifier(id));
			}

			return new ConsumeToken(new PortHH(null, new Identifier(s.getPort().getName())),is,
					new IntegerLiteral(s.getNumberOfTokens()));

		}

		@Override
		public Object visitStmtPeek(StmtPeek s, Object p) {

			//System.out.println("vissit StmtConsume");
			java.util.ArrayList<Identifier>  is = new java.util.ArrayList<Identifier>();
			for(String id : s.getVariabls())
				if(id.matches("_"))
					is.add(new Identifier("temp_" +100* Math.random()));
				else 
					is.add(new Identifier(id+"_ac"+(Integer)p));
			return new ReadToken(new PortHH(null, new Identifier(s.getPort().getName())),is,
					new IntegerLiteral(s.getNumberOfTokens()));

		}

		@Override
		public Object visitExprField(ExprField e, Object p) {
			Field f= e.getField();
			Exp ix= (Exp)e.getStructure().accept(this,null);
			String s = f.getName();
			if(ix instanceof IdentifierExp){
				if(((IdentifierExp)ix).s.equals("Mathexp"))
					s= s+ ((IdentifierExp)ix).s;

			}			

			return new IdentifierExp(s);
		}

		@Override
		public Object visitLValueVariable(LValueVariable e,
				Object p) {
			return new IdentifierExp(e.getVariable().getName());
		}

		@Override
		public Object visitLValueIndexer(LValueIndexer e, Object p) {

			Exp exs=null;	

			if(e.getIndex() != null)
				exs= (Exp)e.getIndex().accept(this,p);
			else
				return (Exp)e.getStructure().accept(this,p);
			return new ExpIndexer((Exp)e.getStructure().accept(this,p),exs);			
		}

		@Override
		public Object visitLValueField(LValueField e, Object p) {
			e.getStructure().accept(this,p);
			return null;
		}

	}

}







