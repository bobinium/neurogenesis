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
public class MotorNeuron {

	private final Network<Object> neuralNetwork;
	
	public MotorNeuron(final Network<Object> newNeuralNetwork) {
		this.neuralNetwork = newNeuralNetwork;
	}
	
	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void step() {
		
	}
}
