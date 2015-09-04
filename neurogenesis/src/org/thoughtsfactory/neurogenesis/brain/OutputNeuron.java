/**
 * 
 */
package org.thoughtsfactory.neurogenesis.brain;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;


/**
 * @author bob
 *
 */
public class OutputNeuron extends Neuron {

	
	/**
	 * 
	 * @param newNeuralNetwork
	 */
	public OutputNeuron(final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid,
			final RegulatoryNetwork newRegulatoryNetwork,
			final Network<Object> newNeuralNetwork,
			final Network<Object> newNeuritesNetwork) {

		super(newSpace, newGrid, newRegulatoryNetwork, 
				newNeuralNetwork, newNeuritesNetwork, false);

	} // End of OutputNeuron()
	
	
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
		
		// Bipolar sigmoid function.
		this.activation = (2 / (1 + Math.pow(Math.E, -1 * netInput))) - 1;
		
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
		
	} // End of calculateActivation()
	
	
	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.LAST_PRIORITY)
	public void step() {
		
		calculateActivation();

		CellMembraneChannel channel = 
				this.membraneChannels.get(CellProductType.SAM);
		channel.setConcentration(0.9);
		channel.setOpenForOutput(true);
		
		expelProductsToMatrix();
		
		this.cellGrowthRegulator = 0.05;
		initialiseNeurites(false, true);
		cellDendritesGrowthHandler();
		
	} // End of step()
	
	
} // End of OutputNeuron class
