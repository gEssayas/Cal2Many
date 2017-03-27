package hh.common.translator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import hh.AST.syntaxtree.*;
import hh.common.passes.*;
import net.opendf.ir.cal.*;
import net.opendf.ir.common.*;
import net.opendf.ir.util.ImmutableList;
import hh.common.Pair;

public class ActorTranslate {

	public static CalC_AST translate(Actor actor,  VarDeclList parms, String entType,
			String entName, HashMap<String, ArrayList<String>> inputs,
			HashMap<String, ArrayList<String>> outputs, String sBackEnd) {
		TranslatActor2CAst translator = new TranslatActor2CAst(actor, parms, entType,
				entName, inputs, outputs);
		return translator.generateCast(entName, sBackEnd);
	}

	private static class TranslatActor2CAst {
		public static HashMap<String,Type> ActorVarsTypes = new HashMap<String, Type>();	

		public boolean isEpiphany = false;
		public boolean blockWhenOutputIsFull = true;
		public boolean use_setjmp = false;
		public ExpList ScheduleSwitchCases= new ExpList();
		public StatementList ScheduleSwitchStms = new StatementList();
		public int ScheduleSwitchAction = 0;


		private final Actor actor;
		public SEQ_Actor cpm;

		public ActionDeclList actionList;
		public ArrayList<String> actionsNames;
		public GuardDeclList guardList;
		//	public FunctionDeclList funList;
		//		public ProcedureDeclList procList;
		public VarDeclList actorVars;
		public VarDeclList actorParms;

		public ArrayList<VarDeclList> predicateVars;

		public FormalList parmList;
		public boolean isOld;
		public HashMap<String, ArrayList<String>> inputsCh;
		public HashMap<String, ArrayList<String>> outputsCh;
		public HashMap<String, Type> portTypes;
		public PortList inputs;
		public PortList outputs;

		public String instName;
		public int IntCheckOUtput=0;


		ImmutableList<Action> transitions;

		public TranslatActor2CAst(Actor actor,  VarDeclList parms, String entType, String entName,
				HashMap<String, ArrayList<String>> ainputsch,
				HashMap<String, ArrayList<String>> aoutputsch) {
			this.actor = actor;
			this.actorParms = parms;
			CommonTranslate.actorParmVals = parms;
			this.inputsCh = ainputsch;
			this.outputsCh = aoutputsch;
			this.portTypes = new HashMap<String, Type>();
			instName = entName;


			predicateVars = new ArrayList<VarDeclList>();

			actorVars = new VarDeclList();
			actionList = new ActionDeclList();
			actionsNames = new ArrayList<String>();
			this.inputs  =getPortListAndFillType(actor.getInputPorts());					
			this.outputs = getPortListAndFillType(actor.getOutputPorts());


			guardList = new GuardDeclList();

			CommonTranslate.funList = new FunctionDeclList();
			CommonTranslate.procList = new ProcedureDeclList();

			parmList = new FormalList();

			transitions = actor.getActions();

		}
		
		private void translateTransitons() {
			
			for (int i = 0; i < transitions.size(); i++) {
				HashMap<String,Type> AcVarsTypes = new HashMap<String, Type>();	

				PortList inputs = getMapPorts(transitions.get(i).getInputPatterns());
				PortList outputs = getOutputPorts(transitions.get(i).getOutputExpressions());
				VarDeclList actionVarList = CommonTranslate.getVarDecls(transitions.get(i).getVarDecls());
				actionVarList.addAll(getInputPortVars(transitions.get(i).getInputPatterns()));

				for(VarDecl vars:actionVarList){
					if(vars instanceof VarDeclSimp)
						AcVarsTypes.put(((VarDeclSimp)vars).i.s, ((VarDeclSimp) vars).t);
					else {
							AcVarsTypes.put(((VarDeclComp)vars).i.s,((VarDeclComp) vars).t);
						}
					
				}

				
				ImmutableList<Statement> acStas = transitions.get(i).getBody();
				String acName;
				if (transitions.get(i).getTag() != null)
					acName = transitions.get(i).getTag().toString();
				else
					acName = "untag.action_" + i;
				if (acName.equals(""))
					acName = "untag.action_" + i;
				StatementList sls = getConsumeTokenStms(transitions.get(i)
						.getInputPatterns(), false);
				for (VarDecl vr : actionVarList) {
					if (vr instanceof VarDeclSimp)
						if (((VarDeclSimp) vr).e != null)
							sls.add(new Assign(((VarDeclSimp) vr).i,
									((VarDeclSimp) vr).e, null));

					if (vr instanceof VarDeclComp)
						if (((VarDeclComp) vr).e != null)
							sls.add(new Assign(((VarDeclComp) vr).i,
									((VarDeclComp) vr).e, null));

				}
				
				
                CommonTranslate.VarsTypes.clear();
                CommonTranslate.VarsTypes.putAll(ActorVarsTypes);
                CommonTranslate.VarsTypes.putAll(AcVarsTypes);
				
                sls.addAll(CommonTranslate.getCStatements(acStas));

				sls.addAll(getOutStatementList(transitions.get(i).getOutputExpressions()));

				actionsNames.add(acName);
				actionList.add(new ActionDecl(null,
						inputs,
						outputs,
						new Identifier(acName), actionVarList,
						new Block(sls), getFiringConditions_bk(i)));
				
	//			System.out.println(" output " + actionList.get(0).output.size());
				

			}

		}

