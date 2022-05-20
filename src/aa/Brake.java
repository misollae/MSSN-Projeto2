package aa;

import processing.core.PVector;

public class Brake extends Behavior {

	public Brake(float weight) {
		super(weight);
	}

	@Override
	public PVector getDesiredVelocity(Boid me) {
		if (PVector.sub(me.eye.getTarget().getPos(),me.getPos()).x == 0 && PVector.sub(me.eye.getTarget().getPos(),me.getPos()).y == 0) return new PVector();
		return me.getVel();
	}

}
