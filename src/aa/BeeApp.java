package aa;

import java.util.ArrayList;
import java.util.List;
import g4p_controls.GButton;
import g4p_controls.GEvent;
import g4p_controls.GLabel;
import g4p_controls.GTextField;
import physics.Body;
import physics.ParticleSystem;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;
import setup.IProcessingApp;
import setup.PSControl;
import tools.SubPlot;

public class BeeApp implements IProcessingApp {
	private int visitlimit;

	private Bee b;
	private double[] window = { -10, 10, -10, 10 };
	private float[] viewport = { 0, 0, 1, 1 };
	private SubPlot plt;
	private Body target;
	private List<Body> allTrackingBodies;
	private Patrol patrol;
	private Arrive arrive;

	private float[] velParams = { PApplet.radians(360), PApplet.radians(360), .3f, .5f };
	private float[] lifetimeParams = { 1, 2 };
	private float[] radiusParams = { 0.05f, 0.08f };
	private float flow = 90;

	private List<Bee> bees = new ArrayList<Bee>();
	private List<ParticleSystem> pss;
	private List<Integer> quantity;
	private PImage bg;
	private boolean displayInfo, displayDNA;
	private GLabel label1, label2, label3, label4, label5, label6, label7, label8, label9, label10, label11, label12; 
	private boolean help;
	private boolean activeBrake;
	private GTextField visitField;
	private GButton updateBttn, updateBttn2;
	private GTextField speedField1, speedField2, forceField1, forceField2, arriveField2, arriveField1, wanderField2, wanderField1, wanderField3, wanderField4;

	private GLabel label13;

	private GLabel label14;

	private SoundFile file;

	private SoundFile bgs;

	@Override
	public void setup(PApplet p) {
		System.out.println("Loading Sound... Please wait!");

		visitlimit = 3;
		try {
			file = new SoundFile(p, "./sound/bee.mp3");
			bgs = new SoundFile(p, "./sound/beebg.mp3");

			file.loop();	
			bgs.loop();
		} catch (Exception e) {
			System.out.println("Loading sound...");
		}
		bg = p.loadImage("./images/flowerfield3.png");
		plt = new SubPlot(window, viewport, p.width, p.height);
		b = new Bee(new PVector(), 1, 0.5f, p.color(0), p, plt);
		patrol = new Patrol(1f, new Arrive(.8f));
		b.addBehaviour(patrol);
		b.addBehaviour(new Wander(.2f));
		b.addBehaviour(new Brake(1f));
		bees.add(b);
		pss = new ArrayList<ParticleSystem>();

		target = new Body(new PVector(), new PVector(), 1f, 0.2f, p.color(255, 0, 0));
		allTrackingBodies = new ArrayList<>();
		quantity = new ArrayList<Integer>();
		allTrackingBodies.add(target);
		Eye eye = new Eye(b, allTrackingBodies);
		b.setEye(eye);
		this.displayInfo = false;
		this.help = true;
		this.displayDNA = false;
		this.activeBrake = false;
		setUpG4P(p);
	}

