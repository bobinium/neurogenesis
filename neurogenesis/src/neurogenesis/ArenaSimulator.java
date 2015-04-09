/**
 * 
 */
package neurogenesis;

import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * @author bob
 *
 */
public class ArenaSimulator {

	private final Robot robot;
	
	private final LightSource lightSource;
	
	private final double maxIlluminationDistance;
	
	private final OutputNeuron motorNeuron;
	
	
	/**
	 * 
	 * @param newRobot
	 * @param newLightSource
	 */
	public ArenaSimulator(final Robot newRobot, 
			final LightSource newLightSource,
			final OutputNeuron newMotorNeuron) {
		
		this.robot = newRobot;
		this.lightSource = newLightSource;
		this.motorNeuron = newMotorNeuron;
		
		this.maxIlluminationDistance = 
				Math.sqrt((this.lightSource.getRadiusOfTrajectory() 
						- this.robot.getRadius()) 
						* (this.robot.getRadius() 
						+ this.lightSource.getRadiusOfTrajectory()));
				
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
	
	
	public double getLightSourceAngularPosition() {
		return this.lightSource.getAngularPosition();
	}
	
	
	public double getRobotAngularPosition() {
		return this.robot.getAngularPosition();
	}
	
	
	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void update() {

		System.out.println("Updating arena...");
		
		// Constant velocity.
		this.robot.update(this.motorNeuron.getActivation());
		this.lightSource.update();
		
		LightSensor[] lightSensors = this.robot.getLightSensors();
		
		for (LightSensor sensor : lightSensors) {
			
			System.out.println("Sensor angular position: " + this.robot.getAngularPosition(sensor) / Math.PI);
			
			double distanceSquared = 
					Math.pow(this.lightSource.getRadiusOfTrajectory(), 2) 
					- (2 * this.lightSource.getRadiusOfTrajectory()
							* this.robot.getRadius()
							* Math.cos(this.lightSource.getAngularPosition()
							- this.robot.getAngularPosition(sensor)))
					+ Math.pow(this.robot.getRadius(), 2);
			
			System.out.println("Distance: " + Math.sqrt(distanceSquared) + ", Max: " + this.maxIlluminationDistance);
			
			double lightIntensity = 0;
			if (Math.sqrt(distanceSquared) <= this.maxIlluminationDistance) {
				lightIntensity = 
					this.lightSource.getLightIntensity() / distanceSquared;
			}
			
			sensor.update(this.robot, lightIntensity);
			
		} // End of for()
		
	} // End of update()
	
	
} // End of ExperimentSimulator class
