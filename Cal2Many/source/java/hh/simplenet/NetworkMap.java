package hh.simplenet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import hh.AST.syntaxtree.*;
import hh.common.Pair;
import hh.common.translator.NetVisitor;



public class NetworkMap {
	List<String> channelPlaceName = new ArrayList<>();
	public List<Pair<String, Integer>> entityPlace;	
	public List<Pair<Integer, Integer>> channelPlace;
	public Map<String, int[]> SIMmap = new HashMap<>();
	public List<List<String>> SIMchs = new ArrayList<>();
	public int comlibVer;
	public NetworkMap(FlatNetwork fnet, int comlib) {
		int [] spline 			= {0,1,2,3,7,6,5,4,8,9,10,11,15,14,13,12}; 
		int [] splineOffchip 	= {3,2,1,0,4,5,6,7,11,10,9,8,12,13,14,15};
		int [] defaultMap 		= {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		boolean IsSplineOffchip = false;
		boolean IsSpline = true;
		boolean useFixedMaps=false;	
		
		
		comlibVer = comlib;
		HashMap<String, Integer>  mapentityPlace = new HashMap<String, Integer>();	
		entityPlace = new ArrayList<Pair<String, Integer>>();
		int eCores=0;
		boolean IsDefaultMap = false;
		int procEle =0;
		if(fnet.entities.size()>16){
			for(Entity ens:fnet.entities){
				mapentityPlace.put(ens.i.s,procEle++);
			}
		}
		else
		if(useFixedMaps){
			
			for(Entity ens:fnet.entities){
				System.out.println(ens.i.s);
			}
			mapentityPlace.put("motionsearchwin",			14);
			mapentityPlace.put("acdcseq",					12);
			mapentityPlace.put("acdcdcpred",				8);
			mapentityPlace.put("motionmemorymanager", 		13);
			mapentityPlace.put("motionunpack",			 	9);
			mapentityPlace.put("acdczzaddr", 				0);
			mapentityPlace.put("acdczigzag",				1);
			mapentityPlace.put("acdcdcsplit",				2);
			mapentityPlace.put("motioninterpolate",			10);
			mapentityPlace.put("motionadd",					11);
			mapentityPlace.put("motionmbpack",				4);
			mapentityPlace.put("middleguy",					3);
			mapentityPlace.put("idct2d",					6);
			mapentityPlace.put("acdcdequant",				5);
			mapentityPlace.put("acdcacpred",				7);
			mapentityPlace.put("ddr",						15);
			
			for(Entry<String, Integer> menp:mapentityPlace.entrySet()){
				entityPlace.add(new Pair<String, Integer>(menp.getKey(),menp.getValue()));
			}

			
		}
		else{
			if(IsSpline)
				for(int i=0;i<16;i++)
					defaultMap[i] = spline[i];
			if(IsSplineOffchip)
				for(int i=0;i<16;i++)
					defaultMap[i] = splineOffchip[i];
			int countEntity=0;
			for(Entity ens:fnet.entities){
				System.out.println("fixedMap.add(, \"" + ens.i.s+"\");");

				countEntity++;
				if(fnet.entities.size()==countEntity){
					if((IsDefaultMap==false) && IsSplineOffchip)
						if(eCores<7)
							eCores = 7;
						else if(eCores == 7)
							eCores = 8;
						else if(eCores<15)
							eCores = 15;
				}
				if(ens.parms.size()>0){
					VarDeclSimp place = (VarDeclSimp)ens.parms.get(0);
					if(place.i.s.equals("PLACED")){
						entityPlace.add(new Pair<String, Integer>(ens.i.s,((IntegerLiteral)place.e).i));
						mapentityPlace.put(ens.i.s,((IntegerLiteral)place.e).i);
						IsDefaultMap = true;
					}
					else
					{
						entityPlace.add(new Pair<String, Integer>(ens.i.s,defaultMap[eCores]));
						mapentityPlace.put(ens.i.s,defaultMap[eCores++]);
					}

				}
				else{
					entityPlace.add(new Pair<String, Integer>(ens.i.s,defaultMap[eCores]));

					mapentityPlace.put(ens.i.s,defaultMap[eCores++]);
				}
			}
		}

		int cores[][] ={{0,0},{0,1},{0,2},{0,3},
				{1,0},{1,1},{1,2},{1,3},
				{2,0},{2,1},{2,2},{2,3},
				{3,0},{3,1},{3,2},{3,3}};
		//	map.put("A", cores[5]);
		for(Entry<String, Integer> en:mapentityPlace.entrySet()){
			int i = en.getValue();
			if(i<16)
				SIMmap.put(en.getKey(), cores[i]);
							
		}


		channelPlace = new ArrayList<Pair<Integer, Integer>>();


		for(Channel c:fnet.chs){
			List<String> schs = new ArrayList<>();

			int source=0,destination=0;

			boolean isTop = false;
			//    System.out.print(c.p1.i.s + "--> " + c.p2.i.s + "  ");
			if(c.p1.i.s!=null){
				if(mapentityPlace.get(c.p1.i.s)!=null){
					source =mapentityPlace.get(c.p1.i.s);
					schs.add(c.p1.i.s);
					schs.add(c.p1.p.s);

				}
				else
				{
					isTop=true;
					//					source.add(4);
					//					source.add(4);
				}
			}
			else{
				isTop=true;
				//				source.add(4);
				//				source.add(4);
			}

			if(c.p2.i.s!=null){
				if(mapentityPlace.get(c.p2.i.s)!=null){
					destination = mapentityPlace.get(c.p2.i.s);
					schs.add(c.p2.i.s);
					schs.add(c.p2.p.s);
				}
				else{
					isTop=true;
					//					destination.add(4);
					//					destination.add(4);
				}

			}
			else{
				isTop=true;
				//				destination.add(4);
				//				destination.add(4);
			}
			if(!isTop){
				channelPlace.add(new Pair<Integer, Integer>(source,destination));
				channelPlaceName.add(c.p1.p.s + "--->"+c.p2.p.s);
			}
			else
				c.toString();
			//System.out.println(source.toString()+" " + destination.toString());
			SIMchs.add(schs);
		}
		System.out.println("");
	}
	public String toString(){
		String sHeap="";
		for(Pair<String, Integer> entP:entityPlace){
			sHeap = sHeap+"#define HEAP_SZ_" + entP.getLeft() + "\t\t\t\t\tHEAP_SZ\n";
		}
		String sArray ="";
		for(int iLoc = 0; iLoc <= entityPlace.size(); iLoc++){
			for(Pair<String, Integer> entP:entityPlace)
				if(iLoc == entP.getRight())
					sArray = sArray + "\"bin/"+ entP.getLeft()+".elf\", ";
		}
		String sPrint="";
		for(Pair<String, Integer> entP:entityPlace){			
			sPrint = sPrint + "\tload_result = e_load(\"bin/"+ entP.getLeft() + ".srec\", &dev, "
					+ getCoreXY(entP.getRight())+", E_FALSE);     /** {"+entP.getRight()+", 0} **/\n";
			sPrint = sPrint + "\tfprintf(stderr, \"Loading "+ entP.getLeft()+".srec ... result: %d \\n\", load_result);\n";			
		}
		String sPrintCh="";
		int chaNo=0;
		int ni=0;
		for(Pair<Integer, Integer> SorDes:channelPlace){
			/*if(!sPrintCh.equals(""))
				sPrintCh = sPrintCh + ",\n";*/


			if(comlibVer == 3){
				//DEFAULT(15, 14, TOKEN_NUM, TOKEN_SIZE),

				sPrintCh = sPrintCh + "/* " + (chaNo++) + " */ DEFAULT("+ SorDes.getLeft()+", " +
						SorDes.getRight()+",TOKEN_NUM, TOKEN_SIZE), /** ("+channelPlaceName.get(ni) +")" +getEntityName(SorDes.getLeft())+ "--> "+getEntityName(SorDes.getRight())+"**/\n";       	


			}
			else{
				// /* 0 */ {COMM_CTYPE_DEFAULT,	{15, 0}, { 14, 0 }, TOKEN_SIZE, NUM_TOKEN, }, /** act15--> act14 **/

				sPrintCh = sPrintCh + "/* " + (chaNo++) + " */ {COMM_CTYPE_DEFAULT,	{"+
						SorDes.getLeft()+", 0}," +
						" { "+SorDes.getRight()+", 0 }, 4, NUM_TOKEN, }, /** ("+channelPlaceName.get(ni) +")" +getEntityName(SorDes.getLeft())+ "--> "+getEntityName(SorDes.getRight())+" **/\n";       	
			}
			ni++;

		}


		return sHeap +"\n\n\n" + sArray + "\n\n\n"+sPrint +"\n\n\n"+sPrintCh;

	}
	private String getCoreXY(Integer id) {

		return (id / 4) + ", " + id % 4;
	}
	private String getEntityName(int core) {
		for(Pair<String, Integer> entP:entityPlace){	
			if(entP.getRight()==core)
				return entP.getLeft();
		}
		return null;
	}
	public NetworkMap(NetworkMap map){
		entityPlace = map.entityPlace;
		channelPlace = map.channelPlace;
	}	
}
