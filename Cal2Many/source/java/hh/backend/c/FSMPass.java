//package hh.backend.c;
//
//import hh.common.translator.Visitor;
//import hh.sequential_AST.syntaxtree.*;
//import hh.simplenet.*;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Map.Entry;
//
//
//
//
//public class FSMPass implements Visitor<Object,Object> {
//
//	public ArrayList<String> sGuards = new ArrayList<String>();
//	public ArrayList<String> sActions = new ArrayList<String>();
////	public HashMap<String, Integer> sActionsWithOutput = new HashMap<String, Integer>();
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
//	public GuardDeclList guardList;
//	
//
//	public Object visit(SEQ_Actor n, Object env) {
//		fsm_stats = new ProcedureDeclList();
//		StatementList sts = new StatementList();
//		
//		int acNo=0;
//		instName=n.i.s;
//
//		inputCons  = n.inputConnections;
//		outputCons = n.outputConnections;
//		actionList = n.acs;
//		guardList  = n.grs;
//		for(GuardDecl gr:n.grs)
//			sGuards.add(gr.i.s);
//		
//		ExpList exs1 = new ExpList();
//		exs1.add(new BoolLiteral(true));		
//		
//		for(ActionDecl ac:n.acs){
//			sActions.add(ac.i.s);
//			ac.i.s=instName+"_"+ac.i.s;		
//			
//		}
//		
//	
//
//		
//		
//		isGlobal=true;
//		for(VarDecl vr: n.vl)
//			vr.accept(this,env);
//	//	n.vl.add(new VarDeclSimp(new IntegerType(0), new Identifier("(*next_state) (void)"),null,true));
//        isGlobal=false;
//
//		n.actionScheduler =(ActionScheduler) n.actionScheduler.accept(this, env);	
//		n.pros.addAll(fsm_stats);
//
//		return  new SEQ_Actor(n.it,n.i,	n.afls,n.input,n.output,n.vl,n.grs,new ActionDeclList(),n.funs,n.pros,
//				n.actionScheduler,
//				n.inputConnections,
//				n.outputConnections);
//	}
//
//
//
//
//	private CStatement TestOutputPorts(int ix, PortList output, CStatement cs) {
//		ExpList exs = new ExpList();
//		
//		exs.add(output.get(0));
////		exs.add(output.get(output.size()-ix));
//		exs.add(new IntegerLiteral(1));
//		if(output.size()-1 == 0){
////		if(ix-1 == 0){
//			// Fire Action
//			/*
//			ExpList exs1 = new ExpList();
//			exs1.add(new BoolLiteral(false));
//			ExpList exs2 = new ExpList();
//			exs2.add(new IdentifierExp("acNo"));
//			exs2.add(new IdentifierExp("actor_state"));
//
//			StatementList sl2 = new StatementList();
//			sl2.add(new StatementCall(new IdentifierExp(instName+ "_Fire_Action"),exs2));
//			sl2.add(new Assign(new Identifier(instName+ "_OutputState"),new IntegerLiteral(-1)));
//			sl2.add(new StatementCall(new IdentifierExp("return"),exs1));*/
//			
//			return new If(new FunctionCall(new IdentifierExp("TestOutputPort"),exs),						      
//						      cs,
//						      null
//						      );
//		}
//		
//		output.remove(0);		
//		return new If(new FunctionCall(new IdentifierExp("TestOutputPort"),exs),
//				TestOutputPorts(ix-1,output,cs),null);
//	}
//
//
//
//	@Override
//	public Object visit(ActionDecl n, Object env) {
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ActionScheduler n, Object env) {
//
//		StatementList sts= new StatementList();
//		ExpList exs = new ExpList();
//	//	exs.add(new IntegerLiteral(1));
//	//	sts.add(new Print(exs,true));
//		for(CStatement st: n.sl)
//		{
//			String sState;
//			Equal eq=null;
//			CStatement state_stm=null;
//			if(st instanceof ElseIf){
//				eq=(Equal)((ElseIf)st).e;
//				state_stm= ((ElseIf)st).s1;
//			}
//			else if(st instanceof If){
//				eq=(Equal)((If)st).e;
//				state_stm= ((If)st).s1;
//			}
//			sState = ((IdentifierExp)eq.e1).s + ((IntegerLiteral)eq.e2).i;
//			
//			state_stm =(CStatement)state_stm.accept(this, sState);
//			fsm_stats.add(new ProcedureDecl(new IntegerType(0), 
//					                        new Identifier(sState),
//					                        new FormalList(), 
//					                        new VarDeclList(), 
//					                        state_stm, 
//					                        new IntegerLiteral(1)
//			                               ));
//			
//		}
//		sts.add(new If(
//				new Equal(new FunctionCall(new IdentifierExp("next_state"),new ExpList()),new IntegerLiteral(0)),
//				new StatementCall(new IdentifierExp("wait"),new ExpList()),null
//				));
//		
//		/*
//		if(bTestOutput){
//			StatementList sts2= new StatementList();
//			
//			StatementList stest= new StatementList();
//			ExpList extest = new ExpList();
//			extest.add(new StringLiteral(instName+" -------- output is fulll"));
//		//	stest.add(new Print(extest,true));
//			stest.add(new StatementCall(new IdentifierExp("wait"),new ExpList()));
//			ExpList exs2 = new ExpList();
//			exs2.add(new IdentifierExp(instName+"_OutputState"));
//			exs2.add(new IdentifierExp(instName+"_NextState"));		
//			sts2.add(new If(new FunctionCall(new IdentifierExp(instName + "_Check_Output"),exs2),
//					new Block(stest),null));
//
//			StatementList sts3= new StatementList();
//			sts3.add(new If(new NotEqual(new IdentifierExp(instName + "_OutputState"),new IntegerLiteral(-1)),
//					new Block(sts2),
//					new Block(sts)
//					));
//
//			return new ActionScheduler(new Identifier("Scheduler_"+instName),n.input,n.output,n.vars,sts3);
//		}
//		else
//			return new ActionScheduler(new Identifier("Scheduler_"+instName),n.input,n.output,n.vars,sts);*/
//		return new ActionScheduler(new Identifier("Scheduler_"+instName),n.input,n.output,n.vars,sts);
//
//	}
//
//
//	@Override
//	public Object visit(And n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(FieldAssign n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ArrayLength n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ArrayLookup n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ArrayType n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(Assign n, Object env) {
//
//		return new Assign((Identifier)n.i.accept(this, env), (Exp)n.e.accept(this, env)	);
//	}
//
//
//	@Override
//	public Object visit(BitAnd n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(BitNot n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(BitOr n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(BitXOr n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(Block n, Object env) {
//
//		StatementList sts=new StatementList();
//        ExpList exs = new ExpList();
//        exs.add(new IntegerLiteral(1));
//		if(n.sl.size()==2){
//			if(n.sl.get(0) instanceof StatementCall){
//				String ss=((IdentifierExp)((StatementCall)n.sl.get(0)).i).s;
//				int acNo =sActions.indexOf(ss);
//				if( acNo != -1){
//					for(VarDecl vd : actionList.get(acNo).vl){
//						if(vd instanceof VarDeclSimp)
//							((VarDeclSimp)vd).e=null;
//						else
//							((VarDeclComp)vd).e=null;
//						sts.add(vd);
//					}
//						
//					
//				//	sts.addAll(actionList.get(acNo).vl);
//					sts.addAll(actionList.get(acNo).sl);					
//					if(n.sl.get(1) instanceof Assign){
//					 Assign as = (Assign) n.sl.get(1);
//					 sts.add(new Assign(new Identifier("next_state"),
//							            new IdentifierExp(as.i.s + ((IntegerLiteral)as.e).i)
//					                    ));
//					}
//					sts.add(new StatementCall(new IdentifierExp("return"),exs));						
//				//	sts.add((CStatement)n.sl.get(1).accept(this, env));
//					
//					if(actionList.get(acNo).output.size()>0){
//						StatementList sts2 = new StatementList();
//						 sts2.add(TestOutputPorts(actionList.get(acNo).output.size(),actionList.get(acNo).output,new Block(sts)));
//						 sts2.add(new Assign(new Identifier("next_state"),
//						            new IdentifierExp((String)env)
//				                    ));
//						 ExpList exs2 = new ExpList();
//				        exs2.add(new IntegerLiteral(0));
//						 sts2.add(new StatementCall(new IdentifierExp("return"),exs2));	
//						 return new Block(sts2);
//					}
//					else			
//						return new Block(sts);
//
//				}
//			}
//			else if(n.sl.get(1) instanceof StatementCall){
//				if(  ((IdentifierExp)((StatementCall)n.sl.get(1)).i).s.equals("wait") ){
//					if(n.sl.get(0) instanceof Assign){
//						 Assign as = (Assign) n.sl.get(0);
//						 sts.add(new Assign(new Identifier("next_state"),
//								            new IdentifierExp(as.i.s + ((IntegerLiteral)as.e).i)
//						                    ));
//						}
//					sts.add(n.sl.get(1));
//					
//				//	sts.addAll(n.sl);
//					
//					return new Block(sts);
//
//				}
//			}
//				
//			
//			
//		}
//		
//		StatementList sts2=new StatementList();	
//		for(CStatement st:n.sl)
//			sts2.add((CStatement)st.accept(this, env));
//		return  new Block(sts2) ;
//	}
//
//
//	@Override
//	public Object visit(BooleanType n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(BoolLiteral n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(Call n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(Channel n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ConsumeToken n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(Divide n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(DoubleLiteral n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ElseIf n, Object env) {
//		Exp e=(Exp)n.e.accept(this, env);
//		CStatement s1=null,s2=null; 
//		if(n.s1!=null)
//			s1=(CStatement)n.s1.accept(this, env);
//		if(n.s2!=null)
//			s2=(CStatement)n.s2.accept(this, env);
//
//		return new ElseIf(e,s1,s2);
//	}
//
//
//	@Override
//	public Object visit(Entity n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(EntityPort n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(Equal n, Object env) {
//
//		return new Equal((Exp)n.e1.accept(this, env),(Exp)n.e2.accept(this, env));
//	}
//
//
//	@Override
//	public Object visit(ExpIndexer n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(FlatNetwork n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(Formal n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(FunctionCall n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(FunctionDecl n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(GenIntegers n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(GoTo n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(GreaterOrEqual n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(GreaterThan n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(GuardDecl n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(Identifier n, Object env) {
//		if(n.s.endsWith("_State"))
//			return new Identifier(instName+"_State");
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(IdentifierExp n, Object env) {
//		if(n.s.endsWith("_State"))
//			return new IdentifierExp(instName+"_State");
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(IdentifierType n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(If n, Object env) {
//		CStatement s1=null;
//		CStatement s2=null;
//		Exp ex=null;
//		if(n.s1 !=null)
//			s1=(CStatement)n.s1.accept(this, env);
//		if(n.s2!=null)
//			s2=(CStatement)n.s2.accept(this, env);
//
//		if(n.e instanceof FunctionCall){
//			String s=((FunctionCall)n.e).i.s;
//			if(sGuards.size()>0)
//			if(sGuards.contains(s)){
//				GuardDecl gr=guardList.get(sGuards.indexOf(s));
//				
//				int iSize=gr.es.size();
//				while (iSize>0){
//					if(ex==null)
//						ex=gr.es.get(gr.es.size()-iSize);
//					else
//						ex= new And(ex,gr.es.get(gr.es.size()-iSize));
//					iSize--;				
//
//				}		
//			}
//		}
//		
//		if(ex==null)
//			ex=(Exp)n.e.accept(this, env);
//
//
//
//
//
//		return new If(ex,s1,s2);
//	}
//
//
//	@Override
//	public Object visit(IfExp n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(IntegerLiteral n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(IntegerType n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(Lable n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(LeftShift n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(LessOrEqual n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(LessThan n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ListAssign n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ListCompGen n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ListComprehension n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(ListType n, Object env) {
//
//		return n;
//	}
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
//	@Override
//	public Object visit(NewObject n, Object env) {
//
//		return n;
//	}
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
//
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
//	/*    String name= ((IdentifierExp)n.e).s;
//		if(sActions.contains(name))
//			return new StatementCall(new IdentifierExp(instName +"_"+name),new ExpList());
//*/
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
//	@Override
//	public Object visit(Structure n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(This n, Object env) {
//
//		return n;
//	}
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
//	public Object visit(TypeNull n, Object env) {
//
//		return n;
//	}
//
//
//	@Override
//	public Object visit(VarDeclComp n, Object env) {
////		if(isGlobal)
////			n.i.s=instName+"_"+n.i.s;
//		return n;
//	}
//
//
//	@Override
//	public Object visit(VarDeclSimp n, Object env) {
////		if(isGlobal)
////			n.i.s=instName+"_"+n.i.s;
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
//	public Object visit(CharLiteral n, Object e) {
//		return n;
//	}
//
//
//
//	@Override
//	public Object visit(ListConc n, Object e) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//	@Override
//	public Object visit(StringConc n, Object e) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//
//	@Override
//	public Object visit(SwitchCase n, Object e) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//
//	@Override
//	public Object visit(For n, Object e) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
