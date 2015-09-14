package org.thoughtsfactory.neurogenesis.brain;

import org.thoughtsfactory.neurogenesis.Configuration;
import org.thoughtsfactory.neurogenesis.SimulationContext;
import org.thoughtsfactory.neurogenesis.SimulationContextHolder;
import org.thoughtsfactory.neurogenesis.genetics.GenomeFactory;


/**
 * Factory for creating instances of cells ready to be deployed in the 
 * simulation.
 * 
 * @author Robert Langlois
 */
public class CellFactory {


    /**
     * Get a new instance of an undifferentiated cell.
     * 
     * @param The ID or label to associate with the new cell.
     * @return A new {@link UndifferentiatedCell} instance.
     */
    public static UndifferentiatedCell getNewUndifferentiatedCell(
            final String newId) {
        
        SimulationContext simulationContext = 
                SimulationContextHolder.getInstance();
        Configuration config = Configuration.getInstance();
        
        GenomeFactory genomeFactory = new GenomeFactory();
        
        UndifferentiatedCell newCell = new UndifferentiatedCell(newId, 
                simulationContext.getBrainSpace(),
                simulationContext.getBrainGrid(),
                genomeFactory.getNewGenome(config.getGenomeSize()), 
                        config.isCellAdhesionEnabled());
        
        return newCell;
        
    } // End of getNewUndifferentiatedCell()
    

    /**
     * Get a new neuron instance.
     * 
     * @param The ID or label to associate with the new cell.
     * @return A new {@link Neuron} instance.
     */
    public static Neuron getNewNeuron(final String newId) {
        
        SimulationContext simulationContext = 
                SimulationContextHolder.getInstance();
        Configuration config = Configuration.getInstance();
        
        GenomeFactory genomeFactory = new GenomeFactory();
        
        Neuron newCell = new Neuron(newId,
                simulationContext.getBrainSpace(), 
                simulationContext.getBrainGrid(),
                genomeFactory.getNewGenome(config.getGenomeSize()), 
                simulationContext.getNeuralNetwork(), 
                simulationContext.getNeuritesNetwork());
        
        return newCell;
        
    } // End of getNewNeuron()
    

    /**
     * Get a new neuron instance from an existing undifferentiated cell.
     * 
     * @param undifferentiatedCell The undifferentiated cell that will serve as
     *                             the template for the new neuron.
     * @return A new {@link Neuron} instance.
     */
    public static Neuron getNeuronFrom(
            final UndifferentiatedCell undifferentiatedCell) {
        
        SimulationContext simulationContext = 
                SimulationContextHolder.getInstance();

        Neuron neuron = new Neuron(undifferentiatedCell.getId() + ",N", 
                undifferentiatedCell, simulationContext.getNeuralNetwork(), 
                simulationContext.getNeuritesNetwork());
        
        return neuron;
        
    } // End of getNeuronFrom()
    

} // End of CellFactory class
