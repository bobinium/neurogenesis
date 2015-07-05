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
	
	
	public void updateConcentration(final Map<GeneticElement, Double> products, final Map<GeneticElement, Double> inputElements) {
		
		double activation = 0;
		
		double totalCost = 0;
		
		GeneticElement foodElement = null;
		
		System.out.println("Number of cis-elements: " + this.cisElements.length);
		
		for (GeneticElement cisElement : this.cisElements) {
		
			System.out.println("Number of trans-elements: " + products.size());
			
			for (GeneticElement transElement : products.keySet()) {
				
				if (transElement.getType() == GeneticElement.ELEMENT_TYPE_SPECIAL_FOOD) {
					foodElement = transElement;
					System.out.println("Food available: " + inputElements.get(transElement));
					System.out.println("Concentration/cost: " + getConcentration() + " / " + totalCost);
					totalCost += Math.min(0.1, inputElements.get(foodElement));
//					if (inputElements.get(transElement) > totalCost + getConcentration()) {
//						System.out.println("*** Will cost: " + getConcentration());
//						totalCost += getConcentration();
//					} else {
//						continue;
//					}
				}
				
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
		
		if (foodElement != null) {
			inputElements.put(foodElement, inputElements.get(foodElement) - totalCost);
			System.out.println("Food cost: " + totalCost + " ==> " + inputElements.get(foodElement));
		}
		
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
