package org.thoughtsfactory.neurogenesis.brain;


/**
 * A sensor describes the operations that are available on a virtual input
 * device such as a robot's light sensor. Input to the brain's neural network is
 * provided by implementing objects through this interface.
 * 
 * @author Robert Langlois
 */
public interface Sensor {

    
    /**
     * Returns the label that identifies the current sensor.
     * 
     * @return The label as a string.
     */
    public String getLabel();
    
    
    /**
     * Returns the value provided by the sensor.
     * 
     * @return The value provided by the sensor.
     */
    public double getValue();
    
    
} // End of Sensor interface
