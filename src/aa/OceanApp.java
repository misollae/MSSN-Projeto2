package aa;

import java.util.ArrayList;
import java.util.List;
import g4p_controls.GButton;
import g4p_controls.GEvent;
import g4p_controls.GLabel;
import g4p_controls.GTextField;
import physics.Body;
import physics.ParticleSystem;
import physics.Water;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.sound.SoundFile;
import setup.IProcessingApp;
import setup.PSControl;
import tools.SubPlot;

public class OceanApp implements IProcessingApp {

	private OctopiGroup octopi;
	private OctopiGroup otherOctopi;

	private List<ParticleSystem> pss = new ArrayList<ParticleSystem>();

	private float[] sacWeights = { 1f, 1f, .2f };
	private double[] window = { 0, 20, 0, 20 };
	private float[] viewport = { 0, 0, 1, 1 };
	private SubPlot plt;
	private PImage bg;
	private Body target;
	private Seek seek = new Seek(1f);
	private Flee flee = new Flee(.5f);

	private float[] velParams = { PApplet.radians(360), PApplet.radians(360), 0.2f, 1.2f };
	private float[] lifetimeParams = { 1, 3 };
	private float[] radiusParams = { 0.1f, 0.12f };
	private float flow = 400;
	private Body coin;
	private Water water;
	private Octopus withCoin;
	private int gamePhase, emControlo, displayEye, teamWithCoin;
	private int redPoints, bluePoints;
	private boolean coinInScene, playing, help;
	private GTextField speedField2, speedField1, forceField1, forceField2, arriveField1, wanderField1, wanderField2, arriveField2, wanderField4, wanderField3;
	private GLabel label5, label6, label8, label7, label10, label11, label12, label9;
	private GButton updateBttn2;
	private SoundFile file;
	private SoundFile bgs;
	private SoundFile coindropsound;
	private SoundFile bleep;

	@Override
	public void setup(PApplet p) {
		System.out.println("Loading Sound... Please wait!");
		water = new Water(p.height, p.color(0, 40));
		
		try {
			file = new SoundFile(p, "./sound/underwater.mp3");
			bgs = new SoundFile(p, "./sound/seabg.mp3");
			coindropsound = new SoundFile(p, "./sound/coindrop.mp3");
			bleep = new SoundFile(p, "./sound/victorysound.mp3");

			file.loop();	
			bgs.loop();
		} catch (Exception e) {
			System.out.println("Loading sound...");
		}


		bg = p.loadImage("./images/ocean.png");
		plt = new SubPlot(window, viewport, p.width, p.height);
		octopi = new OctopiGroup(3, .1f, .8f,
				new int[] { p.color(170, 23, 33), p.color(232, 91, 84), p.color(243, 193, 126) }, sacWeights, p, plt);
		otherOctopi = new OctopiGroup(3, .1f, .8f,
				new int[] { p.color(189, 223, 198), p.color(150, 187, 206), p.color(110, 120, 207) }, sacWeights, p,
				plt);

		target = new Body(new PVector(), new PVector(), 1f, 0.2f, p.color(255, 0, 0));
		octopi.getBoid(0).eye.setTarget(target);
		octopi.getBoid(0).addBehaviour(seek);
		this.emControlo = 0;
		this.coinInScene = false;
		coin = new Body(new PVector(), new PVector(), 10f, 0.2f, p.color(225, 181, 48));
		this.gamePhase = 0;
		this.withCoin = null;
		this.playing = false;
		this.help = true;
		setUpG4P(p);
	}
	
