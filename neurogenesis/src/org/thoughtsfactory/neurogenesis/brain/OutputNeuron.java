/**
 * 
 */
package org.thoughtsfactory.neurogenesis.brain;

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
public class OutputNeuron extends Neuron {

	
	//
	private final Actuator actuator;
	
	
	/**
	 * 
	 * @param newNeuralNetwork
	 */
	public OutputNeuron(final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid,
			final RegulatoryNetwork newRegulatoryNetwork,
			final Network<Object> newNeuralNetwork,
			final Network<Object> newNeuritesNetwork,
			final Actuator newActuator) {

		super(newSpace, newGrid, newRegulatoryNetwork, 
				newNeuralNetwork, newNeuritesNetwork, false);

		this.actuator = newActuator;
		
		CellMembraneChannel foodChannel = 
				this.membraneChannels.get(CellProductType.FOOD);
		foodChannel.setOpenForInput(false);
		foodChannel.setConcentration(1);
		
	} // End of OutputNeuron()
	
	
	/**
	 * 
	 * @return
	 */
	public final Actuator getActuator() {
		return this.actuator;
	}
	
	
	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.LAST_PRIORITY)
	@Override
	public void step() {
		
		if (this.neuritesRoot == null) {
			if (!initialiseNeurites(true, true)) {
				throw new IllegalStateException(
						"Output neuron initialisation failed!");
			}
		}
		
		calculateActivation();
		this.actuator.setValue(this.activation);
		
		CellMembraneChannel channel = 
				this.membraneChannels.get(CellProductType.SAM);
		channel.setConcentration(0.9);		
		
		expelProductsToMatrix();
		
		this.cellGrowthRegulator = 0.9;
		cellDendritesGrowthHandler();
		
	} // End of step()
	
		
} // End of OutputNeuron class
