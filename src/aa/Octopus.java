package aa;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import tools.SubPlot;

public class Octopus extends Boid {
	private int stroke;

	protected Octopus(PVector pos, float mass, float radius, int color, PApplet p, SubPlot plt) {
		super(pos, mass, radius, color, p, plt);
		this.color = color;
		addBehaviour(new Wander(.1f));
		addBehaviour(new Pursuit(1f));
		dna.radiusArrive = 1f;
		this.dna.maxSpeed = this.dna.maxSpeed/1.2f;
		this.dna.radiusWander = 20;
		this.dna.deltaPhiWander = this.dna.deltaPhiWander/2;
	}
	
	@Override
	public void setShape(PApplet p, SubPlot plt) {
		float[] rr = plt.getDimInPixel(radius, radius);
		shape = p.createShape();
		shape.beginShape();
		shape.stroke(p.color(58, 54, 55));
		;
		shape.fill(p.color(252, 180, 58));
		shape.vertex(-rr[0], rr[0] / 2);
		shape.vertex(rr[0], 0);
		shape.vertex(-rr[0], -rr[0] / 2);
		shape.vertex(-rr[0] / 2, 0);
		shape.endShape(PConstants.CLOSE);
	}
	
	public void applyBehaviors(float dt, int gamePhase) {
		if (eye != null) eye.look();
		PVector vd = new PVector();
		
		switch (gamePhase) {
		case 0: {
			for (Behavior behavior : behaviors.subList(2, behaviors.size())) {
				PVector vdd = behavior.getDesiredVelocity(this);
				vdd.mult(behavior.getWeight()/sumWeights);
				vd.add(vdd);
			}
			break;
		}
		case 1, 2: {
			if (behaviors.size() == 6) {
				applyBehavior(5, dt);
			}
			else {
				for (Behavior behavior : behaviors.subList(1, 3)) {
					PVector vdd = behavior.getDesiredVelocity(this);
					vdd.mult(behavior.getWeight()/sumWeights);
					vd.add(vdd);
				}
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + gamePhase);
		}
		
		move(dt, vd);
	}

	@Override
	public void display(PApplet p, SubPlot plt) {
		p.pushMatrix();
		float[] pp = plt.getPixelCoord(pos.x, pos.y);
		float[] rr = plt.getDimInPixel(radius, radius);
		p.translate(pp[0], pp[1]);
		p.rotate(-vel.heading());
		p.stroke(color);
		p.strokeWeight(4);
		p.noFill();
		p.arc(-rr[0] / 1.7f, rr[0] - rr[0] * 1.5f, rr[0] * 1.3f, rr[0] / 1.2f, (float) (0 + 0.5f),
				(float) (Math.PI + 0.5f));
		p.arc(-rr[0] / 3.5f, rr[0] / 1.5f, rr[0] / 2.3f, rr[0] / 2f, (float) (0), (float) (Math.PI + Math.PI / 2.3f));
		p.arc(-rr[0] / 3.5f, -rr[0] / 1.5f, rr[0] / 2.3f, rr[0] / 2f, (float) (Math.PI / 2.3f), (float) (2 * Math.PI));

		p.rotate((float) Math.toRadians(20));
		p.arc(rr[0] / 3.5f + rr[1], rr[0] - rr[0] * 1.395f, rr[0] * 1.2f, rr[0] / 1.2f, (float) (0),
				(float) (Math.PI + Math.PI / 4));
		p.rotate((float) Math.toRadians(-20));

		p.rotate((float) Math.toRadians(180));
		p.arc(rr[0] / 1.7f, rr[0] - rr[0] * 1.5f, rr[0] * 1.3f, rr[0] / 1.2f, (float) (-Math.PI / 7),
				(float) (Math.PI));
		p.rotate((float) Math.toRadians(-20));
		p.arc(-rr[0] / 3.5f - rr[1], rr[0] - rr[0] * 1.395f, rr[0] * 1.2f, rr[0] / 1.2f, (float) (-Math.PI / 4), (float) (Math.PI ));
		p.rotate((float) Math.toRadians(20));
		p.rotate((float) Math.toRadians(-180));	
		p.stroke(p.color(stroke));
		p.strokeWeight(2);
		p.fill(color);
		p.ellipse(0, 0, rr[0], rr[0] * 1.05f);
		p.stroke(p.color(0));
		p.strokeWeight(1);
		p.fill(p.color(0));
		p.ellipse(-rr[0] / 7f, rr[0] / 5f, rr[0] / 8, rr[0] / 10);
		p.ellipse(-rr[0] / 7f, -rr[0] / 5f, rr[0] / 8, rr[0] / 10);
		p.popMatrix();
	}
}
