package physics;

import java.util.ArrayList;
import processing.sound.*;
import java.util.Arrays;
import java.util.List;

import aa.Boid;
import aa.Eye;
import aa.OctopiGroup;
import aa.Pursuit;
import aa.Seek;
import aa.Spaceship;
import g4p_controls.GAbstractControl;
import g4p_controls.GButton;
import g4p_controls.GLabel;
import g4p_controls.GTextField;
import processing.core.*;
import setup.IProcessingApp;
import setup.PSControl;
import tools.SubPlot;

public class AstroCharterApp implements IProcessingApp {
	private boolean addingPlanets;
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
	
	private List<ParticleSystem> powerUps = new ArrayList<ParticleSystem>();

	private Body target;

	private int selected;

	private float[] viewport;
	private double[] window;

	private SubPlot plt;
	private Body sun;
	
	private final int asteroidLimit = 1;
	
	private int[] asteroidColors;
	private int[] planetsColors;
	
	private int collisionsWithPlanet = 0;
	private float asteroidPowder   = 0;
	
	private float[] velParams  = {PApplet.radians(360), PApplet.radians(360), 3e10f, 3e10f};
	private float[] lifetimeParams = {1, 3};
	private float[] radiusParams   = {1e9f, 3e9f};
	private float flow = 300;

	private PImage bg;
	private float speedUp = 60 * 60 * 24 * 30;
	
	private List<ParticleSystem> pss;
	private List<Integer> timesDisplayed = new ArrayList<Integer>();
	private Boid spaceship;
	private List<Body> allTrackingBodies = new ArrayList<Body>();
	private List<ParticleSystem> asteroids = new ArrayList<ParticleSystem>();
	private List<Integer> lives = new ArrayList<Integer>();
	private List<Integer> collisonTimeout = new ArrayList<Integer>();


	private List<Body> asteroidsAux = new ArrayList<Body>();

	private boolean forceFieldUp;
	private float forceFieldPower;
	private int forceFieldTimer;
	private boolean startGame;
	private boolean displaySSI;
	private GTextField nameField;
	private GTextField speedField;
	private GTextField massField;
	private GTextField radiusField;
	private SoundFile bgs;
	private SoundFile shieldsound;
	private SoundFile woosh;
	private SoundFile burn;

	@Override
	public void setup(PApplet p) {
		System.out.println("Loading Sound... Please wait!");
		bg = p.loadImage("./images/map.png");
		
		try {
			bgs = new SoundFile(p, "./sound/spacebg.mp3");
			shieldsound = new SoundFile(p, "./sound/shieldsound.mp3");
			woosh = new SoundFile(p, "./sound/dust.mp3");
			burn = new SoundFile(p, "./sound/burn.mp3");
			bgs.loop();
		} catch (Exception e) {
			System.out.println("Loading sound...");
		}

		this.startGame = false;
		this.forceFieldTimer = 400;
		this.forceFieldPower = 0.1f;
		this.forceFieldUp = false;
		this.pause = false;
		this.help  = true;
		
		this.displaySSI = false;
		
		this.asteroidColors = new int[]{p.color(90, 125, 159), p.color(58, 88, 116), p.color(160, 165, 188)};
		this.planetsColors = new int[]{p.color(143, 109, 81), p.color(201, 139, 90), p.color(127, 134, 118), p.color(138, 47, 44)};

		
		this.pss = new ArrayList<ParticleSystem>();

		this.viewport = new float[] { 0f, 0f, 1f, 1f };
		this.window = new double[] {-2.279e11f*1.5f, 2.279e11f*1.5f, -2.279e11f*1.5f, 2.279e11f*1.5f};

		plt = new SubPlot(window, viewport, p.width, p.height);
		sun = new Body(new PVector(), new PVector(), sunMass, 2*6.963e9f, p.color(235, 172, 75));
		
		target = new Body(new PVector(), new PVector(), 1f, 0.2f, p.color(255, 0, 0));
		spaceship = new Spaceship(new PVector(0,0), 1e3f, 6051.8e6f*1.5f, p.color(0), p, plt);
		spaceship.addBehaviour(new Pursuit(1f));
		allTrackingBodies = new ArrayList<>();
		allTrackingBodies.add(target);
		Eye eye = new Eye(spaceship, allTrackingBodies);
		spaceship.setEye(eye);
		spaceship.getEye().setTarget(target);
		spaceship.dna.maxSpeed = 2e4f;
		spaceship.dna.maxForce = 1e1f;

		collisonTimeout.addAll(Arrays.asList(0, 0, 0, 0));
		lives.addAll(Arrays.asList(3, 3, 3, 3));
		dists.addAll(Arrays.asList(0.5791e11f, 1.082e11f, distEarthSun, 2.279e11f));
		radiuses.addAll(Arrays.asList(2439.7e6f, 6051.8e6f, earthRadius, 3389.5e6f));
		speeds.addAll(Arrays.asList(4.8e4f, 3.5e4f, earthSpeed, 2.4e4f));
		masses.addAll(Arrays.asList(0.33e24f, 4.87e24f, earthMass, 0.64e24f));
		names.addAll(Arrays.asList("Hermes", "Aphrodite", "Gaia", "Ares"));
		colors.addAll(Arrays.asList(p.color(143, 109, 81), p.color(201, 139, 90), p.color(127, 134, 118), p.color(138, 47, 44)));

		for (int i = 0; i < names.size(); i++) {
			planets.add(new Body(new PVector(0, dists.get(i)), new PVector(speeds.get(i), 0), masses.get(i),
					radiuses.get(i)*1.6f, colors.get(i)));
		}
		selected = -1;
		addingPlanets = false;
		
		setUpG4P(p);
	}
	