		private VarDeclList getInputPortVars(ImmutableList<InputPattern> inps) {
			VarDeclList vdls = new VarDeclList();
			for (InputPattern ph : inps) {
				for(String varName:ph.getVariables()){
					if(IsActorVariable(varName))
						continue;
					if (ph.getRepeatExpr() != null)
						vdls.add(new VarDeclComp(new ListType(portTypes.get(ph.getPort().getName()),
								CommonTranslate.getCExp(ph.getRepeatExpr())),
								new Identifier(varName), null,
								true));
					else
						vdls.add(new VarDeclSimp(portTypes.get(ph.getPort().getName()),
								new Identifier(varName), null,
								true));
				}

			}

			return vdls;
		}

		private boolean IsActorVariable(String varName) {
			for(VarDecl vd:actorVars){
				String name;
				if(vd instanceof VarDeclSimp)
					name = ((VarDeclSimp)vd).i.s;
				else
					name = ((VarDeclComp)vd).i.s;
				if(name.equals(varName))
					return true;
					
			}
			return false;
		}
		public StatementList getOutStatementList(
				ImmutableList<OutputExpression> oExps) {
			StatementList cStms = new StatementList();
			for (OutputExpression oExp : oExps)
				cStms.add(getSendTokenStm(oExp));
			return cStms;
		}

		public CStatement getSendTokenStm(OutputExpression oExp) {
			ExpList exps = new ExpList();
			Exp rex;
			List<Boolean> olds = new ArrayList<Boolean>();

			for (Expression ex : oExp.getExpressions()) {
				isOld = false;
				exps.add(CommonTranslate.getCExp(ex));
				olds.add(isOld);

			}
			if (oExp.getRepeatExpr() != null) {
				rex = CommonTranslate.getCExp(oExp.getRepeatExpr());
			} else
				rex = new IntegerLiteral(1);
			return (new SendToken(new PortHH(null, new Identifier(oExp
					.getPort().getName())), exps, olds, rex));

		}

		public StatementList getConsumeTokenStms(
				ImmutableList<InputPattern> ips, boolean read) {
			StatementList cinStms = new StatementList();
			int n = (int) (100 * Math.random());
			for (InputPattern ip : ips) {
				Exp ex;
				if (ip.getRepeatExpr() != null) {
					ex = CommonTranslate.getCExp(ip.getRepeatExpr());
				} else
					ex = new IntegerLiteral(1);

				java.util.ArrayList<Identifier> ids = new java.util.ArrayList<Identifier>();
				for (String id : ip.getVariables())
					if (id.matches("_"))
						ids.add(new Identifier("temp_" + n));
					else
						ids.add(new Identifier(id));

				if (read)
					cinStms.add(new ReadToken(new PortHH(null, new Identifier(
							ip.getPort().getName().toString())), ids, ex));
				else {
					if (ip.getPort().getName() != null) {
						cinStms.add(new ConsumeToken(new PortHH(null,
								new Identifier(ip.getPort().getName()
										.toString())), ids, ex));
					} else {
						cinStms.add((new ConsumeToken(new PortHH(null,
								new Identifier("#" + "what")), ids, ex)));

					}

					//					getPortList(actor.getInputPorts());
					//					getPortList(actor.getOutputPorts());

				}

			}

			return cinStms;
		}

		private PortList getMapPorts(ImmutableList<InputPattern> ips) {

			PortList ports = new PortList();

			for (InputPattern p : ips)
				ports.add(new PortHH(null,
						new Identifier(p.getPort().getName())));

			return ports;
		}

		private PortList getOutputPorts(ImmutableList<OutputExpression> outexp) {

			PortList ports = new PortList();

			for (OutputExpression p : outexp)
				ports.add(new PortHH(null,
						new Identifier(p.getPort().getName())));

			return ports;
		}

		private ExpList getOutputConditions(ImmutableList<OutputExpression> oexs) {
			ExpList tempExps = new ExpList();
			for (OutputExpression oex : oexs) {
				Exp exs;
				PortHH p=new PortHH(null, new Identifier(oex.getPort()
						.getName()));
				if (oex.getRepeatExpr() != null)
					exs=CommonTranslate.getCExp(oex.getRepeatExpr());

				else
					exs=new IntegerLiteral(1);


				tempExps.add(new TestOutputPort(p, exs));

			}
			return tempExps;
		}

