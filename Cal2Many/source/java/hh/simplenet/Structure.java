package hh.simplenet;
import hh.AST.syntaxtree.*;
import hh.common.translator.NetVisitor;



public class Structure {
	public ChannelList chs;
	
	

	public Structure(ChannelList achs) {
		chs=achs;		
	}


	public <T,E>T accept(NetVisitor<T,E> v, E env) {
		return v.visit(this,env);
	}
}
