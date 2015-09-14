package org.thoughtsfactory.neurogenesis;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.space.continuous.ContinuousSpace;


/**
 * Models the robot which is expected to interact with the light source.
 * 
 * @author Robert Langlois
 */
public class Robot {

    
	// INSTANCE VARIABLES ======================================================
	
	
    // Class logger for messages.
    private final static Logger logger = Logger.getLogger(Robot.class);    
        
    
    // The radius of the robot.
    private final int radius;
    
    
    // The continuous space from which this object will be displayed.
    private final ContinuousSpace<Object> space;
    
    
    // The light sensor located on the left side of the robot.
    private final LightSensor leftLightSensor;

    
    // The light sensor located on the right side of the robot.
    private final LightSensor rightLightSensor;
    
    
    // The motion sensor activated when the robot is rotating to the left
    // (i.e. anticlockwise).
    private final MotionSensor leftMotionSensor = new MotionSensor("Left");
    
    
    // The motion sensor activated when the robot is rotating to the right
    // (i.e. clockwise).
    private final MotionSensor rightMotionSensor = new MotionSensor("Right");
    
    
    // The motor used to steer the robot to the left (i.e. anticlockwise).
    private final Motor leftMotor = new Motor("Left");
    
    
    // The motor used to steer the robot to the right (i.e. clockwise).
    private final Motor rightMotor = new Motor("Right");
    
    
    // The angular speed limit for the robot.
    private final double maxAngularVelocity;
    
    
    // The current angular position of the robot.
    private double angularPosition = 0;
    
    
    // The current angular velocity of the robot.
    private double angularVelocity = 0;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new robot instance.
     *  
     * @param radius The radius of the robot.
     * @param newSpace The continuous space from which this object will be
     *                 displayed.
     * @param newMaxAngularVelocity The angular speed limit for the robot.
     */
    public Robot(final int newRadius,
            final ContinuousSpace<Object> newSpace,
            final double newMaxAngularVelocity) {
        
        this.radius = newRadius;
        this.space = newSpace;
        this.maxAngularVelocity = newMaxAngularVelocity;
                
        this.leftLightSensor = new LightSensor(this.space, Math.PI / 12);
        this.rightLightSensor = new LightSensor(this.space, Math.PI / -12);
        
    } // End of Robot()
    

