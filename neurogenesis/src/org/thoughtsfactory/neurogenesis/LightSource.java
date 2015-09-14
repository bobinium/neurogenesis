package org.thoughtsfactory.neurogenesis;

import org.apache.log4j.Logger;

import repast.simphony.space.continuous.ContinuousSpace;


/**
 * Models the light source that illuminates the robot.
 * 
 * @author Robert Langlois
 */
public class LightSource {

   
	// INSTANCE VARIABLES ======================================================
	
	
    // Class logger for messages.
    private final static Logger logger = Logger.getLogger(LightSource.class);    

    
    // The continuous space from which this object will be displayed.
    private final ContinuousSpace<Object> space;
    
    
    // The radius of the light source trajectory.
    private final int radiusOfTrajectory;
    
    
    // The angular position of the light source.
    private double angularPosition;
    
    
    // The angular velocity of the light source.
    private double angularVelocity;
    
    
    // The intensity of the light source.
    private final double lightIntensity;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new light source instance.
     * 
     * @param newSpace The continuous space from which this object will be
     *                 displayed.
     * @param newRadiusOfTrajectory The radius of the trajectory.
     * @param newAngularPosition The initial angular position.
     * @param newAngularVelocity The initial angular velocity.
     * @param newLightIntensity The intensity of the light source.
     */
    public LightSource(final ContinuousSpace<Object> newSpace,
            final int newRadiusOfTrajectory,
            final double newAngularPosition,
            final double newAngularVelocity,
            final double newLightIntensity) {
        
        this.space = newSpace;
        this.radiusOfTrajectory = newRadiusOfTrajectory;
        this.angularPosition = newAngularPosition;
        this.angularVelocity = newAngularVelocity;
        this.lightIntensity = newLightIntensity;
        
    } // End of LightSource()
    
    
    // METHODS =================================================================
    
    
    /**
     * Return the radius of the light source trajectory.
     * 
     * @return The radius of the trajectory.
     */
    public final int getRadiusOfTrajectory() {
        return this.radiusOfTrajectory;
    }
    
    
    /**
     * Return the angular position of the light source.
     * 
     * @return The angular position in radians.
     */
    public final double getAngularPosition() {
        return this.angularPosition;
    }
    

    /**
     * Returns the angular velocity of the light source.
     * 
     * @return The angular velocity in radians/tick.
     */
    public final double getAngularVelocity() {
        return this.angularVelocity;
    }
    
    
    /**
     * Returns the light source intensity.
     * 
     * @return The intensity of the light source.
     */
    public final double getLightIntensity() {
        return this.lightIntensity;
    }
    
    
    /**
     * Update the state of the light source.
     */
    public void update() {
        
        // Constant velocity;
        this.angularPosition = 
                (this.angularPosition + this.angularVelocity) % (2 * Math.PI);
        
        logger.debug("Light source angular position: "
                + this.angularPosition / Math.PI);
        
        this.space.moveTo(this, 
                this.radiusOfTrajectory * Math.cos(this.angularPosition), 
                this.radiusOfTrajectory * Math.sin(this.angularPosition));
        
    } // End update()
    

} // End of LightSource class
