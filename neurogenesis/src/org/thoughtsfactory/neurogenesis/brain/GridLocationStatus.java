package org.thoughtsfactory.neurogenesis.brain;

import repast.simphony.space.grid.GridPoint;


/**
 * A convenient class for reporting the status of a grid cell.
 * 
 * @author Robert Langlois
 */
public class GridLocationStatus {

    
    // INSTANCE VARIABLES ======================================================
    
    
    // The location of the grid cell.
    private final GridPoint location;
    
    
    // The cell, if any, that currently occupies the location.
    private final Cell occupant;
    
    
    // The extracellular matrix sample at the location.
    private final ExtracellularMatrixSample extracellularMatrixSample;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new instance.
     * 
     * @param newLocation The location on which the status is reported.
     * @param newOccupant The occupant cell found at the location, if any.
     * @param newExtracellularMatrixSample The extracellular matrix sample taken
     *                                     from the location.
     */
    public GridLocationStatus(final GridPoint newLocation, 
            final Cell newOccupant, 
            final ExtracellularMatrixSample newExtracellularMatrixSample) {
        
        this.location = newLocation;
        this.occupant = newOccupant;
        this.extracellularMatrixSample = newExtracellularMatrixSample;
        
    } // End of GridLocationStatus()
    
    
    // METHODS =================================================================
    
    
    /**
     * Returns the location on which the status is reported.
     *  
     * @return The location as a {@code GridPoint} set of coordinates.
     */
    public final GridPoint getLocation() {
        return this.location;
    }
    
    
    /**
     * Returns the occupant cell found at the location, if any.
     * 
     * @return The cell that occupies the location of {@code null} if the
     *         location was free of any cells.
     */
    public final Cell getOccupant() {
        return this.occupant;
    }
    
    
    /**
     * Returns the extracelular matrix sample taken at the location.
     * 
     * @return An {@link ExtracellularMatrixSample} object with all the product
     *         concentrations at the location.
     */
    public final ExtracellularMatrixSample getExtracellularMatrixSample() {
        return this.extracellularMatrixSample;
    }
    

} // End of GridLocationStatus class
