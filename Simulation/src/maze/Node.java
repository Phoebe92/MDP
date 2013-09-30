package maze;

public class Node {
	private int x,y;
	private int h;
	private int g;
	private int f;
	private boolean visited;
	private boolean isWall;
	private Node parent;

	public Node(int x, int y, int h, int g, int f, boolean visited,
			boolean isWall) {
		this.x = x;
		this.y = y;
		this.h = h;
		this.g = g;
		this.f = f;
		this.visited = visited;
		this.isWall = isWall;
		this.parent = null;
	}
	
	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public boolean isWall() {
		return isWall;
	}

	public void setWall(boolean isWall) {
		this.isWall = isWall;
	}

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	public int getG() {
		return g;
	}
	public void setG(int g) {
		this.g = g;
	}
	public int getF() {
		return f;
	}
	public void setF(int f) {
		this.f = f;
	}
}
