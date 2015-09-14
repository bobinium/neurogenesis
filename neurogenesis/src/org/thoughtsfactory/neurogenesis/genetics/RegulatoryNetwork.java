package org.thoughtsfactory.neurogenesis.genetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import repast.simphony.random.RandomHelper;


/**
 * A regulatory network defines the genome of a cell. It defines a specific set
 * of relationships between cis- and trans- genetic elements, i.e. a network of
 * affinities between these elements. A regulatory network is made of subunits
 * called regulatory units; these group some cis- and trans- elements together
 * in subunits that are analogous to 'genes'. It also defines a set of genetic
 * elements used to provide external input to the network as well as a set of
 * genetic elements for output from the network.
 * 
 * @author Robert Langlois
 */
public class RegulatoryNetwork {

    
    // CONSTANTS ===============================================================
    
    
    /**
     * The rate at which regulatory unit concentration varies.
     */
    public static final double DELTA_INTEGRATION_RATE = 0.2;
    
    
    // INSTANCE VARIABLES ======================================================
    
    
    // Class logger for messages.
    private final static Logger logger = 
            Logger.getLogger(RegulatoryNetwork.class);    
        
    
    // The set of regulatory units (or 'genes') of this network.
    private RegulatoryUnit[] regulatoryUnits;;
    
    
    // The set of input genetic elements.
    private GeneticElement[] inputElements;
    
    
    // The set of output genetic elements.
    private GeneticElement[] outputElements;
    
    
    // Concentrations of individual trans products.
    private Map<GeneticElement, Double> networkConcentrations = 
            new HashMap<GeneticElement, Double>();
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a bare regulatory network instance for the private purpose of
     * cloning. 
     */
    private RegulatoryNetwork() { 
    }
    
    
    /**
     * Creates a new regulatory network instance.
     * 
     * @param newRegulatoryUnits The set of regulatory units.
     * @param newInputElements The set of input genetic elements.
     * @param newOutputElements The set of output genetic elements.
     */
    public RegulatoryNetwork(final RegulatoryUnit[] newRegulatoryUnits, 
            final GeneticElement[] newInputElements,
            final GeneticElement[] newOutputElements) {
        
        // Note: arrays are not cloned...
        this.regulatoryUnits = newRegulatoryUnits;
        this.inputElements = newInputElements;
        this.outputElements = newOutputElements;
        
    } /* End of RegulatoryNetwork(
            RegulatoryUnit[], GeneticElement[], GeneticElement[]) */
    

    // METHODS =================================================================
    
    
    /**
     * Returns the set of input genetic elements.
     * 
     * @return An array of genetic elements.
     */
    public GeneticElement[] getInputElements() {
        return this.inputElements.clone();
    }
    
    
    /**
     * Returns the set of output genetic elements.
     * 
     * @return An array of genetic elements.
     */
    public GeneticElement[] getOutputElements() {
        return this.outputElements.clone();
    }
    
    
    /**
     * Update the state of the network.
     * 
     * @param inputConcentrations A map associating a concentration to each
     *                            input element of the network.
     */
    public void updateNetwork(
            final Map<GeneticElement, Double> inputConcentrations) {
        
        Map<GeneticElement, Double> currentConcentrations = 
                new HashMap<GeneticElement, Double>();

        // Copy all current concentrations so every regulatory unit receives
        // concentrations from the PREVIOUS tick.
        currentConcentrations.putAll(this.networkConcentrations);
        
        // Adds the concentrations of the input elements; input elements are
        // also for all purposes trans-elements.
        currentConcentrations.putAll(inputConcentrations);
        
        int count = 0;
        
        // Update each regulatory unit in turn.
        for (RegulatoryUnit regulatoryUnit : this.regulatoryUnits) {
            
            regulatoryUnit.updateConcentrations(
                    currentConcentrations, this.networkConcentrations);
            logger.debug("Unit #" + ++count 
                    + " concentration: " + regulatoryUnit.getConcentration());
            
        } // End for()
                
    } // End of updateNetwork()
    
    
    /**
     * Calculate the difference in value to apply to the current concentration
     * for a specified genetic element. 
     *
     * @param outputElement The output genetic element.
     * @param currentConcentration The current concentration for the output
     *                             element.
     * @return The concentration delta to apply.
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
     * Returns the total genome length, including input and output genetic
     * elements.
     *  
     * @return The total length of the genome.
     */
    public int getGenomeLength() {
        
        int genomeLength = this.inputElements.length 
                + this.outputElements.length;
        
        for (RegulatoryUnit regulatoryUnit : this.regulatoryUnits) {
            genomeLength += regulatoryUnit.getCisElements().length
                    + regulatoryUnit.getTransElements().length;
        }
        
        return genomeLength;
        
    } // End of getGenomeLength()
    
    
    /**
     * Mutate a genome.
     * 
     * TODO: Experimental work. Not used.
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
     * Mutate a specific genetic element.
     * 
     * TODO: Experimental work. Not used.
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
     * Clone the current regulatory network.
     * 
     * @return A new regulatory network instance but with the same genome.
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
        
        List<GeneticElement> newOutputElements = 
                new ArrayList<GeneticElement>();
        for (GeneticElement element : this.outputElements) {
            newInputElements.add(element); //.clone());
        }
        
        newNetwork.regulatoryUnits = 
                newUnits.toArray(new RegulatoryUnit[newUnits.size()]);
        newNetwork.networkConcentrations = 
                (Map<GeneticElement, Double>) ((HashMap<GeneticElement, Double>) 
                        this.networkConcentrations).clone();
        
//        newNetwork.inputElements = this.inputElements.clone();
//        newNetwork.outputElements = this.outputElements.clone();
        
        newNetwork.inputElements = newInputElements.toArray(
                new GeneticElement[newInputElements.size()]);
        newNetwork.outputElements = newOutputElements.toArray(
                new GeneticElement[newOutputElements.size()]);
        
        return newNetwork;
        
    } // End of clone()

    
} // End of RegulatoryNetwork class
