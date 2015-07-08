/**
 * 
 */
package neurogenesis;

import java.util.Map;

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

	public static final double LIGHT_TO_ENERGY_EFFICIENCY = 0.1;
	
	private final LightSensor lightSensor;
	
	
	/**
	 * 
	 * @param newNeuralNetwork
	 */
	public InputNeuron(final ContinuousSpace<Object> newSpace,
			final Grid<Object> newGrid,
			final Network<Object> newNeuralNetwork,
			final LightSensor newLightSensor) {
		
		super(newSpace, newGrid);
		
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
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.RANDOM_PRIORITY)
	public void step() {
		
		// get the grid location of this Cell
		GridPoint pt = this.grid.getLocation(this);
		
		for (Object obj : this.grid.getObjectsAt(pt.getX(), pt.getY(), pt.getZ())) {
			
			if (obj instanceof ExtracellularMatrix) {
				 ExtracellularMatrix matrix = (ExtracellularMatrix) obj;
				 double foodConcentration = Math.tanh(getActivation() * LIGHT_TO_ENERGY_EFFICIENCY);
				 System.out.println("New food concentration: " + foodConcentration);
				 
				 Map<GeneticElement, Double> concentrations = matrix.getConcentrations();
				 
				 double currentConcentration = 
						 (concentrations.get(ENERGY_REGULATOR) == null) 
						 ? 0 : concentrations.get(ENERGY_REGULATOR);
				 
				 concentrations.put(ENERGY_REGULATOR, 
						 currentConcentration + foodConcentration);
				 break;

			}
		}
		
	} // End of step()
	
	
} // End of InputNeuron class
