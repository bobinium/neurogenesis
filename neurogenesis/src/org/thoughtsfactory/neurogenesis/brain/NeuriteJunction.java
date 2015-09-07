package org.thoughtsfactory.neurogenesis.brain;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * 
 * @author bob
 *
 */
public class NeuriteJunction implements Destroyable {

	/**
	 * 
	 * @author bob
	 *
	 */
	public enum Type {
		NEURON, DENDRITE, AXON
	}
	
	
	//
	private final static Logger logger = 
			Logger.getLogger(NeuriteJunction.class);	

	
	//
	private Type type;
	
	
	// The neuron to which this neurite junction belongs.
	private final Neuron neuron;
	
	
	//
	private int depth;
	
	
	//
	private boolean active = true;
	

	// The list of neurites that point to this node.
	private List<NeuriteJunction> predecessors = 
			new ArrayList<NeuriteJunction>();
	
	
	// The next neurite to which this one points.
	private NeuriteJunction successor = null;
	
	
	//
	private List<NeuriteJunction> synapses = new ArrayList<NeuriteJunction>();
	
	
	/**
	 * 
	 */
	public NeuriteJunction(final Type newType, 
			final Neuron newNeuron, final int newDepth) { 
		
		this.type = newType;
		this.neuron = newNeuron;
		this.depth = newDepth;
		
	} // End of NeuriteJunction()
	
	
	/**
	 * 
	 * @return
	 */
	public final Type getType() {
		return this.type;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final Neuron getNeuron() {
		return this.neuron;
	}
	
		
	/**
	 * 
	 * @return
	 */
	public final int getDepth() {
		return this.depth;
	}
	
	
	/**
	 * 
	 * @param newDepth
	 */
	public final void setDepth(final int newDepth) { 
		this.depth = newDepth;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final boolean isActive() {
		return this.active;
	}
	
	
	/**
	 * 
	 * @param newValue
	 */
	public final void setActive(final boolean newValue) {
		this.active = newValue;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final List<NeuriteJunction> getPredecessors() {
		return this.predecessors;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final NeuriteJunction getSuccessor() {
		return this.successor;
	}
	
	
	/**
	 * 
	 * @param newSuccessor
	 */
	public final void setSuccessor(final NeuriteJunction newSuccessor) {
		this.successor = newSuccessor;
	}


	/**
	 * 
	 * @return
	 */
	public final List<NeuriteJunction> getSynapses() {
		return this.synapses;
	}
	
	
	/**
	 * 
	 */
	public void destroy() {
		
		logger.info("Destroying neurite: " + this.type);
		
		//this.neuron.destroyNeuriteJunction(this);
		
	} // End of destroy()
	
	
} // End of NeuriteJunction class
