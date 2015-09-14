package org.thoughtsfactory.neurogenesis.brain;

import org.apache.log4j.Logger;

import repast.simphony.space.grid.GridPoint;


/**
 *  Class holding the concentrations of all cell products at a given grid
 *  location.
 *  
 * @author Robert Langlois
 */
public class ExtracellularMatrixSample {

    
    // INSTANCE VARIABLES ======================================================
    
    
    // Class logger for messages.
    @SuppressWarnings("unused")
    private final static Logger logger =
            Logger.getLogger(ExtracellularMatrixSample.class);    
        
    
    // Contains the current concentration of each chemical in this grid cell.
    protected final double[] concentrations = 
            new double[CellProductType.values().length];

    
    // The x-axis grid coordinate of this sample.
    private final int x;
    
    
    // The y-axis grid coordinate of this sample.
    private final int y;
    
    
    // The z-axis grid coordinate of this sample.
    private final int z;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new extracellular matrix sample.
     * 
     * @param newX The x-axis coordinate of this sample.
     * @param newY The y-axis coordinate of this sample.
     * @param newZ The z-axis coordinate of this sample.
     */
    protected ExtracellularMatrixSample(final int newX, 
            final int newY, final int newZ) {
        
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        
        for (CellProductType substanceType : CellProductType.values()) {
            this.concentrations[substanceType.ordinal()] = 0.0;
        }
        
    } // End of ExtracellularMatrixSample()


    // METHODS =================================================================
    
    
    // ACCESSORS ---------------------------------------------------------------
    
    
    /**
     * Returns the x-axis grid coordinate of this sample.
     * 
     * @return The x-axis coordinate.
     */
    public final int getX() {
        return this.x;
    }
    
    
    /**
     * Returns the y-axis grid coordinate of this sample.
     * 
     * @return The y-axis coordinate.
     */
    public final int getY() {
        return this.y;
    }
    
    
    /**
     * Returns the z-axis grid coordinate of this sample.
     * 
     * @return The z-axis coordinate.
     */
    public final int getZ() {
        return this.z;
    }
    
    
    /**
     * Returns the current concentration of a given substance.
     *  
     * @return The substance type.
     */
    public double getConcentration(final CellProductType productType) {
        return this.concentrations[productType.ordinal()];
    }

    
    /**
     * Sets the concentration of a given substance.
     * 
     * @param productType The substance type.
     * @param newConcentration The new concentration.
     */
    public void setConcentration(final CellProductType productType, 
            final double newConcentration) {
        this.concentrations[productType.ordinal()] = newConcentration;
    }
    
    
    /**
     * Returns the current grid coordinates of this sample.
     * 
     * @return The coordinates as a {@code GridPoint} object.
     */
    public GridPoint getPoint() {
        return new GridPoint(this.x, this.y, this.z);
    }
    
        
    // RUNTIME ENVIRONMENT QUERY METHODS ---------------------------------------
    
    
    /**
     * Returns the current food concentration.
     *  
     * @return The food concentration.
     */
    public final double getFoodConcentration() {
        return getConcentration(CellProductType.FOOD);
    }
    
    
    /**
     * Returns the current waste concentration.
     * 
     * @return The waste concentration.
     */
    public final double getWasteConcentration() {
        return getConcentration(CellProductType.WASTE);
    }
    
    
    /**
     * Returns the current substrate adhesion molecules (SAM) concentration.
     * 
     * @return The SAM concentration.
     */
    public final double getSamConcentration() {
        return getConcentration(CellProductType.SAM);
    }
    
    
    /**
     * Returns the current mutagen concentration.
     * 
     * @return The mutagen concentration.
     */
    public final double getMutagenConcentration() {
        return getConcentration(CellProductType.MUTAGEN);
    }
    
    
    /**
     * Returns the current neurogen concentration.
     * 
     * @return The neurogen concentration.
     */
    public final double getNeurogenConcentration() {
        return getConcentration(CellProductType.NEUROGEN);
    }
    

} // End of ExtracellularMatrixSample class
