package org.thoughtsfactory.neurogenesis.brain;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.genetics.RegulatoryNetwork;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;


/**
 * A type of input neuron that delivers food to the extracellular matrix 
 * according to the amount of input received from the sensor it is hooked to.
 * 
 * @author Robert Langlois
 */
public class FoodInputNeuron extends InputNeuron {

    
    // CONSTANTS ===============================================================
    

    /**
     * The activation to food conversion ratio.
     */
    public static final double CONVERTION_TO_ENERGY_EFFICIENCY = 0.1;
    

    // INSTANCE VARIABLES ======================================================
    
    
    // Class logger for messages. 
    private final static Logger logger = 
            Logger.getLogger(FoodInputNeuron.class);    
        

    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new instance of a food input neuron.
     * 
     * @param newId The label that identifies the new cell.
     * @param newSpace The continuous space from which this cell will be 
     *                 displayed. 
     * @param newGrid The grid that defines all locations in the virtual brain.
     * @param newRegulatoryNetwork The gene regulatory network that governs this 
     *                             cell.
     * @param newNeuralNetwork The neural network this neuron will participate 
     *                         in.
     * @param newNeuritesNetwork The network of all neurite instances.
     * @param newSensor The sensor that provides input to this neuron.
     */
    public FoodInputNeuron(final String newId,
            final ContinuousSpace<Object> newSpace,
            final Grid<Object> newGrid,
            final RegulatoryNetwork newRegulatoryNetwork,
            final Network<Object> newNeuralNetwork,
            final Network<Object> newNeuritesNetwork,
            final Sensor newSensor) {
        
        super(newId, newSpace, newGrid, newRegulatoryNetwork, 
                newNeuralNetwork, newNeuritesNetwork, newSensor);
        
        CellMembraneChannel foodChannel = 
                this.membraneChannels.get(CellProductType.FOOD);
        foodChannel.setOpenForOutput(true);
        
    } // End of FoodInputNeuron()
    

    // METHODS =================================================================
    
    
    /**
     * Update the activation state of this neuron. Also delivers food to the
     * extracellular matrix according to a preset rate.
     * 
     * This method is scheduled for execution at every tick of the simulation
     * with the same priority given to most cellular agents.
     */
    @ScheduledMethod(start = 1, interval = 1, 
            priority = ScheduleParameters.RANDOM_PRIORITY)
    @Override // InputNeuron
    public void update() {
        
        double newFoodConcentration = Math.tanh(this.sensor.getValue()
                * CONVERTION_TO_ENERGY_EFFICIENCY);
        logger.debug("Food synthetesis: " + newFoodConcentration);
        
        CellMembraneChannel foodChannel = 
                this.membraneChannels.get(CellProductType.FOOD);
        foodChannel.setConcentration(newFoodConcentration);
        
        super.update();
        
    } // End of update()
    

} // End of FoodInputNeuron class
