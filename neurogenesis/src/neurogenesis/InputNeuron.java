/**
 * 
 */
package neurogenesis;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.graph.Network;

/**
 * @author bob
 *
 */
public class InputNeuron {

	private final Network<Object> neuralNetwork;
	
	private final LightSensor lightSensor;
	
	/**
	 * 
	 * @param newNeuralNetwork
	 */
	public InputNeuron(final Network<Object> newNeuralNetwork,
			final LightSensor newLightSensor) {
		
		this.neuralNetwork = newNeuralNetwork;
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
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		
	}
}
