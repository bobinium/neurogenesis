package org.thoughtsfactory.neurogenesis.brain;


/**
 * A cell membrane channel holds the concentration inside the cell of a specific
 * chemical as well its rate of transfer to an from the extracellular matrix.
 * Channels can be set to be open or closed for either input or output, or both.
 * 
 * @author Robert Langlois.
 */
public class CellMembraneChannel {
    

    // INSTANCE VARIABLES ======================================================
    
    
    // The substance managed by this channel.
    private CellProductType substanceType;
    
    // The current internal concentration.
    private double concentration;
    
    // The rate at which the current substance 
    // is absorbed from the extracellular matrix.
    private double inputRate;
    
    // Indicates if the substance is allowed to flow inside the cell.
    private boolean openForInput;
    
    // The rate at which the substance flows out
    // of the cell into the extracellular matrix.
    private double outputRate;
    
    // Indicated if the substance is allowed to flow out of the cell.
    private boolean openForOutput;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new cell membrane channel instance.
     *  
     * @param newSubstanceType The substance managed by this channel.
     * @param newConcentration The initial concentration.
     * @param newInputRate The initial input rate.
     * @param newInputOpen Indicates if input is allowed or not.
     * @param newOutputRate The initial output rate.
     * @param newOutputOpen Indicates if output is allowed or not.
     */
    public CellMembraneChannel(final CellProductType newSubstanceType, 
            final double newConcentration, 
            final double newInputRate,
            final boolean newInputOpen,
            final double newOutputRate,
            final boolean newOutputOpen) {
        
        this.substanceType = newSubstanceType;
        this.concentration = newConcentration;
        this.inputRate = newInputRate;
        this.openForInput = newInputOpen;
        this.outputRate = newOutputRate;
        this.openForOutput = newOutputOpen;
        
    } // End of CellMembraneChannel()
    
    
    // METHODS =================================================================
    
    
    /**
     * Returns the type of substance managed by this channel.
     *  
     * @return The cell prooduct type.
     */
    public final CellProductType getSubstanceType() {
        return this.substanceType;
    }
    
    
    /**
     * Returns the current internal concentration.
     * 
     * @return The substance concentration inside the cell.
     */
    public final double getConcentration() {
        return this.concentration;
    }
    
    
    /**
     * Sets the concentration inside the cell.
     * 
     * @param newConcentration The new internal concentration.
     */
    public final void setConcentration(final double newConcentration) {
        this.concentration = newConcentration;
    }
    
    
    /**
     * Returns the rate at which the substance is absorbed from the 
     * extracellular matrix.
     * 
     * @return The substance input rate.
     */
    public final double getInputRate() {
        return this.inputRate;
    }
    
    
    /**
     * Sets the rate at which the substance is absorbed from the extracellular 
     * matrix.
     * 
     * @param newInputRate The new input rate.
     */
    public final void setInputRate(final double newInputRate) {
        this.inputRate = newInputRate;
    }
    
    
    /**
     * Indicates if the substance is allowed to flow inside the cell.
     * 
     * @return {@code true} if input is allowed, {@code false} otherwise.
     */
    public final boolean isOpenForInput() {
        return this.openForInput;
    }
    
    
    /**
     * Specify if the substance is allowed to flow inside the cell.
     * 
     * @param newValue {@code true} if input is allowed, {@code false} if not.
     */
    public final void setOpenForInput(final boolean newValue) {
        this.openForInput = newValue;
    }
    
    
    /**
     * Returns the rate at which the substance flows out of the cell into the 
     * extracellular matrix.
     * 
     * @return The output rate.
     */
    public final double getOutputRate() {
        return this.outputRate;
    }
    
    
    /**
     * Sets The rate at which the substance flows out of the cell into the 
     * extracellular matrix.
     * 
     * @param newOutputRate The new output rate.
     */
    public final void setOutputRate(final double newOutputRate) {
        this.outputRate = newOutputRate;
    }
    
    
    /**
     * Indicates if the substance is allowed to flow out of the cell.
     * 
     * @return {@code true} if output is allowed, {@code false} otherwise.
     */
    public final boolean isOpenForOutput() {
        return this.openForOutput;
    }
    
    
    /**
     * Specify if the substance is allowed to flow out of the cell.
     * 
     * @param newValue {@code true} if output is allowed, {@code false} if not. 
     */
    public final void setOpenForOutput(final boolean newValue) {
        this.openForOutput = newValue;
    }
    

} // End of CellMembraneChannel class
