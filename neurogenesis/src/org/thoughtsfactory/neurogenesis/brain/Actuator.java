package org.thoughtsfactory.neurogenesis.brain;


/**
 * An {@code Actuator} describes the operations that are available on a
 * virtual output device such as a robot's motor. Output from the brain's
 * neural network is provided to implementing objects through this interface.
 * 
 * @author Robert Langlois
 */
public interface Actuator {

    
    /**
     * Returns the label that identifies the current actuator.
     * 
     * @return The label as a string.
     */
    public String getLabel();
    
    
    /**
     * Returns the value that defines the current state of the actuator.
     * 
     * @return The current state of the actuator.
     */
    public double getValue();
    
    
    /**
     * Sets the value that defines the state of the actuator.
     * 
     * @param newValue The new state of the actuator.
     */
    public void setValue(final double newValue);
    

} // End of Actuator interface
