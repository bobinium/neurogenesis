package neurogenesis;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;


/**
 * 
 * @author bob
 *
 */
public abstract class Cell {

	/**
	 * 
	 */
	protected final ContinuousSpace<Object> space;
	
	/**
	 * 
	 */
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


	/**
	 * 
	 * @param gridPoint
	 * @return
	 */
	protected boolean isFreeGridCell(GridPoint gridPoint) {
		
		boolean free = true;
		
		for (Object obj : this.grid.getObjectsAt(
				gridPoint.getX(), gridPoint.getY(), gridPoint.getZ())) {
			
			if (obj instanceof Cell) {
				free = false;
				break;
			}
			
		} // End for()
		
		return free;
		
	} // End of isFreeGridCell()

	
} // End of Cell class
