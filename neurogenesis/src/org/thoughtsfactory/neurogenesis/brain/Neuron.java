package org.thoughtsfactory.neurogenesis.brain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.genetics.RegulatoryNetwork;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;


/**
 * A neuron implements the functionalities required to be part of a neural 
 * network as well as mechanisms for growing and establishing connections with
 * other neurons.
 * 
 * @author Robert Langlois
 */
public class Neuron extends GeneRegulatedCell {


    // CONSTANTS ===============================================================
    
    
    /**
     * The maximum number of dendrite roots that can grow out of a neuron. 
     * 25 is the physical limit as there are 26 side cubes to a grid location
     * and one must be allocated to the axon.
     */
    public static int MAX_DENDRITE_ROOTS = 25;
    
    
    /**
     * Maximum number of dendrite leaves to keep as candidates for growing new
     * dendrite offshoots.
     */
    public static int MAX_DENDRITE_LEAVES = 2;
    
    
    /**
     * Rate at which the weight on a connection between two neurons is adjusted.
     */
    public static double LEARNING_RATE = 0.2;
    
    
    // INSTANCE VARIABLES ======================================================
    
    
    // Class logger ofr messages.
    private final static Logger logger = Logger.getLogger(Neuron.class);    
        

    /**
     * The neural network this neuron will participate in. 
     */
    protected final Network<Object> neuralNetwork;
    
    
    /**
     * The network of all neurite instances.
     */
    protected final Network<Object> neuritesNetwork;
    
    
    /**
     * The neurite junction that is currently the axon's tip.
     */
    protected NeuriteJunction axonTip = null;
    
    
    /**
     * The root junction from which all dendrites and the axon departs.
     */
    protected NeuriteJunction neuritesRoot = null;
    
    
    /**
     * The current activation level of the neuron.
     */
    protected double activation;
    
    
    /**
     * A (partial) list of dendrite leaves used to grow new dendrite offshoots.
     */
    protected List<NeuriteJunction> dendriteLeaves = 
            new ArrayList<NeuriteJunction>();
    
    
    /**
     * The pool of dendrites available for recycling.
     */
    protected Queue<NeuriteJunction> freeDendriteLeavesPool = 
            new LinkedList<NeuriteJunction>();
    
    
    /**
     * The total number of dendrites that the current neuron has. 
     */
    protected int totalDendrites = 0;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new instance of a neuron.
     * 
     * @param newId The label that identifies the new cell.
     * @param newSpace The continuous space from which this cell will be 
     *                 displayed. 
     * @param newGrid The grid that defines all locations in the virtual brain.
     * @param newRegulatoryNetwork The gene regulatory network that governs this 
     *                             cell.
     * @param newNeuralNetwork The neural network this neuron will participate 
     *                         in.
     * @param newNeuritesNetwork The network of all neurite instances.
     */
    public Neuron(final String newId,
            final ContinuousSpace<Object> newSpace, 
            final Grid<Object> newGrid,
            final RegulatoryNetwork newRegulatoryNetwork,
            final Network<Object> newNeuralNetwork,
            final Network<Object> newNeuritesNetwork) {
        
        super(newId, newSpace, newGrid, newRegulatoryNetwork, false);
        
        this.neuralNetwork = newNeuralNetwork;
        this.neuritesNetwork = newNeuritesNetwork;
        
    } /* End of Neuron(ContinuousSpace, Grid, 
       			RegulatoryNetwork, Network, Network) */

    
    /**
     * Creates a new neuron instance from another existing gene regulated cell.
     * 
     * @param newId The label that identifies the new cell.
     * @param motherCell The cell to initialise from.
     * @param newNeuralNetwork The neural network this neuron will participate 
     *                         in.
     * @param newNeuritesNetwork The network of all neurite instances.
     */
    public Neuron(final String newId, 
            final GeneRegulatedCell motherCell, 
            final Network<Object> newNeuralNetwork,
            final Network<Object> newNeuritesNetwork) {
        
        super(newId, motherCell, false);
                
        this.attached = false;
        
        this.neuralNetwork = newNeuralNetwork;
        this.neuritesNetwork = newNeuritesNetwork;
        
        CellMembraneChannel samChannel = 
                this.membraneChannels.get(CellProductType.SAM);
        samChannel.setOpenForOutput(true);
        
        CellMembraneChannel neurogenChannel = 
                this.membraneChannels.get(CellProductType.NEUROGEN);
        neurogenChannel.setOpenForInput(false);
        neurogenChannel.setOpenForOutput(true);
        
    } // End of Neuron(String, GeneRegulatedCell, Network, Network)
    
    
    // METHODS =================================================================
    
    
    // ACESSORS ----------------------------------------------------------------
    
    
    /**
     * Returns the current activation level of the neuron.
     * 
     * @return The activation level.
     */
    public final double getActivation() {
        return this.activation;
    };
    

