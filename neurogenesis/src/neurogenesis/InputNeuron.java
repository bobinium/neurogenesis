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
public class InputNeuron extends Neuron {

	private final LightSensor lightSensor;
	
	/**
	 * 
	 * @param newNeuralNetwork
	 */
	public InputNeuron(final Network<Object> newNeuralNetwork,
			final LightSensor newLightSensor,
			final ContinuousSpace<Object> newSpace,
			final Grid<Object> newGrid) {
		
		super(newNeuralNetwork, newSpace, newGrid);
		
		this.lightSensor = newLightSensor;
		
	} // End of InputNeuron()
	

	/**
	 * 
	 * @return
	 */
	public Network<Object> getNeuralNetwork() {
		return this.neuralNetwork;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public LightSensor getLightSensor() {
		return this.lightSensor;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getActivation() {
		return this.lightSensor.getLightIntensity();
	}
	
	
	/**
	 * 
	 */
	public void step() {
		
	}
	
	
} // End of InputNeuron class
