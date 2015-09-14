package org.thoughtsfactory.neurogenesis;

import org.thoughtsfactory.neurogenesis.brain.ExtracellularMatrix;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;


/**
 * A simulation context holder object holds a singleton that provides access to
 * all simulation objects shared with agents.
 *  
 * @author Robert Langlois
 */
public final class SimulationContextHolder implements SimulationContext {

    
    // CLASS VARIABLES =========================================================
    

    // The singleton instance.
    private static final SimulationContextHolder context = 
            new SimulationContextHolder();
    

    // INSTANCE VARIABLES ======================================================

    
    // The continuous space from which all visual brain objects are displayed.
    private ContinuousSpace<Object> brainSpace;
    
    
    // The brain three dimensional Cartesian grid.
    private Grid<Object> brainGrid;
    
    
    // The network of all neurons.
    private Network<Object> neuralNetwork;

    
    // The network of all neurites.
    private Network<Object> neuritesNetwork;
    
    
    // The brain's extracellular matrix.
    private ExtracellularMatrix extracellularMatrix;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Private constructor: prevents instantiation.
     */
    private SimulationContextHolder() {
    }

    
    // METHODS =================================================================
    
    
    /**
     * Returns the simulation context singleton.
     *  
     * @return The simulation context instance.
     */
    public static SimulationContextHolder getInstance() {
        return context;
    }
    

    /**
     * Returns the continuous space from which all visual brain objects are
     * displayed.
     *
     * @return A Repast continuous space.
     */
    @Override
    public ContinuousSpace<Object> getBrainSpace() {
        return this.brainSpace;
    }
    

    /**
     * Returns the brain three dimensional Cartesian grid.
     * 
     * @return A Repast grid.
     */
    @Override
    public Grid<Object> getBrainGrid() {
        return this.brainGrid;
    }
    
    
    /**
     * Returns the network of all neurons.
     * 
     * @return A Repast network.
     */
    @Override
    public Network<Object> getNeuralNetwork() {
        return this.neuralNetwork;
    }
    
    
    /**
     * Returns the network of all neurites.
     * 
     * @return A Repast network.
     */
    @Override
    public Network<Object> getNeuritesNetwork() {
        return this.neuritesNetwork;
    }
    
    
    /**
     * Returns the brain's extracellular matrix.
     * 
     * @return An extracellular matrix implementation.
     */
    @Override
    public ExtracellularMatrix getExtracellularMatrix() {
        return this.extracellularMatrix;
    }
    
    
    /**
     * Sets the continuous space from which all visual brain objects are
     * displayed.
     *
     * @param newBrainSpace A Repast continuous space.
     */
    public void setBrainSpace(final ContinuousSpace<Object> newBrainSpace) {
        this.brainSpace = newBrainSpace;
    }
    
    
    /**
     * Sets the brain three dimensional Cartesian grid.
     * 
     * @param newBrainGrid A Repast grid.
     */
    public void setBrainGrid(final Grid<Object> newBrainGrid) {
        this.brainGrid = newBrainGrid;
    }
    
    
    /**
     * Sets the network of all neurons.
     * 
     * @param newNeuralNetwork A Repast network.
     */
    public void setNeuralNetwork(final Network<Object> newNeuralNetwork) {
        this.neuralNetwork = newNeuralNetwork;
    }
    

    /**
     * Sets the network of all neurites.
     * 
     * @param newNeuritesNetwork A Repast network.
     */
    public void setNeuritesNetwork(final Network<Object> newNeuritesNetwork) {
        this.neuritesNetwork = newNeuritesNetwork;
    }
    

    /**
     * Sets the brain's extracellular matrix.
     * 
     * @param newExtracellularMatrix An extracellular matrix implementation.
     */
    public void setExtracellularMatrix(
            final ExtracellularMatrix newExtracellularMatrix) {
        this.extracellularMatrix = newExtracellularMatrix;
    }
    
    
} // End of SimulationContextHolder class