    // METHODS =================================================================
    
    
    /**
     * Returns the radius of the robot.
     *  
     * @return The robot's radius.
     */
    public final int getRadius() {
        return this.radius;
    }
    
    
    /**
     * Returns light sensor located on the left side of the robot.
     * 
     * @return The left light sensor.
     */
    public final LightSensor getLeftLightSensor() {
        return this.leftLightSensor;
    }
    
    
    /**
     * Returns light sensor located on the right side of the robot.
     * 
     * @return The right light sensor.
     */
    public final LightSensor getRightLightSensor() {
        return this.rightLightSensor;
    }
    
    
    /**
     * Returns the motion sensor activated when the robot is rotating to the
     * left (i.e. anticlockwise).
     * 
     * @return The left motion sensor.
     */
    public final MotionSensor getLeftMotionSensor() {
        return this.leftMotionSensor;
    }
    
    
    /**
     * Returns the motion sensor activated when the robot is rotating to the
     * right (i.e. clockwise).
     * 
     * @return The right motion sensor.
     */
    public final MotionSensor getRightMotionSensor() {
        return this.rightMotionSensor;
    }
    
    
    /**
     * Returns the motor used to steer the robot to the left
     * (i.e. anticlockwise).
     * 
     * @returns The left motor.
     */
    public final Motor getLeftMotor() {
        return this.leftMotor;
    }
    
    
    /**
     * Returns the motor used to steer the robot to the right (i.e. clockwise).
     * 
     * @returns The right motor.
     */
    public final Motor getRightMotor() {
        return this.rightMotor;
    }
    
    
    /**
     * Returns the angular speed limit for the robot.
     * 
     * @return The maximum angular velocity.
     */
    public final double getMaxAngularVelocity() {
        return this.maxAngularVelocity;
    }
    
    
    /**
     * Returns the current angular position of the robot.
     * 
     * @return The angular position in radians.
     */
    public final double getAngularPosition() {
        return this.angularPosition;
    }
    
    
    /**
     * Returns the current angular velocity of the robot.
     * 
     * @return The angular velocity in radians/tick, positive if rotating to
     *         the left, negative if rotating to the right.
     */
    public final double getAngularVelocity() {
        return this.angularVelocity;
    }
    
    
    /**
     * Returns the set of light sensors the robots has.
     * 
     * @return An array of light sensors.
     */
    public final LightSensor[] getLightSensors() {
        return new LightSensor[] { 
                this.leftLightSensor, this.rightLightSensor };
    }
    
    
    /**
     * Returns the absolute angular position of the specified light sensor.
     * 
     * @param lightSensor One of the robot's iight sensor.
     * @return The absolute angular position in radians.
     */
    public double getAngularPosition(final LightSensor lightSensor) {
        
        return normaliseAngularPosition(this.angularPosition 
                + lightSensor.getAngularRelativePosition());
        
    } // End of getAngularPosition(LightSensor)
    
    
    /**
     * The size of the robot for the benefit of the 2D display.
     * 
     * @return The size the robot should have when displayed.
     */
    public int getSize() {
        return this.radius * 2 * 15000;
    }
    
    
    /**
     * Update the state of the robot.
     */
    public void update() {
        
        double leftAcceleration = this.leftMotor.getAcceleration();
        logger.debug("Left motor accelration: " + leftAcceleration);
        
        double rightAcceleration = this.rightMotor.getAcceleration();
        logger.debug("Right motor accelration: " + rightAcceleration);
        
        // Calculates new velocity.
        double newAngularVelocity = this.angularVelocity 
                + leftAcceleration - rightAcceleration;
        if (newAngularVelocity > this.maxAngularVelocity) {
            this.angularVelocity = this.maxAngularVelocity;
        } else if (newAngularVelocity < -this.maxAngularVelocity) {
            this.angularVelocity = -this.maxAngularVelocity;
        } else {
            this.angularVelocity = newAngularVelocity;
        }
        
        // Calculates new position.
        this.angularPosition = normaliseAngularPosition(this.angularPosition 
                + this.angularVelocity);
        
        logger.debug("Robot's angular position: "
                + this.angularPosition / Math.PI);
        logger.debug("Robot's angular velocity: " + this.angularVelocity);
        
        if (this.angularVelocity < 0) {
            this.leftMotionSensor.update(0);
            this.rightMotionSensor.update(
                    Math.abs(this.angularVelocity / this.maxAngularVelocity));
        } else {
            this.leftMotionSensor.update(
                    this.angularVelocity / this.maxAngularVelocity);
            this.rightMotionSensor.update(0);
        }

    } // End of update()
    
    
    
    /**
     * Normalise angular position in the range 2 * PI.
     *  
     * @param rawAngularPosition Angular position before normalisation.
     * @return The new normalised angular position (0 <= x < 2 * PI).
     */
    private double normaliseAngularPosition(final double rawAngularPosition) {
        
        double normalisedAngularPosition = rawAngularPosition % (2 * Math.PI);
        if (normalisedAngularPosition < 0) {
            normalisedAngularPosition = 
                    (2 * Math.PI) + normalisedAngularPosition; 
        }

        return normalisedAngularPosition;
        
    } // End of normaliseAngularPosition()


    /**
     * Initialises the robot.
     * 
     * @param context The Repast simulation context.
     */
    public void setUp(final Context<Object> context) {
        
        context.add(this);
        this.space.moveTo(this, 0, 0);

        context.add(this.leftLightSensor);
        context.add(this.rightLightSensor);

        this.space.moveTo(this.leftLightSensor, 
                this.radius 
                * Math.cos(getAngularPosition(this.leftLightSensor)), 
                this.radius 
                * Math.sin(getAngularPosition(this.leftLightSensor)));
        
        this.space.moveTo(this.rightLightSensor, 
                this.radius    
                * Math.cos(getAngularPosition(this.rightLightSensor)), 
                this.radius 
                * Math.sin(getAngularPosition(this.rightLightSensor)));
                
    } // End of setUp()
    

} // End of Robot class
