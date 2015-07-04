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
public abstract class Neuron extends Cell {

	
	protected final Network<Object> neuralNetwork;
	
	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	protected Neuron(final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid,
			final Network<Object> newNeuralNetwork) {
		
		super(newSpace, newGrid);
		
		this.neuralNetwork = newNeuralNetwork;
		
	} // End of Neuron()

	
	/**
	 * 
	 * @return
	 */
	public abstract double getActivation();
	
	
} // End of Neuron class
