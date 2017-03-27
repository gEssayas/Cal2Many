
/** 
	This is a simple test harness for the 2D-IDCT, illustrating how it is instantiated, and how data
	is fed to it.
*/

network Test () ==> :

entities

	source = TestSource();

	idct = idct2d(INP_SZ = 13, PIX_SZ = 9);	

	print = TestPrint();
	
	
structure

	source.V --> idct.In;
	source.Signed --> idct.Signed;
	idct.Out --> print.B;	
end


