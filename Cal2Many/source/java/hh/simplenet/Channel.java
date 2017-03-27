package hh.simplenet;
import net.opendf.ir.net.ToolAttribute;
import net.opendf.ir.util.ImmutableList;
import hh.AST.syntaxtree.VarDeclList;
import hh.common.translator.NetVisitor;



public class Channel {
	public EntityPort p1,p2;
	public ImmutableList<ToolAttribute> parms;

	public Channel(EntityPort ap1,EntityPort ap2, ImmutableList<ToolAttribute> aparms){
		p1=ap1;p2=ap2; parms=aparms;
	}
	public String toString(){
		/*String chS;
		if(this.p1.i.s!=null)
			chS = this.p1.i.s + ".";
		else
			chS ="top.";
		
		if(this.p1.p.s!=null)
			chS = chS+ this.p1.p.s + "-->";
		else
			chS =chS+"top-->";*/
		
		return p1.i.s + "." + p1.p.s + "-->" + p2.i.s + "." + p2.p.s;
			
	}


	public <T,E>T accept(NetVisitor<T,E> v, E env) {
		return v.visit(this,env);
	}
}
