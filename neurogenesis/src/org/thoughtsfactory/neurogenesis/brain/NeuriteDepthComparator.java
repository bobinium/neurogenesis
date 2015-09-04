package org.thoughtsfactory.neurogenesis.brain;

import java.util.Comparator;

/**
 * 
 * @author bob
 *
 */
public class NeuriteDepthComparator implements Comparator<NeuriteJunction> {

	/**
	 * 
	 */
	@Override
	public int compare(NeuriteJunction junction1, NeuriteJunction junction2) {

		return junction2.getDepth() - junction1.getDepth();
			
	} // End of compare()

} // End of NeuriteDepthComparator class
