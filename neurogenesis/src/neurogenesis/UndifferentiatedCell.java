package neurogenesis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neurogenesis.brain.CellProduct;
import neurogenesis.brain.CellState;
import neurogenesis.brain.GenomeFactory;
import neurogenesis.brain.RegulatedCell;
import neurogenesis.brain.RegulatoryNetwork;
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
	private boolean alive = true;
		
	//
	private CellState cellState = CellState.ALIVE;
	
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
		
		this.internalConcentrations.put(CellProduct.FOOD, 0.01);
		
	} // End of Cell()
	
	
	/**
	 * 
	 * @return
	 */
	public int getStatusFlagAlive() {
		return (this.cellState == CellState.ALIVE) ? 1 :0;
	}

	
	/**
	 * 
	 * @return
	 */
	public int getStatusFlagDead() {
		return (this.cellState == CellState.DEAD) ? 1 :0;
	}

	
	/**
	 * 
	 * @return
	 */
	public int getStatusFlagDividing() {
		return (this.cellState == CellState.DIVIDING) ? 1 :0;
	}

	
	/**
	 * 
	 * @return
	 */
	public int getStatusFlagDifferentiating() {
		return (this.cellState == CellState.DIFFERENTIATING) ? 1 :0;
	}

	
	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.RANDOM_PRIORITY)
	public void step() {

		this.cellState = CellState.ALIVE;
		
		absorbProductsFromMatrix();
		updateRegulatoryNetwork();
		updateCellConcentrations();
		expelProductsToMatrix();
				
		// get the grid location of this Cell
		GridPoint pt = this.grid.getLocation(this);
		
		// Handles cell death.
			
		double cellDeathRegulator = 
				this.internalConcentrations.get(CellProduct.WASTE);
		
		System.out.println("Cell death regulator concentration: " 
				+ cellDeathRegulator);

		if ((cellDeathRegulator > REGULATOR_UNIVERSAL_THRESHOLD)
				|| (this.internalConcentrations.get(CellProduct.FOOD) <= 0)) {
			
			this.alive = false;
			
			@SuppressWarnings("unchecked")
			Context<Object> context = ContextUtils.getContext(this);
			context.remove(this);
			System.out.println("****** Cell death event ******");
			this.cellState = CellState.DEAD;
			return;

		} // End if()
		
		// Handles cellular division.
			
		if (!this.alive) {
			throw new IllegalStateException("** I'm dead Jim!!");
		}
		
		System.out.println("Cell division regulator concentration: " 
				+ this.cellDivisionRegulator);
		
		if (this.cellDivisionRegulator > REGULATOR_UNIVERSAL_THRESHOLD) {
			
			GridPoint pointWithNoCell = findFreeGridCell(pt);
			
			if (pointWithNoCell != null) {
				
				this.cellDivisionRegulator = 
						this.cellDivisionRegulator / 2;
			
				UndifferentiatedCell daughterCell = 
						(UndifferentiatedCell) clone();
			
				@SuppressWarnings("unchecked")
				Context<Object> context = ContextUtils.getContext(this);
				context.add(daughterCell);
				this.space.moveTo(daughterCell, 
						pointWithNoCell.getX() + 0.5, 
						pointWithNoCell.getY() + 0.5, 
						pointWithNoCell.getZ() + 0.5);
				this.grid.moveTo(daughterCell, pointWithNoCell.getX(), 
						pointWithNoCell.getY(), pointWithNoCell.getZ());
				System.out.println("****** Cell division event ******");
				this.cellState = CellState.DIVIDING;

				return;
				
			} // End if()
			
		} // End if()
				
		// Handles cell adhesion.
		
		System.out.println("Cell adhesion regulator concentration: " 
				+ this.cellAdhesionRegulator);
		
		if (this.attached) {
			
			if (this.cellAdhesionRegulator > REGULATOR_UNIVERSAL_THRESHOLD) {
			} else {
				
				this.attached = false;
				System.out.println("****** Cell breaking away event ******");
				return;
				
			} // End if()
			
		} else {
			
			if (this.cellAdhesionRegulator > REGULATOR_UNIVERSAL_THRESHOLD) {
				
				List<RegulatedCell> partnerCells = 
						findPartnerCells(pt, REGULATOR_UNIVERSAL_THRESHOLD);
				
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
		
		System.out.println("Cell differentiation regulator concentration: " 
				+ this.cellDifferentiationRegulator);
		
		if (this.cellDifferentiationRegulator > REGULATOR_UNIVERSAL_THRESHOLD) {
			
			@SuppressWarnings("unchecked")
			Context<Object> context = ContextUtils.getContext(this);
			context.remove(this);

			// should use factory!
			Neuron neuron = new Neuron(this.space, this.grid);
			context.add(neuron);
			this.space.moveTo(neuron, pt.getX() + 0.5, pt.getY() + 0.5, pt.getZ() + 0.5);
			this.grid.moveTo(neuron, pt.getX(),	pt.getY(), pt.getZ());
			System.out.println("****** Cell differentiation event ******");
			this.cellState = CellState.DIFFERENTIATING;

			return;
				
		} // End if()
		
		// Handles mutations.
		
		double mutagenConcentration = 
				this.internalConcentrations.get(CellProduct.MUTAGEN);
		System.out.println("Cell mutagen concentration: " 
				+ mutagenConcentration);
		
		if (mutagenConcentration > REGULATOR_UNIVERSAL_THRESHOLD) {
			
			GenomeFactory genomeFactory = new GenomeFactory();
			genomeFactory.mutate(this.regulatoryNetwork);
			
			System.out.println("****** Cell mutation event ******");
			this.internalConcentrations.put(CellProduct.MUTAGEN, 
					INITIAL_CONCENTRATION);

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
					
						Map<CellProduct, Double> concentrations = 
								matrix.getConcentrations();
					
						double gradient = concentrations.get(CellProduct.FOOD);
								//+ cconcentrations.get();
						if (gradient > maxGradient) {
							pointWithMostElements = gridPoint;
							maxGradient = gradient;
						}
					
					} // End for() matrices
				
				} // End if()
				
			} // End for() gridCells

			if (pointWithMostElements != null) {
				this.space.moveTo(this, 
						pointWithMostElements.getX() + 0.5, 
						pointWithMostElements.getY() + 0.5, 
						pointWithMostElements.getZ() + 0.5);
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
	 */
	@SuppressWarnings("unchecked")
	public Object clone() {
		
		UndifferentiatedCell newCell = 
				new UndifferentiatedCell(this.space, this.grid, 
						this.regulatoryNetwork.clone());
		
		newCell.generationId = this.generationId;
		newCell.internalConcentrations = (Map<CellProduct, Double>) 
				((HashMap<CellProduct, Double>) 
						this.internalConcentrations).clone();
		newCell.cellDivisionRegulator = this.cellDivisionRegulator;
		newCell.cellAdhesionRegulator = this.cellAdhesionRegulator;
		newCell.cellDifferentiationRegulator = 
				this.cellDifferentiationRegulator;
		newCell.attached = this.attached;
		
		return newCell;
		
	} // End of clone()
	
	
	/**
	 * 
	 * @return
	 */
	public double getFoodConcentration() {
		return this.internalConcentrations.get(CellProduct.FOOD);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getWasteConcentration() {
		return this.internalConcentrations.get(CellProduct.WASTE);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getCamConcentration() {
		return this.cellAdhesionRegulator;
	}

	
	/**
	 * 
	 * @return
	 */
	public double getMutagenConcentration() {
		return this.internalConcentrations.get(CellProduct.MUTAGEN);
	}

	
} // End of UndifferentiatedCell class
