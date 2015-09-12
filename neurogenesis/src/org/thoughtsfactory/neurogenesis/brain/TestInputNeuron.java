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
import repast.simphony.space.grid.Grid;


/**
 * @author bob
 *
 */
public class TestInputNeuron extends InputNeuron {

	
	//
	private final static Logger logger = Logger.getLogger(TestInputNeuron.class);	
		

	/**
	 * 
	 * @param newNeuralNetwork
	 */
	public TestInputNeuron(final String newId,
			final ContinuousSpace<Object> newSpace,
			final Grid<Object> newGrid,
			final RegulatoryNetwork newRegulatoryNetwork,
			final Network<Object> newNeuralNetwork,
			final Network<Object> newNeuritesNetwork,
			final Sensor newSensor) {
		
		super(newId, newSpace, newGrid, newRegulatoryNetwork, 
				newNeuralNetwork, newNeuritesNetwork, newSensor);
		
	} // End of TestInputNeuron()
	

	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.RANDOM_PRIORITY)
	@Override
	public void step() {

		this.activation = this.sensor.getValue();

		logger.info("Test input neuron activation (" 
				+ getSensor().getLabel() + ") : " + this.activation);
		
	} // End of step()
	
	
} // End of TestInputNeuron class
