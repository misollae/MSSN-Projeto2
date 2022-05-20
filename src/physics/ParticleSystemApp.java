package physics;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import setup.PSControl;
import tools.SubPlot;

public class ParticleSystemApp implements IProcessingApp{

	private List<ParticleSystem> pss;
	private double[] window    = { -2.279e11f, 2.279e11f ,-2.279e11f, 2.279e11f };
	private float[] viewport   = { 0.2f, 0.2f, 0.6f, 0.6f };
	private SubPlot plt;
	private float[] velParams  = {PApplet.radians(360), PApplet.radians(360), 1e10f/1.5f, 1e10f/1.5f};
	private float[] lifetimeParams = {2, 5};
	private float[] radiusParams   = {1e9f, 3e9f};
	private float flow = 400;
	
	@Override
	public void setup(PApplet p) {
		plt = new SubPlot(window, viewport, p.width, p.height);	
		pss = new ArrayList<ParticleSystem>();
	}

	@Override
	public void draw(PApplet p, float dt) {
		p.background(255);
		for (ParticleSystem ps : pss) {
			ps.applyForce(new PVector(0, 0));
		}
		
		for (ParticleSystem ps : pss) {
			ps.move(dt);
			ps.display(p, plt);
		}
		
		velParams[0] = PApplet.map(p.mouseX, 0, p.width, PApplet.radians(0), PApplet.radians(360));
		for (ParticleSystem ps : pss) {
			ps.getPSControl().setVelParams(velParams);
		}
	}

	@Override
	public void mousePressed(PApplet p) {
		double[] ww = plt.getWorldCoord(p.mouseX, p.mouseY);
		System.out.println(p.mouseX);
		int color   = p.color(p.random(255), p.random(255), p.random(255));
		
		PSControl psc = new PSControl(velParams, lifetimeParams, radiusParams, flow, color);
		ParticleSystem ps  = new ParticleSystem(new PVector((float)ww[0],(float)ww[1]), new PVector(), 2.8e24f, 24622e6f, psc);
		pss.add(ps);
	}

	@Override
	public void keyPressed(PApplet p) {
	}
}
