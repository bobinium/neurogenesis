package neurogenesis;

public class LightSensor {

	private final double angularRelativePosition;
	
	private double lightIntensity = 0;
	
	
	/**
	 * 
	 * @param newRobot
	 */
	public LightSensor(final double newAngularRelativePosition) {
		
		this.angularRelativePosition = newAngularRelativePosition;
		
	} // End of LightSensor()
	
	
	/**
	 * 
	 * @return
	 */
	public double getAngularRelativePosition() {
		return this.angularRelativePosition;
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
	public void update(final double newLightIntensity) {
		this.lightIntensity = newLightIntensity;
	}
	
	
} // End of LightSensor class
