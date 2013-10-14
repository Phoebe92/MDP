//latest version of old algo
package maze;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Map {
	
	private int[][] map; 
	private int[][] vMap;
	private int y;
	private int x;
	Robot r = new Robot(1,1);
	Client mp = new Client();

	public Map(int y, int x) {
		this.y = y+2;
		this.x = x+2;
		this.map = new int[y+2][x+2];
		this.vMap = new int[y+2][x+2];
	}
	
	public void readMap(){

		File file = new File("map1.txt");
		FileInputStream fis = null;
		
		int i=0;
		int j=0;
		try {
			fis = new FileInputStream(file);
			int content;
			while ((content = fis.read()) != -1) {
				if ((char)content == '1' || (char)content == '0'){
                    map[i][j] =  Character.getNumericValue((char)content);
                    j++;                                
                }else if((char)content == '\n'){
                    j=0;
                    i++;
                }
				
				/*if((char)content=='\n'){
					j=0;
					i++;
				}else if((char)content==' '){
					
				}else{
					System.out.println(content);
					System.out.println((char)content);
					System.out.println(Integer.parseInt("9"));
					String cont = (char) content + "";
					map[i][j] =  Integer.parseInt('9');
					j++;
				}*/
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void constructMap(){
		int i,j;
		for(i=0;i<y;i++){
			for(j=0;j<x;j++){
				if(map[i][j]==1)
					map[i][j] = 1;
				else 
					map[i][j] = 0;
			}
		}
	}
	
	public void refreshMap(){
		int i, j;
		for(i = 1; i < (y - 1); i++){
			for (j = 1; j < (x - 1); j++){
				map[i][j] = 0;
			}
		}
	}
	
	
	public void constructVirtualMap(){
		int i,j;
		for(i=0;i<y-2;i++){
			for(j=0;j<x-2;j++){
				if(map[i][j]==1||map[i+1][j]==1||map[i][j+1]==1||map[i+1][j+1]==1||map[i+2][j]==1||map[i+2][j+1]==1||map[i+2][j+2]==1||map[i+1][j+2]==1||map[i][j+2]==1)
					vMap[i][j] = 1;
				else 
					vMap[i][j] = 0;
			}
		}
		for(i=0;i<y-2;i++){
			vMap[i][x-1] = 1;
			vMap[i][x-2] = 1;
		}
		for(j=0;j<x-2;j++){
			vMap[y-1][j] = 1;
			vMap[y-2][j] = 1;
		}
		vMap[y-1][x-1]=1;
		vMap[y-2][x-1]=1;
		vMap[y-1][x-2]=1;
		vMap[y-2][x-2]=1;
	}
	
	public void list(){
		int i,j;
		for(i=0;i<y;i++){
			for(j=0;j<x;j++){
				System.out.print(map[i][j]+" ");
			}
			System.out.println();
		}
	}
	public void listV(){
		int i,j;
		for(i=0;i<y;i++){
			for(j=0;j<x;j++){
				System.out.print(vMap[i][j]+" ");
			}
			System.out.println();
		}
	}

	
	public void draw(Graphics g){
		drawGrids(g);
		drawStartAndEnd(g);
		Color c = null;
		g.setColor(Color.BLACK);
		//r.draw(g);
		int i,j;
		for(i=0;i<y;i++){
			for(j=0;j<x;j++){
				if(map[i][j]==1)
					g.fillRect(j*30, i*30, 30, 30);
				if(map[i][j]==2){
					c = g.getColor();
					g.setColor(Color.BLUE);
					g.fillRect(j*30, i*30, 30, 30);
					g.setColor(c);
				}
			}
		}
		drawV(g);
		drawMist(g);
		r.draw(g);
		r.drawV(g);
	}
	public void drawV(Graphics g){
		drawGridsV(g);
		drawStartAndEndV(g);
		Color c = null;
		g.setColor(Color.BLACK);
		//r.drawV(g);
		int i,j;
		for(i=0;i<y;i++){
			for(j=0;j<x;j++){
				if(vMap[i][j]==1)
					g.fillRect(j*30+700, i*30, 30, 30);
				if(vMap[i][j]==2){
					c = g.getColor();
					g.setColor(Color.BLUE);
					g.fillRect(j*30+700, i*30, 30, 30);
					g.setColor(c);
				}
			}
		}
	}
	
	public void drawMist(Graphics g){
		
	}
	
	private void drawGrids(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.BLACK);
	    for (int i = 0; i <= y; i++)
	    	g.drawLine(0, i * 30, x * 30, i * 30);
	    for (int j = 0; j <= x; j++)
	    	g.drawLine(j * 30,0, j * 30, y * 30);
	    g.setColor(c);
	     //g.drawLine(i * space, 0, i * space, grids * space);
	}
	private void drawGridsV(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.BLACK);
	    for (int i = 0; i <= y; i++)
	    	g.drawLine(0+700, i * 30, x * 30+700, i * 30);
	    for (int j = 0; j <= x; j++)
	    	g.drawLine(j * 30+700,0, j * 30+700, y * 30);
	    g.setColor(c);
	     //g.drawLine(i * space, 0, i * space, grids * space);
	}
	
	private void drawStartAndEnd(Graphics g){
		Color c = g.getColor();
		g.setColor(Color.GREEN);
		g.fillRect(30, 30, 90, 90);
		g.fillRect(18*30, 13*30, 90, 90);
		g.setColor(c);
	}
	private void drawStartAndEndV(Graphics g){
		Color c = g.getColor();
		g.setColor(Color.GREEN);
		g.fillRect(30+700, 30, 30, 30);
		g.fillRect(18*30+700, 13*30, 30, 30);
		g.setColor(c);
	}


	private Node[][] mapNode = new Node[17][22];
	private List<Node> closed = new ArrayList<Node>();
	private List<Node> open = new ArrayList<Node>();
	
	public void setUnvisitedAsWall(){
		for (int i = 0; i < 17; i++){
			for (int j = 0; j < 22; j++){
				System.out.println(mapNode[i][j].isVisited());
				if (mapNode[i][j].isVisited() == false){
					mapNode[i][j].setWall(true);
				}
			}
		}
		
		mapNode[13][18].setWall(false);
	}
	
	public void initialNode(){
		for(int nodei = 0;nodei<17;nodei++){
			for(int nodej = 0;nodej<22;nodej++){
				if(vMap[nodei][nodej]==0||vMap[nodei][nodej]==2){
					mapNode[nodei][nodej] = new Node(nodei,nodej,16+21-nodei-nodej,0,0,false,false);
				}else
					mapNode[nodei][nodej] = new Node(nodei,nodej,16+21-nodei-nodej,0,0,false,true);
			}
		}	
	}
	
	public void testPrintNode(){
		System.out.println();
		for(int nodei = 0;nodei<17;nodei++){
			for(int nodej = 0;nodej<22;nodej++){
				if(mapNode[nodei][nodej].isWall()){
					System.out.print(1+" ");
				}
				else
					System.out.print(0+" ");
			}
			System.out.println();
		}
	}
	
	public String findShortestPath(int x, int y){   //A*
		Node cur = null;
		String s = null;
		List<Node> near = new ArrayList<Node>();
		open.clear();
		closed.clear();
		int gScore;
		boolean gBest;
		open.add(mapNode[y][x]);
		//System.out.println("b1");
		if(x == mp.getEndX() && y == mp.getEndY()){
			return s;
		}
		
		while(!open.isEmpty()){
			cur = findLowest();
			//updateVMap(cur.getX(),cur.getY(),2);
			//System.out.println(cur.getX()+" "+cur.getY());
			if((cur.getX() == mp.getEndY())&&(cur.getY() == mp.getEndX())){
				while(cur.getParent().getParent()!=null){
					//System.out.println(cur.getX()+" "+cur.getY());
					
					
					updateMap(cur.getX(),cur.getY(),2);
					updateMap(cur.getX()+1,cur.getY(),2);
					updateMap(cur.getX(),cur.getY()+1,2);
					updateMap(cur.getX()+1,cur.getY()+1,2);
					
					updateMap(cur.getX()+2,cur.getY()+1,2);
					updateMap(cur.getX()+2,cur.getY()+2,2);
					updateMap(cur.getX()+2,cur.getY(),2);
					updateMap(cur.getX()+1,cur.getY()+2,2);
					updateMap(cur.getX(),cur.getY()+2,2);
					
					updateVMap(cur.getX(),cur.getY(),2);
					cur = cur.getParent();
					if(cur.getParent().getParent()==null){
						System.out.println(cur.getY()+" "+cur.getX());
						s = Integer.toString(cur.getX())+","+Integer.toString(cur.getY());
						//r.update(cur.getY(), cur.getX());
					}
					
				}
				updateMap(cur.getX(),cur.getY(),2);
				updateMap(cur.getX()+1,cur.getY(),2);
				updateMap(cur.getX(),cur.getY()+1,2);
				updateMap(cur.getX()+1,cur.getY()+1,2);
				
				updateMap(cur.getX()+2,cur.getY()+1,2);
				updateMap(cur.getX()+2,cur.getY()+2,2);
				updateMap(cur.getX()+2,cur.getY(),2);
				updateMap(cur.getX()+1,cur.getY()+2,2);
				updateMap(cur.getX(),cur.getY()+2,2);
				
				updateVMap(cur.getX(),cur.getY(),2);
				
				if((cur.getParent().getX()==mp.getEndY()-1 && cur.getParent().getY()== mp.getEndX())||(cur.getParent().getX()==mp.getEndY()+1&&cur.getParent().getY()==mp.getEndX())||(cur.getParent().getX()==mp.getEndY()&&cur.getParent().getY()==mp.getEndX()+1)||(cur.getParent().getX()==mp.getEndY()&&cur.getParent().getY()==mp.getEndX()-1)){			
					s = Integer.toString(cur.getX())+","+Integer.toString(cur.getY());
				}
				
				return s;
			}
			closed.add(cur);
			mapNode[cur.getX()][cur.getY()].setVisited(true);
			//System.out.println("set true");
			open.remove(cur);
						
		near = findNear(cur);
			
		for(Node nearNodes : near){
			if(nearNodes.isWall()||findInClosed(nearNodes)){
				continue;
			}
			//near = findNear(cur);
			gBest = false;
			gScore = cur.getG() + 1;
			if(!findInOpen(nearNodes)){
				gBest = true;
				//near.setH() = astar.heuristic(neighbor.pos, end.pos);
				open.add(nearNodes);
				//openList.push(neighbor);
			}else if(gScore < nearNodes.getG()) {
				// We have already seen the node, but last time it had a worse g (distance from start)
				gBest = true;
			}
			if(gBest) {
				// Found an optimal (so far) path to this node.	 Store info on how we got here and
				//	just how good it really is...
				nearNodes.setParent(cur);
				nearNodes.setG(gScore);
				nearNodes.setF(nearNodes.getG()+nearNodes.getH());
				//nearNodes.debug = "F: " + neighbor.f + "<br />G: " + neighbor.g + "<br />H: " + neighbor.h;
			}
		}
	}	
		return s;
	}	
	private List<Node> findNear(Node cur){
		List<Node> near = new ArrayList<Node>();
		if(cur.getX()>0)
			near.add(mapNode[cur.getX()-1][cur.getY()]);
		if(cur.getX()<17)
			near.add(mapNode[cur.getX()+1][cur.getY()]);
		if(cur.getY()>0)
			near.add(mapNode[cur.getX()][cur.getY()-1]);
		if(cur.getY()<22)
			near.add(mapNode[cur.getX()][cur.getY()+1]);
		return near;
	}
	private Node findLowest(){
		Node node = open.get(0);
		for(Node nodes : open){
			if(nodes.getF() < node.getF()){
				node = nodes;
			}
		}
		return node;
	}
	private boolean findInClosed(Node nearNodes){
		for(Node node : closed){
			if(node.getX()==nearNodes.getX() && node.getY() == nearNodes.getY())
				return true;
		}
		return false;
	}
	private boolean findInOpen(Node nearNodes){
		for(Node node : open){
			if(node.getX()==nearNodes.getX() && node.getY() == nearNodes.getY())
				return true;
		}
		return false;
	}
	
	
	
	
	public void explore(){
		System.out.println("Explore");
	}
	
	public String[] recieve(){
		Scanner sc = new Scanner(System.in);
		String s = sc.next();
		String[] parts = s.split("\\|");
		return parts;
	}
	
	public void updateMap(String[] s){
		int i=0;
		int x,y;
		String[] parts;
		for(;i<s.length;i++){
			parts = s[i].split(",");
			x = Integer.parseInt(parts[0]);
			y = Integer.parseInt(parts[1]);
			System.out.println(s[i]);
			if ((x > 0 && x < 21) && (y > 0 && y < 16)){
				updateMap(y,x,1);
			}
		}
	}

	
	public void updateMap(int y,int x,int value){
		map[y][x] = value;
		//r.moveRight();
	}
	public void updateVMap(int y,int x,int value){
		vMap[y][x] = value;
		//r.moveRight();
	}
	public void updateRMap(int y,int x,int value){
		//vMap[y][x] = value;
		//r.moveRight();
	}

}