/**
 * 
 */
package neurogenesis;

import org.apache.log4j.Logger;

import neurogenesis.brain.OutputNeuron;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * @author bob
 *
 */
public class ArenaSimulator {


	//
	private final static Logger logger = Logger.getLogger(ArenaSimulator.class);	

	
	//
	private final Robot robot;
	
	private final LightSource lightSource;
	
	private final double maxIlluminationDistance;
	
	private final OutputNeuron motorNeuron;
	
	
	private double sumAverageAngleDelta = 0;
	
	private long tickCount = 0;
	
	
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
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.FIRST_PRIORITY)
	public void update() {

		logger.info("Updating arena...");
		
		// Constant velocity.
		this.robot.update(this.motorNeuron.getActivation());
		this.lightSource.update();
		
		LightSensor[] lightSensors = this.robot.getLightSensors();
		
		for (LightSensor sensor : lightSensors) {
			
			logger.debug("Sensor angular position: " 
					+ this.robot.getAngularPosition(sensor) / Math.PI);
			
			double distanceSquared = 
					Math.pow(this.lightSource.getRadiusOfTrajectory(), 2) 
					- (2 * this.lightSource.getRadiusOfTrajectory()
							* this.robot.getRadius()
							* Math.cos(this.lightSource.getAngularPosition()
							- this.robot.getAngularPosition(sensor)))
					+ Math.pow(this.robot.getRadius(), 2);
			
			logger.debug("Distance: " + Math.sqrt(distanceSquared) 
					+ ", Max: " + this.maxIlluminationDistance);
			
			double lightIntensity = 0;
			if (Math.sqrt(distanceSquared) <= this.maxIlluminationDistance) {
				lightIntensity = 
					this.lightSource.getLightIntensity() / distanceSquared;
			}
			
			sensor.update(this.robot, lightIntensity);
			
		} // End of for()
		
		this.sumAverageAngleDelta += getAngleDeltaLightAndRobot();
		this.tickCount++;
		
	} // End of update()
	
	
	/**
	 * 
	 * @return
	 */
	public double getAngleDeltaLightAndRobot() {
		
		double angleDelta = Math.abs(getLightSourceAngularPosition() 
				- getRobotAngularPosition());
		
		return (angleDelta > Math.PI) ? 2 * Math.PI - angleDelta : angleDelta;
		
	} // End of getAngleDeltaLightAndRobot()
	
	
	/**
	 * 
	 * @return
	 */
	public double getSignAngleDeltaLightAndRobot() {
		
		double angleDelta = getLightSourceAngularPosition() 
				- getRobotAngularPosition();

		if (angleDelta > Math.PI) {
			angleDelta -= 2 * Math.PI;
		} else if (angleDelta < -Math.PI) {
			angleDelta += 2 * Math.PI;
		}
		
		return Math.signum(angleDelta);

	} // End of getSignAngleDeltaLightAndRobot()
	
	
	/**
	 * 
	 * @return
	 */
	public double getAverageAngleDelta() {
		return this.sumAverageAngleDelta / this.tickCount;
	}

	
	/**
	 * 
	 * @return
	 */
	public double getSineRobotAngularPosition() {
		return Math.sin(getRobotAngularPosition());
	}
	

	/**
	 * 
	 * @return
	 */
	public double getCosineRobotAngularPosition() {
		return Math.cos(getRobotAngularPosition());
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getSineLightAngularPosition() {
		return Math.sin(getLightSourceAngularPosition());
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getCosineLightAngularPosition() {
		return Math.cos(getLightSourceAngularPosition());
	}
	
	
} // End of ArenaSimulator class
