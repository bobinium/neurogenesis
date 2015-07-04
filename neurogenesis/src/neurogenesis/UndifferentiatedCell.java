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
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<ExtracellularMatrix> nghCreatorMorphogene = new GridCellNgh<ExtracellularMatrix>(this.grid,
				pt,	ExtracellularMatrix.class, 1, 1, 1);
		List<GridCell<ExtracellularMatrix>> gridCellsMorphogenes = nghCreatorMorphogene.getNeighborhood(true);
		SimUtilities.shuffle(gridCellsMorphogenes, RandomHelper.getUniform());
		
		Map<GeneticElement, Double> inputElements = new HashMap<GeneticElement, Double>();

//		for (GridCell<Morphogene> gridCellMorphogene : gridCellsMorphogenes) {
//			for (Morphogene morphogenesList : gridCellMorphogene.items()) {
//				Double morphogeneConcentration = morphogenesList.getConcentrations().get(morphogene);
//				double externalConcentration = (morphogeneConcentration == null) ? 0 : morphogeneConcentration;
//				GridPoint gridPoint = gridCellMorphogene.getPoint();
//				System.out.println("Grid morphogenes: " + gridPoint.getX() + ", " + gridPoint.getY() + ", " + gridPoint.getZ());
//				System.out.println("Initial external concentration: " + externalConcentration);
//				if (internalConcentration > externalConcentration) {
//					double deltaConcentration = internalConcentration - externalConcentration;
//					double diffusionQty = deltaConcentration / 100;
//					newInternalConcentration =- diffusionQty;
//					morphogenesList.getConcentrations().put(morphogene, externalConcentration + diffusionQty);
//					System.out.println("Final external concentration: " + (externalConcentration + diffusionQty));
//				}
//			}
//		}
		
		this.regulatoryNetwork.updateNetwork(inputElements);
		
		Map<GeneticElement, Double> outputElements = this.regulatoryNetwork.getOutputElements();
		
		// Handles diffusion of morphogenes.
		
		for (GeneticElement morphogene : outputElements.keySet()) {
			
			if (morphogene != this.cellDivisionRegulator && morphogene != this.cellDeathRegulator) {
				
				double internalConcentration = outputElements.get(morphogene);
				System.out.println("Initial internal concentration: " + internalConcentration);
				
				// use the GridCellNgh class to create GridCells for
				// the surrounding neighbourhood.
//				GridCellNgh<Morphogene> nghCreatorMorphogene = new GridCellNgh<Morphogene>(this.grid,
//						pt,	Morphogene.class, 1, 1, 1);
//				List<GridCell<Morphogene>> gridCellsMorphogenes = nghCreatorMorphogene.getNeighborhood(true);
//				SimUtilities.shuffle(gridCellsMorphogenes, RandomHelper.getUniform());
				
				double newInternalConcentration = internalConcentration;
				for (GridCell<ExtracellularMatrix> gridCellMorphogene : gridCellsMorphogenes) {
					for (ExtracellularMatrix morphogenesList : gridCellMorphogene.items()) {
						Double morphogeneConcentration = morphogenesList.getConcentrations().get(morphogene);
						double externalConcentration = (morphogeneConcentration == null) ? 0 : morphogeneConcentration;
						GridPoint gridPoint = gridCellMorphogene.getPoint();
						System.out.println("Grid morphogenes: " + gridPoint.getX() + ", " + gridPoint.getY() + ", " + gridPoint.getZ());
						System.out.println("Initial external concentration: " + externalConcentration);
						if (internalConcentration > externalConcentration) {
							double deltaConcentration = internalConcentration - externalConcentration;
							double diffusionQty = deltaConcentration / 100;
							newInternalConcentration =- diffusionQty;
							morphogenesList.getConcentrations().put(morphogene, externalConcentration + diffusionQty);
							System.out.println("Final external concentration: " + (externalConcentration + diffusionQty));
						}
					}
				}
				
				outputElements.put(morphogene, newInternalConcentration);
				System.out.println("Final internal concentration: " + newInternalConcentration);
				
			}
		}

		// Handles cell death.
		if (this.cellDeathRegulator != null) {
			
			double cellDeathConcentration = outputElements.get(this.cellDeathRegulator);
			
			if (cellDeathConcentration > 0.9) {
				
				System.out.println("*** Cell death ***");
				this.alive = false;
				
				outputElements.put(this.cellDeathRegulator, 0.01);
				
				Context<Object> context = ContextUtils.getContext(this);
				context.remove(this);
				return;

			}
		
		}
		
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
