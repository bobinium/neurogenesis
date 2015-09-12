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
	
	
	//
	private String label = "";
	
	
	/**
	 * 
	 */
	public Motor() {	
	}
	
	
	/**
	 * 
	 * @param newLabel
	 */
	public Motor(final String newLabel) {
		
		this.label = newLabel;
		
	} // End of Motor(String)
	
	
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
	public String getLabel() {
		return this.label;
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

	
	/**
	 * 
	 * @param newLabel
	 */
	public void setLabel(final String newLabel) {
		this.label = newLabel;
	}
	
	
} // End of Motor class
