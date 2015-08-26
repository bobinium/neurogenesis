package neurogenesis.brain;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;

public class CellFactory {

	//
	private static ContinuousSpace<Object> continuousSpace;
	
	//
	private static Grid<Object> grid;
	
	//
	private static Network<Object> neuralNetwork;
	
	//
	private static Network<Object> neuritesNetwork;
	
	
	/**
	 * 
	 * @return
	 */
	public static final ContinuousSpace<Object> getContinuousSpace() {
		return continuousSpace;
	}
	
	
	/**
	 * 
	 * @param newContinuousSpace
	 */
	public static final synchronized void setContinuousSpace(
			final ContinuousSpace<Object> newContinuousSpace) {
		continuousSpace = newContinuousSpace;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static final Grid<Object> getGrid() {
		return grid;
	}
	
	
	/**
	 * 
	 * @param newGrid
	 */
	public static final synchronized void setGrid(final Grid<Object> newGrid) {
		grid = newGrid;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static final Network<Object> getNeuralNetwork() {
		return neuralNetwork;
	}
	
	
	/**
	 * 
	 */
	public static final synchronized void setNeuralNetwork(
			final Network<Object> newNeuralNetwork) {
		neuralNetwork = newNeuralNetwork;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static final Network<Object> getNeuritesNetwork() {
		return neuritesNetwork;
	}
	
	
	/**
	 * 
	 * @param newNeuritesNetwork
	 */
	public static final synchronized void setNeuritesNetwork(
			final Network<Object> newNeuritesNetwork) {
		neuritesNetwork = newNeuritesNetwork;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static UndifferentiatedCell getNewUndifferentiatedCell() {
		
		GenomeFactory genomeFactory = new GenomeFactory();
		
		UndifferentiatedCell newCell = 
				new UndifferentiatedCell(continuousSpace, grid, 
						genomeFactory.getNewGenome());
		
		return newCell;
		
	} // End of getNewUndifferentiatedCell()
	

	/**
	 * 
	 * @param undifferentiatedCell
	 * @return
	 */
	public static Neuron getNeuronFrom(
			final UndifferentiatedCell undifferentiatedCell) {
		
		Neuron neuron = new Neuron(undifferentiatedCell,
				neuralNetwork, neuritesNetwork);
		
		return neuron;
		
	} // End of getNeuronFrom()
	
	
} // End of CellFactory class
