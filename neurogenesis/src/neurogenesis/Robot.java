/**
 * 
 */
package neurogenesis;


/**
 * @author bob
 *
 */
public class Robot {

	
	private int radius;
	
	private double angularPosition;
	
	private final double maxAngularVelocity;
	
	private double angularVelocity;
	
	private final LightSensor[] lightSensors;
	
	
	/**
	 * 
	 * @param radius
	 * @param newAngularPosition
	 */
	public Robot(final int newRadius, 
			final double newAngularPosition,
			final double newMaxAngularVelocity,
			final double newAngularVelocity,
			final LightSensor[] newLightSensors) {
		
		this.radius = newRadius;
		this.angularPosition = newAngularPosition;
		this.maxAngularVelocity = newMaxAngularVelocity;
		this.angularVelocity = newAngularVelocity;
		this.lightSensors = newLightSensors;
				
	} // End of Robot()
	
	
	/**
	 * 
	 * @return
	 */
	public int getRadius() {
		return this.radius;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getAngularPosition() {
		return this.angularPosition;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getMaxAngularVelocity() {
		return this.maxAngularVelocity;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getAngularVelocity() {
		return this.angularVelocity;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public LightSensor[] getLightSensors() {
		// Return a defensive copy of the array.
		return this.lightSensors.clone();
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getAngularPosition(final LightSensor lightSensor) {
		
		return this.angularPosition + lightSensor.getAngularRelativePosition();
		
	} // End of getAngularPosition(LightSensor)
	
	
	/**
	 * 
	 * @param deltaAngularVelocity
	 */
	public void update(final double deltaAngularVelocity) {
		
		// Calculates new velocity.
		double newAngularVelocity = 
				this.angularVelocity + deltaAngularVelocity;
		if (newAngularVelocity > this.maxAngularVelocity) {
			this.angularVelocity = this.maxAngularVelocity;
		} else {
			this.angularVelocity = newAngularVelocity;
		}
		
		// Calculates new position.
		this.angularPosition = 
				(this.angularPosition + this.angularVelocity) % (2 * Math.PI);
		
		System.out.println("Robot's angular position: " + this.angularPosition);
		
	} // End of update()
	
	
} // End of Robot class
