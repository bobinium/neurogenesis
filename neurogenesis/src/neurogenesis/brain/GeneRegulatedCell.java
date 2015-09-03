package neurogenesis.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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
	 * 
	 */
	protected static final double REGULATOR_UNIVERSAL_THRESHOLD = 0.7;
	
	
	/**
	 * 
	 */
	protected static final double INITIAL_CONCENTRATION = 0.01;
	
	
	/**
	 * 
	 */
	protected static final double OSMOSIS_RATE = 0.2;
	
	
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
	public Map<CellProductType, CellMembraneChannel> membraneChannels = 
			new HashMap<CellProductType, CellMembraneChannel>();
	

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
	protected double cellDifferentiationRegulator = INITIAL_CONCENTRATION;
	
	
	/**
	 * 
	 */
	protected boolean attached = false;

	
	/**
	 * 
	 */
	protected boolean alive = true;
		

	// CONSTRUCTORS ============================================================
	
	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	protected GeneRegulatedCell(final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid, 
			final RegulatoryNetwork newRegulatoryNetwork) {
		
		super(newSpace, newGrid);
		
		this.regulatoryNetwork = newRegulatoryNetwork;
		
		// Food is only taken in, not out.
		this.membraneChannels.put(CellProductType.FOOD,
				new CellMembraneChannel(CellProductType.FOOD, 
						INITIAL_CONCENTRATION, OSMOSIS_RATE, true, 
						0, false));
		
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
						INITIAL_CONCENTRATION, 0, false,
						OSMOSIS_RATE, false));
		
	} // End of Cell(ContinuousSpace, Grid, RegulatoryNetwork)


	/**
	 * 
	 * @param motherCell
	 */
	protected GeneRegulatedCell(final GeneRegulatedCell motherCell) {
		
		super(motherCell);
		
		this.regulatoryNetwork = motherCell.regulatoryNetwork.clone();
		this.membraneChannels.putAll(motherCell.membraneChannels);

		this.cellGrowthRegulator = motherCell.cellGrowthRegulator;
		this.cellAdhesionRegulator = motherCell.cellAdhesionRegulator;
		this.cellDifferentiationRegulator = 
				motherCell.cellDifferentiationRegulator;
		this.attached = motherCell.attached;
		this.alive = motherCell.alive;

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
	protected List<GeneRegulatedCell> findPartnerCells(final GridPoint pt, 
			final double cellAdhesionThreshold) {
		
		List<GeneRegulatedCell> partnerCells = new ArrayList<GeneRegulatedCell>();
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<GeneRegulatedCell> nghCreator = 
				new GridCellNgh<GeneRegulatedCell>(this.grid, pt, 
						GeneRegulatedCell.class, 1, 1, 1);
		List<GridCell<GeneRegulatedCell>> gridCells = 
				nghCreator.getNeighborhood(false);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		for (GridCell<GeneRegulatedCell> gridCell : gridCells) {
			
			if (gridCell.size() > 1) {
				//throw new IllegalStateException("Only one cell per grid unit!");
			}
			
			for (GeneRegulatedCell cell : gridCell.items()) {
				
				if (cell.cellAdhesionRegulator > cellAdhesionThreshold) {
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
				
				CellMembraneChannel wasteChannel = 
						this.membraneChannels.get(CellProductType.WASTE);
				double currentWasteConcentration = 
						wasteChannel.getConcentration();
				double deltaWasteConcentration = this.regulatoryNetwork
						.calculateOutputConcentrationDelta(outputElement, 
								currentWasteConcentration);
				double newWasteConcentration = 
						currentWasteConcentration + deltaWasteConcentration;
				
				if (newWasteConcentration < 0) {
					throw new IllegalStateException(
							"Output element WASTE concentration is negative!"
							+ " (old = " + currentWasteConcentration
							+ ", new = " + newWasteConcentration + ")");
				}
								
				wasteChannel.setConcentration(newWasteConcentration);
				break;
				
			case SPECIAL_OUT_CAM:
								
				double deltaCamConcentration = this.regulatoryNetwork
						.calculateOutputConcentrationDelta(outputElement, 
								this.cellAdhesionRegulator);
				double newCamConcentration = 
						this.cellAdhesionRegulator + deltaCamConcentration;
				
				if (newCamConcentration < 0) {
					throw new IllegalStateException(
							"Output element CAM concentration is negative!"
							+ " (old = " + this.cellAdhesionRegulator
							+ ", new = " + newCamConcentration + ")");
				}
												
				this.cellAdhesionRegulator = newCamConcentration;
				break;
				
			case SPECIAL_OUT_SAM:
				
				CellMembraneChannel samChannel = 
						this.membraneChannels.get(CellProductType.SAM);
				double currentSamConcentration = samChannel.getConcentration(); 
				double deltaSamConcentration = this.regulatoryNetwork
						.calculateOutputConcentrationDelta(outputElement, 
								currentSamConcentration);
				double newSamConcentration = 
						currentSamConcentration + deltaSamConcentration;
				
				if (newSamConcentration < 0) {
					throw new IllegalStateException(
							"Output element SAM concentration is negative!"
							+ " (old = " + currentSamConcentration
							+ ", new = " + newSamConcentration + ")");
				}
								
				samChannel.setConcentration(newSamConcentration);
				break;
				
			case SPECIAL_OUT_MUTAGEN:
				
				CellMembraneChannel mutagenChannel = this.membraneChannels
						.get(CellProductType.MUTAGEN);
				double currentMutagenConcentration = 
						mutagenChannel.getConcentration();
				double deltaMutagenConcentration = this.regulatoryNetwork
						.calculateOutputConcentrationDelta(outputElement, 
								currentMutagenConcentration);
				double newMutagenConcentration =
						currentMutagenConcentration + deltaMutagenConcentration;
				
				if (newMutagenConcentration < 0) {
					throw new IllegalStateException(
							"Output element MUTAGEN concentration is negative!"
							+ " (old = " + currentMutagenConcentration
							+ ", new = " + newMutagenConcentration + ")");
				}
												
				mutagenChannel.setConcentration(newMutagenConcentration);
				break;
				
			case SPECIAL_OUT_MITOGEN:
				
				double deltaMitogenConcentration = this.regulatoryNetwork
						.calculateOutputConcentrationDelta(outputElement, 
								this.cellGrowthRegulator);
				double newMitogenConcentration =
						this.cellGrowthRegulator + deltaMitogenConcentration;
				
				if (newMitogenConcentration < 0) {
					throw new IllegalStateException(
							"Output element MITOGEN concentration is negative!"
							+ " (old = " + this.cellGrowthRegulator
							+ ", new = " + newMitogenConcentration + ")");
				}
																
				this.cellGrowthRegulator = newMitogenConcentration;
				break;
				
			case SPECIAL_OUT_NEUROGEN:

				double deltaNeurogenConcentration = this.regulatoryNetwork
						.calculateOutputConcentrationDelta(outputElement, 
								this.cellDifferentiationRegulator);
				double newNeurogenConcentration =
						this.cellDifferentiationRegulator 
						+ deltaNeurogenConcentration;
				
				if (newNeurogenConcentration < 0) {
					throw new IllegalStateException(
							"Output element NEUROGEN concentration is negative!"
							+ " (old = " + this.cellDifferentiationRegulator
							+ ", new = " + newNeurogenConcentration + ")");
				}
				
				this.cellDifferentiationRegulator = newNeurogenConcentration;				
				break;
				
			case SPECIAL_OUT_ENERGY:
				
				// Energy spent delta is always negative.
				
				CellMembraneChannel foodChannel = 
						this.membraneChannels.get(CellProductType.FOOD);
				double currentFoodConcentration = 
						foodChannel.getConcentration();
				double deltaFoodConcentration = this.regulatoryNetwork
						.calculateEnergyConcentrationDelta(outputElement, 
								currentFoodConcentration);
				logger.debug("Energy delta: " + deltaFoodConcentration);
				double newFoodConcentration =
						currentFoodConcentration + deltaFoodConcentration;
								
				foodChannel.setConcentration(Math.max(0, newFoodConcentration));
				break;
				
			default:
				
			} // End switch()
			
		} // End for()
		
	} // End of updateCellConcentrations()
	
	
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
	 * 
	 */
	protected boolean cellDeathHandler() {
		
		double wasteConcentration = this.membraneChannels
				.get(CellProductType.WASTE).getConcentration();
		
		logger.debug("Cell death waste concentration: " 
				+ wasteConcentration);

		double foodConcentration = this.membraneChannels
				.get(CellProductType.FOOD).getConcentration();
		
		if ((wasteConcentration > REGULATOR_UNIVERSAL_THRESHOLD)
				|| (foodConcentration == 0)) {
			
			this.alive = false;
			
			@SuppressWarnings("unchecked")
			Context<Object> context = ContextUtils.getContext(this);
			context.remove(this);
			logger.info("Cell death event: food = " + foodConcentration 
					+ ", waste = " + wasteConcentration);
			return true;

		} // End if()
		
		return false;
		
	} // End of cellDeathHandler()
	
	
	/**
	 * 
	 */
	protected boolean cellDivisionHandler() {
		
		logger.debug("Cell growth regulator concentration: " 
				+ this.cellGrowthRegulator);
		
		if (checkConcentrationTrigger(this.cellGrowthRegulator, true)) {
			
			// get the grid location of this Cell
			GridPoint pt = this.grid.getLocation(this);
			
			GridPoint pointWithNoCell = findFreeGridCell(pt);
			
			if (pointWithNoCell != null) {
				
				logger.info("Cell division event: growth regulator = " 
						+ this.cellGrowthRegulator);

				this.cellGrowthRegulator = 
						this.cellGrowthRegulator / 2;
			
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
				
		return false;
		
	} // End of cellDivisionHandler()


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
