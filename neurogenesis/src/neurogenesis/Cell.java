package neurogenesis;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;


/**
 * 
 * @author bob
 *
 */
public abstract class Cell {

	protected final ContinuousSpace<Object> space;
	
	protected final Grid<Object> grid;
	
	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	protected Cell(final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid) {
		
		this.space = newSpace;
		this.grid = newGrid;
		
	} // End of Cell()


} // End of Cell class
