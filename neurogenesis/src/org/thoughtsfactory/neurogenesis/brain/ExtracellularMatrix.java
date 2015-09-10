package org.thoughtsfactory.neurogenesis.brain;

import java.util.List;

import repast.simphony.space.grid.GridPoint;


/**
 * 
 * @author bob
 *
 */
public interface ExtracellularMatrix {

	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public ExtracellularMatrixSample getSample(int x, int y, int z);

	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param extentX
	 * @param extendY
	 * @param extentZ
	 * @return
	 */
	public List<ExtracellularMatrixSample> getAreaSample(GridPoint pt, 
			int extentX, int extentY, int extentZ, boolean includeCentre);
	
	
} // End of ExtracellularMatrix interface
