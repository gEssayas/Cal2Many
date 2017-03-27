
network Pidct1d (PLACED, MEM_SZ, ROW) X0, X1 ==> Y0, Y1, Y2, Y3:

var

	COEFF_SZ = 13;
	SCALE_SZ = 30;
	ACC_SZ = 24;

entities

	scale = Scale(PLACED = PLACED, isz = MEM_SZ, osz = SCALE_SZ, csz = COEFF_SZ);
	
	combine = Combine(PLACED = PLACED, isz = SCALE_SZ, osz = ACC_SZ, row = ROW);
	
	shufflefly = ShuffleFly(PLACED = PLACED, sz = ACC_SZ);

	shuffle = Shuffle(PLACED = PLACED, sz = ACC_SZ);
	
	final = Final(PLACED = PLACED, isz = ACC_SZ, osz = MEM_SZ);
	
structure

	X0 --> scale.X0;
	X1 --> scale.X1;

	scale.Y0 --> combine.X0;
	scale.Y1 --> combine.X1;
	scale.Y2 --> combine.X2;
	scale.Y3 --> combine.X3;
	
	combine.Y0 --> shufflefly.X0;
	combine.Y1 --> shufflefly.X1;

	shufflefly.Y0 --> shuffle.X0;	
	shufflefly.Y1 --> shuffle.X1;	
	shufflefly.Y2 --> shuffle.X2;	
	shufflefly.Y3 --> shuffle.X3;	
	
	shuffle.Y0 --> final.X0;
	shuffle.Y1 --> final.X1;
	shuffle.Y2 --> final.X2;
	shuffle.Y3 --> final.X3;
	
	final.Y0 --> Y0;
	final.Y1 --> Y1;
	final.Y2 --> Y2;
	final.Y3 --> Y3;
	
end