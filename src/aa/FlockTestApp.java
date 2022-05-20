package aa;

import processing.core.PApplet;
import setup.IProcessingApp;
import tools.SubPlot;

public class FlockTestApp implements IProcessingApp {
	
	private Flock flock;
	private float[] sacWeights = {1f, 1f, 1f};
	private double[] window  = {-10, 10, -10, 10};
	private float[] viewport = {0, 0, 1, 1};
	private SubPlot plt;
	

	@Override
	public void setup(PApplet p) {
		plt = new SubPlot(window, viewport, p.width, p.height);
		flock = new Flock(200, .1f, .3f, p.color(0, 100, 200), sacWeights, p, plt);
	}

	@Override
	public void draw(PApplet p, float dt) {
		//p.background(255);
		float[] bb = plt.getBoundingBox();
		p.fill(255, 64);
		p.rect(bb[0], bb[1], bb[2], bb[3]);
		flock.applyBehavior(dt);
		flock.display(p, plt);
	}

	@Override
	public void mousePressed(PApplet p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(PApplet p) {
		// TODO Auto-generated method stub
		
	}

}
