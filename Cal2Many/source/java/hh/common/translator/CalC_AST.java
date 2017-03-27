package hh.common.translator;

import hh.AST.syntaxtree.SEQ_Actor;





public class CalC_AST  {
		public SEQ_Actor pm;
		
		public CalC_AST(/*String name, 
				    NamespaceDecl namespace,
				    ParDeclType [] typePars, 
				    ParDeclValue [] valuePars, 
				    DeclType [] typeDecls, 
				    DeclVar [] varDecls,
				    ImmutableList<PortDecl>  inputPorts,
				    ImmutableList<PortDecl>  outputPorts,*/
				    SEQ_Actor pm) 
		{
			
		//	super(name, namespace, typePars, valuePars, typeDecls, varDecls);
			this.pm = pm;
			
		}		
	
}
