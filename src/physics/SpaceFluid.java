package physics;

import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

public class SpaceFluid extends Fluid{
	private float radius;
	private int color;
	private PVector pos;

	public SpaceFluid(int color, PVector pos) {
		super(.5f);
		this.radius = 60;
		this.color  = color;
		this.pos    = pos;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	public boolean isInside(float x, float y) {
		boolean inside1 = Math.pow(x - (pos.x - radius/2), 2) + Math.pow(y - (pos.y + radius/3), 2)  <= Math.pow(radius/2, 2);
		boolean inside2 = Math.pow(x - (pos.x - radius/2 + radius/2), 2) + Math.pow(y - (pos.y + radius/3 - radius/2), 2) <= Math.pow(radius/2, 2);
		return (inside1 || inside2);
	}
	
	public PVector drag(Body b){
		float area = PApplet.pow(b.radius, 2.0f) * PApplet.PI;
		float mag = b.vel.mag();
		return PVector.mult(b.vel, -0.5f*density*area*mag);
	}
	
	public void display(PApplet p, SubPlot plt) {
		p.pushStyle();
		p.noStroke();
		p.fill(color);
		p.arc(pos.x - radius/2, pos.y + radius/3, radius, radius, (float) (-2*Math.PI), (float) (-Math.PI/2));
		p.rect(pos.x - radius/2, pos.y - radius/6, radius/2, radius/2);
		p.arc(pos.x - radius/2 + radius/2, pos.y + radius/3 - radius/2, radius, radius, (float) (-Math.PI), (float) (Math.PI/2));
		p.popStyle();
	}
}
