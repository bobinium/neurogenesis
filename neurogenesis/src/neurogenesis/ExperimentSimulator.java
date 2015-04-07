/**
 * 
 */
package neurogenesis;

import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * @author bob
 *
 */
public class ExperimentSimulator {

	private final Robot robot;
	
	private final LightSource lightSource;
	
	private final double maxIlluminationDistance;
	
	
	/**
	 * 
	 * @param newRobot
	 * @param newLightSource
	 */
	public ExperimentSimulator(final Robot newRobot, 
			final LightSource newLightSource) {
		
		this.robot = newRobot;
		this.lightSource = newLightSource;
		
		this.maxIlluminationDistance = 
				Math.sqrt(Math.pow(this.lightSource.getRadiusOfTrajectory(), 2) 
						- (2 * this.lightSource.getRadiusOfTrajectory() 
								* this.robot.getRadius()));
				
	} // End of ExperimentSimulator()
	
	
	/**
	 * 
	 * @return
	 */
	public Robot getRobot() {
		return this.robot;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public LightSource getLightSource() {
		return this.lightSource;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getMaxIlluminationDistance() {
		return this.maxIlluminationDistance;
	}
	
	
	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void update() {

		// Constant velocity.
		this.robot.update(0);
		this.lightSource.update();
		
		LightSensor[] lightSensors = this.robot.getLightSensors();
		
		for (LightSensor sensor : lightSensors) {
			
			double distanceSquared = 
					Math.pow(this.lightSource.getRadiusOfTrajectory(), 2) 
					+ (2 * this.lightSource.getRadiusOfTrajectory()
							* this.robot.getRadius()
							* Math.cos(this.lightSource.getAngularPosition()
							- this.robot.getAngularPosition(sensor)))
					+ Math.pow(this.robot.getRadius(), 2);
			
			double lightIntensity = 0;
			if (Math.sqrt(distanceSquared) <= this.maxIlluminationDistance) {
				lightIntensity = 
					this.lightSource.getLightIntensity() / distanceSquared;
			}
			
			sensor.update(lightIntensity);
			
		} // End of for()
		
	} // End of update()
	
	
} // End of ExperimentSimulator class
