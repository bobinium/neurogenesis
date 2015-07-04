package neurogenesis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RegulatoryUnit {

	
	private GeneticElement[] cisElements;
	
	
	private GeneticElement[] transElements;
	
	
	private double concentration = 0.01;
	
	
	public RegulatoryUnit(final GeneticElement[] newGeneticElements) {
	
		List<GeneticElement> newCisElements = new ArrayList<GeneticElement>();
		List<GeneticElement> newTransElements = new ArrayList<GeneticElement>();
		
		for (GeneticElement element : newGeneticElements) {
			switch (element.getType()) {
			case GeneticElement.ELEMENT_TYPE_CIS:
				newCisElements.add(element);
				break;
			case GeneticElement.ELEMENT_TYPE_TRANS:
				newTransElements.add(element);
				break;
			}
		}
		
		if (newCisElements.size() < 1) {
			throw new IllegalStateException("Need at least one cis-regulator");
		}
		
		if (newTransElements.size() < 1) {
			throw new IllegalStateException("Need at least one trans-regulator");
		}
		
		this.cisElements = newCisElements
				.toArray(new GeneticElement[newCisElements.size()]);
		
		this.transElements = newTransElements
				.toArray(new GeneticElement[newTransElements.size()]);
		
	}
	
	
	public GeneticElement[] getTransElements() {
		return this.transElements;
	}
	
	
	public double getConcentration() {
		return this.concentration;
	}
	
	
	public void updateConcentration(final Map<GeneticElement, Double> products) {
		
		double activation = 0;
		
		System.out.println("Number of cis-elements: " + this.cisElements.length);
		
		for (GeneticElement cisElement : this.cisElements) {
		
			System.out.println("Number of trans-elements: " + products.size());
			
			for (GeneticElement transElement : products.keySet()) {
				
				double affinityDeltaX = transElement.getAffinityX() - cisElement.getAffinityX();
				double affinityDeltaY = transElement.getAffinityY() - cisElement.getAffinityY();
					
				double affinity = Math.sqrt(Math.pow(affinityDeltaX, 2) + Math.pow(affinityDeltaY, 2));
				System.out.println("Affinity: " + affinity);
				
				activation += affinity * products.get(transElement) * transElement.getSign();
			
			}

		}
		
		System.out.println("Activation: " + activation);
		
		double deltaConcentration = Math.tanh(activation / 2) - getConcentration();
		System.out.println("Delta concentration: " + deltaConcentration);
		
		this.concentration += deltaConcentration;
		
	}
	
	
	public RegulatoryUnit clone() {
		
		List<GeneticElement> elements = new ArrayList<GeneticElement>();
		elements.addAll(Arrays.asList(this.cisElements));
		elements.addAll(Arrays.asList(this.transElements));
		
		RegulatoryUnit newUnit = new RegulatoryUnit(elements.toArray(new GeneticElement[elements.size()]));
		newUnit.concentration = this.concentration;
		
		return newUnit;
		
	}
	
} // End of RegulatoryUnit class
