import java.awt.Color;
import java.awt.Graphics;


public class Robot {
	public static final int XSPEED = 30;
	public static final int YSPEED = 30;
	public static final int STEP = 30;
	private int x,y;
	public Robot(int x, int y) {
		this.x = x;
		this.y = y;
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

	public void moveRight(){
		x+=STEP;
	}
	public void moveLeft(){
		x-=STEP;
	}
	public void moveUp(){
		y+=STEP;
	}
	public void moveDown(){
		y-=STEP;
	}
	public void draw(Graphics g){
			Color c = g.getColor();
			g.setColor(Color.RED);
			g.fillOval(x, y, 60, 60);
			g.setColor(c);
	}
	public void drawV(Graphics g){
		Color c = g.getColor();
		g.setColor(Color.RED);
		g.fillOval(x+700, y, 30, 30);
		g.setColor(c);
}
	

}
