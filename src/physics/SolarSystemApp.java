package physics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import g4p_controls.GButton;
import g4p_controls.GEvent;
import g4p_controls.GLabel;
import g4p_controls.GTextField;
import processing.core.*;
import processing.sound.SoundFile;
import setup.IProcessingApp;
import setup.PSControl;
import tools.SubPlot;

public class SolarSystemApp implements IProcessingApp {
	private boolean pause;
	private boolean help;

	private float sunMass = 1.989e30f;
	private float earthMass = 5.97e24f;
	private float distEarthSun = 1.496e11f;
	private float earthSpeed = 3e4f;
	private float earthRadius = 6371e6f;

	private List<Body> planets = new ArrayList<Body>();
	private List<Float> dists = new ArrayList<Float>();
	private List<Float> masses = new ArrayList<Float>();
	private List<Float> radiuses = new ArrayList<Float>();
	private List<String> names = new ArrayList<String>();
	private List<Float> speeds = new ArrayList<Float>();
	private List<Integer> colors = new ArrayList<Integer>();

	private double windowScale;
	private double xOffSet;
	private double yOffSet;

	private int selected;

	private float[] viewport;
	private double[] window;

	private SubPlot plt;
	private Body sun;

	private PSControl psc, psc2;
	private ParticleSystem ps, ps2, ps3;
	private float[] velParams = { PApplet.radians(360), PApplet.radians(360), 3e10f, 3e10f };
	private float[] lifetimeParams = { 2, 5 };
	private float[] radiusParams = { 1e9f, 3e9f };
	private float flow = 400;
	private float[] lifetimeParams2 = { 10, 20 };
	private float[] velParams2 = { PApplet.radians(360), PApplet.radians(360), 5e10f, 5e10f };
	private float[] radiusParams2 = { 3e9f, 6e9f };
	private float flow2 = 600;

	private PImage bg;
	private float speedUp = 60 * 60 * 24 * 30;
	private GLabel label1, label2, label3, label4;

	private boolean displayBeltName, displayBeltName2;
	private GLabel label5;
	private GButton updateBttn;
	private GTextField gField;
	private SoundFile file, file2;

