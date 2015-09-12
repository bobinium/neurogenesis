package org.thoughtsfactory.neurogenesis.brain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.SimulationContext;
import org.thoughtsfactory.neurogenesis.SimulationContextHolder;
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
	protected double cellNeurotransmitterRegulator = INITIAL_CONCENTRATION;
	
	
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
	protected int[] polarity = null;
	
	
	/**
	 * 
	 */
	protected int childNumber = 0;
	
	
	// CONSTRUCTORS ============================================================
	
	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	protected GeneRegulatedCell(final String newId, 
			final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid, 
			final RegulatoryNetwork newRegulatoryNetwork,
			final boolean newCellAdhesionEnabled) {
		
		super(newId, newSpace, newGrid);
		
		this.regulatoryNetwork = newRegulatoryNetwork;
		this.cellAdhesionEnabled = newCellAdhesionEnabled;
		
		// Food is by default only taken in, not out.
		this.membraneChannels.put(CellProductType.FOOD,
				new CellMembraneChannel(CellProductType.FOOD, 
						INITIAL_CONCENTRATION, OSMOSIS_RATE, true, 
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
	protected GeneRegulatedCell(final String newId,
			final GeneRegulatedCell motherCell, 
			final boolean newCellAdhesionEnabled) {
		
		super(newId, motherCell);
		
		this.regulatoryNetwork = motherCell.regulatoryNetwork.clone();
		this.membraneChannels.putAll(motherCell.membraneChannels);

		this.cellGrowthRegulator = motherCell.cellGrowthRegulator;
		this.cellAdhesionRegulator = motherCell.cellAdhesionRegulator;
		this.attached = motherCell.attached;
		this.alive = motherCell.alive;

		this.cellAdhesionEnabled = newCellAdhesionEnabled;
		
		this.polarity = motherCell.polarity;
		
	} // End of GeneRegulatedCell(GeneReulatedCell)
	
	
	// METHODS =================================================================
	
	
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
	protected GridLocationStatus findHighestConcentrationGridCell(
			final CellProductType productType, final GridPoint pt, 
			final int extentX, final int extentY, final int extentZ, 
			final boolean includeCentre, final boolean vacant) {
		
		SimulationContext simulationContext = 
				SimulationContextHolder.getInstance();
		
		ExtracellularMatrix matrix = simulationContext.getExtracellularMatrix();
		
		List<ExtracellularMatrixSample> samples = matrix.getAreaSample(
				pt, extentX, extentY, extentZ, includeCentre);		
		SimUtilities.shuffle(samples, RandomHelper.getUniform());
		
		GridPoint bestLocation = null;
		GridPoint freeLocation = null;

		ExtracellularMatrixSample bestLocationSample = null;
		ExtracellularMatrixSample freeLocationSample = null;
		
		double maxConcentration = -1;
		
		for (ExtracellularMatrixSample sample : samples) {
			
			double concentration = sample.getConcentration(productType);
			
			if (concentration > maxConcentration) {
				
				bestLocation = sample.getPoint();
				bestLocationSample = sample;
				maxConcentration = concentration;

				if (vacant && isFreeGridCell(bestLocation)) {
					freeLocation = bestLocation;
					freeLocationSample = sample;
				}
				
			} // End if()
			
		} // End for(sample)
		
		GridLocationStatus gridLocationStatus;
		
		if (vacant && (freeLocation != null)) {

			// Return the free location.
			gridLocationStatus = new GridLocationStatus(freeLocation, 
					null, freeLocationSample);
			
		} else {
			
			// Return the best location.
			gridLocationStatus = new GridLocationStatus(bestLocation, 
					getCellAt(bestLocation), bestLocationSample);
			
		} // End if()
		
		return gridLocationStatus;
		
	} // End of findHighestConcentrationGridCell()


	/**
	 * 
	 * @return
	 */
	protected List<GeneRegulatedCell> findPartnerCells(final GridPoint pt, 
			final double cellAdhesionThreshold) {
		
		List<GeneRegulatedCell> partnerCells = 
				new ArrayList<GeneRegulatedCell>();
		
		// Use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<GeneRegulatedCell> nghCreator = 
				new GridCellNgh<GeneRegulatedCell>(this.grid, pt, 
						GeneRegulatedCell.class, 1, 1, 1);
		List<GridCell<GeneRegulatedCell>> gridCells = 
				nghCreator.getNeighborhood(false);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		for (GridCell<GeneRegulatedCell> gridCell : gridCells) {
			
			if (gridCell.size() > 0) {
				
				assert gridCell.size() == 1 : 
						"Only one cell per grid unit! (current = " 
						+ gridCell.size() + ")";
			
				GeneRegulatedCell cell = gridCell.items().iterator().next();
				
				// Check if the neighbour has the right concentration of CAM.
				if (cell.cellAdhesionEnabled && 
						(cell.cellAdhesionRegulator > cellAdhesionThreshold)) {
					partnerCells.add(cell);
				}
				
			} // End if()
			
		} // End for() gridCells
		
		return partnerCells;
		
	} // End of findPartnerCells()


	/**
	 * 
	 * @param sourcePolarity
	 * @param targetPolarity
	 * @return
	 */
	protected boolean validatePolarity(final GeneRegulatedCell target) {
		
		GridPoint sourcePos = this.grid.getLocation(this);
		GridPoint targetPos = this.grid.getLocation(target);
		
		/* Remember: source and target must share at least on common Cartesian 
		 * plane such as (x,y), (x,z) or (y, z).
		 */
		
		final int X = 0;
		final int Y = 1;
		final int Z = 2;
		
		int[] sourceVector = sourcePos.toIntArray(new int[3]);
		int[] targetVector = targetPos.toIntArray(new int[3]);
		
		int[] diffTargetSource = new int[3];
		int numCommonPlanes = 0;
		int selectedPlane = -1;

		for (int i = 0; i < 3; i++) {
			
			diffTargetSource[i] = targetVector[i] - sourceVector[i];
			
			// Identify common planes.
			if (diffTargetSource[i] == 0) {
				numCommonPlanes++;
				if (selectedPlane > 0) {
					// Already have a common plane: pick one at random.
					if (RandomHelper.nextIntFromTo(0, 1) == 0) {
						// Take the new one instead.
						selectedPlane = i;
					}
				} else {
					selectedPlane = i;
				}
			}
			
		} // End for()
		
		if ((numCommonPlanes == 3) || (numCommonPlanes == 0)) {
			/* Target coordinates equal source coordinates, or no common
			 * Cartesian plane ==> not valid.
			 */
			return false;
		}
		
		// Dot product for each plane.
	
		int[] validSourcePolarity;
		int[] validTargetPolarity;
		
		switch (selectedPlane) {
		case X:
			validSourcePolarity = new int[] { 0, diffTargetSource[Y], 0 };
			validTargetPolarity = new int[] { 0, 0, -diffTargetSource[Z] };			
			break;			
		case Y:
			validSourcePolarity = new int[] { diffTargetSource[X], 0, 0 };
			validTargetPolarity = new int[] { 0, 0, -diffTargetSource[Z]};			
			break;
		case Z:
			validSourcePolarity = new int[] { diffTargetSource[X], 0, 0 };
			validTargetPolarity = new int[] { 0, -diffTargetSource[Y], 0 };
			break;
		default:
			throw new IllegalStateException(
					"Should have picked a Cartesian plane!");
		}
		
		return (Arrays.equals(this.polarity, validSourcePolarity) 
				&& Arrays.equals(target.polarity, validTargetPolarity));
			
	} // End of validatePolarity()
	
	
	/**
	 * 
	 * @param target
	 * @return
	 */
	protected int[] calculateMatchingPolarity(
			final GeneRegulatedCell otherCell) {
		
		GridPoint thisPos = this.grid.getLocation(this);
		GridPoint otherPos = this.grid.getLocation(otherCell);
		
		/* Remember: source and target must share at least on common Cartesian 
		 * plane such as (x,y), (x,z) or (y, z).
		 */
		
		int[] thisPosVector = thisPos.toIntArray(new int[3]);
		int[] otherPosVector = otherPos.toIntArray(new int[3]);
		
		int[] diffPosThisOther = new int[3];
		int[] thisPolarity = null;

		for (int i = 0; i < 3; i++) {	
			diffPosThisOther[i] = thisPosVector[i] - otherPosVector[i];			
		}
		
		final int X = 0;
		final int Y = 0;
		final int Z = 0;
		
		if (diffPosThisOther[X] == 0) {
			
			if (diffPosThisOther[Y] == 0) {
					
				if (diffPosThisOther[Z] == 0) {
					
					// The other cell has the same position as this one:
					// should never happen.
					return null;
					
				} else {

					/* Check if trying to stick to the base face of the other
					 * cell or if the polarity is on the opposite side. If
					 * not just take the current polarity. 
					 */

					if (otherCell.polarity[Z] == 0) {
						thisPolarity = otherCell.polarity.clone();
					} else {
						return null;
					}
					
				} // End if()
				
			} else {
				
				if (diffPosThisOther[Z] == 0) {
					
					if (otherCell.polarity[Y] == 0) {
						thisPolarity = otherCell.polarity.clone();
					} else {
						return null;
					}
					
				} else {
					
					// Aligned only on x.
					if (otherCell.polarity[X] == 0) {
						
						if (diffPosThisOther[Y] == otherCell.polarity[Y]) {
							thisPolarity = 
									new int[] { 0, 0, -diffPosThisOther[Z] };
						} else if (diffPosThisOther[Z] 
								== otherCell.polarity[Z]) {
							thisPolarity = 
									new int[] { 0, -diffPosThisOther[Y], 0 };
						} else {
							// This cell and the polarity of the other cell
							// are not on the same side.
							return null; 
						}
						
					} else {
						
						return null;
						
					} // End if()

				} // End if()
				
			} // End if()
			
		} else {
			
			if (diffPosThisOther[Y] == 0) {
				
				if (diffPosThisOther[Z] == 0) {
					
					if (otherCell.polarity[X] == 0) {
						thisPolarity = otherCell.polarity.clone();
					} else {
						return null;
					}
					
				} else {

					if (otherCell.polarity[Y] == 0) {
						
						if (diffPosThisOther[X] == otherCell.polarity[X]) {
							thisPolarity = 
									new int[] { 0, 0, -diffPosThisOther[Z] };
						} else if (diffPosThisOther[Z] 
								== otherCell.polarity[Z]) {
							thisPolarity = 
									new int[] { -diffPosThisOther[X], 0, 0 };
						} else {
							// This cell and the polarity of the other cell
							// are not on the same side.
							return null; 
						}
						
					} else {
						
						return null;
						
					} // End if()

				} // End if()
				
			} else {
				
				if (diffPosThisOther[Z] == 0) {
					
					if (otherCell.polarity[Z] == 0) {
						
						if (diffPosThisOther[X] == otherCell.polarity[X]) {
							thisPolarity = 
									new int[] { 0, -diffPosThisOther[Y], 0 };
						} else if (diffPosThisOther[Y] 
								== otherCell.polarity[Y]) {
							thisPolarity = 
									new int[] { -diffPosThisOther[X], 0, 0 };
						} else {
							// This cell and the polarity of the other cell
							// are not on the same side.
							return null; 
						}
						
					} else {
						
						return null;
						
					} // End if()
					
				} else {
					
					// No common plane.
					return null;
					
				} // End if()

			} // End if()
			
		} // End if()
			
		return thisPolarity;
		
	} // End of calculateMatchingPolarity()
	
	
	/**
	 * 
	 * @return
	 */
	protected ExtracellularMatrixSample getExtracellularMatrixSample(
			final GridPoint pt) {
		
		// Get the external concentration from the extracellular matrix
		// at current position.

		SimulationContext simulationContext = 
				SimulationContextHolder.getInstance();
		
		ExtracellularMatrix matrix = simulationContext.getExtracellularMatrix();
		
		ExtracellularMatrixSample sample = 
				matrix.getSample(pt.getX(), pt.getY(), pt.getZ());

		return sample;
		
	} // End of getExternalConcentrations()
	
	
	/**
	 * 
	 * @param neighbour
	 * @return
	 */
	@Override
	protected boolean bumpRequest(final Cell requester, 
			final GridPoint requesterLocation, final int extentX, 
			final int extentY, final int extentZ) {

		if (this.attached && !((requester instanceof GeneRegulatedCell) 
				&& ((GeneRegulatedCell) requester).attached)) {
			
			// Sedentary cells won't move for puny free roaming cells!
			return false;
				
		} // End if()
			
		return super.bumpRequest(requester, 
				requesterLocation, extentX, extentY, extentZ);		
		
	} // End of bumpRequest()
	
	
	// CELL LIFECYCLE METHODS --------------------------------------------------
	

	/**
	 * 
	 */
	protected void absorbProductsFromMatrix() {
		
		// get the grid location of this Cell
		GridPoint pt = this.grid.getLocation(this);
		
		// Get the external concentration from the extracellular matrix
		// at current position.		
		ExtracellularMatrixSample extracellularMatrix = getExtracellularMatrixSample(pt);

		// Absorb external products if external concentration is higher.
		
		for (CellProductType substanceType : CellProductType.values()) {
			
			double externalConcentration = 
					extracellularMatrix.getConcentration(substanceType);
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
				
				extracellularMatrix.setConcentration(substanceType, 
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
				
			case SPECIAL_OUT_NEUROTRANS:
				
				this.cellNeurotransmitterRegulator = 
						updateRegulatorConcentration(outputElement, 
								this.cellNeurotransmitterRegulator);
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
		ExtracellularMatrixSample extracellularMatrix = 
				getExtracellularMatrixSample(pt);

		// Expel internal products if internal concentration is higher.
		
		for (CellMembraneChannel substanceChannel : 
				this.membraneChannels.values()) {
			
			double internalConcentration = substanceChannel.getConcentration();
			double externalConcentration = 
					extracellularMatrix.getConcentration(
							substanceChannel.getSubstanceType());
			
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
				extracellularMatrix.setConcentration(
						substanceChannel.getSubstanceType(), 
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
		
		logger.debug("R.I.P. ...");
		
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
			
			GridPoint targetLocation;
			
			if (this.attached) {
				
				int extentX = (this.polarity[0] == 0) ? 1 : 0;
				int extentY = (this.polarity[1] == 0) ? 1 : 0;
				int extentZ = (this.polarity[2] == 0) ? 1 : 0;
				
				GridLocationStatus locationStatus = 
						findHighestConcentrationGridCell(CellProductType.SAM, 
								pt, extentX, extentY, extentZ, false, true);
				
				if (locationStatus.getOccupant() != null) {
					
					logger.debug("No free space: bumping neighbour.");
						
					if (!locationStatus.getOccupant()
							.bumpRequest(this, pt, extentX, extentY, extentZ)) {
							
						logger.debug("Neighbour can't or won't move: "
								+ "choking to death.");
							
						die();
						return true;
							
					} // End if()
						
				} // End if()
				
				targetLocation = locationStatus.getLocation();
				
			} else {
				
				GridLocationStatus locationStatus = 
						findHighestConcentrationGridCell(CellProductType.FOOD, 
								pt, 1, 1, 1, false, true);
				
				if (locationStatus.getOccupant() != null) {
					
					logger.debug("No free space: bumping neighbour.");
						
					if (!locationStatus.getOccupant()
							.bumpRequest(this, pt, 1, 1, 1)) {
							
						logger.debug("Neighbour can't or won't move: "
								+ "choking to death.");
							
						die();
						return true;
							
					} // End if()
						
				} // End if()
				
				targetLocation = locationStatus.getLocation();
				
			} // End if()
				
			logger.info("Cell division event: growth regulator = " 
					+ this.cellGrowthRegulator);

			this.cellGrowthRegulator = this.cellGrowthRegulator / 2;
			
			Cell daughterCell = getClone(getId() + "," + ++this.childNumber);
			
			@SuppressWarnings("unchecked")
			Context<Object> context = ContextUtils.getContext(this);
			context.add(daughterCell);
			
			daughterCell.moveTo(targetLocation);

			return true;
				
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
					
					detachCell();										
					changedAdhesionState = true;
					
					logger.info("Cell breaking away: no partners");
					
				} else {
					
					// Cell still attached.
					
					diffuseToPartnerCells(partnerCells);
					
					ExtracellularMatrixSample extracellularMatrix = getExtracellularMatrixSample(pt);
					double samConcentration = extracellularMatrix.getConcentration(CellProductType.SAM);
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
				
				detachCell();
				changedAdhesionState = true;
				
				logger.info("Cell breaking away: concentration too low."); 
				
			} // End if()
			
		} else {
			
			if (this.cellAdhesionRegulator > REGULATOR_UNIVERSAL_THRESHOLD) {
				
				List<GeneRegulatedCell> partnerCells = 
						findPartnerCells(pt, REGULATOR_UNIVERSAL_THRESHOLD);
				
				if (partnerCells.size() > 0) {			
					changedAdhesionState = attachCell(partnerCells);
				}
							
			} // End if()

		} // End if()
		
		return changedAdhesionState;
		
	} // End of cellAdhesionHandler()
	
	
	/**
	 * 
	 */
	protected boolean attachCell(final List<GeneRegulatedCell> partnerCells) {
		
		assert this.polarity == null : 
				"Detached cells should have no polarity!";
		
		int[] polarity = null;
		int polarisedCellCount = 0;
		
		for (GeneRegulatedCell partnerCell : partnerCells) {
			
			if (partnerCell.polarity != null) {
				polarisedCellCount++;
				polarity = calculateMatchingPolarity(partnerCell);
				if (polarity != null) {	
					break;
				}
			}
			
		} // End for(partnerCell)
		
		if (polarity == null) {
			
			if (polarisedCellCount == 0) {
			
				// Randomly pick one.
			
				polarity = new int[] { 0, 0, 0 };
			
				int plane = RandomHelper.nextIntFromTo(0, 2);
				int value = (RandomHelper.nextIntFromTo(0, 1) == 0) ? -1 : 1;
				polarity[plane] = value;
			
			} else {
				
				// Cannot attach: can't match polarity to surrounding cells.
				return false;
				
			} // End if()
			
		} // End if)_
		
		this.polarity = polarity;
		
		this.attached = true;
		
		this.membraneChannels.get(CellProductType.SAM).setOpenForOutput(true);
		this.membraneChannels.get(CellProductType.FOOD).setOpenForOutput(true);

		logger.info("Cell adhesion event.");
		
		return true;
		
	} // End of attachCell()
	
	
	/**
	 * 
	 */
	protected void detachCell() {
		
		this.attached = false;
		this.polarity = null;
		
		this.membraneChannels.get(CellProductType.SAM).setOpenForOutput(false);
		this.membraneChannels.get(CellProductType.FOOD).setOpenForOutput(false);
		
	} // End of detachCell()
	
	
	/**
	 * 
	 */
	protected void diffuseToPartnerCells(
			final List<GeneRegulatedCell> partnerCells) {
		
		assert this.attached : "Cell is not supposed to use gap junctions!";
	
		for (CellMembraneChannel substanceChannel : 
				this.membraneChannels.values()) {
	
			double localConcentration =	substanceChannel.getConcentration();
			double newLocalConcentration = localConcentration;
	
			for (GeneRegulatedCell partnerCell : partnerCells) {
	
				if (partnerCell.attached) {
					
					CellMembraneChannel partnerChannel = 
							partnerCell.membraneChannels
							.get(substanceChannel.getSubstanceType());
					
					double partnerConcentration = 
							partnerChannel.getConcentration(); 
			
					if (localConcentration > partnerConcentration) {
			
						double equilibriumConcentration =
								(localConcentration	+ partnerConcentration) / 2;
			
						double diffusingConcentration =
								(localConcentration	- equilibriumConcentration) 
								* GAP_TRANSFER_RATE;
			
						newLocalConcentration = 
								localConcentration - diffusingConcentration;
						double newPartnerConcentration =
								partnerConcentration + diffusingConcentration;
				
						partnerChannel.setConcentration(
								newPartnerConcentration);
				
						break;
						
					} // End if()
			
				} // End if()
			
			} // End for(partnerCells)
				
			substanceChannel.setConcentration(newLocalConcentration);
	
		} // End for() products

	} // End diffuseThroughGapJunction()
	
	
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
					
			GridLocationStatus locationStatus =	
					findHighestConcentrationGridCell(CellProductType.FOOD, 
							pt, 1, 1, 1, true, true);

			if (locationStatus.getOccupant() == null) {
				
				moveTo(locationStatus.getLocation());
				logger.debug("Cell movement event.");

				return true;
			
			} // End if()
			
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
