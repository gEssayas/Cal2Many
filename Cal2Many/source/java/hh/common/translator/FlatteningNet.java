package hh.common.translator;



import hh.AST.syntaxtree.*;
import hh.simplenet.*;

import java.util.HashSet;




public class FlatteningNet {
	private static int topnet=0;	
	private static ChannelList netChs = new ChannelList();
	private static ChannelList fnetChs = new ChannelList();
	public static HashSet<String> hsNets = new HashSet<String>();
	private static FlatNetwork fnet = new FlatNetwork(null,new FormalList(), new PortList(),new PortList(), new VarDeclList(), new EntityList(), new ChannelList());
	//private static FlatNetwork top_net = new FlatNetwork(null,new FormalList(), new PortList(),new PortList(), new VarDeclList(), new EntityList(), new ChannelList());
	//private static FlatNetwork top_net;

	//Gets the AST of each network with ther names and the name of the top level network


	public static FlatNetwork Flatten(FlatNetwork [] nets, String [] nlnames, String topNL) {
		final  FlatNetwork [] networks=CopyNet(nets);
		for(String s:nlnames)
			if(!s.equals(topNL))
				hsNets.add(s);

		System.out.println("Flatten is called network size " + networks.length);


		if( networks.length == 1) 
			if( networks[0].vl.size()==0){

				return networks[0];
			}
			else{
				fnet=PassVarVals(networks[0]);
				fnet.vl.clear();

				return fnet;
			}

		for(int i=0; i< networks.length;i++){
			if(networks[i].it.s.equals(topNL)){
				networks[i].i=new Identifier(topNL);
				topnet=i;
				break;
			}
		}
		for(int i=0; i<networks.length;i++)
			networks[i]=PassVarVals(networks[i]);		

		fnet.it=new IdentifierType(networks[topnet].it.s);
		//		if(networks[topnet].fls !=null)
		//			fnet.fls.addAll(networks[topnet].fls);	 
		//fnet.chs.addAll(networks[topnet].chs);
		fnet.input.addAll(networks[topnet].input);
		fnet.output.addAll(networks[topnet].output);

		FlattenNL(networks,networks[topnet],null,nlnames,"");
		// fnet.chs.clear();
		System.out.println("\nPrint all channelssssssssss in netchs\n");
		Print_Channels(netChs);

		if(netChs.size()>0)
			RemoveSubNetChannels();
		//	NewFlattenNetsChannel();
		System.out.println("\nPrint all channelssssssssss in netchs\n");
		Print_Channels(netChs);
		//	netChs.clear();

		//	       System.out.println("\nPrint all channelssssssssss in NewfnetChs\n");
		//					Print_Channels(NewfnetChs);


		System.out.println("\nPrint all channelssssssssss in fnet.chs  " + fnet.chs.size() +    "\n" );



		fnet.chs.addAll(netChs);
		//	RemoveSubNetChannels();

		System.out.println("\nPrint all channelssssssssss in fnet.chs  " + fnet.chs.size() +    "\n" );
		Print_Channels(fnet.chs);


		Print_VarList(fnet);
		return fnet;
	}

	private static void RemoveSubNetChannels() {
		ChannelList newChs= new ChannelList();
		boolean allchecked = true;
		for(int ic=0; ic<netChs.size();ic++){
			if(hsNets.contains(netChs.get(ic).p1.t.s)){		
				allchecked = false;
				newChs.addAll(ReplaceSubNetChWithActorCh(netChs.get(ic)));
			}
			else
				newChs.add(netChs.get(ic));				
		}
		netChs.clear();
		netChs.addAll(newChs);
		if(!allchecked)
			RemoveSubNetChannels();
		else{
			System.out.println("\nPrint all channelssssssssss in netchs\n");
			Print_Channels(netChs);
			for(int ic=netChs.size()-1;ic>=0;ic--)
				if(hsNets.contains(netChs.get(ic).p2.t.s))
					netChs.remove(ic);			

		}



	}

	private static ChannelList ReplaceSubNetChWithActorCh(Channel newCh) {
		ChannelList chs=new ChannelList();
		for(int ich=0;ich < netChs.size(); ich++){		
			if(newCh.p1.t.s.equals(netChs.get(ich).p2.t.s))
				if(newCh.p1.i.s.equals(netChs.get(ich).p2.i.s))
					if(newCh.p1.p.s.equals(netChs.get(ich).p2.p.s)){
						chs.add(new Channel(netChs.get(ich).p1,newCh.p2,newCh.parms));
						return chs;
					}


		}

		return chs;
	}

