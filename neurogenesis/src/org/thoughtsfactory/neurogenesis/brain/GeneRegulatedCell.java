package org.thoughtsfactory.neurogenesis.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.genetics.GeneticElement;
import org.thoughtsfactory.neurogenesis.genetics.RegulatoryNetwork;

import repast.simphony.context.Context;
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
public abstract class GeneRegulatedCell extends Cell {

	
	// PROPERTIES ==============================================================
	
	
	// CONSTANTS ---------------------------------------------------------------
	
	
	/**
	 * Less than 0.01%
	 */
	protected static final double MORTAL_LOWEST_FOOD_CONCENTRATION = 0.001;
	
	
	/**
	 * 
	 */
	protected static final double REGULATOR_UNIVERSAL_THRESHOLD = 0.95;
	
	
	/**
	 * 
	 */
	protected static final double INITIAL_CONCENTRATION = 0.01;
	
	
	/**
	 * 
	 */
	private static final double OSMOSIS_RATE = 0.2;
	
	
	/**
	 * 
	 */
	protected static final double GAP_TRANSFER_RATE = 1.0; // 0.7;
	
	
	// INSTANCE VARIABLES ------------------------------------------------------
	
	
	//
	private final static Logger logger = 
			Logger.getLogger(GeneRegulatedCell.class);	
		
	
	/**
	 * Concentration of permeable products inside the current cell.
	 */
	protected final Map<CellProductType, CellMembraneChannel> membraneChannels = 
			new HashMap<CellProductType, CellMembraneChannel>();
	

	/**
	 * 
	 */
	protected final boolean cellAdhesionEnabled;
	
	
	/**
	 * 
	 */
	protected String generationId;
	
	
	/**
	 * 
	 */
	protected RegulatoryNetwork regulatoryNetwork;
	

	/**
	 * 
	 */
	protected double cellGrowthRegulator = INITIAL_CONCENTRATION;
	
	
	/**
	 * 
	 */
	protected double cellAdhesionRegulator = INITIAL_CONCENTRATION;
	
	
	/**
	 * 
	 */
	protected double cellEnergyRegulator = INITIAL_CONCENTRATION;
	
	
	/**
	 * 
	 */
	protected boolean attached = false;

	
	/**
	 * 
	 */
	protected boolean alive = true;
		

	/**
	 * 
	 */
	protected List<GeneRegulatedCell> invaders = 
			new ArrayList<GeneRegulatedCell>();
	
	
	// CONSTRUCTORS ============================================================
	
	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	protected GeneRegulatedCell(final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid, 
			final RegulatoryNetwork newRegulatoryNetwork,
			final boolean newCellAdhesionEnabled) {
		
		super(newSpace, newGrid);
		
		this.regulatoryNetwork = newRegulatoryNetwork;
		this.cellAdhesionEnabled = newCellAdhesionEnabled;
		
		// Food is by default only taken in, not out.
		this.membraneChannels.put(CellProductType.FOOD,
				new CellMembraneChannel(CellProductType.FOOD, 
						0 /*INITIAL_CONCENTRATION*/, OSMOSIS_RATE, true, 
						OSMOSIS_RATE, false));
		
		this.membraneChannels.put(CellProductType.WASTE, 
				new CellMembraneChannel(CellProductType.WASTE, 
						INITIAL_CONCENTRATION, OSMOSIS_RATE, true,
						OSMOSIS_RATE, true));
		
		this.membraneChannels.put(CellProductType.MUTAGEN, 
				new CellMembraneChannel(CellProductType.MUTAGEN, 
						INITIAL_CONCENTRATION, OSMOSIS_RATE, true, 
						OSMOSIS_RATE, true));
		
		// SAM is released only by attached cells.
		this.membraneChannels.put(CellProductType.SAM, 
				new CellMembraneChannel(CellProductType.SAM, 
						INITIAL_CONCENTRATION, OSMOSIS_RATE, false,
						OSMOSIS_RATE, false));
		
		// Neurogen is released only by neurons.
		this.membraneChannels.put(CellProductType.NEUROGEN, 
				new CellMembraneChannel(CellProductType.NEUROGEN, 
						INITIAL_CONCENTRATION, OSMOSIS_RATE, true,
						OSMOSIS_RATE, false));

	} // End of Cell(ContinuousSpace, Grid, RegulatoryNetwork)