	public void setUpG4P(PApplet p) {

		label4 = new GLabel(p, 20, 20, 1500, 20, "H -> Hide/Show help");
		label4.setLocalColorScheme(p.color(200));
		label14 = new GLabel(p, 20, 65, 1500, 20, "Up/Down -> Speed up/Slow down bee");
		label14.setLocalColorScheme(p.color(200));
		label2 = new GLabel(p, 20, 50, 1500, 20, "P -> Hide/Show pollen info");
		label2.setLocalColorScheme(p.color(200));
		label13 = new GLabel(p, 20, 35, 1500, 20, "B -> Hide/Show Bee's DNA");
		label13.setLocalColorScheme(p.color(200));
		label1 = new GLabel(p, p.width - 110 - 35, 220, 1500, 20, "Visit amount: ");
		label1.setLocalColorScheme(p.color(200));
		label3 = new GLabel(p, 20, 5, 1500, 20, "Click any spot on the screen to add pollen. Use Up/Down to change the bee's speed.");
		label3.setLocalColorScheme(p.color(200));
		updateBttn = new GButton(p, p.width - 70, 250, 50, 20, "Set!");
		updateBttn.setLocalColorScheme(180, true);
		updateBttn.addEventHandler(this, "handleUpdate");
		visitField = new GTextField(p, p.width - 70, 220, 50, 20);
		visitField.setNumeric(0, 999999999, visitlimit);
		visitField.setText("3");
		// Update bee moveset
		label5 = new GLabel(p, p.width - 110 - 35, 10, 1500, 20, "Speed:             /");
		label5.setLocalColorScheme(p.color(200));
		speedField1 = new GTextField(p, p.width - 100, 10, 35, 20);
		speedField1.setNumeric(0f, 999999999f, 5f);
		speedField1.setText("5");
		speedField2 = new GTextField(p,  p.width - 55, 10, 35, 20);
		speedField2.setNumeric(0f, 999999999f, 7f);
		speedField2.setText("7");
		
		label6 = new GLabel(p, p.width - 110 - 35, 35, 1500, 20, "Force:              /");
		label6.setLocalColorScheme(p.color(200));
		forceField1 = new GTextField(p, p.width - 100, 35, 35, 20);
		forceField1.setNumeric(0, 999999999, 4f);
		forceField1.setText("4");
		forceField2 = new GTextField(p,  p.width - 55, 35, 35, 20);
		forceField2.setNumeric(0, 999999999, 7f);
		forceField2.setText("7");
		
		label7 = new GLabel(p, p.width - 110 - 35, 56, 1500, 20, "Arrival Radius:");
		label8 = new GLabel(p, p.width - 110 - 35, 78, 1500, 20, "                        /");
		label8.setLocalColorScheme(p.color(200));
		label7.setLocalColorScheme(p.color(200));
		arriveField1 = new GTextField(p, p.width - 100, 76, 35, 20);
		arriveField1.setNumeric(0f, 999999999f, 3f);
		arriveField1.setText("3");
		arriveField2 = new GTextField(p,  p.width - 55, 76, 35, 20);
		arriveField2.setNumeric(0f, 999999999f, 5f);
		arriveField2.setText("5");
		
		label9 = new GLabel(p, p.width - 110 - 35, 96, 1500, 20, "Wander Variation:");
		label10 = new GLabel(p, p.width - 110 - 35, 119, 1500, 20, "                        /");
		label9.setLocalColorScheme(p.color(200));
		label10.setLocalColorScheme(p.color(200));
		wanderField1 = new GTextField(p, p.width - 100, 116, 35, 20);
		wanderField1.setNumeric(0f, 999999999f, 0.5f);
		wanderField1.setText("0.5");
		wanderField2 = new GTextField(p,  p.width - 55, 116, 35, 20);
		wanderField2.setNumeric(0f, 999999999f, 0.7f);
		wanderField2.setText("0.7");
		
		label11 = new GLabel(p, p.width - 110 - 35, 136, 1500, 20, "Wander Radius:");
		label12 = new GLabel(p, p.width - 110 - 35, 159, 1500, 20, "                        /");
		label11.setLocalColorScheme(p.color(200));
		label12.setLocalColorScheme(p.color(200));
		wanderField3 = new GTextField(p, p.width - 100, 156, 35, 20);
		wanderField3.setNumeric(0f, 999999999f, 7f);
		wanderField3.setText("7");
		wanderField4 = new GTextField(p,  p.width - 55, 156, 35, 20);
		wanderField4.setNumeric(0f, 999999999f, 10f);
		wanderField4.setText("10");
		
		updateBttn2 = new GButton(p, p.width - 100, 185, 80, 20, "Randomize!");
		updateBttn2.setLocalColorScheme(180, true);
		updateBttn2.addEventHandler(this, "handleUpdate2");
	}

	public void handleUpdate(GButton button, GEvent event) {
		if (button == updateBttn && event == GEvent.CLICKED) {
			this.visitlimit = visitField.getValueI();
		}
	}
	
	public void handleUpdate2(GButton button, GEvent event) {
		if (button == updateBttn2 && event == GEvent.CLICKED) {
			b.dna.reRandomizeSpeed(speedField1.getValueF(), speedField2.getValueF());
			b.dna.reRandomizeForce(forceField1.getValueF(), forceField2.getValueF());
			b.dna.reRandomizeRadiusArrive(arriveField1.getValueF(), arriveField2.getValueF());
			b.dna.reRandomizeDeltaTWander(wanderField1.getValueF(), wanderField2.getValueF());
			b.dna.reRandomizeRadiusWander(wanderField3.getValueF(), wanderField4.getValueF());
		}
	}

