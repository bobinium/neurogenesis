package org.thoughtsfactory.neurogenesis.brain;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.genetics.RegulatoryNetwork;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;


/**
 * A neuron that forwards its activation level to an external actuator. This
 * implementation though uses a bipolar sigmoid function and is used to
 * demonstrate the control simulation test.
 * 
 * @author Robert Langlois
 */
public class TestOutputNeuron extends OutputNeuron {

    
    // INSTANCE VARIABLES ======================================================

    
    // Class logger for messages.
    private final static Logger logger = 
            Logger.getLogger(TestOutputNeuron.class);    

    
    // CONSTRUCTORS ============================================================

    
    /**
     * Creates a new instance of a test output neuron.
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
    public TestOutputNeuron(final String newId,
            final ContinuousSpace<Object> newSpace, 
            final Grid<Object> newGrid,
            final RegulatoryNetwork newRegulatoryNetwork,
            final Network<Object> newNeuralNetwork,
            final Network<Object> newNeuritesNetwork,
            final Actuator newActuator) {

        super(newId, newSpace, newGrid, newRegulatoryNetwork, 
                newNeuralNetwork, newNeuritesNetwork, newActuator);

    } // End of TestOutputNeuron()

    
    // METHODS =================================================================
    

    /**
     * Update the activation state of this neuron.
     * 
     * This method is scheduled for execution at every tick of the simulation
     * with a LAST priority, i.e. after most cellular agents.
     */
    @ScheduledMethod(start = 1, interval = 1, 
            priority = ScheduleParameters.LAST_PRIORITY)
    @Override // OutputNeuron
    public void update() {
        
        calculateActivation();
        this.actuator.setValue(this.activation);
        
    } // End of update()

    
    /**
     * Calculate the activation of the neuron using a bipolar sigmoid function. 
     */
    @Override // Neuron
    protected void calculateActivation() {
        
        double netInput = 0;
        
        for (RepastEdge<Object> inputEdge :    
                this.neuralNetwork.getInEdges(this)) {
            
            Neuron inputNeuron = (Neuron) inputEdge.getSource();
            
            logger.debug("Input neuron: activation = " 
                    + inputNeuron.getActivation() + ", weight = " 
                    + inputEdge.getWeight());

            netInput += inputNeuron.getActivation() * inputEdge.getWeight();
            
        } // End for(inputEdge)
        
        logger.debug("Net input: " + netInput);
        
        // Bipolar sigmoid function.
        this.activation = (2 / (1 + Math.pow(Math.E, -netInput))) - 1;
        logger.debug("Activation: " + this.activation);
        
    } // calculateActivation()


} // End of TestOutputNeuron class
