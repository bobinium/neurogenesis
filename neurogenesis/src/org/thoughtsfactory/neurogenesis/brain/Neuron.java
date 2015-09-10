/**
 * 
 */
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
 * @author 
 *
 */
public class Neuron extends GeneRegulatedCell {


	// CONSTANTS ===============================================================
	
	
	/**
	 * 
	 */
	public static int MAX_DENDRITE_ROOTS = 25;
	
	
	/**
	 * 
	 */
	public static int MAX_DENDRITE_LEAVES = 2;
	
	
	/**
	 * 
	 */
	public static double LEARNING_RATE = 0.2;
	
	
	//
	private final static Logger logger = Logger.getLogger(Neuron.class);	
		
	
	// INSTANCE VARIABLES ======================================================
	
	
	/**
	 * 
	 */
	protected final Network<Object> neuralNetwork;
	
	
	/**
	 * 
	 */
	protected final Network<Object> neuritesNetwork;
	
	
	/**
	 * 
	 */
	protected NeuriteJunction axonTip = null;
	
	
	/**
	 * 
	 */
	protected NeuriteJunction neuritesRoot = null;
	
	
	/**
	 * 
	 */
	protected double activation;
	
	
	/**
	 * 
	 */
	protected List<NeuriteJunction> dendriteLeaves = 
			new ArrayList<NeuriteJunction>();
	
	
	/**
	 * 
	 */
	protected Queue<NeuriteJunction> freeDendriteLeavesPool = 
			new LinkedList<NeuriteJunction>();
	
	
	/**
	 * 
	 */
	protected int totalDendrites = 0;
	
	
	// CONSTRUCTORS ============================================================
	
	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	public Neuron(final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid,
			final RegulatoryNetwork newRegulatoryNetwork,
			final Network<Object> newNeuralNetwork,
			final Network<Object> newNeuritesNetwork,
			final boolean newCellAdhesionEnabled) {
		
		super(newSpace, newGrid, newRegulatoryNetwork, newCellAdhesionEnabled);
		
		this.neuralNetwork = newNeuralNetwork;
		this.neuritesNetwork = newNeuritesNetwork;
		
	} // End of Neuron(ContinuousSpace, Grid, RegulatoryNetwork, Network)

	
	/**
	 * 
	 * @param baseCell
	 */
	public Neuron(final GeneRegulatedCell motherCell, 
			final Network<Object> newNeuralNetwork,
			final Network<Object> newNeuritesNetwork) {
		
		super(motherCell, false);
				
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
		
	} // End of Neuron(GeneRegulatedCell)
	
	
	// METHODS =================================================================
	
	
	// ACESSORS ----------------------------------------------------------------
	
	
	/**
	 * 
	 * @return
	 */
	public final double getActivation() {
		return this.activation;
	};
	

