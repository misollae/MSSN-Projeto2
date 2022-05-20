package aa;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import tools.SubPlot;

public class Spaceship extends Boid {
	public Spaceship(PVector pos, float mass, float radius, int color, PApplet p, SubPlot plt) {
		super(pos, mass, radius, color, p, plt);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void setShape(PApplet p, SubPlot plt) {
		float[] rr = plt.getDimInPixel(radius, radius);
		shape = p.createShape();
		shape.beginShape();
		shape.stroke(p.color(58, 54, 55));;
		shape.fill(p.color(252, 180, 58));
		shape.vertex(-rr[0], rr[0]/2);
		shape.vertex(rr[0], 0);
		shape.vertex(-rr[0], -rr[0]/2);
		shape.vertex(-rr[0]/2, 0);
		shape.endShape(PConstants.CLOSE);
	}

	@Override
	public void display(PApplet p, SubPlot plt) {
		p.pushMatrix();
		float[] pp = plt.getPixelCoord(pos.x, pos.y);
		float[] rr = plt.getDimInPixel(radius, radius);
		p.strokeWeight(rr[0]/10f);
		p.stroke(p.color(58, 54, 55));
		p.translate(pp[0], pp[1]);
		p.rotate(-vel.heading());
		p.fill(p.color(253, 227, 143));   
		p.arc(0f - rr[0]/1.8f, 0f, rr[0]/2f, rr[0], (float)0, (float)((2*Math.PI)));
		p.fill(p.color(185, 160, 181));   
		p.arc(0f - rr[0]/4.4f, 0f, rr[0]/1.3f, 2*rr[0], (float)0, (float)((2*Math.PI)));
		p.fill(p.color(177, 220, 210));   
		p.line(0f, 0f, rr[0]*1.05f, 0f);
		p.arc(0f, 0f, rr[0]/2.5f, rr[0], (float)(Math.PI/2f) - 0.1f, (float)((3*Math.PI/2f) + 0.1));
		p.rotate((float) Math.PI);
		p.arc(0f, 0f, rr[0], rr[0], (float)(Math.PI/2f) - 0.1f, (float)((3*Math.PI/2f) + 0.1));
		p.rotate((float) Math.PI);
		p.fill(p.color(253, 227, 143));
		p.circle(rr[0]*1.05f, 0f, rr[0]/4);
		p.popMatrix();
	}
}