		private ExpList getInputConditions(ImmutableList<InputPattern> ips) {
			ExpList tempExps = new ExpList();
			for (InputPattern p : ips) {
				Exp exs;
				for (String id : p.getVariables())
					CommonTranslate.hsActionVars.add(id);

				PortHH pp = new PortHH(null, new Identifier(p.getPort().getName()));
				if (p.getRepeatExpr() != null)
					exs= CommonTranslate.getCExp(p.getRepeatExpr());
				else
					exs = new IntegerLiteral(p.getVariables().size());
				tempExps.add(new TestInputPort(pp, exs));
			}
			return tempExps;

		}

		private ActionFiringCond getFiringConditions_bk(int transNo) {

			ImmutableList<Expression> guards = transitions.get(transNo).getGuards();
			ImmutableList<InputPattern> ips = transitions.get(transNo).getInputPatterns();
			ImmutableList<DeclVar> varDecls = transitions.get(transNo).getVarDecls();


			CommonTranslate.hsActionVars.clear();

			for (VarDecl vr : getInputPortVars(transitions.get(transNo).getInputPatterns())) {
				if (vr instanceof VarDeclSimp)
					CommonTranslate.hsActionVars.add(((VarDeclSimp) vr).i.s);
				else
					CommonTranslate.hsActionVars.add(((VarDeclComp) vr).i.s);
			}

			for (DeclVar dl : varDecls)
				CommonTranslate.hsActionVars.add(dl.getName());

			//			VarDeclList guardVars = new VarDeclList();
			StatementList tempStms = new StatementList();
			ExpList tempExps = new ExpList();

			CommonTranslate.bCollectVars = transNo;
			CommonTranslate.varIsUsedInGuardExps.clear();
			for(Expression guard : guards)
				tempExps.add(CommonTranslate.getCExp(guard));

			int oldGuardVarSize =0;

			while(oldGuardVarSize != CommonTranslate.varIsUsedInGuardExps.size()){
				oldGuardVarSize = CommonTranslate.varIsUsedInGuardExps.size();
				for(DeclVar var: varDecls){
					if(CommonTranslate.varIsUsedInGuardExps.contains(var.getName()))
						if(var.getInitialValue()!=null)
							CommonTranslate.getCExp(var.getInitialValue());
				}  

			}

			VarDeclList tempVar = new VarDeclList();
			for (VarDecl vr : getInputPortVars(transitions.get(transNo).getInputPatterns())) {

				String varname="";
				VarDecl var;
				if (vr instanceof VarDeclSimp){
					varname=((VarDeclSimp) vr).i.s;
				}
				else {
					varname = ((VarDeclComp) vr).i.s;

				}
				if (CommonTranslate.varIsUsedInGuardExps.contains(varname)) {
					String id="";

					if (vr instanceof VarDeclSimp){
						id=((VarDeclSimp) vr).i.s+"_ac"+ transNo;
						var = new VarDeclSimp(((VarDeclSimp) vr).t, new Identifier(id), ((VarDeclSimp) vr).e, ((VarDeclSimp) vr).isAssignable);
					}
					else {
						id = ((VarDeclComp) vr).i.s + "_ac"+ transNo;
						var = new VarDeclComp(((VarDeclComp) vr).t, new Identifier(id), ((VarDeclComp) vr).e, ((VarDeclComp) vr).isAssignable);
					}

					tempVar.add(var);
				}

			}

			StatementList stmsAfterToken = new StatementList();

			for (InputPattern p : ips){
				for (String id : p.getVariables()) {
					if (CommonTranslate.varIsUsedInGuardExps.contains(id)) {
						List<Identifier> is = new ArrayList<Identifier>();

						is.add(new Identifier(id + "_ac"+ transNo));
						if (p.getRepeatExpr() != null)
							stmsAfterToken.add(new ReadToken(
									new PortHH(null,new Identifier(p.getPort().getName())),
									is, 
									CommonTranslate.getCExp(p.getRepeatExpr())));

						else
							stmsAfterToken.add(new ReadToken(
									new PortHH(null,
											new Identifier(p
													.getPort()
													.getName())),
													is, new IntegerLiteral(1)));

					}




				}
			}
			VarDeclList guardVars = new VarDeclList();
			for(DeclVar varDecl:varDecls){
				if(CommonTranslate.varIsUsedInGuardExps.contains(varDecl.getName()))
					guardVars.add(CommonTranslate.getVarDecl(varDecl));

			}



			for (VarDecl vd : guardVars) {
				Type t;
				Exp varex;
				String vdi = "";
				boolean isSimp = false;
				if (vd instanceof VarDeclComp) {
					vdi = ((VarDeclComp) vd).i.s;
					varex = ((VarDeclComp) vd).e;
					t = ((VarDeclComp) vd).t;
				} else {
					isSimp = true;
					varex = ((VarDeclSimp) vd).e;
					vdi = ((VarDeclSimp) vd).i.s;
					t = ((VarDeclSimp) vd).t;

				}
				vdi = vdi + "_ac" + transNo;

				if (varex != null)
					tempStms.add(new Assign(new Identifier(vdi), varex, t));


				if (isSimp)
					tempVar.add(new VarDeclSimp(t, new Identifier(
							vdi), null, false));
				else
					tempVar.add(new VarDeclComp(t, new Identifier(
							vdi), null, false));


			}


			CommonTranslate.bCollectVars = -1;			






			VarDeclList vdls = new VarDeclList();
			vdls.addAll(tempVar);
			predicateVars.add(vdls);

			return new ActionFiringCond(tempVar, tempStms, stmsAfterToken, tempExps);
		}

