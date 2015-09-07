package org.thoughtsfactory.neurogenesis.genetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import repast.simphony.random.RandomHelper;


public class RegulatoryNetwork {

	/**
	 * 
	 */
	public static final double DELTA_INTEGRATION_RATE = 0.2;
	
	
	//
	private final static Logger logger = 
			Logger.getLogger(RegulatoryNetwork.class);	
		
	
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
			logger.debug("Unit #" + ++count 
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
			
		for (GeneticElement transElement : 
				this.networkConcentrations.keySet()) {
				
			double affinity = 
					transElement.getAffinityForCisElement(outputElement); 
				
			activation += affinity 
					* this.networkConcentrations.get(transElement);
			
		} // End of for() trans elements

		logger.debug("Activation (output): " + activation);
			
		double deltaConcentration = Math.tanh(activation / 2) 
					* ((activation >= 0) ? 1 - currentConcentration 
							: currentConcentration) * DELTA_INTEGRATION_RATE;
		logger.debug("Delta concentration (output): " + deltaConcentration);
		
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
	
		if (outputElement.getType() != GeneticElement.Type.SPECIAL_OUT_ENERGY) {
			throw new IllegalArgumentException(
					"Need an energy output genetic element!");
		}
		
		/**
		 * Cost of the network in energy is genetically determined by the
		 * values of the SPECIAL_OUT_ENERGY genetic element in relation to
		 * all products of the network, given here by the activation value.
		 * Cost is also a function of the network size in the way that more
		 * trans elements are an opportunity to raise the activation higher.
		 */
		
		double activation = 0;
			
		for (GeneticElement transElement : 
				this.networkConcentrations.keySet()) {
				
			double affinity = Math.abs(transElement
					.getAffinityForCisElement(outputElement)); 
				
			activation += affinity 
					* this.networkConcentrations.get(transElement);
			
		} // End of for() trans elements

		logger.debug("Activation (output): " + activation);
		
		// Squash the result between 0.5 and 1 with a sigmoid function
		// (activation always positive).
		double deltaConcentration = -1 / (1 + Math.pow(Math.E, -activation)); 
		logger.debug("Delta concentration energy: " + deltaConcentration);
		
		return deltaConcentration;
		
	} // End of calculateEnergyConcentrationDelta()


	/**
	 * 
	 * @return
	 */
	private int getGenomeLength() {
		
		int genomeLength = this.inputElements.length 
				+ this.outputElements.length;
		
		for (RegulatoryUnit regulatoryUnit : this.regulatoryUnits) {
			genomeLength += regulatoryUnit.getCisElements().length
					+ regulatoryUnit.getTransElements().length;
		}
		
		return genomeLength;
		
	} // End of getGenomeLength()
	
	
	/**
	 * 
	 */
	public void mutate() {
		
		int genomeLength = getGenomeLength();
		
		int selectedGeneticElementPos = 
				RandomHelper.nextIntFromTo(0, genomeLength - 1);
		GeneticElement selectedGeneticElement = null;
		
		if (selectedGeneticElementPos < this.inputElements.length) {
			
			selectedGeneticElement = 
					this.inputElements[selectedGeneticElementPos];
			this.inputElements[selectedGeneticElementPos] = 
					mutateGeneticElement(selectedGeneticElement);
			
		} else {
			
			selectedGeneticElementPos -= this.inputElements.length;
			
			if (selectedGeneticElementPos < this.outputElements.length) {
			
				selectedGeneticElement = 
						this.outputElements[selectedGeneticElementPos];
				this.outputElements[selectedGeneticElementPos] = 
						mutateGeneticElement(selectedGeneticElement);
			
			} else {
			
				selectedGeneticElementPos -= this.outputElements.length;
			
				for (RegulatoryUnit regulatoryUnit : this.regulatoryUnits) {
		
					int cisLength = regulatoryUnit.getCisElements().length;
					int transLength = regulatoryUnit.getTransElements().length;
				
					if (selectedGeneticElementPos < cisLength) {
					
						selectedGeneticElement = regulatoryUnit
								.getCisElements()[selectedGeneticElementPos];
						regulatoryUnit.replaceCisElement(selectedGeneticElement, 
								mutateGeneticElement(selectedGeneticElement));
						break;
					
					} else {
						
						selectedGeneticElementPos -= cisLength;
						
						if (selectedGeneticElementPos < transLength) {
						
							selectedGeneticElement = regulatoryUnit
									.getTransElements()
									[selectedGeneticElementPos];
							regulatoryUnit.replaceTransElement(
									selectedGeneticElement, 
									mutateGeneticElement(
											selectedGeneticElement));
							break;

						} else {
					
							selectedGeneticElementPos -= transLength;
					
						} // End if()
						
					} // End if()
					
				} // End if()
				
			} // End for()

		} // End if()
		
	} // End of mutate()
	
	
	/**
	 * 
	 * @param geneticElement
	 * @return
	 */
	private GeneticElement mutateGeneticElement(
			final GeneticElement geneticElement) {
		
		GeneticElement.Type newType = geneticElement.getType();
		double newAffinityX = geneticElement.getAffinityX();
		double newAffinityY = geneticElement.getAffinityY();
		int newSign = geneticElement.getSign();
		
		// 133 bits for a genetic element.
		int bitPos = RandomHelper.nextIntFromTo(0, 132);

		if (bitPos < 4) {
				
			// Genetic element type field.
			int typePos = RandomHelper
					.nextIntFromTo(0, GeneticElement.Type.values().length - 1);
			newType = GeneticElement.Type.values()[typePos];
				
		} else if (bitPos < 68) {
			
			newAffinityX = RandomHelper
					.nextDoubleFromTo(0, GenomeFactory.MAX_AFFINITY);
			
		} else if (bitPos < 132) {
			
			newAffinityY = RandomHelper
					.nextDoubleFromTo(0, GenomeFactory.MAX_AFFINITY);
			
		} else {
			
			int randomInt = RandomHelper.nextIntFromTo(0, 1);
			newSign = (randomInt == 0) ? -1 : 1;
			
		} // End if()
					
		GeneticElement newGeneticElement = new GeneticElement(newType, 
				newAffinityX, newAffinityY, newSign);

		return newGeneticElement;
		
	} // End of mutateGeneticElement()
	
	
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
			newInputElements.add(element); //.clone());
		}
		
		List<GeneticElement> newOutputElements = new ArrayList<GeneticElement>();
		for (GeneticElement element : this.outputElements) {
			newInputElements.add(element); //.clone());
		}
		
		newNetwork.regulatoryUnits = 
				newUnits.toArray(new RegulatoryUnit[newUnits.size()]);
		newNetwork.networkConcentrations = 
				(Map<GeneticElement, Double>) ((HashMap<GeneticElement, Double>) 
						this.networkConcentrations).clone();
		
//		newNetwork.inputElements = this.inputElements.clone();
//		newNetwork.outputElements = this.outputElements.clone();
		
		newNetwork.inputElements = newInputElements.toArray(
				new GeneticElement[newInputElements.size()]);
		newNetwork.outputElements = newOutputElements.toArray(
				new GeneticElement[newOutputElements.size()]);
		
		return newNetwork;
		
	} // End of clone()
	
} // End of RegulatoryNetwork class
