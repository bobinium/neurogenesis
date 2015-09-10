package org.thoughtsfactory.neurogenesis;

import org.thoughtsfactory.neurogenesis.brain.ExtracellularMatrix;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;

/**
 * 
 * @author bob
 *
 */
public final class SimulationContextHolder implements SimulationContext {

	
	//
	private static final SimulationContextHolder context = 
			new SimulationContextHolder();
	
	
	//
	private ContinuousSpace<Object> brainSpace;
	
	
	//
	private Grid<Object> brainGrid;
	
	
	//
	private Network<Object> neuralNetwork;

	
	//
	private Network<Object> neuritesNetwork;
	
	
	//
	private ExtracellularMatrix extracellularMatrix;
	
	
	/**
	 * 
	 */
	private SimulationContextHolder() {
	}
	
	
	/**
	 * 
	 * @return
	 */
	@Override
	public ContinuousSpace<Object> getBrainSpace() {
		return this.brainSpace;
	}
	

	/**
	 * 
	 */
	@Override
	public Grid<Object> getBrainGrid() {
		return this.brainGrid;
	}
	
	
	/**
	 * 
	 */
	@Override
	public Network<Object> getNeuralNetwork() {
		return this.neuralNetwork;
	}
	
	
	/**
	 * 
	 */
	@Override
	public Network<Object> getNeuritesNetwork() {
		return this.neuritesNetwork;
	}
	
	
	/**
	 * 
	 * @return
	 */
	@Override
	public ExtracellularMatrix getExtracellularMatrix() {
		return this.extracellularMatrix;
	}
	
	
	/**
	 * 
	 * @param newBrainSpace
	 */
	public void setBrainSpace(final ContinuousSpace<Object> newBrainSpace) {
		this.brainSpace = newBrainSpace;
	}
	
	
	/**
	 * 
	 * @param newBrainGrid
	 */
	public void setBrainGrid(final Grid<Object> newBrainGrid) {
		this.brainGrid = newBrainGrid;
	}
	
	
	/**
	 * 
	 * @param newNeuralNetwork
	 */
	public void setNeuralNetwork(final Network<Object> newNeuralNetwork) {
		this.neuralNetwork = newNeuralNetwork;
	}
	

	/**
	 * 
	 * @param newNeuralNetwork
	 */
	public void setNeuritesNetwork(final Network<Object> newNeuritesNetwork) {
		this.neuritesNetwork = newNeuritesNetwork;
	}
	

	/**
	 * 
	 * @param newExtracellularMatrix
	 */
	public void setExtracellularMatrix(
			final ExtracellularMatrix newExtracellularMatrix) {
		this.extracellularMatrix = newExtracellularMatrix;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static SimulationContextHolder getInstance() {
		return context;
	}
	
	
} // End of SimulationContextHolder class