		private ActionFiringCond getFiringConditions(int transNo) {

			ImmutableList<Expression> guards = transitions.get(transNo)
					.getGuards();
			ImmutableList<InputPattern> ips = transitions.get(transNo)
					.getInputPatterns();
			ImmutableList<DeclVar> varDecls = transitions.get(transNo)
					.getVarDecls();

			VarDeclList tempVar = new VarDeclList();
			StatementList stmsAfterToken = new StatementList();
			ExpList tempExps = new ExpList();

			HashSet<String> sVars = new HashSet<String>();
			CommonTranslate.hsActionVars.clear();
			CommonTranslate.IsUsedInGuardExps.clear();
			for (VarDecl vr : getInputPortVars(transitions.get(transNo).getInputPatterns())) {
				if (vr instanceof VarDeclSimp)
					CommonTranslate.hsActionVars.add(((VarDeclSimp) vr).i.s);
				else
					CommonTranslate.hsActionVars.add(((VarDeclComp) vr).i.s);
			}

			for (DeclVar dl : varDecls)
				CommonTranslate.hsActionVars.add(dl.getName());

			VarDeclList guardVars = new VarDeclList();
			StatementList tempStms = new StatementList();




			for (Expression guard : guards) {

				CommonTranslate.bCollectVars = transNo;

				tempExps.add(CommonTranslate.getCExp(guard));
				for (InputPattern p : ips) {
					for (String id : p.getVariables())
						if (CommonTranslate.IsUsedInGuardExps.containsKey(id)) {
							CommonTranslate.IsUsedInGuardExps.put(id, false);
							if (p.getRepeatExpr() != null)
								guardVars.add(new VarDeclComp(new ListType(
										new BooleanType(), 
										CommonTranslate.getCExp(p.getRepeatExpr())),
										new Identifier(
												id), null, true));
							else
								guardVars.add(new VarDeclSimp(new BooleanType(),
										new Identifier(id), null, true));

						}
				}
				while (CommonTranslate.IsUsedInGuardExps.containsValue(true)) {
					for (DeclVar dl : varDecls)
						if (CommonTranslate.IsUsedInGuardExps.containsKey(dl.getName()))
							if (CommonTranslate.IsUsedInGuardExps.get(dl.getName())) {
								guardVars.add(CommonTranslate.getVarDecl(dl));
								CommonTranslate.IsUsedInGuardExps.put(dl.getName(), false);
							}
				}

				CommonTranslate.bCollectVars = transNo;
				{
					Collections.reverse(guardVars);
					for (InputPattern p : ips)
						for (String id : p.getVariables()) {
							if (CommonTranslate.IsUsedInGuardExps.containsKey(id)) {
								for (VarDecl vd : guardVars) {
									String vdi;
									boolean isSimp = false;
									Type t;
									if (vd instanceof VarDeclComp) {
										vdi = ((VarDeclComp) vd).i.s;
										t = ((VarDeclComp) vd).t;
									} else {
										isSimp = true;
										vdi = ((VarDeclSimp) vd).i.s;
										t = ((VarDeclSimp) vd).t;
									}
									if (vdi.equals(id)) {
										vdi = vdi + "_ac" + transNo;
										if (!sVars.contains(vdi)) {
											if (isSimp)
												tempVar.add(new VarDeclSimp(t,
														new Identifier(vdi),
														null, false));
											else
												tempVar.add(new VarDeclComp(t,
														new Identifier(vdi),
														null, false));
											sVars.add(vdi);
										}

										java.util.ArrayList<Identifier> is = new java.util.ArrayList<Identifier>();

										if (id.matches("_"))
											is.add(new Identifier("temp_" + 100
													* Math.random()));
										else
											is.add(new Identifier(id + "_ac"
													+ transNo));
										if (p.getRepeatExpr() != null)
											stmsAfterToken.add(new ReadToken(
													new PortHH(null,new Identifier(p.getPort().getName())),
													is, 
													CommonTranslate.getCExp(p.getRepeatExpr())));

										else
											stmsAfterToken.add(new ReadToken(
													new PortHH(null,
															new Identifier(p
																	.getPort()
																	.getName())),
																	is, new IntegerLiteral(1)));

									}

								}
							}
						}

					for (VarDecl vd : guardVars) {
						Type t;
						Exp varex;
						String vdi = "";
						boolean isSimp = false;
						if (vd instanceof VarDeclComp) {
							vdi = ((VarDeclComp) vd).i.s;
							varex = ((VarDeclComp) vd).e;
							t = ((VarDeclComp) vd).t;
						} else {
							isSimp = true;
							varex = ((VarDeclSimp) vd).e;
							vdi = ((VarDeclSimp) vd).i.s;
							t = ((VarDeclSimp) vd).t;

						}
						vdi = vdi + "_ac" + transNo;

						if (varex != null)
							tempStms.add(new Assign(new Identifier(vdi), varex, t));

						if (!sVars.contains(vdi)) {
							if (isSimp)
								tempVar.add(new VarDeclSimp(t, new Identifier(
										vdi), null, false));
							else
								tempVar.add(new VarDeclComp(t, new Identifier(
										vdi), null, false));
							sVars.add(vdi);
						}

					}
				}
				CommonTranslate.bCollectVars = -1;
			}

			VarDeclList vdls = new VarDeclList();
			vdls.addAll(tempVar);
			predicateVars.add(vdls);

			return new ActionFiringCond(tempVar, tempStms, stmsAfterToken, tempExps);

		}

