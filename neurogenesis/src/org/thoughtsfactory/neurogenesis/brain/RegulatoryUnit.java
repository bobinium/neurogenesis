package org.thoughtsfactory.neurogenesis.brain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.brain.GeneticElement.Type;

public class RegulatoryUnit {

	
	//
	private final static Logger logger = Logger.getLogger(RegulatoryUnit.class);	
		
	
	//
	private List<GeneticElement> cisElements = new ArrayList<GeneticElement>();
	
	//
	private List<GeneticElement> transElements = new ArrayList<GeneticElement>();
	
	//
	private double concentration = 0.01;
	
	
	RegulatoryUnit() {
		
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
				GeneticElement.Type.CIS, 
				cisAffinityX, cisAffinityY, cisSign));

		this.transElements.add(new GeneticElement(
				GeneticElement.Type.TRANS, 
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
	public void addGeneticElement(final GeneticElement.Type type, 
			final double affinityX, final double affinityY, final int sign) {
		
		switch (type) {
		case CIS:
			this.cisElements.add(
					new GeneticElement(type, affinityX, affinityY, sign));
			break;
		case TRANS:
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
	public void updateConcentrations(
			final Map<GeneticElement, Double> inputElements,
			final Map<GeneticElement, Double> outputElements) {
		
		double activation = 0;
		
		logger.debug("Number of cis-elements: " + this.cisElements.size());
		logger.debug("Number of input elements: "	+ inputElements.size());
		
		for (GeneticElement cisElement : this.cisElements) {		
			for (GeneticElement inputElement : inputElements.keySet()) {				
				double affinity = 
						inputElement.getAffinityForCisElement(cisElement);
				activation += affinity * inputElements.get(inputElement);			
			}
		}
		
		logger.debug("Activation: " + activation);
		logger.debug("Current concentration: " + this.concentration);
		
//		double deltaConcentration = 
//				Math.tanh(activation / 2) - this.concentration;
		double deltaConcentration = Math.tanh(activation / 2) 
				* ((activation >= 0) ? 1 - this.concentration 
						: this.concentration) * RegulatoryNetwork.DELTA_INTEGRATION_RATE;
		logger.debug("Delta concentration: " + deltaConcentration);

		this.concentration += deltaConcentration;

		if (this.concentration < 0) {
			throw new IllegalStateException(
					"Regulatory unit concentration is negative! (" 
							+ this.concentration + ")");
		}
		
		for (GeneticElement transElement : this.transElements) {		
			outputElements.put(transElement, this.concentration);
		}
		
	} // End of updateConcentrations()
	
	
	/**
	 * 
	 * @param currentElement
	 * @param newElement
	 */
	public void replaceCisElement(final GeneticElement currentElement, 
			final GeneticElement newElement) {
		
		this.cisElements.remove(currentElement);
		this.cisElements.add(newElement);
		
	} // End of replaceCisElement()
	
	
	/**
	 * 
	 * @param currentElement
	 * @param newElement
	 */
	public void replaceTransElement(final GeneticElement currentElement, 
			final GeneticElement newElement) {
		
		this.transElements.remove(currentElement);
		this.transElements.add(newElement);
		
	} // End of replaceCisElement()
	
	
	/**
	 * 
	 */
	public RegulatoryUnit clone() {
		
		RegulatoryUnit newUnit = new RegulatoryUnit();
		
		newUnit.cisElements = new ArrayList<GeneticElement>();
		for (GeneticElement element : this.cisElements) {
			newUnit.cisElements.add(element); //.clone());
		}
		
		newUnit.transElements = new ArrayList<GeneticElement>();
		for (GeneticElement element : this.transElements) {
			newUnit.transElements.add(element); //.clone());
		}
		
		newUnit.concentration = this.concentration;
		
		return newUnit;
		
	} // End of clone()
	
} // End of RegulatoryUnit class
