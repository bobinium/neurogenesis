/**
 * 
 */
package org.thoughtsfactory.neurogenesis.brain;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.genetics.RegulatoryNetwork;

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
public class TestOutputNeuron extends OutputNeuron {

	
	//
	private final static Logger logger = 
			Logger.getLogger(TestOutputNeuron.class);	

	
	/**
	 * 
	 * @param newNeuralNetwork
	 */
	public TestOutputNeuron(final String newId,
			final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid,
			final RegulatoryNetwork newRegulatoryNetwork,
			final Network<Object> newNeuralNetwork,
			final Network<Object> newNeuritesNetwork,
			final Actuator newActuator) {

		super(newId, newSpace, newGrid, newRegulatoryNetwork, 
				newNeuralNetwork, newNeuritesNetwork, newActuator);

	} // End of TestOutputNeuron()
	
	
	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.LAST_PRIORITY)
	@Override
	public void step() {
		
		calculateActivation();
		this.actuator.setValue(this.activation);
		
	} // End of step()

	
	/**
	 * 
	 */
	@Override
	protected void calculateActivation() {
		
		double netInput = 0;
		
		for (RepastEdge<Object> inputEdge :	
				this.neuralNetwork.getInEdges(this)) {
			
			Neuron inputNeuron = (Neuron) inputEdge.getSource();
			
			logger.debug("Input neuron: activation = " 
					+ inputNeuron.getActivation() + ", weight = " 
					+ inputEdge.getWeight());

			netInput += inputNeuron.getActivation() * inputEdge.getWeight();
			
		} // End for(inputEdge)
		
		logger.debug("Net input: " + netInput);
		
		// Bipolar sigmoid function.
		this.activation = (2 / (1 + Math.pow(Math.E, -netInput))) - 1;
		logger.debug("Activation: " + this.activation);
		
	} // calculateActivation()

		
} // End of TestOutputNeuron class
