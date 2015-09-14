package org.thoughtsfactory.neurogenesis.genetics;

import java.util.ArrayList;
import java.util.List;

import repast.simphony.random.RandomHelper;


/**
 * Class used to create new genomes / regulatory networks.
 * 
 * @author Robert Langlois
 */
public class GenomeFactory {

    
    // INSTANCE VARIABLES ======================================================
    
    
    // The minimum number of cis elements to include in a regulatory unit.
    private static final int MIN_NUMBER_CIS_ELEMENTS = 1;
    
    
    // The maximum number of cis elements to include in a regulatory unit.
    private static final int MAX_NUMBER_CIS_ELEMENTS = 5;
    
    
    // The minimum number of trans elements to include in a regulatory unit.
    private static final int MIN_NUMBER_TRANS_ELEMENTS = 1;
    
    
    // The maximum number of trans elements to include in a regulatory unit.
    private static final int MAX_NUMBER_TRANS_ELEMENTS = 5;
    
    
    // The minimum number of regulatory units in a network.
    private static final int MIN_NUMBER_GENES = 2;
    
    
    // The maximum number of regulatory units in a network.
    private static final int MAX_NUMBER_GENES = 5;
    
    
    /**
     * The maximum value each affinity component is allowed to have.
     */
    public static final double MAX_AFFINITY = 10.0;
    
    
    // METHODS =================================================================
    
    
    /**
     * Creates a new regulatory network with the specified number of regulatory 
     * units.
     * 
     * @return The new regulatory network instance.
     */
    public RegulatoryNetwork getNewGenome(final int genomeSize) {

        List<RegulatoryUnit> regulatoryUnits = new ArrayList<RegulatoryUnit>();
        
        for (int i = 1; i <= genomeSize; i++) {
            
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
            case SPECIAL_OUT_NEUROTRANS:
                outputElements.add(getNewGeneticElement(elementType));
            }
    
        } // End for(elementType)
                    
        return new RegulatoryNetwork(regulatoryUnits.toArray(
                new RegulatoryUnit[regulatoryUnits.size()]),
                inputElements.toArray(new GeneticElement[inputElements.size()]),
                outputElements.toArray(
                        new GeneticElement[outputElements.size()]));
        
    } // End of getNewGenome(int)
    

    /**
     * Creates a new regulatory network with a random number of regulatory
     * units.
     * 
     * @return The new regulatory network instance.
     */
    public RegulatoryNetwork getNewGenome() {

        List<RegulatoryUnit> regulatoryUnits = new ArrayList<RegulatoryUnit>();
        
        for (int i = MIN_NUMBER_GENES; 
                i <= RandomHelper.nextIntFromTo(MIN_NUMBER_GENES, 
                        MAX_NUMBER_GENES); i++) {
            
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
            case SPECIAL_OUT_NEUROTRANS:
                outputElements.add(getNewGeneticElement(elementType));
            }
    
        } // End for(elementType)
                    
        return new RegulatoryNetwork(regulatoryUnits.toArray(
                new RegulatoryUnit[regulatoryUnits.size()]),
                inputElements.toArray(new GeneticElement[inputElements.size()]),
                outputElements.toArray(
                        new GeneticElement[outputElements.size()]));
        
    } // End of getNewGenome()

    
    /**
     * Returns a new regulatory unit instance.
     * 
     * @return A randomly generated regulatory unit.
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
     * Adds a new randomly generated genetic element to a regulatory unit.
     * 
     * @param regulatoryUnit The regulatory unit to which the new genetic
     *                       element will be added.
     * @param elementType The type of genetic element to create.
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
        
    } // End of addNewGeneticElement()

    
    /**
     * Returns a new randomly generated genetic element of the specified type.
     * 
     * @param type The type of genetic element to create.
     * @return The new genetic element.
     */
    private GeneticElement getNewGeneticElement(
    		final GeneticElement.Type type) {
        
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
    

} // End of GenomeFactory class