    // CELL LIFE CYCLE METHDOS -------------------------------------------------
    
    
    /**
     * Execute the life cycle of a neuron.
     * 
     * This method is scheduled for execution at every tick of the simulation
     * with the same priority given to most cellular agents.
     */
    @ScheduledMethod(start = 1, interval = 1, 
            priority = ScheduleParameters.RANDOM_PRIORITY)
    public void update() {

        // TODO: Put initialisation code in a scheduled method executed once.
        if (this.neuritesRoot == null) {
            if (!initialiseNeurites(true, true)) {
                die();
                return;
            }
        }
        
        calculateActivation();
        
        absorbProductsFromMatrix();
        updateRegulatoryNetwork();
        updateCellConcentrations();
                
        // Handles cell death.
        if (!cellDeathHandler()) {
            
            // Handles neurites growth.            
            cellAxonGrowthHandler();
            cellDendritesGrowthHandler();
            
            // Handles mutations.
            //cellMutationHandler();
            
            expelProductsToMatrix();

        } // End if()
        
    } // End of update()


    /**
     * Remove a cell from the context along with all with all the dependent
     * objects that it owns. Neurons must destroy all their neurites before
     * dying properly.
     */
    @Override // GeneRegulatedCell
    protected void die() {

        this.alive = false;
        
        logger.debug("Destroying neurites belonging to current neuron.");
            
        // Remove the axon first so as to not 
        // attempt to remove the root twice.

        if (this.axonTip != null) {
            destroyAxon();
        }
            
        if (this.neuritesRoot != null) {
            destroyDendrites();
        }
            
        @SuppressWarnings("unchecked")
        Context<Object> context = ContextUtils.getContext(this);

        // Destroy dendrites still in the pool.
        for (NeuriteJunction junction : this.freeDendriteLeavesPool) {
            context.remove(junction);
        }

        context.remove(this);

    } // End of die()
    

    /**
     * Destroy (i.e. remove from the context) all neurite junctions that make 
     * the axon and adjust relevant dependencies.
     */
    protected void destroyAxon() {
    
        logger.debug("Destroying axon...");
        
        assert this.axonTip != null : "No axon tip!";
        assert this.neuritesRoot != null : "No dendrite root!";
        
        NeuriteJunction currentJunction = this.axonTip;

        @SuppressWarnings("unchecked")
        Context<Object> context = ContextUtils.getContext(this);

        while (currentJunction != this.neuritesRoot) {
            
            assert currentJunction.getType() == NeuriteJunction.Type.AXON :
                    "Not an AXON junction!?";
            
            currentJunction.setActive(false);
            
            for (NeuriteJunction synapse : currentJunction.getSynapses()) {
                
                // Remove the synaptic link.
                synapse.getPredecessors().remove(currentJunction);
                
            } // End for(synapse)
            
            context.remove(currentJunction);

            assert currentJunction.getPredecessors().size() == 1 :
                "Axon junctions must have one and one only predecessor!";
            
            currentJunction = currentJunction.getPredecessors().get(0);
            
        } // End while()
        
        this.axonTip = null;
        
    } // End of destroyAxon()
    

