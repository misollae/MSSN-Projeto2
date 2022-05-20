package aa;

import processing.core.PApplet;
import processing.core.PVector;

public class Arrive extends Behavior{

	public Arrive(float weight) {
		super(weight);
	}

	@Override
	public PVector getDesiredVelocity(Boid me) {
		 float R = me.dna.radiusArrive;
		 PVector desired = PVector.sub(me.eye.getTarget().getPos(),me.getPos());
	 	 float d = desired.mag();
	 	 desired.normalize();
	 	 if (d < R) {
	 		float m = PApplet.map(d,0,R,0,me.dna.maxSpeed);
	      	desired.mult(m);
	     } else {
	    	desired.mult(me.dna.maxSpeed);
	     }
	    PVector v = PVector.sub(desired,me.getVel());
	    v.limit(me.dna.maxForce);
	    return v;
	}

}
