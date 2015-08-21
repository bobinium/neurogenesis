package neurogenesis.brain;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.random.RandomHelper;


public class GenomeFactory {

	
	private static final int MIN_NUMBER_CIS_ELEMENTS = 1;
	
	private static final int MAX_NUMBER_CIS_ELEMENTS = 5;
	
	private static final int MIN_NUMBER_TRANS_ELEMENTS = 1;
	
	private static final int MAX_NUMBER_TRANS_ELEMENTS = 5;
	
	private static final int MIN_NUMBER_GENES = 2;
	
	private static final int MAX_NUMBER_GENES = 20;
	
	private static final double MAX_AFFINITY = 10.0;
	
	
	/**
	 * 
	 * @return
	 */
	public RegulatoryNetwork getNewGenome() {

		List<RegulatoryUnit> regulatoryUnits = new ArrayList<RegulatoryUnit>();
		
		for (int i = MIN_NUMBER_GENES; 
				i <= RandomHelper.nextIntFromTo(MIN_NUMBER_GENES, 
						MAX_NUMBER_GENES); i++) {
			
			regulatoryUnits.add(getNewGene());
			
		} // End for()
		
		GeneticElement foodInputElement = 
				getNewGeneticElement(GeneticElement.Type.SPECIAL_IN_FOOD); 
		
		GeneticElement camFeedbackInputElement = 
				getNewGeneticElement(GeneticElement.Type.SPECIAL_IN_CAM); 
		
		GeneticElement mutagenFeedbackInputElement = 
				getNewGeneticElement(GeneticElement.Type.SPECIAL_IN_MUTAGEN); 
		
		GeneticElement wasteOutputElement = 
				getNewGeneticElement(GeneticElement.Type.SPECIAL_OUT_WASTE);
		
		GeneticElement camOutputElement = 
				getNewGeneticElement(GeneticElement.Type.SPECIAL_OUT_CAM);
		
		GeneticElement samOutputElement = 
				getNewGeneticElement(GeneticElement.Type.SPECIAL_OUT_SAM);
		
		GeneticElement mutagenOutputElement = 
				getNewGeneticElement(GeneticElement.Type.SPECIAL_OUT_MUTAGEN);
		
		GeneticElement growthOutputElement = 
				getNewGeneticElement(GeneticElement.Type.SPECIAL_OUT_MITOGEN); 
		
		GeneticElement neurogenOutputElement = 
				getNewGeneticElement(GeneticElement.Type.SPECIAL_OUT_NEUROGEN);
		
		GeneticElement energyOutputElement = 
				getNewGeneticElement(GeneticElement.Type.SPECIAL_OUT_ENERGY);
		
		return new RegulatoryNetwork(regulatoryUnits.toArray(
				new RegulatoryUnit[regulatoryUnits.size()]),
				new GeneticElement[] { 
						foodInputElement, 
						camFeedbackInputElement,
						mutagenFeedbackInputElement },
				new GeneticElement[] {
						wasteOutputElement,
						camOutputElement,
						samOutputElement,
						mutagenOutputElement,
						growthOutputElement,
						neurogenOutputElement,
						energyOutputElement });
		
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
	
	
	/**
	 * 
	 * @param regulatoryNetwork
	 */
	public void mutate(final RegulatoryNetwork regulatoryNetwork) {
		
	} // End of mutate()
	
	
} // End of GenomeFactory