    /**
     * Destroy (i.e. remove from the context) all neurite junctions that
     * form the dendrites network of the current neuron and adjust relevant 
     * dependencies.
     */
    protected void destroyDendrites() {

        logger.debug("Destroying dendrites...");
        
        this.neuritesRoot.setActive(false);
        
        Stack<NeuriteJunction> junctionsToDestroy = 
                new Stack<NeuriteJunction>();
        junctionsToDestroy.push(this.neuritesRoot);
            
        @SuppressWarnings("unchecked")
        Context<Object> context = ContextUtils.getContext(this);

        while (!junctionsToDestroy.isEmpty()) {
        
            NeuriteJunction currentJunction = junctionsToDestroy.pop();
            
            // Has to be a NEURON (root) or DENDRITE. 
            assert (currentJunction.getType() != NeuriteJunction.Type.AXON) :
                    "Current junction belong to an AXON!";
            
            for (NeuriteJunction predecessor : 
                    currentJunction.getPredecessors()) {

                if (predecessor.getNeuron() == this) {
                    
                    assert predecessor.getType() 
                            == NeuriteJunction.Type.DENDRITE :
                            "Predecessor is of the wrong junction type!";
                    
                    predecessor.setActive(false);                        
                    junctionsToDestroy.push(predecessor);
                    
                } else {
                    
                    assert predecessor.getType() == NeuriteJunction.Type.AXON :
                            "This should be an AXON from another neuron!";
                    
                    predecessor.getSynapses().remove(currentJunction);

                } // End if()
            
            } // End for(predeccessor)

            context.remove(currentJunction);
            
        } // End while()
    
    } // End of destroyDendrites()
        
        
    /**
     * Internal procedure that initialises the first level dendrites and/or 
     * the axon tip or a newly born neuron. Assume that the current neuron has 
     * already been deployed in the context.
     * 
     * @param createAxon A boolean specifying if the axon must be created.
     * @param createDendrites A boolean specifying if the first level dendrites
     *                        must be created.
     * @return {@code true<} if the initialisation went successfully,
     *         {@code false} otherwise.
     */
    protected boolean initialiseNeurites(final boolean createAxon, 
            final boolean createDendrites) {
        
        assert this.neuritesRoot == null : "Neurite root already initialised!";

        boolean success = true;
        
        // Creates the root to all neurites.
            
        this.neuritesRoot =    
                new NeuriteJunction(NeuriteJunction.Type.NEURON, this, 0);
            
        @SuppressWarnings("unchecked")
        Context<Object> context = ContextUtils.getContext(this);
        context.add(this.neuritesRoot);

        GridPoint pt = this.grid.getLocation(this);
        this.space.moveTo(this.neuritesRoot, pt.getX() + 0.5, 
                pt.getY() + 0.5, pt.getZ() + 0.5); 
        this.grid.moveTo(this.neuritesRoot, 
                pt.getX(), pt.getY(), pt.getZ());
            
        // Creates the axon.
            
        if (createAxon) {
            
            assert this.axonTip == null : "Axon tip already initialised!";
            
            this.axonTip = extendNeurite(NeuriteJunction.Type.AXON,    
                    this.neuritesRoot, false);
            if (this.axonTip == null) {
                logger.warn("Could not initialise axon!");
                success = false;
            }
            
        } // End if()
            
        // Creates the initial dendrites.
            
        if (createDendrites) {
                
            for (int n = 1;    
                    n <= Math.min(MAX_DENDRITE_ROOTS, MAX_DENDRITE_LEAVES); 
                    n++) {
                
                if ((n == 1) || (RandomHelper.nextDoubleFromTo(0, 1) 
                        <= this.cellGrowthRegulator)) {
                    
                    NeuriteJunction newDendrite = 
                            extendNeurite(NeuriteJunction.Type.DENDRITE, 
                                    this.neuritesRoot, false);
                    
                    if (newDendrite != null) {
                        this.dendriteLeaves.add(newDendrite);
                        this.totalDendrites++;
                    }
                                                            
                } // End if()
                
            } // End for()
            
            if (this.dendriteLeaves.isEmpty()) {
                logger.warn("Dendrites not initialised!");
                success = false;
            }
                
        } // End if()
            
        return success;
        
    } // End of initialiseNeurites()
    
    
    /**
     * Handles the growth of the axon.
     * 
     * @return {@code true} if the axon has grown, {@code false} otherwise.
     */
    protected boolean cellAxonGrowthHandler() {
        
        assert this.axonTip != null : "Uninitialised axon!?";
        
        logger.debug("Cell axon growth regulator concentration: " 
                + this.cellGrowthRegulator);
        
        /* Probability of having the axon grow decreases exponentially
         * with the depth of the axon's tip.
         */
        if (checkConcentrationTrigger(this.cellGrowthRegulator
                 / Math.pow(this.axonTip.getDepth(), 2), false)) {

            NeuriteJunction newJunction = 
                    extendNeurite(NeuriteJunction.Type.AXON, 
                            this.axonTip, false);

            if (newJunction != null) {
                this.axonTip = newJunction;
                return true;                
            } else {
                logger.warn("Could not grow axon!");
            }

        } // End if()
        
        return false;
        
    } // End of cellAxonGrowthHandler()

    
    /**
     * Handles the growth of dendrites.
     *  
     * @return {@code true} if there is any new dendrite, {@code false} 
     *         otherwise.
     */
    protected boolean cellDendritesGrowthHandler() {

        assert this.neuritesRoot != null : "Uninitialised neurites root!?";
        assert !this.dendriteLeaves.isEmpty() : "No dendrite leaves!";
        
        logger.debug("Cell dendrites growth regulator concentration: " 
                + this.cellGrowthRegulator);
        
        /*
         *  Find the best neurite junction to bud the next dendrite(s). The
         *  best junction is one with the least external SAM concentration
         *  (i.e. "away" from neurons and other SAM expelling cells) and
         *  the lowest dendrite depth (i.e. cost less to expand and maintain).
         */
        
        NeuriteJunction nextBud = null;
        double minValue = Double.MAX_VALUE;
    
        for (NeuriteJunction dendriteLeaf : this.dendriteLeaves) {
                            
            logger.debug("Searching: dendrite depth = " 
                    + dendriteLeaf.getDepth());
                
            GridPoint dendriteLocation = this.grid.getLocation(dendriteLeaf);
            assert dendriteLocation != null : 
                "Dendrite location is null! (Neuron is " 
                    + ((dendriteLeaf.getNeuron().alive)    
                            ? "alive" : "dead") + ")";
                
            ExtracellularMatrixSample extracellularMatrix = 
                    getExtracellularMatrixSample(dendriteLocation);
            
            double externalConcentration = 
                    extracellularMatrix.getConcentration(CellProductType.SAM);
                
            double currentValue = externalConcentration 
                    * Math.pow(dendriteLeaf.getDepth(), 2);
            if (currentValue < minValue) {
                nextBud = dendriteLeaf;
                minValue = currentValue;
            }
                
        } // End for(dendriteLeaf)

        /* Probability of having the axon grow decreases exponentially
         * with the depth of the selected dendrite leaf.
         */
            
        if (checkConcentrationTrigger(this.cellGrowthRegulator
                / Math.pow(nextBud.getDepth(), 2), false)) {

            // First branch from bud.

            NeuriteJunction newJunction1 = 
                    extendNeurite(NeuriteJunction.Type.DENDRITE, nextBud, 
                            this.dendriteLeaves.size() > 1);

            if (newJunction1 == null) {
                logger.warn("Could not grow first branch of dendrite!.");
                if (this.dendriteLeaves.size() > 1) {
                    logger.warn("Discarding leaf bud.");
                    discardDendriteLeaf(nextBud);
                }
                return false;
            }
            
            logger.debug("Number of leaves: " + this.dendriteLeaves.size());

            // In any cases, selected dendrite is a leaf no longer.
            this.dendriteLeaves.remove(nextBud);
            
            logger.debug("Number of leaves (removed bud): " 
                    + this.dendriteLeaves.size());

            // Add the new leaf to the list if not a synapse.
            if (newJunction1.getType() != NeuriteJunction.Type.AXON) {
                this.dendriteLeaves.add(newJunction1);
                this.totalDendrites++;
                logger.debug("Number of leaves (added J1): " 
                        + this.dendriteLeaves.size());
            }
            
            // Second (optional) branch from bud.
            
            if (checkConcentrationTrigger(this.cellGrowthRegulator 
                    / Math.pow(nextBud.getDepth(), 2), false)) {
                
                NeuriteJunction newJunction2 = 
                        extendNeurite(NeuriteJunction.Type.DENDRITE, nextBud, 
                                this.dendriteLeaves.size() > 1);
                
                if (newJunction2 == null) {
                    // At this point the first branch at least 
                    // was added successfully.
                    logger.warn("Could not grow second branch of dendrite!");
                    return true;
                }

                if (newJunction2.getType() != NeuriteJunction.Type.AXON) {
                    
                    // Need to add the second dendrite and possibly expel one
                    // dendrite from the table if it is full.
                        
                    this.dendriteLeaves.add(newJunction2);
                    this.totalDendrites++;
                    logger.debug("Number of leaves (added J2): " 
                            + this.dendriteLeaves.size());
                    
                    if (this.dendriteLeaves.size() > MAX_DENDRITE_LEAVES) {
                        
                        /* Leaves that are expelled from the list won't ever be
                         * candidate again as the root of new buds, hence they
                         * can be discarded. Also, only new dendrites have the
                         * opportunity to connect to axons.
                         */

                        NeuriteJunction dendriteToRemove = 
                                this.dendriteLeaves.get(0);
                        for (NeuriteJunction dendriteLeaf : 
                                this.dendriteLeaves) {
                            if (dendriteLeaf.getDepth() 
                                    < dendriteToRemove.getDepth()) {
                                dendriteToRemove = dendriteLeaf;
                            }
                        }

                        discardDendriteLeaf(dendriteToRemove);
                                                
                    } // End if()
                    
                } // End if()
                
            } // End if ()
            
            return true;

        } // End if()
        
        return false;
        
    } // End of cellDendritesGrowthHandler()


