package neurogenesis;


public class LightSource {

	private final int radiusOfTrajectory;
	
	private double angularPosition;
	
	private double angularVelocity;
	
	private final double lightIntensity;
	
	
	/**
	 * 
	 * @param newRadius
	 */
	public LightSource(final int newRadiusOfTrajectory,
			final double newAngularPosition,
			final double newAngularVelocity,
			final double newLightIntensity) {
		
		this.radiusOfTrajectory = newRadiusOfTrajectory;
		this.angularPosition = newAngularPosition;
		this.angularVelocity = newAngularVelocity;
		this.lightIntensity = newLightIntensity;
		
	} // End of LightSource()
	
	
	/**
	 * 
	 * @return
	 */
	public int getRadiusOfTrajectory() {
		return this.radiusOfTrajectory;
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
	public double getAngularVelocity() {
		return this.angularVelocity;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getLightIntensity() {
		return this.lightIntensity;
	}
	
	
	/**
	 * 
	 */
	public void update() {
		
		// Constant velocity;
		this.angularPosition = this.angularPosition + this.angularVelocity;
		
	} // End update()
	
} // End of LightSource class