	public void setUpG4P(PApplet p) {
		nameField = new GTextField(p, p.width - 100, 25, 70, 19);
		nameField.setText("Hator");
		speedField = new GTextField(p, p.width - 100, 48, 50, 19);
		speedField.setNumeric(0, 9.9f, 3f);
		speedField.setText("3");
		massField = new GTextField(p, p.width - 100, 71, 50, 19);
		massField.setNumeric(0f, 9.9f, 5.97f);
		massField.setText("5.97");
		radiusField = new GTextField(p, p.width - 100, 96, 50, 19);
		radiusField.setNumeric(0f, 9.9f, 6.37f);
		radiusField.setText("6.37");
	}

	@Override
	public void draw(PApplet p, float dt) {
		p.background(bg);
		sun.display(p, plt);
		
		if (help) {				
			
		p.strokeWeight(3);
		p.fill(p.color(58, 88, 116));
		p.textSize(13.5f);
		p.text("Name: ", p.width - 140, 39);
		p.text("Speed:", p.width - 140, 63);
		p.text("e4f", p.width - 45, 63);
		p.text("Mass:", p.width - 140, 87);
		p.text("e24f", p.width - 45, 87);
		p.text("Radius:", p.width - 140, 111);
		p.text("e9f", p.width - 45, 111);		
		
		p.text("With your force field up, try to collect asteroid dust, and protect the planets you're free to create!", 20,  p.height - 94);
		p.text("H -> Hide/Show help", 20, p.height -  79);
		p.text("F -> Force Field up", 20, p.height - 64);
		p.text("D -> Delete Selected planet", 20, p.height - 49);
		p.text("Mouse Click/I -> Display planet/spaceship info", 20, p.height -34);
		p.text("A + Click -> Add planet on clicked location", 20, p.height - 19);
		}
		
		int random = (int) p.random(0, 160);
		if (asteroids.size() <= asteroidLimit && (random == 20)) {

			PSControl psc = new PSControl(new float[]{PApplet.radians(360), PApplet.radians(360), 1e10f, 1e10f}, new float[]{.5f, 1f}, new float[]{(1e9f), (3e9f)}, 800, asteroidColors[(int) p.random(0, 3)]);
			ParticleSystem ps  = new ParticleSystem(new PVector(-4e10f, -4e10f), new PVector(2.5e11f/2, 0), 0.28e24f, 24622e8f, psc);			
			
			float randomXpos = 0; 
			float randomYpos = 0;
			
			randomXpos = 0; randomYpos = p.random(0, p.height/2); 

			double[] coords = plt.getWorldCoord(randomXpos, randomYpos);

			Body psaux  = new Body(new PVector((float)coords[0], (float)coords[1]), new PVector(1e4f, 0), 0.28e10f, earthRadius, p.color(95, 78, 67));

			asteroids.add(ps);
			asteroidsAux.add(psaux);
		}
		
		int random2 = (int) p.random(0, 180);
		if (!pause && powerUps.size() <= 0 && (random2 == 20)) {
			PSControl psc = new PSControl(new float[]{PApplet.radians(360), PApplet.radians(360), 2e10f, 2e10f}, new float[]{.5f, 1f}, new float[]{(1e9f), (3e9f)}, 800, p.color(149, 189, 152, 150));

			float randomXpos = p.random(0, p.width); float randomYpos = p.random(0, 2*p.height/3);

			double[] coords = plt.getWorldCoord(randomXpos, randomYpos);
			ParticleSystem ps  = new ParticleSystem(new PVector((float)coords[0], (float)coords[1]), new PVector(0, 0), 0.28e24f, 24622e8f, psc);			
			powerUps.add(ps);
		}
		
		double[] ppM = plt.getWorldCoord(p.mouseX, p.pmouseY);
		float[] ppS = plt.getPixelCoord(spaceship.getPos().x, spaceship.getPos().y);
		float[] rr = plt.getPixelCoord(spaceship.radius, spaceship.radius);

		target.setPos(new PVector((float) ppM[0], (float) ppM[1]));
		spaceship.applyBehaviors(dt * speedUp);
		if (forceFieldUp && forceFieldTimer >= 0) {
			forceFieldTimer -= 1;
			p.fill(p.color(149, 189, 152, 150));
			p.stroke(p.color(149, 189, 152));
			p.strokeWeight(1);
			p.circle((float) ppS[0], (float) ppS[1], (float) (rr[0]*forceFieldPower));
		} else if(forceFieldTimer < 0) forceFieldUp = false;
		
		if (selected == -2) {
			sun.displayName(p, plt, "Sun", p.color(113, 87, 79));
		}
		
		if (!pause && !powerUps.isEmpty()) {
			for (int i = powerUps.size()-1 ; i>=0 ; i--) {
				ParticleSystem ps = powerUps.get(i);
				
				if ((PVector.dist(spaceship.getPos(), ps.getPos()) <= (spaceship.radius)*1.7f)) {
					ps.reduceFlow(50);
					this.forceFieldPower += 0.001f;
				} else {
					ps.reduceFlow(1);
				}
							
				ps.move(dt);
				ps.display(p, plt);
				
				if (ps.isEmpty()) powerUps.remove(ps);
			}
		}
		
		if (!pause && !asteroids.isEmpty()) {
			for (int i = asteroids.size()-1 ; i>=0 ; i--) {
				ParticleSystem ps = asteroids.get(i);
				Body psaux = asteroidsAux.get(i);
				
				float[] coords = plt.getPixelCoord(psaux.getPos().x, psaux.getPos().y);

				if (coords[0] < -100 || coords[1] < -100 || coords[0] > p.width + 100 || coords[1] > p.height + 100) {
					asteroids.remove(i);
					asteroidsAux.remove(i);
					break;
				}
				
				if (ps.isEmpty() && ps.hasStopped()) {asteroids.remove(ps); asteroidsAux.remove(psaux); break;}
				
				if (!ps.hasStopped()) {
					psaux.applyForce(new PVector(0, -1e13f));
					if ((PVector.dist(new PVector(ppS[0], ppS[1]), new PVector(coords[0], coords[1])) <= rr[0]*forceFieldPower - 10) && forceFieldUp) {
						if (!burn.isPlaying()) burn.play();
						ps.reduceFlow(50); ps.vel.div(1.2f); 
						this.asteroidPowder += 0.1f;
						if (this.forceFieldPower > 0.1f) this.forceFieldPower -= 0.001;
						}
					psaux.move(dt*speedUp/1000);
				}
				ps.setPos(psaux.getPos());
				
				ps.move(dt);
				ps.display(p, plt);
			}
		}
		
		
		if (!pss.isEmpty()) {
			for (int i = pss.size()-1 ; i>=0 ; i--) {
				ParticleSystem ps = pss.get(i);
				ps.move(dt);
				ps.display(p, plt);
				int times = timesDisplayed.get(pss.indexOf(ps));
				if (times+1 > 50) {ps.stopAdding();}
				else{timesDisplayed.set(pss.indexOf(ps), timesDisplayed.get(pss.indexOf(ps))+1);}
				if (times+1 > 400) {timesDisplayed.remove(pss.indexOf(ps)); pss.remove(ps);}
			}
		}
		
		for (int i = planets.size()-1 ; i >= 0 ; i--) {
			Body planet = planets.get(i);
			if (!pause) {
				if (collisonTimeout.get(planets.indexOf(planet)) != 0){
					collisonTimeout.set(planets.indexOf(planet), collisonTimeout.get(planets.indexOf(planet))-1);
				}
				
				PVector f = sun.attraction(planet);
				planet.applyForce(f); 				
				planet.move(dt * speedUp);
				p.pushStyle();
				p.fill(planet.color);
				p.textSize(14);
				p.stroke(20);
				float[] ppP = plt.getPixelCoord(planet.getPos().x, planet.getPos().y);
				p.text(lives.get(planets.indexOf(planet)) + " hearts", (float) ppP[0] - 20, (float) ppP[1] - 15);
				
				if ((PVector.dist(spaceship.getPos(), planet.getPos()) <= (planet.radius)*1.7f) && collisonTimeout.get(planets.indexOf(planet)) == 0) {
					lives.set(planets.indexOf(planet), lives.get(planets.indexOf(planet))-1);
					collisonTimeout.set(planets.indexOf(planet), 10);
					
					this.collisionsWithPlanet++;
					
					PVector desired = new PVector(spaceship.getVel().x, -spaceship.dna.maxSpeed);
					PVector desired2 = new PVector(spaceship.dna.maxSpeed, spaceship.getVel().y);

					PVector steer = PVector.sub(desired, spaceship.getVel());
					PVector steer2 = PVector.sub(desired2, spaceship.getVel());

					steer.limit(spaceship.dna.maxForce);
					steer2.limit(spaceship.dna.maxForce);

					spaceship.applyForce(steer.mult(.3e2f));
					spaceship.applyForce(steer2.mult(.3e2f));
				}
								
				if (!asteroids.isEmpty()) {
					for (int j = asteroids.size()-1 ; j >=0 ; j--) {
						ParticleSystem ps = asteroids.get(j);
						Body psaux = asteroidsAux.get(j);
						if (!ps.hasStopped() && !ps.isEmpty() && (PVector.dist(ps.getPos(), planet.getPos()) <= (planet.radius)*1.7f) && collisonTimeout.get(planets.indexOf(planet)) == 0) {
							lives.set(planets.indexOf(planet), lives.get(planets.indexOf(planet))-1);
							collisonTimeout.set(planets.indexOf(planet), 10);
							ps.stopAdding();
							burn.play();
							psaux.setVel(new PVector());
						}
					}
				}
				
			}				
			planet.display(p, plt);
			if (planets.indexOf(planet) == selected) planet.displayName(p, plt, names.get(planets.indexOf(planet)), p.color(113, 87, 79));
			if (lives.get(planets.indexOf(planet)) == 0) explode(planet);
		}
		spaceship.move(dt * speedUp);
		
		if (!pause) {
			spaceship.display(p, plt);
			if (displaySSI) {
					p.stroke(250);
					p.strokeWeight(3);
					float[] pos = plt.getPixelCoord(spaceship.getPos().x, spaceship.getPos().y);
					
					p.pushStyle();
					p.fill(p.color(207, 238, 250, 150));
					p.strokeWeight(0);
					p.fill(p.color(61, 63, 49));
					p.textSize(13);
					p.stroke(20);
					p.text("Max Speed: " + Math.round(spaceship.dna.maxSpeed* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 69f));
					p.text("Max Force: " + Math.round(spaceship.dna.maxForce* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 58f));
					p.text("Force Field: " + Math.round(this.forceFieldPower * 10 * 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 47f));
					p.text("Collisions with Planets: " + this.collisionsWithPlanet, (float) (pos[0] - 55f), (float) (pos[1] - 36f));
					p.text("Asteroid Dust Caught: " + Math.round(this.asteroidPowder * 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 25f));
					p.popStyle();
				
			}
		}
	}

