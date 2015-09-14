package org.thoughtsfactory.neurogenesis;

import org.apache.log4j.Logger;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;


/**
 * The arena supervisor is the agent responsible for maintaining the state of
 * the arena: the virtual area where both the robot and the light source
 * evolves.
 * 
 * @author Robert Langlois
 */
public class ArenaSupervisor {


	// INSTANCE VARIABLES ======================================================
	
	
    // Class logger for messages.
    private final static Logger logger = 
    		Logger.getLogger(ArenaSupervisor.class);    

    
    // The virtual robot.
    private final Robot robot;
    
    
    // The virtual light source.
    private final LightSource lightSource;
    
    
    /*
     * The calculated maximum distance that a point on the robot's outer shell
     * has to be beyond which it is no more illuminated by the light source.
     */
    private final double maxIlluminationDistance;
    
    
    // The statistic sum of the angle between the robot and the light source
    // updated at every tick.
    private double sumAverageAngleDelta = 0;
    
    
    // The current tick count.
    private long tickCount = 0;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new arena supervisor instance.
     * 
     * @param newRobot The virtual robot.
     * @param newLightSource The virtual light source.
     */
    public ArenaSupervisor(final Robot newRobot, 
            final LightSource newLightSource) {
        
        this.robot = newRobot;
        this.lightSource = newLightSource;
        
        this.maxIlluminationDistance = 
                Math.sqrt((this.lightSource.getRadiusOfTrajectory() 
                        - this.robot.getRadius()) 
                        * (this.robot.getRadius() 
                        + this.lightSource.getRadiusOfTrajectory()));
                
    } // End of ArenaSupervisor()
    
    
    // METHODS =================================================================
    
    
    /**
     * Returns the virtual robot deployed in the arena.
     *  
     * @return The virtual robot.
     */
    public final Robot getRobot() {
        return this.robot;
    }
    
    
    /**
     * Return the virtual light source deployed in the arena.
     * 
     * @return The virtual light source.
     */
    public final LightSource getLightSource() {
        return this.lightSource;
    }
    
    
    /**
     * Returns the calculated maximum distance that a point on the robot's outer 
     * shell has to be beyond which it is no more illuminated by the light
     * source.
     * 
     * @return The maximum illumination distance.
     */
    public final double getMaxIlluminationDistance() {
        return this.maxIlluminationDistance;
    }
    
    
    /**
     * Returns the light source current angular position.
     * 
     * @return The angular position of the light source in radians.
     */
    public double getLightSourceAngularPosition() {
        return this.lightSource.getAngularPosition();
    }
    
    
    /**
     * Returns the robot's current angular position.
     * 
     * @return The angular position of the robot in radians.
     */
    public double getRobotAngularPosition() {
        return this.robot.getAngularPosition();
    }
    
    
    /**
     * Update the state of the arena.
     * 
     * This method is scheduled for execution at every tick of the simulation,
     * with a FIRST priority, i.e. BEFORE most cellular agent are allowed
     * action.
     */
    @ScheduledMethod(start = 1, interval = 1, 
            priority = ScheduleParameters.FIRST_PRIORITY)
    public void update() {

        logger.info("Updating arena...");
        
        // Constant velocity.
        this.robot.update();
        this.lightSource.update();
        
        for (LightSensor sensor : this.robot.getLightSensors()) {
            
            logger.debug("Sensor angular position: " 
                    + this.robot.getAngularPosition(sensor) / Math.PI);
            
            double distanceSquared = 
                    Math.pow(this.lightSource.getRadiusOfTrajectory(), 2) 
                    - (2 * this.lightSource.getRadiusOfTrajectory()
                            * this.robot.getRadius()
                            * Math.cos(this.lightSource.getAngularPosition()
                            - this.robot.getAngularPosition(sensor)))
                    + Math.pow(this.robot.getRadius(), 2);
            
            logger.debug("Distance: " + Math.sqrt(distanceSquared) 
                    + ", Max: " + this.maxIlluminationDistance);
            
            double lightIntensity = 0;
            if (Math.sqrt(distanceSquared) <= this.maxIlluminationDistance) {
                lightIntensity = 
                    this.lightSource.getLightIntensity() / distanceSquared;
            }
            
            sensor.update(this.robot, lightIntensity);
            
        } // End of for(sensor)
        
        this.sumAverageAngleDelta += getAngleDeltaLightAndRobot();
        this.tickCount++;
        
    } // End of update()
    
    
    /**
     * Returns the angle between the orientation of the robot and the
     * orientation of the light source.
     * 
     * @return The angle in radians.
     */
    public double getAngleDeltaLightAndRobot() {
        
        double angleDelta = Math.abs(getLightSourceAngularPosition() 
                - getRobotAngularPosition());
        
        return (angleDelta > Math.PI) ? 2 * Math.PI - angleDelta : angleDelta;
        
    } // End of getAngleDeltaLightAndRobot()
    
    
    /**
     * Returns the relative position of the light source relative to the robot.
     * 
     * @return {@code -1} if the light source is to the left of the robot,
     *         {@code 0} if they are both perfectly aligned, and {@code 1}
     *         if the light source is to the right of the robot.
     */
    public double getSignAngleDeltaLightAndRobot() {
        
        double angleDelta = getLightSourceAngularPosition() 
                - getRobotAngularPosition();

        if (angleDelta > Math.PI) {
            angleDelta -= 2 * Math.PI;
        } else if (angleDelta < -Math.PI) {
            angleDelta += 2 * Math.PI;
        }
        
        return Math.signum(angleDelta);

    } // End of getSignAngleDeltaLightAndRobot()
    
    
    // RUNTIME QUERY METHODS ---------------------------------------------------
    
    
    /**
     * Returns the average difference in orientation between the robot and the
     * light source.
     *  
     * @return The average angle between the robot and the light source in
     *         radians.
     */
    public double getAverageAngleDelta() {
        return this.sumAverageAngleDelta / this.tickCount;
    }

    
    /**
     * Returns the sine of the current angle between the robot and the light
     * source.
     * 
     * @return The sine of the angle.
     */
    public double getSineRobotAngularPosition() {
        return Math.sin(getRobotAngularPosition());
    }
    

    /**
     * Returns the cosine of the current angle between the robot and the light
     * source.
     * 
     * @return The cosine of the angle.
     */
    public double getCosineRobotAngularPosition() {
        return Math.cos(getRobotAngularPosition());
    }
    
    
    /**
     * Returns the sine of the current light source angular position.
     * 
     * @return The sine of the angle.
     */
    public double getSineLightAngularPosition() {
        return Math.sin(getLightSourceAngularPosition());
    }
    
    
    /**
     * Returns the cosine of the current light source angular position.
     * 
     * @return The cosine of the angle.
     */
    public double getCosineLightAngularPosition() {
        return Math.cos(getLightSourceAngularPosition());
    }
    

} // End of ArenaSupervisor class
