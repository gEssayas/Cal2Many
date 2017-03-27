//package hh.common.passes;
//
//import hh.common.translator.VisitorActor;
//import hh.common.translator.VisitorExp;
//import hh.common.translator.VisitorStm;
//import hh.common.translator.VisitorType;
//import hh.sequential_AST.syntaxtree.*;
//import hh.simplenet.*;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//
//
//
//
//
//
//public class ImperativePass implements 
//
//VisitorActor<Object,Object>, VisitorType<Object,Object>, VisitorStm<Object,Object>, VisitorExp<Object,Object>
//
//{
//	public ArrayList<String> sGuards = new ArrayList<String>();
//	public ArrayList<String> sActions = new ArrayList<String>();
//	public String instName="";
//	public boolean bTestOutput=false;
//	public boolean isGlobal=false;
//   
//	public HashMap<String, ArrayList<String>> inputCons;
//	public HashMap<String, ArrayList<String>> outputCons;
//
//
//	public ProcedureDeclList fsm_stats;
//
//	public ActionDeclList actionList;
//	public FunctionDeclList newfuns;
//	public ProcedureDeclList newpros;
//	public HashMap<String,Type> varTypes = new HashMap<String,Type>();
//
//
//
//
//	private boolean oldval;
//	public ExpList globExp= new ExpList();
//	public IdentifierList globId= new IdentifierList(); 
//	public HashSet<String> hsInclude = new HashSet<String>();
//	public String sInclude ="";
//	public String typeName="";
//	public ArrayList<Channel> cInput = new ArrayList<Channel>();
//	public ArrayList<Channel> cOutput = new ArrayList<Channel>();
//
//	public boolean printPort;
//
//
//
//
//
//
//
//   public int tmp=0;
//
//	public Object visit(SEQ_Actor n, Object env) {
//		fsm_stats = new ProcedureDeclList();
//		for(VarDecl av: n.vl){
//			if(av instanceof VarDeclSimp)
//				varTypes.put(((VarDeclSimp)av).i.s, ((VarDeclSimp)av).t);
//			else if(av instanceof VarDeclComp)
//				varTypes.put(((VarDeclComp)av).i.s, ((VarDeclComp)av).t);
//
//					
//		}
//		
//		
//
//		
//		instName=n.i.s;
//
//		inputCons  = n.inputConnections;
//		outputCons = n.outputConnections;
//
//		actionList = new ActionDeclList();
//		newfuns = new FunctionDeclList();
//		newpros = new ProcedureDeclList();
//
//		for(ActionDecl at:n.acs)
//			actionList.add((ActionDecl)at.accept(this, env));
//
//		for(FunctionDecl at:n.funs)
//			newfuns.add((FunctionDecl)at.accept(this, env));
//
//		for(ProcedureDecl at:n.pros)
//			newpros.add((ProcedureDecl)at.accept(this, env));
//
//
//
//		ExpList exs1 = new ExpList();
//		exs1.add(new BooleanLiteral(true));		
//
//	
//
//
//
//		return  new SEQ_Actor(n.varTypes, n.it,n.i,	n.afls,n.input,n.output,n.vl,n.grs,actionList,newfuns,newpros,
//				(ActionScheduler)n.actionScheduler.accept(this, env),
//				n.inputConnections,
//				n.outputConnections);
//	}
//
//
//
//
//	//	  public Type t;
//	//	  public Identifier i;
//	//	  public VarDeclList vl;
//	//	  public CStatement sl;  
//	//	  public PortList input, output;
//	//	  public ActionFiringCond afc;
//
//	public Object visit(ActionDecl n, Object env) {
//		PortList ins=new PortList(),outs=new PortList();
//		VarDeclList vls = new VarDeclList();
//
//		if(n.input!=null)
//			for(PortHH pr:n.input)
//				ins.add((PortHH)pr.accept(this,env));
//		if(n.output!=null)
//			for(PortHH pr:n.input)
//				outs.add((PortHH)pr.accept(this,env));
//		for(VarDecl v:n.vl)
//			vls.add((VarDecl)v.accept(this, env));
//		n.i.s=instName + "_"+n.i.s;
//		return new ActionDecl(n.t,ins,outs, n.i,vls,((CStatement)n.sl.accept(this, env)),
//				n.afc);
//	}
//	public Object visit(ActionScheduler n, Object env) {
//		StatementList newSts = new StatementList();
//		for(CStatement cs: n.sl){
//			newSts.add((CStatement)cs.accept(this, env));
//		}
//		n.sl=newSts;
//		return n;
//	}
//
//	// Exp e1,e2;
//	public Object visit(And n, Object env) {
//		return n;}
//
//	// Identifier i;
//	// Exp e1,e2;
//	public Object visit(FieldAssign n, Object env) {
//
//		return n;
//	}
//
//
//	// Identifier i;
//	// Exp e;
//	public Object visit(Assign n, Object env) {
///*		if(n.e instanceof FunctionCall){
//			return (CStatement)n.e.accept(this, n.i);
//		}*/
//		n.e = (Exp) n.e.accept(this, env);
//		return n;}
//
//	// Exp e1,e2;
//	public Object visit(BitAnd n, Object env) {
//		return n;}
//
//
//	// Exp e1,e2;
//	public Object visit(BitNot n, Object env) {
//		return n;}
//
//	// Exp e1,e2;
//	public Object visit(BitOr n, Object env) {
//		return n;}
//
//	@Override
//	public Object visit(BitXOr n, Object env) {
//
//		return n;}
//
//
//	// StatementList sl;
//	public Object visit(Block n, Object env) {
//		StatementList newsts = new StatementList();
//		for(CStatement s:n.sl)
//			newsts.add((CStatement)s.accept(this, env));		
//		return new Block(newsts);}
//
//	public Object visit(BooleanType n, Object env) {
//		return n;}
//
//	public Object visit(BooleanLiteral n, Object env) {
//		return n;}
//
//	// Exp e;
//	// identifier i;
//	// ExpList el;
//	public Object visit(Call n, Object env) {
//		return n;}
//
//	public Object visit(Channel n, Object env) {
//		return n;}
//
//	public Object visit(ConsumeToken n, Object env){
//
//		return n;}
//
//	// Exp e1,e2;
//	public Object visit(Divide n, Object env) {
//		return n;}
//
//	public Object visit(DoubleLiteral n, Object env) {
//		return n;}
//	@Override
//	public Object visit(ElseIf n, Object env) {
//		return n;}
//
//	public Object visit(Entity n, Object env) {
//		return n;}
//
//	public Object visit(EntityPort n, Object env) {
//		return n;}
//
//
//	public Object visit(Equal n, Object env) {
//		return n;}
//
//	@Override
//	public Object visit(ExpIndexer n, Object env) {
//		//cCode = cCode +("[");
//
//		return n;
//	}
//
//	public Object visit(FlatNetwork n, Object env) {
//		return n;}
//
//	// Type t;
//	// Identifier i;
//	public Object visit(Formal n, Object env) {
//		return n;}
//
//	public Object visit(FunctionCall n, Object env){
///*		StatementList sts = new StatementList();
//		for(FunctionDecl fnd: newfuns){
//			if(fnd.i.s.equals(n.i.s)){
//				for(int i=0;i<fnd.fl.size();i++){
//					if(fnd.fl.get(i).t instanceof ListType)
//						sts.add(new VarDeclComp(fnd.fl.get(i).t,fnd.fl.get(i).i,n.exs.get(i),true));
//					else
//						sts.add(new VarDeclSimp(fnd.fl.get(i).t,fnd.fl.get(i).i,n.exs.get(i),true));
//				}
//				String fun = "fn_" + tmp++;
//				for(VarDecl vd:fnd.vl){
//					if(vd instanceof VarDeclComp)
//						((VarDeclComp)vd).i.s = fun+((VarDeclComp)vd).i.s 
//					sts.add()
//				}
//				
//				
//			}
//		}
//		*/
//		n.i.s = instName+ "_"+n.i.s;
//		
//		return n;}
//
//	public Object visit(FunctionDecl n, Object env) {
//		n.i.s = instName+ "_"+n.i.s;
//		n.e = (Exp)n.e.accept(this, env);
//		VarDeclList newVar = new VarDeclList();
//		for(VarDecl vl:n.vl)
//			newVar.add((VarDecl)vl.accept(this, env));
//		n.vl=newVar;
//
//		return n;}
//
//	@Override
//	public Object visit(GoTo n, Object e) {
//		return n;}
//
//	public Object visit(GreaterOrEqual n, Object env) {
//		return n;}
//
//	public Object visit(GreaterThan n, Object env) {
//		return n;}
//
//	// Type t;
//	// Identifier i;
//	// FormalList fl;
//	// VarDeclList vl;
//	// StatementList sl;
//	// Exp e;
//	public Object visit(GuardDecl n, Object env) {
//		return n;}
//
//	// String s;
//	public Object visit(Identifier n, Object env) {
//		return n;
//	}
//
//	// String s;
//	public Object visit(IdentifierExp n, Object env) {
//
//		return n;
//		}
//
//	// String s;
//	public Object visit(IdentifierType n, Object env) {
//		return n;}
//
//	// Exp e;
//	// Statement s1,s2;
//	public Object visit(If n, Object env) {
//		if(n.s1 !=null)
//			n.s1 = (CStatement)n.s1.accept(this, env);
//		if(n.s2 !=null)
//			n.s2 = (CStatement)n.s2.accept(this, env);
//		return n;}
//
//	// Exp c,e1,e2;
//	public Object visit(IfExp n, Object env){
//		return n;}
//
//	// int i;
//	public Object visit(IntegerLiteral n, Object env) {
//		return n;}
//
//
//	public Object visit(IntegerType n, Object env) {
//		return n;}
//
//	@Override
//	public Object visit(Lable n, Object e) {
//		return n;}
//
//	// Exp e1,e2;
//	public Object visit(LeftShift n, Object env) {
//		return n;}
//
//	public Object visit(LessOrEqual n, Object env) {
//		return n;}
//
//	// Exp e1,e2;
//	public Object visit(LessThan n, Object env) {
//		return n;}
//
//	@Override
//	public Object visit(ListAssign n, Object env) {
//         
//		
//		if(n.e instanceof ListComprehension){
//			String svar =  "array"+tmp++;
//			StatementList sts = new StatementList();
//			sts.add(new VarDeclComp(varTypes.get(((IdentifierExp)n.i).s),new Identifier(svar),null,true));
//			sts.addAll((StatementList)n.e.accept(this, svar));
//			String ivar ="var"+tmp++;
//			IdentifierExp ie = new IdentifierExp(ivar);
//	    	sts.add(new VarDeclSimp(new IntegerType(32),new Identifier(ivar),new IntegerLiteral(0),true));
//           
//			sts.add(new For(ie,new IntegerLiteral(0),
//					new LessOrEqual(ie,n.len),
//					new Plus(ie,new IntegerLiteral(1)),
//					new ListAssign(new  ExpIndexer(n.i,ie),null,new ExpIndexer(new IdentifierExp(svar),ie),null)
//                   ));
//			
//			return new Block(sts);
//		}
//			
//
//		
//		
//		if(n.es.size()==0 && n.len!=null){
//			String ivar ="var"+tmp++;
//			IdentifierExp ie = new IdentifierExp(ivar);
//			StatementList sts = new StatementList();
//
//	    	sts.add(new VarDeclSimp(new IntegerType(32),new Identifier(ivar),new IntegerLiteral(0),true));
//			sts.add(new For(ie,new IntegerLiteral(0),
//					new LessOrEqual(ie,n.len),
//					new Plus(ie,new IntegerLiteral(1)),
//					new ListAssign(new  ExpIndexer(n.i,ie),null,new ExpIndexer(n.e,ie),null)
//					));    
//
//			return new Block(sts);
//		
//		   }
//		
//		
//		  		
//
//		
//		
//		
//		
//
//	/*	if(n.e instanceof ListComprehension)
//			n.e.accept(this, ((IdentifierExp)n.i).s);
//		else{
//				n.i.accept(this,env);
//				for(Exp e: n.es){
//					cCode = cCode +("[");
//					e.accept(this,env);
//					cCode = cCode +("] ");
//				
//				cCode = cCode +(" = ");
//				n.e.accept(this,env);
//				cCode = cCode +(";");
//			}
//		}*/
//		return n;
//	}
//
//	
//	//returns statement list which have variable declarations for index and tmp array and for loop 
//	public Object visit(ListCompGen n, Object env){
//        StatementList sts = new StatementList();
//		For afor;
//	
//		String var="";
//		if(n.colExp!=null){
//			 sts.addAll(n.vars);
//		
//			if(n.colExp instanceof GenIntegers){
//				IdentifierExp ie =new IdentifierExp(((VarDeclSimp)n.vars.get(0)).i.s);
//				afor = new For(ie,
//						((GenIntegers)n.colExp).e1,
//						new LessOrEqual(ie, ((GenIntegers)n.colExp).e2),
//					    new Plus(ie,new IntegerLiteral(1)),null);
//
//				return new Pair<StatementList, For>(sts,afor);
//				
//				
//			}
//		   if(n.colExp instanceof ListComprehension){
//			   String svar =  "array"+tmp++;
//			   sts.addAll((StatementList)n.colExp.accept(this, svar));			   
//				
//				IdentifierExp ie = new IdentifierExp("index_"+svar);
//		    	sts.add(new VarDeclSimp(new IntegerType(32),new Identifier("index_"+svar),new IntegerLiteral(0),true));
//
//
//				afor = new For(ie,
//						new IntegerLiteral(0),
//						new LessThan(ie, new IdentifierExp("len_"+svar)),
//					    new Plus(ie,new IntegerLiteral(1)),
//						new Assign(new Identifier(((VarDeclSimp)n.vars.get(0)).i.s),new ExpIndexer(new IdentifierExp(svar),ie))
//                        );
//				return new Pair<StatementList, For>(sts,afor);			   
//		   }
//		}
//			
//		//	cCode = cCode +("\n n.colExp ListCompGenFiliter");
//		if(n.fils != null)
//			for(Exp ex: n.fils)
//				ex.accept(this,n.vars);
//
//
//
//		
//		
//		return var;
//
//	}
//
//
//
//
//	public Object visit(ListComprehension n, Object env){
//		
//		StatementList sts = new StatementList();
//		
//		String var="";
//		if(env instanceof String)
//			var =(String)env;
//		else
//			return null;
//		
//		
//		if(n.compGen.size()==0){
//			
//			sts.add(new VarDeclComp(new ListType(new IntegerType(32),new IntegerLiteral(n.eles.size())),
//					new Identifier(var),true,n.eles
//					));
//			sts.add(new VarDeclSimp(new IntegerType(32),new Identifier("len_"+var),new IntegerLiteral(n.eles.size()),true));
//			return sts;
//			
//			
//		}
//		if(n.eles.size()==1){
//			
//			StatementList sts1 = new StatementList();
//		 ArrayList<Pair<StatementList, For>> sts_fors = new ArrayList<Pair<StatementList, For>>();
//		 
//		   for(ListCompGen lcg: n.compGen)			   
//			   sts_fors.add((Pair<StatementList, For>) lcg.accept(this, env));
//		   
//		   for(Pair<StatementList, For> sts_for : sts_fors){
//			   sts.addAll(sts_for.getLeft());
//			   if(sts_for.getRight().s!=null)
//				  sts1.add(sts_for.getRight().s);
//		   }
//		   		   
//			String ivar =  "index_"+var;
//
//			sts.add(new VarDeclSimp(new IntegerType(32),new Identifier(ivar),new IntegerLiteral(0),true));
//  	
//			IdentifierExp ie = new IdentifierExp(ivar);
//			
//			
//			sts1.add(new ListAssign(new  ExpIndexer(new IdentifierExp(var),ie),null, (Exp)n.eles.get(0),null));
//
//			sts1.add(new Assign(new Identifier(ivar),new Plus(ie,new IntegerLiteral(1))));
//			
//          
//		
//			 sts_fors.get(sts_fors.size()-1).getRight().s = new Block(sts1);
//			 
//			 for(int i=sts_fors.size()-2;i>=0;i--)
//				 sts_fors.get(i).getRight().s = sts_fors.get(i+1).getRight();
//		    
//			 sts.add(sts_fors.get(0).getRight());
//			 return sts;		   
//			   
//		}
//		
//		
//		
//		
//		
//		
//	/*	StatementList sts1 = new StatementList();
//		StatementList sts2 = new StatementList();
//		
//		n.e.accept(this, ((IdentifierExp)n.i).s);
//		ListComprehension lcp = (ListComprehension)n.e;
//		
//		for(ListCompGen lcg: lcp.compGen){
//			String ivar ="array"+tmp++;
//
//			sts1.addAll((StatementList)lcg.colExp.accept(this, ivar));
//		}
//		*/
//		
//		//DisplayListComprehension(n,env);
//		
//		
//
//		return sts;
//	}
//	public Object visit(ListType n, Object env) {
//		return n;
//	}
//
//
//
//	@Override
//	public Object visit(Minus n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(Negate n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(NewArray n, Object env) {
//
//		return n;
//	}
//
//
//
//
//	@Override
//	public Object visit(Not n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(NotEqual n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(Or n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(Plus n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(PortHH n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(Print n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ProcedureDecl n, Object env) {
//		n.i.s = instName+ "_"+n.i.s;
//		VarDeclList newVar = new VarDeclList();
//		for(VarDecl vl:n.vl)
//			newVar.add((VarDecl)vl.accept(this, env));
//		n.vl=newVar;
//		n.sl=(CStatement)n.sl.accept(this, env);
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ReadToken n, Object env) {
//		return n;
//	}
//
//
//	@Override
//	public Object visit(RightShift n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(SendToken n, Object env) {
//
//		return n;
//	}
//
//	@Override
//	public Object visit(SimpEntityExpr n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(StatementCall n, Object env) {
//		   String name= ((IdentifierExp)n.i).s;
//		//if(sActions.contains(name))
//		   n.i.s = instName+ "_"+n.i.s;
//		 
//		return n;
//	}
//
//
//	@Override
//	public Object visit(StringLiteral n, Object env) {
//
//		return n;
//	}
//
//
//
//	@Override
//	public Object visit(Times n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(NullType n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(VarDeclComp n, Object env) {
//		//		if(isGlobal)
//		//			n.i.s=instName+"_"+n.i.s;
//		if(n.e !=null)
//			n.e = (Exp) n.e.accept(this, env);
//		return n;
//	}
//
//
//	@Override
//	public Object visit(VarDeclSimp n, Object env) {
//		//		if(isGlobal)
//		//			n.i.s=instName+"_"+n.i.s;
//		if(n.e !=null)
//			n.e = (Exp) n.e.accept(this, env);
//		return n;
//	}
//
//
//	@Override
//	public Object visit(While n, Object env) {
//
//		return n;
//	}
//
//
//
//	@Override
//	public Object visit(CharLiteral n, Object env) {
//		return n;
//	}
//
//
//
//	@Override
//	public Object visit(ListConc n, Object env) {
//		return n;
//	}
//
//
//
//	@Override
//	public Object visit(StringConc n, Object env) {
//		return n;
//	}
//
//
//
//
//	@Override
//	public Object visit(SwitchCase n, Object env) {
//		StatementList newSts = new StatementList();
//		for(CStatement cs:n.sts)
//			newSts.add((CStatement)cs.accept(this, env));
//		n.sts=newSts;
//		return n;
//	}
//
//
//
//
//	@Override
//	public Object visit(For n, Object env) {
//		return n;
//	}
//
//
//
//
//	@Override
//	public Object visit(FloatType n, Object env) {
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ForEach n, Object env) {
//
//		
//		
//		
//	    StatementList sts = new StatementList();
//	    StatementList sts1 = new StatementList();
//	 ArrayList<Pair<StatementList, For>> sts_fors = new ArrayList<Pair<StatementList, For>>();
//	 
//	   for(ListCompGen lcg: n.compGen)			   
//		   sts_fors.add((Pair<StatementList, For>) lcg.accept(this, env));
//	   
//	   for(Pair<StatementList, For> sts_for : sts_fors){
//		   sts.addAll(sts_for.getLeft());
//		   if(sts_for.getRight().s!=null)
//			  sts1.add(sts_for.getRight().s);
//	   }
//	   		   
//			
//	   sts1.add(n.s);
//      
//	
//		 sts_fors.get(sts_fors.size()-1).getRight().s = new Block(sts1);
//		 
//		 for(int i=sts_fors.size()-2;i>=0;i--)
//			 sts_fors.get(i).getRight().s = sts_fors.get(i+1).getRight();
//	    
//		 sts.add(sts_fors.get(0).getRight());
//		 return new Block(sts);		   
//
//	}
//
//
//	@Override
//	public Object visit(Break n, Object e) {
//		return n;
//	}
//
//
//	@Override
//	public Object visit(TestInputPort n, Object e) {
//		return n;
//	}
//
//
//	@Override
//	public Object visit(TestOutputPort n, Object e) {
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ExpList n, Object e) {
//		return n;
//	}
//
//
//	@Override
//	public Object visit(MultipleOps n, Object e) {
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ActionFiringCond n, Object e) {
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ActorFSM n, Object e) {
//		return n;
//	}
//
//
//
//
//	@Override
//	public Object visit(GenIntegers n, Object e) {
//		return n;
//	}
//
//
//
//
//
//
//
//}
