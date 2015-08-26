package neurogenesis.brain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
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
public class ExtracellularMatrix {


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

	
	private static final boolean DEBUG = false;
	
	
	// INSTANCE VARIABLES ------------------------------------------------------
	
	
	//
	private final static Logger logger = Logger.getLogger(ExtracellularMatrix.class);	
		
	// The 3D continuous space to which this object belong.
	private final ContinuousSpace<Object> space;
	
	// The 3D grid to which this object belong.
	private final Grid<Object> grid;

	// Contains the current concentration of each chemical in this grid cell.
	private Map<CellProductType, Double> concentrations = 
			new HashMap<CellProductType, Double>();

	
	// CONSTRUCTORS ============================================================
	
	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	public ExtracellularMatrix(final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid) {
		
		this.space = newSpace;
		this.grid = newGrid;
		
		for (CellProductType substanceType : CellProductType.values()) {
			this.concentrations.put(substanceType, 0.0);
		}
		
	} // End of ExtracellularMatrix()


	// METHODS =================================================================
	
	/**
	 * 
	 * @return
	 */
	public final double getFoodConcentration() {
		return this.concentrations.get(CellProductType.FOOD);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final double getWasteConcentration() {
		return this.concentrations.get(CellProductType.WASTE);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final double getSamConcentration() {
		return this.concentrations.get(CellProductType.SAM);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final double getMutagenConcentration() {
		return this.concentrations.get(CellProductType.MUTAGEN);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Map<CellProductType, Double> getConcentrations() {
		return this.concentrations;
	}

	
	/**
	 * Push diffusion model.
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.RANDOM_PRIORITY)
	public void step() {
		
		// get the grid location of this Cell
		GridPoint pt = this.grid.getLocation(this);
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<ExtracellularMatrix> nghCreator = 
				new GridCellNgh<ExtracellularMatrix>(this.grid,
						pt,	ExtracellularMatrix.class, 1, 1, 1);
		List<GridCell<ExtracellularMatrix>> gridCells = 
				nghCreator.getNeighborhood(false);

		// Apply diffusion and decay to each product in this grid cell.
		for (CellProductType substanceType : this.concentrations.keySet()) {
			
			double localConcentration =	this.concentrations.get(substanceType);
			
			// Applies decay rate.
			localConcentration -= localConcentration * DECAY_RATE;
			
			if (DEBUG) {
				System.out.println("Local concentration: " 
						+ localConcentration);
			}
			
			double newLocalConcentration = localConcentration;
			
			for (GridCell<ExtracellularMatrix> gridCell : gridCells) {
			
				if (gridCell.size() != 1) {
					throw new IllegalStateException(
							"One extracellular matrix per grid cell allowed!");
				}
				
				ExtracellularMatrix neighbourMatrix = 
						gridCell.items().iterator().next();
				
				Map<CellProductType, Double> neighbourConcentrations = 
							neighbourMatrix.getConcentrations();
				
				double neighbourConcentration = 
						neighbourConcentrations.get(substanceType);
					
				if (localConcentration > neighbourConcentration) {
					
					double equilibriumConcentration =
							(localConcentration + neighbourConcentration) / 2;
					
					double diffusingConcentration =
							(localConcentration - equilibriumConcentration) 
							* DIFFUSION_RATE;
					
					// Proceed only if we have the concentration to spare.
					if (diffusingConcentration < newLocalConcentration) {
						
						newLocalConcentration -= diffusingConcentration;
						double newNeighbourConcentration =
								neighbourConcentration + diffusingConcentration;
						
						neighbourConcentrations.put(substanceType, 
								newNeighbourConcentration);
						
					} // End if()
					
				} // End if()
					
			} // End for() grid cells
						
			if (DEBUG) {
				System.out.println("New local concentration: " 
						+ newLocalConcentration);
			}
			
			this.concentrations.put(substanceType, newLocalConcentration);
			
		} // End for() products
		
		//System.out.println("Max concentration: " + maxConcentration);
		//System.out.println("New matrix colour: " + this.colour);
		
	} // End of step()
	
	
} // End of ExtracellularMatrix class
