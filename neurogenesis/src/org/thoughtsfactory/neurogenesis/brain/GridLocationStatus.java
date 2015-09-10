package org.thoughtsfactory.neurogenesis.brain;

import repast.simphony.space.grid.GridPoint;


/**
 * 
 * @author bob
 *
 */
public class GridLocationStatus {

	
	//
	private final GridPoint location;
	
	
	//
	private final Cell occupant;
	
	
	//
	private final ExtracellularMatrixSample extracellularMatrixSample;
	
	
	/**
	 * 
	 * @param newLocation
	 * @param newOccupant
	 * @param newExtracellularMatrixSample
	 */
	public GridLocationStatus(final GridPoint newLocation, 
			final Cell newOccupant, 
			final ExtracellularMatrixSample newExtracellularMatrixSample) {
		
		this.location = newLocation;
		this.occupant = newOccupant;
		this.extracellularMatrixSample = newExtracellularMatrixSample;
		
	} // End of LocationQueryReport()
	
	
	/**
	 * 
	 * @return
	 */
	public final GridPoint getLocation() {
		return this.location;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final Cell getOccupant() {
		return this.occupant;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final ExtracellularMatrixSample getExtracellularMatrixSample() {
		return this.extracellularMatrixSample;
	}
	
	
} // End of GridLocationStatus class
