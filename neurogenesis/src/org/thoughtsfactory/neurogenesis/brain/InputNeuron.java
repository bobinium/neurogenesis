/**
 * 
 */
package org.thoughtsfactory.neurogenesis.brain;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.genetics.RegulatoryNetwork;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;


/**
 * A neuron that gets its activation level from an external sensor.
 * 
 * @author Robert Langlois
 */
public class InputNeuron extends Neuron {

    
    // INSTANCE VARIABLES ======================================================
    

    // Class logger for messages.
    private final static Logger logger = Logger.getLogger(InputNeuron.class);    
        

    /**
     *  The external sensor this neuron is hooked to.
     */
    protected final Sensor sensor;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new instance of an input neuron.
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
    public InputNeuron(final String newId,
            final ContinuousSpace<Object> newSpace,
            final Grid<Object> newGrid,
            final RegulatoryNetwork newRegulatoryNetwork,
            final Network<Object> newNeuralNetwork,
            final Network<Object> newNeuritesNetwork,
            final Sensor newSensor) {
        
        super(newId, newSpace, newGrid, newRegulatoryNetwork, 
                newNeuralNetwork, newNeuritesNetwork);
        
        this.sensor = newSensor;
        
    } // End of InputNeuron()
    

    // METHODS =================================================================
    
    
    /**
     * Returns the sensor this input neuron is hooked to.
     * 
     * @return The input sensor.
     */
    public final Sensor getSensor() {
        return this.sensor;
    }
    
    
    /**
     * Update the activation state of this neuron.
     * 
     * This method is scheduled for execution at every tick of the simulation
     * with the same priority given to most cellular agents.
     */
    @ScheduledMethod(start = 1, interval = 1, 
            priority = ScheduleParameters.RANDOM_PRIORITY)
    @Override // Neuron
    public void update() {

        // TODO: Put initialisation code in a scheduled method executed once.
        if (this.neuritesRoot == null) {
            // Input neurons have only an axon, no dendrites.
            if (!initialiseNeurites(true, false)) {
                throw new IllegalStateException(
                        "Input neuron initialisation failed!");
            }
        }
        
        this.activation = this.sensor.getValue();

        logger.info("Input neuron activation: " + this.activation);
        
        CellMembraneChannel samChannel = 
                this.membraneChannels.get(CellProductType.SAM);
        samChannel.setConcentration(0.9);
        
        expelProductsToMatrix();

        this.cellGrowthRegulator = 0.5;
        cellAxonGrowthHandler();
        
    } // End of update()
    

} // End of InputNeuron class
