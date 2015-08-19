package neurogenesis;

import java.util.ArrayList;
import java.util.List;

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
public abstract class RegulatedCell extends Cell {

	/**
	 * 
	 */
	public static final double REGULATOR_UNIVERSAL_THRESHOLD = 0.95;
	
	
	/**
	 * 
	 */
	public static final GeneticElement ENERGY_REGULATOR = 
			new GeneticElement(GeneticElement.ELEMENT_TYPE_SPECIAL_IN, 0, 0, 1);
	

	/**
	 * 
	 */
	public static final GeneticElement CELL_DIVISION_REGULATOR =
			new GeneticElement(GeneticElement.ELEMENT_TYPE_SPECIAL_OUT, 0, 0, 1);
	

	/**
	 * 
	 */
	public static final GeneticElement CELL_DEATH_REGULATOR =
			new GeneticElement(GeneticElement.ELEMENT_TYPE_SPECIAL_OUT, 3.9, 3.9, 1);
	
	
	/**
	 * 
	 */
	public static final GeneticElement CELL_ADHESION_REGULATOR =
			new GeneticElement(GeneticElement.ELEMENT_TYPE_SPECIAL_OUT, 0, 0, 1);
	
	
	/**
	 * 
	 */
	public static final GeneticElement CELL_DIFFERENTIATION_REGULATOR =
			new GeneticElement(GeneticElement.ELEMENT_TYPE_SPECIAL_OUT, 0, 0, 1);
	
	
	/**
	 * 
	 */
	protected double cellDivisionConcentration = 0;
	
	/**
	 * 
	 */
	protected double cellDeathConcentration = 0;
	
	/**
	 * 
	 */
	protected double cellAdhesionConcentration = 0;
	
	/**
	 * 
	 */
	protected boolean attached = false;

	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	protected RegulatedCell(final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid) {
		
		super(newSpace, newGrid);
		
	} // End of Cell()


	/**
	 * 
	 * @return
	 */
	public double getCellDivisionConcentration() {
		return this.cellDivisionConcentration;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public boolean isAttached() {
		return this.attached;
	}
	
	
	/**
	 * 
	 * @return
	 */
	protected List<RegulatedCell> findPartnerCells(final GridPoint pt, 
			final double cellAdhesionThreshold) {
		
		List<RegulatedCell> partnerCells = new ArrayList<RegulatedCell>();
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<RegulatedCell> nghCreator = new GridCellNgh<RegulatedCell>(
				this.grid, pt, RegulatedCell.class, 1, 1, 1);
		List<GridCell<RegulatedCell>> gridCells = 
				nghCreator.getNeighborhood(false);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		for (GridCell<RegulatedCell> gridCell : gridCells) {
			
			if (gridCell.size() > 1) {
				//throw new IllegalStateException("Only one cell per grid unit!");
			}
			
			for (RegulatedCell cell : gridCell.items()) {
				
				if (cell.cellAdhesionConcentration > cellAdhesionThreshold) {
					partnerCells.add(cell);
				}
				
			} // End for() cells
			
		} // End for() gridCells
		
		return partnerCells;
		
	} // End of findPartnerCells()


} // End of RegulatedCell class
