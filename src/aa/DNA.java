package aa;

public class DNA {
	public float maxSpeed;
	public float maxForce;
	public float visionDistance;
	public float visionSafeDistance;
	public float visionAngle;
	public float deltaTPursuit;
	public float radiusArrive;
	public float deltaTWander;
	public float radiusWander;
	public float deltaPhiWander;
	
	public DNA() {
		//Physics
		maxSpeed           = random(5, 7);
		maxForce           = random(4, 7);
		//Vision
		visionDistance     = random(2, 2);
		visionSafeDistance = 0.25f * visionDistance;
		visionAngle    = (float)Math.PI * 0.8f;
		//Persuit
		deltaTPursuit  = random(0.1f, 1f);
		//Arrive
		radiusArrive   = random(3f, 5f);
		//Wander
		//deltaTWander = random(1f, 1f);
		deltaTWander = random(0.5f,0.7f);
		radiusWander   = random(7,10);
		deltaPhiWander = (float)Math.PI/6;
	}
	
	public void reRandomizeSpeed(float min, float max) {
		maxSpeed = random(min, max);		
	}
	
	public void reRandomizeForce(float min, float max) {
		maxForce = random(min, max);	
	}
	
	public void reRandomizeRadiusArrive(float min, float max) {
		radiusArrive = random(min, max);	
	}
	
	public void reRandomizeDeltaTWander(float min, float max) {
		deltaTWander = random(min, max);	
	}
	
	public void reRandomizeRadiusWander(float min, float max) {
		radiusWander = random(min, max);	
	}
	
	public static float random(float min, float max) {
		return (float) (min + (max - min)*Math.random());
	}

}
