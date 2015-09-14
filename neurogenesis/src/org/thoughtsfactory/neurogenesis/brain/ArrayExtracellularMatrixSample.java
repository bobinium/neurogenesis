package org.thoughtsfactory.neurogenesis.brain;

import org.apache.log4j.Logger;


/**
 *  Class holding the concentrations of all cell products at a given grid
 *  location.
 *  
 * @author Robert Langlois
 */
public class ArrayExtracellularMatrixSample extends ExtracellularMatrixSample {


    // INSTANCE VARIABLES ======================================================
    
    
    // Class logger for messages.
    @SuppressWarnings("unused")
    private final static Logger logger = 
            Logger.getLogger(ArrayExtracellularMatrixSample.class);    
        
        
    // The extracellular matrix from which this sample was taken.
    private final ArrayExtracellularMatrix extracellularMatrix;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new extracellular matrix sample.
     * 
     * @param newX The x-axis coordinate of this sample.
     * @param newY The y-axis coordinate of this sample.
     * @param newZ The z-axis coordinate of this sample.
     * @param newExtracellularMatrix The extracellular matrix from which this
     *                               sample originates.
     */
    public ArrayExtracellularMatrixSample(final int newX, 
            final int newY, final int newZ, 
            final ArrayExtracellularMatrix newExtracellularMatrix) {
        
        super(newX, newY, newZ);
        
        this.extracellularMatrix = newExtracellularMatrix;
        
    } // End of ArrayExtracellularMatrixSample()


    // METHODS =================================================================

    
    /**
     * Sets the concentration of the specified cell product to the given value.
     * 
     * @param productType The substance type.
     * @param newConcentration The new concentration.
     */
    @Override // ExtracellularMatrixSample
    public void setConcentration(final CellProductType productType, 
            final double newConcentration) {
        
        super.setConcentration(productType, newConcentration);

        // Callback the extracellular matrix for update.
        this.extracellularMatrix.updateConcentration(getX(), getY(), getZ(), 
                productType, newConcentration);
        
    } // End of setConcentration()


} // End of ArrayExtracellularMatrixSample class
