package hh.simplenet;
import hh.AST.syntaxtree.*;
import hh.common.translator.NetVisitor;



public class FlatNetwork {
	   public Identifier i;
	   public IdentifierType it;
	public FormalList fls; // parmas

	public PortList input, output;

	public VarDeclList vl; // state vars
	public EntityList entities;
	public ChannelList chs;	

	public FlatNetwork(IdentifierType ait, FormalList afls, PortList ainput,PortList aoutput, VarDeclList avl,EntityList aentities, ChannelList achs) {

		it=ait; fls=afls; input=ainput; output=aoutput; vl=avl; entities=aentities; chs=achs;
	}
    public FlatNetwork(FlatNetwork n){
    	it=n.it; fls= n.fls; input =n.input; output=n.output; vl=n.vl; entities=n.entities; chs=n.chs;
    }

	public <T,E>T accept(NetVisitor<T,E> v, E env) {
		return v.visit(this,env);
	}
}
