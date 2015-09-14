package org.thoughtsfactory.neurogenesis.genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * A regulatory unit is analogous to a gene. It groups some cis- and
 * trans-elements together, ensuring that all trans-elements of the unit share
 * the same dependency on the same set of cis-elements. All cis- and 
 * trans-elements of a unit thus have the same concentration.
 *  
 * @author Robert Langlois
 */
public class RegulatoryUnit {

    
    // INSTANCE VARIABLES ======================================================
    
    
    // Class logger for messages.
    private final static Logger logger = Logger.getLogger(RegulatoryUnit.class);    
        
    
    // The list genetic cis-elements that are part of the unit.
    private List<GeneticElement> cisElements = new ArrayList<GeneticElement>();
    
    
    // The list of genetic trans-elements that are part of the unit.
    private List<GeneticElement> transElements = 
    		new ArrayList<GeneticElement>();
    
    
    // The current concentration in the unit.
    private double concentration = 0.01;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a bare regulatory unit instance for the purpose of cloning. 
     */
    RegulatoryUnit() { // package access.
    }
    
    
    /**
     * Creates a new regulatory unit instance. New regulatory units must always
     * have at least one cis-element and one trans-element.
     * 
     * TODO: Do we really need to enforce this here?
     * 
     * @param cisAffinityX The x-axis affinity component of the cis-element.
     * @param cisAffinityY The y-axis affinity component of the cis-eleemnt.
     * @param cisSign The affinity sign of the cis-element,
     * @param transAffinityX The x-axis affinity component of the trans-element.
     * @param transAffinityY The y-axis affinity component of the trans-element.
     * @param transSign The affinity sign of the trans-element.
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
        
    } // End of RegulatoryUnit(double, double, int, double, double, int)
    
    
    /**
     * Returns the list of cis-elements.
     * 
     * @return An array of genetic elements. 
     */
    public GeneticElement[] getCisElements() {
        return this.cisElements.toArray(
                new GeneticElement[this.cisElements.size()]);
    }
    
    
    /**
     * Returns the list of trans-elements.
     * 
     * @return An array of genetic elements.
     */
    public GeneticElement[] getTransElements() {
        return this.transElements.toArray(
                new GeneticElement[this.transElements.size()]);
    }
    
    
    /**
     * Returns the current concentration in the unit.
     * 
     * @return The current unit concentration.
     */
    public double getConcentration() {
        return this.concentration;
    }
    
    
    /**
     * Adds a new genetic element to the current unit.
     * 
     * @param type The genetic element's type.
     * @param affinityX The x-axis affinity component.
     * @param affinityY The y-axis affinity component.
     * @param sign The affinity's sign.
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
     * Update the concentration of this unit and of all its trans-elements.
     * 
     * @param inputElements The current concentrations of all trans-elements 
     *                      in the whole regulatory network to which this unit
     *                      belongs.
     * @param outputElements The updated concentrations for trans-elements
     *                       belonging to the current unit.
     */
    public void updateConcentrations(
            final Map<GeneticElement, Double> inputElements,
            final Map<GeneticElement, Double> outputElements) {
        
        double activation = 0;
        
        logger.debug("Number of cis-elements: " + this.cisElements.size());
        logger.debug("Number of input elements: " + inputElements.size());
        
        // Calculate the activation provided by input elements.
        for (GeneticElement cisElement : this.cisElements) {        
            for (GeneticElement inputElement : inputElements.keySet()) {                
                double affinity = 
                        inputElement.getAffinityForCisElement(cisElement);
                activation += affinity * inputElements.get(inputElement);            
            }
        }
        
        logger.debug("Activation: " + activation);
        logger.debug("Current concentration: " + this.concentration);
        
//        double deltaConcentration = 
//                Math.tanh(activation / 2) - this.concentration;
        double deltaConcentration = Math.tanh(activation / 2) 
                * ((activation >= 0) ? 1 - this.concentration 
                        : this.concentration) 
                        * RegulatoryNetwork.DELTA_INTEGRATION_RATE;
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
     * Replace a cis-element with another.
     * 
     * TODO: Experimental work.
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
     * Replace a trans-element with another.
     * 
     * TODO: Experimental work.
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
     * Clone the current regulatory unit.
     * 
     * @return A new regulatory unit instance with the same genetic elements.
     */
    public RegulatoryUnit clone() {
        
        RegulatoryUnit newUnit = new RegulatoryUnit();
        
        newUnit.cisElements = new ArrayList<GeneticElement>();
        for (GeneticElement element : this.cisElements) {
        	// We do not clone genetic elements: no point taking more memory for
        	// so many cells that share a common genome.
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
