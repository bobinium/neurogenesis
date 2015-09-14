package org.thoughtsfactory.neurogenesis;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.brain.Sensor;

import repast.simphony.space.continuous.ContinuousSpace;


/**
 * A class that represents a robot's light sensor.
 * 
 * @author Robert Langlois
 */
public class LightSensor implements Sensor {

    
	// INSTANCE VARIABLES ======================================================
	
	
    // Class logger for messages;
    private final static Logger logger = Logger.getLogger(LightSensor.class);    

    
    // The continuous space from which this sensor will be displayed.
    private final ContinuousSpace<Object> space;
    
    
    // The position of the current sensor relative to the robot's median.
    private final double angularRelativePosition;
    
    
    // The light intensity currently perceived by the sensor.
    private double lightIntensity = 0;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new light sensor instance.
     * 
     * @param newSpace The continuous space from which the sensor will be
     *                 displayed.
     * @param newAngularRelativePosition The position of the sensor relative to
     *                                   the robot's median.
     */
    public LightSensor(final ContinuousSpace<Object> newSpace,
            final double newAngularRelativePosition) {
        
        this.space = newSpace;
        this.angularRelativePosition = newAngularRelativePosition;
                
    } // End of LightSensor()
    
    
    // METHODS =================================================================
    
    
    // ACCESSORS ---------------------------------------------------------------
    
    
    /**
     * Returns the continuous space from which this sensor is being displayed.
     * 
     * @return The continuous space of the current object.
     */
    public final ContinuousSpace<Object> getSpace() {
        return this.space;
    }
    
    
    /**
     * Returns the position of the current sensor relative to the robot's
     * median.
     * 
     * @return The relative position in radians.
     */
    public final double getAngularRelativePosition() {
        return this.angularRelativePosition;
    }
    
    
    /**
     * Returns the light intensity currently perceived by the sensor.
     * 
     * @return The current light intensity.
     */
    public final double getLightIntensity() {
        return this.lightIntensity;
    }
    
    
    // OTHER METHODS -----------------------------------------------------------
    

    /**
     * Update the state of the light sensor.
     * 
     * @param robot The robot to which this sensor belongs.
     * @param newLightIntensity The updated light intensity.
     */
    public void update(final Robot robot, final double newLightIntensity) {
        
    	// Move the sensor along with the robot.
        this.space.moveTo(this, 
                robot.getRadius() * Math.cos(robot.getAngularPosition(this)), 
                robot.getRadius() * Math.sin(robot.getAngularPosition(this)));
        
        logger.debug("Updating sensor (" + getLabel() 
                + ") with intensity " + newLightIntensity);
        this.lightIntensity = newLightIntensity;
        
    } // End of update()

    
    // INTERFACES IMPLEMENTATION -----------------------------------------------
    

    /**
     * Returns the label that identifies the current sensor.
     * 
     * @return The label as a string.
     */
    @Override // Sensor
    public String getLabel() {
        return this.angularRelativePosition > 0 ? "Left" : "Right";
    }

    
    /**
     * Returns the value provided by the sensor.
     * 
     * @return The value provided by the sensor.
     */
    @Override // Sensor
    public double getValue() {
        return this.lightIntensity;
    }

    
} // End of LightSensor class
