package org.thoughtsfactory.neurogenesis;


/**
 * A configuration object holds a singleton with all the parameters needed to
 * run the simulation.
 *  
 * @author Robert Langlois
 */
public final class Configuration {

    
	// CLASS VARIABLES =========================================================
	

    // The singleton instance.
    private static final Configuration configuration = new Configuration();

    
	// INSTANCE VARIABLES ======================================================
	
	
    // The brain grid quadrant size.
    private int brainGridQuadrantSize;
    
    
    // The size of the genom in number of regulatory units.
    private int genomeSize;
    

    // Indicates if the cell adhesion feature is enabled.
    private boolean cellAdhesionEnabled;

    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Private constructor: prevents instantiation.
     */
    private Configuration() {
    }
    
    
    // METHODS =================================================================
    
    
    /**
     * Returns the configuration singleton.
     *  
     * @return The configuration instance.
     */
    public static Configuration getInstance() {
        return configuration;
    }
    

    /**
     * Returns the size of a Cartesian quadrant of the grid, excluding the
     * central origin at 0.
     * 
     * @return The brain grid quadrant size.
     */
    public int getBrainGridQuadrantSize() {
        return this.brainGridQuadrantSize;
    }
    
    
    /**
     * Sets the brain grid quadrant size.
     * 
     * @param The grid quadrant size.
     */
    public void setBrainGridQuadrantSize(final int newSize) {
        this.brainGridQuadrantSize = newSize;
    }
    
    
    /**
     * Returns the genome size in number of regulatory units.
     * 
     * @return The genome size.
     */
    public int getGenomeSize() {
        return this.genomeSize;
    }
    
    
    /**
     * Sets the number of regulatory units in the genome.
     * 
     * @param newGenomeSize The genome size.
     */
    public void setGenomeSize(final int newGenomeSize) {
        this.genomeSize = newGenomeSize;
    }
    

    /**
     * Indicates if the cell adhesion feature is enables or not.
     * 
     * @return {@code true} if the feature is enabled, {@code false} otherwise.
     */
    public boolean isCellAdhesionEnabled() {
        return this.cellAdhesionEnabled;
    }
    
    
    /**
     * Specify if the cell adhesion feature is enabled.
     * 
     * @param newValue {@code true} if the feature is enabled, {@code false}
     *                 otherwise.
     */
    public void setCellAdhesionEnabled(final boolean newValue) {
        this.cellAdhesionEnabled = newValue;
    }
    

} // End of Configuration class
