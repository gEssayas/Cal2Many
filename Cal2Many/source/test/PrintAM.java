


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;

import net.opendf.ir.am.ActorMachine;
import net.opendf.ir.am.ConditionVisitor;
import net.opendf.ir.am.ICall;
import net.opendf.ir.am.ITest;
import net.opendf.ir.am.IWait;
import net.opendf.ir.am.Instruction;
import net.opendf.ir.am.InstructionVisitor;
import net.opendf.ir.am.PortCondition;
import net.opendf.ir.am.PredicateCondition;
import net.opendf.ir.cal.Actor;
import net.opendf.ir.cal.Action;
import net.opendf.ir.cal.InputPattern;
import net.opendf.ir.cal.OutputExpression;
import net.opendf.ir.cal.ScheduleFSM;
import net.opendf.ir.cal.Transition;
import net.opendf.ir.common.Decl;
import net.opendf.ir.common.DeclVar;
import net.opendf.ir.common.ExprLiteral;
import net.opendf.ir.common.Expression;
import net.opendf.ir.common.Namespace;
import net.opendf.ir.common.NamespaceDecl;
import net.opendf.ir.common.PortDecl;
import net.opendf.ir.common.QID;
import net.opendf.parser.lth.CalParser;
import net.opendf.transform.caltoam.ActorToActorMachine;

import org.w3c.dom.Document;
import org.w3c.dom.Node;