	@Override
	public void draw(PApplet p, float dt) {
		p.background(bg);
		for (ParticleSystem ps : pss) {

			if (quantity.get(pss.indexOf(ps)) == 0) {
				ps.stopAdding();
				patrol.remFromPath(ps);
				allTrackingBodies.remove(ps);
			}
			if (ps.hasStopped() && ps.isEmpty()) {
				quantity.remove(pss.indexOf(ps));
				pss.remove(ps);
				break;
			} else if (displayInfo && !ps.hasStopped()) {
				p.pushStyle();
				float[] pos = plt.getPixelCoord(ps.getPos().x, ps.getPos().y);
				if (pos[1] < p.height / 1.8)
					p.fill(p.color(254, 254, 232));
				else {
					p.fill(p.color(254, 254, 232, 150));
					p.strokeWeight(0);
					p.rect((float) (pos[0] - 57f), (float) (pos[1] - 40f), 115, 20);
					p.fill(p.color(61, 63, 49));
				}
				p.textSize(14);
				p.stroke(20);
				p.text("Remaining Visits: " + quantity.get(pss.indexOf(ps)), (float) (pos[0] - 55f), (float) (pos[1] - 25f));
				p.popStyle();
			}
			ps.move(dt);
			ps.display(p, plt);
		}
		
		if (displayDNA) {
			p.pushStyle();
			float[] pos = plt.getPixelCoord(b.getPos().x, b.getPos().y);
			if (pos[1] < p.height / 1.8)
				p.fill(p.color(254, 254, 232));
			else {
				p.fill(p.color(254, 254, 232, 150));
				p.strokeWeight(0);
				p.rect((float) (pos[0] - 57f), (float) (pos[1] - 80f), 115, 58);
				p.fill(p.color(61, 63, 49));
			}
			p.textSize(13);
			p.stroke(20);
			p.text("Max Speed: " + Math.round(b.dna.maxSpeed* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 69f));
			p.text("Max Force: " + Math.round(b.dna.maxForce* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 58f));
			p.text("Radius Arrive: " + Math.round(b.dna.radiusArrive* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 47f));
			p.text("Wander DeltaT: " + Math.round(b.dna.deltaTWander* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 36f));
			p.text("Radius Wander: " + Math.round(b.dna.radiusWander* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 25f));
			p.popStyle();
		}

		if (!pss.isEmpty())checkVisits(p);
		
		// Proíbir saídas por cima/baixo
		if (b.getPos().y <= -7) {
			PVector desired = new PVector(b.getVel().x, b.dna.maxSpeed);
			PVector steer = PVector.sub(desired, b.getVel());
			steer.limit(b.dna.maxForce);
			System.out.println("hi");
			b.applyForce(steer);
		}
		if (b.getPos().y >= 7) {
			PVector desired = new PVector(b.getVel().x, -b.dna.maxSpeed);
			PVector steer = PVector.sub(desired, b.getVel());
			steer.limit(b.dna.maxForce);
			b.applyForce(steer);
		}
		
		for (Bee bee : bees) {
			if (pss.isEmpty())
				bee.applyBehavior(1, dt);
			else {
				bee.applyBehavior(0, dt);
			}
		}
		b.display(p, plt);
	}

	public void checkVisits(PApplet p) {
		float visitRadius = (b.dna.maxSpeed <= 5) ? .6f : 3/b.dna.maxSpeed;
		if (Math.abs(b.getPos().x - b.getEye().getTarget().getPos().x) <= visitRadius && Math.abs(b.getPos().y - b.getEye().getTarget().getPos().y) <= visitRadius) {
			for (ParticleSystem ps : pss) {
				if (ps.getPos().equals(b.getEye().getTarget().getPos())) {
					quantity.set(pss.indexOf(ps), quantity.get(pss.indexOf(ps)) - 1);
				}
			}
		}
	}

	@Override
	public void mousePressed(PApplet p) {
		if (this.help && p.mouseX >= p.width - 120 && p.mouseX <= p.width && p.mouseY >= 0 && p.mouseY <= 280) return;
		double[] ww = plt.getWorldCoord(p.mouseX, p.mouseY);
		PSControl psc = new PSControl(velParams, lifetimeParams, radiusParams, flow, p.color(235, 150, 5));
		ParticleSystem ps = new ParticleSystem(new PVector((float) ww[0], (float) ww[1]), new PVector(), .1f, .2f, psc);
		pss.add(ps);
		quantity.add(visitlimit);
		allTrackingBodies.add(ps);
		patrol.addToPath(ps);
	}

	@Override
	public void keyPressed(PApplet p) {
		if (p.key == PConstants.CODED && p.keyCode == PConstants.UP) {
			b.dna.maxSpeed += .1f;
		}
		if (p.key == PConstants.CODED && p.keyCode == PConstants.DOWN) {
			if (b.dna.maxSpeed - .1f < 0) b.dna.maxSpeed = 0f;
			else b.dna.maxSpeed -= .1f;
		}
		if (p.key == 'P' || p.key == 'p') {
			this.displayInfo = !displayInfo;
		}
		
		if (p.key == 'B' || p.key == 'b') {
			this.displayDNA = !displayDNA;
		}

		if (p.key == 'H' || p.key == 'h') {
			this.help = !help;
			visitField.setVisible(this.help);
			speedField1.setVisible(this.help); speedField2.setVisible(this.help); 
			forceField1.setVisible(this.help); forceField2.setVisible(this.help);
			arriveField2.setVisible(this.help); arriveField1.setVisible(this.help);
			wanderField2.setVisible(this.help); wanderField1.setVisible(this.help);
			wanderField3.setVisible(this.help); wanderField4.setVisible(this.help);
			updateBttn.setVisible(this.help); updateBttn2.setVisible(this.help);
			label1.setVisible(this.help); label2.setVisible(this.help); label3.setVisible(this.help);
			label4.setVisible(this.help); label5.setVisible(this.help); label6.setVisible(this.help); 
			label7.setVisible(this.help); label8.setVisible(this.help); label9.setVisible(this.help);
			label10.setVisible(this.help); label11.setVisible(this.help); label12.setVisible(this.help);
			label13.setVisible(this.help); label14.setVisible(this.help);
		}
	}

}
