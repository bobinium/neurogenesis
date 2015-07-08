package neurogenesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegulatoryUnit {

	//
	private List<GeneticElement> cisElements = new ArrayList<GeneticElement>();
	
	//
	private List<GeneticElement> transElements = new ArrayList<GeneticElement>();
	
	//
	private double concentration = 0.01;
	
	
	private RegulatoryUnit() {
		
	}
	
	
	/**
	 * 
	 * @param cisAffinityX
	 * @param cisAffinityY
	 * @param cisSign
	 * @param transAffinityX
	 * @param transAffinityY
	 * @param transSign
	 */
	public RegulatoryUnit(final double cisAffinityX, final double cisAffinityY, 
			final int cisSign, final double transAffinityX, 
			final double transAffinityY, final int transSign) {
	
		this.cisElements.add(new GeneticElement(
				GeneticElement.ELEMENT_TYPE_CIS, 
				cisAffinityX, cisAffinityY, cisSign));

		this.transElements.add(new GeneticElement(
				GeneticElement.ELEMENT_TYPE_TRANS, 
				transAffinityX, transAffinityY, transSign));
		
	} // End of RegulatoryUnit()
	
	
	/**
	 * 
	 * @return
	 */
	public GeneticElement[] getCisElements() {
		return this.cisElements.toArray(
				new GeneticElement[this.cisElements.size()]);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public GeneticElement[] getTransElements() {
		return this.transElements.toArray(
				new GeneticElement[this.transElements.size()]);
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getConcentration() {
		return this.concentration;
	}
	
	
	/**
	 * 
	 * @param type
	 * @param affinityX
	 * @param affinityY
	 * @param sign
	 */
	public void addGeneticElement(final int type, 
			final double affinityX, final double affinityY, final int sign) {
		
		switch (type) {
		case GeneticElement.ELEMENT_TYPE_CIS:
			this.cisElements.add(
					new GeneticElement(type, affinityX, affinityY, sign));
			break;
		case GeneticElement.ELEMENT_TYPE_TRANS:
			this.transElements.add(
					new GeneticElement(type, affinityX, affinityY, sign));
			break;
		default:
			throw new IllegalArgumentException(
					"This type of GeneticElement is not allowed here!");
		}
		
	} // End of addGeneticElement()

	
	/**
	 * 
	 * @param inputElements
	 * @param externalProducts
	 */
	public double updateConcentrations(
			final Map<GeneticElement, Double> inputElements,
			final Map<GeneticElement, Double> outputElements) {
		
		double activation = 0;
		
		System.out.println("Number of cis-elements: " + this.cisElements.size());
		System.out.println("Number of input elements: "	+ inputElements.size());
		
		for (GeneticElement cisElement : this.cisElements) {
		
			for (GeneticElement inputElement : inputElements.keySet()) {
				
				double affinityDeltaX = inputElement.getAffinityX() - cisElement.getAffinityX();
				double affinityDeltaY = inputElement.getAffinityY() - cisElement.getAffinityY();
					
				double affinity = Math.sqrt(Math.pow(affinityDeltaX, 2) + Math.pow(affinityDeltaY, 2));
				System.out.println("Affinity: " + affinity);
				
				activation += affinity * inputElements.get(inputElement) * inputElement.getSign();
			
			}

		}
		
		System.out.println("Activation: " + activation);
		
		double deltaConcentration = Math.tanh(activation / 2) - this.concentration;
		System.out.println("Delta concentration: " + deltaConcentration);
		this.concentration += deltaConcentration;
		
		for (GeneticElement transElement : this.transElements) {		
			outputElements.put(transElement, this.concentration);
		}
		
		double energyCost = Math.abs(deltaConcentration);
		return energyCost;
		
	} // End of updateConcentrations()
	
	
	/**
	 * 
	 */
	public RegulatoryUnit clone() {
		
		RegulatoryUnit newUnit = new RegulatoryUnit();
		newUnit.cisElements = this.cisElements;
		newUnit.transElements = this.transElements;
		newUnit.concentration = this.concentration;
		
		return newUnit;
		
	} // End of clone()
	
} // End of RegulatoryUnit class
