package aa;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import tools.SubPlot;

public class Bee extends Boid {
	protected Bee(PVector pos, float mass, float radius, int color, PApplet p, SubPlot plt) {
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
		p.fill(p.color(252, 180, 58));
		p.circle(0, 0, rr[0]);
		p.fill(p.color(58, 54, 55));
		p.triangle(-rr[0]/1.5f, 0, -rr[0]/2, rr[0]/6f, -rr[0]/2 , -rr[0]/6f);
		p.ellipse(0+rr[0]/1.7f,  0, rr[1]/1f, rr[0]/1.1f);
		p.noFill();
		p.stroke(p.color(58, 54, 55));
		p.strokeWeight(rr[0]/4f);
		p.rotate((float) Math.toRadians(180));
		p.arc(0, 0, rr[0]/2.5f, rr[0]/1.2f, (float) (-Math.PI/2.5), (float) (Math.PI/2.5));
		p.rotate((float) Math.toRadians(180));
		p.strokeWeight(rr[0]/6f);
		p.rotate((float) Math.toRadians(-30));
		p.arc(rr[0]/2.6f - rr[1], rr[0]-rr[0]/1.5f, rr[0]/1.7f, rr[0]/2.5f, (float) (0.2f*Math.PI/4), (float) (Math.PI));
		p.rotate((float) Math.toRadians(30));
		p.rotate((float) Math.toRadians(180+20));
		p.arc(-rr[0]/2.5f + rr[1], -rr[0]+rr[0], rr[0]/1.7f, rr[0]/2.5f, (float) (0.2f*Math.PI/4), (float) (Math.PI));
		p.rotate((float) Math.toRadians(180-20));
		p.strokeWeight(1);
		p.stroke(p.color(221, 239, 243, 110));
		p.fill(p.color(221, 239, 243, 100));
		p.rotate((float) Math.toRadians(-70));
		p.arc(rr[0]/3.5f + rr[1], -rr[0]+rr[0]*1.2f, rr[0], rr[0]/1.5f, (float) (0), (float) (2f*Math.PI));
		p.rotate((float) Math.toRadians(70));
		p.rotate((float) Math.toRadians(245));
		p.arc(-rr[0]/3.5f - rr[1], -rr[0]+rr[0]*1.2f, rr[0], rr[0]/1.5f, (float) (0), (float) (2f*Math.PI));
		p.rotate((float) Math.toRadians(-245));
		p.popMatrix();
	}
}