    /**
     * Removes the specified dendrite leaf from the dendrites tree and add it
     * to the recycling pool.
     * 
     * @param The dendrite to remove.
     */
    protected void discardDendriteLeaf(NeuriteJunction dendriteToRemove) {
        
        logger.info("Discarding dendrite leaves...");

        this.dendriteLeaves.remove(dendriteToRemove);
        logger.debug("Number of leaves (removed deepest): " 
                + this.dendriteLeaves.size());

        boolean done = false;
        
        while (!done) {
            
            // Moving up the tree...
            NeuriteJunction successor = dendriteToRemove.getSuccessor();
    
            dendriteToRemove.setActive(false);
            
            RepastEdge<Object> edgeToRemove = 
                    this.neuritesNetwork.getEdge(dendriteToRemove, 
                            dendriteToRemove.getSuccessor());
            if (edgeToRemove == null) {
                throw new IllegalStateException(
                        "No edge between junctions!");
            }

            this.neuritesNetwork.removeEdge(edgeToRemove);

            dendriteToRemove.getPredecessors().clear();
            this.freeDendriteLeavesPool.add(dendriteToRemove);
            this.totalDendrites--;
            
            logger.debug("Dendrite depth " 
                    + dendriteToRemove.getDepth() + " added to pool.");
    
            // The successor is not a leaf?
            List<NeuriteJunction> predecessors = successor.getPredecessors();
            if (predecessors.size() > 1) {
                predecessors.remove(dendriteToRemove);
                done = true;
            } else {
                dendriteToRemove = successor;
            }
            
        } // End while()

    } // End of discardDendriteLeaf()

    
    /**
     * Extend a new neurite into the grid.
     * 
     * @param newJunctionType The type of neurite to be created.
     * @param currentJunction The junction from which the new neurite will be an 
     *                        offshoot.
     * @param findSynapes Create a synapse if possible.
     */
    protected NeuriteJunction extendNeurite(
            final NeuriteJunction.Type newJunctionType,
            final NeuriteJunction currentJunction,
            final boolean findSynapses) {
        
        if (newJunctionType == NeuriteJunction.Type.NEURON) {
            throw new IllegalArgumentException("AXON or DENDRITE only!");
        }
        
        GridPoint currentLocation = this.grid.getLocation(currentJunction);
        assert currentLocation != null : "Current location is null!!!";
        
        // Use the GridCellNgh class to create GridCells for
        // the surrounding neighbourhood.
        GridCellNgh<Object> nghCreator = 
                new GridCellNgh<Object>(this.grid, currentLocation, 
                        Object.class, 1, 1, 1);
        List<GridCell<Object>> gridCells =    
                nghCreator.getNeighborhood(false);
        SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

        // Pick the first free grid cell among the shuffled list.
        
        GridCell<Object> selectedGridCell = null;
        double minConcentration = Double.MAX_VALUE;
        NeuriteJunction newJunction = null;
        
        // Synapses are not created from a root NEURON type.
        final boolean lookForSynapse = findSynapses 
                && (newJunctionType == NeuriteJunction.Type.DENDRITE)
                && (currentJunction.getType() == NeuriteJunction.Type.DENDRITE);
        
        // Scan the neighbourhood.
        for (GridCell<Object> gridCell : gridCells) {
            
            boolean freeCell = true;
            NeuriteJunction synapse = null;
            
            if (gridCell.size() > 0) {
                
            	// Examine objects in the current grid cell.
                for (Object obj : gridCell.items()) {

                    if (obj instanceof NeuriteJunction) {
                        
                        NeuriteJunction junction = (NeuriteJunction) obj;
                        
                        if (!junction.isActive()) {
                        	// Skip.
                            continue;
                        }
                    
                        if (junction.getNeuron() == this) {
                        	
                        	// Neurite belongs to this neuron.
                            freeCell = false;
                            if (!lookForSynapse) {
                                break;
                            }
                            
                            /* Grid cell is not free but keep looking just in
                             * case we find a synapse instead.
                             */
                            
                        } else if (lookForSynapse 
                                && (junction.getType() 
                                        == NeuriteJunction.Type.AXON)) {
                        	
                        	// Found a site for a synapse.
                            synapse = junction;
                            break;
                            
                        } // End if()
                        
                    } else if (obj instanceof GeneRegulatedCell) {
                        
                        GeneRegulatedCell cell = (GeneRegulatedCell) obj;
                        if (cell.attached) {
                        	// Dendrites can't grow in a grid cell where there
                        	// is a sedentary attached cell.
                            freeCell = false;
                            break;
                        }
                        
                    } // End if()
                    
                } // End for(junction)
                        
            } // End if()
            
            if (synapse == null) {
                
                if (freeCell) {
                    
                    ExtracellularMatrixSample extracellularMatrix = 
                            getExtracellularMatrixSample(gridCell.getPoint());
                    double samConcentration = extracellularMatrix
                            .getConcentration(CellProductType.SAM);
                    if (samConcentration < minConcentration) {
                        minConcentration = samConcentration;
                        selectedGridCell = gridCell;
                    }
                    
                } // End if()
                
            } else {
                                    
                newJunction = synapse;
                selectedGridCell = gridCell;
                break;
                    
            } // End if()
            
        } // End for(gridCell)
        
        if (selectedGridCell == null) {
            return null;
        }
        
        if (newJunction == null) {
            
            if ((newJunctionType == NeuriteJunction.Type.AXON) 
                    || this.freeDendriteLeavesPool.isEmpty()) {
                
                // Create the new junction.
            
                newJunction = new NeuriteJunction(newJunctionType, 
                        this, currentJunction.getDepth() + 1);
            
                @SuppressWarnings("unchecked")
                Context<Object> context = ContextUtils.getContext(this);

                context.add(newJunction);

                logger.debug("Created new junction: " + newJunction.getType());
                
            } else {
                
                /*
                 *  Recycle a free dendrite leaf. Dendrites are numerous and
                 *  consequently expensive objects to create and dispose of
                 *  through the Java garbage collection mechanism, so we pool
                 *  them instead.
                 */
                
                newJunction = this.freeDendriteLeavesPool.remove();
                
                logger.debug("Recycling junction: " + newJunction.getType() 
                        + ", old depth = " + newJunction.getDepth());

                newJunction.setDepth(currentJunction.getDepth() + 1);
                newJunction.setActive(true);
                                
            } // End if();
            
            GridPoint newJunctionLocation = selectedGridCell.getPoint();
            
            this.space.moveTo(newJunction, 
                    getNewNeuriteSpacePos(currentLocation.getX(), 
                            newJunctionLocation.getX()), 
                    getNewNeuriteSpacePos(currentLocation.getY(), 
                            newJunctionLocation.getY()), 
                    getNewNeuriteSpacePos(currentLocation.getZ(), 
                            newJunctionLocation.getZ())); 
            this.grid.moveTo(newJunction, newJunctionLocation.getX(), 
                    newJunctionLocation.getY(), newJunctionLocation.getZ());

            if (newJunctionType == NeuriteJunction.Type.DENDRITE) {
                
                // Create a new dendrite leaf.
                currentJunction.getPredecessors().add(newJunction);
                newJunction.setSuccessor(currentJunction);
                this.neuritesNetwork.addEdge(newJunction, currentJunction);
                
            } else {
                
                // Create a new axon junction.
                newJunction.getPredecessors().add(currentJunction);
                currentJunction.setSuccessor(newJunction);
                
                this.neuritesNetwork.addEdge(currentJunction, newJunction);
                
            } // End if()
                        
        } else {
        
            // Create the dendrite synapse.
            
            currentJunction.getPredecessors().add(newJunction);
            newJunction.getSynapses().add(currentJunction);
            
            this.neuritesNetwork.addEdge(newJunction, currentJunction);
            this.neuralNetwork.addEdge(newJunction.getNeuron(), this, 
                    RandomHelper.nextDoubleFromTo(-1, 1));
        
            logger.info("New synapse created.");
            
        } // End if()
        
        return newJunction;
        
    } // End of extendNeurite()

    
    /**
     * Helper method to visually position a neurite on the edge of a continuous
     * space location, since the centre of the location can be occupied by a
     * cell. This is more appealing visually. Notice that grid coordinates are
     * integers while the result is a double precision continuous space
     * coordinate.
     * 
     * @param sourcePos The grid coordinate on a chosen axis of the neurite from
     *                  which the new/target neurite is an offshoot. 
     * @param targetPos The grid coordinate on the same axis of the new
     *                  neurite.
     * @return A randomly picked continuous space coordinate that keeps clear of
     *         the centre of the continuous space location of the target.             
     */
    protected double getNewNeuriteSpacePos(final int sourcePos, 
            final int targetPos) {

        // Gives the sign, or direction.
        int deltaPos = targetPos - sourcePos;
        
        if (deltaPos == 0) {
            // Same plane: length of cell.
            return targetPos + RandomHelper.nextDoubleFromTo(0, 1);
        } else if (deltaPos < 0) {
            // Behind: border 0.2 thick.
            return targetPos + 0.8 + RandomHelper.nextDoubleFromTo(0, 0.2);
        } else {
            // In front: border 0.2 thick.
            return targetPos + RandomHelper.nextDoubleFromTo(0, 0.2);
        }
        
    } // End of getNewNeuriteSpacePos()
    
    
    /**
     * Identifies if a grid coordinate is on the same side as another relative
     * to a central location.
     * 
     * @param centreCoord The central grid coordinate.
     * @param coord1 The first coordinate.
     * @param coord2 The second coordinate.
     * @return {@code true} if both coordinates are on the same side relative to
     *         the centre, {@code false} otherwise.
     */
    protected boolean isOnTheSameSide(final int centreCoord, 
            final int coord1, final int coord2) {
        
        return (coord1 > centreCoord && coord2 > centreCoord) 
                || (coord1 < centreCoord && coord2 < centreCoord);
        
    } // End of isOnTheSameSide()
    
    
    /**
     * Calculate the activation of the neuron. 
     */
    protected void calculateActivation() {
        
        double foodConcentration = this.membraneChannels
                .get(CellProductType.FOOD).getConcentration();
        
        logger.debug("Neuron food concentration: " + foodConcentration);
        
        List<RepastEdge<Object>> inputEdges = 
                new ArrayList<RepastEdge<Object>>();
        
        double netInput = 0;
        
        for (RepastEdge<Object> inputEdge :    
                this.neuralNetwork.getInEdges(this)) {
            
            Neuron inputNeuron = (Neuron) inputEdge.getSource();
            
            logger.debug("Input neuron: activation = " 
                    + inputNeuron.getActivation() + ", weight = " 
                    + inputEdge.getWeight());

            netInput += inputNeuron.getActivation() * inputEdge.getWeight()    
                    * this.cellNeurotransmitterRegulator * foodConcentration;
            
            inputEdges.add(inputEdge);
            
        } // End for(inputEdge)
        
        logger.debug("Number of input edges: " + inputEdges.size());
        logger.debug("Net input: " + netInput);
        
        this.activation = 1 / (1 + Math.pow(Math.E, -netInput));
        logger.debug("Activation: " + this.activation);
        
        // Adjust the weight using Hebb's rule.
        for (RepastEdge<Object> inputEdge : inputEdges) {
            
            Neuron inputNeuron = (Neuron) inputEdge.getSource();
            double deltaWeight = LEARNING_RATE * this.activation 
                        * (inputNeuron.activation - inputNeuron.activation 
                                * Math.abs(inputEdge.getWeight()));

            double newWeight = inputEdge.getWeight() 
                    + Math.signum(inputEdge.getWeight()) * deltaWeight;
            
            logger.debug("Delta weight: " + deltaWeight);
            logger.debug("Input neuron new weight: " + newWeight); 

            inputEdge.setWeight(newWeight);
            
        } // End for(inputEdge)
        
    } // calculateActivation()


