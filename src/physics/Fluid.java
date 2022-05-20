package physics;

import processing.core.PApplet;
import processing.core.PVector;

public abstract class Fluid {

	protected float density;
	
	public Fluid(float density) {
		this.density = density;
	}
	
	public PVector drag(Body b){
		float area = PApplet.pow(b.radius, 2.0f) * PApplet.PI;
		float mag = b.vel.mag();
		return PVector.mult(b.vel, -0.5f*density*area*mag);
	}
}