public class PrintAM {
	/**/     
	private static int [][] branch;
	private static String [] name;
	private static String  wname="";
	private static int [] states;
	private static int [] no_states;
	private static 		int counter = 0;
	/**/  
	private static ActorMachine am;
	public static void main(String[] args) throws Exception {

		ArrayList<String> net_Files = new ArrayList<String>() ;
		ArrayList<String> cal_Files = new ArrayList<String>();
		int len=0;

		int generate = Integer.parseInt(args[2]); 
		
		if(generate==2){
		File folder = new File(args[0]);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String str= listOfFiles[i].getName();
				if(str.endsWith(".nl")){
					net_Files.add(str.substring(0, str.length()-3));
				}
				else if(listOfFiles[i].getName().endsWith(".cal"))
					cal_Files.add(str.substring(0, str.length()-4));
			}
		}
		}
		else if (generate==0){
			
			cal_Files.add(args[1]);
			
		}

		for(String arg : cal_Files)
		{   

			//System.out.println("\nActor Machine for " + arg);
			File calFile = new File(args[0]+arg+".cal");

			CalParser parser = new CalParser();
			Actor a = parser.parse(calFile);
			if (!parser.parseProblems.isEmpty()) {
				for (String p : parser.parseProblems) {
					System.err.println(p);
				}
				return;
			}
			
	        printFSM(a.getScheduleFSM());

			
			
			ActorToActorMachine trans = new ActorToActorMachine();
			am = trans.translate(a);
			//    			System.out.println("The new actor machine \n\t" +
			//    					"Conditions   : "+ am.getConditions().size()+"\n\t" +
			//    					"Transitons   : "+am.getTransitions().size()+ "\n\t"+
			//    					"Controllers  : "+am.getConditions().size()+"\n\t" +
			//    					"ActorActions : "+a.getActions().size());

			/**/
			counter=0;
			len=am.getController().size();

			branch=new int[len][2];
			name =new String[len];
			states=new int[len];
			no_states=new int[len];

			for(int i=0;i<len;i++){
				states[i]=-1;
				no_states[i]=0;        			
			}
			//System.out.println(am.getName());

			PrintVisitor pv = new PrintVisitor();
			int cr=0;
			cr=0;
		//	System.out.println("\nsize of am :>"+am.getController().size() );
			for(List<Instruction> inst : am.getController()) {
				cr++;
				//        			if(inst.size()>1)
				//        			   System.err.println(cr+ " size of inst:> .................... "+ inst.size());
				if(inst.size()>0){
					inst.get(0).accept(pv,null);
				}else{
					branch[counter][0]= -1;
					branch[counter][1]=-1;	
					name[counter]="dead()";
					counter ++;

				}    				
			}
			int num_state=0;
			for(int j=0; j<no_states.length;j++)
				num_state+=no_states[j];
			System.out.println("\t" + arg+ "\t\t"+ num_state);
			
		}

		        		PrintAmTable(len);
		//
		//        		System.out.println("\n\nint state=0; \n\nvoid scheduler_"+a.getName()+"()\n{");
		//       		amprint();
		//        		System.out.println("}");     




	}

	private static void printFSM(ScheduleFSM fsm) {
		// TODO Auto-generated method stub
		System.out.println("FSM-------" +fsm.getInitialState()+"\n");
		for(Transition t: fsm.getTransitions()){
			for(QID q: t.getActionTags()){
				System.out.println(t.getSourceState() +"( " + q.toString() +") -----> " + t.getDestinationState());
			}
		}
		
		
	}

	private static class PrintVisitor implements InstructionVisitor<Object,Object>, ConditionVisitor<Object,Object> {

		public Object visitWait(IWait i, Object o) {
			name[counter]="wait(); /*" +wname + "*/";
			wname="";
			branch[counter][0]=i.S();
			branch[counter][1]=-2;			
			///**/System.out.println(counter + "\twait()\t" + i.S());
			states[i.S()]=1;

			counter++;
			no_states[i.S()]=1;
			return o;
		}

		public Object visitTest(ITest i, Object o) {

			branch[counter][0]=i.S0();
			branch[counter][1]=i.S1();			
			///**/System.out.print(counter + "\ttest ");
			counter++;
			am.getCondition(i.C()).accept(this,null);
			///**/System.out.println("\t" + i.S0() + "\t" + i.S1());
			no_states[i.S0()]=1;
			no_states[i.S1()]=1;
			return o;
		}

		public Object visitCall(ICall i, Object o) {
			name[counter]="action_"+ i.T()+"()";
			branch[counter][0]=i.S();
			branch[counter][1]=-2;
			states[i.S()]=1;
			///**/System.out.println( counter + "\tcall(action_"+ i.T()+ ")\t" + i.S());
			counter++;
			no_states[i.S()]=1;
			return o;
		}

		public Object visitInputCondition(PortCondition c, Object o) {
			name[counter-1]="TestInputPort(&" + c.getPortName().getName() + "," + c.N() + ")";
			wname=wname +"#"+"_port_"+c.getPortName().getName();
			///**/System.out.print("input(" + c.getPortName().getName() + "," + c.N() + ")");
			return o;
		}

		public Object visitOutputCondition(PortCondition c, Object o) {
			name[counter-1]="TestOutputPort(&" + c.getPortName().getName() + "," + c.N() + ")";
			///**/System.out.print("output(" + c.getPortName().getName() + "," + c.N() + ")");
			return o;
		}

		public Object visitPredicateCondition(PredicateCondition c, Object o) {
			name[counter-1]=c.toString()+"_guard()";
			wname=wname +"#"+ name[counter-1];
			///**/System.out.print(c.toString()+"_guard");
			return o;
		}

	}
	static private  String indent = "";;
	public static void amprint(){
		System.out.println("\tif(state==0){");
		amprint(0);
		for(int i=1;i<states.length;i++){
			if(states[i]==1){
				System.out.println("\t}\n\telse if (state=="+i+"){");
				amprint(i);
			}
		}
		System.out.println("\t}");

	}
	public static void amprint(int node) {
		{

			{
				if(branch[node][1]==-2){
					System.out.print("\t\t{ "+name[node].replace('.','_')+";");
					System.out.println("  state="+branch[node][0]+";}");
				}else if(branch[node][1]==-1){
					System.out.println("\t\t "+name[node].replace('.','_')+";");
				}
				else{
					//  System.out.println("\t\tif ("+name[node]);
					System.out.println("\t\tif ("+name[node].replace('.','_')+")");
					amprint(branch[node][1]);
					System.out.println("\t\telse ");
					amprint(branch[node][0]);
				}
			}
		}
	}

	/*		private static  void  printPort(CompositePortDecl cpd){
			List<PortDecl> ports = cpd.getChildren();

			for(int i=0; i< ports.size(); i++){
				if (ports.get(i) instanceof AtomicPortDecl) {
					AtomicPortDecl apd = (AtomicPortDecl) ports.get(i);	
											System.out.print(apd.getLocalName());
					}
				else
					System.out.println(" *** TODU for" + ports.get(i).toString());


			   if(i< ports.size()-1)
				   System.out.print(", ");
			   else
				   System.out.println(";");
			}



		}
	 */
	private static void PrintAmTable(int len){
		System.out.println("-2 for call and -1 for wait");

		System.out.println("State\tFalse\tTrue\tName");

		for(int i=0; i<len;i++)
			System.out.println( i +"\t"+branch[i][0]+"\t"+branch[i][1]+"\t"+name[i]);

	}
	private static void SimpScheduler(int len){
		System.out.println("-2 for call and -1 for wait");

		System.out.println("State\tFalse\tTrue\tName");

		for(int i=0; i<len;i++)
			System.out.println( i +"\t"+branch[i][0]+"\t"+branch[i][1]+"\t"+name[i]);

	}





	/*	  testdata/calml/Sieve.calml
	  testdata/calml/Split.calml
	  testdata/calml/Z.calml
	  testdata/calml/InitialTokens.calml
	  testdata/calml/Merge.calml*/

}
