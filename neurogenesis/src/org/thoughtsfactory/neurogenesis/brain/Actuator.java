package org.thoughtsfactory.neurogenesis.brain;

/**
 * 
 * @author bob
 *
 */
public interface Actuator {

	
	/**
	 * 
	 * @return
	 */
	public double getValue();
	
	
	/**
	 * 
	 * @param newValue
	 */
	public void setValue(final double newValue);
	
	
} // End of Actuator interface
