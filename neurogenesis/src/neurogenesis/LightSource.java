package neurogenesis;

import repast.simphony.space.continuous.ContinuousSpace;


public class LightSource {

	private final ContinuousSpace<Object> space;
	
	private final int radiusOfTrajectory;
	
	private double angularPosition;
	
	private double angularVelocity;
	
	private final double lightIntensity;
	
	
	/**
	 * 
	 * @param newRadius
	 */
	public LightSource(final ContinuousSpace<Object> newSpace,
			final int newRadiusOfTrajectory,
			final double newAngularPosition,
			final double newAngularVelocity,
			final double newLightIntensity) {
		
		this.space = newSpace;
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
		this.angularPosition = 
				(this.angularPosition + this.angularVelocity) % (2 * Math.PI);
		
		System.out.println("Light source angular position: " 
				+ this.angularPosition / Math.PI);
		
		this.space.moveTo(this, 
				this.radiusOfTrajectory * Math.cos(this.angularPosition), 
				this.radiusOfTrajectory * Math.sin(this.angularPosition));
		
	} // End update()
	
	
	/**
	 * 
	 * @return
	 */
	public double getSineOfAngularPosition() {
		return Math.sin(this.angularPosition);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getCosineOfAngularPosition() {
		return Math.cos(this.angularPosition);
	}
	
	
} // End of LightSource class