	@Override
	public void mousePressed(PApplet p) {
		if (addingPlanets) {
			double[] ww = plt.getWorldCoord(p.mouseX, p.mouseY);
			float newMass = this.massField.getValueF() * 1e24f;
			int newColor = planetsColors[(int) p.random(0, 4)];
			float newSpeed = this.speedField.getValueF() * 1e4f;
			float newRadius = this.radiusField.getValueF() * 1e9f;
			Body newP = new Body(new PVector((float) ww[0], (float) ww[1]), new PVector(newSpeed, 0), newMass,
					newRadius, newColor);
			planets.add(newP);
			masses.add(newMass);
			radiuses.add(newRadius);
			names.add(this.nameField.getText());
			speeds.add(newSpeed);
			colors.add(newColor);
			dists.add((float) ww[0]);
			lives.add(3);
			collisonTimeout.add(0);
			this.addingPlanets = false;
			return;
		}
		selected = -1;
		for (Body planet : planets) {
			if (planet.isInside(p.mouseX, p.mouseY, plt)) {
				selected = planets.indexOf(planet);
				return;
			}
		}
		if (sun.isInside(p.mouseX, p.mouseY, plt)) {
			selected = -2;
			return;
		}
		spaceship.getVel().mult(1.6f);

	}
	
	private void explode(Body toDelete) {
		int indexToDelete = planets.indexOf(toDelete);
		woosh.play();
		PSControl psc = new PSControl(velParams, lifetimeParams, radiusParams, flow, colors.get(indexToDelete));
		ParticleSystem ps  = new ParticleSystem(new PVector(toDelete.getPos().x, toDelete.getPos().y), new PVector(), 2.8e24f, 24622e4f, psc);
		planets.remove(indexToDelete);
		masses.remove(indexToDelete);
		radiuses.remove(indexToDelete);
		names.remove(indexToDelete);
		speeds.remove(indexToDelete);
		colors.remove(indexToDelete);
		lives.remove(indexToDelete);
		collisonTimeout.remove(indexToDelete);
		pss.add(ps);
		timesDisplayed.add(0);
	}

	@Override
	public void keyPressed(PApplet p) {
		if (selected >= 0 && (p.key == 'D' || p.key == 'd')) {
			Body toDelete = planets.get(selected);
			PSControl psc = new PSControl(velParams, lifetimeParams, radiusParams, flow, colors.get(selected));
			explode(toDelete);
		}
		if (p.key == 'A' || p.key == 'a') {
			addingPlanets = !addingPlanets;
		}

		if (p.key == ' ') {
			this.pause = !pause;
		}
		
		if (p.key == 'H' || p.key == 'h') {
			this.help = !help;
			this.nameField.setVisible(this.help);
			this.speedField.setVisible(this.help);
			this.massField.setVisible(this.help);
			this.radiusField.setVisible(this.help);
		}
		
		if ((p.key == 'F' || p.key == 'f') && !forceFieldUp) {
			forceFieldTimer = 200;
			this.shieldsound.play();
			this.forceFieldUp = !forceFieldUp;
		}
		if (p.key == 'I' || p.key == 'i') {
			this.displaySSI = !this.displaySSI;
		}
		
	}

}
