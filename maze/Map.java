import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Map {
	
	private int[][] map; //= new int[22][17];
	private int[][] vMap;
	private int y;
	private int x;
	Robot r = new Robot(30,30);
	

	
	public Map(int y, int x) {
		this.y = y+2;
		this.x = x+2;
		//constructMap(x, y);
	}
	
	private void constructMap(int y, int x){
		map = new int[y][x];
		vMap = new int[y][x];
	}
	
	public void initialMap(){
		int i,j;
		constructMap(y,x);
		for(i=0;i<y;i++){
			for(j=0;j<x;j++){
				map[i][j] = 0;
			}
		}
		for(i=0;i<y;i++){
			map[i][0] = 1;
			map[i][x-1] = 1;
		}
		for(j=0;j<x;j++){
			map[0][j] = 1;
			map[y-1][j] = 1;
		}
	}
	public void readMap(){

		File file = new File("map.txt");
		FileInputStream fis = null;

		constructMap(y,x);
		
		int i=0;
		int j=0;
		try {
			fis = new FileInputStream(file);
 
			System.out.println("Total file size to read (in bytes) : "+ fis.available());
 
			int content;
			while ((content = fis.read()) != -1) {
				if((char)content=='\n'){
					j=0;
					i++;
				}else if((char)content==' '){
					
				}else{
					map[i][j] =  Integer.parseInt((char)content+"");
					j++;
				}
				// convert to char and display it
				System.out.print((char) content);
				System.out.print("_");
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
	
	String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}
	
	
	public void initialMapV2(){
		int i,j;
		Random generator = new Random();
		constructMap(y,x);
		for(i=0;i<y;i++){
			for(j=0;j<x;j++){
				if(generator.nextInt(20)>17)
					map[i][j] = 1;
				else
					map[i][j] = 0;
			}
		}
		map[1][1] = 0;
		map[2][1] = 0;
		map[1][2] = 0;
		map[2][2] = 0;
		map[15][20] = 0;
		map[14][20] = 0;
		map[14][19] = 0;
		map[15][19] = 0;
		for(i=0;i<y;i++){
			map[i][0] = 1;
			map[i][x-1] = 1;
		}
		for(j=0;j<x;j++){
			map[0][j] = 1;
			map[y-1][j] = 1;
		}
	}
	
	public void constructVirtualMap(){
		int i,j;
		for(i=0;i<y-1;i++){
			for(j=0;j<x-1;j++){
				if(map[i][j]+map[i+1][j]+map[i][j+1]+map[i+1][j+1]>=1)
					vMap[i][j] = 1;
				else 
					vMap[i][j] = 0;
			}
		}
		for(i=0;i<y-1;i++)
			vMap[i][x-1] = 1;
		for(j=0;j<x-1;j++)
			vMap[y-1][j] = 1;
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
		r.draw(g);
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
		//g.fillRect(x, y, width, height);
		//g.fillRect(58,300,30,30);
	}
	public void drawV(Graphics g){
		drawGridsV(g);
		drawStartAndEndV(g);
		Color c = null;
		g.setColor(Color.BLACK);
		r.drawV(g);
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
		//g.fillRect(x, y, width, height);
		//g.fillRect(58,300,30,30);
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
		g.fillRect(30, 30, 60, 60);
		g.fillRect(19*30, 14*30, 60, 60);
		g.setColor(c);
	}
	private void drawStartAndEndV(Graphics g){
		Color c = g.getColor();
		g.setColor(Color.GREEN);
		g.fillRect(30+700, 30, 30, 30);
		g.fillRect(19*30+700, 14*30, 30, 30);
		g.setColor(c);
	}
	
	public void findPath(){  //greedy
		
	}
	private Node[][] mapNode = new Node[17][22];
	private List<Node> closed = new ArrayList<Node>();
	private List<Node> open = new ArrayList<Node>();
	
	public void initialNode(){
		for(int nodei = 0;nodei<17;nodei++){
			for(int nodej = 0;nodej<22;nodej++){
				if(vMap[nodei][nodej]==0){
					mapNode[nodei][nodej] = new Node(nodei,nodej,16+21-nodei-nodej,0,0,false,false);
				}else
					mapNode[nodei][nodej] = new Node(nodei,nodej,16+21-nodei-nodej,0,0,false,true);
			}
		}
		/*for(int nodeii = 0;nodeii<17;nodeii++){
			mapNode.get(nodeii*22).setWall(true);
			mapNode.get(nodeii*22+21).setWall(true);
		}
		for(int nodejj = 0;nodejj<17;nodejj++){
			mapNode.get(nodejj*22).setWall(true);
			mapNode.get(nodejj*22+21).setWall(true);
		}*/
		
	}
	public void testPrintNode(){
		/*for(Node item : mapNode){
			System.out.println(item.getX()+"  "+item.getY()+"  "+item.isWall());
		}*/
		System.out.println();
		for(int nodei = 0;nodei<17;nodei++){
			for(int nodej = 0;nodej<22;nodej++){
				if(mapNode[nodei][nodej].isWall()){
					System.out.print(1+" ");
				}
				else
					System.out.print(0+" ");
				//System.out.println();
					//mapNode[nodei][nodej] = new Node(nodei,nodej,16+21-nodei-nodej,0,0,false,true);
			}
			System.out.println();
		}
		
	}
	public void findShortestPath(){   //A*
		//Node node_goal = new Node(null,null,1,15,15);
		//open.add(mapNode.get())
		//var curr = currentNode;
		//var ret = [];
		//while(curr.parent) {
		//	ret.push(curr);
		//	curr = curr.parent;
		//}
		//return ret.reverse();
		//Node curr = null;
		Node cur = null;
		List<Node> near = new ArrayList<Node>();
		int gScore;
		boolean gBest;
		open.add(mapNode[1][1]);
		//System.out.println("b1");
		while(!open.isEmpty()){
			cur = findLowest();
			//updateVMap(cur.getX(),cur.getY(),2);
			//System.out.println(cur.getX()+" "+cur.getY());
			if((cur.getX() == 14)&&(cur.getY() == 19)){
				while(cur.getParent()!=null){
					System.out.println(cur.getX()+" "+cur.getY());
					updateMap(cur.getX(),cur.getY(),2);
					updateMap(cur.getX()+1,cur.getY(),2);
					updateMap(cur.getX(),cur.getY()+1,2);
					updateMap(cur.getX()+1,cur.getY()+1,2);
					updateVMap(cur.getX(),cur.getY(),2);
					cur = cur.getParent();
					
				}
				//return
				return;
				//break;
			}
			closed.add(cur);
			open.remove(cur);
			
			
		near = findNear(cur);
		//System.out.println("b2");
		
		
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
		}}
		
	}
	public void findMap(){   //explore
		
	}
	private List<Node> findNear(Node cur){
		List<Node> near = new ArrayList<Node>();
		//System.out.println("b3");
		if(cur.getX()>0)
			near.add(mapNode[cur.getX()-1][cur.getY()]);
		if(cur.getX()<17)
			near.add(mapNode[cur.getX()+1][cur.getY()]);
		if(cur.getY()>0)
			near.add(mapNode[cur.getX()][cur.getY()-1]);
		if(cur.getX()<22)
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
		r.moveRight();
	}

}
/*
push startNode onto openList
while(openList is not empty) {
   currentNode = find lowest f in openList
   if currentNode is final, return the successful path
   push currentNode onto closedList and remove from openList
   foreach neighbor of currentNode {
       if neighbor is not in openList {
              save g, h, and f then save the current parent
              add neighbor to openList
       }
       if neighbor is in openList but the current g is better than previous g {
               save g and f, then save the current parent
       }
   }
*/