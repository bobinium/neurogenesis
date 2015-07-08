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
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;


/**
 * 
 * @author bob
 *
 */
public class UndifferentiatedCell extends RegulatedCell {


	//
	private static final double CELL_DIVISION_THRESHOLD = 0.8;
	
	//
	private static final double CELL_DEATH_THRESHOLD = 0.9;

	//
	private static final double CELL_ADHESION_THRESHOLD = 0.5;

	//
	private static final double CELL_DIFFERENTIATION_THRESHOLD = 0.9;
	
	//
	private Map<GeneticElement, Double> internalConcentrations = 
			new HashMap<GeneticElement, Double>();
	
	//
	private RegulatoryNetwork regulatoryNetwork;
	
	//
	private double cellDifferentiationConcentration = 0;
	
	//
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
			final RegulatoryNetwork newRegulatoryNetwork) {
		
		super(newSpace, newGrid);
		
		this.regulatoryNetwork = newRegulatoryNetwork;
		
		this.internalConcentrations.put(ENERGY_REGULATOR, 0.0);
		
	} // End of Cell()
	
	
	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.RANDOM_PRIORITY)
	public void step() {

		// get the grid location of this Cell
		GridPoint pt = this.grid.getLocation(this);
		
		// Get the external concentration from extracellular matrix.
		
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

		// Absorb external products if external concentration is higher.
		
		for (GeneticElement product : externalConcentrations.keySet()) {
			
			double externalConcentration = externalConcentrations.get(product);
			double internalConcentration = 
					(this.internalConcentrations.get(product) == null) 
					? 0 : this.internalConcentrations.get(product);
			
			System.out.println("Internal = " + internalConcentration 
					+ " ; External = " + externalConcentration);
			if (externalConcentration > internalConcentration) {
				double maxDiffusingConcentration =  (externalConcentration - internalConcentration) / 2;
				double newExternalConcentration = externalConcentration - maxDiffusingConcentration;
				double newInternalConcentration = internalConcentration + maxDiffusingConcentration;
				externalConcentrations.put(product, newExternalConcentration);
				this.internalConcentrations.put(product, newInternalConcentration);
				System.out.println("New internal concentration: " 
						+ newInternalConcentration);
			}
			
		} // end for()
		
		// Update the regulatory network.
		double energyCost = 
				this.regulatoryNetwork.updateNetwork(this.internalConcentrations);
		
		// Handles energy spent.
		
		double currentEnergy = this.internalConcentrations.get(ENERGY_REGULATOR);
		double newEnergy = currentEnergy - energyCost;
		this.internalConcentrations.put(ENERGY_REGULATOR, Math.max(0, newEnergy));
		if (newEnergy < 0) {
			this.cellDeathConcentration = 
					Math.min(0.99, this.cellDeathConcentration 
							+ Math.abs(newEnergy));
		}
		
		// Handles cell death.
			
		double deltaConcentration =
				this.regulatoryNetwork.calculateOutputConcentrationDelta(
						CELL_DEATH_REGULATOR, this.cellDeathConcentration);
		this.cellDeathConcentration += deltaConcentration;
		
		System.out.println("Cell death regulator concentration: " 
				+ this.cellDeathConcentration);

		if (this.cellDeathConcentration > CELL_DEATH_THRESHOLD) {
			
			this.alive = false;
			
			@SuppressWarnings("unchecked")
			Context<Object> context = ContextUtils.getContext(this);
			context.remove(this);
			System.out.println("****** Cell death event ******");
			return;

		} // End if()
		
		// Handles cellular division.
			
		if (!this.alive) {
			throw new IllegalStateException("** I'm dead Jim!!");
		}
		
		deltaConcentration =
				this.regulatoryNetwork.calculateOutputConcentrationDelta(
						CELL_DIVISION_REGULATOR, this.cellDivisionConcentration);
		this.cellDivisionConcentration += deltaConcentration;
		
		System.out.println("Cell division regulator concentration: " 
				+ this.cellDivisionConcentration);
		
		if (this.cellDivisionConcentration > CELL_DIVISION_THRESHOLD) {
			
			GridPoint pointWithNoCell = findFreeGridCell(pt);
			
			if (pointWithNoCell != null) {
				
				this.cellDivisionConcentration = 
						this.cellDivisionConcentration / 2;
			
				UndifferentiatedCell daughterCell = 
						(UndifferentiatedCell) clone();
			
				@SuppressWarnings("unchecked")
				Context<Object> context = ContextUtils.getContext(this);
				context.add(daughterCell);
				this.space.moveTo(daughterCell, pointWithNoCell.getX(), 
						pointWithNoCell.getY(), pointWithNoCell.getZ());
				this.grid.moveTo(daughterCell, pointWithNoCell.getX(), 
						pointWithNoCell.getY(), pointWithNoCell.getZ());
				System.out.println("****** Cell division event ******");

				return;
				
			} // End if()
			
		} // End if()
				
		// Handles cell adhesion.
		
		deltaConcentration =
				this.regulatoryNetwork.calculateOutputConcentrationDelta(
						CELL_ADHESION_REGULATOR, this.cellAdhesionConcentration);
		this.cellAdhesionConcentration += deltaConcentration;
		
		System.out.println("Cell adhesion regulator concentration: " 
				+ this.cellAdhesionConcentration);
		
		if (this.attached) {
			
			if (this.cellAdhesionConcentration > CELL_ADHESION_THRESHOLD) {
			} else {
				
				this.attached = false;
				System.out.println("****** Cell breaking away event ******");
				return;
				
			} // End if()
			
		} else {
			
			if (this.cellAdhesionConcentration > CELL_ADHESION_THRESHOLD) {
				
				List<RegulatedCell> partnerCells = 
						findPartnerCells(pt, CELL_ADHESION_THRESHOLD);
				
				if (partnerCells.size() > 0) {
					
					this.attached = true;
					
					for (RegulatedCell partnerCell : partnerCells) {
						
						partnerCell.attached = true;
						
					} // End for() partner cells
					
					System.out.println("****** Cell adhesion event ******");
					return;
					
				} // End if()
							
			} // End if()

		} // End if()
		
		// Handles cellular differentiation.
		
		deltaConcentration =
				this.regulatoryNetwork.calculateOutputConcentrationDelta(
						CELL_DIFFERENTIATION_REGULATOR, 
						this.cellDifferentiationConcentration);
		this.cellDifferentiationConcentration += deltaConcentration;
		
		System.out.println("Cell differentiation regulator concentration: " 
				+ this.cellDifferentiationConcentration);
		
		if (this.cellDifferentiationConcentration > CELL_DIFFERENTIATION_THRESHOLD) {
			
			// should use factory!
			Neuron neuron = new Neuron(this.space, this.grid);
		
			@SuppressWarnings("unchecked")
			Context<Object> context = ContextUtils.getContext(this);
			context.remove(this);
			context.add(neuron);
			this.space.moveTo(neuron, pt.getX(), pt.getY(), pt.getZ());
			this.grid.moveTo(neuron, pt.getX(),	pt.getY(), pt.getZ());
			System.out.println("****** Cell differentiation event ******");

			return;
				
		} // End if()
		
		// Handles movement.
		
		if (!this.attached) {
			
			// use the GridCellNgh class to create GridCells for
			// the surrounding neighbourhood.
			GridCellNgh<ExtracellularMatrix> nghCreator = 
					new GridCellNgh<ExtracellularMatrix>(
							this.grid, pt, ExtracellularMatrix.class, 1, 1, 1);
			List<GridCell<ExtracellularMatrix>> gridCells = 
					nghCreator.getNeighborhood(true);
			SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
			GridPoint pointWithMostElements = null;
			double maxGradient = -1;
			
			for (GridCell<ExtracellularMatrix> gridCell : gridCells) {
				
				if (gridCell.size() > 1) {
					throw new IllegalStateException("Multiple matrix!");
				}
			
				GridPoint gridPoint = gridCell.getPoint();
				
				if (isFreeGridCell(gridPoint)) {
					
					for (ExtracellularMatrix matrix : gridCell.items()) {
					
						Map<GeneticElement, Double> concentrations = 
								matrix.getConcentrations();
					
						double gradient = concentrations.get(ENERGY_REGULATOR);
								//+ cconcentrations.get();
						if (gradient > maxGradient) {
							pointWithMostElements = gridPoint;
							maxGradient = gradient;
						}
					
					} // End for() matrices
				
				} // End if()
				
			} // End for() gridCells

			if (pointWithMostElements != null) {
				this.space.moveTo(this, pointWithMostElements.getX(), 
						pointWithMostElements.getY(), 
						pointWithMostElements.getZ());
				this.grid.moveTo(this, pointWithMostElements.getX(), 
						pointWithMostElements.getY(), 
						pointWithMostElements.getZ());
				System.out.println("****** Cell movement event ******");
			}
			
		} // End if()
		
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

	
	/**
	 * 
	 * @return
	 */
	private GridPoint findFreeGridCell(final GridPoint pt) {
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<UndifferentiatedCell> nghCreator = 
				new GridCellNgh<UndifferentiatedCell>(
						this.grid, pt, UndifferentiatedCell.class, 1, 1, 1);
		List<GridCell<UndifferentiatedCell>> gridCells = 
				nghCreator.getNeighborhood(false);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		GridPoint pointWithNoCell = null;
		for (GridCell<UndifferentiatedCell> gridCell : gridCells) {
			if (gridCell.size() == 0) {
				pointWithNoCell = gridCell.getPoint();
				break;
			}
		}
		
		return pointWithNoCell;
		
	} // End of findFreeGridCell()
	
	
	/**
	 * 
	 * @return
	 */
	public double getSize() {
		return 0.001 + this.cellDivisionConcentration * 0.002;		
	}
	
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Object clone() {
		
		UndifferentiatedCell newCell = 
				new UndifferentiatedCell(this.space, this.grid, 
						this.regulatoryNetwork.clone());
		
		newCell.internalConcentrations = (Map<GeneticElement, Double>) 
				((HashMap<GeneticElement, Double>) 
						this.internalConcentrations).clone();
		newCell.cellDivisionConcentration = this.cellDivisionConcentration;
		newCell.cellDeathConcentration = this.cellDeathConcentration;
		newCell.cellAdhesionConcentration = this.cellAdhesionConcentration;
		
		return newCell;
		
	} // End of clone()
	
} // End of UndifferentiatedCell class
