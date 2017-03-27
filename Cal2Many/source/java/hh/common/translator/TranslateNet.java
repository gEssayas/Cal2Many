package hh.common.translator;

import hh.AST.syntaxtree.*;
import hh.simplenet.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.opendf.ir.common.*;
import net.opendf.ir.net.*;
import net.opendf.ir.net.ast.*;
import net.opendf.ir.util.ImmutableEntry;
import net.opendf.ir.util.ImmutableList;
public class TranslateNet {

	public static FlatNetwork translate(NetworkDefinition network) {
		Translat2CNet translator = new Translat2CNet(network);
		return translator.generateCast();
	}

	
	private static class Translat2CNet  implements DeclVisitor<Object,Object>, ExpressionVisitor<Object,Object>,StatementVisitor<Object,Object>,
	StructureStmtVisitor<Object, Object>,EntityExprVisitor<Object,Object>, LValueVisitor<Object,Object>{
		private final NetworkDefinition network;
		public  FlatNetwork fnet;
		public PortList input, output;
		public VarDeclList varList;
		public EntityList entityList;
		public ChannelList chanList;
		public FormalList parmList;
		
		
		

		public Translat2CNet(NetworkDefinition network) {
			this.network = network;
			varList = new VarDeclList();
			entityList = new EntityList();
			chanList = new ChannelList();
			parmList= new FormalList();
			input =new PortList();
			output=new PortList();
       		}

		public FlatNetwork generateCast() {
			varList.clear();
			for(Entry<String, EntityExpr> entity : network.getEntities()){
				
				//System.out.println(entity.getKey());
				
				entityList.add((Entity)entity.getValue().accept(this, entity.getKey()));
				
			}		
			
			
			if(network.getStructure() != null && network.getStructure().size()>0){
				for(StructureStatement structure : network.getStructure()){
					chanList.add((Channel)structure.accept(this, null));
					}
				
			}

			
			System.out.println("\n network.accept********** " + network.getName());
			network.accept(this,null);
			
           
			
			FlatNetwork ast = new FlatNetwork  (
			 new IdentifierType(network.getName()),
			 parmList,
				getPortList(network.getInputPorts()),
				getPortList(network.getOutputPorts()),
					varList,
					entityList,
					chanList
					);
			return ast;
		}
		
		public ListOfListCompGen getGenrateFilter(ImmutableList<GeneratorFilter> immutableList){
			ListOfListCompGen  lcgfs = new ListOfListCompGen();
			ExpList fils= new ExpList();
			VarDeclList vars= new VarDeclList();
			for(GeneratorFilter gf :immutableList){
				fils.clear();
				vars.clear();
				if(gf.getFilters()!=null){
					//System.out.println(" getFilters ");
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
		
	
		public StatementList getCStatementList(Statement [] calStms){
			StatementList cStms = new StatementList();
			for(Statement calstm : calStms){
				//System.out.println("\n Statem In block " + calstm.toString());
				cStms.add((CStatement)calstm.accept(this,null));
			}
			return cStms;
		}
		

		public  PortList  getPortList( ImmutableList<PortDecl> ports){
			PortList ps = new PortList();
			for (PortDecl pd : ports){
				
						ps.add( new PortHH(
								getType(pd.getType()),
								new Identifier (pd.getName())));
					
			}


			return ps;
		}

			public Type getType(TypeExpr t){
			if(t==null)
				return null;
			Map<String, Exp> emval = new HashMap<String,Exp>();
			Map<String, Type> emtype = new HashMap<String,Type>();
			if(t.getValueParameters() != null){
				ImmutableList<ImmutableEntry<String, Expression>> mval = t.getValueParameters();
				for(Map.Entry<String, Expression> entry : mval){
					//System.out.println("------------key ---------->"+entry.getKey() + ", value "+entry.getValue()+" _length_ "+ mval.size());
					
					emval.put(entry.getKey(), (Exp)entry.getValue().accept(this,null));
					
				}

			}
			if(t.getTypeParameters() != null){
				ImmutableList<ImmutableEntry<String, TypeExpr>> mtype = t.getTypeParameters();
				for(Map.Entry<String, TypeExpr> entry : mtype){
					//System.out.println(".............key <::::::::::> "+entry.getKey() + ", value "+entry.getValue().getName()+" -length- "+ mtype.size());
					emtype.put(entry.getKey(), getType(entry.getValue()));
				}

			}
			
			//System.out.println(" type name " + t.getName());

			if(t.getName().equals("bool")){return new BooleanType();}
			else if (t.getName().equals("int")){
				
				
				Exp len=null;

				if(emval != null){
					System.out.println(" emval.size " + emval.size());
					for(Map.Entry<String, Exp> entry : emval.entrySet())
						if(entry.getKey().matches("size"))
							len=entry.getValue();		
				}
				int isize=0;
				if(len instanceof IntegerLiteral)
					isize=((IntegerLiteral)len).i;
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

			//int type=0
			//System.out.println(" DeclEntity name " + d.getName() +" name space " +d.getNamespaceDecl());
			//System.out.println(" input " +d.getInputPorts().getFullName() +"  output " + d.getOutputPorts().getFullName());

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


				if(dv.getType()!=null){
					//System.out.println("  dv.getType() "+ dv.getType().getName() +" _is the type"); // variable declaration list
					
				}
				dv.accept(this,p);
			}
			for(ParDeclType pdt : d.getTypeParameters()){
			//	//System.out.println("//getTypeParam "+pdt.getName());
			}
		return new Identifier(d.getName());
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
					
					Type ttemp =getType(dv.getType());
					if(ttemp instanceof ListType)
						varList.add(new VarDeclComp(ttemp, new Identifier(dv.getName()),(Exp)dv.getInitialValue().accept(this,p),dv.isAssignable()));
					else
						varList.add(new VarDeclSimp(ttemp, new Identifier(dv.getName()),(Exp)dv.getInitialValue().accept(this,p),dv.isAssignable()));
					
			}
			else
				varList.add(new VarDeclSimp(getType(dv.getType()), new Identifier(dv.getName()),null,dv.isAssignable()));	
			//System.out.println("END visitDeclar");

		return null;
		}

		@Override
		public Object visitExprApplication(ExprApplication e, Object p) {
  			//System.out.println("\nvisit  ExprApplication");
			//if( e.getArgs() != null)

			
			ImmutableList<Expression> exarg = e.getArgs();
			ExpList cexs = new ExpList();

			//System.out.println("\n  e.getargs over ExprApplication " + e.getFunction().toString());
            e.getFunction().accept(this,p);
			if (e.getFunction() instanceof ExprVariable){
				//System.out.println("\n  instanceof ExprVariable");

			    String op = ((ExprVariable)e.getFunction()).getVariable().getName();
			    
			   
				if(op.equals("bitand"))      return    new BitAnd((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
				else if(op.equals("lshift")) return 	new LeftShift((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
				else if(op.equals("rshift")) return    new RightShift((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
				else if(op.equals("bitor"))  return    new BitOr((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
				else if(op.equals("Integers"))  return    new GenIntegers((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));

				else{
			    for(Expression calex : exarg)
				cexs.add((Exp)calex.accept(this,p));
			    
			    return new FunctionCall((IdentifierExp)e.getFunction().accept(this,p),cexs);
				}
			
			}
			/*else if (e.getFunction() instanceof ExprEntry){
				ExprEntry ee =(ExprEntry) e.getFunction();
				ExpList es = new ExpList();			
				if(ee.getName().equals("nextBoolean") )				
					return new FunctionCall(new IdentifierExp("Rand_" + ee.getName()),es);
				if(ee.getName().equals("nextInt") )
				{
					es.add((Exp)exarg.get(0).accept(this,p) );
					return new FunctionCall(new IdentifierExp("Rand_" + ee.getName()),es);
				}
			}
			
*/			System.err.println("visit  ExprApplication "+e.getFunction().toString());
			return null;

		}

		@Override
		public Object visitExprBinaryOp(ExprBinaryOp e, Object p) {
				//System.out.println("visitExprBinary");
			ImmutableList<String>      s = e.getOperations();
			
			ImmutableList<Expression> exarg = e.getOperands();
			//System.out.println(s[0]);
				
			if(s.get(0).equals("+"))return    new Plus((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("-"))return    new Minus((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("*"))return    new Times((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("/"))return    new Divide((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("!="))return    new NotEqual((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("="))return    new Equal((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals(">"))return    new GreaterThan((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals(">="))return    new GreaterOrEqual((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("<"))return    new LessThan((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("<="))return    new LessOrEqual((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("or"))return    new Or((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals("and"))return    new And((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
			else if(s.get(0).equals(".."))  return    new GenIntegers((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));

			else return null;
		}

//public Object visitExprEntry(ExprEntry e, Object p) {
//			//System.out.println("\nvisit  ExprEntry " +e.getName());
//
//			
//		return null;
//		}

		@Override
		public Object visitExprIf(ExprIf e, Object p) {

			//System.out.println("\nvisit  ExprIf");
			return  new IfExp((Exp) e.getCondition().accept(this,null), (Exp) e.getThenExpr().accept(this,null),(Exp)e.getElseExpr().accept(this,null));

		

		}


		@Override
		public Object visitExprIndexer(ExprIndexer e, Object p) {
			// TODU Indexer ??
			//System.out.println("\nvisit  ExprIndexer");
			Exp exs = null;			
			
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

			//System.out.println("visit  ExprLambda _____ " + e.toString());
		/*	funList.add(null,
					new Identifier(e.g ac.toString()+"_guard"),
					null,
					transferList(varList),
					getConsumeTokenStms(ac.getInputPatterns(),true),
					getExp(ex)));*/


		return null;
		}

		@Override
		public Object visitExprLet(ExprLet e, Object p) {

//System.out.println("\nvisit  ExprLet");
			
		return null;
		}

		@Override
		public Object visitExprList(ExprList e, Object p) {

			System.out.println("\nvisit  ExprList");
			ExpList cexs = new ExpList();
			if(e.getElements()!=null){
				System.out.println(" getElements length " + e.getElements().size());
				for(Expression exp: e.getElements()){
					cexs.add((Exp)exp.accept(this, p));
					System.out.println(cexs.get(cexs.size()-1).toString());
				}
				
			}
			System.out.println("_______________I am telling you___________");			

			if(e.getGenerators()!=null){
				return new ListComprehension(cexs,getGenrateFilter(e.getGenerators()));

				//col_exp = new ListComprehension(cexs,null, VarDeclList avars, Exp acolExp, ExpList afils)
			}
			else
				return new ListComprehension(cexs,null);



		}
		@Override
		public Object visitExprLiteral(ExprLiteral e, Object p) {

			//System.out.println("visit_ExprLiteral  = " + e);

			switch ( e.getKind().ordinal()+1){
			case 1:
				//col_exp = 
				//System.out.println("  ExprLiteral   1" + e.getKind());
				break;
			case 2:
				//System.out.println("  ExprLiteral   2" + e.getKind());
				return  new BooleanLiteral(true);
			
			case 3:
				//System.out.println("  ExprLiteral   3" + e.getKind());
				return  new BooleanLiteral(false);
				
			case 4:
				//System.out.println("  ExprLiteral   4" + e.getKind());
				break;
			case 5:
				//System.out.println("  ExprLiteral   5" +Integer.parseInt(e.getText()));
				return  new IntegerLiteral(Integer.parseInt(e.getText()));
				
			case 6:
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

//System.out.println("\nvisit  ExprProc");
			
		return null;
		}

		@Override
		public Object visitExprSet(ExprSet e, Object p) {

       //System.out.println("\nvisit  ExprSet");
        /*
		ExpList cexs = new ExpList();
		if(e.getElements()!=null){
			//System.out.println(" getElements length " + e.getElements().size());
		for(Expression eelt: e.getElements())
			cexs.add((Exp)eelt.accept(this,p));
		}
		//System.out.println("_______________I am telling you___________");

		if(e.getGenerators()!=null){
			return new ListComprehension(cexs,getGenrateFilter(e.getGenerators()));

			//col_exp = new ListComprehension(cexs,null, VarDeclList avars, Exp acolExp, ExpList afils)
		}
		else
			return new ListComprehension(cexs,null);*/
		return null;

		}
		@Override
		    public Object visitExprUnaryOp(ExprUnaryOp e, Object p) {
		    //System.out.print("Unary Operator " + e.getOperation());
		    
		    
		    if(e.getOperation().equals("bitnot"))return   new BitNot((Exp) e.getOperand().accept(this,null));
		    else if(e.getOperation().equals("-"))return   new Negate((Exp) e.getOperand().accept(this,null));
		   // else if(e.getOperation().equals("negate"))return   new Negate((Exp) e.getOperand().accept(this,null));
		    else if(e.getOperation().equals("not"))return   new Not((Exp) e.getOperand().accept(this,null));
		    else return null;
		    
		    
		}

		@Override
		public Object visitExprVariable(ExprVariable e, Object p) {

		//	//System.out.println("\nvisit  ExprVariable "+ e.getName());
			return new IdentifierExp(e.getVariable().getName());
			}
		public Object visitStmtAssignment(StmtAssignment s, Object p) {
			LValue left = s.getLValue();
			if (left instanceof LValueIndexer){
				return new ListAssign((Exp)s.getLValue().accept(this,p),new ExpList(),(Exp)s.getExpression().accept(this,p),null, false, null);
			}
			
			

//			if(left instanceof LValueField)
//			{
//				Field f= (Field)((LValueField) left).getField();
//				/*	
//				return new FieldAssign(
//						(Identifier)((LValueField) left).getStructure().accept(this,p),
//						f.get
//						(Exp)s.getExpression().accept(this,p));*/
//
//			}

			return new Assign(new Identifier(((LValueVariable)s.getLValue()).getVariable().getName()),(Exp)s.getExpression().accept(this,p), null);

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
			//System.out.println("\nvisit  StmtCall");
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
			
		return null;
		}

		@Override
		public Object visitStmtWhile(StmtWhile s, Object p) {
			//System.out.println("\nvisit StmtWhile ");
			return new While((Exp)s.getCondition().accept(this,p),(CStatement)s.getBody().accept(this,p));		
		
		
		
		}

		@Override
		public Object visitStmtForeach(StmtForeach s, Object p) {
// TODO 
//System.out.println("\nvisit  StmtForeach");
        

			return null;// new For((CStatement)s.getBody().accept(this,p));
		}

		@Override
		public Object visitStmtConsume(StmtConsume s, Object p) {
			return null;
		}

		@Override
		public Object visitStructureConnectionStmt(StructureConnectionStmt stmt, Object p) {
			//System.out.println("vissit StructureConnectionStmt " + stmt.toString());
			if(stmt.getSrc().getEntityIndex()!=null)
			for(Expression e: stmt.getSrc().getEntityIndex())
				e.accept(this,p);
			String src=stmt.getSrc().getEntityName();
			String dst=stmt.getDst().getEntityName();
			return new Channel(
					new EntityPort(new IdentifierType(getEntityType(src)),new Identifier(src),new Identifier(stmt.getSrc().getPortName())),
					new EntityPort(new IdentifierType(getEntityType(dst)),new Identifier(dst),new Identifier(stmt.getDst().getPortName())),					
					stmt.getToolAttributes());
		}

		private String getEntityType(String src) {
			if(src ==null)
				return network.getName();			
			for(Entity en:entityList)
				if(en.i.s.equals(src))
					return(en.it.s);
			
			return null;
		}

		@Override
		public Object visitStructureIfStmt(StructureIfStmt stmt, Object p) {
			//System.out.println("vissit StructureIfStmt");
		return null;
		}

		@Override
		public Object visitStructureForeachStmt(StructureForeachStmt stmt, Object p) {
			// TODO Auto-generated method stub
			//System.out.println("vissit StructureForeachStmt");
			return null;
		}

		@Override
		public Object visitEntityInstanceExpr(EntityInstanceExpr e, Object p) {
				VarDeclList vrs = new VarDeclList();
			/////System.out.println("vissit EntityInstanceExpr "+ e.getEntityName());
			for(ToolAttribute ta: e.getToolAttributes()){
			//	//System.out.println("ToolAttribute " + ta.getName());
				
			}
            for(Entry<String, Expression> entity : e.getParameterAssignments()){
				
				////System.out.println("key "+entity.getKey());
            	
				
				vrs.add(new VarDeclSimp(null,new Identifier(entity.getKey()),(Exp)entity.getValue().accept(this, null),true));
				
				
			}
			
			return new Entity(new IdentifierType( e.getEntityName()), new Identifier((String)p), vrs);
		}

		@Override
		public Object visitEntityIfExpr(EntityIfExpr e, Object p) {
			// TODO Auto-generated method stub
			//System.out.println("vissit EntityIfExpr");
			return null;
		}

		@Override
		public Object visitEntityListExpr(EntityListExpr e, Object p) {
			// TODO Auto-generated method stub
			//System.out.println("vissit EntityListExpr");
			return null;
		}

		@Override
		public Object visitStmtPeek(StmtPeek s, Object p) {
			return null;
		}

		public Object visitLValueVariable(LValueVariable e,
				Object p) {
			return new IdentifierExp(e.getVariable().getName());
		}

		@Override
		public Object visitLValueIndexer(LValueIndexer e, Object p) {
			
			System.out.println("visit  LValueIndexer");
			Exp exs=null;	

			if(e.getIndex() != null)
				exs= (Exp)e.getIndex().accept(this,p);
			else
				return (Exp)e.getStructure().accept(this,p);
			return new ExpIndexer((Exp)e.getStructure().accept(this,p),exs);			
		}

		@Override
		public Object visitLValueField(LValueField e, Object p) {
			// TODO Auto-generated method stub
			e.getStructure().accept(this,p);
			return null;
		
	}

		@Override
		public Object visitStmtBlock(StmtBlock s, Object p) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object visitExprField(ExprField e, Object p) {
			// TODO Auto-generated method stub
			return null;
		}

}
}