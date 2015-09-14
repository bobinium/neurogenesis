package org.thoughtsfactory.neurogenesis;

import org.thoughtsfactory.neurogenesis.brain.Sensor;


/**
 * A class that represents a robot's motion sensor.
 * 
 * @author Robert Langlois
 */
public class MotionSensor implements Sensor {

    
	// INSTANCE VARIABLES ======================================================
	
	
    // The ratio of the current angular speed of the robot over its maximum
	// angular speed.
    private double speedRatio = 0;
    
    
    // The label of the sensor.
    private String label = "";
    
    
    // CONSTRUCTORS ============================================================
    
    /**
     * Creates a new motion sensor instance.
     */
    public MotionSensor() {
    }
    
    
    /**
     * Creates a new motion sensor instance identified by the provided label.
     * 
     * @param newLabel The label assigned to the new sensor instance.
     */
    public MotionSensor(final String newLabel) {
        
        this.label = newLabel;
    
    } // End of MotionSensor(String)
    
    
    // METHODS =================================================================
    
    
    /**
     * Returns the ratio of the current angular speed of the robot over its
     * maximum angular speed.
     * 
     * @return The angular speed ratio.
     */
    public final double getSpeedRatio() {
        return this.speedRatio;
    }
    
    
    /**
     * Sets the label that identifies the currrent sensor.
     * 
     * @param newLabel The new label of the sensor.
     */
    public final void setLabel(final String newLabel) {
        this.label = newLabel;
    }
    
    
    /**
     * Update the state of the sensor.
     * 
     * @param newSpeedRatio The updated angular speed ratio.
     */
    public void update(final double newSpeedRatio) {
        this.speedRatio = newSpeedRatio;
    }

    
    // INTERFACES IMPLEMENTATION -----------------------------------------------
    
    
    /**
     * Returns the label that identifies the current sensor.
     * 
     * @return The label as a string.
     */
    @Override // Sensor
    public String getLabel() {
        return this.label;
    }
    
    
    /**
     * Returns the value provided by the sensor.
     * 
     * @return The value provided by the sensor.
     */
    @Override // Sensor
    public double getValue() {
        return this.speedRatio;
    }


} // End of MotionSensor class
