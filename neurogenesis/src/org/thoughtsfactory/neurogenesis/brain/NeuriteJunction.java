package org.thoughtsfactory.neurogenesis.brain;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author bob
 *
 */
public class NeuriteJunction {

	/**
	 * 
	 * @author bob
	 *
	 */
	public enum Type {
		NEURON, DENDRITE, AXON
	}
	
	
	//
	private Type type;
	
	
	// The neuron to which this neurite junction belongs.
	private final Neuron neuron;
	
	
	//
	//private boolean synapse = false;
	
	
	//
	private final int depth;
	
	
	//
	public boolean active = true;
	

	// The list of neurites that point to this node.
	private List<NeuriteJunction> predecessors = 
			new ArrayList<NeuriteJunction>();
	
	
	// The next neurite to which this one points.
	private NeuriteJunction successor = null;
	
	
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
//	public final boolean isSynapse() {
//		
//		if (this.type == Type.AXON) {
//			
//			boolean synapse = false;
//		
//			for (Object obj : this.neuron.neuritesNetwork.getSuccessors(this)) {
//				NeuriteJunction junction = (NeuriteJunction) obj;
//				if (junction.getNeuron() != this.neuron) {
//					synapse = true;
//					break;
//				}
//			}
//				
//			return synapse;
//			
//		} else {
//		
//			return false;
//			
//		} // End if()
//			
//	} // End of isSynapse()
	
	
	/**
	 * 
	 * @param newValue
	 */
//	public final void setSynapse(final boolean newValue) {
//		this.synapse = newValue;
//	}
	
	
	/**
	 * 
	 * @return
	 */
	public final int getDepth() {
		return this.depth;
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
	public void setSuccessor(final NeuriteJunction newSuccessor) {
		this.successor = newSuccessor;
	}


} // End of NeuriteJunction class
