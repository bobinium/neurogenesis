package org.thoughtsfactory.neurogenesis;

import org.thoughtsfactory.neurogenesis.brain.ExtracellularMatrix;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;


/**
 * Interface that provides access to all simulation objects shared with agents.
 * 
 * @author Robert Langlois
 */
public interface SimulationContext {

    
    /**
     * Returns the continuous space from which all visual brain objects are
     * displayed.
     *
     * @return A Repast continuous space.
     */
    public ContinuousSpace<Object> getBrainSpace();
    
    
    /**
     * Returns the brain three dimensional Cartesian grid.
     * 
     * @return A Repast grid.
     */
    public Grid<Object> getBrainGrid();
    
    
    /**
     * Returns the network of all neurons.
     * 
     * @return A Repast network.
     */
    public Network<Object> getNeuralNetwork();

    
    /**
     * Returns the network of all neurites.
     * 
     * @return A Repast network.
     */
    public Network<Object> getNeuritesNetwork();
    

    /**
     * Returns the brain's extracellular matrix.
     * 
     * @return An extracellular matrix implementation.
     */
    public ExtracellularMatrix getExtracellularMatrix();    
    

} // End of SimulationContext interface
