package neurogenesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RegulatoryNetwork {

	//
	private RegulatoryUnit[] regulatoryUnits;;
	
	//
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
	public RegulatoryNetwork(final RegulatoryUnit[] newRegulatoryUnits) {
		
		this.regulatoryUnits = newRegulatoryUnits;	
		
	} // End of RegulatoryNetwork()
	
	
	/**
	 * 
	 * @param inputElements
	 */
	public double updateNetwork(
			final Map<GeneticElement, Double> externalConcentrations) {
		
		Map<GeneticElement, Double> currentConcentrations = new HashMap<GeneticElement, Double>();
		Map<GeneticElement, Double> newConcentrations = new HashMap<GeneticElement, Double>();

		currentConcentrations.putAll(this.networkConcentrations);
		currentConcentrations.putAll(externalConcentrations);
		
		int count = 0;
		double energyCost = 0;
		
		for (RegulatoryUnit regulatoryUnit : this.regulatoryUnits) {
			
			energyCost += regulatoryUnit
					.updateConcentrations(currentConcentrations, newConcentrations);
			System.out.println("Unit #" + ++count 
					+ " concentration: " + regulatoryUnit.getConcentration());
			
		} // End for()
		
		// Update network concentrations.
		for (GeneticElement transElement : newConcentrations.keySet()) {
			this.networkConcentrations.put(transElement, 
					newConcentrations.get(transElement));
		}
		
		return energyCost;
		
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
				
			double affinityDeltaX = 
					transElement.getAffinityX() - outputElement.getAffinityX();
			double affinityDeltaY = 
					transElement.getAffinityY() - outputElement.getAffinityY();
					
			double affinity = Math.sqrt(Math.pow(affinityDeltaX, 2) 
					+ Math.pow(affinityDeltaY, 2));
			System.out.println("Affinity (output): " + affinity);
				
			activation += affinity 
					* this.networkConcentrations.get(transElement) 
					* transElement.getSign();
			
		} // End of for() trans elements

		System.out.println("Activation (output): " + activation);
			
		double deltaConcentration = Math.tanh(activation / 2) - currentConcentration;
		System.out.println("Delta concentration (output): " + deltaConcentration);
					
		return deltaConcentration;
		
	} // End of calculateOutputConcentrationDelta()

	
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
		
		newNetwork.regulatoryUnits = newUnits.toArray(new RegulatoryUnit[newUnits.size()]);
		newNetwork.networkConcentrations = (Map<GeneticElement, Double>) ((HashMap<GeneticElement, Double>) this.networkConcentrations).clone();
		
		return newNetwork;
		
	}
	
} // End of RegulatoryNetwork class
