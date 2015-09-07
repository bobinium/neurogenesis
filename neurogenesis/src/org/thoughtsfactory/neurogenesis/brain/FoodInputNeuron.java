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
public class FoodInputNeuron extends InputNeuron {

	
	/**
	 * 
	 */
	public static final double CONVERTION_TO_ENERGY_EFFICIENCY = 0.1;
	
	
	//
	private final static Logger logger = 
			Logger.getLogger(FoodInputNeuron.class);	
		

	/**
	 * 
	 * @param newNeuralNetwork
	 */
	public FoodInputNeuron(final ContinuousSpace<Object> newSpace,
			final Grid<Object> newGrid,
			final RegulatoryNetwork newRegulatoryNetwork,
			final Network<Object> newNeuralNetwork,
			final Network<Object> newNeuritesNetwork,
			final Sensor newSensor) {
		
		super(newSpace, newGrid, newRegulatoryNetwork, 
				newNeuralNetwork, newNeuritesNetwork, newSensor);
		
		CellMembraneChannel foodChannel = 
				this.membraneChannels.get(CellProductType.FOOD);
		foodChannel.setOpenForOutput(true);
		
	} // End of FoodInputNeuron()
	

	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.RANDOM_PRIORITY)
	@Override
	public void step() {
		
		double newFoodConcentration = Math.tanh(this.sensor.getValue()
				* CONVERTION_TO_ENERGY_EFFICIENCY);
		logger.debug("Food photosynthetesis: " + newFoodConcentration);
		
		CellMembraneChannel foodChannel = 
				this.membraneChannels.get(CellProductType.FOOD);
		foodChannel.setConcentration(newFoodConcentration);
		
		super.step();
		
	} // End of step()
	
	
} // End of FoodInputNeuron class
