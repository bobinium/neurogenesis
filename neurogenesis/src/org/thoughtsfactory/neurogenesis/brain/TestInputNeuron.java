package org.thoughtsfactory.neurogenesis.brain;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.genetics.RegulatoryNetwork;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;


/**
 * A neuron that gets its activation level from an external sensor. This
 * implementation is used to demonstrate the control simulation test.
 * 
 * @author Robert Langlois
 */
public class TestInputNeuron extends InputNeuron {


	// INSTANCE VARIABLES ======================================================
    

    // Class logger for messages.
    private final static Logger logger = 
    		Logger.getLogger(TestInputNeuron.class);    
        

    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new instance of a test input neuron.
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
    public TestInputNeuron(final String newId,
            final ContinuousSpace<Object> newSpace,
            final Grid<Object> newGrid,
            final RegulatoryNetwork newRegulatoryNetwork,
            final Network<Object> newNeuralNetwork,
            final Network<Object> newNeuritesNetwork,
            final Sensor newSensor) {
        
        super(newId, newSpace, newGrid, newRegulatoryNetwork, 
                newNeuralNetwork, newNeuritesNetwork, newSensor);
        
    } // End of TestInputNeuron()
    

    // METHODS =================================================================
    
    
    /**
     * Update the activation state of this neuron.
     * 
     * This method is scheduled for execution at every tick of the simulation
     * with the same priority given to most cellular agents.
     */
    @ScheduledMethod(start = 1, interval = 1, 
            priority = ScheduleParameters.RANDOM_PRIORITY)
    @Override // InputNeuron
    public void update() {

        this.activation = this.sensor.getValue();

        logger.info("Test input neuron activation (" 
                + getSensor().getLabel() + ") : " + this.activation);
        
    } // End of update()
    

} // End of TestInputNeuron class
