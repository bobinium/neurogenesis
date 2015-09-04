/**
 * 
 */
package org.thoughtsfactory.neurogenesis.brain;

import java.util.Map;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.LightSensor;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;


/**
 * @author bob
 *
 */
public class InputNeuron extends Neuron {

	
	/**
	 * 
	 */
	public static final double LIGHT_TO_ENERGY_EFFICIENCY = 0.1;
	
	
	//
	private final static Logger logger = Logger.getLogger(InputNeuron.class);	
		

	//
	private final LightSensor lightSensor;
	
	
	/**
	 * 
	 * @param newNeuralNetwork
	 */
	public InputNeuron(final ContinuousSpace<Object> newSpace,
			final Grid<Object> newGrid,
			final RegulatoryNetwork newRegulatoryNetwork,
			final Network<Object> newNeuralNetwork,
			final Network<Object> newNeuritesNetwork,
			final LightSensor newLightSensor) {
		
		super(newSpace, newGrid, newRegulatoryNetwork, 
				newNeuralNetwork, newNeuritesNetwork, false);
		
		this.lightSensor = newLightSensor;
		
	} // End of InputNeuron()
	

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
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.RANDOM_PRIORITY)
	public void step() {
		
		this.activation = (1 / (1 + Math.pow(Math.E, -1 
				* this.lightSensor.getLightIntensity())));

		CellMembraneChannel channel = this.membraneChannels.get(CellProductType.SAM);
		channel.setConcentration(0.9);
		channel.setOpenForOutput(true);
		
		expelProductsToMatrix();

		// get the grid location of this Cell
		GridPoint pt = this.grid.getLocation(this);
		
		Map<CellProductType, Double> externalConcentrations = 
				getExternalConcentrations(pt);
		
		double externalConcentration = 
				externalConcentrations.get(CellProductType.FOOD);
				 
		double internalConcentration = 
				Math.tanh(this.lightSensor.getLightIntensity()
				* LIGHT_TO_ENERGY_EFFICIENCY);
		
		if (internalConcentration > externalConcentration) {
			
			double equilibriumConcentration = 
					(internalConcentration + externalConcentration) / 2;
		
			double diffusingConcentration =
					internalConcentration - equilibriumConcentration; 

			double newExternalConcentration = 
					externalConcentration + diffusingConcentration;
			logger.debug("New input food concentration: " 
					+ newExternalConcentration);

			externalConcentrations.put(CellProductType.FOOD, 
					newExternalConcentration);
		
		} // End if()
		
		this.cellGrowthRegulator = 0.1;
		initialiseNeurites(true, false);
		cellAxonGrowthHandler();
		
	} // End of step()
	
	
} // End of InputNeuron class
