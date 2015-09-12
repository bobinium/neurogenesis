package org.thoughtsfactory.neurogenesis.brain;

import java.util.List;

import org.apache.log4j.Logger;

import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;


/**
 * 
 * @author bob
 *
 */
public abstract class Cell {

	
	//
	private final static Logger logger = Logger.getLogger(Cell.class);	
		
	
	//
	private final String id;
	
	
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
	protected Cell(final String newId, 
			final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid) {
	
		this.id = newId;
		this.space = newSpace;
		this.grid = newGrid;
		
	} // End of Cell(ContinuousSpace, Grid)


	/**
	 * 
	 * @param motherCell
	 */
	protected Cell(final String newId, final Cell motherCell) {
	
		this.id = newId;
		this.space = motherCell.space;
		this.grid = motherCell.grid;
		
	} // End of Cell(Cell)
	
	
	/**
	 * 
	 * @return
	 */
	public final String getId() {
		return this.id;
	}
	
	
	/**
	 * 
	 * @param gridPoint
	 * @return
	 */
	protected boolean isFreeGridCell(final GridPoint gridPoint) {
		
		boolean free = true;
		
		for (Object obj : this.grid.getObjectsAt(
				gridPoint.getX(), gridPoint.getY(), gridPoint.getZ())) {
			
			if (obj instanceof Cell) {
				free = false;
				break;
			}
			
		} // End for(obj)
		
		return free;
		
	} // End of isFreeGridCell(GridPoint)


	/**
	 * 
	 * @param gridPoint
	 * @return
	 */
	protected Cell getCellAt(final GridPoint gridPoint) {
		
		Cell cell = null;
		
		for (Object obj : this.grid.getObjectsAt(
				gridPoint.getX(), gridPoint.getY(), gridPoint.getZ())) {
			
			if (obj instanceof Cell) {
				cell = (Cell) obj;
				break;
			}
			
		} // End for(obj)
		
		return cell;
		
	} // End of getCellAt()

	
	/**
	 * 
	 * @return
	 */
	protected GridPoint findFreeGridCell(final GridPoint pt) {
		
		// Use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<Cell> nghCreator = 
				new GridCellNgh<Cell>(this.grid, pt, Cell.class, 1, 1, 1);
		List<GridCell<Cell>> gridCells = nghCreator.getNeighborhood(false);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		GridPoint pointWithNoCell = null;
		for (GridCell<Cell> gridCell : gridCells) {
			if (gridCell.size() == 0) {
				pointWithNoCell = gridCell.getPoint();
				break;
			}
		}
		
		return pointWithNoCell;
		
	} // End of findFreeGridCell()
	
	
	/**
	 * 
	 * @param pt
	 */
	public final void moveTo(final GridPoint pt) {
		
		moveTo(pt.getX(), pt.getY(), pt.getZ());

	} // End of moveTo(GridPoint)
	
	
	/**
	 * 
	 * @param pt
	 */
	public void moveTo(final int x, final int y, final int z) {
		
		this.space.moveTo(this, x + 0.5, y + 0.5, z + 0.5);
		this.grid.moveTo(this, x, y, z);

	} // End of moveTo(int, int, int)

	
	/**
	 * 
	 * @param neighbour
	 * @return
	 */
	protected boolean bumpRequest(final Cell requester, 
			final GridPoint requesterLocation, final int extentX, 
			final int extentY, final int extentZ) {
		
		// Get the grid location of this cell.
		GridPoint pt = this.grid.getLocation(this);
				
		// Use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<Cell> nghCreator = new GridCellNgh<Cell>(this.grid, pt, 
				Cell.class, extentX, extentY, extentZ);
		List<GridCell<Cell>> gridCells = nghCreator.getNeighborhood(false);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		GridCell<Cell> freeGridCell = null;
		
		for (GridCell<Cell> gridCell : gridCells) {
			if (gridCell.size() == 0) {
				freeGridCell = gridCell;
				break;
			}
		}

		// No free cell?
		if (freeGridCell == null) {
		
			// Ask the opposite neighbour...
			
			int[] requesterCoords = requesterLocation.toIntArray(new int[3]);
			int[] currentCoords = pt.toIntArray(new int[3]);
			
			int[] origin = 
					this.grid.getDimensions().originToIntArray(new int[3]);
			
			int[] neighbourCoords = new int[3];

			int numIdenticalCoords = 0;
			for (int i = 0; i < 3; i++) {
				int newCoord = currentCoords[i]	
						+ (currentCoords[i] - requesterCoords[i]);
				if (Math.abs(newCoord) > origin[i]) {
					// Current cell is at one of the border of the grid.
					neighbourCoords[i] = currentCoords[i];
					numIdenticalCoords++;
				} else {
					neighbourCoords[i] = newCoord;
					if (newCoord == currentCoords[i]) {
						numIdenticalCoords++;
					}
				}
			}
			
			if (numIdenticalCoords == 3) {
				
				/* New 'neighbour' location is the same as current location:
				 * we are caught in a corner!
				 */
				
				// Sorry, nowhere to go.
				return false;
			
			} else {
				
				// Forward the request.
				
				Cell neighbour = null;
				
				for (Object obj : this.grid.getObjectsAt(neighbourCoords)) {
					if (obj instanceof Cell) {
						neighbour = (Cell) obj;
						break;
					}
				}
				
				assert neighbour != null : 
						"No cell in a neighbourhood with no free grid cells??";
				
				if (neighbour.bumpRequest(this, pt, 
						extentX, extentY, extentZ)) {
					
					// Neighbour has agreed: move to its now free location.
					
					logger.debug("Cell " + this.id 
							+ " shifting to (" + neighbourCoords[0] 
							+ "," + neighbourCoords[1] + "," 
							+ neighbourCoords[2] + ") as requested by " 
							+ requester.id + " from ("
							+ requesterLocation.getX() + "," 
							+ requesterLocation.getY() + "," 
							+ requesterLocation.getZ() + ").");
					
					moveTo(neighbourCoords[0], neighbourCoords[1], 
							neighbourCoords[2]);
						
					return true;
					
				} else {

					// Neighbour has refused: nowhere to go.
					return false;
					
				} // End if()
				
			} // End if()
			
		} else {
			
			// Agree to the request: move to free grid cell.
			
			GridPoint newLocation = freeGridCell.getPoint();
			
			logger.info("Cell " + this.id + "being bumped to (" 
					+ newLocation.getX() + "," + newLocation.getY() + "," 
					+ newLocation.getZ() + ") as requested by " + requester.id 
					+ " from (" + requesterLocation.getX() + "," 
					+ requesterLocation.getY() + "," + requesterLocation.getZ() 
					+ ").");
			
			moveTo(newLocation); 
				
			return true;
			
		} // End if()
		
	} // End of bumpRequest()
	
	
	/**
	 * 
	 */
	protected abstract Cell getClone(final String newId);
	
	
} // End of Cell class
