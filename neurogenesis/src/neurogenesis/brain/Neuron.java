/**
 * 
 */
package neurogenesis.brain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
 * @author bob
 *
 */
public class Neuron extends GeneRegulatedCell {

	public static int count = 0;
	
	/**
	 * 
	 */
	protected static final int MAX_DENDRITE_ROOTS = 1; // 25
	
	
	/**
	 * 
	 */
	protected static final int DENDRITE_LEAVES_LIST_SIZE = 2; // 30
	
	
	/**
	 * 
	 */
	public static final double LEARNING_RATE = 0.2;
	
	
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
	
	
	//
	private final NeuriteJunction[] dendriteLeaves = 
			new NeuriteJunction[DENDRITE_LEAVES_LIST_SIZE];
	
	
	//
	private int dendriteLeavesListSize = 0;
	
	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	public Neuron(final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid,
			final RegulatoryNetwork newRegulatoryNetwork,
			final Network<Object> newNeuralNetwork,
			final Network<Object> newNeuritesNetwork) {
		
		super(newSpace, newGrid, newRegulatoryNetwork);
		
		this.neuralNetwork = newNeuralNetwork;
		this.neuritesNetwork = newNeuritesNetwork;
		
	} // End of Neuron(ContinuousSpace, Grid, RegulatoryNetwork, Network)

	
	/**
	 * 
	 * @param baseCell
	 */
	public Neuron(final GeneRegulatedCell motherCell, 
			Network<Object> newNeuralNetwork,
			Network<Object> newNeuritesNetwork) {
		
		super(motherCell);
				
		this.neuralNetwork = newNeuralNetwork;
		this.neuritesNetwork = newNeuritesNetwork;
		
		this.membraneChannels.get(CellProductType.SAM).setOpenForOutput(true);
		
	} // End of Neuron(GeneRegulatedCell)
	
	
	/**
	 * 
	 * @return
	 */
	public double getActivation() {
		return this.activation;
	};
	

	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.RANDOM_PRIORITY)
	public void step() {

		calculateActivation();
		
		absorbProductsFromMatrix();
		updateRegulatoryNetwork();
		updateCellConcentrations();
				
		// Handles cell death.
		if (!cellDeathHandler()) {
			
			// Handles neurites growth.
			initialiseNeurites(true, true);
			cellAxonGrowthHandler();
			cellDendritesGrowthHandler();
			
			// Handles cell adhesion.
			//cellAdhesionHandler();

			// Handles mutations.
			//cellMutationHandler();
			
			// Handles movement.
			//cellMovementHandler();
			
			expelProductsToMatrix();

		} // End if()
		
	} // End of step()

	
	/**
	 * 
	 */
	@Override
	protected boolean cellDeathHandler() {
		
		double wasteConcentration = this.membraneChannels
				.get(CellProductType.WASTE).getConcentration();
		
		System.out.println("Cell death waste concentration: " 
				+ wasteConcentration);

		double foodConcentration = this.membraneChannels
				.get(CellProductType.FOOD).getConcentration();
		
//		if ((wasteConcentration > REGULATOR_UNIVERSAL_THRESHOLD)
//				|| (foodConcentration == 0)) {
		if (count++ > 500) { 
			
			for (RepastEdge<Object> edge : this.neuralNetwork.getEdges(this)) {
				this.neuralNetwork.removeEdge(edge);
			}
		
			List<NeuriteJunction> junctions = new ArrayList<NeuriteJunction>();

			if (this.neuritesRoot != null) {
				junctions.add(this.neuritesRoot);
				gatherNeuriteJunctions(NeuriteJunction.Type.DENDRITE,
						this.neuritesRoot, junctions);
			}
			
			if (this.axonTip != null) {
				junctions.add(this.axonTip);
				gatherNeuriteJunctions(NeuriteJunction.Type.AXON,
						this.axonTip, junctions);
			}
			
			@SuppressWarnings("unchecked")
			Context<Object> context = ContextUtils.getContext(this);
			
			for (NeuriteJunction junction : junctions) {
				for (RepastEdge<Object> edge : 
						this.neuritesNetwork.getEdges(junction)) {
					this.neuritesNetwork.removeEdge(edge);
				}
				context.remove(junction);
			}
			
			this.alive = false;
			
			context.remove(this);
			System.out.println("****** Neuron death event ******");
			return true;

		} // End if()
				
		return false;
		
	} // End of cellDeathHandler)_
	
	
	/**
	 * 
	 * @param dendrites
	 */
	protected void gatherNeuriteJunctions(
			final NeuriteJunction.Type junctionType,
			final NeuriteJunction currentJunction,
			final List<NeuriteJunction> junctions) {
		
		for (Object obj : 
				this.neuritesNetwork.getPredecessors(currentJunction)) {
			NeuriteJunction nextJunction = (NeuriteJunction) obj;
			// Do not collect beyond a synapse!
			if (nextJunction.getType() == junctionType) {
				junctions.add(nextJunction);
				gatherNeuriteJunctions(junctionType, nextJunction, junctions);
			}
		}
		
	} // End of gatherNeuriteJunctions()

	
	/**
	 * 
	 * @return
	 */
	public boolean initialiseNeurites(final boolean createAxon, 
			final boolean createDendrites) {
		
		if (this.neuritesRoot == null) {
			
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
				this.axonTip = extendNeurite(NeuriteJunction.Type.AXON, 
						this.neuritesRoot);
			}
			
			// Creates the initial dendrites.
			
			this.dendriteLeavesListSize = 0;

			if (createDendrites) {
				
				for (int n = 1; n <= MAX_DENDRITE_ROOTS; n++) {
				
					if ((n == 1) || (RandomHelper.nextDoubleFromTo(0, 1) 
							<= this.cellGrowthRegulator)) {
					
						NeuriteJunction newDendrite = 
								extendNeurite(NeuriteJunction.Type.DENDRITE, 
										this.neuritesRoot);
					
						if (newDendrite == null) {
							break;
						}
						
						this.dendriteLeaves[this.dendriteLeavesListSize++] = 
								newDendrite;
			
					} // End if()
				
				} // End for()
			
			} // End  if()
			
			return true;

		} // End if()
		
		return false;
		
	} // End of initialiseNeurites()
	
	
	/**
	 * 
	 * @return
	 */
	protected boolean cellAxonGrowthHandler() {
		
		System.out.println("Cell axon growth regulator concentration: " 
				+ this.cellGrowthRegulator);
		
		if (this.checkConcentrationTrigger(this.cellGrowthRegulator, false)) {

			NeuriteJunction newJunction = 
					extendNeurite(NeuriteJunction.Type.AXON, this.axonTip);

			if (newJunction != null) {
				this.axonTip = newJunction;
				return true;				
			}

		} // End if()
		
		return false;
		
	} // End of cellAxonGrowthHandler

	
	/**
	 * 
	 * @return
	 */
	protected boolean cellDendritesGrowthHandler() {
		
		System.out.println("Cell dendrites growth regulator concentration: " 
				+ this.cellGrowthRegulator);
		
		this.cellGrowthRegulator = 0.5;
		if (this.checkConcentrationTrigger(this.cellGrowthRegulator, false)) {

			int currentDepth = -1;
			int currentDepthPos = -1;
			int selectedDepthTopPos = -1;
			int selectedDendritePos = -1;
			double minConcentration = Double.MAX_VALUE;
			
			for (int i = 0; i < this.dendriteLeavesListSize; i++) {
							
				int dendriteDepth = this.dendriteLeaves[i].getDepth();
				if (dendriteDepth != currentDepth) {
					currentDepth = dendriteDepth;
					currentDepthPos = i;
				}
								
				GridPoint dendriteLocation = 
						this.grid.getLocation(this.dendriteLeaves[i]);
				if (dendriteLocation == null) {
					System.out.println("Dendrite location is null! (Neuron is " 
							+ ((this.dendriteLeaves[i].getNeuron().alive) 
									? "alive" : "dead") + ")");
				}
				Map<CellProductType, Double> externalConcentrations = 
						getExternalConcentrations(dendriteLocation);
			
				double externalConcentration = 
						externalConcentrations.get(CellProductType.SAM);
				
				if (externalConcentration < minConcentration) {
					selectedDepthTopPos = currentDepthPos;
					selectedDendritePos = i;
					minConcentration = externalConcentration;
				}
				
			} // End for()
			
			NeuriteJunction nextBud = this.dendriteLeaves[selectedDendritePos];
			
			// First branch from bud.

			NeuriteJunction newJunction1 = 
					extendNeurite(NeuriteJunction.Type.DENDRITE, nextBud);

			if (newJunction1 == null) {
				return false;
			}
			
			if (!newJunction1.isSynapse()) {
				
				// Selected dendrite is a leaf no longer.
				if (selectedDendritePos != selectedDepthTopPos) {
					this.dendriteLeaves[selectedDendritePos] 
							= this.dendriteLeaves[selectedDepthTopPos];
				}
				this.dendriteLeaves[selectedDepthTopPos] = newJunction1;
				
			} // End if()
			
			// Second (optional) branch from bud.
			
			if (RandomHelper.nextDoubleFromTo(0, 1) > this.membraneChannels
					.get(CellProductType.SAM).getConcentration()) {
				
				NeuriteJunction newJunction2 = 
						extendNeurite(NeuriteJunction.Type.DENDRITE, nextBud);
				
				if (newJunction2 == null) {
					// At this point the first branch at least 
					// was added successfully.
					return true;
				}

				if (newJunction1.isSynapse()) {
					
					// Selected dendrite is a leaf no longer.
					if (selectedDendritePos != selectedDepthTopPos) {
						this.dendriteLeaves[selectedDendritePos] 
								= this.dendriteLeaves[selectedDepthTopPos];
					}
					this.dendriteLeaves[selectedDepthTopPos] = newJunction2;
					
				} else {
					
					NeuriteJunction junctionToSwap = newJunction2; 
					for (int i = selectedDepthTopPos; 
							i < this.dendriteLeavesListSize; i++) {
						NeuriteJunction currentJunction = 
								this.dendriteLeaves[i];
						this.dendriteLeaves[i] = junctionToSwap;
						junctionToSwap = currentJunction;
					}
					
					if (this.dendriteLeavesListSize 
							< DENDRITE_LEAVES_LIST_SIZE) {
						
						this.dendriteLeaves[this.dendriteLeavesListSize++] = 
								junctionToSwap;
						
					} else {
						
						/* Leaves that are expelled from the list won't ever be
						 * candidate again as the root of new buds, hence they
						 * can be discarded. Also, only new dendrites have the
						 * opportunity to connect to axons.
						 */
					
						System.out.println("Discarding dendrite leaves...");
						
						@SuppressWarnings("unchecked")
						Context<Object> context = ContextUtils.getContext(this);

						boolean done = false;
						
						while (!done) {
							
							NeuriteJunction successor =	
									(NeuriteJunction) this.neuritesNetwork
									.getSuccessors(junctionToSwap)
									.iterator().next();

							for (RepastEdge<Object> edge :	
									this.neuritesNetwork
									.getEdges(junctionToSwap)) {
								this.neuritesNetwork.removeEdge(edge);
							}
					
							context.remove(junctionToSwap);
							System.out.println("Removed dendrite depth " 
									+ junctionToSwap.getDepth());
					
							// The successor is not a leaf?
							if (this.neuritesNetwork.getPredecessors(successor)
									.iterator().hasNext()) {
								done = true;
							} else {
								junctionToSwap = successor;
							}
							
						} // End while()
						
					} // End if()
					
				} // End if()
				
			} // End if ()
			
			return true;

		} // End if()
		
		return false;
		
	} // End of cellDendritesGrowthHandler

	
	/**
	 * 
	 */
	protected NeuriteJunction extendNeurite(
			final NeuriteJunction.Type newJunctionType,
			final NeuriteJunction currentJunction) {
		
		if (newJunctionType == NeuriteJunction.Type.NEURON) {
			throw new IllegalArgumentException("AXON or DENDRITE only!");
		}
		
		GridPoint currentLocation =	this.grid.getLocation(currentJunction);
		if (currentLocation == null) {
			System.out.println("Current location is null!!!");
		}
		
		// Use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<NeuriteJunction> nghCreator = 
				new GridCellNgh<NeuriteJunction>(this.grid, currentLocation, 
						NeuriteJunction.class, 1, 1, 1);
		List<GridCell<NeuriteJunction>> gridCells =	
				nghCreator.getNeighborhood(false);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

		// Pick the first free grid cell among the shuffled list.
		
		GridCell<NeuriteJunction> selectedGridCell = null;
		double minConcentration = Double.MAX_VALUE;
		NeuriteJunction newJunction = null;
		
		for (GridCell<NeuriteJunction> gridCell : gridCells) {
			
			boolean freeCell = true;
			NeuriteJunction synapse = null;
			
			if (gridCell.size() > 0) {
				for (NeuriteJunction junction : gridCell.items()) {
					synapse = null;
					if (junction.getNeuron() == this) {
						freeCell = false;
						break;
					} else if ((currentJunction.getType() == 
							NeuriteJunction.Type.DENDRITE) 
							&& (junction.getType() == NeuriteJunction.Type.AXON)
							&& (this.neuralNetwork.getEdge(
									junction.getNeuron(), this) == null)) {
						synapse = junction;
					}
				}
			}
			
			if (freeCell) {
				
				if (synapse == null) {
					
					Map<CellProductType, Double> externalConcentrations = 
							getExternalConcentrations(gridCell.getPoint());
					double samConcentration = 
							externalConcentrations.get(CellProductType.SAM);
					if (samConcentration < minConcentration) {
						minConcentration = samConcentration;
						selectedGridCell = gridCell;
					}
				
				} else {
					
					newJunction = synapse;
					selectedGridCell = gridCell;
					break;
					
				} // End if()
					
			} // End if()
			
		} // End for()
		
		if (selectedGridCell == null) {
			return null;
		}
		
		if (newJunction == null) {
			
			newJunction = new NeuriteJunction(newJunctionType, 
					this, currentJunction.getDepth() + 1);
			
			@SuppressWarnings("unchecked")
			Context<Object> context = ContextUtils.getContext(this);

			context.add(newJunction);

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
				this.neuritesNetwork.addEdge(newJunction, currentJunction);
			} else {
				this.neuritesNetwork.addEdge(currentJunction, newJunction);				
			}
						
		} else {
		
			newJunction.setSynapse(true);
			this.neuritesNetwork.addEdge(newJunction, currentJunction);
			this.neuralNetwork.addEdge(newJunction.getNeuron(), this, 
					RandomHelper.nextDoubleFromTo(-1, 1));
		
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
		
		return targetPos;
		
//		int deltaPos = targetPos - sourcePos;
//		
//		if (deltaPos == 0) {
//			return targetPos + RandomHelper.nextDoubleFromTo(0, 1);
//		} else if (deltaPos < 0) {
//			return targetPos + 0.9 + RandomHelper.nextDoubleFromTo(0, 0.1);
//		} else {
//			return targetPos + RandomHelper.nextDoubleFromTo(0, 0.1);
//		}
		
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
	private void calculateActivation() {
		
		double netInput = 0;
		
		for (Object obj : this.neuralNetwork.getPredecessors(this)) {
			if (obj instanceof Neuron) {
				Neuron neuron = (Neuron) obj;
				RepastEdge<Object> edge = this.neuralNetwork.getEdge(neuron, this);
				netInput += neuron.getActivation() * edge.getWeight();
			}
		}
		
		// Sigmoid function.
		this.activation = (1 / (1 + Math.pow(Math.E, -1 * netInput)));
		
		// Ajust the weight using the Hebbian rule.
		for (Object obj : this.neuralNetwork.getPredecessors(this)) {
			if (obj instanceof Neuron) {
				Neuron neuron = (Neuron) obj;
				RepastEdge<Object> edge = this.neuralNetwork.getEdge(neuron, this);
				double newWeight = LEARNING_RATE * this.activation 
						* (neuron.activation - neuron.activation 
								* edge.getWeight());
				edge.setWeight(newWeight);
			}
		}
		
	} // calculateActivation()

	
	/** 
	 */
	protected Cell clone() {
		throw new IllegalStateException("Neurons cannot be cloned!");
	}
	
	
} // End of Neuron class