	private static void Print_Channels(ChannelList chs) {
		int i=0;
		for(Channel chnet: chs){
			System.out.println(i++  + " " + chnet.p1.t.s + " " +  chnet.p1.i.s + "."+chnet.p1.p.s+"--> " + chnet.p2.t.s+" "+chnet.p2.i.s+"."+chnet.p2.p.s);
		}
	}



	public static void Print_VarList(FlatNetwork fnet){
		System.out.println("Network name  " +fnet.it.s);
		for(Entity copy_en: fnet.entities){
			System.out.print("\n_______________ Entity Type "+ copy_en.it.s+ ":  "+copy_en.i.s+"(");
			for (VarDecl vrdl: copy_en.parms){
				System.out.print(" "+((VarDeclSimp)vrdl).i.s + " = ");
				if(((VarDeclSimp)vrdl).e instanceof IdentifierExp){
					System.out.print(((IdentifierExp)(((VarDeclSimp)vrdl).e)).s);
				}
				if(((VarDeclSimp)vrdl).e instanceof IntegerLiteral){
					System.out.print(((IntegerLiteral)(((VarDeclSimp)vrdl).e)).i);
				}
				if(((VarDeclSimp)vrdl).e instanceof BooleanLiteral){
					System.out.print(((BooleanLiteral)(((VarDeclSimp)vrdl).e)).value);
				}


			}

			System.out.print(")");
			System.out.print("\n");
			}

	}

	public static void FlattenNL(FlatNetwork [] networks,FlatNetwork net, VarDeclList fparm,String [] nlnames,String instName){

		CopyChannels(instName,net.chs,nlnames);
		System.out.println(fnetChs.size()+" " + netChs.size());

		for(int iEnt=0; iEnt<net.entities.size();iEnt++){
			String enName =net.entities.get(iEnt).it.s;
			int inet = TheEntityIsNL(enName,nlnames);
			if(inet==-1){
				System.out.println("network vl size "+ net.vl.size()); 
				fnet.entities.add(CopyEntity(net.entities.get(iEnt),instName,net.vl,net.fls, fparm));
			}
			else{
				copyParms(fparm,net.entities.get(iEnt).parms);
				FlattenNL(networks, networks[inet],net.entities.get(iEnt).parms,nlnames,instName+net.entities.get(iEnt).i.s);
			}			 

		}



	}

	private static void copyParms(VarDeclList fparm, VarDeclList parms) {
		// TODO Auto-generated method stub
		
		for(VarDecl v : parms){
			Exp e;
			String vName, pName;

			if(v instanceof VarDeclSimp){
				vName=((VarDeclSimp) v).i.s;
			}
			else{
				e=null;
				vName = null;
			}
			if(fparm != null)
			for(VarDecl p:fparm){
				if(p instanceof VarDeclSimp){
						e=((VarDeclSimp) p).e;
						pName=((VarDeclSimp) p).i.s;
					}
					else{
						e=null;
						pName = null;
					}
				if(vName.equals(pName)){
					((VarDeclSimp) v).e = e;
					
				}
			}
		}
		
	}

	private static void CopyChannels(String entName, ChannelList chs, String[] nlnames) {

		for(Channel c:chs){
			//			System.out.println("\nchannel " + entName);
			//			Print_Channels(c);
			//			if(c.p1.i.s!=null)
			//				if(c.p1.i.s.equals("final"))
			//					System.out.print(true);
			if(c.p1.i.s!=null)
				if(c.p2.i.s!=null)
					if(TheEntityIsNL(c.p1.t.s,nlnames)==-1 && TheEntityIsNL(c.p2.t.s,nlnames)==-1)
						fnet.chs.add(new Channel(								
								new EntityPort(c.p1.t,new Identifier(entName + c.p1.i.s),c.p1.p),								
								new EntityPort(c.p2.t,new Identifier(entName + c.p2.i.s),c.p2.p),										
								c.parms));
					else{
						netChs.add(new Channel(								
								new EntityPort(c.p1.t,new Identifier(entName + c.p1.i.s),c.p1.p),								
								new EntityPort(c.p2.t,new Identifier(entName + c.p2.i.s),c.p2.p),										
								c.parms));
					}
				else{
					netChs.add(new Channel(								
							new EntityPort(c.p1.t,new Identifier(entName + c.p1.i.s),c.p1.p),								
							new EntityPort(c.p2.t,new Identifier(entName),c.p2.p),		
							c.parms));
				}
			else if(c.p2.i.s!=null){
				netChs.add(new Channel(								
						new EntityPort(c.p1.t,new Identifier(entName),c.p1.p),								
						new EntityPort(c.p2.t,new Identifier(entName + c.p2.i.s),c.p2.p),										
						c.parms));
			}
			else{
				netChs.add(new Channel(								
						new EntityPort(c.p1.t,new Identifier(null),c.p1.p),								
						new EntityPort(c.p2.t,new Identifier(null),c.p2.p),										
						c.parms));
			}
			//			
			//			System.out.println("\n\nfnet.chs");
			//			Print_Channels(fnet.chs);
			//			System.out.println("\n\nnetChs");
			//			Print_Channels(netChs);

		}
	}



