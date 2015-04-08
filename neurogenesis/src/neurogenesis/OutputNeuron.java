/**
 * 
 */
package neurogenesis;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
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
	public OutputNeuron(final Network<Object> newNeuralNetwork,
			final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid) {

		super(newNeuralNetwork, newSpace, newGrid);

	} // End of OutputNeuron()
	
	
	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		
	}
	
} // End of OutputNeuron class
