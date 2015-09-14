package org.thoughtsfactory.neurogenesis.brain;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * A neurite junction is used to mark the presence of a neurite in a grid cell.
 * Neurite junctions are linked and represents either an axon's path or a 
 * particular path on a dendrites tree.
 * 
 * @author Robert Langlois
 */
public class NeuriteJunction {

	
    /**
     * The possible types of neurite junctions.
     */
    public enum Type {
        NEURON, DENDRITE, AXON
    }
    
    
    // INSTANCE VARIABLES ======================================================
    
    
    // Class logger for messages.
    @SuppressWarnings("unused")
    private final static Logger logger = 
    		Logger.getLogger(NeuriteJunction.class);    

    
    // The junction's type.
    private final Type type;
    
    
    // The neuron to which this neurite junction belongs.
    private final Neuron neuron;
    
    
    // The depth of the junction relative to its neuron.
    private int depth;
    
    
    // If the junction is active or in the process
    // of being removed or recycled.
    private boolean active = true;
    

    // The list of neurites that point to this junction.
    private final List<NeuriteJunction> predecessors = 
            new ArrayList<NeuriteJunction>();
    
    
    // The next neurite to which this one points.
    private NeuriteJunction successor = null;
    
    
    // Junctions from other neurons that connect from this junction.
    private final List<NeuriteJunction> synapses = 
    		new ArrayList<NeuriteJunction>();
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new neurite junction instance.
     * 
     * @param newType The junction's type.
     * @param newNeuron The neuron to which this junction belongs.
     * @param newDepth The depth of this junction.
     */
    public NeuriteJunction(final Type newType, 
            final Neuron newNeuron, final int newDepth) { 
        
        this.type = newType;
        this.neuron = newNeuron;
        this.depth = newDepth;
        
    } // End of NeuriteJunction()
    
    
    // METHODS =================================================================
    
    
    /**
     * Returns the junction's type.
     * 
     * @return One of {@link Type}.
     */
    public final Type getType() {
        return this.type;
    }
    
    
    /**
     * Returns the neuron that owns this junction.
     * 
     * @return The neuron to which this junction belongs.
     */
    public final Neuron getNeuron() {
        return this.neuron;
    }
    
        
    /**
     * Returns the depth of this junction along the path from its neuron.
     * 
     * @return An integer > 0 indicating the junction's depth.
     */
    public final int getDepth() {
        return this.depth;
    }
    
    
    /**
     * Sets the junction's depth.
     * 
     * @param newDepth The new depth of this junction.
     */
    public final void setDepth(final int newDepth) { 
        this.depth = newDepth;
    }
    
    
    /**
     * Returns whether this junction is active or in the process of being
     * removed or recycled.
     * 
     * @return {@code true} if the junction is active, {@code false} otherwise.
     */
    public final boolean isActive() {
        return this.active;
    }
    
    
    /**
     * Specify whether this junction is active or in the process of being
     * removed or recycled.
     * 
     * @param newValue {@code true} to specify that the junction is active, 
     *                 {@code false} to specify that its not.
     */
    public final void setActive(final boolean newValue) {
        this.active = newValue;
    }
    
    
    /**
     * Returns the list of junctions from the same neuron that points to the
     * current junction.
     *  
     * @return A list of neurite junctions.
     */
    public final List<NeuriteJunction> getPredecessors() {
        return this.predecessors;
    }
    
    
    /**
     * Returns the single junction to which this current junction points to. 
     * Successors in a dendrite tree are always up toward the neuron, while in 
     * an axon they are always away toward the tip.
     * 
     * @return A neurite junction.
     */
    public final NeuriteJunction getSuccessor() {
        return this.successor;
    }
    
    
    /**
     * Sets the neurite junction to which this junction points to.
     * 
     * @param newSuccessor The next junction to which this junction should
     *                     point.
     */
    public final void setSuccessor(final NeuriteJunction newSuccessor) {
        this.successor = newSuccessor;
    }


    /**
     * Returns the list of junctions from other neurons that connect from this
     * junction.
     * 
     * @return If the current junction is part of an axon, zero or more 
     *         junctions, or an empty list otherwise.
     */
    public final List<NeuriteJunction> getSynapses() {
        return this.synapses;
    }
    

} // End of NeuriteJunction class
