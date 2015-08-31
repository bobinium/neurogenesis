package neurogenesis.brain;


public class NeuriteJunction {

	public enum Type {
		NEURON, DENDRITE, AXON
	}
	
	//
	private Type type;
	
	
	//
	private final Neuron neuron;
	
	
	//
	private boolean synapse = false;
	
	
	//
	private final int depth;
	
	
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
	public final boolean isSynapse() {
		return this.synapse;
	}
	
	
	/**
	 * 
	 * @param newValue
	 */
	public final void setSynapse(final boolean newValue) {
		this.synapse = newValue;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final int getDepth() {
		return this.depth;
	}
	
	
} // End of NeuriteJunction class
