package org.thoughtsfactory.neurogenesis.brain;

import org.apache.log4j.Logger;



/**
 * 
 * @author bob
 *
 */
public class ArrayExtracellularMatrixSample extends ExtracellularMatrixSample {


	// PROPERTIES ==============================================================
	

	// CONSTANTS ---------------------------------------------------------------
	
	
	// INSTANCE VARIABLES ------------------------------------------------------
	
	
	//
	@SuppressWarnings("unused")
	private final static Logger logger = 
			Logger.getLogger(ArrayExtracellularMatrixSample.class);	
		
		
	//
	private final ArrayExtracellularMatrix extracellularMatrix;
	
	
	// CONSTRUCTORS ============================================================
	
	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	public ArrayExtracellularMatrixSample(final int newX, 
			final int newY, final int newZ, 
			final ArrayExtracellularMatrix newExtracellularMatrix) {
		
		super(newX, newY, newZ);
		
		this.extracellularMatrix = newExtracellularMatrix;
		
	} // End of ArrayExtracellularMatrixSample()


	// METHODS =================================================================

	
	/**
	 * 
	 * @param productType
	 * @param newConcentration
	 */
	@Override
	public void setConcentration(final CellProductType productType, 
			final double newConcentration) {
		
		super.setConcentration(productType, newConcentration);

		this.extracellularMatrix.updateConcentration(getX(), getY(), getZ(), 
				productType, newConcentration);
		
	} // End setConcentration()
	

} // End of ArrayExtracellularMatrixSample class
