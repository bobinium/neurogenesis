package neurogenesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RegulatoryNetwork {

	
	private RegulatoryUnit[] regulatoryUnits;;
	
	
	private Map<GeneticElement, Double> outputElements = new HashMap<GeneticElement, Double>();
	
	
	public RegulatoryNetwork(final RegulatoryUnit[] newRegulatoryUnits, 
			final GeneticElement[] newOutputElements) {
		
		this.regulatoryUnits = newRegulatoryUnits;	
		
		for (GeneticElement outputElement : newOutputElements) {
			this.outputElements.put(outputElement, 0.01);
		}
		
	}
	
	
	public Map<GeneticElement, Double> getOutputElements() {
		return this.outputElements;
	}
	
	
	public void updateNetwork(final Map<GeneticElement, Double> inputElements) {
		
		Map<GeneticElement, Double> products = new HashMap<GeneticElement, Double>();
		
		for (GeneticElement inputElement : inputElements.keySet()) {
			if (inputElement.getType() == GeneticElement.ELEMENT_TYPE_SPECIAL_FOOD) {
				System.out.println("Food is there!");
			}
			double concentration = 
					(inputElements.get(inputElement) == null) 
					? 0 : inputElements.get(inputElement);
			products.put(inputElement, concentration);
		}
		
		for (RegulatoryUnit regulatoryUnit : this.regulatoryUnits) {

			for (GeneticElement transElement : regulatoryUnit.getTransElements()) {
				products.put(transElement, regulatoryUnit.getConcentration());
			}
			
		}
		
		int count = 0;
		
		for (RegulatoryUnit regulatoryUnit : this.regulatoryUnits) {
			
			regulatoryUnit.updateConcentration(products, inputElements);
			System.out.println("Unit #" + ++count + " concentration: " + regulatoryUnit.getConcentration());
			
		}
		
		updateOutputElements(products);
		
	}
	
	private void updateOutputElements(final Map<GeneticElement, Double> products) {
		
		System.out.println("Number of out-elements: " + this.outputElements.size());
		
		for (GeneticElement outputElement : this.outputElements.keySet()) {
		
			double activation = 0;
			
			System.out.println("Number of trans-elements (output): " + products.size());
			
			for (GeneticElement transElement : products.keySet()) {
				
				double affinityDeltaX = transElement.getAffinityX() - outputElement.getAffinityX();
				double affinityDeltaY = transElement.getAffinityY() - outputElement.getAffinityY();
					
				double affinity = Math.sqrt(Math.pow(affinityDeltaX, 2) + Math.pow(affinityDeltaY, 2));
				System.out.println("Affinity (output): " + affinity);
				
				activation += affinity * products.get(transElement) * transElement.getSign();
			
			}

			System.out.println("Activation (output): " + activation);
			
			double concentration = this.outputElements.get(outputElement);
			double deltaConcentration = Math.tanh(activation / 2) - concentration;
			System.out.println("Delta concentration (output): " + deltaConcentration);
			
			this.outputElements.put(outputElement, concentration + deltaConcentration);
			System.out.println("Ouput concentration: " + this.outputElements.get(outputElement));

		}
		
	}

	
	public RegulatoryNetwork clone() {
		
		List<RegulatoryUnit> newUnits = new ArrayList<RegulatoryUnit>();
		for (RegulatoryUnit unit : this.regulatoryUnits) {
			newUnits.add(unit.clone());
		}
		
		RegulatoryNetwork newNetwork = new RegulatoryNetwork(newUnits.toArray(new RegulatoryUnit[newUnits.size()]), 
				this.outputElements.keySet().toArray(new GeneticElement[this.outputElements.size()]));
		
		return newNetwork;
		
	}
	
}