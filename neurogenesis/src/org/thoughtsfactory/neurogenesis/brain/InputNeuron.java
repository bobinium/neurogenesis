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
public class InputNeuron extends Neuron {

	
	//
	private final static Logger logger = Logger.getLogger(InputNeuron.class);	
		

	//
	protected final Sensor sensor;
	
	
	/**
	 * 
	 * @param newNeuralNetwork
	 */
	public InputNeuron(final ContinuousSpace<Object> newSpace,
			final Grid<Object> newGrid,
			final RegulatoryNetwork newRegulatoryNetwork,
			final Network<Object> newNeuralNetwork,
			final Network<Object> newNeuritesNetwork,
			final Sensor newSensor) {
		
		super(newSpace, newGrid, newRegulatoryNetwork, 
				newNeuralNetwork, newNeuritesNetwork, false);
		
		this.sensor = newSensor;
		
	} // End of InputNeuron()
	

	/**
	 * 
	 * @return
	 */
	public final Sensor getSensor() {
		return this.sensor;
	}
	
	
	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.RANDOM_PRIORITY)
	@Override
	public void step() {

		if (this.neuritesRoot == null) {
			if (!initialiseNeurites(true, false)) {
				throw new IllegalStateException(
						"Input neuron initialisation failed!");
			}
		}
		
		this.activation = this.sensor.getValue();

		logger.info("Input neuron activation: " + this.activation);
		
		CellMembraneChannel samChannel = 
				this.membraneChannels.get(CellProductType.SAM);
		samChannel.setConcentration(0.9);
		
		expelProductsToMatrix();

		this.cellGrowthRegulator = 0.5;
		cellAxonGrowthHandler();
		
	} // End of step()
	
	
} // End of InputNeuron class