		public CalC_AST generateCast(String entName,String sBackEnd) {
			isEpiphany = sBackEnd.equals("Epiphany");

			actorVars.addAll(CommonTranslate.getVarDecls(actor.getVarDecls()));
			for(VarDecl vars:actorVars){
				List<Exp> dims = new ArrayList<Exp>();
				if(vars instanceof VarDeclSimp)
					ActorVarsTypes.put(((VarDeclSimp)vars).i.s, ((VarDeclSimp) vars).t);
				else {
					ActorVarsTypes.put(((VarDeclComp)vars).i.s,((VarDeclComp)vars).t);
				}				
			}

			
			translateTransitons();
			CommonTranslate.bCollectVars = -1;
			parmList.clear();
			for (ParDeclValue pdv : actor.getValueParameters()) {
				parmList.add(new Formal(CommonTranslate.getType(pdv.getType()), new Identifier(
						pdv.getName())));

			}
		//	System.out.println(CommonTranslate.procList.size());

			ActionScheduler acSc = getActionScheduler();


			cpm = new SEQ_Actor(new IdentifierType(actor.getName()),
					new Identifier(entName), parmList,
					inputs,outputs, actorVars ,
					transferList(guardList), actionList, CommonTranslate.funList, CommonTranslate.procList,
					acSc, inputsCh, outputsCh);

			CalC_AST ast = new CalC_AST(cpm);
			return ast;
		}

