package hh.simplenet;

import hh.AST.syntaxtree.*;
import hh.common.translator.*;

class Main{
    public static void main(String [] argv) 
	throws java.io.FileNotFoundException, java.io.IOException{
	NetworkLexer lexer = new NetworkLexer( new java.io.FileReader(argv[0]) );
	lexer.yylex();
	/*NetworkParser parser = new NetworkParser(lexer);
	parser.parse();
*/
	/* 

	The line below uses the field *ast* defined in Network.jacc
	(see the jacc section where the code that has to be included
	in the parser class is given and see the assignment to *ast*
	when the start symbol is parsed). As it is
	defined when you get the lab it is declared as
  
            public Exp ast;

	for you to be able to test what you get on small
	sources containing just sums of integers and other sums.
	    
        When you work out the whole grammar it should be defined as 
  
            public Program ast;

	I recommend you to work the grammar bottom up, start by
	completing the expressions, then statements, then method
	declaration and then class declaration

	*/
	
	
//	if (parser.ast!=null)parser.ast.accept(new PrettyPrintVisitor(),null);

	System.out.println();
    }
}