	public void setUpG4P(PApplet p) {

		// Update moveset
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
		updateBttn2.setLocalColorScheme(p.color(218), true);
		updateBttn2.addEventHandler(this, "handleUpdate2");
	}

	
	public void handleUpdate2(GButton button, GEvent event) {
		if (button == updateBttn2 && event == GEvent.CLICKED) {
			for (Boid o : octopi.getBodies()) {
				o.dna.reRandomizeSpeed(speedField1.getValueF(), speedField2.getValueF());
				o.dna.reRandomizeForce(forceField1.getValueF(), forceField2.getValueF());
				o.dna.reRandomizeRadiusArrive(arriveField1.getValueF(), arriveField2.getValueF());
				o.dna.reRandomizeDeltaTWander(wanderField1.getValueF(), wanderField2.getValueF());
				o.dna.reRandomizeRadiusWander(wanderField3.getValueF(), wanderField4.getValueF());
			}
			for (Boid o : otherOctopi.getBodies()) {
				o.dna.reRandomizeSpeed(speedField1.getValueF(), speedField2.getValueF());
				o.dna.reRandomizeForce(forceField1.getValueF(), forceField2.getValueF());
				o.dna.reRandomizeRadiusArrive(arriveField1.getValueF(), arriveField2.getValueF());
				o.dna.reRandomizeDeltaTWander(wanderField1.getValueF(), wanderField2.getValueF());
				o.dna.reRandomizeRadiusWander(wanderField3.getValueF(), wanderField4.getValueF());
			}
		}
	}

