package org.thoughtsfactory.neurogenesis.brain;

import java.util.ArrayList;
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
 * A gene regulated cell implements the overall lifecycle of a cell that is
 * governed by a gene regulatory network.
 * 
 * @author Robert Langlois
 */
public abstract class GeneRegulatedCell extends Cell {

    
    // CONSTANTS ===============================================================
    
    
    /**
     * The lowest food concentration below which no cell is allowed to live.
     */
    protected static final double MORTAL_LOWEST_FOOD_CONCENTRATION = 0.001;
    
    
    /**
     * Value used by all events that are triggered by an upper concentration 
     * limit.
     */
    protected static final double REGULATOR_UNIVERSAL_THRESHOLD = 0.95;
    
    
    /**
     * Initial internal concentration for all cell products and regulators. 
     * Must be > 0.
     */
    protected static final double INITIAL_CONCENTRATION = 0.01;
    
    
    /**
     * Default transfer rate in any direction between the cell and the
     * extracellular matrix.
     */
    private static final double OSMOSIS_RATE = 0.2;
    
    
    /**
     * Transfer rate of cell products between attached cells.
     */
    protected static final double GAP_TRANSFER_RATE = 1.0; // 0.7;
    
    
    // INSTANCE VARIABLES ======================================================
    
    
    // Class for messages.
    private final static Logger logger = 
            Logger.getLogger(GeneRegulatedCell.class);    
        
    
    /**
     * Membrane channels controlling the transit of cell products in and out of
     * the cell, and consequently their concentration inside the cell.
     */
    protected final Map<CellProductType, CellMembraneChannel> membraneChannels = 
            new HashMap<CellProductType, CellMembraneChannel>();
    

    /**
     * Determines if this cell is allowed to attach to any other cell at all 
     * during its life time.
     */
    protected final boolean cellAdhesionEnabled;
    
    
    /**
     * The gene regulatory network that governs this cell.
     */
    protected final RegulatoryNetwork regulatoryNetwork;
    

