package org.thoughtsfactory.neurogenesis.genetics;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.random.RandomHelper;


/**
 * 
 * @author bob
 *
 */
public class GenomeFactory {

	
	private static final int MIN_NUMBER_CIS_ELEMENTS = 1;
	
	private static final int MAX_NUMBER_CIS_ELEMENTS = 5;
	
	private static final int MIN_NUMBER_TRANS_ELEMENTS = 1;
	
	private static final int MAX_NUMBER_TRANS_ELEMENTS = 5;
	
	private static final int MIN_NUMBER_GENES = 2;
	
	private static final int MAX_NUMBER_GENES = 5;
	
	public static final double MAX_AFFINITY = 10.0;
	
	
	/**
	 * 
	 * @return
	 */
	public RegulatoryNetwork getNewGenome(final int genomeSize) {

		List<RegulatoryUnit> regulatoryUnits = new ArrayList<RegulatoryUnit>();
		
//		for (int i = MIN_NUMBER_GENES; 
//				i <= RandomHelper.nextIntFromTo(MIN_NUMBER_GENES, 
//						MAX_NUMBER_GENES); i++) {
			
		for (int i = 1;	i <= genomeSize; i++) {
			
			regulatoryUnits.add(getNewGene());
			
		} // End for()
		
		List<GeneticElement> inputElements = new ArrayList<GeneticElement>();
		List<GeneticElement> outputElements = new ArrayList<GeneticElement>();
		
		for (GeneticElement.Type elementType : GeneticElement.Type.values()) {

			switch (elementType) {
			case CIS: 
			case TRANS:
				break;
			case SPECIAL_IN_FOOD:
			case SPECIAL_IN_CAM:
			case SPECIAL_IN_MUTAGEN:
				inputElements.add(getNewGeneticElement(elementType));
				break;
			case SPECIAL_OUT_WASTE:
			case SPECIAL_OUT_CAM:
			case SPECIAL_OUT_SAM: 
			case SPECIAL_OUT_MUTAGEN:
			case SPECIAL_OUT_MITOGEN:
			case SPECIAL_OUT_NEUROGEN:
			case SPECIAL_OUT_ENERGY:
			case SPECIAL_OUT_FOOD_RATE_IN:
			case SPECIAL_OUT_WASTE_RATE_IN:
			case SPECIAL_OUT_WASTE_RATE_OUT:
			case SPECIAL_OUT_SAM_RATE_OUT:
			case SPECIAL_OUT_MUTAGEN_RATE_IN:
			case SPECIAL_OUT_MUTAGEN_RATE_OUT:
			case SPECIAL_OUT_NEUROGEN_RATE_IN:
			case SPECIAL_OUT_NEUROGEN_RATE_OUT:
				outputElements.add(getNewGeneticElement(elementType));
				break;
			}
	
		} // End for(elementType)
					
		return new RegulatoryNetwork(regulatoryUnits.toArray(
				new RegulatoryUnit[regulatoryUnits.size()]),
				inputElements.toArray(new GeneticElement[inputElements.size()]),
				outputElements.toArray(
						new GeneticElement[outputElements.size()]));
		
	} // End of getNewGenome()
	
	
	/**
	 * 
	 * @return
	 */
	private RegulatoryUnit getNewGene() {
		
		RegulatoryUnit regulatoryUnit = new RegulatoryUnit();
		
		for (int i = MIN_NUMBER_CIS_ELEMENTS; 
				i <= RandomHelper.nextIntFromTo(MIN_NUMBER_CIS_ELEMENTS, 
						MAX_NUMBER_CIS_ELEMENTS); i++) {
			
			addNewGeneticElement(regulatoryUnit, GeneticElement.Type.CIS);
			
		} // End for()
		
		for (int i = MIN_NUMBER_TRANS_ELEMENTS; 
				i <= RandomHelper.nextIntFromTo(MIN_NUMBER_TRANS_ELEMENTS, 
						MAX_NUMBER_TRANS_ELEMENTS); i++) {
			
			addNewGeneticElement(regulatoryUnit, GeneticElement.Type.TRANS);
			
		} // End for()

		return regulatoryUnit;
		
	} // End of getNewGene()
	
	
	/**
	 * 
	 * @return
	 */
	private void addNewGeneticElement(final RegulatoryUnit regulatoryUnit, 
			final GeneticElement.Type elementType) {
		
		double affinityX = RandomHelper.nextDoubleFromTo(0, MAX_AFFINITY);
		double affinityY = RandomHelper.nextDoubleFromTo(0, MAX_AFFINITY);
		
		int randomInt = RandomHelper.nextIntFromTo(0, 1);
		int sign;
		if (randomInt == 0) {
			sign = -1;
		} else {
			sign = 1;
		}
		
		regulatoryUnit.addGeneticElement(elementType, 
				affinityX, affinityY, sign);
		
	} // End of getNewGeneticELement()

	
	/**
	 * 
	 * @param type
	 * @return
	 */
	private GeneticElement getNewGeneticElement(final GeneticElement.Type type) {
		
		double affinityX = RandomHelper.nextDoubleFromTo(0, MAX_AFFINITY);
		double affinityY = RandomHelper.nextDoubleFromTo(0, MAX_AFFINITY);
		
		int sign;
		int randomInt = RandomHelper.nextIntFromTo(0, 1);
		if (randomInt == 0) {
			sign = -1;
		} else {
			sign = 1;
		}
		
		return new GeneticElement(type, affinityX, affinityY, sign);
		
	} // End of getNewGeneticElement()
	
	
} // End of GenomeFactory
