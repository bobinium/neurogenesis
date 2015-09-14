package org.thoughtsfactory.neurogenesis.brain;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.genetics.RegulatoryNetwork;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;


/**
 * A neuron that forwards its activation level to an external actuator.
 * 
 * @author Robert Langlois
 */
public class OutputNeuron extends Neuron {

    
    // INSTANCE VARIABLES ======================================================

    
    // Class logger for messages.
    @SuppressWarnings("unused")
    private final static Logger logger = Logger.getLogger(OutputNeuron.class);    

    
    /**
     *  The external actuator this neuron is hooked to.
     */
    protected final Actuator actuator;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new instance of an output neuron.
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
     * @param newActuator The actuator that gets the output of this neuron.
     */
    public OutputNeuron(final String newId,
            final ContinuousSpace<Object> newSpace, 
            final Grid<Object> newGrid,
            final RegulatoryNetwork newRegulatoryNetwork,
            final Network<Object> newNeuralNetwork,
            final Network<Object> newNeuritesNetwork,
            final Actuator newActuator) {

        super(newId, newSpace, newGrid, newRegulatoryNetwork, 
                newNeuralNetwork, newNeuritesNetwork);

        this.actuator = newActuator;
        
        CellMembraneChannel foodChannel = 
                this.membraneChannels.get(CellProductType.FOOD);
        foodChannel.setOpenForInput(false);
        foodChannel.setConcentration(1);
        
    } // End of OutputNeuron()
    
    
    // METHODS =================================================================
    
    
    /**
     * Returns the actuator that gets the output of this neuron.
     * 
     * @return The output actuator.
     */
    public final Actuator getActuator() {
        return this.actuator;
    }
    
    
    /**
     * Update the activation state of this neuron.
     * 
     * This method is scheduled for execution at every tick of the simulation
     * with a LAST priority, i.e. after most cellular agents.
     */
    @ScheduledMethod(start = 1, interval = 1, 
            priority = ScheduleParameters.LAST_PRIORITY)
    @Override
    public void update() {
        
        // TODO: Put initialisation code in a scheduled method executed once.
        if (this.neuritesRoot == null) {
            if (!initialiseNeurites(true, true)) {
                throw new IllegalStateException(
                        "Output neuron initialisation failed!");
            }
        }
        
        calculateActivation();
        this.actuator.setValue(this.activation);
        
        CellMembraneChannel channel = 
                this.membraneChannels.get(CellProductType.SAM);
        channel.setConcentration(0.9);        
        
        expelProductsToMatrix();
        
        this.cellGrowthRegulator = 0.9;
        cellDendritesGrowthHandler();
        
    } // End of update()
    

} // End of OutputNeuron class