    /**
     * Concentration of cell growth regulator. Determines events like cell
     * division.
     */
    protected double cellGrowthRegulator = INITIAL_CONCENTRATION;
    
    
    /**
     * Concentration of cell adhesion regulator. Determines if the cell is
     * capable of switching to an attached state or need to revert to a free
     * roaming cell.
     */
    protected double cellAdhesionRegulator = INITIAL_CONCENTRATION;
    
    
    /**
     * Concentration of cell energy regulator. Determines the rate at which
     * food is consumed by the current cell.
     */
    protected double cellEnergyRegulator = INITIAL_CONCENTRATION;
    
    
    /**
     * Concentration of neurotransmitter regulator. Affects the activation level 
     * of cells which are instances of neuron.
     * 
     * TODO: Move this to the Neuron class?
     */
    protected double cellNeurotransmitterRegulator = INITIAL_CONCENTRATION;
    
    
    /**
     * Indicates if the current cell is in an attached state or is a free
     * roaming cell.
     */
    protected boolean attached = false;

    
    /**
     * Indicates if the current cell is alive or just died.
     */
    protected boolean alive = true;
        
    
    /**
     * For attached cell only; indicates the polarity, or orientation, of the
     * cell relative to the brain's grid. This affects how cells attach to one
     * in other to form patterns.
     */
    protected int[] polarity = null;
    
    
    /**
     * How many cellular divisions this cell has gone through so far.
     */
    protected int cellDivisionCount = 0;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Initialises a new gene regulated cell instance.
     *
     * @param newId The label that identifies the new cell.
     * @param newSpace The continuous space from which this cell will be 
     *                 displayed. 
     * @param newGrid The grid that defines all locations in the virtual brain.
     * @param newRegulatoryNetwork The gene regulatory network that governs this 
     *                             cell.
     * @param newCellAdhesionEnabled Indicates if this cell is allowed to attach
     *                               to any other cell at all during its life 
     *                               time.
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
                        INITIAL_CONCENTRATION, OSMOSIS_RATE, false,
                        OSMOSIS_RATE, false));

    } // End of Cell(String, ContinuousSpace, Grid, RegulatoryNetwork, boolean)


    /**
     * Initialises a new cell instance from another existing cell.
     * 
     * @param newId The label that identifies the new cell.
     * @param motherCell The cell to initialise from.
     * @param newCellAdhesionEnabled Indicates if this cell is allowed to attach
     *                               to any other cell at all during its life 
     *                               time.
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
        
    } // End of GeneRegulatedCell(String, GeneRegulatedCell, boolean)
    
    
    // METHODS =================================================================
    
    
    // ACCESSORS ---------------------------------------------------------------
    
    
    /**
     * Returns this cell growth regulator concentration.
     * 
     * @return The cell growth regulator concentration.
     */
    public double getCellDivisionConcentration() {
        return this.cellGrowthRegulator;
    }
    
    
    /**
     * Indicates if this cell is attached or not.
     * 
     * @return {@code true} if the cell is attached, {@code false} otherwise.
     */
    public boolean isAttached() {
        return this.attached;
    }
    
    
    // HELPER METHODS ----------------------------------------------------------
    
    
    /**
     * Find a location in the grid around a given set of coordinates which has
     * the highest concentration of a given product.
     * 
     * @param productType The substance to look for.
     * @param pt The location around which to scan.
     * @param extentX How far should the scan extend on the x-axis.
     * @param extentY How far should the scan extend on the y-axis.
     * @param extentZ How far should the scan extend on the z-axis.
     * @param includeCentre Should the scan include the central location or not.
     * @param vacant Should the location be free of cells or not.
     * @return The status of the location found as a {@link GridLocationStatus}
     *         object.
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
     * Find the set of potential candidates for cellular adhesion around a given
     * location.
     *
     * @param pt The location around which to scan.
     * @param cellAdhesionThreshold The required concentration of cell adhesion 
     *                              regulator. 
     * @return A list of gene regulated cells that are either already attached 
     *         or just reached the specified cell adhesion regulator threshold.
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
     * Calculate the required polarity, or orientation, in the Cartesian grid
     * to align the current cell with a given attached cell.
     * 
     * @param otherCell The other cell to which we would like to attach the 
     *                  current cell.
     * @return The polarity or orientation that would align with the given cell
     *         or {@code null} if alignment is not possible.
     */
    protected int[] calculateMatchingPolarity(
    		final GeneRegulatedCell otherCell) {
        
        GridPoint thisPos = this.grid.getLocation(this);
        GridPoint otherPos = this.grid.getLocation(otherCell);
        
        /* 
         * Remember: source and target must share at least on common Cartesian 
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
     * Returns an extracellular matrix sample from the specified grid location.
     * 
     * @param pt The location to get the sample from.
     * @return A sample of product concentrations at the specified location.
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
        
    } // End of getExtracellularMatrixSample()
    
    
    // CELL LIFECYCLE METHODS --------------------------------------------------
    

    /**
     * Transfer cell products from the extracellular matrix into the cell.
     * Products are absorbed only if their external concentration is higher than
     * the concentration inside the cell.
     */
    protected void absorbProductsFromMatrix() {
        
        // Get the grid location of this Cell
        GridPoint pt = this.grid.getLocation(this);
        
        // Get the external concentration from the extracellular matrix
        // at current position.        
        ExtracellularMatrixSample matrixSample = 
        		getExtracellularMatrixSample(pt);

        // Absorb external products if external concentration is higher.
        
        for (CellProductType substanceType : CellProductType.values()) {
            
            double externalConcentration = 
                    matrixSample.getConcentration(substanceType);
            CellMembraneChannel substanceChannel = 
                    this.membraneChannels.get(substanceType);
            double internalConcentration = substanceChannel.getConcentration(); 
            
            logger.debug("External = " + externalConcentration 
                    + " ==> Internal = " + internalConcentration);
            
            if (substanceChannel.isOpenForInput()
                    && (externalConcentration > internalConcentration)) {
                
            	// TODO: Could be more straightforward.
                double equilibriumConcentration = 
                        (externalConcentration + internalConcentration) / 2;
                
                double diffusingConcentration =
                        (externalConcentration - equilibriumConcentration) 
                        * substanceChannel.getInputRate();
                
                double newExternalConcentration = 
                        externalConcentration - diffusingConcentration;
                double newInternalConcentration = 
                        internalConcentration + diffusingConcentration;
                
                matrixSample.setConcentration(substanceType, 
                        newExternalConcentration);
                substanceChannel.setConcentration(newInternalConcentration);
                logger.debug("New internal concentration: " 
                        + newInternalConcentration);
                
            } // End if()
            
        } // end for()
        
    } // End of absorbProductsFromMatrix()
    
    
    /**
     * Handles gene expression.
     */
    protected void updateRegulatoryNetwork() {
        
    	// Concentrations provided as input to the regulatory network.
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
            
        } // End for(inputElement)

        // Update the regulatory network.
        this.regulatoryNetwork.updateNetwork(inputConcentrations);
                
    } // End of updateRegulatoryNetwork()
    
    
    /**
     * Updates the concentration of all byproducts of the gene regulatory 
     * network.
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
            
        } // End for(outputElement)
        
    } // End of updateCellConcentrations()
    

    /**
     * Calculates the new concentration of a regulator given an output genetic
     * element.
     * 
     * @param outputElement The genetic element that determines the 
     *                      concentration of the regulator.
     * @param currentConcentration The current concentration of the regulator.
     * @return The new regulator concentration.
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
     * Updates the internal concentration of a cell product given an output 
     * genetic element.
     * 
     * @param outputElement The genetic element that determines the 
     *                      concentration of the substance.
     * @param productType The substance for which the concentration should be
     *                    updated.
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
     * Updates the channel input rate for a product given an output genetic 
     * element.
     * 
     * @param outputElement The genetic element that determines the input rate
     *                      for the substance.
     * @param productType The substance for which the input rate should be
     *                    updated.
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
     * Updates the channel output rate for a product given an output genetic 
     * element.
     * 
     * @param outputElement The genetic element that determines the output rate
     *                      for the substance.
     * @param productType The substance for which the output rate should be
     *                    updated.
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
                        
        channel.setOutputRate(newOutputRate);

    } // End of updateMembraneChannelOutputRate()
    
    
    /**
     * Transfer cell products from inside the cell to the extracellular matrix.
     * Products are expelled only if their internal concentration is higher than
     * the concentration outside the cell.
     */
    protected void expelProductsToMatrix() {
        
        // Get the grid location of this Cell
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
     * Handles the event of cell death. Cells can die either from lack of food
     * or from a too high internal concentration of waste.
     * 
     * @return {@code true} if the cell just died, {@code false} otherwise.
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
     * Handles the event of cellular division.
     * 
     * @return {@code true} if the cell just divided, {@code false} otherwise.
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
            
            Cell daughterCell = getClone(getId() + "," 
            		+ ++this.cellDivisionCount);
            
            @SuppressWarnings("unchecked")
            Context<Object> context = ContextUtils.getContext(this);
            context.add(daughterCell);
            
            daughterCell.moveTo(targetLocation);

            return true;
                
        } // End if()
            
        return false;
        
    } // End of cellDivisionHandler()


    /**
     * Handles the event of cellular adhesion.
     * 
     * @return {@code true} if the cell was a free roaming cell that just 
     *         attached to some other cells or if the cell just broke away from
     *         cells it was previously attached to, {@code false} otherwise.
     */
    protected boolean cellAdhesionHandler() {
        
        boolean changedAdhesionState = false;
        
        logger.debug("Cell adhesion regulator concentration: " 
                + this.cellAdhesionRegulator);
        
        // Get the grid location of this cell.
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
     * Attempt to attach a cell to potential partner cells.
     * 
     * @param partnerCells A list of potential partners for adhesion.
     * @return {@code true} if the cell was successfully attached to at least 
     *         one partner cell, {@code false} otherwise.
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
            
                // Randomly pick this cell's polarity.
            
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
        
        // Attached cells can output both SAM and FOOD.
        this.membraneChannels.get(CellProductType.SAM).setOpenForOutput(true);
        this.membraneChannels.get(CellProductType.FOOD).setOpenForOutput(true);

        logger.info("Cell adhesion event.");
        
        return true;
        
    } // End of attachCell()
    
    
    /**
     * Detach a cell from is neighbours.
     */
    protected void detachCell() {
        
        this.attached = false;
        this.polarity = null;
        
        // Close output channels for food and SAM.
        this.membraneChannels.get(CellProductType.SAM).setOpenForOutput(false);
        this.membraneChannels.get(CellProductType.FOOD).setOpenForOutput(false);
        
    } // End of detachCell()
    
    
    /**
     * Attached cells can diffuse substances at a different, potentially higher
     * rate than when exchanging through the extracellular matrix.
     * 
     * @param A list of partner cells.
     */
    protected void diffuseToPartnerCells(
            final List<GeneRegulatedCell> partnerCells) {
        
        assert this.attached : "Cell is not supposed to use gap junctions!";
    
        for (CellMembraneChannel substanceChannel : 
                this.membraneChannels.values()) {
    
            double localConcentration =    substanceChannel.getConcentration();
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
                                (localConcentration + partnerConcentration) / 2;
            
                        double diffusingConcentration =
                                (localConcentration - equilibriumConcentration) 
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

    } // End diffuseToPartnerCells()
    
    
    /**
     * Handles cell mutation events.
     * 
     * TODO: Not used or even tested at the moment.
     * 
     * @return {@code true} if the genome of the cell just mutated,
     *         {@code false} otherwise.
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
     * Handles free roaming cells movement.
     * 
     * @return {@code true} if the cell just moved to a different location,
     *         {@code false} otherwise.
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
     * Helper method for checking the threshold of an event.
     *  
     * @param concentration The threshold concentration.
     * @param useThreshold Specify if the threshold value should be used as an
     *                     upper limit that should be reached ({@code true}) or
     *                     as a probability of the event happening
     *                     ({@code false}).
     * @return {@code true} if the threshold has been triggered, {@code false}
     *                      otherwise.
     */
    protected boolean checkConcentrationTrigger(final double concentration, 
            final boolean useThreshold) {
        
        if (useThreshold) {
            return (concentration > REGULATOR_UNIVERSAL_THRESHOLD);
        } else {
            return (RandomHelper.nextDoubleFromTo(0, 1) <= concentration);
        }
        
    } // End of checkConcentrationTrigger()

        
    // OVERRIDEN METHODS -------------------------------------------------------


    /**
     * Handles another cell's request to move out of the way to a new location.
     * 
     * @param requester The cell that made the request.
     * @param requesterLocation The location of the cell that made the request.
     * @param extentX The extent on the x-axis to which the current cell is
     *                requested or allowed to move. Expected values are 
     *                {@code 0} and {@code 1}.
     * @param extentY The extent on the y-axis to which the current cell is
     *                requested or allowed to move. Expected values are 
     *                {@code 0} and {@code 1}.
     * @param extentZ The extent on the z-axis to which the current cell is
     *                requested or allowed to move. Expected values are 
     *                {@code 0} and {@code 1}.
     * @return {@code true} if the current cell compelled with the request,
     *         {@code false} otherwise.
     */
    @Override // Cell
    protected boolean bumpRequest(final Cell requester, 
            final GridPoint requesterLocation, final int extentX, 
            final int extentY, final int extentZ) {

        if (this.attached && !((requester instanceof GeneRegulatedCell) 
                && ((GeneRegulatedCell) requester).attached)) {
            
            // Sedentary attached cells won't move for puny free roaming cells!
            return false;
                
        } // End if()
            
        return super.bumpRequest(requester, 
                requesterLocation, extentX, extentY, extentZ);        
        
    } // End of bumpRequest()
    
    
    // RUNTIME QUERY METHODS ---------------------------------------------------
    
    
    /**
     * Returns the internal food concentration.
     *  
     * @return The internal food concentration.
     */
    public final double getFoodConcentration() {
        return this.membraneChannels
        		.get(CellProductType.FOOD).getConcentration();
    }
    
    
    /**
     * Returns the internal waste concentration.
     * 
     * @return The internal waste concentration.
     */
    public final double getWasteConcentration() {
        return this.membraneChannels
                .get(CellProductType.WASTE).getConcentration();
    }
    
    
    /**
     * Returns the internal cell adhesion molecules (CAM) concentration.
     * 
     * @return The internal CAM concentration.
     */
    public final double getCamConcentration() {
        return this.cellAdhesionRegulator;
    }

    
    /**
     * Returns the internal mutagen concentration.
     * 
     * @return The internal mutagen concentration.
     */
    public final double getMutagenConcentration() {
        return this.membraneChannels
                .get(CellProductType.MUTAGEN).getConcentration();
    }


    /**
     * Returns the internal neurogen concentration.
     * 
     * @return The internal neurogen concentration.
     */
    public final double getNeurogenConcentration() {
        return this.membraneChannels
                .get(CellProductType.NEUROGEN).getConcentration();
    }
        

} // End of GeneRegulatedCell class
