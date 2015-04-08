/**
 * 
 */
package neurogenesis;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;

/**
 * @author bob
 *
 */
public abstract class Neuron {

	protected final Network<Object> neuralNetwork;
	
	protected ContinuousSpace<Object> space;
	
	protected Grid<Object> grid;
	
	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	public Neuron(final Network<Object> newNeuralNetwork,
			final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid) {
		
		this.neuralNetwork = newNeuralNetwork;
		this.space = newSpace;
		this.grid = newGrid;
		
	} // End of Neuron()

	
} // End of Neuron class