	@Override
	public void draw(PApplet p, float dt) {
		p.background(bg);
//		water.display(p, plt);
		if (help) {			
		p.strokeWeight(3);
		p.fill(p.color(255));
		p.textSize(13.5f);
		p.text("Grab the coin and take it to your base!", 20, 19);
		p.text("H -> Hide/Show help", 20, 34);
		p.text("S -> Start/Stop Coin Chasing game", 20, 49);
		p.text("E -> Toggle debugging elements", 20, 64);
		p.text("1, 2, 3 -> Switch leading octopus", 20, 79);
		p.text("Mouse Click -> Release ink", 20, 94);
		}

		
		if (playing) {

			if (!coinInScene && (int) (p.random(0, 160)) == 20) {
				coinInScene = true;
				this.coindropsound.play();
				coin.setPos(new PVector(p.random(3, 17), 20));
				for (int i = 0; i < 3; i++) {
					if (i != emControlo)
						octopi.getBoid(i).getEye().setTarget(coin);
				}
				for (int i = 0; i < 3; i++) {
					otherOctopi.getBoid(i).getEye().setTarget(coin);
				}
			}

			if (coinInScene && gamePhase != 2) {
				if (coin.getPos().y < 18)
					this.gamePhase = 1;
				if (coin.getPos().y < 0) {
					coinInScene = false;
				} else {
					PVector f = new PVector(0, 10f * -9.8f);
					coin.applyForce(f);
					f = water.drag(coin);
					coin.applyForce(f);
					coin.move(dt * 2);
					coin.display(p, plt);
				}

				for (int i = 0; i < 3; i++) {
					if (Math.abs(octopi.getBoid(i).getPos().x - coin.getPos().x) <= .6f
							&& Math.abs(octopi.getBoid(i).getPos().y - coin.getPos().y) <= .6f) {
						PSControl psc = new PSControl(velParams, lifetimeParams, radiusParams, flow, p.color(0));
						ParticleSystem ps = new ParticleSystem(
								new PVector((float) octopi.getBoid(i).getPos().x, (float) octopi.getBoid(i).getPos().y),
								new PVector(), .1f, .2f, psc);
						pss.add(ps);

						this.gamePhase = 2;
						this.withCoin = (Octopus) octopi.getBoid(i);
						teamWithCoin = 1;

						if (i != emControlo) {
							withCoin.getEye().setTarget(
									new Body(new PVector(0, 0), new PVector(), 10f, 0.2f, p.color(225, 181, 48)));
							PVector desired = new PVector(withCoin.getVel().x, -withCoin.dna.maxSpeed);
							PVector steer = PVector.sub(desired, withCoin.getVel());
							steer.limit(withCoin.dna.maxForce * 2);
							withCoin.applyForce(steer.mult(5f));
						}

					}
				}
				for (int i = 0; i < 3; i++) {
					if (Math.abs(otherOctopi.getBoid(i).getPos().x - coin.getPos().x) <= .6f
							&& Math.abs(otherOctopi.getBoid(i).getPos().y - coin.getPos().y) <= .6f) {
						this.gamePhase = 2;
						this.withCoin = (Octopus) otherOctopi.getBoid(i);
						teamWithCoin = 2;

						PSControl psc = new PSControl(velParams, lifetimeParams, radiusParams, flow, p.color(0));
						ParticleSystem ps = new ParticleSystem(new PVector((float) otherOctopi.getBoid(i).getPos().x,
								(float) otherOctopi.getBoid(i).getPos().y), new PVector(), .1f, .2f, psc);
						pss.add(ps);
						PVector desired = new PVector(withCoin.getVel().x, -withCoin.dna.maxSpeed);
						PVector steer = PVector.sub(desired, withCoin.getVel());
						steer.limit(withCoin.dna.maxForce * 2);
						withCoin.applyForce(steer.mult(5f));
						withCoin.getEye().setTarget(
								new Body(new PVector(20, 0), new PVector(), 10f, 0.2f, p.color(225, 181, 48)));
					}
				}
			}

			if (coinInScene && gamePhase == 2) {
				coin.setPos(new PVector(withCoin.getPos().x + 0.5f, withCoin.getPos().y + .15f));
				coin.move(dt);
				
				if (teamWithCoin == 2) {
					withCoin.addBehaviour(flee);
					withCoin.getEye().setTarget(octopi.getBoid(0));
					withCoin.applyBehavior(withCoin.behaviors.size()-1,dt/10);
					withCoin.getEye().setTarget(octopi.getBoid(1));
					withCoin.applyBehavior(withCoin.behaviors.size()-1,dt/10);
					withCoin.getEye().setTarget(octopi.getBoid(2));
					withCoin.applyBehavior(withCoin.behaviors.size()-1,dt/10);
					withCoin.getEye().setTarget(new Body(new PVector(20, 0), new PVector(), 10f, 0.2f, p.color(225, 181, 48)));
					withCoin.removeBehavior(flee);
					
					
				}
				
				if (teamWithCoin == 1) {
					if (!withCoin.equals(octopi.getBoid(emControlo))) {
						withCoin.addBehaviour(flee);
						withCoin.getEye().setTarget(otherOctopi.getBoid(0));
						withCoin.applyBehavior(withCoin.behaviors.size()-1,dt/10);
						withCoin.getEye().setTarget(otherOctopi.getBoid(1));
						withCoin.applyBehavior(withCoin.behaviors.size()-1,dt/10);
						withCoin.getEye().setTarget(otherOctopi.getBoid(2));
						withCoin.applyBehavior(withCoin.behaviors.size()-1,dt/10);
						withCoin.getEye().setTarget(new Body(new PVector(0, 0), new PVector(), 10f, 0.2f, p.color(225, 181, 48)));
						withCoin.removeBehavior(flee);
					}
					
				}

				System.out.println(coin.getPos());
				
				if (Math.pow(coin.getPos().x, 2) <= 7 && Math.pow(coin.getPos().y, 2) <= 7) {
					gamePhase = 0;
					withCoin = null;
					coinInScene = false;
					bleep.play();
					redPoints++;
				}
				if (Math.pow((coin.getPos().x - 20), 2) <= 7 && Math.pow(coin.getPos().y, 2) <= 7) {
					gamePhase = 0;
					withCoin = null;
					bleep.play();
					coinInScene = false;
					bluePoints++;
				}

				else {
					coin.display(p, plt);
				}
			}

			p.strokeWeight(3);
			p.stroke(p.color(170, 23, 33));
			p.fill(p.color(255));
			p.textSize(15);
			p.text("Red team coins: " + redPoints, 110, p.height - 15);
			p.text("Blue team coins: " + bluePoints, 575, p.height - 15);
			p.fill(p.color(170, 23, 33, 150));
			p.circle(0, p.height, 200);
			p.fill(p.color(110, 120, 207, 150));
			p.stroke(p.color(110, 120, 207));
			p.circle(p.width, p.height, 200);

		}

		double[] pp = plt.getWorldCoord(p.mouseX, p.pmouseY);
		target.setPos(new PVector((float) pp[0], (float) pp[1]));
		
		if (Math.abs(octopi.getBoid(emControlo).getPos().x - target.getPos().x) <= .05f && Math.abs(octopi.getBoid(emControlo).getPos().y - target.getPos().y) <= .05f){
			octopi.getBoid(emControlo).setPos(target.getPos());
			}
			

		if (displayEye == 1) {
			for (Boid o : octopi.getBodies()) {
				o.getEye().display(p, plt);
				p.stroke(250);
				p.strokeWeight(3);
				float[] pos = plt.getPixelCoord(o.getPos().x, o.getPos().y);
				float[] vel = plt.getPixelCoord(o.getVel().x / 2 + o.getPos().x, o.getVel().y / 2 + o.getPos().y);

				p.line(pos[0], pos[1], vel[0], vel[1]);
				p.circle(vel[0], vel[1], 3);
				p.pushStyle();
				p.fill(p.color(207, 238, 250, 150));
				p.strokeWeight(0);
				p.rect((float) (pos[0] - 57f), (float) (pos[1] - 80f), 115, 58);
				p.fill(p.color(61, 63, 49));
				p.textSize(13);
				p.stroke(20);
				p.text("Max Speed: " + Math.round(o.dna.maxSpeed* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 69f));
				p.text("Max Force: " + Math.round(o.dna.maxForce* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 58f));
				p.text("Radius Arrive: " + Math.round(o.dna.radiusArrive* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 47f));
				p.text("Wander DeltaT: " + Math.round(o.dna.deltaTWander* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 36f));
				p.text("Radius Wander: " + Math.round(o.dna.radiusWander* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 25f));
				p.popStyle();

			}

		}
		if (displayEye == 2) {
			for (Boid o : otherOctopi.getBodies()) {
				o.getEye().display(p, plt);
				p.stroke(250);
				p.strokeWeight(3);
				float[] pos = plt.getPixelCoord(o.getPos().x, o.getPos().y);
				float[] vel = plt.getPixelCoord(o.getVel().x / 2 + o.getPos().x, o.getVel().y / 2 + o.getPos().y);
				
				p.line(pos[0], pos[1], vel[0], vel[1]);
				p.circle(vel[0], vel[1], 3);
				p.pushStyle();
				p.fill(p.color(207, 238, 250, 150));
				p.strokeWeight(0);
				p.rect((float) (pos[0] - 57f), (float) (pos[1] - 80f), 115, 58);
				p.fill(p.color(61, 63, 49));
				p.textSize(13);
				p.stroke(20);
				p.text("Max Speed: " + Math.round(o.dna.maxSpeed* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 69f));
				p.text("Max Force: " + Math.round(o.dna.maxForce* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 58f));
				p.text("Radius Arrive: " + Math.round(o.dna.radiusArrive* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 47f));
				p.text("Wander DeltaT: " + Math.round(o.dna.deltaTWander* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 36f));
				p.text("Radius Wander: " + Math.round(o.dna.radiusWander* 100.0) / 100.0, (float) (pos[0] - 55f), (float) (pos[1] - 25f));
				p.popStyle();
			}
		}

		// Proíbir saídas por cima/baixo

		for (int i = 0; i < 3; i++) {
			Boid b = octopi.getBoid(i);
			if (b.getPos().y <= 1) {
				PVector desired = new PVector(b.getVel().x, b.dna.maxSpeed);
				PVector steer = PVector.sub(desired, b.getVel());
				steer.limit(b.dna.maxForce);
				b.applyForce(steer);
			}
			if (b.getPos().y >= 19) {
				PVector desired = new PVector(b.getVel().x, -b.dna.maxSpeed);
				PVector steer = PVector.sub(desired, b.getVel());
				steer.limit(b.dna.maxForce);
				b.applyForce(steer);
			}
			if (b.getPos().x <= .5f) {
				PVector desired = new PVector(b.dna.maxSpeed, b.getVel().y);
				PVector steer = PVector.sub(desired, b.getVel());
				steer.limit(b.dna.maxForce);
				b.applyForce(steer);
			}
			if (b.getPos().x >= 19.5f) {
				PVector desired = new PVector(-b.dna.maxSpeed, b.getVel().y);
				PVector steer = PVector.sub(desired, b.getVel());
				steer.limit(b.dna.maxForce);
				b.applyForce(steer);
			}
		}
		for (int i = 0; i < 3; i++) {
			Boid b = otherOctopi.getBoid(i);
			if (b.getPos().y <= 1) {
				PVector desired = new PVector(b.getVel().x, b.dna.maxSpeed);
				PVector steer = PVector.sub(desired, b.getVel());
				steer.limit(b.dna.maxForce);
				b.applyForce(steer);
			}
			if (b.getPos().y >= 19) {
				PVector desired = new PVector(b.getVel().x, -b.dna.maxSpeed);
				PVector steer = PVector.sub(desired, b.getVel());
				steer.limit(b.dna.maxForce);
				b.applyForce(steer);
			}
			if (b.getPos().x <= .5f) {
				PVector desired = new PVector(b.dna.maxSpeed, b.getVel().y);
				PVector steer = PVector.sub(desired, b.getVel());
				steer.limit(b.dna.maxForce);
				b.applyForce(steer);
			}
			if (b.getPos().x >= 19.5f) {
				PVector desired = new PVector(-b.dna.maxSpeed, b.getVel().y);
				PVector steer = PVector.sub(desired, b.getVel());
				steer.limit(b.dna.maxForce);
				b.applyForce(steer);
			}
		}

		octopi.applyBehavior(dt, gamePhase);
		octopi.display(p, plt);
		otherOctopi.applyBehavior(dt, gamePhase);
		otherOctopi.display(p, plt);

		for (ParticleSystem ps : pss) {
			if (ps.getNumParticles() > 400)
				ps.stopAdding();
			ps.move(dt);
			ps.display(p, plt);
		}
	}