		private ActionScheduler getActionScheduler() {

			//actions priority  <high, low>
			List<Pair<String, String>> actionsPriority = new ArrayList<Pair<String, String>>();

			if (actor.getPriorities() != null)
				actionsPriority = getActionPriority(actor.getPriorities());

			HashMap<Integer, String> actionIdName = new HashMap<Integer, String>();

			for (int iac = 0; iac < actor.getActions().size(); iac++) {
				Action ac = actor.getActions().get(iac);
				if (ac.getTag() == null) {
					actionIdName.put(iac, "");
					continue;
				}
				actionIdName.put(iac, ac.getTag().toString());
			}

			// <<source, destination>, list of actions in the source state> of each state in the FSM
			List<Pair<ArrayList<String>, HashSet<Integer>>> fsmTransitions = new ArrayList<Pair<ArrayList<String>, HashSet<Integer>>>();

			if (actor.getScheduleFSM() != null){
				fsmTransitions.addAll(getActionInFSM(actor.getScheduleFSM(),actionIdName));
				actorVars.add( new VarDeclSimp(new IntegerType(32), new Identifier(instName+"_State"), new IdentifierExp("FSM_"+instName+"_"+actor.getScheduleFSM().getInitialState()),true));

			}



			ExpList acases = new ExpList();


			List<String> sFSMStates = new ArrayList<String>();
			List<Integer> listOFfsmActions = new ArrayList<Integer>();

			int iFsState = 0;


			List<List<Pair<Integer,StatementList>>> fsmActionIdStats = new ArrayList<List<Pair<Integer,StatementList>>>();

			for (Pair<ArrayList<String>, HashSet<Integer>> fsm : fsmTransitions) {
				List<Integer> tmpfsmActions = new ArrayList<Integer>();

				String sFsState = "FSM_" + instName + "_" + fsm.getLeft().get(0);
				tmpfsmActions.addAll(fsm.getRight());

				if (sFSMStates.contains(sFsState)) {
					fsmActionIdStats.get((sFSMStates.indexOf(sFsState))).addAll(
							getActionCalls(tmpfsmActions, fsm.getLeft().get(1)));
				} else {
					sFSMStates.add(sFsState);
					actorVars.add(new VarDeclSimp(new IntegerType(32),
							new Identifier(sFsState), new IntegerLiteral(
									iFsState++), false));
					acases.add(new IdentifierExp(sFsState));
					fsmActionIdStats.add(getActionCalls(tmpfsmActions, fsm.getLeft().get(1)));
				}

				listOFfsmActions.addAll(tmpfsmActions);

			}



			StatementList aSwichCaseStat = new StatementList();

			for(List<Pair<Integer, StatementList>> fsmActionss :fsmActionIdStats){
				List<StatementList>  tmpfsmStatments = new ArrayList<StatementList>();
				List<Integer> tmpfsmActions = new ArrayList<Integer>();	
				for(Pair<Integer, StatementList> fsmAction:fsmActionss){
					tmpfsmActions.add(fsmAction.getLeft());
					tmpfsmStatments.add(fsmAction.getRight());					
				}				
				List<Integer> orderedfsmActions = OrderActions(tmpfsmActions, actionsPriority);
				StatementList  orderedfsmStatments = new StatementList();
				for(Integer ordAc: orderedfsmActions){
					int ind=tmpfsmActions.indexOf(ordAc);
					orderedfsmStatments.addAll(tmpfsmStatments.get(ind));
				}

				orderedfsmStatments.add(new Break());
				aSwichCaseStat.add(new Block(orderedfsmStatments));				
			}

			List<Integer> untagged = new ArrayList<Integer>();
			List<Integer> tagged = new ArrayList<Integer>();

			for (int iac = 0; iac < actor.getActions().size(); iac++) {
				if (actor.getActions().get(iac).getTag() == null)
					untagged.add(iac);
				else if (!listOFfsmActions.contains(iac))
					tagged.add(iac);
			}
			StatementList actionSchedulerStms = new StatementList();

			List<Pair<Integer, StatementList>> idStatUntagged = getActionCalls(untagged, null);

			for(Pair<Integer, StatementList> idStUn:idStatUntagged)
				actionSchedulerStms.addAll(idStUn.getRight());
			if (actionsPriority.size() > 0 && tagged.size() > 0)
				tagged = OrderActions(tagged, actionsPriority);

			List<Pair<Integer, StatementList>> idStatTagged = getActionCalls(tagged, null);
			for(Pair<Integer, StatementList> idStTa:idStatTagged)
				actionSchedulerStms.addAll(idStTa.getRight());
			if (actor.getScheduleFSM() != null) {				
				actionSchedulerStms.add(new SwitchCase(new IdentifierExp(
						instName + "_State"), acases, aSwichCaseStat));
			}
			VarDeclList acScVars = new VarDeclList();

			for (VarDeclList evd : predicateVars)
				acScVars.addAll(evd);

			//			actionSchedulerStms.add(0, new Assign(
			//					new Identifier("bTestAction"), new BooleanLiteral(false)));
			//			

			StatementList tmpschedulerStms = new StatementList();
			StatementList schedulerStms = new StatementList();


			tmpschedulerStms.add(new Lable(instName + "Schedule"));
			tmpschedulerStms.addAll(actionSchedulerStms);
			if(isEpiphany){
				schedulerStms.addAll(tmpschedulerStms);
			}
			else
				if(blockWhenOutputIsFull){
					if(actor.getOutputPorts().size()>0){
						if(use_setjmp){
							ExpList longex= new ExpList();
							longex.add(new IdentifierExp(instName+"CheckOutPut_buf"));
							longex.add(new IntegerLiteral(1));
							tmpschedulerStms.add(new Assign(new Identifier(instName + "CheckOutput"),new BooleanLiteral(false), null));
							schedulerStms.add(new If(new Not(new IdentifierExp(instName + "CheckOutput")),
									new Block(tmpschedulerStms),new StatementCall(new IdentifierExp("longjmp"), longex)));
							//               	 actorVars.add(new VarDeclSimp(new IntegerType(32), new Identifier(instName+"_IntCheckOutput"), new IntegerLiteral(0), true));
							actorVars.add(new VarDeclSimp(new BooleanType(), new Identifier(instName+"CheckOutput"), new BooleanLiteral(false), true));
							actorVars.add(new VarDeclSimp(new IdentifierType("jmp_buf"), new Identifier(instName+"CheckOutPut_buf"), null, true));
						}
						else {

							tmpschedulerStms.add(new Assign(new Identifier(instName+"_IntCheckOutput"), new IntegerLiteral(-1), null));
							StatementList tmpElseStms = new StatementList();            		 
							tmpElseStms.add(new SwitchCase(new IdentifierExp(instName+"_IntCheckOutput"), ScheduleSwitchCases, ScheduleSwitchStms));

							schedulerStms.add(new If(new Equal(new IdentifierExp(instName+"_IntCheckOutput"), new IntegerLiteral(-1)),
									new Block(tmpschedulerStms),
									new Block(tmpElseStms)
									));


							actorVars.add(new VarDeclSimp(new IntegerType(32), new Identifier(instName+"_IntCheckOutput"), new IntegerLiteral(-1), true));


						}
					}
					else
						schedulerStms.addAll(tmpschedulerStms);
				}



			/*			schedulerStms.add(new While(new IdentifierExp("bTestAction"),
					new Block(actionSchedulerStms)));
			acScVars.add(new VarDeclSimp(new BooleanType(),
					new Identifier("bTestAction"), new BooleanLiteral(true),
					true));*/
			schedulerStms.add(new GoTo(instName + "Schedule"));
			return new ActionScheduler(new Identifier("Scheduler_" + instName),
					inputs, outputs, acScVars,
					schedulerStms);
		}

