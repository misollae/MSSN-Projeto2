package physics;


import processing.core.PVector;

public abstract class Mover {
	// atributos, protected pois vai usar herança
	protected PVector pos;
	protected PVector vel;
	protected PVector acc;
	protected float mass;
	private static double G = 6.67e-11;
	
	// Construtor
	protected Mover(PVector pos, PVector vel, float mass) {
		this.pos    = pos.copy();
		this.vel    = vel;
		this.mass   = mass;
		acc = new PVector();
	}
	
	public void applyForce(PVector force) {
		// Se chamar duas vezes este método com dois
		// vetores de força diferente, estamos a somá-las
		acc.add(PVector.div(force, mass));
	}
	
	public PVector attraction(Mover m) {
		PVector r  = PVector.sub(pos, m.pos);
		float dist = r.mag();
		float strength = (float) (G * mass * m.mass / Math.pow(dist, 2));
		return r.normalize().mult(strength);
	}
	
	public void move(float dt) {
		vel.add(acc.mult(dt));  // método "Destrói" a aceleração
		pos.add(PVector.mult(vel, dt));
		acc.mult(0); // Garantir que acc é 0
	}
	
	public void setPos(PVector pos) {
		this.pos = pos;
	}
	public void setVel(PVector vel) {
		this.vel = vel;
	}
	public PVector getPos() {
		return pos;
	}
	public PVector getVel() {
		return vel;
	}
	
	public double getG() {
		return G;
	}
	
	public static void setG(double G) {
		Mover.G = G;
	} 
}
