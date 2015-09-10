package org.thoughtsfactory.neurogenesis.brain;

import org.apache.log4j.Logger;

import repast.simphony.space.grid.GridPoint;


/**
 * 
 * @author bob
 *
 */
public class ExtracellularMatrixSample {

	
	// PROPERTIES ==============================================================
	

	// CONSTANTS ---------------------------------------------------------------
	
	
	// INSTANCE VARIABLES ------------------------------------------------------
	
	
	//
	@SuppressWarnings("unused")
	private final static Logger logger =
			Logger.getLogger(ExtracellularMatrixSample.class);	
		
	
	// Contains the current concentration of each chemical in this grid cell.
	protected final double[] concentrations = 
			new double[CellProductType.values().length];

	
	//
	private final int x;
	
	
	//
	private final int y;
	
	
	//
	private final int z;
	
	
	// CONSTRUCTORS ============================================================
	
	
	/**
	 * 
	 * @param space
	 * @param grid
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
	
	
	/**
	 * 
	 * @return
	 */
	public final int getX() {
		return this.x;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final int getY() {
		return this.y;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final int getZ() {
		return this.z;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getConcentration(final CellProductType productType) {
		return this.concentrations[productType.ordinal()];
	}

	
	/**
	 * 
	 * @param productType
	 * @param newConcentration
	 */
	public void setConcentration(final CellProductType productType, 
			final double newConcentration) {
		this.concentrations[productType.ordinal()] = newConcentration;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public GridPoint getPoint() {
		return new GridPoint(this.x, this.y, this.z);
	}
	
		
	/**
	 * 
	 * @return
	 */
	public final double getFoodConcentration() {
		return getConcentration(CellProductType.FOOD);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final double getWasteConcentration() {
		return getConcentration(CellProductType.WASTE);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final double getSamConcentration() {
		return getConcentration(CellProductType.SAM);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final double getMutagenConcentration() {
		return getConcentration(CellProductType.MUTAGEN);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final double getNeurogenConcentration() {
		return getConcentration(CellProductType.NEUROGEN);
	}
	
			
} // End of ExtracellularMatrixSample class
