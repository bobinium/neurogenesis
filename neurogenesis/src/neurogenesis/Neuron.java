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
public class Neuron extends RegulatedCell {

	protected static Network<Object> neuralNetwork;
	
	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	protected Neuron(final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid) {
//			final Network<Object> newNeuralNetwork) {
		
		super(newSpace, newGrid);
		
//		this.neuralNetwork = newNeuralNetwork;
		
	} // End of Neuron()

	
	/**
	 * 
	 * @return
	 */
	public double getActivation() {
		return 0;
	};
	
	
} // End of Neuron class
