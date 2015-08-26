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
	protected NeuriteJunction dendrite = null;
	
	
	/**
	 * 
	 */
	protected double activation;
	
	
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
			initialiseNeurites();
			if (cellAxonGrowthHandler() | cellDendritesGrowthHandler()) {
				//this.cellGrowthRegulator -= this.cellGrowthRegulator / 5;
			}
			
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
		
		if (super.cellDeathHandler()) {
		
			List<NeuriteJunction> junctions = new ArrayList<NeuriteJunction>();

			if (this.dendrite != null) {
				junctions.add(this.dendrite);
				gatherDendriteJunctions(this.dendrite, junctions);
			}
			
			if (this.axonTip != null) {
				junctions.add(this.axonTip);
				gatherAxonJunctions(this.axonTip, junctions);
			}
			
			@SuppressWarnings("unchecked")
			Context<Object> context = ContextUtils.getContext(this.dendrite);
			
			for (NeuriteJunction junction : junctions) {
				context.remove(junction);
			}
			
			return true;
			
		} // End if()
		
		return false;
		
	} // End of cellDeathHandler)_
	
	
	/**
	 * 
	 * @param dendrites
	 */
	protected void gatherDendriteJunctions(
			final NeuriteJunction currentJunction,
			final List<NeuriteJunction> junctions) {
		
		for (Object obj : this.neuritesNetwork.getSuccessors(currentJunction)) {
			NeuriteJunction nextJunction = (NeuriteJunction) obj;
			// Do not collect beyond a synapse!
			if (nextJunction.getType() == NeuriteJunction.Type.DENDRITE) {
				junctions.add(nextJunction);
				gatherDendriteJunctions(nextJunction, junctions);
			}
		}
		
	} // End of gatherDendriteJunctions()

	
	/**
	 * 
	 * @param dendrites
	 */
	protected void gatherAxonJunctions(final NeuriteJunction currentJunction,
			final List<NeuriteJunction> junctions) {
		
		for (Object obj : 
				this.neuritesNetwork.getPredecessors(currentJunction)) {
			NeuriteJunction prevJunction = (NeuriteJunction) obj;
			// Do not collect beyond a synapse!
			if (prevJunction.getType() == NeuriteJunction.Type.AXON) {
				junctions.add(prevJunction);
				gatherAxonJunctions(prevJunction, junctions);
			}
		}
		
	} // End of gatherAxonJunctions()
	
	
	/**
	 * 
	 * @return
	 */
	protected boolean initialiseNeurites() {
		
		if (this.axonTip == null) {
			
			GridPoint pt = this.grid.getLocation(this);
			
			// Creates the axon.
			
			this.axonTip = 
					new NeuriteJunction(NeuriteJunction.Type.AXON, this);
			
			GridPoint axonLocation = 
					findNeuriteFreeGridCell(this.grid.getLocation(this));
			
			@SuppressWarnings("unchecked")
			Context<Object> context = ContextUtils.getContext(this);
			context.add(this.axonTip);
			
			this.space.moveTo(this.axonTip, 
					getNewNeuriteSpacePos(pt.getX(), axonLocation.getX()), 
					getNewNeuriteSpacePos(pt.getY(), axonLocation.getY()), 
					getNewNeuriteSpacePos(pt.getZ(), axonLocation.getZ())); 
			this.grid.moveTo(this.axonTip, axonLocation.getX(), 
					axonLocation.getY(), axonLocation.getZ());
			
			this.neuritesNetwork.addEdge(this, this.axonTip);
			
			// Creates the dendrite.
			
			this.dendrite = new NeuriteJunction(
					NeuriteJunction.Type.DENDRITE, this);

			GridPoint dendriteLocation = 
					this.findGridCellForDendrite(pt, axonLocation);
			
			context.add(this.dendrite);
			
			this.space.moveTo(this.dendrite, 
					getNewNeuriteSpacePos(pt.getX(), dendriteLocation.getX()), 
					getNewNeuriteSpacePos(pt.getY(), dendriteLocation.getY()), 
					getNewNeuriteSpacePos(pt.getZ(), dendriteLocation.getZ())); 
			this.grid.moveTo(this.dendrite, dendriteLocation.getX(), 
					dendriteLocation.getY(), dendriteLocation.getZ());
			
			this.neuritesNetwork.addEdge(this.dendrite, this);
			
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

			GridPoint pt = this.grid.getLocation(this);
							
			NeuriteJunction newJunction = 
					new NeuriteJunction(NeuriteJunction.Type.AXON, this);
			
			GridPoint freeLocation = findNeuriteFreeGridCell(
					this.grid.getLocation(this.axonTip));
			
			if (freeLocation == null) {
				return false;
			}
			
			@SuppressWarnings("unchecked")
			Context<Object> context = ContextUtils.getContext(this);
			context.add(newJunction);
			
			this.space.moveTo(newJunction, 
					getNewNeuriteSpacePos(pt.getX(), freeLocation.getX()), 
					getNewNeuriteSpacePos(pt.getY(), freeLocation.getY()), 
					getNewNeuriteSpacePos(pt.getZ(), freeLocation.getZ())); 
			this.grid.moveTo(newJunction, freeLocation.getX(), 
					freeLocation.getY(), freeLocation.getZ());
			
			this.neuritesNetwork.addEdge(this.axonTip, newJunction);
			this.axonTip = newJunction;
			
			return true;

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
		
		if (this.checkConcentrationTrigger(this.cellGrowthRegulator, false)) {

			GridPoint pt = this.grid.getLocation(this);
				
			NeuriteJunction newJunction = new NeuriteJunction(
					NeuriteJunction.Type.DENDRITE, this);
			
			NeuriteJunction nextBud = 
					findJunctionWithLowestConcentration(
							this.dendrite, this.dendrite, Double.MAX_VALUE);
			
			GridPoint freeLocation = 
					findNeuriteFreeGridCell(this.grid.getLocation(nextBud));

			if (freeLocation == null) {
				return false;
			}
			
			NeuriteJunction synapse = null;
			
			for (Object obj : this.grid.getObjectsAt(freeLocation.getX(), 
					freeLocation.getY(), freeLocation.getZ())) {
				if (obj instanceof NeuriteJunction) {
					NeuriteJunction junction = (NeuriteJunction) obj;
					if ((junction.getNeuron() != this) 
							&& (junction.getType() == NeuriteJunction.Type.AXON)
							&& (this.neuralNetwork.getEdge(
									junction.getNeuron(), this) == null)) {
						synapse = junction;
						break;
					}
				}
			}
			
			if (synapse == null) {
				
				@SuppressWarnings("unchecked")
				Context<Object> context = ContextUtils.getContext(this);
				context.add(newJunction);
			
				this.space.moveTo(newJunction, 
						getNewNeuriteSpacePos(pt.getX(), freeLocation.getX()), 
						getNewNeuriteSpacePos(pt.getY(), freeLocation.getY()), 
						getNewNeuriteSpacePos(pt.getZ(), freeLocation.getZ())); 
				this.grid.moveTo(newJunction, freeLocation.getX(), 
						freeLocation.getY(), freeLocation.getZ());

				this.neuritesNetwork.addEdge(newJunction, nextBud);
				
			} else {
				
				synapse.setSynapse(true);
				this.neuritesNetwork.addEdge(synapse, nextBud);
				this.neuralNetwork.addEdge(synapse.getNeuron(), this, 
						RandomHelper.nextDoubleFromTo(-1, 1));
				
			} // End if()
			
			return true;

		} // End if()
		
		return false;
		
	} // End of cellDendritesGrowthHandler

	
	/**
	 * 
	 * @return
	 */
	protected NeuriteJunction findJunctionWithLowestConcentration(
			final NeuriteJunction currentJunction, 
			NeuriteJunction candidateJunction, 
			double minConcentration) {
		
		GridPoint pt = this.grid.getLocation(currentJunction);
		
		Map<CellProductType, Double> externalConcentrations = 
				getExternalConcentrations(pt);
		double externalConcentration = 
				externalConcentrations.get(CellProductType.SAM);
		
		if (externalConcentration < minConcentration) {
			minConcentration = externalConcentration;
			candidateJunction = currentJunction;
		}
		
		if (currentJunction.getType() == NeuriteJunction.Type.AXON) {
			for (Object node : 
					this.neuritesNetwork.getSuccessors(currentJunction)) {
				NeuriteJunction nextJunction = (NeuriteJunction) node;
				if (nextJunction.getNeuron() == this) {
					candidateJunction = findJunctionWithLowestConcentration(
							nextJunction, candidateJunction, minConcentration);
				}
			}
		} else {
			for (Object node : 
					this.neuritesNetwork.getPredecessors(currentJunction)) {
				NeuriteJunction nextJunction = (NeuriteJunction) node;
				if (nextJunction.getNeuron() == this) {
					candidateJunction = findJunctionWithLowestConcentration(
							nextJunction, candidateJunction, minConcentration);
				}
			}
		}

		return candidateJunction;
		
	} // End of findJunctionWithLowestConcentration()
	
	
	/**
	 * 
	 */
	protected GridPoint findNeuriteFreeGridCell(
			final GridPoint currentLocation) {
		
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
		
		for (GridCell<NeuriteJunction> gridCell : gridCells) {
			
			boolean freeCell = true;
			
			if (gridCell.size() > 0) {
				for (NeuriteJunction junction : gridCell.items()) {
					if (junction.getNeuron() == this) {
						freeCell = false;
						break;
					}
				}
			}
			
			if (freeCell) {
				
				Map<CellProductType, Double> externalConcentrations = 
						getExternalConcentrations(gridCell.getPoint());
				double samConcentration = 
						externalConcentrations.get(CellProductType.SAM);
				if (samConcentration < minConcentration) {
					minConcentration = samConcentration;
					selectedGridCell = gridCell;
				}
				
			} // End if()
			
		} // End for()
		
		if (selectedGridCell == null) {
			return null;
		} else {
			GridPoint selectedGridCellPos = selectedGridCell.getPoint();
			return selectedGridCellPos;
		}
		
	} // End of findNeuriteFreeGridCell()
	
	
	/**
	 * 
	 * @param sourcePos
	 * @return
	 */
	protected double getNewNeuriteSpacePos(final int sourcePos, 
			final int targetPos) {
		
		int deltaPos = targetPos - sourcePos;
		
		if (deltaPos == 0) {
			return targetPos + RandomHelper.nextDoubleFromTo(0, 1);
		} else if (deltaPos < 0) {
			return targetPos + 0.9 + RandomHelper.nextDoubleFromTo(0, 0.1);
		} else {
			return targetPos + RandomHelper.nextDoubleFromTo(0, 0.1);
		}
		
	} // End of getNewNeuriteSpacePos()
	
	
	/**
	 * 
	 */
	protected GridPoint findGridCellForDendrite(
			final GridPoint currentLocation, final GridPoint axonLocation) {
		
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
		
		for (GridCell<NeuriteJunction> gridCell : gridCells) {
			
			GridPoint pt = gridCell.getPoint();
			
			if (isOnTheSameSide(currentLocation.getX(), axonLocation.getX(), 
					pt.getX()) || isOnTheSameSide(currentLocation.getY(),
							axonLocation.getY(), pt.getY())
							|| isOnTheSameSide(currentLocation.getZ(),
									axonLocation.getZ(), pt.getZ())) {
				continue;
			}
			
			boolean freeCell = true;
			
			if (gridCell.size() > 0) {
				for (NeuriteJunction junction : gridCell.items()) {
					if (junction.getNeuron() == this) {
						freeCell = false;
						break;
					}
				}
			}
			
			if (freeCell) {
				
				Map<CellProductType, Double> externalConcentrations = 
						getExternalConcentrations(pt);
				double samConcentration = 
						externalConcentrations.get(CellProductType.SAM);
				if (samConcentration < minConcentration) {
					minConcentration = samConcentration;
					selectedGridCell = gridCell;
				}
				
			} // End if()
			
		} // End for()
		
		if (selectedGridCell == null) {
			return null;
		} else {
			GridPoint selectedGridCellPos = selectedGridCell.getPoint();
			return selectedGridCellPos;
		}
		
	} // End of findNeuriteFreeGridCell()

	
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
