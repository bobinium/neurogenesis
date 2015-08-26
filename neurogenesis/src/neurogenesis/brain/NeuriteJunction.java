package neurogenesis.brain;

public class NeuriteJunction {

	public enum Type {
		DENDRITE, AXON
	}
	
	//
	private final Type type;
	
	
	//
	private final Neuron neuron;
	
	
	//
	private boolean synapse = false;
	
	
	/**
	 * 
	 */
	public NeuriteJunction(final Type newType, final Neuron newNeuron) {
		
		this.type = newType;
		this.neuron = newNeuron;
		
	} // End of NeuriteJunction()
	
	
	/**
	 * 
	 * @return
	 */
	public Type getType() {
		return this.type;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public Neuron getNeuron() {
		return this.neuron;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public boolean isSynapse() {
		return this.synapse;
	}
	
	
	/**
	 * 
	 * @param newValue
	 */
	public void setSynapse(final boolean newValue) {
		this.synapse = newValue;
	}
	
	
} // End of NeuriteJunction
