package aa;

import java.util.ArrayList;
import java.util.List;

import physics.Body;
import processing.core.PApplet;
import processing.core.PVector;
import tools.SubPlot;

public class OctopiGroup {
	private List<Boid> boids;

	public OctopiGroup(int nboids, float mass, float radius, int[] colors, float[] sacWeights, PApplet p, SubPlot plt) {
		boids = new ArrayList<Boid>();
		double[] w = plt.getWindow();
		float x = p.random((float) w[0], (float) w[1]);
		float y = p.random((float) w[2], (float) w[3]);

		for (int i = 0; i < nboids; i++) {
			Octopus b = new Octopus(new PVector(x + (i*1.2f), y + radius*2*i), mass, radius, colors[i], p, plt);
			b.addBehaviour(new Separate(sacWeights[0]));
			b.addBehaviour(new Align(sacWeights[1]));
			b.addBehaviour(new Cohesion(sacWeights[2]));
			boids.add(b);
		}

		List<Body> bodies = boidList2BodyList(boids);
		for (Boid b : boids) {
			b.dna.visionAngle = b.dna.visionAngle;
			b.dna.visionDistance = b.dna.visionDistance*1.4f;

			b.setEye(new Eye(b, bodies));
		}
	}

	public List<Boid> getBodies(){
		return boids;
	}
	
	private List<Body> boidList2BodyList(List<Boid> boids) {
		List<Body> bodies = new ArrayList<Body>();
		for (Boid b : boids)
			bodies.add(b);
		return bodies;
	}

	public void applyBehavior(float dt, int gamePhase) {
		for (Boid b : boids) {			
			((Octopus)b).applyBehaviors(dt, gamePhase);
		}
	}

	public Boid getBoid(int i) {
		return boids.get(i);
	}

	public void display(PApplet p, SubPlot plt) {
		for (Boid b : boids) {
			b.display(p, plt);
		}
	}

}
