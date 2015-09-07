/**
 * 
 */
package org.thoughtsfactory.neurogenesis;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;


/**
 * @author bob
 *
 */
public class Robot {

	
	//
	private final static Logger logger = Logger.getLogger(Robot.class);	
		
	
	//
	private final int radius;
	
	
	//
	private final ContinuousSpace<Object> space;
	
	
	//
	private final LightSensor leftLightSensor;

	
	//
	private final LightSensor rightLightSensor;
	
	
	//
	private final MotionSensor leftMotionSensor = new MotionSensor();
	
	
	//
	private final MotionSensor rightMotionSensor = new MotionSensor();
	
	
	//
	private final Motor leftMotor = new Motor();
	
	
	//
	private final Motor rightMotor = new Motor();
	
	
	//
	private final double maxAngularVelocity;
	
	
	//
	private double angularPosition = 0;
	
	
	//
	private double angularVelocity = 0;
	
	
	/**
	 * 
	 * @param radius
	 * @param newAngularPosition
	 */
	public Robot(final int newRadius,
			final ContinuousSpace<Object> newSpace,
			final double newMaxAngularVelocity) {
		
		this.radius = newRadius;
		this.space = newSpace;
		this.maxAngularVelocity = newMaxAngularVelocity;
				
		this.leftLightSensor = new LightSensor(this.space, Math.PI / 12);
		this.rightLightSensor = new LightSensor(this.space, Math.PI / -12);
		
	} // End of Robot()
	
	
	/**
	 * 
	 * @return
	 */
	public final int getRadius() {
		return this.radius;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final LightSensor getLeftLightSensor() {
		return this.leftLightSensor;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final LightSensor getRightLightSensor() {
		return this.rightLightSensor;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final MotionSensor getLeftMotionSensor() {
		return this.leftMotionSensor;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final MotionSensor getRightMotionSensor() {
		return this.rightMotionSensor;
	}
	
	
	/**
	 * 
	 */
	public final Motor getLeftMotor() {
		return this.leftMotor;
	}
	
	
	/**
	 * 
	 */
	public final Motor getRightMotor() {
		return this.rightMotor;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final double getMaxAngularVelocity() {
		return this.maxAngularVelocity;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final double getAngularPosition() {
		return this.angularPosition;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final double getAngularVelocity() {
		return this.angularVelocity;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final LightSensor[] getLightSensors() {
		return new LightSensor[] { 
				this.leftLightSensor, this.rightLightSensor };
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getAngularPosition(final LightSensor lightSensor) {
		
		return normaliseAngularPosition(this.angularPosition 
				+ lightSensor.getAngularRelativePosition());
		
	} // End of getAngularPosition(LightSensor)
	
	
	/**
	 * 
	 * @return
	 */
	public int getSize() {
		return this.radius * 2 * 15000;
	}
	
	
	/**
	 * 
	 * @param deltaAngularVelocity
	 */
	public void update() {
		
		// Calculates new velocity.
		double newAngularVelocity = this.angularVelocity 
				+ this.leftMotor.getAcceleration() 
				- this.rightMotor.getAcceleration();
		if (newAngularVelocity > this.maxAngularVelocity) {
			this.angularVelocity = this.maxAngularVelocity;
		} else if (newAngularVelocity < -this.maxAngularVelocity) {
			this.angularVelocity = -this.maxAngularVelocity;
		} else {
			this.angularVelocity = newAngularVelocity;
		}
		
		// Calculates new position.
		this.angularPosition = normaliseAngularPosition(this.angularPosition 
				+ this.angularVelocity);
		
		logger.debug("Robot's angular position: "
				+ this.angularPosition / Math.PI);
		logger.debug("Robot's angular velocity: " + this.angularVelocity);
		
		if (this.angularVelocity < 0) {
			this.leftMotionSensor.update(0);
			this.rightMotionSensor.update(
					Math.abs(this.angularVelocity / this.maxAngularVelocity));
		} else {
			this.leftMotionSensor.update(
					this.angularVelocity / this.maxAngularVelocity);
			this.rightMotionSensor.update(0);
		}

	} // End of update()
	
	
	
	/**
	 * 
	 * @param rawPosition
	 * @return
	 */
	private double normaliseAngularPosition(final double rawAngularPosition) {
		
		double normalisedAngularPosition = rawAngularPosition % (2 * Math.PI);
		if (normalisedAngularPosition < 0) {
			normalisedAngularPosition = 
					(2 * Math.PI) + normalisedAngularPosition; 
		}

		return normalisedAngularPosition;
		
	} // End of normaliseAngularPosition()


	/**
	 * 
	 * @param context
	 */
	public void setUp(final Context<Object> context) {
		
		context.add(this);
		this.space.moveTo(this, 0, 0);

		context.add(this.leftLightSensor);
		context.add(this.rightLightSensor);

		this.space.moveTo(this.leftLightSensor, 
				this.radius 
				* Math.cos(getAngularPosition(this.leftLightSensor)), 
				this.radius 
				* Math.sin(getAngularPosition(this.leftLightSensor)));
		
		this.space.moveTo(this.rightLightSensor, 
				this.radius	
				* Math.cos(getAngularPosition(this.rightLightSensor)), 
				this.radius 
				* Math.sin(getAngularPosition(this.rightLightSensor)));
				
	} // End of setUp()
	
	
} // End of Robot class
