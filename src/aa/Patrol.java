package aa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import physics.Body;
import processing.core.PVector;

public class Patrol extends Behavior{
	private Behavior movementType; 
	private List<Body> path;
	private int indice;

	public Patrol(float weight, Behavior movementType) {
		super(weight);
		this.indice = 0;
		this.path = new ArrayList<Body>();
		this.movementType = movementType;
	}

	@Override
	public PVector getDesiredVelocity(Boid me) {
		if (path.isEmpty()) return new PVector();
		if ( Math.abs(me.getPos().x - me.getEye().getTarget().getPos().x) <= 3/me.dna.maxSpeed && 
			Math.abs(me.getPos().y - me.getEye().getTarget().getPos().y) <= 3/me.dna.maxSpeed) {
			if (indice == path.size()-1) {shuffle(); indice = 0;}
			else indice++;
		}
		me.getEye().setTarget(path.get(indice));
		return movementType.getDesiredVelocity(me);
	}
	
	public void shuffle() {
		Collections.shuffle(path);
	}
	
	public void addToPath(Body point) {
		path.add(point);
	}
	
	public void remFromPath(Body point) {
		path.remove(point);
		if (indice >= path.size()) {shuffle(); indice = 0;}
	}
}
