package org.thoughtsfactory.neurogenesis;

import org.thoughtsfactory.neurogenesis.brain.Actuator;


/**
 * A class that represents a robot's motor.
 * 
 * @author Robert Langlois
 */
public class Motor implements Actuator {

    
    // The angular acceleration of the motor.
    private double acceleration = 0;
    
    
    // The label that identifies the motor.
    private String label = "";
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new motor instance.
     */
    public Motor() {    
    }
    
    
    /**
     * Creates a new motor instance identified by the provided label.
     * 
     * @param newLabel The label assigned to the new motor instance.
     */
    public Motor(final String newLabel) {
        
        this.label = newLabel;
        
    } // End of Motor(String)
    

    // METHODS =================================================================
    
    
    // ACCESSORS ---------------------------------------------------------------
    
    
    /**
     * Return the angular acceleration of the motor.
     * 
     * @return The angular acceleration in radians/tick.
     */
    public final double getAcceleration() {
        return this.acceleration;
    }

    
    /**
     * Sets the label that identifies the currrent motor.
     * 
     * @param newLabel The new label of the motor.
     */
    public void setLabel(final String newLabel) {
        this.label = newLabel;
    }
    

    // INTERFACES IMPLEMENTATION -----------------------------------------------
    
    
    /**
     * Returns the label that identifies the current actuator.
     * 
     * @return The label as a string.
     */
    @Override // Actuator
    public String getLabel() {
        return this.label;
    }
    
    
    /**
     * Returns the value that defines the current state of the actuator.
     * 
     * @return The current state of the actuator.
     */
    @Override // Actuator
    public double getValue() {
        return this.acceleration;
    }

    
    /**
     * Sets the value that defines the state of the actuator.
     * 
     * @param newValue The new state of the actuator.
     */
    @Override // Actuator
    public void setValue(final double newValue) {
        this.acceleration = newValue;
    }

    
} // End of Motor class