	@Override
	public void setup(PApplet p) {
		System.out.println("Loading Sound... Please wait!");
		bg = p.loadImage("./images/map2.png");
		
		try {
			file = new SoundFile(p, "./sound/cosmos.mp3");
			file.loop();	
		} catch (Exception e) {
			System.out.println("Loading sound...");
		}

		this.pause = false;
		this.help = true;
		this.displayBeltName = false;
		this.displayBeltName2 = false;
		this.windowScale = 2;
		this.xOffSet = 0;
		this.yOffSet = 0;

		this.viewport = new float[] { 0.2f, 0.2f, 0.6f, 0.6f };
		this.window = new double[] { -windowScale * 2.279e11f + xOffSet, windowScale * 2.279e11f + xOffSet,
				-windowScale * 2.279e11f + yOffSet, windowScale * 2.279e11f + yOffSet };

		plt = new SubPlot(window, viewport, p.width, p.height);
		sun = new Body(new PVector(), new PVector(), sunMass, 2 * 6.963e9f, p.color(239, 142, 56));

		float escala = 1f;
		float diminuidor = (float) Math.sqrt(escala);

		this.psc = new PSControl(velParams, lifetimeParams, radiusParams, flow, p.color(200, 203, 221));
		this.psc2 = new PSControl(velParams2, lifetimeParams2, radiusParams2, flow2, p.color(200, 203, 221));

		this.ps = new ParticleSystem(new PVector(0, 4.78e11f), new PVector(2.5e5f, 0), 0.28e24f, 24622e7f, psc);
		this.ps2 = new ParticleSystem(new PVector(0, -4.78e11f), new PVector(2.5e5f, 0), 0.28e24f, 24622e7f, psc);
		this.ps3 = new ParticleSystem(new PVector(0, -4.78e11f), new PVector(2.5e5f, 0), 0.28e24f, 54622e8f, psc2);

		dists.addAll(Arrays.asList(0.5791e11f, 1.082e11f, distEarthSun, 2.279e11f, 7.785e11f / escala,
				14.34e11f / escala, 28.71e11f / escala, 44.95e11f / escala));
		radiuses.addAll(Arrays.asList(2439.7e6f, 6051.8e6f, earthRadius, 3389.5e6f, (69911e6f * diminuidor) / escala,
				(58232e6f * diminuidor) / escala, (25362e6f * diminuidor) / escala, (24622e6f * diminuidor) / escala));
		speeds.addAll(Arrays.asList(4.8e4f, 3.5e4f, earthSpeed, 2.4e4f, 1.31e4f * diminuidor, 0.97e4f * diminuidor,
				0.68e4f * diminuidor, 0.54e4f * diminuidor));
		masses.addAll(Arrays.asList(0.33e24f, 4.87e24f, earthMass, 0.64e24f, 1898.6e24f * diminuidor,
				568.46e24f * diminuidor, 86.62e24f * diminuidor, 102.43e24f * diminuidor));
		names.addAll(Arrays.asList("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"));
		colors.addAll(Arrays.asList(p.color(219, 206, 202), p.color(238, 208, 180), p.color(11, 158, 210),
				p.color(161, 37, 27), p.color(209, 167, 127), p.color(205, 160, 86), p.color(79, 208, 231),
				p.color(115, 172, 172)));
		// this.moon = new Body(new PVector(0, distEarthSun+384400e3f+earthRadius), new
		// PVector(earthSpeed, 0), earthMass/81, 1737.4e6f, p.color(219, 206, 202));

		for (int i = 0; i < names.size(); i++) {
			planets.add(new Body(new PVector(0, dists.get(i)), new PVector(speeds.get(i), 0), masses.get(i),
					radiuses.get(i), colors.get(i)));
		}
		selected = -1;
		setUpG4P(p);
	}

	public void setUpG4P(PApplet p) {

		label5 = new GLabel(p, 10, 9, 1500, 20, "G Value: ");
		label5.setLocalColorScheme(p.color(128, 128, 121));
		updateBttn = new GButton(p, 130, 10, 40, 20, "Set!");
		updateBttn.setLocalColorScheme(p.color(128, 128, 121));
		updateBttn.addEventHandler(this, "handleUpdate");
		gField = new GTextField(p, 65, 10, 60, 20);
		gField.setNumeric(0f, 999999999f, (float) 6.67e-11);
		gField.setText(String.valueOf(sun.getG()));

		label4 = new GLabel(p, 10, p.height - 70, 1500, 20, "H -> Hide/Show help");
		label4.setLocalColorScheme(p.color(125, 127, 201));
		label2 = new GLabel(p, 10, p.height - 56, 1500, 20, "R -> Reset view");
		label2.setLocalColorScheme(p.color(125, 127, 201));
		label1 = new GLabel(p, 10, p.height - 42, 1500, 20, "Space -> Pause");
		label1.setLocalColorScheme(p.color(125, 127, 201));
		label3 = new GLabel(p, 10, p.height - 27, 1500, 20,
				"Use the arrow keys to move the window around, hit 'M' or 'N' to zoom in and out, respectively. Click any planet to display information!");
		label3.setLocalColorScheme(p.color(125, 127, 201));
	}

	public void handleUpdate(GButton button, GEvent event) {
		if (button == updateBttn && event == GEvent.CLICKED) {
			Mover.setG(gField.getValueF());
		}
	}

	public void setG(double G) {
		Mover.setG(G);
	}