		private List<Integer> OrderActions(List<Integer> tagged,
				List<Pair<String, String>> orderedActions) {
			List<Integer> ordAct = new ArrayList<Integer>();
			boolean addaction;

			if (orderedActions.size() == 0)
				return tagged;
			ordAct.add(tagged.get(0));
			for (int j = 1; j < tagged.size(); j++) {
				addaction = true;
				for (int i = 0; i < ordAct.size(); i++) {
					if (CheakPriority(ordAct.get(i), tagged.get(j),
							orderedActions)) {
						addaction = false;
						ordAct.add(i, tagged.get(j));
						break;
					}
				}
				if (addaction)
					ordAct.add(tagged.get(j));
			}

			return ordAct;
		}

		// true if action 2 have high priority than action 1
		private boolean CheakPriority(Integer iAc1, Integer iAc2,
				List<Pair<String, String>> priority) {
			String ac1, ac2;
			if (actor.getActions().get(iAc1) != null)
				ac1 = actor.getActions().get(iAc1).toString();
			else
				return false;

			if (actor.getActions().get(iAc2) != null)
				ac2 = actor.getActions().get(iAc2).toString();
			else
				return true;
			if (ac1.equals(""))
				return false;
			if (ac2.equals(""))
				return true;
			for (Pair<String, String> pr : priority) {
				if (ac1.startsWith(pr.getLeft()))
					if (ac2.startsWith(pr.getRight()))
						return false;
				if (ac2.startsWith(pr.getLeft()))
					if (ac1.startsWith(pr.getRight()))
						return true;

			}
			return false;
		}

		private List<Pair<Integer,StatementList>> getActionCalls(List<Integer> actions,
				String dist) {

			List<Pair<Integer,StatementList>> actionIdStats = new ArrayList<Pair<Integer,StatementList>>();
			ExpList exre = new ExpList();
			exre.add(new IntegerLiteral(0));

			for (Integer iac : actions) {
				StatementList actionStatments = new StatementList();
				StatementList tmpcallSts = new StatementList();
				CStatement inputSt, outputSt, condSt;
				ExpList exs = new ExpList();
				exs.addAll(actionList.get(iac).input);
				exs.addAll(actionList.get(iac).output);
				tmpcallSts.add(new StatementCall(new IdentifierExp(instName + "_" + actionList.get(iac).i.s), exs));
				if (dist != null) {
					tmpcallSts.add(new Assign(new Identifier(instName + "_State"),
							new IdentifierExp("FSM_" + instName + "_" + dist), null));
					/*if(actionList.get(iac).output.size()>0)
						tmpcallSts.add(new StatementCall(new IdentifierExp("return"),exre));
					else */{
						tmpcallSts.add(new GoTo(instName + "Schedule"));
						//	    tmpcallSts.add(new Break());
					}

				}
				else /*if(actionList.get(iac).output.size()>0)
					tmpcallSts.add(new StatementCall(new IdentifierExp("return"),exre));
				else*/
					tmpcallSts.add(new GoTo(instName + "Schedule"));

				actionStatments.addAll(actionList.get(iac).afc.initStms);
				CStatement elseOutput;

				if(isEpiphany)
					outputSt = new Block(tmpcallSts);
				else {
					if (actionList.get(iac).output.size() > 0){				
						if(blockWhenOutputIsFull){
							StatementList tmpelse = new StatementList();

							if(use_setjmp){
								tmpelse.add(new Assign(new Identifier(instName + "CheckOutput"),new BooleanLiteral(true), null));
								//tmpelse.add(new Assign(new Identifier(instName + "_IntCheckOutput"),new IntegerLiteral(IntCheckOUtput++)));
							}
							else {

								tmpelse.add(new Assign(new Identifier(instName + "_IntCheckOutput"),new IntegerLiteral(ScheduleSwitchAction), null));
								StatementList tmpCase = new StatementList();
								tmpCase.add(new Assign(new Identifier(instName+"_IntCheckOutput"), new IntegerLiteral(-1), null));
								tmpCase.add(new GoTo(instName + "_Action_"+ScheduleSwitchAction));


								ScheduleSwitchCases.add(new IntegerLiteral(ScheduleSwitchAction));
								ScheduleSwitchStms.add(new Block(tmpCase));
							}

							tmpelse.add(new StatementCall(new IdentifierExp("return"),exre));
							elseOutput = new Block(tmpelse);

						}
						else
							elseOutput = null;


						CStatement tmpoutputSt;
						tmpoutputSt = new If(
								connectConditions(getOutputConditions(actor
										.getActions().get(iac)
										.getOutputExpressions())),
										new Block(tmpcallSts), elseOutput);
						if(blockWhenOutputIsFull){
							StatementList outputSts = new StatementList();
							//and c statemntes like &&labels or setjump(instName +"buf") longjump
							if(use_setjmp){
								ExpList bufs = new ExpList();
								bufs.add(new IdentifierExp(instName + "CheckOutPut_buf"));
								outputSts.add(new StatementCall(new IdentifierExp("setjmp"),bufs));
							}
							else
								outputSts.add(new Lable(instName + "_Action_"+ScheduleSwitchAction++));
							outputSts.add(tmpoutputSt);
							outputSt= new Block(outputSts);
						}
						else
							outputSt =tmpoutputSt;
					}
					else
						outputSt = new Block(tmpcallSts);
				}




				if (actionList.get(iac).afc.conds.size() > 0)
					condSt = new If(
							connectConditions(actionList.get(iac).afc.conds),
							outputSt, null);
				else
					condSt = outputSt;


				if (actionList.get(iac).input.size() > 0){
					StatementList tokenStates = new StatementList(); 
					tokenStates.addAll(actionList.get(iac).afc.stmsAftertoken);;	
					tokenStates.add(condSt);

					inputSt = new If(connectConditions(getInputConditions(actor
							.getActions().get(iac).getInputPatterns())),
							new Block(tokenStates), null);

				}
				else
					inputSt = condSt;

				actionStatments.add(inputSt);

				actionIdStats.add(new Pair<Integer,StatementList>(iac,actionStatments));
			}

			return actionIdStats;
		}

