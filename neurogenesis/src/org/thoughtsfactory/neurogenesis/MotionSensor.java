package org.thoughtsfactory.neurogenesis;

import org.thoughtsfactory.neurogenesis.brain.Sensor;


/**
 * 
 * @author bob
 *
 */
public class MotionSensor implements Sensor {

	
	//
	private double speedRatio = 0;
	
	
	/**
	 * 
	 * @return
	 */
	public final double getSpeedRatio() {
		return this.speedRatio;
	}
	
	
	/**
	 * 
	 */
	@Override
	public double getValue() {
		return this.speedRatio;
	}


	/**
	 * 
	 * @param newSpeedRatio
	 */
	public void update(final double newSpeedRatio) {
		this.speedRatio = newSpeedRatio;
	}
	
	
} // End of MotionSensor class
