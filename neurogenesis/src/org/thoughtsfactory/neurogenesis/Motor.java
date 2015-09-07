package org.thoughtsfactory.neurogenesis;

import org.thoughtsfactory.neurogenesis.brain.Actuator;


/**
 * 
 * @author bob
 *
 */
public class Motor implements Actuator {

	
	//
	private double acceleration = 0;
	
	
	/**
	 * 
	 * @return
	 */
	public final double getAcceleration() {
		return this.acceleration;
	}
	
	
	/**
	 * 
	 */
	@Override
	public double getValue() {
		return this.acceleration;
	}

	/**
	 * 
	 */
	@Override
	public void setValue(final double newValue) {
		this.acceleration = newValue;
	}

	
} // End of Motor class
