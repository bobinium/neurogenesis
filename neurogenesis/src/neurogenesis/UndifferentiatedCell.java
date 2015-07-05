package neurogenesis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;


/**
 * 
 * @author bob
 *
 */
public class UndifferentiatedCell extends Cell {

	private Map<GeneticElement, Double> internalConcentrations = new HashMap<GeneticElement, Double>();
	
	
	private RegulatoryNetwork regulatoryNetwork;
	
	// The gene product responsible for cells division.
	private final GeneticElement cellDivisionRegulator;
	
	// The gen product responsible for cells death.
	private final GeneticElement cellDeathRegulator;
	
	
	private boolean alive = true;
	
	
	/**
	 * 
	 * @param newSpace
	 * @param newGrid
	 * @param newRegulatoryNetwork
	 * @param newCellDivisionRegulator
	 * @param newCellDeathRegulator
	 */
	public UndifferentiatedCell(final ContinuousSpace<Object> newSpace,
			final Grid<Object> newGrid,
			final RegulatoryNetwork newRegulatoryNetwork,
			final GeneticElement newCellDivisionRegulator,
			final GeneticElement newCellDeathRegulator) {
		
		super(newSpace, newGrid);
		
		this.regulatoryNetwork = newRegulatoryNetwork;
		
		this.cellDivisionRegulator = newCellDivisionRegulator;
		this.cellDeathRegulator = newCellDeathRegulator;

	} // End of Cell()
	
	
	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.RANDOM_PRIORITY)
	public void step() {

		// get the grid location of this Cell
		GridPoint pt = this.grid.getLocation(this);
		
		Map<GeneticElement, Double> externalConcentrations = null;

		for (Object obj : this.grid.getObjectsAt(pt.getX(), pt.getY(), pt.getZ())) {			
			if (obj instanceof ExtracellularMatrix) {
				 ExtracellularMatrix matrix = (ExtracellularMatrix) obj;
				 externalConcentrations = matrix.getConcentrations();
				 break;
			}
		}

		if (externalConcentrations == null) {
			throw new IllegalStateException("No input elements!");
		}

		for (GeneticElement product : externalConcentrations.keySet()) {
			
			double externalConcentration = externalConcentrations.get(product);
			double internalConcentration = 
					(this.internalConcentrations.get(product) == null) 
					? 0 : this.internalConcentrations.get(product);
			
			if (externalConcentration > internalConcentration) {
				double maxDiffusingConcentration =  (externalConcentration - internalConcentration) / 2;
				double newExternalConcentration = externalConcentration - maxDiffusingConcentration;
				double newInternalConcentration = internalConcentration + maxDiffusingConcentration;
				externalConcentrations.put(product, newExternalConcentration);
				this.internalConcentrations.put(product, newInternalConcentration);
			}
		}
		
		this.regulatoryNetwork.updateNetwork(this.internalConcentrations);
		
		Map<GeneticElement, Double> outputElements = this.regulatoryNetwork.getOutputElements();
		
		// Handles cell death.
//		if (this.cellDeathRegulator != null) {
//			
//			double cellDeathConcentration = outputElements.get(this.cellDeathRegulator);
//			
//			if (cellDeathConcentration > 0.9) {
//				
//				System.out.println("*** Cell death ***");
//				this.alive = false;
//				
//				outputElements.put(this.cellDeathRegulator, 0.01);
//				
//				Context<Object> context = ContextUtils.getContext(this);
//				context.remove(this);
//				return;
//
//			}
//		
//		}
		
		// Handles cellular division.
		if (this.cellDivisionRegulator != null) {
			
			if (!this.alive) {
				throw new IllegalStateException("** I'm dead Jim!!");
			}
			
			double cellDivisionConcentration = 
					outputElements.get(this.cellDivisionRegulator);
			
			if (cellDivisionConcentration > 0.9) {
				
				System.out.println("/// Cell division ///");
				
				outputElements.put(this.cellDivisionRegulator, 0.01);
				
				UndifferentiatedCell daughterCell = new UndifferentiatedCell(this.space, this.grid, 
						this.regulatoryNetwork.clone(), 
						this.cellDivisionRegulator, 
						this.cellDeathRegulator);
				
				// use the GridCellNgh class to create GridCells for
				// the surrounding neighbourhood.
				GridCellNgh<UndifferentiatedCell> nghCreator = new GridCellNgh<UndifferentiatedCell>(this.grid,
						pt,	UndifferentiatedCell.class, 1, 1, 1);
				List<GridCell<UndifferentiatedCell>> gridCells = nghCreator.getNeighborhood(true);
				SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
				
				GridPoint pointWithNoCell = null;
				for (GridCell<UndifferentiatedCell> cell : gridCells) {
					if (cell.size() == 0) {
						Context<Object> context = ContextUtils.getContext(this);
						context.add(daughterCell);
						pointWithNoCell = cell.getPoint();
						this.space.moveTo(daughterCell, pointWithNoCell.getX(), 
								pointWithNoCell.getY(), pointWithNoCell.getZ());
						this.grid.moveTo(daughterCell, pointWithNoCell.getX(), 
								pointWithNoCell.getY(), pointWithNoCell.getZ());
						break;
					}
				}
				
				return;
				
			}
		
		}
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<UndifferentiatedCell> nghCreator = new GridCellNgh<UndifferentiatedCell>(this.grid,
				pt,	UndifferentiatedCell.class, 1, 1, 1);
		List<GridCell<UndifferentiatedCell>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
/*		GridPoint pointWithMostSAM = null;
		for (GridCell<Cell> gridCell : gridCells) {
			if (gridCell.size() == 0) {
				Context<Object> context = ContextUtils.getContext(this);
				context.add(daughterCell);
				pointWithMostSAM = gridCell.getPoint();
				this.space.moveTo(daughterCell, pointWithMostSAM.getX(), 
						pointWithMostSAM.getY(), pointWithMostSAM.getZ());
				this.grid.moveTo(daughterCell, pointWithMostSAM.getX(), 
						pointWithMostSAM.getY(), pointWithMostSAM.getZ());
				break;
			}
		}
		public void moveTowards(GridPoint pt) {
			// only move if we are not already in this grid location
			if (!pt.equals(grid.getLocation(this))) {
				NdPoint myPoint = space.getLocation(this);
				NdPoint otherPoint = new NdPoint(pt.getX(), pt.getY());
				double angle = SpatialMath.calcAngleFor2DMovement(space,
						myPoint, otherPoint);
				space.moveByVector(this, 1, angle, 0);
				myPoint = space.getLocation(this);
				grid.moveTo(this, (int) myPoint.getX(), (int) myPoint.getY());
				moved = true;
			
		}
		} */
				
	}

} // End of Cell class
