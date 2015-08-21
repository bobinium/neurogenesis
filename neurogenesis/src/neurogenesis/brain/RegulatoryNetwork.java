package neurogenesis.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RegulatoryNetwork {

	/**
	 * 
	 */
	public static final double DELTA_INTEGRATION_RATE = 0.2;
	
	
	//
	private RegulatoryUnit[] regulatoryUnits;;
	
	//
	private GeneticElement[] inputElements;
	
	//
	private GeneticElement[] outputElements;
	
	// Concentration of trans products.
	private Map<GeneticElement, Double> networkConcentrations = 
			new HashMap<GeneticElement, Double>();
	
	
	/**
	 * 
	 */
	private RegulatoryNetwork() {
		
	}
	
	
	/**
	 * 
	 * @param newRegulatoryUnits
	 * @param newOutputElements
	 */
	public RegulatoryNetwork(final RegulatoryUnit[] newRegulatoryUnits, 
			final GeneticElement[] newInputElements,
			final GeneticElement[] newOutputElements) {
		
		this.regulatoryUnits = newRegulatoryUnits;
		this.inputElements = newInputElements;
		this.outputElements = newOutputElements;
		
	} // End of RegulatoryNetwork()
	
	
	/**
	 * 
	 * @return
	 */
	public GeneticElement[] getInputElements() {
		return this.inputElements.clone();
	}
	
	
	/**
	 * 
	 * @return
	 */
	public GeneticElement[] getOutputElements() {
		return this.outputElements.clone();
	}
	
	
    /**
	 * 
	 * @param inputElements
	 */
	public void updateNetwork(
			final Map<GeneticElement, Double> inputConcentrations) {
		
		Map<GeneticElement, Double> currentConcentrations = 
				new HashMap<GeneticElement, Double>();

		// Copy all current concentrations so every regulatory unit receives
		// concentrations from the PREVIOUS tick.
		currentConcentrations.putAll(this.networkConcentrations);
		
		currentConcentrations.putAll(inputConcentrations);
		
		int count = 0;
		
		for (RegulatoryUnit regulatoryUnit : this.regulatoryUnits) {
			
			regulatoryUnit.updateConcentrations(
					currentConcentrations, this.networkConcentrations);
			System.out.println("Unit #" + ++count 
					+ " concentration: " + regulatoryUnit.getConcentration());
			
		} // End for()
				
	} // End of updateNetwork()
	
	
	/**
	 * 
	 * @param outputElement
	 * @param currentConcentration
	 * @return
	 */
	public double calculateOutputConcentrationDelta(
			final GeneticElement outputElement, 
			final double currentConcentration) {
	
		double activation = 0;
			
		for (GeneticElement transElement : this.networkConcentrations.keySet()) {
				
			double affinity = 
					transElement.getAffinityForCisElement(outputElement); 
				
			activation += affinity 
					* this.networkConcentrations.get(transElement);
			
		} // End of for() trans elements

		System.out.println("Activation (output): " + activation);
			
		double deltaConcentration = Math.tanh(activation / 2) 
					* ((activation >= 0) ? 1 - currentConcentration 
							: currentConcentration) * DELTA_INTEGRATION_RATE;
		System.out.println("Delta concentration (output): " 
							+ deltaConcentration);
		
		return deltaConcentration;
		
	} // End of calculateOutputConcentrationDelta()

	
	/**
	 * 
	 * @param outputElement
	 * @param currentConcentration
	 * @return
	 */
	public double calculateEnergyConcentrationDelta(
			final GeneticElement outputElement, 
			final double currentConcentration) {
	
		double activation = 0;
			
		for (GeneticElement transElement : this.networkConcentrations.keySet()) {
				
			double affinity = Math.abs(transElement
					.getAffinityForCisElement(outputElement)); 
				
			activation += affinity 
					* this.networkConcentrations.get(transElement);
			
		} // End of for() trans elements

		System.out.println("Activation (output): " + activation);
		
		// cost
		double deltaConcentration = -1 * activation 
				/ (Math.sqrt(10 * 10 + 10 * 10) 
						* this.networkConcentrations.size());
		System.out.println("Delta concentration energy: " + deltaConcentration);
		
		return deltaConcentration;
		
	} // End of calculateEnergyConcentrationDelta()

	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public RegulatoryNetwork clone() {
		
		RegulatoryNetwork newNetwork = new RegulatoryNetwork();

		List<RegulatoryUnit> newUnits = new ArrayList<RegulatoryUnit>();
		for (RegulatoryUnit unit : this.regulatoryUnits) {
			newUnits.add(unit.clone());
		}
		
		List<GeneticElement> newInputElements = new ArrayList<GeneticElement>();
		for (GeneticElement element : this.inputElements) {
			newInputElements.add(element.clone());
		}
		
		List<GeneticElement> newOutputElements = new ArrayList<GeneticElement>();
		for (GeneticElement element : this.outputElements) {
			newInputElements.add(element.clone());
		}
		
		newNetwork.regulatoryUnits = 
				newUnits.toArray(new RegulatoryUnit[newUnits.size()]);
		newNetwork.networkConcentrations = 
				(Map<GeneticElement, Double>) ((HashMap<GeneticElement, Double>) 
						this.networkConcentrations).clone();
		newNetwork.inputElements = newInputElements.toArray(
				new GeneticElement[newInputElements.size()]);
		newNetwork.outputElements = newOutputElements.toArray(
				new GeneticElement[newOutputElements.size()]);
		
		return newNetwork;
		
	}
	
} // End of RegulatoryNetwork class
