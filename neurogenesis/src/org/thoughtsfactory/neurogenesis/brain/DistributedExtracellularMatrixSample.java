package org.thoughtsfactory.neurogenesis.brain;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;



/**
 * 
 * @author bob
 *
 */
public class DistributedExtracellularMatrixSample 
		extends ExtracellularMatrixSample {

	// PROPERTIES ==============================================================
	

	// CONSTANTS ---------------------------------------------------------------
	
	
	/**
	 * The rate at which chemicals diffuses between grid cells.
	 */
	public static final double DIFFUSION_RATE =  0.2;
	
	
	/**
	 * The rate at which chemicals naturally decay.
	 */
	public static final double DECAY_RATE = 0.001;

		
	// INSTANCE VARIABLES ------------------------------------------------------
	
	
	//
	private final static Logger logger = 
			Logger.getLogger(DistributedExtracellularMatrixSample.class);	
		
	
	// The 3D grid to which this object belong.
	private final Grid<Object> grid;


	//
	private ExtracellularMatrixSample[] neighbours = null;
	
	
	// CONSTRUCTORS ============================================================
	
	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	public DistributedExtracellularMatrixSample(
			final int newX, final int newY, final int newZ,
			final Grid<Object> newGrid) {

		super(newX, newY, newZ);
		
		this.grid = newGrid;
		
	} // End of ExtracellularMatrixSample()


	// METHODS =================================================================
	
	
	/**
	 * Push diffusion model.
	 */
	@ScheduledMethod(start = 1, priority = ScheduleParameters.FIRST_PRIORITY)
	public void init() {
		
		// get the grid location of this Cell
		GridPoint pt = this.grid.getLocation(this);
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<ExtracellularMatrixSample> nghCreator = 
				new GridCellNgh<ExtracellularMatrixSample>(this.grid,
						pt,	ExtracellularMatrixSample.class, 1, 1, 1);
		List<GridCell<ExtracellularMatrixSample>> gridCells = 
				nghCreator.getNeighborhood(false);

		List<ExtracellularMatrixSample> samples = 
				new ArrayList<ExtracellularMatrixSample>();
		
		for (GridCell<ExtracellularMatrixSample> gridCell : gridCells) {
			
			assert gridCell.size() == 1 : 
					"One extracellular matrix per grid cell allowed!";
				
			ExtracellularMatrixSample neighbourSample = 
						gridCell.items().iterator().next();
				
			samples.add(neighbourSample);

		} // End for() grid cells
						
		this.neighbours = 
				samples.toArray(new ExtracellularMatrixSample[samples.size()]);
		
	} // End of init()

	
	/**
	 * Push diffusion model.
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.RANDOM_PRIORITY)
	public void update() {
		
		// Apply diffusion and decay to each product in this grid cell.
		for (CellProductType substanceType : CellProductType.values()) {
			
			double previousLocalConcentration =	
					this.concentrations[substanceType.ordinal()];
			logger.debug("Local concentration: " + previousLocalConcentration);
			
			// Applies decay rate.
			double localConcentration = previousLocalConcentration 
					- (previousLocalConcentration * DECAY_RATE);	
	
			for (ExtracellularMatrixSample neighbourSample : this.neighbours) {

				double neighbourConcentration = 
						neighbourSample.getConcentration(substanceType);
					
				double diffusingConcentration =	
						(localConcentration	- neighbourConcentration) / 2 
						* DIFFUSION_RATE;
				
				neighbourSample.setConcentration(substanceType, 
						neighbourConcentration + diffusingConcentration);
				localConcentration -= diffusingConcentration;
				
			} // End for() grid cells
						
			logger.debug("New local concentration: " + localConcentration);
			
			this.concentrations[substanceType.ordinal()] = localConcentration;
			
		} // End for() products
		
	} // End of update()

	
} // End of DistributedExtracellularMatrixSample class
