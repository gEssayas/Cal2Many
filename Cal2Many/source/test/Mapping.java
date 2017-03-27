import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Mapping extends JPanel {
    public int coreL = 100;
    public int coreLR = (coreL * 7) /10; 
    public int routerL = coreL/3;
    public int routerLR = (routerL * 7) /10;
    public int coreTorouter = 5;
    public int nodeL = coreL + routerL + coreTorouter + 15;
	public Map<Integer,Integer> numSIMChannel = new HashMap<>(); 
	
	/*   PE  -- 0--  PE  -- 1--  PE  -- 2--  PE
	 *    |           |          |            |
	 *   12         13           14           15   
	 *    |           |          |            |
	 *   PE  -- 3--  PE  -- 4--  PE  -- 5--  PE
	 *    |           |          |            |
	 *   16         17           18           19 
	 *    |           |          |            |
	 *   PE  -- 6--  PE  -- 7--  PE  -- 8--  PE 
	 *    |           |          |            |
	 *   20         21          22           23
	 *    |           |          |            |
	 *   PE  -- 9--  PE  --10--  PE  --11--  PE
	 * */
	
	public Map<String, int[]> SIMmap = new HashMap<>();
	public List<SIMChannel> SIMchs = new ArrayList<>(); 

    public class  SIMChannel{
    	public String sa;
    	public String sp;
    	public String da;
    	public String dp;
    	public SIMChannel(String asa,String asp,String ada, String adp ){
    		sa = asa; sp=asp; da =ada; dp=adp;
    	}
    	public SIMChannel(List<String> chs ){
    		sa = chs.get(0); sp=chs.get(1); da =chs.get(2); dp=chs.get(3);
    	}
    }

	public Mapping(Map<String, int[]> aSIMmap, List<List<String>> aSIMchs) {
		SIMmap.putAll(aSIMmap);
		for(List<String> chs:aSIMchs){
			if(chs.size()==4)
				SIMchs.add(new SIMChannel(chs));
		}
	}
	public void drawCore(Graphics g, int a, int b) {
		int core_xpoints[] = {a,  a+coreL,  a + coreL,   a + coreLR,  a        };
		int core_ypoints[] = {b,  b      ,  b + coreLR,  b + coreL,   b + coreL};
		
		int ra = a + coreL + coreTorouter;
		int rb = b + coreL + coreTorouter;
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        g2.draw(new Line2D.Float((a + coreL + a + coreLR)/2, (b + coreLR + b + coreL)/2,
        		(ra+ ra + routerL-routerLR)/2, (rb + routerL-routerLR+ rb)/2));      
		
        int router_xpoints[] = {ra, ra + routerL-routerLR, ra + routerL, ra + routerL, ra};
		int router_ypoints[] = {rb + routerL-routerLR, rb, rb, rb + routerL, rb + routerL};

		int connect_xpoints[] = {(a + coreL + a + coreLR)/2};
		int connect_ypoints[] = {};
		
		int npoints = 5;

		g2.drawPolygon(core_xpoints, core_ypoints, npoints);
		g2.drawPolygon(router_xpoints, router_ypoints, npoints);
		
 		
	}	
	private void drawCoreArray(Graphics g, int a, int b) {
		int corea,coreb;
		for(int i=0;i<4;i++){
			corea = a;
			coreb = b + i*(nodeL);
			for(int j=0;j<4;j++){
				drawCore(g, corea + j*(coreL + routerL + coreTorouter+15), coreb);
			}
		}
	}
	private void drawNetwork(Graphics g, int a, int b, int[] sr, int[] ds) {
          
		int sra = a + coreL + coreTorouter + sr[1]*(nodeL);
		int srb = b + coreL + coreTorouter + sr[0]*(nodeL);
		int start = routerL - routerLR +2;
		
		int srCore = sr[0]*4 + sr[1];
		int dsCore = ds[0]*4 + ds[1];
		//horizontal		
		if(sr[1] < ds[1]){
			for(int i= sr[1];i<ds[1];i++){
				int k= i+ 3*sr[0];
				if(numSIMChannel.containsKey(k))
					numSIMChannel.put(k,numSIMChannel.get(k) +1);				
				else 
					numSIMChannel.put(k, 1);
				int st=(numSIMChannel.get(k)-1) *4;
				g.drawLine(sra+routerL, srb+start+st, sra+nodeL, srb+start+st);
				sra = sra + nodeL;
			}
		}
		else{
			for(int i= sr[1];i>ds[1];i--){
				int k= i+ 3*sr[0]-1;
				if(numSIMChannel.containsKey(k))
					numSIMChannel.put(k,numSIMChannel.get(k) +1);				
				else 
					numSIMChannel.put(k, 1);
				int st=(numSIMChannel.get(k)-1) *4;
				
				g.drawLine(sra, srb+start+st, sra-nodeL, srb+start+st);
				sra = sra - nodeL;
			}

		}
		// vertical	
		if(sr[0]<ds[0]){
			for(int i= sr[0];i<ds[0];i++){
				int k= ds[1]*3 + 11 + i+1;
				if(numSIMChannel.containsKey(k))
					numSIMChannel.put(k,numSIMChannel.get(k) +1);				
				else 
					numSIMChannel.put(k, 1);
				int st=(numSIMChannel.get(k)-1) *4;
				
				
				g.drawLine(sra+start+st, srb+routerL, sra+start+st, srb+nodeL);
				srb = srb + nodeL;
			}			
		}
		else {
			for(int i = sr[0];i>ds[0];i--){
				int k= ds[1]*3 + 11 + i;
				if(numSIMChannel.containsKey(k))
					numSIMChannel.put(k,numSIMChannel.get(k) +1);				
				else 
					numSIMChannel.put(k, 1);
				int st=(numSIMChannel.get(k)-1) *4;

				g.drawLine(sra+start+st, srb, sra+start+st, srb-nodeL+routerL);
				srb = srb - nodeL;
			}
			
		}
		System.out.println(numSIMChannel.toString());

		
	}
	public void drawNetwork(Map<String,int[]> map, List<SIMChannel> chs,Graphics g, int a, int b){
		drawCoreArray(g,a,b);
		Map<String,Integer> ports = new HashMap<>();
		for(SIMChannel ch:chs){
			int[] source = map.get(ch.sa);
			int[] destination = map.get(ch.da);
			int red = (int)(Math.random()*256);
			int green = (int)(Math.random()*256);
			int blue = (int)(Math.random()*256);
			g.setColor(new Color(red,green,blue));
			drawNetwork(g,a,b,source,destination);
			
			
		}

    }
	

	public void paint(Graphics g) {
				
		numSIMChannel.clear();
		drawNetwork(SIMmap, SIMchs, g, 100, 100);
		System.out.println("numSIMChannel \n" + numSIMChannel.toString());
		
	}

}