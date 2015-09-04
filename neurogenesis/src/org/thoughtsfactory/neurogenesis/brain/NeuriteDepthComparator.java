package org.thoughtsfactory.neurogenesis.brain;

import java.util.Comparator;

/**
 * 
 * @author bob
 *
 */
public class NeuriteDepthComparator implements Comparator<NeuriteJunction> {

	/**
	 * Ascending order.
	 */
	@Override
	public int compare(NeuriteJunction junction1, NeuriteJunction junction2) {

		return junction1.getDepth() - junction2.getDepth();
			
	} // End of compare()

} // End of NeuriteDepthComparator class
