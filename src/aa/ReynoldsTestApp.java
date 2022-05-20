package aa;

import java.util.ArrayList;
import java.util.List;

import physics.Body;
import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import tools.SubPlot;

public class ReynoldsTestApp implements IProcessingApp {
	private Boid wander, seeker, pursuiter, boid; 
	private Flock flock;
	private float[] sacWeights = {1f, 1f, 1f};
	private double[] window  = {-10, 10, -10, 10};
	//Viewports
	private float[] view1 = {0.02f, 0.51f, 0.96f, 0.47f};
	private float[] view2 = {0.02f, 0.02f, 0.47f, 0.47f};
	private float[] view3 = {0.51f, 0.02f, 0.47f, 0.47f};
	private SubPlot plt1, plt2, plt3;
	private Body target;
	private int ix = 0;

	@Override
	public void setup(PApplet p) {
		plt1 = new SubPlot(window, view1, p.width, p.height);
		plt2 = new SubPlot(window, view2, p.width, p.height);
		plt3 = new SubPlot(window, view3, p.width, p.height);

		flock  = new Flock(200, .1f, .3f, p.color(0, 100, 200), sacWeights, p, plt1);
		boid   = flock.getBoid(4);
		
		wander = new Boid(new PVector(p.random((float)window[0], (float)window[1]), p.random((float)window[2], (float)window[3])), .5f, .5f, p.color(255, 0, 0), p, plt2);
		wander.addBehaviour(new Wander(.4f));
		
		pursuiter = new Boid(new PVector(p.random((float)window[0], (float)window[1]), p.random((float)window[2], (float)window[3])), .5f, .5f, p.color(0, 255, 0), p, plt2);
		pursuiter.addBehaviour(new Pursuit(1f));
		List<Body> allTrackingBodies = new ArrayList<Body>();
		allTrackingBodies.add(wander);
		pursuiter.setEye(new Eye(pursuiter, allTrackingBodies));
		
		target = new Body(new PVector(), new PVector(), 1f, .3f, p.color(0));
		seeker = new Boid(new PVector(p.random((float)window[0], (float)window[1]), p.random((float)window[2], (float)window[3])), .5f, .5f, p.color(0, 0, 255), p, plt3);
		seeker.addBehaviour(new Seek(1f));
		//seeker.addBehaviour(new Wander(.5f));
		seeker.addBehaviour(new Flee(1f));
		allTrackingBodies = new ArrayList<Body>();
		allTrackingBodies.add(target);
		seeker.setEye(new Eye(seeker, allTrackingBodies));
	}

	@Override
	public void draw(PApplet p, float dt) {
		p.background(255);
		float[] bb = plt1.getBoundingBox();
		p.fill(255, 64);
		p.rect(bb[0], bb[1], bb[2], bb[3]);
		
		bb = plt2.getBoundingBox();
		p.fill(190, 170, 45, 64);
		p.rect(bb[0], bb[1], bb[2], bb[3]);
		
		bb = plt3.getBoundingBox();
		p.fill(120, 170, 150, 64);
		p.rect(bb[0], bb[1], bb[2], bb[3]);
		
		wander.applyBehaviors(dt);
		pursuiter.applyBehaviors(dt);
		seeker.applyBehavior(ix, dt);
		seeker.getEye().display(p, plt3);
		flock.applyBehavior(dt);
		
		flock.display(p, plt1);
		//boid.getEye().display(p, plt1);
		wander.display(p, plt2);
		pursuiter.display(p, plt2);
		target.display(p, plt3);
		seeker.display(p, plt3);
	}

	@Override
	public void mousePressed(PApplet p) {
		if (plt3.isInside(p.mouseX, p.mouseY)) {
			double[] w = plt3.getWorldCoord(p.mouseX, p.mouseY);
			target.setPos(new PVector((float)w[0], (float)w[1]));	
		}
	}

	@Override
	public void keyPressed(PApplet p) {
		if (p.key == 't') {
			ix = (ix + 1) % 2;
		}
	}

}
