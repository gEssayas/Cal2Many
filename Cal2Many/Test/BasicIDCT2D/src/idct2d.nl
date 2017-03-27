
/**
  2D-IDCT
  This is a 'flat' variant of the IDCT written by Dave Parlour
  which multiplexed the 1D-IDCT for both rows and columns.
  This version simply uses two 1D-IDCTs.
*/


network idct2d (INP_SZ, PIX_SZ) In, Signed ==> Out :

var
	MEM_SZ = 16;
	OUT_SZ = 10;
	
entities

	rowsort = RowSort(sz = INP_SZ);

	idctRow = idct1d(MEM_SZ = MEM_SZ, ROW = true);

	trans = Transpose(sz = MEM_SZ);
	
 	idctCol = idct1d(MEM_SZ = MEM_SZ, ROW = false);
 	
	shift = Shift(isz = MEM_SZ, csz = OUT_SZ);
	
	retrans = Retranspose(isz = OUT_SZ, osz = OUT_SZ);
	
	clip = Clip(isz = OUT_SZ, osz = PIX_SZ);

structure

	In --> rowsort.ROW;
	Signed --> clip.SIGNED;

	rowsort.Y0 --> idctRow.X0;
	rowsort.Y1 --> idctRow.X1;
	
	idctRow.Y0 --> trans.X0;
	idctRow.Y1 --> trans.X1;
	idctRow.Y2 --> trans.X2;
	idctRow.Y3 --> trans.X3;


	trans.Y0 --> idctCol.X0;
	trans.Y1 --> idctCol.X1;
	
	idctCol.Y0 --> shift.X0;
	idctCol.Y1 --> shift.X1;
	idctCol.Y2 --> shift.X2;
	idctCol.Y3 --> shift.X3;

	shift.Y0 --> retrans.X0;
	shift.Y1 --> retrans.X1;
	shift.Y2 --> retrans.X2;
	shift.Y3 --> retrans.X3;

	retrans.Y --> clip.I;
	
	clip.O --> Out;
end
