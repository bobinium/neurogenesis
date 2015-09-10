package org.thoughtsfactory.neurogenesis.brain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;


/**
 * 
 * @author bob
 *
 */
public class DistributedExtracellularMatrix implements ExtracellularMatrix {

	
	// PROPERTIES ==============================================================
	

	// INSTANCE VARIABLES ------------------------------------------------------
	
	
	//
	@SuppressWarnings("unused")
	private final static Logger logger = 
			Logger.getLogger(DistributedExtracellularMatrix.class);	
		
	
	// The 3D continuous space to which this object belong.
	private final ContinuousSpace<Object> space;
	
	
	// The 3D grid to which this object belong.
	private final Grid<Object> grid;

		
	// CONSTRUCTORS ============================================================
	
	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	public DistributedExtracellularMatrix(final Context<Object> context,
			final ContinuousSpace<Object> newSpace, final Grid<Object> newGrid, 
			final int gridQuadrantSize, 
			final Map<CellProductType, Double> initialConcentrations) {
	
		this.space = newSpace;
		this.grid = newGrid;
		
		for (int x = -gridQuadrantSize; x <= gridQuadrantSize; x++) {
			
			for (int y = -gridQuadrantSize;	y <= gridQuadrantSize; y++) {
				
				for (int z = -gridQuadrantSize;	z <= gridQuadrantSize; z++) {
					
					ExtracellularMatrixSample matrixSample;
					
					if ((x % 2 == 0) && (y % 2 == 0) && (z % 2 == 0)) {
						
						matrixSample = 
								new DistributedExtracellularMatrixSample(
										x, y, z, this.grid);
					
					} else {
						
						matrixSample = new ExtracellularMatrixSample(x, y, z);
						
					} // End if()
					
					for (CellProductType substanceType : 
							initialConcentrations.keySet()) {
						
						matrixSample.setConcentration(substanceType, 
								initialConcentrations.get(substanceType));
						
					} // End for(substanceType)
					
					context.add(matrixSample);
					
					this.space.moveTo(matrixSample, x + 0.5, y + 0.5, z + 0.5);
					this.grid.moveTo(matrixSample, x, y, z);
					
				} // End of for(z)
				
			} // End of for(y)
			
		} // End of for(x)
			
	} // End of DistributedExtracellularMatrix()


	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public ExtracellularMatrixSample getSample(
			final int x, final int y, final int z) {
	
		ExtracellularMatrixSample sample = null; 
		
		// Get the external concentration from the extracellular matrix
		// at current position.
		
		// Find extracellular matrix (there should be only one instance).
		for (Object obj : this.grid.getObjectsAt(x, y, z)) {			
			if (obj instanceof ExtracellularMatrixSample) {
				 sample = (ExtracellularMatrixSample) obj;
				 break;
			}
		}

		assert sample != null : "Distributed extracellular matrix not found!";
		
		return sample;
		
	} // End of getSample()
	
	
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
	public List<ExtracellularMatrixSample> getAreaSample(final GridPoint pt, 
			final int extentX, final int extentY, final int extentZ, 
			final boolean includeCentre) {

		// Use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<ExtracellularMatrixSample> nghCreator = 
				new GridCellNgh<ExtracellularMatrixSample>(this.grid, pt, 
						ExtracellularMatrixSample.class, 
						extentX, extentY, extentZ);
		List<GridCell<ExtracellularMatrixSample>> gridCells = 
				nghCreator.getNeighborhood(includeCentre);

		List<ExtracellularMatrixSample> samples = 
				new ArrayList<ExtracellularMatrixSample>();
		
		for (GridCell<ExtracellularMatrixSample> gridCell : gridCells) {
			
			assert gridCell.size() == 1 : "No extracellular matrix!";
			
			samples.add(gridCell.items().iterator().next());
			
		} // End for(gridCell)
		
		return samples;

	} // End getAreaSample();
	

} // End of DistributedExtracellularMatrix class
