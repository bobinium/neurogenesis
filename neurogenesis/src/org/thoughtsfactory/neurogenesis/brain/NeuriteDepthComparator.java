package org.thoughtsfactory.neurogenesis.brain;

import java.util.Comparator;


/**
 * Implements a {@code Comparator} to compare two neurites according to their 
 * depth, i.e. how far they are from their neuron.
 * 
 * @author Robert Langlois
 */
public class NeuriteDepthComparator implements Comparator<NeuriteJunction> {

    /**
     * Returns the ordering between the two specified neurites, assuming an 
     * ascending order.
     * 
     * @param junction1 The first neurite junction.
     * @param junction2 The second neurite junction.
     * @return {@code -1} if the first junction is closer to the neuron than the
     *         second, {@code 0} if they are both at the same distance, and
     *         {@code 1} if the first junction is further away than the second.
     */
    @Override
    public int compare(NeuriteJunction junction1, NeuriteJunction junction2) {

        return junction1.getDepth() - junction2.getDepth();
            
    } // End of compare()

    
} // End of NeuriteDepthComparator class
