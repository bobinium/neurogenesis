package org.thoughtsfactory.neurogenesis.brain;

import org.thoughtsfactory.neurogenesis.genetics.RegulatoryNetwork;

import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;


/**
 * A type of input neuron that gets its activation level from a motion sensor. 
 * Actually this class is no different than a standard input neuron; it was
 * created only for the purpose of distinguishing between neuron types in the 3D
 * display of the simulation runtime environment.
 * 
 * @author Robert Langlois
 */
public class MotionInputNeuron extends InputNeuron {

        
    /**
     * Creates a new instance of a motion input neuron.
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
    public MotionInputNeuron(final String newId,
            final ContinuousSpace<Object> newSpace,
            final Grid<Object> newGrid,
            final RegulatoryNetwork newRegulatoryNetwork,
            final Network<Object> newNeuralNetwork,
            final Network<Object> newNeuritesNetwork,
            final Sensor newSensor) {
        
        super(newId, newSpace, newGrid, newRegulatoryNetwork, 
                newNeuralNetwork, newNeuritesNetwork, newSensor);
        
    } // End of MotionInputNeuron()


} // End of MotionInputNeuron class