	@Override
	public void draw(PApplet p, float dt) {
		
		p.background(bg);
		sun.display(p, plt);
		if (!pause) {
			ps.setPos(planets.get(1).getPos().copy().mult(4f));
			ps.moveSolar(dt, dt);
			ps2.setPos(planets.get(1).getPos().copy().mult(-4f));
			ps2.moveSolar(dt, dt);
			ps3.setPos(planets.get(1).getPos().copy().mult(50f));
			ps3.moveSolar(dt, dt);
		}
		ps.display(p, plt);
		ps2.display(p, plt);
		ps3.display(p, plt);

		if (selected == -2) {
			sun.displayName(p, plt, "Sun", p.color(255, 255, 255));
		}

		if (displayBeltName) {
			p.pushStyle();
			float[] pp = plt.getPixelCoord(0, 4.78e11f);
			float[] r = plt.getDimInPixel(ps.radius, ps.radius);
			p.fill(p.color(255, 255, 255));
			p.textSize(12);
			p.text("The Main Asteroid Belt", (float) (pp[0] - 55), pp[1] + r[1] / 3.5f);
			p.popStyle();
		}

		if (displayBeltName2) {
			p.pushStyle();
			float[] pp = plt.getPixelCoord(0, 4.78e11f * 12.5f);
			p.fill(p.color(255, 255, 255));
			p.textSize(12);
			p.text("Kuiper Belt", (float) (pp[0] - 25), pp[1] + (pp[1]) / 10f);
			p.popStyle();
		}

		for (Body planet : planets) {
			planet.displayTrajectory(dists.get(planets.indexOf(planet)), sun, p, plt, p.color(90, 100, 124));
			if (!pause) {
				PVector f = sun.attraction(planet);
				planet.applyForce(f);
				for (Body planet2 : planets) {
					if (planet != planet2) {
						PVector f2 = planet.attraction(planet2);
						planet.applyForce(f2);
					}
				}
				planet.move(dt * speedUp);
			}
			if (planets.indexOf(planet) == selected)
				planet.displayName(p, plt, names.get(planets.indexOf(planet)), p.color(255, 255, 255));
			planet.display(p, plt);
		}

	}// 171, 143

	@Override
	public void mousePressed(PApplet p) {
		double[] click = plt.getWorldCoord(p.mouseX, p.mouseY);
		boolean underBelt = (Math.pow(click[0] + xOffSet, 2) / Math.pow(4.8e11f + xOffSet, 2))
				+ (Math.pow(click[1] + yOffSet, 2) / Math.pow(4.9e11f + yOffSet, 2)) <= 1;
		boolean aboveBelt = (Math.pow(click[0] + xOffSet, 2) / Math.pow(3.6e11f + xOffSet, 2))
				+ (Math.pow(click[1] + yOffSet, 2) / Math.pow(3.6e11f + yOffSet, 2)) >= 1;
		if (aboveBelt && underBelt) {
			displayBeltName = true;
		} else {
			displayBeltName = false;
		}
		;

		boolean underBelt2 = (Math.pow(click[0] + xOffSet, 2) / Math.pow((4.8e11f * 12.5f) + xOffSet, 2))
				+ (Math.pow(click[1] + yOffSet, 2) / Math.pow((4.9e11f * 12.5f) + yOffSet, 2)) <= 1;
		boolean aboveBelt2 = (Math.pow(click[0] + xOffSet, 2) / Math.pow((3.6e11f * 12.5f) + xOffSet, 2))
				+ (Math.pow(click[1] + yOffSet, 2) / Math.pow((3.6e11f * 12.5f) + yOffSet, 2)) >= 1;
		if (aboveBelt && underBelt) {
			displayBeltName = true;
		} else {
			displayBeltName = false;
		}
		;
		if (aboveBelt2 && underBelt2) {
			displayBeltName2 = true;
		} else {
			displayBeltName2 = false;
		}
		;

		selected = -1;
		for (Body planet : planets) {
			if (planet.isInside(p.mouseX, p.mouseY, plt)) {
				selected = planets.indexOf(planet);
			}
		}
		if (sun.isInside(p.mouseX, p.mouseY, plt)) {
			selected = -2;
		}
	}