    // OVERRIDEN METHODS -------------------------------------------------------
    
    
    /**
     * Move the current cell to the specified coordinates, this both in the
     * continuous space and in the grid.
     * 
     * @param x The x-axis coordinate of the new location.
     * @param y The y-axis coordinate of the new location.
     * @param z The z-axis coordinate of the new location.
     */
    @Override // Cell
    public void moveTo(final int x, final int y, final int z) {
        
        // Once initialised neuron can't be move.
        if (this.neuritesRoot == null) {
            super.moveTo(x, y, z);
        } else {
            throw new IllegalStateException(
                    "Neuron can't be moved once intialised!");
        }
        
    } // End of moveTo()
    
    
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
    @Override // GeneRegulatedCell
    protected boolean bumpRequest(final Cell requester, 
            final GridPoint requesterLocation, final int extentX, 
            final int extentY, final int extentZ) {

        // Neurons can't be bumped.
        return false;
        
    } // End of bumpRequest()
    
    
    /** 
     * Clone the current cell giving it a new ID.
     * 
     * @param newId The ID of the new cell.
     */
    @Override // Cell
    protected Cell getClone(final String newId) {
        throw new IllegalStateException("Neurons cannot be cloned!");
    }

    
    // RUNTIME QUERY METHODS ---------------------------------------------------
    
    
    /**
     * Returns the activation level formatted as a string.
     * 
     * @return The activation level formatted as a string.
     */
    public String getFormattedActivation() {
    	// Scientific notation with one decimal.
        return String.format("%+.1e", this.activation); 
    }
    
    
} // End of Neuron class
