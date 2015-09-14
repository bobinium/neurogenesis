package org.thoughtsfactory.neurogenesis.genetics;

import org.apache.log4j.Logger;


/**
 * Genetic elements are the building blocks of a cell's genome. There are 4
 * main types: cis, trans, input and output.
 * 
 * TODO: Improve the description.
 * 
 * @author Robert Langlois
 */
public class GeneticElement {

    
    /**
     * An enumeration of the valid genetic element types.
     */
    public enum Type {
    	
        CIS, 
        TRANS, 
        SPECIAL_IN_FOOD,
        SPECIAL_IN_CAM,
        SPECIAL_IN_MUTAGEN,
        SPECIAL_OUT_WASTE, 
        SPECIAL_OUT_CAM, 
        SPECIAL_OUT_SAM, 
        SPECIAL_OUT_MUTAGEN,
        SPECIAL_OUT_MITOGEN,
        SPECIAL_OUT_NEUROGEN,
        SPECIAL_OUT_ENERGY,
        SPECIAL_OUT_NEUROTRANS,
        SPECIAL_OUT_FOOD_RATE_IN,
        SPECIAL_OUT_WASTE_RATE_IN,
        SPECIAL_OUT_WASTE_RATE_OUT,
        SPECIAL_OUT_SAM_RATE_OUT,
        SPECIAL_OUT_MUTAGEN_RATE_IN,
        SPECIAL_OUT_MUTAGEN_RATE_OUT,
        SPECIAL_OUT_NEUROGEN_RATE_IN,
        SPECIAL_OUT_NEUROGEN_RATE_OUT,
        
    } // End of Type enum
        
    
    // INSTANCE VARIABLES ======================================================
    
    
    // Class logger for messages.
    private final static Logger logger = Logger.getLogger(GeneticElement.class);    
        
    
    // The type of genetic element.
    private Type type;
    
    
    // The x-axis trans-cis affinity component.
    private double affinityX;
    
    
    // The y-axis trans-cis affinity component.
    private double affinityY;
    
    
    // The sign of the affinity: activatory (+) or inhibitory (-). 
    private int sign;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates new genetic element instance.
     *  
     * @param newType The type of genetic element.
     * @param newAffinityX The x-axis trans-cis affinity component.
     * @param newAffinityY The y-axis trans-cis affinity component.
     * @param newSign The sign of the affinity.
     */
    public GeneticElement(final Type newType, final double newAffinityX, 
            final double newAffinityY, final int newSign) {
        
        this.type = newType;
        this.affinityX = newAffinityX;
        this.affinityY = newAffinityY;
        this.sign = newSign;
        
    } // End of GeneticElement()
    
    
    // METHODS =================================================================
    
    
    /**
     * Returns the type of the genetic element.
     *  
     * @return One of {@link Type}.
     */
    public final Type getType() {
        return this.type;
    }
    
    
    /**
     * Returns the x-axis component of the trans-cis affinity.
     * 
     * @return The x-axis component of the affinity.
     */
    public final double getAffinityX() {
        return this.affinityX;
    }
    
    
    /**
     * Returns the y-axis component of the trans-cis affinity.
     * 
     * @return The y-axis component of the affinity.
     */
    public final double getAffinityY() {
        return this.affinityY;
    }
    
    
    /**
     * Returns the sign of the trans-cis affinity.
     * 
     * @return {@code -1} if the affinity is inhibitory, {@code 1} if it is
     *         activatory.
     */
    public final int getSign() {
        return this.sign;
    }

    
    /**
     * Calculate the affinity score between the current element and a
     * cis-element.
     * 
     * @param cisElement The cis-element for which we want the affinity score 
     *                   calculated.
     * @return The affinity score between the two genetic elements.
     */
    public double getAffinityForCisElement(final GeneticElement cisElement) {
        
        double affinityDeltaX = this.affinityX - cisElement.affinityX;
        double affinityDeltaY = this.affinityY - cisElement.affinityY;
        
        double affinity = Math.sqrt(Math.pow(affinityDeltaX, 2) 
                + Math.pow(affinityDeltaY, 2)) * this.sign * cisElement.sign;
        logger.debug("Affinity: " + affinity);
    
        return affinity;
        
    } // End of getAffinityForCisElement()

    
    /**
     * Clone the current genetic element.
     * 
     * @return A new genetic element instance with the same values as the
     *         current one.
     */
    public GeneticElement clone() {
        
        GeneticElement newElement = 
                new GeneticElement(this.type, this.affinityX, 
                        this.affinityY, this.sign);
        
        return newElement;
        
    } // End of clone()
    

} // End of GeneticElement class
