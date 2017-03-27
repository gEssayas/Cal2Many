//package hh.common.translator;
//
//import hh.sequential_AST.syntaxtree.*;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//
//
//
//import net.opendf.ir.cal.*;
//import net.opendf.ir.common.*;
//import net.opendf.ir.common.ExprLiteral.Kind;
//import net.opendf.ir.util.ImmutableEntry;
//import net.opendf.ir.util.ImmutableList;
//
//public class TranslateActor {
//
//
//	public static CalC_AST translate(Actor actor,String entType,String entName,HashMap<String, ArrayList<String>> inputs,HashMap<String, ArrayList<String>> outputs) {
//		TranslatActor2CAst translator = new TranslatActor2CAst(actor,entType,inputs,outputs);
//		return translator.generateCast(entName);
//	}
//
//	private static class TranslatActor2CAst  implements DeclVisitor<Object,Object>, ExpressionVisitor<Object,Object>,
//	StatementVisitor<Object,Object>,  LValueVisitor<Object,Object>{
//		private final Actor actor;
//		public  SEQ_Actor cpm;
//
//
//
////		StatementList amStms;
//		public ActionDeclList actionList;
//		public ArrayList<String> actionsNames;
//		int bCollectVars;
//		HashMap<String,Boolean> hmGuardVars;
//		HashSet<String> hsGuardVars;
//		public GuardDeclList guardList;
//		public FunctionDeclList funList;
//		public ProcedureDeclList procList;
//		public VarDeclList varList;
//
//		public ArrayList<VarDeclList> predicateVars;
//		public ArrayList<StatementList> predicateStms;
//		public ExpList predicateExp;
//
//		public FormalList parmList;
//		public boolean isOld;
//		public HashMap<String, ArrayList<String>> inputs;
//		public HashMap<String, ArrayList<String>> outputs;
//		public String instName;
//		public String actorName;
//		public String actionName;
//		
//		ImmutableList<Action> transitions;
//		public TranslatActor2CAst( Actor actor, String entType, HashMap<String, ArrayList<String>> inputs, HashMap<String, ArrayList<String>> outputs) {
//			this.actor = actor;
//			this.inputs=inputs;
//			this.outputs=outputs;
//			instName="";//entType;
//			actionName="";
//	
//			predicateVars = new ArrayList<VarDeclList>();
//			predicateStms = new ArrayList<StatementList>();
//			predicateExp = new ExpList();
//
//			varList = new VarDeclList();
//			actionList = new ActionDeclList();
//			actionsNames = new ArrayList<String>();
//			bCollectVars=-1;
//			hmGuardVars = new HashMap<String,Boolean>();
//			hsGuardVars = new HashSet<String>();
//
//			guardList = new GuardDeclList();
//
//			funList = new FunctionDeclList();
//			procList = new ProcedureDeclList();
//
//			parmList= new FormalList();
//
//			transitions =actor.getActions();
//	
//
//
//
//
//		}
//		
//
//		private CStatement testOutputPorts(PortList outputports, ExpList ports,int acNo,int nextState) {
//			CStatement st=null;
//			ExpList testport=new ExpList();
//			
//			
//			
//			if(outputports.size()==1){
//				
//				testport.add(outputports.get(0));
//				
//				StatementList stms = new StatementList();
//				stms.add(new StatementCall(new IdentifierExp("wait"),new ExpList()));
//
//				StatementList sts=new StatementList();
//				sts.add(new StatementCall(new IdentifierExp("untag.action_"+acNo),ports));
//				sts.add(new Assign(new Identifier(instName+"_State"),new IntegerLiteral(nextState)));
//				
//				return new If(new FunctionCall(new IdentifierExp("TestOutputPort"),testport),
//						new Block(sts),new Block(stms));
//						
//			}
//			else{
//				
//				
//				
//				
//			}
//				
//			
//			
//			
//	/*		for(int i=0; i<ports.size();i++){
//				for(int j=i;j<ports.size();j++){
//					System.out.println("if(Tesoutport(" + ports.get(j).i.s +"){" );
//				}
//				System.out.println("action call;\n}\nelse{");
//				
//			}*/
//			return st;
//		}
//
//		
//		private void translateTransitons() {
//			for(int i=0;i<transitions.size();i++){
//				varList.clear();
//                PortList inputs  = getMapPorts(transitions.get(i).getInputPatterns());
//			    PortList outputs = getOutputPorts(transitions.get(i).getOutputExpressions());
//				for(DeclVar dl:transitions.get(i).getVarDecls()){
//					dl.accept(this,null);
//					if(dl.getIdentifier()!=null)
//						System.out.println(dl.getName() + " What is going on");
//
//				}		
//				
//				varList.addAll(getInputPortVars(transitions.get(i).getInputPatterns()));
//
//				ImmutableList<Statement> acStas = transitions.get(i).getBody(); 
//				//Statement acSta = transitions.get(i).getBody();
//				String acName;
//				if(transitions.get(i).getTag()!= null)
//					acName = transitions.get(i).getTag().toString();
//				else
//					acName ="untag.action_"+i;
//				if(acName.equals(""))
//					acName ="untag.action_"+i;
//				StatementList sls= getConsumeTokenStms(transitions.get(i).getInputPatterns(),false);
//				for(VarDecl vr:varList){
//					if(vr instanceof VarDeclSimp)
//						if(((VarDeclSimp)vr).e !=null)
//							sls.add(new Assign(((VarDeclSimp)vr).i,((VarDeclSimp)vr).e));
//						
//						if(vr instanceof VarDeclComp)
//							if(((VarDeclComp)vr).e!=null)
//								sls.add(new Assign(((VarDeclComp)vr).i,((VarDeclComp)vr).e));			
//					
//				
//				}
//				sls.addAll(getCStatementList(acStas));
//				sls.addAll(getOutStatementList(transitions.get(i).getOutputExpressions()));
//
//				
//				actionsNames.add(acName);
//				actionList.add(
//						new ActionDecl(null, 
//								inputs,//getMapPorts(transitions.get(i).getInputPatterns()),
//								outputs,//getOutputPorts(transitions.get(i).getOutputExpressions()),
//								new Identifier(acName),
//								transferList(varList),
//								new Block(sls),
//								getFiringConditions(i)
//								)
//						);	
//				
//				
//				
//			}
//			
//			/*		return (new SendToken( new PortHH(null,new Identifier (s.getPort().getName())),							
//					exs,olds,new IntegerLiteral(s.getRepeat())));
//			return new ConsumeToken(new PortHH(null, new Identifier(s.getPort().getName())),is,
//					new IntegerLiteral(s.getNumberOfTokens()));
//			return new ReadToken(new PortHH(null, new Identifier(s.getPort().getName())),is,
//					new IntegerLiteral(s.getNumberOfTokens()));
//
//			 * 
//			 * 
//			 * */
//		}
//		private VarDeclList getInputPortVars(ImmutableList<InputPattern> inps) {
//             VarDeclList vdls = new VarDeclList();
//             for(InputPattern ph:inps){
//             	if(ph.getRepeatExpr()!=null)
//             		vdls.add(new VarDeclComp(
//             				new ListType( new IntegerType(32),(Exp)ph.getRepeatExpr().accept(this,null)),
//             				new Identifier(ph.getVariables().get(0)),null, true));
//             	else
//             		vdls.add(new VarDeclSimp(new IntegerType(32),new Identifier(ph.getVariables().get(0)),null,true));
//             	
//             }
//
//			
//			return vdls;
//		}
//
//
//		public StatementList getOutStatementList(ImmutableList<OutputExpression> oExps){
//			StatementList cStms = new StatementList();
//			for(OutputExpression oExp : oExps)							
//				cStms.add(getSendTokenStm(oExp));
//			return cStms;
//		}
//		public CStatement getSendTokenStm(OutputExpression oExp){
//			ExpList exps = new ExpList();
//			Exp rex;
//			ArrayList<Boolean> olds = new ArrayList<Boolean>();
//
//			//			System.out.println("name " + name);
//			for(Expression ex : oExp.getExpressions()){
//				isOld=false;
//				exps.add((Exp)ex.accept(this,null));
//				olds.add(isOld);
//				System.out.println(" getSendTokenStm _____000000000_____" + isOld);
//
//			}
//			if(oExp.getRepeatExpr()!=null){
//				System.out.println("Output repeat expression");
//				rex=(Exp)oExp.getRepeatExpr().accept(this,null);					
//			}
//			else
//				rex=new IntegerLiteral(0);
//			return (new SendToken( new PortHH(null,new Identifier (oExp.getPort().getName())),exps,olds,rex));
//
//		}
//		public StatementList getConsumeTokenStms(ImmutableList<InputPattern> ips, boolean read ){
//		StatementList cinStms = new StatementList();
//		int n=(int) (100* Math.random());
//		for(InputPattern ip: ips){
//			Exp ex;
//			if(ip.getRepeatExpr() != null){
//				System.out.println(" repeat Expre in input  ###reapeat");
//				ex= (Exp)ip.getRepeatExpr().accept(this,null);
//			}
//			else
//				ex = new IntegerLiteral(1);
//
//		//	FormalList is = new FormalList();
//			java.util.ArrayList<Identifier> ids = new java.util.ArrayList<Identifier>();
//			for(String id : ip.getVariables())
//				if(id.matches("_"))
//					//is.add(new Formal(null,new Identifier("temp_" +n)));
//					ids.add(new Identifier("temp_" +n));
//				else 
//		//			is.add(new Formal(null,new Identifier(id)));
//					ids.add(new Identifier(id));
//
//			if(read)
//		//		cinStms.add(new ReadToken(new PortHH(null, new Identifier(ip.getPort().getName().toString())),is,ex));
//				cinStms.add(new ReadToken(new PortHH(null, new Identifier(ip.getPort().getName().toString())),ids,ex));
//			else{
//				if(ip.getPort().getName() != null){
//					cinStms.add(new ConsumeToken(new PortHH(null, new Identifier(ip.getPort().getName().toString())),ids,ex));
//					//System.out.print("the port "+ip.getPortname().toString());
//				}
//				else{
//					//System.out.print("where is the port ");
//
//					cinStms.add((new ConsumeToken(new PortHH(null, new Identifier("#"+"what")),ids,ex)));
//
//				}
//				
//				getPortList(actor.getInputPorts());
//				getPortList(actor.getOutputPorts());
//				 
//
//			}
//
//		}
//
//		return cinStms;
//	}
//		
//		private PortList getMapPorts(ImmutableList<InputPattern> ips) {
//
//			PortList ports = new PortList();
//			
//			for(InputPattern p : ips)
//				ports.add(new PortHH(null, new Identifier(p.getPort().getName())));
//
//
//			return ports;
//		}
//		
//		private PortList getOutputPorts(ImmutableList<OutputExpression> outexp) {
//
//			PortList ports = new PortList();
//
//			for(OutputExpression p : outexp)
//				ports.add(new PortHH(null, new Identifier(p.getPort().getName())));
//
//
//			return ports;
//		}
//
//		private ExpList getOutputConditions(ImmutableList<OutputExpression> oexs){
//		ExpList tempExps         =    new ExpList();	
//		for(OutputExpression oex: oexs){
//			ExpList exs2 = new ExpList();
//			exs2.add(new PortHH(null, new Identifier(oex.getPort().getName())));
//			if(oex.getRepeatExpr()!=null)
//				exs2.add((Exp)oex.getRepeatExpr().accept(this,null));
//			else
//				exs2.add(new IntegerLiteral(1));
//
//			
//			tempExps.add(new FunctionCall(new IdentifierExp("TestOutputPort"),exs2));
//		
//		}
//         return tempExps;
//		}
//		
//		private ExpList getInputConditions(ImmutableList<InputPattern> ips) {
//			ExpList tempExps         =    new ExpList();		
//			for(InputPattern p : ips){
//				ExpList exs = new ExpList();
//				for(String id : p.getVariables())
//					hsGuardVars.add(id);
//								
//				exs.add(new PortHH(null, new Identifier(p.getPort().getName())));
//				if(p.getRepeatExpr()!=null)
//					exs.add((Exp)p.getRepeatExpr().accept(this,null));
//				else
//					exs.add(new IntegerLiteral(1));
//			    tempExps.add( new FunctionCall(new IdentifierExp("TestInputPort"),exs));		
//			}
//			return tempExps;
//
//		}
//		
//		
//		private ActionFiringCond getFiringConditions(int transNo){ 
//					
//			ImmutableList<Expression> guards =transitions.get(transNo).getGuards();
//			ImmutableList<InputPattern> ips =transitions.get(transNo).getInputPatterns();
//			ImmutableList<DeclVar> varDecls = transitions.get(transNo).getVarDecls();
//			
//			VarDeclList tempVar      =    new VarDeclList();
//			StatementList tempStms   =    new StatementList();
//			ExpList tempExps         =    new ExpList();
//			
//			HashSet<String> sVars = new HashSet<String>();
//		//	Exp ex =null;
//			hsGuardVars.clear();
//			hmGuardVars.clear();
//			for(VarDecl vr:getInputPortVars(transitions.get(transNo).getInputPatterns())){
//				if(vr instanceof VarDeclSimp)
//					hsGuardVars.add(((VarDeclSimp) vr).i.s);
//				else
//					hsGuardVars.add(((VarDeclComp) vr).i.s);
//			}
//			
//			for(DeclVar dl:  varDecls)
//				hsGuardVars.add(dl.getName());
//
//			
//			for(Expression guard : guards){
//				
//	
//					bCollectVars=transNo;
//
//					tempExps.add((Exp) guard.accept(this, null));
//					varList.clear();
//					//					System.out.print(instName);
//					for(InputPattern p : ips){
//						for(String id : p.getVariables())
//							if(hmGuardVars.containsKey(id)){
//								hmGuardVars.put(id,false);
//				             	if(p.getRepeatExpr()!=null)
//				             		varList.add(new VarDeclComp(
//				             				new ListType( new BooleanType(),(Exp)p.getRepeatExpr().accept(this,null)),
//				             				new Identifier(id),null, true));
//				             	else
//				             		varList.add(new VarDeclSimp(new BooleanType(),new Identifier(id),null,true));
//
//							}
//					}
//					while(hmGuardVars.containsValue(true) ){
//						for(DeclVar dl:  varDecls)
//							if(hmGuardVars.containsKey(dl.getName()))
//								if(hmGuardVars.get(dl.getName())){
//									dl.accept(this,null);
//									hmGuardVars.put(dl.getName(),false);
//								}
//						System.out.println("1");
//					}
//
//					bCollectVars = transNo;
//					//if(varList.size()>0)
//					{
//						Collections.reverse(varList);
//						//		stms.addAll(varList);
//						for(InputPattern p : ips)
//							for(String id : p.getVariables()){
//								if(hmGuardVars.containsKey(id)){
//									for(VarDecl vd :varList){
//										String vdi;
//										boolean isSimp=false;
//										Type t;
//										if(vd instanceof VarDeclComp){
//											vdi=((VarDeclComp)vd).i.s;
//											t=((VarDeclComp)vd).t;
//										}
//										else{
//											isSimp=true;
//											vdi=((VarDeclSimp)vd).i.s;
//											t=((VarDeclSimp)vd).t;
//										}
//										if(vdi.equals(id)){
//				                            vdi=vdi+"_ac"+transNo;
//											if(!sVars.contains(vdi)){
//												if(isSimp)
//													tempVar.add(new VarDeclSimp(t, new Identifier(vdi),null, false));
//												else
//													tempVar.add(new VarDeclComp(t, new Identifier(vdi),null, false));
//												sVars.add(vdi);
//											}
//											
//											
//											java.util.ArrayList<Identifier>  is = new java.util.ArrayList<Identifier>();
//											
//												if(id.matches("_"))
//													is.add(new Identifier("temp_" +100* Math.random()));
//												else 
//													is.add(new Identifier(id+"_ac"+transNo));
//												if(p.getRepeatExpr()!=null)
//													tempStms.add(new ReadToken(new PortHH(null, new Identifier(p.getPort().getName())),is,
//															(Exp)p.getRepeatExpr().accept(this, null)));
//												else
//													tempStms.add(new ReadToken(new PortHH(null, new Identifier(p.getPort().getName())),is,
//															new IntegerLiteral(1) ));
//
//											
//										}
//
//									}
//								}
//							}
//
//						for(VarDecl vd: varList){
//							Type t;
//							Exp varex;
//							String vdi="";
//							boolean isSimp=false;
//							if(vd instanceof VarDeclComp){
//								vdi=((VarDeclComp)vd).i.s;
//								varex=((VarDeclComp)vd).e;										
//								t=((VarDeclComp)vd).t;
//							}
//							else{
//								isSimp=true;
//								varex=((VarDeclSimp)vd).e;
//								vdi=((VarDeclSimp)vd).i.s;
//								t=((VarDeclSimp)vd).t;
//
//							}
//                            vdi=vdi+"_ac"+transNo;
//
//
//							if(varex!=null)
//								tempStms.add(new Assign( new Identifier(vdi),varex));
//
//							if(!sVars.contains(vdi)){
//								if(isSimp)
//									tempVar.add(new VarDeclSimp(t, new Identifier(vdi),null, false));
//								else
//									tempVar.add(new VarDeclComp(t, new Identifier(vdi),null, false));
//								sVars.add(vdi);
//							}
//
//						}
//					}
//					bCollectVars=-1;
//			} 
//
//			
//			
//			
//			
//
//				VarDeclList vdls= new VarDeclList();
//				vdls.addAll(tempVar);
////
////				StatementList stmls = new StatementList();
////				stmls.addAll(tempStms);
////				predicateExp.add(ex);				
//				predicateVars.add(vdls);
////				predicateStms.add(stmls);
//				
//                 
//				return new ActionFiringCond(tempVar, tempStms,tempExps);
//
//
//
//		}
//
//		public CalC_AST generateCast(String entName) {
//			translateTransitons();
//
//			//TODO test outputport
//			actorName = entName;
//			bCollectVars = -1;
//			System.out.println("................. size of varList" +varList.size());
//			parmList.clear();
//			for( ParDeclValue pdv : actor.getValueParameters()){
//				parmList.add(new Formal(getType( pdv.getType()), new Identifier(pdv.getName())));
//
//			}
//			varList.clear();
//			for(DeclVar dl: actor.getVarDecls()){
//				dl.accept(this,null);
//				if(dl.getIdentifier()!=null)
//					System.out.println(dl.getName() + " What is going on");
//
//			}
//			//		varList.add(new VarDeclSimp(new BooleanType(),new Identifier(instName+"_isWaitting"),null,true));
//					varList.add(new VarDeclSimp(new IntegerType(32),new Identifier("token_count"),new IntegerLiteral(0),true));
//			cpm = new SEQ_Actor( new IdentifierType(instName) ,new Identifier (entName),
//					parmList,
//					getPortList(actor.getInputPorts()),
//					getPortList(actor.getOutputPorts()),
//					transferList(varList),
//					transferList(guardList),
//					actionList,
//					funList,
//					procList,
//					getActionScheduler(),
//					inputs,
//					outputs
//					);
//
//			CalC_AST ast = new CalC_AST(cpm);
//			return ast;
//		}
//		private ActionScheduler getActionScheduler() {
//			
//			HashMap<String, ArrayList<String>> orderedActions = new HashMap<String, ArrayList<String>>();
//			HashMap<String, ArrayList<String>> actionSets = new HashMap<String, ArrayList<String>>();
//            
//			
//			HashMap<Integer,String> actionList = new HashMap<Integer,String>();
//			
//			ArrayList< Integer> untagged = new ArrayList< Integer>();
//			ArrayList<Integer> tagged = new ArrayList< Integer>();
//			ArrayList<Integer> fsmAction = new ArrayList<Integer>();	
//			
//			HashSet<String> fsmActions = new HashSet<String>();
//			StatementList actionSchedulerStms =	new StatementList();			
//			StatementList schedulerStms =	new StatementList();			
//			VarDeclList acScVars  = new VarDeclList();
//			
//			actionSets = getActionSets();			
//			
//			for(VarDeclList evd:predicateVars)
//				acScVars.addAll(evd);
//			if(actor.getScheduleFSM()!=null)
//				fsmActions     = getActionInFSM(actor.getScheduleFSM());
//            if(actor.getPriorities()!=null)
//            	orderedActions = getActionPriority(actor.getPriorities());
//			
//			for(int iac=0;iac<actor.getActions().size();iac++){
//				  Action ac = actor.getActions().get(iac);
//				if(ac.getTag()==null){
//					actionList.put(iac,"");
//					continue;
//				}
//				
//                actionList.put(iac, ac.getTag().toString());
//			}
//				
//				if(fsmActions.contains(actionTag))
//					fsmAction.add(iac);
//				else if( actionTag.indexOf(".") !=-1){
//						if(fsmActions.contains(actionTag.substring(0, actionTag.indexOf("."))))
//							fsmAction.add(iac);
//						tagged.add(iac);
//				}
//				else					
//					tagged.add(iac);			
//			}
//
//		//	actionSchedulerStms.add(new Assign(new Identifier(instName+"_isWaitting"),new BoolLiteral(true)));
//
//			
//			actionSchedulerStms.addAll(getActionCalls(untagged));
//			
//			if(orderedActions.size()>0)
//				tagged=orderedActions(orderedActions,tagged);
//				
//			actionSchedulerStms.addAll(getActionCalls(tagged));
//			
//			actionSchedulerStms.addAll(getActionCalls(fsmAction));
//			
//	//		actionSchedulerStms.add(new If(new Equal(new IdentifierExp(instName+"_isWaitting"),new BoolLiteral(true)),new StatementCall(new IdentifierExp("wait"),new ExpList()),null));
//			
//			schedulerStms.add(new While(new LessThan(new IdentifierExp("token_count"),new IdentifierExp("IN_BUFFER_SIZE")),new Block(actionSchedulerStms)));
//						
//			return  new ActionScheduler(new Identifier("scheduler_"+instName), 
//					getPortList(actor.getInputPorts()),
//					getPortList(actor.getOutputPorts()),
//					acScVars,
//					actionSchedulerStms//schedulerStms		
//					);
//		}
//
//
//		private HashMap<String, ArrayList<String>> getActionSets() {
//			HashMap<String, ArrayList<String>> actionSets = new HashMap<String, ArrayList<String>>();
//			for(String name: actionsNames){
//                String new_name;
//                
//				if(name.indexOf('.')>0){
//					int i2 =name.indexOf('.');
//					
//					while(i2>0){						
//						ArrayList<String> names = new ArrayList<String>();
//
//					new_name  = name.substring(0,i2);
//					
//					if(actionSets.containsKey(new_name)){
//						names = actionSets.get(new_name);
//						if(!names.contains(name))
//							names.add(name);
//						actionSets.put(new_name,names);
//					}
//					else{	
//						names.add(name);
//					     actionSets.put(new_name, names);
//					}
//					i2=name.indexOf('.', i2+1);					
//				}
//					
//				}
//				else{
//					ArrayList<String> names = new ArrayList<String>();
//					if(actionSets.containsKey(name)){
//						names = actionSets.get(name);
//						if(!names.contains(name))
//							names.add(name);
//						actionSets.put(name,names);
//					}
//					else{	
//						names.add(name);
//					     actionSets.put(name, names);
//					}
//				}
//			}
//			return actionSets;
//		}
//
//
//		private ArrayList<Integer> orderedActions(HashMap<String, ArrayList<String>> orderedActions, ArrayList<Integer> actions) {
//			//TODO Ordere actions accordining to their priority
//			/*
//			 * */
//			
//			
//			return actions;
//		}
//
//
//		private StatementList getActionCalls(ArrayList<Integer>  actions) {
//			
//			StatementList actionCalls = new StatementList();
////			if(instName.equals("Separate"))
////				System.out.print("sssssssss");
//			String pref = "";//actorName+"_";		
//			
//			for( Integer iac: actions){
//				
//				StatementList callSts = new StatementList();
//				CStatement inputSt, outputSt, condSt;
//				ExpList exs = new ExpList();
//				exs.addAll(actionList.get(iac).input);
//				exs.addAll(actionList.get(iac).output);
//				callSts.add(new StatementCall(new IdentifierExp(pref+actionList.get(iac).i.s),exs));
//		//		callSts.add(new Assign(new Identifier(instName+"_isWaitting"),new BoolLiteral(false)));
//				actionCalls.addAll(actionList.get(iac).afc.stms);
//			    if(actionList.get(iac).afc.conds.size()>0)
//			    	condSt =new If(connectConditions(actionList.get(iac).afc.conds), new Block(callSts),null);// new StatementCall(new IdentifierExp("wait"),new ExpList()));
//			    else
//			    	condSt = new Block(callSts);
//			    if(actionList.get(iac).output.size()>0)
//			    	outputSt = new If(connectConditions(getOutputConditions(actor.getActions().get(iac).getOutputExpressions())),condSt,null);// new StatementCall(new IdentifierExp("wait"),new ExpList()));
//			    else
//			    	outputSt = condSt;
//			    if(actionList.get(iac).input.size()>0)
//			    	inputSt =new If(connectConditions(getInputConditions(actor.getActions().get(iac).getInputPatterns())),outputSt, null);// new StatementCall(new IdentifierExp("wait"),new ExpList()));			    	
//			    else
//			    	inputSt = outputSt;
//			    
//			    actionCalls.add(inputSt);
//			    actionCalls.add(condSt);
//			}
//
//			
//			return actionCalls;
//		}
//
//
//		private Exp connectConditions(ExpList exs) {
//			
//			if(exs.size()>0){
//				if(exs.size()==1)
//					return exs.get(0);
//				Exp ex= exs.get(0);
//				exs.remove(0);
//				return new And(ex,connectConditions(exs));
//			}
//			
//			
//			
//			
//			
//			return null;
//		}
//
//
//		private HashMap<String, ArrayList<String>> getActionPriority(ImmutableList<ImmutableList<QID>> priorities) {
//			HashMap<String, ArrayList<String>> orderedActions = new HashMap<String, ArrayList<String>>();
//		/*	ArrayList<Integer> orderedActionInt = new ArrayList<Integer>();
//			ArrayList<String> orderedActionName = new ArrayList<String>();
//			ArrayList<Integer> actionIndex = new ArrayList<Integer>();
//			
//			String name;
//			if(instName.equals("Clip"))
//			for(ImmutableList<QID> qids:priorities){	
//				if(orderedActionName.size()<0){	
//					for(QID qid:qids){
//						name = qid.toString();					
//						if(actionsNames.contains(name)){
//							orderedActionName.add(name);
//						}else
//							for(String acName: actionsNames){
//								if(acName.startsWith(name + "."))
//									orderedActionName.add(acName);
//							}
//					}
//				}
//				else{
//					//find index of each action
//					for(QID qid:qids){
//						actionIndex.add(orderedActionName.indexOf());
//							
//				}
//			}
//			}
//			
//				for(QID qid:qids){
//					name = qid.toString();
//					if(orderedActionName.size()>0){
//						if(actionsNames.contains(name)){
//							if(!orderedActionName.contains(name))
//								orderedActionName.add(name);
//							else{
//								
//							}
//
//					}
//					else{
//						if(actionsNames.contains(name)){
//							orderedActionName.add(name);
//						}else
//							for(String acName: actionsNames){
//								if(acName.startsWith(name + "."))
//									orderedActionName.add(acName);
//							}
//					}
//					System.out.print(" " + qid.toString());
//				}
//				System.out.println();
//				
//			}
//			
//			for(Entry<String, ArrayList<String>> or:orderedActions.entrySet()){
//				System.out.print("\n"+or.getKey() + " << ");
//				for(String s: or.getValue())
//					System.out.print(s +"  " );
//				
//			}
//			
//			*/
//			return orderedActions;
//		}
//
//
//		private HashSet<String> getActionInFSM(ScheduleFSM fsm) {
//			HashSet<String> fsmAction = new HashSet<String>();
//			for(Transition t: fsm.getTransitions())
//				for(QID q: t.getActionTags())
//					fsmAction.add(q.toString());
//
//			return fsmAction;
//		}
//
//
//		public ListOfListCompGen getGenrateFilter(ImmutableList<GeneratorFilter> immutableList){
//			ListOfListCompGen  lcgfs = new ListOfListCompGen();
//			for(GeneratorFilter gf :immutableList){
//				ExpList fils= new ExpList();
//				VarDeclList vars= new VarDeclList();
//
//				if(gf.getFilters()!=null){
//					System.out.println("getFilters size " + gf.getFilters().size());
//					for(Expression egf:gf.getFilters())
//						fils.add((Exp)egf.accept(this,null));
//				}
//
//				if(gf.getVariables()!=null){
//					System.out.println(" gen getVariables " + gf.getVariables().size());
//					for(DeclVar dvr: gf.getVariables()){
//						vars.add(new VarDeclSimp(getType(dvr.getType()),new Identifier(dvr.getName()),null,true));
//						System.out.println(" gen getVar:- " + dvr.getName());
//					}
//
//				}
//				lcgfs.add(new ListCompGen (vars,(Exp)gf.getCollectionExpr().accept(this,null),fils));
//			}
//
//
//
//			return lcgfs;
//		}
//
//		public GuardDeclList transferList(GuardDeclList vars){
//			GuardDeclList vs =new GuardDeclList();
//			for(GuardDecl v:vars)
//				vs.add(v);				
//			return vs;
//		}
//		public VarDeclList transferList(VarDeclList vars){
//			VarDeclList vs =new VarDeclList();
//			for(VarDecl v:vars)
//				vs.add(v);				
//			return vs;
//		}
//
//		public StatementList getCStatementList(ImmutableList<Statement> immutableList){
//			StatementList cStms = new StatementList();
//			for(Statement calstm : immutableList){
//				System.out.println("\n Statem In block............................... " + calstm.toString());
//				cStms.add((CStatement)calstm.accept(this,null));
//			}
//			return cStms;
//		}
//
//		public  PortList  getPortList(List<PortDecl> ports){
//			PortList ps = new PortList();
//			for (PortDecl pd : ports)
//				ps.add( new PortHH(getType(pd.getType()),new Identifier (pd.getName())));	
//
//
//			return ps;
//		}
//
//		public Type getType(TypeExpr t){
//			if(t==null)
//				return null;
//			Map<String, Exp> emval = new HashMap<String,Exp>();
//			Map<String, Type> emtype = new HashMap<String,Type>();
//			if(t.getValueParameters() != null){
//				ImmutableList<ImmutableEntry<String, Expression>> mval =  t.getValueParameters();
//				for(ImmutableEntry<String, Expression> entry : mval){
//					System.out.println("------------key ---------->"+entry.getKey() + ", value "+entry.getValue()+" _length_ "+ mval.size());
//
//					emval.put(entry.getKey(), (Exp)entry.getValue().accept(this,null));
//
//				}
//
//			}
//			if(t.getTypeParameters() != null){
//				ImmutableList<ImmutableEntry<String, TypeExpr>> mtype = t.getTypeParameters();
//				for(ImmutableEntry<String, TypeExpr> entry : mtype){
//					System.out.println(".............key <::::::::::> "+entry.getKey() + ", value "+entry.getValue().getName()+" -length- "+ mtype.size());
//					emtype.put(entry.getKey(), getType(entry.getValue()));
//				}
//
//			}
//
//			System.out.println(" type name " + t.getName());
//
//			if(t.getName().equals("bool")){return new BooleanType();}
//			else if (t.getName().equals("int")){
//				Exp len=null;
//
//				if(emval != null){
//					System.out.println(" emval.size " + emval.size());
//					for(Map.Entry<String, Exp> entry : emval.entrySet())
//						if(entry.getKey().matches("size"))
//							len=entry.getValue();		
//				}
//				int isize=0;
//				if(len instanceof IntegerLiteral)
//					isize=((IntegerLiteral)len).i;
//				return (new IntegerType(isize));
//
//			}
//			else if(t.getName().equals("List")){
//
//				Exp len=null;
//				Type tt=null;
//				if(emval != null){
//					System.out.println(" emval.size " + emval.size());
//					for(Map.Entry<String, Exp> entry : emval.entrySet())
//						if(entry.getKey().matches("size"))
//							len=entry.getValue();		
//				}
//
//				if(emtype != null){
//					System.out.println(" emtype.size " + emtype.size());
//					for(Map.Entry<String, Type> entry : emtype.entrySet())
//						tt=entry.getValue();
//
//
//
//				}
//				return(new ListType(tt,len));}
//			else return null;
//
//		}
//
//		public Object visitDeclEntity(DeclEntity d, Object p) {
//
//			for( ParDeclValue pdv : d.getValueParameters()){
//				System.out.println(" getValueParameters   " + pdv.getName());
//				//if( == null)
//				parmList.add(new Formal(getType( pdv.getType()), new Identifier(pdv.getName())));
//				//	varList.add(new VarDecl() /*new ParameterType()*/,new Identifier(pdv.getName()),null));
//
//			}
//			for(DeclType  dt : d.getTypeDecls()){
//				System.out.println("getType "+ dt.getName());
//				dt.accept(this,p);
//			}
//			for(DeclVar dv : d.getVarDecls()){
//				System.out.println("getVarDecls "+ dv.getName() ); // variable declaration list 
//
//
//				if(dv.getType()!=null)
//					System.out.println("  dv.getType() "+ dv.getType().getName() +" _is the type"); // variable declaration list 
//				dv.accept(this,p);
//			}
//			for(ParDeclType pdt : d.getTypeParameters())
//				System.out.println("//getTypeParam "+pdt.getName());
//			return null;
//		}
//
//		@Override
//		public Object visitDeclType(DeclType d, Object p) {
//
//			System.out.println("\n visitDeclType ");
//			//d.accept(this);
//
//			return null;
//		}
//
//		@Override
//		public Object visitDeclVar(DeclVar dv, Object p) {
//
//
//			System.out.println("\nvisitDeclVar name "+ dv.getName() +" kind  " + dv.getKind() + "  -  ");
//			if(dv.getInitialValue()!=null){
//				if (dv.getInitialValue() instanceof ExprLambda){
//
//					System.out.println("ExprLambda " + dv.getName());
//					dv.getInitialValue().accept(this,dv.getName());
//					//					funList.add((FunctionDecl) dv.getInitialValue().accept(this,p));
//					//					funList.add(processExprLambda(dv.getName(), (ExprLambda) dv.getInitialValue()));
//				}
//				else if(dv.getInitialValue() instanceof ExprProc)
//				{
//					dv.getInitialValue().accept(this,dv.getName());
//				}else{	
//					Type ttemp =getType(dv.getType());
//					if(ttemp instanceof ListType)
//						varList.add(new VarDeclComp(ttemp, new Identifier(dv.getName()),(Exp)dv.getInitialValue().accept(this,p),dv.isAssignable()));
//					else
//						varList.add(new VarDeclSimp(ttemp, new Identifier(dv.getName()),(Exp)dv.getInitialValue().accept(this,p),dv.isAssignable()));
//				}
//			}
//			else
//				varList.add(new VarDeclSimp(getType(dv.getType()), new Identifier(dv.getName()),null,dv.isAssignable()));	
//			System.out.println("END visitDeclar");
//
//			return null;
//		}
//
//		@Override
//		public Object visitExprApplication(ExprApplication e, Object p) {
//			System.out.println("\nvisit  ExprApplication");
//			ImmutableList<Expression> exarg = e.getArgs();
//			ExpList cexs = new ExpList();
//
//			System.out.println("\n  e.getargs over ExprApplication " + e.getFunction().toString());
//			e.getFunction().accept(this,p);
//			if (e.getFunction() instanceof ExprVariable){
//				System.out.println("\n  instanceof ExprVariable");
//
//				ExprVariable op= (ExprVariable)e.getFunction();		
//				if(op.getVariable().getName().equals("bitand"))      return    new   BitAnd((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//				else if(op.getVariable().getName().equals("lshift")) return 	new LeftShift((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//				else if(op.getVariable().getName().equals("rshift")) return    new   RightShift((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//				else if(op.getVariable().getName().equals("bitor"))  return    new   BitOr((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//				else if(op.getVariable().getName().equals("bitxor"))  return    new   BitXOr((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//				else if(op.getVariable().getName().equals("Integers"))  return    new   GenIntegers((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//				else if(op.getVariable().getName().equals("bitnot"))  return    new   BitNot((Exp)exarg.get(0).accept(this,null));
//
//				else{
//					for(Expression calex : exarg)
//						cexs.add((Exp)calex.accept(this,p));
//
//					return new FunctionCall((IdentifierExp)e.getFunction().accept(this,p),cexs);
//				}
//
//			}
//			else if (e.getFunction() instanceof ExprField){
//				for(Expression calex: exarg)
//					cexs.add((Exp)calex.accept(this,p));
//				return new FunctionCall((IdentifierExp)e.getFunction().accept(this,p),cexs);
//
//			}
//			/*	ExprField ee =(ExprField) e.getFunction();
//				ExpList es = new ExpList();		
//				ee.g
//				if(ee.getName().equals("nextBoolean") )				
//					return new FunctionCall(new IdentifierExp("Rand_" + ee.getName()),es);
//				else if(ee.getName().equals("nextInt") )
//				{
//				//	es.add((Exp)exarg.get(0).accept(this,p) );
//					return new FunctionCall(new IdentifierExp("Rand_" + ee.getName()),es);
//				}
//				else if(ee.getName().equals("nextDouble") )
//				{
//						return new FunctionCall(new IdentifierExp("Rand_" + ee.getName()),es);
//				}
//				else
//					return new FunctionCall(new IdentifierExp(ee.getName()),es);
//			}
//			//return new FunctionCall(new IdentifierExp(ee.getName()),es);*/
//			System.err.println("visitExprApplication " +e.toString());
//			System.exit(0);
//			return null;
//
//		}
//
//		@Override
//		public Object visitExprBinaryOp(ExprBinaryOp e, Object p) {
//			ImmutableList<String>      s = e.getOperations();
//			ImmutableList<Expression> exarg = e.getOperands();
//			System.out.println("visitExprBinary " + s.get(0));
//			if(exarg.size()>2)
//				System.out.println("Not binary operatoorrrrrrrr in Actor "+instName);
//
//
//			if(s.get(0).equals("+"))
//				if(exarg.get(0) instanceof ExprList || exarg.get(1) instanceof ExprList)
//					return    new ListConc((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//
//				else if(exarg.get(0) instanceof ExprLiteral || exarg.get(1) instanceof ExprLiteral){
//					if(exarg.get(0) instanceof ExprLiteral)
//						if(((ExprLiteral)exarg.get(0)).getKind().equals(Kind.String))
//							return    new StringConc((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//					if(exarg.get(1) instanceof ExprLiteral)
//						if(((ExprLiteral)exarg.get(1)).getKind().equals(Kind.String))
//							return    new StringConc((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//					return    new Plus((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//
//				}
//				else
//					return    new Plus((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//
//
//			else if(s.get(0).equals("-"))   return    new   Minus((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals("*"))   return    new   Times((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals("/"))   return    new   Divide((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals("!="))  return    new   NotEqual((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals("="))   return    new   Equal((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals(">"))   return    new   GreaterThan((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals(">="))  return    new   GreaterOrEqual((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals("<"))   return    new   LessThan((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals("<="))  return    new   LessOrEqual((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals("or"))  return    new   Or((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals("and")) return    new   And((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals(".."))  return    new   GenIntegers((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals("&"))   return    new   BitAnd((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals("<<"))  return 	  new   LeftShift((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals(">>"))  return    new   RightShift((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals("|"))   return    new   BitOr((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else if(s.get(0).equals("^"))   return    new   BitXOr((Exp)exarg.get(0).accept(this,null),(Exp)exarg.get(1).accept(this,null));
//			else{
//				System.err.println(s.get(0)+" ExprBinary not found");	
//				return null;
//			}
//		}
//
//
//
//		@Override
//		public Object visitExprIf(ExprIf e, Object p) {
//
//			System.out.println("\nvisit  ExprIf");
//			return  new IfExp((Exp) e.getCondition().accept(this,null), (Exp) e.getThenExpr().accept(this,null),(Exp)e.getElseExpr().accept(this,null));
//
//
//
//		}
//
//
//		@Override
//		public Object visitExprIndexer(ExprIndexer e, Object p) {
//			System.out.println("\nvisit  ExprIndexer");
//			Exp exs=null;	
//
//			if(e.getIndex() != null)
//				exs= (Exp)e.getIndex().accept(this,p);
//			else
//				return (Exp)e.getStructure().accept(this,p);
//			return new ExpIndexer((Exp)e.getStructure().accept(this,p),exs);
//		}
//
//		@Override
//		public Object visitExprInput(ExprInput e, Object p) {
//
//			System.out.println("\nvisit  ExprInput");
//
//
//			return null;
//		}
//
//		@Override
//		public Object visitExprLambda(ExprLambda e, Object p) {
//			String name="_lambda";
//			if(p instanceof String)
//				name=(String)p;
//			if(instName.equals("DCPred") && name.equals("saturate"))
//				System.out.println("variabls how " +e.getTypeParameters().size());
//			FormalList fls = new FormalList();
//			VarDeclList vds = new VarDeclList();
//			Exp ret_ex;
//			if(e.getValueParameters() !=null)
//				for(ParDeclValue pm: e.getValueParameters()){
//					System.out.println(" name :" +pm.getName());
//					if(pm.getType()!=null)
//						System.out.println(" Type :"  +pm.getType().getName());
//
//					fls.add(new Formal (getType(pm.getType()),new Identifier(pm.getName())));
//
//				}
//
//			if(e.getBody() instanceof ExprLet){
//				vds=(VarDeclList)e.getBody().accept(this,"var");
//				ret_ex = (Exp)e.getBody().accept(this,"exp");
//			}
//			else
//				ret_ex = (Exp)e.getBody().accept(this,p);
//
//			if(e.getTypeParameters()!=null)
//				for(ParDeclType pdt: e.getTypeParameters()){
//					System.out.println(pdt.getName());
//				}
//
//			funList.add(new FunctionDecl(null,
//					new Identifier(name),
//					fls,//getparm
//					vds,//getvardeclr
//					ret_ex)
//					);
//
//
//			return new FunctionCall(new IdentifierExp(name),new ExpList());
//		}
//
//		@Override
//		public Object visitExprLet(ExprLet e, Object p) {
//
//			System.out.println("\nvisit  ExprLet");
//			if(p instanceof String){
//				if(p.equals("var")){
//					VarDeclList vds = new VarDeclList();
//					for(DeclVar decvar: e.getVarDecls()){
//						if(decvar.getInitialValue()!=null){
//							Type ttemp=getType(decvar.getType());
//							if( ttemp instanceof ListType)
//								vds.add(new VarDeclComp(ttemp, new Identifier(decvar.getName()),(Exp)decvar.getInitialValue().accept(this,null),decvar.isAssignable()));
//							else
//								vds.add(new VarDeclSimp(ttemp, new Identifier(decvar.getName()),(Exp)decvar.getInitialValue().accept(this,null),decvar.isAssignable()));
//						}else
//							vds.add(new VarDeclSimp(getType(decvar.getType()), new Identifier(decvar.getName()),null,decvar.isAssignable()));
//					}	
//					return vds;
//				}
//				else
//					return e.getBody().accept(this,p);
//
//			}
//			else
//				return e.getBody().accept(this,p);
//		}
//
//		@Override
//		public Object visitExprList(ExprList e, Object p) {
//
//			System.out.println("\nvisit  ExprList");
//			ExpList cexs = new ExpList();
//			if(e.getElements()!=null){
//				System.out.println(" getElements length " + instName+ e.getElements().size());
//				for(Expression exp: e.getElements())
//					cexs.add((Exp)exp.accept(this, p));
//
//			}
//			System.out.println("_______________I am telling you___________");			
//
//			if(e.getGenerators()!=null){
//				return new ListComprehension(cexs,getGenrateFilter(e.getGenerators()));
//
//				//col_exp = new ListComprehension(cexs,null, VarDeclList avars, Exp acolExp, ExpList afils)
//			}
//			else
//				return new ListComprehension(cexs,null);
//
//
//
//		}
//
//		@Override
//		public Object visitExprLiteral(ExprLiteral e, Object p) {
//
//			System.out.println("visit_ExprLiteral  = " + e.getKind());
//			//	Null("Null"), True("True"), False("False"), Char(null), Integer(null), Real(null), String(null);
//
//			switch ( e.getKind().ordinal()+1){
//			case 1:
//				//col_exp = 
//				System.out.println("  ExprLiteral   1 " + e.getKind());
//				break;
//			case 2:
//				System.out.println("  ExprLiteral   2 " + e.getKind());
//				return  new BoolLiteral(true);
//
//			case 3:
//				System.out.println("  ExprLiteral   3 " + e.getKind());
//				return  new BoolLiteral(false);
//
//			case 4:
//				System.out.println("  ExprLiteral   4 " + e.getKind());
//				return new CharLiteral(e.getText().charAt(0));
//
//			case 5:
//				if(e.getText().startsWith("0x"))
//					return  new IntegerLiteral(Integer.parseInt(e.getText().substring(2),16));
//				else
//					return  new IntegerLiteral(Integer.parseInt(e.getText()));
//
//			case 6:
//				System.out.println("  ExprLiteral   6" + e.getKind());
//				return  new DoubleLiteral(Double.parseDouble(e.getText()));				
//			case 7:
//				System.out.println("  ExprLiteral   7" + e.getKind());
//				return new StringLiteral(e.getText());
//			}
//			return null;
//		}
//
//		@Override
//		public Object visitExprMap(ExprMap e, Object p) {
//			System.out.println("\nvisit  ExprMap");
//
//			return null;
//		}
//
//		@Override
//		public Object visitExprProc(ExprProc e, Object p) {
//
//			System.out.println("\nvisit  ExprProc ");
//			FormalList fls = new FormalList();
//			VarDeclList vds = new VarDeclList();
//			Exp ret_ex;
//			if(e.getValueParameters() !=null)
//				for(ParDeclValue pm: e.getValueParameters()){
//					System.out.println(" name :" +pm.getName());
//					if(pm.getType()!=null)
//						System.out.println(" Type :"  +pm.getType().getName());
//
//					fls.add(new Formal (getType(pm.getType()),new Identifier(pm.getName())));
//
//				}
//			/*for(ParDeclType pdv: e.getTypeParameters()){
//				if(pdv.getType()!=null){
//					Type ttemp=getType(pdv.getType());
//					if( ttemp instanceof ListType)
//						vds.add(new VarDeclComp(ttemp, new Identifier(pdv.getName()),(Exp)pdv.getInitialValue().accept(this,null),decvar.isAssignable()));
//					else
//						vds.add(new VarDeclSimp(ttemp, new Identifier(pdv.getName()),(Exp)pdv.getInitialValue().accept(this,null),decvar.isAssignable()));
//				}else
//					vds.add(new VarDeclSimp(getType(decvar.getType()), new Identifier(decvar.getName()),null,decvar.isAssignable()));
//			}	
//			 */
//
//
//			procList.add(new ProcedureDecl(null, new Identifier((String)p),fls,vds,(CStatement)e.getBody().accept(this,p),null));
//
//			return null;
//		}
//
//		@Override
//		public Object visitExprSet(ExprSet e, Object p) {
//
//			System.out.println("\nvisit  ExprSet");
//			return null;
//
//		}
//		@Override
//		public Object visitExprUnaryOp(ExprUnaryOp e, Object p) {
//
//			System.out.println("Unary Operator " + e.getOperation());
//
//
//			if(e.getOperation().equals("bitnot"))return   new BitNot((Exp) e.getOperand().accept(this,p));
//			else if(e.getOperation().equals("-"))return   new Negate((Exp) e.getOperand().accept(this,p));
//			else if(e.getOperation().equals("#"))return   e.getOperand().accept(this,"_length");
//			// else if(e.getOperation().equals("negate"))return   new Negate((Exp) e.getOperand().accept(this,null));
//			else if(e.getOperation().equals("not"))return   new Not((Exp) e.getOperand().accept(this,p));
//			else return null;
//
//
//		}
//
//		@Override
//		public Object visitExprVariable(ExprVariable e, Object p) {
//
//			System.out.println("\nvisit  ExprVariable "+ e.getVariable().getName());
//			String name = e.getVariable().getName();
//
//			if(name.startsWith("$old$")){
//
//				System.out.println(name +" -- yse it  is old " + name.subSequence(0,5) +" the var name is " + name.substring(5));
//				isOld=true;
//			}
//
//
//			if(bCollectVars !=-1)
//				if(hsGuardVars.contains(name)){
//					hmGuardVars.put(name,true);
//					return new IdentifierExp(name+"_ac"+bCollectVars);
//				}
//			//			if(p instanceof String)
//			//				name=name+(String)p;
//			return new IdentifierExp(name);
//
//
//
//		}
//		public Object visitStmtAssignment(StmtAssignment s, Object p) {
//			LValue left = s.getLValue();
//			if (left instanceof LValueIndexer || s.getExpression() instanceof ExprList){
//				return new ListAssign((Exp)s.getLValue().accept(this,p),new ExpList(),(Exp)s.getExpression().accept(this,p),null);
//			}
//			if(left instanceof LValueField){
//
//			}
//			return new Assign(new Identifier(((LValueVariable)s.getLValue()).getVariable().getName()),(Exp)s.getExpression().accept(this,p));
//
//		}
//
//
//		@Override
//		public Object visitStmtBlock(StmtBlock s, Object p) {
//
//			System.out.println("\nvisit  StmtBlock " + s.getStatements().size());
//
//			return new Block(getCStatementList(s.getStatements()));
//		}
//
//		@Override
//		public Object visitStmtIf(StmtIf s, Object p) {
//			System.out.println("\nvisit StmtIf ");
//			s.getCondition().accept(this,null);
//			System.out.println("\nvisit StmtIf Condition " + s.getCondition().toString());
//
//			if(s.getElseBranch()!=null)
//				return new If((Exp)s.getCondition().accept(this,p),(CStatement)s.getThenBranch().accept(this,p),(CStatement)s.getElseBranch().accept(this,p));
//			else
//				return new If((Exp)s.getCondition().accept(this,p),(CStatement)s.getThenBranch().accept(this,p),null);
//		}
//
//		@Override
//		public Object visitStmtCall(StmtCall s, Object p) {
//			System.out.println("\nvisit  StmtCall "+ instName);
//			ExpList args = new ExpList();
//			for(Expression arg: s.getArgs())
//				args.add((Exp) arg.accept(this,p));
//			if (s.getProcedure().accept(this,p) instanceof IdentifierExp){
//				IdentifierExp iex= (IdentifierExp)s.getProcedure().accept(this,p);
//				if(iex.s.equals("println"))
//					return new Print(args,true);
//				else if (iex.s.equals("print"))
//					return new Print(args,false);
//				else
//					return new StatementCall((IdentifierExp)s.getProcedure().accept(this,p),args);
//			}
//			else
//				return new StatementCall((IdentifierExp)s.getProcedure().accept(this,p),args);
//		}
//
//		@Override
//		public Object visitStmtOutput(StmtOutput s, Object p) {
//
//			System.out.println("\nvisit  StmtOutput");
//			ImmutableList<Expression> amex =s.getValues();
//			ExpList exs= new ExpList();
//			ArrayList<Boolean> olds = new ArrayList<Boolean>();
//			for(int i=0;i<amex.size();i++)
//				exs.add((Exp) amex.get(i).accept(this,p));
//			//			return (new SendToken( new Port(null,new Identifier (outputs.get(instName+"#"+s.getPort().getName()))),							
//			//							exs,olds,new IntegerLiteral(s.getRepeat())));
//			return (new SendToken( new PortHH(null,new Identifier (s.getPort().getName())),							
//					exs,olds,new IntegerLiteral(s.getRepeat())));
//			
//		}
//
//		@Override
//		public Object visitStmtWhile(StmtWhile s, Object p) {
//			System.out.println("\nvisit StmtWhile ");
//			return new While((Exp)s.getCondition().accept(this,p),(CStatement)s.getBody().accept(this,p));		
//		}
//
//		@Override
//		public Object visitStmtForeach(StmtForeach s, Object p) {
//			//For(Exp ainit,Exp acon, Exp astep, CStatement as) 
//			System.out.println("\nvisit  StmtForeach");
//			return null;// new For((CStatement)s.getBody().accept(this,p));
//		}
//
//		@Override
//		public Object visitStmtConsume(StmtConsume s, Object p) {
//			System.out.println("vissit StmtConsume " + instName);
//			java.util.ArrayList<Identifier>  is = new java.util.ArrayList<Identifier>();
//			for(String id : s.getVariabls()){
//				if(id.matches("_"))
//					is.add(new Identifier("temp_" +100* Math.random()));
//				else 
//					is.add(new Identifier(id));
//			}
//
//			return new ConsumeToken(new PortHH(null, new Identifier(s.getPort().getName())),is,
//					new IntegerLiteral(s.getNumberOfTokens()));
//			
//		}
//
//		@Override
//		public Object visitStmtPeek(StmtPeek s, Object p) {
//
//			System.out.println("vissit StmtConsume");
//			java.util.ArrayList<Identifier>  is = new java.util.ArrayList<Identifier>();
//			for(String id : s.getVariabls())
//				if(id.matches("_"))
//					is.add(new Identifier("temp_" +100* Math.random()));
//				else 
//					is.add(new Identifier(id+"_ac"+(Integer)p));
//			return new ReadToken(new PortHH(null, new Identifier(s.getPort().getName())),is,
//					new IntegerLiteral(s.getNumberOfTokens()));
//
//		}
//
//		@Override
//		public Object visitExprField(ExprField e, Object p) {
//			System.out.println("visitExprField");
//			Field f= e.getField();
//			Exp ix= (Exp)e.getStructure().accept(this,null);
//			String s = f.getName();
//			if(ix instanceof IdentifierExp){
//				if(((IdentifierExp)ix).s.equals("Mathexp"))
//					System.out.println(f.getName());
//				s= s+ ((IdentifierExp)ix).s;
//
//			}			
//
//			return new IdentifierExp(s);
//		}
//
//		@Override
//		public Object visitLValueVariable(LValueVariable e,
//				Object p) {
//			return new IdentifierExp(e.getVariable().getName());
//		}
//
//		@Override
//		public Object visitLValueIndexer(LValueIndexer e, Object p) {
//
//			System.out.println("visit  LValueIndexer");
//			Exp exs=null;	
//
//			if(e.getIndex() != null)
//				exs= (Exp)e.getIndex().accept(this,p);
//			else
//				return (Exp)e.getStructure().accept(this,p);
//			return new ExpIndexer((Exp)e.getStructure().accept(this,p),exs);			
//		}
//
//		@Override
//		public Object visitLValueField(LValueField e, Object p) {
//			e.getStructure().accept(this,p);
//			return null;
//		}
//
//	}
//
//}
//
//
//
//
//
//
//