	/**
	 * 
	 * @param motherCell
	 */
	protected GeneRegulatedCell(final GeneRegulatedCell motherCell, 
			final boolean newCellAdhesionEnabled) {
		
		super(motherCell);
		
		this.regulatoryNetwork = motherCell.regulatoryNetwork.clone();
		this.membraneChannels.putAll(motherCell.membraneChannels);

		this.cellGrowthRegulator = motherCell.cellGrowthRegulator;
		this.cellAdhesionRegulator = motherCell.cellAdhesionRegulator;
		this.attached = motherCell.attached;
		this.alive = motherCell.alive;

		this.cellAdhesionEnabled = newCellAdhesionEnabled;
		
	} // End of GeneRegulatedCell(GeneReulatedCell)
	
	
	// METHODS =================================================================
	
	
	/**
	 * 
	 * @return
	 */
	public String getGenerationId() {
		return this.generationId;
	}
	
	
	/**
	 * 
	 * @param newId
	 */
	public void setGenerationId(final String newId) {
		this.generationId = newId;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getCellDivisionConcentration() {
		return this.cellGrowthRegulator;
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
	protected GridPoint findFreeGridCell(final GridPoint pt) {
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<GeneRegulatedCell> nghCreator = 
				new GridCellNgh<GeneRegulatedCell>(
						this.grid, pt, GeneRegulatedCell.class, 1, 1, 1);
		List<GridCell<GeneRegulatedCell>> gridCells = 
				nghCreator.getNeighborhood(false);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		GridPoint pointWithNoCell = null;
		for (GridCell<GeneRegulatedCell> gridCell : gridCells) {
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
	protected GridPoint findHighestSamGridCell(final GridPoint pt) {
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<ExtracellularMatrix> nghCreator = 
				new GridCellNgh<ExtracellularMatrix>(
						this.grid, pt, ExtracellularMatrix.class, 1, 1, 1);
		List<GridCell<ExtracellularMatrix>> gridCells = 
				nghCreator.getNeighborhood(false);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		GridPoint pointWithMostSAM = null;
		double maxSamConcentration = 0;
		for (GridCell<ExtracellularMatrix> gridCell : gridCells) {
			if (gridCell.size() > 0) {
				ExtracellularMatrix matrix = gridCell.items().iterator().next();
				Map<CellProductType, Double> concentrations = 
						matrix.getConcentrations();
				double samConcentration = 
						concentrations.get(CellProductType.SAM);
				if (samConcentration > maxSamConcentration) {
					maxSamConcentration = samConcentration;
					pointWithMostSAM = gridCell.getPoint();
				}
			} else {
				throw new IllegalStateException("No extracellular matrix!");
			}
		}
		
		return pointWithMostSAM;
		
	} // End of findHighestSamGridCell()

	
	/**
	 * 
	 * @return
	 */
	protected List<GeneRegulatedCell> findPartnerCells(final GridPoint pt, 
			final double cellAdhesionThreshold) {
		
		List<GeneRegulatedCell> partnerCells = 
				new ArrayList<GeneRegulatedCell>();
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<GeneRegulatedCell> nghCreator = 
				new GridCellNgh<GeneRegulatedCell>(this.grid, pt, 
						GeneRegulatedCell.class, 1, 1, 1);
		List<GridCell<GeneRegulatedCell>> gridCells = 
				nghCreator.getNeighborhood(false);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		for (GridCell<GeneRegulatedCell> gridCell : gridCells) {
			
//			if (gridCell.size() > 1) {
//				throw new IllegalStateException("Only one cell per grid unit!");
//			}
			
			for (GeneRegulatedCell cell : gridCell.items()) {
				
				// Check if the neighbour has the right concentration of CAM.
				if (cell.cellAdhesionEnabled && 
						(cell.cellAdhesionRegulator > cellAdhesionThreshold)) {
					partnerCells.add(cell);
				}
				
			} // End for() cells
			
		} // End for() gridCells
		
		return partnerCells;
		
	} // End of findPartnerCells()


	/**
	 * 
	 * @return
	 */
	protected Map<CellProductType, Double> getExternalConcentrations(
			final GridPoint pt) {
		
		// Get the external concentration from the extracellular matrix
		// at current position.
		
		Map<CellProductType, Double> externalConcentrations = null;

		// Find extracellular matrix (there should be only one instance).
		for (Object obj : this.grid.getObjectsAt(
				pt.getX(), pt.getY(), pt.getZ())) {			
			if (obj instanceof ExtracellularMatrix) {
				 ExtracellularMatrix matrix = (ExtracellularMatrix) obj;
				 externalConcentrations = matrix.getConcentrations();
				 break;
			}
		}

		if (externalConcentrations == null) {
			throw new IllegalStateException("No input elements!");
		}

		return externalConcentrations;
		
	} // End of getExternalConcentrations()
	
	
	// CELL LIFECYCLE METHODS --------------------------------------------------
	

	/**
	 * 
	 */
	protected void absorbProductsFromMatrix() {
		
		// get the grid location of this Cell
		GridPoint pt = this.grid.getLocation(this);
		
		// Get the external concentration from the extracellular matrix
		// at current position.		
		Map<CellProductType, Double> externalConcentrations = 
				getExternalConcentrations(pt);

		// Absorb external products if external concentration is higher.
		
		for (CellProductType substanceType : externalConcentrations.keySet()) {
			
			double externalConcentration = 
					externalConcentrations.get(substanceType);
			CellMembraneChannel substanceChannel = 
					this.membraneChannels.get(substanceType);
			double internalConcentration = substanceChannel.getConcentration(); 
			
			logger.debug("External = " + externalConcentration 
					+ " ==> Internal = " + internalConcentration);
			
			if (substanceChannel.isOpenForInput()
					&& (externalConcentration > internalConcentration)) {
				
				double equilibriumConcentration = 
						(externalConcentration + internalConcentration) / 2;
				
				double diffusingConcentration =
						(externalConcentration - equilibriumConcentration) 
						* substanceChannel.getInputRate();
				
				double newExternalConcentration = 
						externalConcentration - diffusingConcentration;
				double newInternalConcentration = 
						internalConcentration + diffusingConcentration;
				
				externalConcentrations.put(substanceType, 
						newExternalConcentration);
				substanceChannel.setConcentration(newInternalConcentration);
				logger.debug("New internal concentration: " 
						+ newInternalConcentration);
				
			} // End if()
			
		} // end for()
		
	} // End of absorbProductsFromMatrix()
	
	
	/**
	 * Handles gene expression and associated energy cost.
	 */
	protected void updateRegulatoryNetwork() {
		
		Map<GeneticElement, Double> inputConcentrations = 
				new HashMap<GeneticElement, Double>();
		
		for (GeneticElement inputElement : 
				this.regulatoryNetwork.getInputElements()) {
			
			switch (inputElement.getType()) {
			case SPECIAL_IN_FOOD:
				inputConcentrations.put(inputElement, 
						this.membraneChannels.get(CellProductType.FOOD)
						.getConcentration());
				break;
			case SPECIAL_IN_CAM:
				inputConcentrations.put(inputElement, 
						this.cellAdhesionRegulator);
				break;
			case SPECIAL_IN_MUTAGEN:
				inputConcentrations.put(inputElement, 
						this.membraneChannels
						.get(CellProductType.MUTAGEN).getConcentration());
				break;
			default:
			}
			
		} // End for()

		// Update the regulatory network.
		this.regulatoryNetwork.updateNetwork(inputConcentrations);
				
	} // End of updateRegulatoryNetwork()
	
	
	/**
	 * 
	 */
	protected void updateCellConcentrations() {
		
		for (GeneticElement outputElement : 
				this.regulatoryNetwork.getOutputElements()) {
			
			switch (outputElement.getType()) {
			
			case SPECIAL_OUT_WASTE:
				
				updateMembraneChannelConcentration(outputElement, 
						CellProductType.WASTE);
				break;
				
			case SPECIAL_OUT_CAM:

				this.cellAdhesionRegulator = 
						updateRegulatorConcentration(outputElement, 
						this.cellAdhesionRegulator);
				break;
				
			case SPECIAL_OUT_SAM:
				
				updateMembraneChannelConcentration(outputElement, 
						CellProductType.SAM);
				break;
				
			case SPECIAL_OUT_MUTAGEN:
				
				updateMembraneChannelConcentration(outputElement, 
						CellProductType.MUTAGEN);
				break;
				
			case SPECIAL_OUT_MITOGEN:
				
				this.cellGrowthRegulator = 
						updateRegulatorConcentration(outputElement, 
								this.cellGrowthRegulator);
				break;
				
			case SPECIAL_OUT_NEUROGEN:

				updateMembraneChannelConcentration(outputElement, 
						CellProductType.NEUROGEN);
				break;
				
			case SPECIAL_OUT_ENERGY:

				this.cellEnergyRegulator = 
						updateRegulatorConcentration(outputElement, 
						this.cellEnergyRegulator);

				// Energy spent delta is always negative.
				
				CellMembraneChannel foodChannel = 
						this.membraneChannels.get(CellProductType.FOOD);
				double currentFoodConcentration = 
						foodChannel.getConcentration();
				double deltaFoodConcentration = 
						currentFoodConcentration * this.cellEnergyRegulator;
				logger.debug("Energy delta: " + deltaFoodConcentration);
				double newFoodConcentration = 
						currentFoodConcentration - deltaFoodConcentration;
								
				foodChannel.setConcentration(newFoodConcentration);
				
				break;
				
			case SPECIAL_OUT_FOOD_RATE_IN:
				
				updateMembraneChannelInputRate(outputElement, 
						CellProductType.FOOD);
				break;
				
			case SPECIAL_OUT_WASTE_RATE_IN:
				
				updateMembraneChannelInputRate(outputElement, 
						CellProductType.WASTE);
				break;
				
			case SPECIAL_OUT_WASTE_RATE_OUT:
				
				updateMembraneChannelOutputRate(outputElement, 
						CellProductType.WASTE);
				break;
				
			case SPECIAL_OUT_SAM_RATE_OUT:
				
				updateMembraneChannelOutputRate(outputElement, 
						CellProductType.SAM);
				break;
				
			case SPECIAL_OUT_MUTAGEN_RATE_IN:
				
				updateMembraneChannelInputRate(outputElement, 
						CellProductType.MUTAGEN);
				break;
				
			case SPECIAL_OUT_MUTAGEN_RATE_OUT:
				
				updateMembraneChannelOutputRate(outputElement, 
						CellProductType.MUTAGEN);
				break;
				
			case SPECIAL_OUT_NEUROGEN_RATE_IN:
				
				updateMembraneChannelInputRate(outputElement, 
						CellProductType.NEUROGEN);
				break;
				
			case SPECIAL_OUT_NEUROGEN_RATE_OUT:
				
				updateMembraneChannelOutputRate(outputElement, 
						CellProductType.NEUROGEN);
				break;
				
			default:
				
			} // End switch()
			
		} // End for()
		
	} // End of updateCellConcentrations()
	

	/**
	 * 
	 * @return
	 */
	protected double updateRegulatorConcentration(
			final GeneticElement outputElement, 
			final double currentConcentration) {
		
		double deltaConcentration = this.regulatoryNetwork
				.calculateOutputConcentrationDelta(outputElement, 
						currentConcentration);
		double newConcentration = currentConcentration + deltaConcentration;
		
		assert newConcentration > 0 : 
			"Negative regulator concentration for " + outputElement.getType();
		
		return newConcentration;

	} // End of updateRegulatorConcentration()
	
	
	/**
	 * 
	 * @param outputElement
	 * @param productType
	 */
	protected void updateMembraneChannelConcentration(
			final GeneticElement outputElement, 
			final CellProductType productType) {
		
		CellMembraneChannel channel = this.membraneChannels.get(productType);
		double currentConcentration = channel.getConcentration(); 
		double deltaConcentration = this.regulatoryNetwork
				.calculateOutputConcentrationDelta(outputElement, 
						currentConcentration);
		double newConcentration = currentConcentration + deltaConcentration;
		
		assert newConcentration > 0 : 
			"Negative concentration for " + outputElement.getType();
						
		channel.setConcentration(newConcentration);

	} // End of updateMembraneChannelConcentration()
	
	
	/**
	 * 
	 * @param geneticElement
	 */
	protected void updateMembraneChannelInputRate(
			final GeneticElement outputElement, 
			final CellProductType productType) {
		
		CellMembraneChannel channel = this.membraneChannels.get(productType);
		double currentInputRate = channel.getInputRate();
		double deltaInputRate = this.regulatoryNetwork
				.calculateOutputConcentrationDelta(outputElement, 
						currentInputRate);
		double newInputRate = currentInputRate + deltaInputRate;
		
		assert newInputRate > 0 : 
				"Negative input rate for " + outputElement.getType();
						
		channel.setInputRate(newInputRate);

	} // End of updateMembraneChannelInputRate()
	
	
	/**
	 * 
	 * @param geneticElement
	 */
	protected void updateMembraneChannelOutputRate(
			final GeneticElement outputElement, 
			final CellProductType productType) {
		
		CellMembraneChannel channel = this.membraneChannels.get(productType);
		double currentOutputRate = channel.getOutputRate();
		double deltaOutputRate = this.regulatoryNetwork
				.calculateOutputConcentrationDelta(outputElement, 
						currentOutputRate);
		double newOutputRate = currentOutputRate + deltaOutputRate;
		
		assert newOutputRate > 0 : 
				"Negative output rate for " + outputElement.getType();
						
		channel.setInputRate(newOutputRate);

	} // End of updateMembraneChannelOutputRate()
	
	
	/**
	 * 
	 */
	protected void expelProductsToMatrix() {
		
		// get the grid location of this Cell
		GridPoint pt = this.grid.getLocation(this);
		
		// Get the external concentration from the extracellular matrix
		// at current position.
		Map<CellProductType, Double> externalConcentrations = 
				getExternalConcentrations(pt);

		// Expel internal products if internal concentration is higher.
		
		for (CellMembraneChannel substanceChannel : 
				this.membraneChannels.values()) {
			
			double internalConcentration = substanceChannel.getConcentration();
			double externalConcentration = externalConcentrations
					.get(substanceChannel.getSubstanceType());
			
			logger.debug("Internal = " + internalConcentration 
					+ " ==> External = " + externalConcentration);
			
			if (substanceChannel.isOpenForOutput()
					&& (internalConcentration > externalConcentration)) {
				
				double equilibriumConcentration = 
						(internalConcentration + externalConcentration) / 2;
				
				double diffusingConcentration =
						(internalConcentration - equilibriumConcentration) 
						* substanceChannel.getOutputRate();
				
				double newInternalConcentration = 
						internalConcentration - diffusingConcentration;
				double newExternalConcentration = 
						externalConcentration + diffusingConcentration;
				
				substanceChannel.setConcentration(newInternalConcentration);
				externalConcentrations.put(substanceChannel.getSubstanceType(), 
						newExternalConcentration);
				logger.debug("New internal concentration: " 
						+ newInternalConcentration);
				
			} // End if()
			
		} // end for()

	} // End of expelProductsToMatrix()
	

	/**
	 * Handles the event of cell death. Cells cans die either from lack of food
	 * or from a too high internal concentration of waste.
	 */
	protected boolean cellDeathHandler() {
		
		double wasteConcentration = this.membraneChannels
				.get(CellProductType.WASTE).getConcentration();
		
		logger.debug("Cell death waste concentration: " 
				+ wasteConcentration);

		double foodConcentration = this.membraneChannels
				.get(CellProductType.FOOD).getConcentration();
		
		if ((wasteConcentration > REGULATOR_UNIVERSAL_THRESHOLD)
				|| (foodConcentration < MORTAL_LOWEST_FOOD_CONCENTRATION)) {

			logger.info("Cell death event: food = " + foodConcentration 
					+ ", waste = " + wasteConcentration);

			die();
			
			return true;

		} // End if()
		
		return false;
		
	} // End of cellDeathHandler()
	
	
	/**
	 * Remove a cell from the context along with all with all the dependent
	 * objects that it owns. 
	 */
	protected void die() {
		
		this.alive = false;
		
		@SuppressWarnings("unchecked")
		Context<Object> context = ContextUtils.getContext(this);
		
		context.remove(this);

	} // End of die()
	
	
	/**
	 * 
	 */
	protected boolean cellDivisionHandler() {
		
		logger.debug("Cell growth regulator concentration: " 
				+ this.cellGrowthRegulator);
		
		if (checkConcentrationTrigger(this.cellGrowthRegulator, true)) {
			
			// get the grid location of this Cell
			GridPoint pt = this.grid.getLocation(this);
			
			if (this.attached) {
				
				GeneRegulatedCell neighbourToKick = null;
				GridPoint pointWithMostSAM = findHighestSamGridCell(pt);
				for (Object obj : this.grid.getObjectsAt(
						pointWithMostSAM.getX(), pointWithMostSAM.getY(), 
						pointWithMostSAM.getZ())) {
					if (obj instanceof UndifferentiatedCell) {
						neighbourToKick = (GeneRegulatedCell) obj;
						break;
					}
				}

				if (neighbourToKick != null) {
					neighbourToKick.invaders.add(this);
				}
					
				logger.info("Cell division event (attached):"
						+ " growth regulator = " 
						+ this.cellGrowthRegulator);

				this.cellGrowthRegulator = this.cellGrowthRegulator / 2;
		
				Cell daughterCell = clone();
			
				@SuppressWarnings("unchecked")
				Context<Object> context = ContextUtils.getContext(this);
				context.add(daughterCell);
				this.space.moveTo(daughterCell, 
						pointWithMostSAM.getX() + 0.5, 
						pointWithMostSAM.getY() + 0.5, 
						pointWithMostSAM.getZ() + 0.5);
				this.grid.moveTo(daughterCell, pointWithMostSAM.getX(), 
						pointWithMostSAM.getY(), pointWithMostSAM.getZ());

				return true;
								
			} else {
				
				GridPoint pointWithNoCell = findFreeGridCell(pt);
			
				if (pointWithNoCell != null) {
				
					logger.info("Cell division event: growth regulator = " 
							+ this.cellGrowthRegulator);

					this.cellGrowthRegulator = this.cellGrowthRegulator / 2;
			
					Cell daughterCell = clone();
			
					@SuppressWarnings("unchecked")
					Context<Object> context = ContextUtils.getContext(this);
					context.add(daughterCell);
					this.space.moveTo(daughterCell, 
							pointWithNoCell.getX() + 0.5, 
							pointWithNoCell.getY() + 0.5, 
							pointWithNoCell.getZ() + 0.5);
					this.grid.moveTo(daughterCell, pointWithNoCell.getX(), 
							pointWithNoCell.getY(), pointWithNoCell.getZ());

					return true;
				
				} // End if()
				
			} // End if()
			
		} // End if()
				
		return false;
		
	} // End of cellDivisionHandler()


	/**
	 * 
	 * @return
	 */
	protected boolean cellInvasionHandler() {
		
		if (!this.invaders.isEmpty()) {

			GridPoint pt = this.grid.getLocation(this);
			
			GridPoint pointWithMostSAM = findHighestSamGridCell(pt);
			
			if (pointWithMostSAM != null) {
				
				this.invaders.clear();
				
				GeneRegulatedCell neighbourToKick = null;
				for (Object obj : this.grid.getObjectsAt(
						pointWithMostSAM.getX(), pointWithMostSAM.getY(), 
						pointWithMostSAM.getZ())) {
					if (obj instanceof UndifferentiatedCell) {
						neighbourToKick = (GeneRegulatedCell) obj;
						break;
					}
				}

				if (neighbourToKick != null) {
					neighbourToKick.invaders.add(this);
				}
					
				this.space.moveTo(this, 
						pointWithMostSAM.getX() + 0.5, 
						pointWithMostSAM.getY() + 0.5, 
						pointWithMostSAM.getZ() + 0.5);
				this.grid.moveTo(this, pointWithMostSAM.getX(), 
						pointWithMostSAM.getY(), 
						pointWithMostSAM.getZ());
				
				logger.info("Cell kicking event.");
				return true;
				
			} // End if()
			
		} // End if()
		
		return false;
		
	} // End of cellInvasionHandler()
	
	
	/**
	 * 
	 * @return
	 */
	protected boolean cellAdhesionHandler() {
		
		boolean changedAdhesionState = false;
		
		logger.debug("Cell adhesion regulator concentration: " 
				+ this.cellAdhesionRegulator);
		
		// get the grid location of this Cell
		GridPoint pt = this.grid.getLocation(this);
				
		if (this.attached) {
			
			if (this.cellAdhesionRegulator > REGULATOR_UNIVERSAL_THRESHOLD) {
				
				List<GeneRegulatedCell> partnerCells = 
						findPartnerCells(pt, REGULATOR_UNIVERSAL_THRESHOLD);
				
				if (partnerCells.size() == 0) {
					
					this.attached = false;
					this.membraneChannels
							.get(CellProductType.SAM).setOpenForOutput(false);
					changedAdhesionState = true;
					logger.info("Cell breaking away event: no partners, CAM = " 
							+ this.cellAdhesionRegulator);
					
				} else {
					
					// Apply diffusion and decay to each product
					// in this grid cell.
					for (CellMembraneChannel substanceChannel : 
							this.membraneChannels.values()) {
						
						double localConcentration = 
								substanceChannel.getConcentration();	
						
						double newLocalConcentration = localConcentration;
						
						for (GeneRegulatedCell partnerCell : partnerCells) {
						
							CellMembraneChannel partnerChannel = partnerCell
									.membraneChannels
									.get(substanceChannel.getSubstanceType());
							double partnerConcentration = 
									partnerChannel.getConcentration(); 
								
							if (localConcentration > partnerConcentration) {
								
								double equilibriumConcentration =
										(localConcentration 
												+ partnerConcentration) / 2;
								
								double diffusingConcentration =
										(localConcentration 
												- equilibriumConcentration) 
										* GAP_TRANSFER_RATE;
								
								// Proceed only if we have the concentration to spare.
								if (diffusingConcentration < newLocalConcentration) {
									
									newLocalConcentration -= diffusingConcentration;
									double newPartnerConcentration =
											partnerConcentration + diffusingConcentration;
									
									partnerChannel.setConcentration(
											newPartnerConcentration);
									
								} // End if()
								
							} // End if()
								
						} // End for() grid cells
									
						substanceChannel
								.setConcentration(newLocalConcentration);
						
					} // End for() products

					Map<CellProductType, Double> externalConcentrations = getExternalConcentrations(pt);
					double samConcentration = externalConcentrations.get(CellProductType.SAM);
					for (Object obj : this.grid.getObjectsAt(pt.getX(), pt.getY(), pt.getZ())) {
						if (obj instanceof Destroyable) {
							if (RandomHelper.nextDoubleFromTo(0, 1) <= samConcentration) {
								Destroyable destroyableObject = (Destroyable) obj;
								//destroyableObject.destroy();
							}
						}
					}
					
				} // End if()
				
			} else {
				
				this.attached = false;
				this.membraneChannels
						.get(CellProductType.SAM).setOpenForOutput(false);
				changedAdhesionState = true;
				logger.info("Cell breaking away event: CAM = " 
						+ this.cellAdhesionRegulator);
				
			} // End if()
			
		} else {
			
			if (this.cellAdhesionRegulator > REGULATOR_UNIVERSAL_THRESHOLD) {
				
				List<GeneRegulatedCell> partnerCells = 
						findPartnerCells(pt, REGULATOR_UNIVERSAL_THRESHOLD);
				
				if (partnerCells.size() > 0) {
					
					this.attached = true;
					this.membraneChannels
							.get(CellProductType.SAM).setOpenForOutput(true);
					changedAdhesionState = true;
					
					for (GeneRegulatedCell partnerCell : partnerCells) {
						
						partnerCell.attached = true;
						partnerCell.membraneChannels
								.get(CellProductType.SAM)
								.setOpenForOutput(true);
						
					} // End for() partner cells
										
					logger.info("Cell adhesion event: CAM = " 
							+ this.cellAdhesionRegulator);
					
				} // End if()
							
			} // End if()

		} // End if()
		
		return changedAdhesionState;
		
	} // End of cellAdhesionHandler()
	
	
	/**
	 * 
	 * @return
	 */
	protected boolean cellMutationHandler() {
		
		CellMembraneChannel cellMutagen = this.membraneChannels
				.get(CellProductType.MUTAGEN);
		double mutagenConcentration = cellMutagen.getConcentration();
		System.out.println("Cell mutagen concentration: " 
				+ mutagenConcentration);
		
		if (checkConcentrationTrigger(mutagenConcentration, false)) {
			
			this.regulatoryNetwork.mutate();
			
			logger.info("Cell mutation event: mutagen = " 
					+ mutagenConcentration);
			//cellMutagen.setConcentration(INITIAL_CONCENTRATION);
			return true;

		} // End if()
		
		return false;
		
	} // End of cellMutationHandler()
	
	
	/**
	 * 
	 */
	protected boolean cellMovementHandler() {
		
		if (!this.attached) {
			
			// get the grid location of this Cell
			GridPoint pt = this.grid.getLocation(this);
					
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
					
						Map<CellProductType, Double> concentrations = 
								matrix.getConcentrations();
					
						double gradient = 
								concentrations.get(CellProductType.FOOD);
								//+ concentrations.get(CellProductType.SAM);
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
				logger.info("Cell movement event.");
				return true;
			}
			
		} // End if()
		
		return false;
		
	} // End of cellMovementHandler()
	
	
	/**
	 * 
	 * @return
	 */
	public double getFoodConcentration() {
		return this.membraneChannels
				.get(CellProductType.FOOD).getConcentration();
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getWasteConcentration() {
		return this.membraneChannels
				.get(CellProductType.WASTE).getConcentration();
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
		return this.membraneChannels
				.get(CellProductType.MUTAGEN).getConcentration();
	}


	/**
	 * 
	 * @return
	 */
	public double getNeurogenConcentration() {
		return this.membraneChannels
				.get(CellProductType.NEUROGEN).getConcentration();
	}
	
	
	/**
	 * 
	 * @param concentration
	 * @param useThreshold
	 * @return
	 */
	protected boolean checkConcentrationTrigger(final double concentration, 
			final boolean useThreshold) {
		
		if (useThreshold) {
			return (concentration > REGULATOR_UNIVERSAL_THRESHOLD);
		} else {
			return (RandomHelper.nextDoubleFromTo(0, 1) <= concentration);
		}
		
	} // End of checkConcentrationTrigger()
	
	
} // End of GeneRegulatedCell class
