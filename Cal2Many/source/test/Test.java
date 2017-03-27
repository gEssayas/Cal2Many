

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.opendf.ir.am.ActorMachine;
import net.opendf.ir.cal.Actor;
import net.opendf.ir.common.Decl;
import net.opendf.ir.common.DeclVar;
import net.opendf.ir.common.ExprLiteral;
import net.opendf.ir.common.Expression;
import net.opendf.parser.lth.CalParser;
import net.opendf.transform.caltoam.ActorToActorMachine;
import net.opendf.transform.outcond.OutputConditionAdder;
import net.opendf.util.ControllerToGraphviz;

public class Test {
	public static void main(String[] args) throws FileNotFoundException {
		File calFile = new File(args[1]);
		
		CalParser parser = new CalParser();
		Actor actor = parser.parse(calFile);
		if (!parser.parseProblems.isEmpty()) {
			for (String p : parser.parseProblems) {
				System.err.println(p);
			}
			return;
		}
		
/*		List<Decl> actorArgs = new ArrayList<Decl>();
		actorArgs.add(varDecl("MAXW_IN_MB", lit(121)));
		actorArgs.add(varDecl("MAXH_IN_MB", lit(69)));
		actorArgs.add(varDecl("ADDR_SZ", lit(24)));
		actorArgs.add(varDecl("PIX_SZ", lit(9)));
		actorArgs.add(varDecl("MV_SZ", lit(9)));
		actorArgs.add(varDecl("SAMPLE_COUNT_SZ", lit(8)));
		actorArgs.add(varDecl("SAMPLE_SZ", lit(13)));
		actorArgs.add(varDecl("MB_COORD_SZ", lit(8)));
		actorArgs.add(varDecl("BTYPE_SZ", lit(12)));
		actorArgs.add(varDecl("NEWVOP", lit(2048)));
		actorArgs.add(varDecl("INTRA", lit(1024)));
		actorArgs.add(varDecl("INTER", lit(512)));
		actorArgs.add(varDecl("QUANT_MASK", lit(31)));
		actorArgs.add(varDecl("ROUND_TYPE", lit(32)));
		actorArgs.add(varDecl("FCODE_MASK", lit(448)));
		actorArgs.add(varDecl("FCODE_SHIFT", lit(6)));
		actorArgs.add(varDecl("ACPRED", lit(1)));
		actorArgs.add(varDecl("ACCODED", lit(2)));
		actorArgs.add(varDecl("FOURMV", lit(4)));
		actorArgs.add(varDecl("MOTION", lit(8)));
		actorArgs.add(varDecl("QUANT_SZ", lit(6)));*/
		
		
		ActorToActorMachine trans = new ActorToActorMachine(); 
		ActorMachine actorMachine = trans.translate(actor);
		
		ControllerToGraphviz.print(new PrintWriter("controller.gv"), actorMachine, "Controller");
		
		OutputConditionAdder out = new OutputConditionAdder();
		actorMachine = out.addOutputConditions(actorMachine);

		ControllerToGraphviz.print(new PrintWriter("controller_oc.gv"), actorMachine, "ControllerOC");
	}
	
	/*private static DeclVar varDecl(String name, Expression expr) {
		return new DeclVar(null, name, null, expr, false);
	}
	
	private static ExprLiteral lit(int i) {
		return new ExprLiteral(ExprLiteral.litInteger, Integer.toString(i));
	}*/

}
