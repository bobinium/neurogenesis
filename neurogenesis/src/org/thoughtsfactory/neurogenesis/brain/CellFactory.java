package org.thoughtsfactory.neurogenesis.brain;

import org.thoughtsfactory.neurogenesis.Configuration;
import org.thoughtsfactory.neurogenesis.SimulationContext;
import org.thoughtsfactory.neurogenesis.SimulationContextHolder;
import org.thoughtsfactory.neurogenesis.genetics.GenomeFactory;


/**
 * 
 * @author bob
 *
 */
public class CellFactory {


	/**
	 * 
	 * @return
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
	 * 
	 * @return
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
				simulationContext.getNeuritesNetwork(), false);
		
		return newCell;
		
	} // End of getNewNeuron()
	

	/**
	 * 
	 * @param undifferentiatedCell
	 * @return
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
