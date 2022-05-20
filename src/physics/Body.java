package physics;
import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

public class Body extends Mover {
	public int color;
	protected float radius;

	public Body(PVector pos, PVector vel, float mass, float radius, int color) {
		super(pos, vel, mass);
		this.color  = color;
		this.radius = radius;
	}
	
	public boolean isInside(int x, int y, SubPlot plt) {
		float[] pp = plt.getPixelCoord(pos.x, pos.y);
		float[] r  = plt.getDimInPixel(radius, radius);				
		return ((x >= pp[0] - r[0]) && (x <= pp[0] + r[0]) && (y >= pp[1] - r[0]) && (y <= pp[1] + r[0]));
	}
	
	public float getRadius() {
		return radius;
	}
	
	public void displayTrajectory(float distanceCenter, Body center, PApplet p, SubPlot plt, int color) {
		p.pushStyle();
		float[] pp = plt.getPixelCoord(center.getPos().x, center.getPos().y);
		float[] r  = plt.getDimInPixel(distanceCenter, distanceCenter);

		p.noFill();
		p.stroke(90, 100, 124);
		p.strokeWeight(0.7f);
		p.ellipse(pp[0], pp[1], 2*r[0], 2*r[1]);
		p.popStyle();
	}
	
	public void displayName(PApplet p, SubPlot plt, String name, int color) {
		p.pushStyle();
		float[] pp = plt.getPixelCoord(pos.x, pos.y);
		float[] r  = plt.getDimInPixel(radius, radius);

		p.fill(color);
		
		p.textSize(12);
		p.text("\"" + name + "\"", pp[0] + 8 + r[0], pp[1] - 52 + r[1]); 
		p.text("Dist. to Sun: "  + pos.x, pp[0] + 8  + r[0], pp[1] - 40 + r[1]); 
		p.text("Speed: "   + vel.x, pp[0] + 8  + r[0], pp[1] - 29 + r[1]); 
		p.text("Mass: "    + mass, pp[0] + 8  + r[0], pp[1] - 17 + r[1]); 
		p.text("Radius: "  + radius, pp[0] + 8  + r[0], pp[1] - 5 + r[1]); 
		p.popStyle();
	}
	
	public void display(PApplet p, SubPlot plt) {
		p.pushStyle();
		float[] pp = plt.getPixelCoord(pos.x, pos.y);
		float[] r  = plt.getDimInPixel(radius, radius);
		p.noStroke();
		p.fill(color);
		p.circle(pp[0], pp[1], 2*r[0]);
		p.popStyle();
	}
}
