package hh.simplenet;
import hh.AST.syntaxtree.*;
import hh.common.translator.NetVisitor;



public class Entity{
    public IdentifierType it;
 	public Identifier i;
	//public ExpList parms; // properti
	public VarDeclList parms;

	public Entity(IdentifierType ait,Identifier ai, VarDeclList aparms) {
	 it=ait; i=ai; parms=aparms;
	}
	
   /* public Entity(Entity e){
    	it= new IdentifierType(e.it.s);
    	i= new Identifier(e.i.s);
    	parms=new ExpList();
    	
    	for(Exp ex: e.parms)
    		parms.add(ex);
    }
    */
    
     
	public <T,E>T accept(NetVisitor<T,E> v, E env) {
		return v.visit(this,env);
	}
}
