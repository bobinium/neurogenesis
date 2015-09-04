package org.thoughtsfactory.neurogenesis;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;

/**
 * 
 * @author bob
 *
 */
public interface SimulationContext {

	
	/**
	 * 
	 * @return
	 */
	public ContinuousSpace<Object> getBrainSpace();
	
	
	/**
	 * 
	 * @return
	 */
	public Grid<Object> getBrainGrid();
	
	
	/**
	 * 
	 */
	public Network<Object> getNeuralNetwork();

	
	/**
	 * 
	 */
	public Network<Object> getNeuritesNetwork();
	
	
} // End of SimulationContext interface
