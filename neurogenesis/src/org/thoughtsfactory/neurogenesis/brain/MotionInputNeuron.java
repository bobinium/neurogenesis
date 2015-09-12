/**
 * 
 */
package org.thoughtsfactory.neurogenesis.brain;

import org.thoughtsfactory.neurogenesis.genetics.RegulatoryNetwork;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;


/**
 * @author bob
 *
 */
public class MotionInputNeuron extends InputNeuron {

		
	/**
	 * 
	 * @param newNeuralNetwork
	 */
	public MotionInputNeuron(final String newId,
			final ContinuousSpace<Object> newSpace,
			final Grid<Object> newGrid,
			final RegulatoryNetwork newRegulatoryNetwork,
			final Network<Object> newNeuralNetwork,
			final Network<Object> newNeuritesNetwork,
			final Sensor newSensor) {
		
		super(newId, newSpace, newGrid, newRegulatoryNetwork, 
				newNeuralNetwork, newNeuritesNetwork, newSensor);
		
	} // End of MotionInputNeuron()
	

} // End of MotionInputNeuron class