	private static Entity CopyEntity(Entity en, String s,VarDeclList vls, FormalList netfls,VarDeclList fparm) {
		VarDeclList vexs=new VarDeclList();
		boolean lookIdEx=true;
		for(int ips =0; ips<en.parms.size(); ips++){
			if(en.parms.get(ips) instanceof VarDeclSimp){
				VarDeclSimp vs =(VarDeclSimp)en.parms.get(ips);

				if(vs.e instanceof IdentifierExp){
					System.out.println("IdeExp name --> "+((IdentifierExp)vs.e).s);
					lookIdEx=true;
					for(VarDecl v :vls){
						Exp e;
						String vName;

						if(v instanceof VarDeclSimp){
							e=((VarDeclSimp) v).e;
							vName=((VarDeclSimp) v).i.s;
						}
						else{
							e=null;
							vName = null;
						}
						if( vName.equals(((IdentifierExp)vs.e).s)){
							vs.e=e;
							vexs.add(new VarDeclSimp(null,new Identifier(vs.i.s),e,true));
							lookIdEx=false;
							break;
						}
					}
					if(lookIdEx){
						if(fparm !=null)						
							if(fparm.size()>0){
								for(int ifm=0;ifm<fparm.size();ifm++){
									String vfName = netfls.get(ifm).i.s;
									lookIdEx=false;
									if( vfName.equals(((IdentifierExp)vs.e).s)){
										Exp ex=((VarDeclSimp)fparm.get(ifm)).e;
										vexs.add(new VarDeclSimp(null,new Identifier(vs.i.s),ex,true));
										lookIdEx=false;
										break;
									}

								}
							}
					}
					if(lookIdEx)
						System.out.println(" !!!!!!!!!!!!!!!!!! not found in both !!!!!!!!!!!!!!!!!!");

				}
				else{
					vexs.add(en.parms.get(ips));
				}

			}
		}


		Entity copy_en = new Entity(new IdentifierType(en.it.s), new Identifier(s+en.i.s), vexs);		
		return copy_en;
	}

	public static FlatNetwork [] CopyNet(FlatNetwork [] nets){
		FlatNetwork [] copy_nets = new FlatNetwork[nets.length];
		for(int i=0;i<nets.length;i++)
			copy_nets[i]=CopyNet(nets[i]);
		return copy_nets;
	}
	public static FlatNetwork CopyNet(FlatNetwork net){

		FlatNetwork copy_net =new FlatNetwork(null,new FormalList(), new PortList(),new PortList(), new VarDeclList(), new EntityList(), new ChannelList());
		copy_net.it=new IdentifierType(net.it.s);
		copy_net.fls.addAll(net.fls);
		copy_net.chs.addAll(net.chs);
		copy_net.entities.addAll(net.entities);
		copy_net.input.addAll(net.input);
		copy_net.output.addAll(net.output);
		copy_net.vl.addAll(net.vl);

		return copy_net;


	}
	public static int TheEntityIsNL(String ent, String [] nlnames){
		for(int i=0;i< nlnames.length;i++)
			if(nlnames[i].equals(ent))
				return i;

		return -1;
	}



	public static FlatNetwork PassVarVals(FlatNetwork net){


		for(VarDecl v : net.vl){
			Exp e;
			String vName;

			if(v instanceof VarDeclSimp){
				e=((VarDeclSimp) v).e;
				vName=((VarDeclSimp) v).i.s;
			}
			else{
				e=null;
				vName = null;
			}
			for(Entity ent: net.entities){
				for(int ips =0; ips<ent.parms.size(); ips++)
				{
					if(ent.parms.get(ips) instanceof VarDeclSimp)
						if(((VarDeclSimp)ent.parms.get(ips)).e instanceof IdentifierExp)
							if( vName.equals(((IdentifierExp)(((VarDeclSimp)ent.parms.get(ips)).e)).s)){
								VarDeclSimp vs= (VarDeclSimp)ent.parms.get(ips);
								System.out.println(" ----------------> "+vs.i.s);
								vs.e =e;
								ent.parms.remove(ips);
								ent.parms.add(ips,vs);
							}
				}
			}
		}
		return net;
	}
}