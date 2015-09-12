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
	
	
	//
	private String label = "";
	
	
	/**
	 * 
	 */
	public MotionSensor() {
	}
	
	
	/**
	 * 
	 * @param newLabel
	 */
	public MotionSensor(final String newLabel) {
		
		this.label = newLabel;
	
	} // End of MotionSensor(String)
	
	
	/**
	 * 
	 * @return
	 */
	public final double getSpeedRatio() {
		return this.speedRatio;
	}
	
	
	/**
	 * 
	 * @return
	 */
	@Override
	public String getLabel() {
		return this.label;
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
	 * @param newLabel
	 */
	public void setLabel(final String newLabel) {
		this.label = newLabel;
	}
	
	
	/**
	 * 
	 * @param newSpeedRatio
	 */
	public void update(final double newSpeedRatio) {
		this.speedRatio = newSpeedRatio;
	}
	
	
} // End of MotionSensor class