	// OTHER METHDOS -----------------------------------------------------------
	
	
	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.RANDOM_PRIORITY)
	public void step() {

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
		
	} // End of step()


	/**
	 * 
	 * @param neighbour
	 * @return
	 */
	@Override
	protected boolean bumpRequest(final Cell requester, 
			final GridPoint requesterLocation, final int extentX, 
			final int extentY, final int extentZ) {

		// Neurons can't be bumped.
		return false;
		
	} // End of bumpRequest()
	
	
	/**
	 * 
	 */
	@Override
	public void moveTo(final int x, int y, int z) {
		
		// Once initialised neuron can't be move.
		if (this.neuritesRoot == null) {
			super.moveTo(x, y, z);
		} else {
			throw new IllegalStateException(
					"Neuron can't be moved once intialised!");
		}
		
	} // End of moveTo()
	
	
	/**
	 * Remove a cell from the context along with all with all the dependent
	 * objects that it owns. Neurons must destroy all their neurites before
	 * dying properly.
	 */
	@Override
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
			
			currentJunction.getPredecessors().get(0);
			
		} // End while()
		
		this.axonTip = null;
		
	} // End of destroyAxon()
	

	/**
	 * Destroy (i.e. remove from the context) all neurite junctions that
	 * form the dendrites network of the current neuron and adjust relevant 
	 * dependencies.
	 */
	protected void destroyDendrites() {

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
	 * 
	 * @param junctiontoDestroy
	 */
	protected void removefNeuriteJunction(
			final NeuriteJunction junctionToDestroy) {

		if (junctionToDestroy.getType() == NeuriteJunction.Type.DENDRITE) {

			junctionToDestroy.setActive(false);
	
			Stack<NeuriteJunction> junctionsToRemove = 
					new Stack<NeuriteJunction>();
			junctionsToRemove.push(junctionToDestroy);
				
			while (!junctionsToRemove.isEmpty()) {
			
				NeuriteJunction currentJunction = junctionsToRemove.pop();
				
				for (NeuriteJunction junction : 
						currentJunction.getPredecessors()) {

					if (junction.getNeuron() == this) {		
						junction.setActive(false);						
						junctionsToRemove.push(junction);
					} else {
						junction.getSynapses().remove(currentJunction);
//						RepastEdge<Object> edgeToRemove = this.neuralNetwork
//								.getEdge(junction.getNeuron(), this);
//						this.neuralNetwork.removeEdge(edgeToRemove);
					}
				
					int edgeCount = 0;
					for (RepastEdge<Object> edge : this.neuritesNetwork.getEdges(currentJunction)) {
						edgeCount++;
					}
					logger.debug("Numer of edges: " + edgeCount);
					
					RepastEdge<Object> edgeToRemove = this.neuritesNetwork
							.getEdge(junction, currentJunction);
					if (edgeToRemove == null) {
						throw new IllegalStateException(
								"No edge between junctions!");
					}

					this.neuritesNetwork.removeEdge(edgeToRemove);

				} // End for(junction)

				currentJunction.getPredecessors().clear();
				this.freeDendriteLeavesPool.add(currentJunction);
			
			} // End while()
		
		} else {

//			NeuriteJunction currentJunction = junctionToDestroy;
//			
//			while (currentJunction != null) {
//				
//				currentJunction.setActive(false);
//				NeuriteJunction sucessor = currentJunction.getSuccessor();
//				
//			} // End while()
			
		} // End if()
		
	} // End of destroyNeuriteJunction()
	
	
	/**
	 * Internal procedure that initialises the first level dendrites and/or 
	 * the axon tip or a newly born neuron. Assume that the current neuron has 
	 * already been deployed in the context.
	 * 
	 * @param createAxon A boolean specifying if the axon must be created.
	 * @param createDendrites A boolean specifying if the first level dendrites
	 *                        must be created.
	 * @return <code>true</code> if the initialisation went successfully,
	 * <code>false</code> otherwise.
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
		
	} // End of initialiseNeurites(boolean, boolean)
	
	
	/**
	 * 
	 * @return
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
		
	} // End of cellAxonGrowthHandler

	
	/**
	 * 
	 * @return
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
				
		} // End for()

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
	 * 
	 */
	protected void discardDendriteLeaf(NeuriteJunction dendriteToRemove) {
		
		logger.info("Discarding dendrite leaves...");

		this.dendriteLeaves.remove(dendriteToRemove);
		logger.debug("Number of leaves (removed deepest): " 
				+ this.dendriteLeaves.size());

		boolean done = false;
		
		while (!done) {
			
			// Moving up the tree...
			NeuriteJunction successor =	dendriteToRemove.getSuccessor();
	
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
	 * 
	 */
	protected NeuriteJunction extendNeurite(
			final NeuriteJunction.Type newJunctionType,
			final NeuriteJunction currentJunction,
			final boolean findSynapses) {
		
		if (newJunctionType == NeuriteJunction.Type.NEURON) {
			throw new IllegalArgumentException("AXON or DENDRITE only!");
		}
		
		GridPoint currentLocation =	this.grid.getLocation(currentJunction);
		if (currentLocation == null) {
			logger.warn("Current location is null!!!");
		}
		
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
		
		for (GridCell<Object> gridCell : gridCells) {
			
			boolean freeCell = true;
			NeuriteJunction synapse = null;
			
			if (gridCell.size() > 0) {
				
				for (Object obj : gridCell.items()) {

					if (obj instanceof NeuriteJunction) {
						
						NeuriteJunction junction = (NeuriteJunction) obj;
						
						if (!junction.isActive()) {
							continue;
						}
					
						if (junction.getNeuron() == this) {
							freeCell = false;
							if (!lookForSynapse) {
								break;
							}
						} else if (lookForSynapse 
								&& (junction.getType() 
										== NeuriteJunction.Type.AXON)) {
							synapse = junction;
							break;
						}
						
					} else if (obj instanceof GeneRegulatedCell) {
						
						GeneRegulatedCell cell = (GeneRegulatedCell) obj;
						if (cell.attached) {
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
				
				// Recycle a free dendrite leaf.
				
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
	 * 
	 * @param sourcePos
	 * @return
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
	 * 
	 * @param centrePos
	 * @param refPos
	 * @param newPos
	 * @return
	 */
	protected boolean isOnTheSameSide(final int centrePos, 
			final int refPos, final int newPos) {
		
		return (refPos > centrePos && newPos > centrePos) 
				|| (refPos < centrePos && newPos < centrePos);
		
	} // End of isOnTheSameSide()
	
	
	/**
	 * 
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
		
		// Ajust the weight using the Hebbian rule.
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


	/**
	 * 
	 * @return
	 */
	public String getFormattedActivation() {
		return String.format("%+.1e", this.activation); 
	}
	
	
	/** 
	 */
	protected Cell clone() {
		throw new IllegalStateException("Neurons cannot be cloned!");
	}
	
	
} // End of Neuron class
