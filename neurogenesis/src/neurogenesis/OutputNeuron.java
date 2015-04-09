/**
 * 
 */
package neurogenesis;

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

	private double activation;
	
	/**
	 * 
	 * @param newNeuralNetwork
	 */
	public OutputNeuron(final Network<Object> newNeuralNetwork,
			final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid) {

		super(newNeuralNetwork, newSpace, newGrid);

	} // End of OutputNeuron()
	
	
	/**
	 * 
	 */
	public double getActivation() {
		return this.activation;
	}
	
	
	/**
	 * 
	 */
	private void calculateActivation() {
		
		double netInput = 0;
		
		for (Object obj : this.neuralNetwork.getAdjacent(this)) {
			if (obj instanceof Neuron) {
				Neuron neuron = (Neuron) obj;
				RepastEdge<Object> edge = this.neuralNetwork.getEdge(neuron, this);
				netInput += neuron.getActivation() * edge.getWeight();
			}
		}
		
		// Bipolar sigmoid function.
		this.activation = (2 / (1 + Math.pow(Math.E, -1 * netInput))) - 1;
	}
	
	
	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		calculateActivation();
	}
	
} // End of OutputNeuron class