	@Override
	public void mousePressed(PApplet p) {
		if (this.help && p.mouseX >= p.width - 120 && p.mouseX <= p.width && p.mouseY >= 0 && p.mouseY <= 205) return;

		PSControl psc = new PSControl(velParams, lifetimeParams, radiusParams, flow, p.color(0));
		ParticleSystem ps = new ParticleSystem(new PVector((float) octopi.getBoid(emControlo).getPos().x,
				(float) octopi.getBoid(emControlo).getPos().y), new PVector(), .1f, .2f, psc);
		octopi.getBoid(emControlo).applyForce(octopi.getBoid(emControlo).getVel().mult(3));
		pss.add(ps);
	}

	@Override
	public void keyPressed(PApplet p) {
		if (p.key == '1') {
			octopi.getBoid(emControlo).removeBehavior(seek);
			octopi.getBoid(emControlo).eye.setTarget(octopi.getBoid(0));
			this.emControlo = 0;
			octopi.getBoid(emControlo).eye.setTarget(target);
			octopi.getBoid(emControlo).addBehaviour(seek);
		}
		if (p.key == '2') {
			octopi.getBoid(emControlo).removeBehavior(seek);
			octopi.getBoid(emControlo).eye.setTarget(octopi.getBoid(0));
			this.emControlo = 1;
			octopi.getBoid(emControlo).eye.setTarget(target);
			octopi.getBoid(emControlo).addBehaviour(seek);
		}
		if (p.key == '3') {
			octopi.getBoid(emControlo).removeBehavior(seek);
			octopi.getBoid(emControlo).eye.setTarget(octopi.getBoid(0));
			this.emControlo = 2;
			octopi.getBoid(emControlo).eye.setTarget(target);
			octopi.getBoid(emControlo).addBehaviour(seek);
		}
		if (p.key == 'S' || p.key == 's') {
			this.playing = !playing;
			if (playing == false) {
				this.gamePhase = 0;
				for (int i = 0; i < 3; i++) {
					if (i != emControlo)
						octopi.getBoid(i).getEye().setTarget(octopi.getBoid(0));
				}
				for (int i = 0; i < 3; i++) {
					otherOctopi.getBoid(i).getEye().setTarget(otherOctopi.getBoid(0));
				}
			}
		}
		if (p.key == 'E' || p.key == 'e') {
			this.displayEye++;
			System.out.println(displayEye);
			if (displayEye > 2)
				displayEye = 0;
		}
		if (p.key == 'H' || p.key == 'h') {
			this.help = !help;
			speedField1.setVisible(this.help); speedField2.setVisible(this.help); 
			forceField1.setVisible(this.help); forceField2.setVisible(this.help);
			arriveField2.setVisible(this.help); arriveField1.setVisible(this.help);
			wanderField2.setVisible(this.help); wanderField1.setVisible(this.help);
			wanderField3.setVisible(this.help); wanderField4.setVisible(this.help);
			label5.setVisible(this.help); label6.setVisible(this.help); 
			label7.setVisible(this.help); label8.setVisible(this.help); label9.setVisible(this.help);
			label10.setVisible(this.help); label11.setVisible(this.help); label12.setVisible(this.help);
			updateBttn2.setVisible(this.help);
		}
	}

}