	@Override
	public void keyPressed(PApplet p) {
		if (p.key == 'D' || p.key == 'd') {
			planets.remove(selected);
			dists.remove(selected);
		}

		if (p.key == 'N' || p.key == 'n') {
			if (windowScale < 1)
				this.windowScale += 0.1;
			else
				this.windowScale += 0.2;
			this.window = new double[] { -windowScale * 2.279e11f + xOffSet, windowScale * 2.279e11f + xOffSet,
					-windowScale * 2.279e11f + yOffSet, windowScale * 2.279e11f + yOffSet };
			this.plt = new SubPlot(window, viewport, p.width, p.height);
		}
		if (p.key == 'M' || p.key == 'm') {
			if (windowScale > 1)
				this.windowScale -= 0.2;
			else if (windowScale > 0.2)
				this.windowScale -= 0.1;
			this.window = new double[] { -windowScale * 2.279e11f + xOffSet, windowScale * 2.279e11f + xOffSet,
					-windowScale * 2.279e11f + yOffSet, windowScale * 2.279e11f + yOffSet };
			this.plt = new SubPlot(window, viewport, p.width, p.height);
		}
		if (p.key == 'R' || p.key == 'r') {
			this.windowScale = 2;
			this.xOffSet = 0;
			this.yOffSet = 0;
			this.window = new double[] { -windowScale * 2.279e11f + xOffSet, windowScale * 2.279e11f + xOffSet,
					-windowScale * 2.279e11f + yOffSet, windowScale * 2.279e11f + yOffSet };
			this.plt = new SubPlot(window, viewport, p.width, p.height);
		}

		if (p.key == PConstants.CODED && p.keyCode == PConstants.UP) {
			this.yOffSet += 1e10f * windowScale / 2;
			this.window = new double[] { -windowScale * 2.279e11f + xOffSet, windowScale * 2.279e11f + xOffSet,
					-windowScale * 2.279e11f + yOffSet, windowScale * 2.279e11f + yOffSet };
			this.plt = new SubPlot(window, viewport, p.width, p.height);
		}
		if (p.key == PConstants.CODED && p.keyCode == PConstants.DOWN) {
			this.yOffSet -= 1e10f * windowScale / 2;
			this.window = new double[] { -windowScale * 2.279e11f + xOffSet, windowScale * 2.279e11f + xOffSet,
					-windowScale * 2.279e11f + yOffSet, windowScale * 2.279e11f + yOffSet };
			this.plt = new SubPlot(window, viewport, p.width, p.height);
		}
		if (p.key == PConstants.CODED && p.keyCode == PConstants.LEFT) {
			this.xOffSet -= 1e10f * windowScale / 2;
			this.window = new double[] { -windowScale * 2.279e11f + xOffSet, windowScale * 2.279e11f + xOffSet,
					-windowScale * 2.279e11f + yOffSet, windowScale * 2.279e11f + yOffSet };
			this.plt = new SubPlot(window, viewport, p.width, p.height);
		}
		if (p.key == PConstants.CODED && p.keyCode == PConstants.RIGHT) {
			this.xOffSet += 1e10f * windowScale / 2;
			this.window = new double[] { -windowScale * 2.279e11f + xOffSet, windowScale * 2.279e11f + xOffSet,
					-windowScale * 2.279e11f + yOffSet, windowScale * 2.279e11f + yOffSet };
			this.plt = new SubPlot(window, viewport, p.width, p.height);
		}

		if (p.key == ' ') {
			this.pause = !pause;
		}

		if (p.key == 'H' || p.key == 'h') {
			this.help = !help;
			label1.setVisible(this.help);
			label2.setVisible(this.help);
			label3.setVisible(this.help);
			label4.setVisible(this.help);
		}
	}

}
