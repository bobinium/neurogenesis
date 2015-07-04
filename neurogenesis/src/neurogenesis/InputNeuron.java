/**
 * 
 */
package neurogenesis;

import java.util.List;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;


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
	public InputNeuron(final ContinuousSpace<Object> newSpace,
			final Grid<Object> newGrid,
			final Network<Object> newNeuralNetwork,
			final LightSensor newLightSensor) {
		
		super(newSpace, newGrid, newNeuralNetwork);
		
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
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<ExtracellularMatrix> nghCreator = 
				new GridCellNgh<ExtracellularMatrix>(this.grid,
						pt,	ExtracellularMatrix.class, 1, 1, 1);
		List<GridCell<ExtracellularMatrix>> gridCells = 
				nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
	} // End of step()
	
	
} // End of InputNeuron class