		private Exp connectConditions(ExpList exs) {

			if (exs.size() > 0) {
				if (exs.size() == 1)
					return exs.get(0);
				Exp ex = exs.get(0);
				exs.remove(0);
				return new And(ex, connectConditions(exs));
			}

			return null;
		}

		private ArrayList<Pair<String, String>> getActionPriority(
				ImmutableList<ImmutableList<QID>> priorities) {
			ArrayList<Pair<String, String>> orderedActions = new ArrayList<Pair<String, String>>();
			for (ImmutableList<QID> qids : priorities) {
				for (int i = 0; i < qids.size(); i++)
					for (int j = i + 1; j < qids.size(); j++)
						orderedActions.add(new Pair<String, String>(qids.get(i).toString(),qids.get(j).toString()));
			}
			return orderedActions;
		}

		private ArrayList<Pair<ArrayList<String>, HashSet<Integer>>> getActionInFSM(
				ScheduleFSM fsm, HashMap<Integer, String> actionList) {
			ArrayList<Pair<ArrayList<String>, HashSet<Integer>>> fsmAction = new ArrayList<Pair<ArrayList<String>, HashSet<Integer>>>();

			for (Transition t : fsm.getTransitions()) {
				ArrayList<String> sour_dist = new ArrayList<String>();
				for (QID q : t.getActionTags()) {
					HashSet<Integer> acts = getAllActions(q.toString(),
							actionList);
					sour_dist.add(t.getSourceState());
					sour_dist.add(t.getDestinationState());
					fsmAction.add(new Pair<ArrayList<String>, HashSet<Integer>>(sour_dist, acts));
				}
			}
			return fsmAction;
		}

		private HashSet<Integer> getAllActions(String tag,
				HashMap<Integer, String> actionList) {
			HashSet<Integer> acNos = new HashSet<Integer>();
			for (Entry<Integer, String> act : actionList.entrySet()) {
				if (act.getValue().equals(tag)) {
					acNos.add(act.getKey());
					continue;
				}
				if (act.getValue().startsWith(tag+".")) {
					acNos.add(act.getKey());
				}
			}

			return acNos;
		}

		public GuardDeclList transferList(GuardDeclList vars) {
			GuardDeclList vs = new GuardDeclList();
			for (GuardDecl v : vars)
				vs.add(v);
			return vs;
		}



		public PortList getPortListAndFillType(List<PortDecl> ports) {
			PortList ps = new PortList();
			for (PortDecl pd : ports){
				String s =pd.getName();
				Type t = CommonTranslate.getType(pd.getType());
				ps.add(new PortHH(t, new Identifier(s)));
				portTypes.put(s, t);
			}

			return ps;
		}

	}

}
