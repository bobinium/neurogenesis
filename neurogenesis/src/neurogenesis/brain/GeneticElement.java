package neurogenesis.brain;


public class GeneticElement {

	
	/**
	 * 
	 * @author bob
	 *
	 */
	public enum Type {
		CIS, 
		TRANS, 
		SPECIAL_IN_FOOD,
		SPECIAL_IN_CAM,
		SPECIAL_IN_MUTAGEN,
		SPECIAL_OUT_WASTE, 
		SPECIAL_OUT_CAM, 
		SPECIAL_OUT_SAM, 
		SPECIAL_OUT_MUTAGEN,
		SPECIAL_OUT_MITOGEN,
		SPECIAL_OUT_NEUROGEN,
		SPECIAL_OUT_ENERGY
	}
		
	
	//
	private Type type;
	
	//
	private double affinityX;
	
	//
	private double affinityY;
	
	//
	private int sign;
	
	
	/**
	 * 
	 * @param newType
	 * @param newAffinityX
	 * @param newAffinityY
	 * @param newSign
	 */
	public GeneticElement(final Type newType, final double newAffinityX, 
			final double newAffinityY, final int newSign) {
		
		this.type = newType;
		this.affinityX = newAffinityX;
		this.affinityY = newAffinityY;
		this.sign = newSign;
		
	} // End of GeneticElement()
	
	
	/**
	 * 
	 * @return
	 */
	public Type getType() {
		return this.type;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getAffinityX() {
		return this.affinityX;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getAffinityY() {
		return this.affinityY;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public int getSign() {
		return this.sign;
	}

	
	/**
	 * 
	 * @param cisElement
	 * @return
	 */
	public double getAffinityForCisElement(final GeneticElement cisElement) {
		
		double affinityDeltaX = this.affinityX - cisElement.affinityX;
		double affinityDeltaY = this.affinityY - cisElement.affinityY;
		
		double affinity = Math.sqrt(Math.pow(affinityDeltaX, 2) 
				+ Math.pow(affinityDeltaY, 2)) * this.sign * cisElement.sign;
		//System.out.println("Affinity: " + affinity);
	
		return affinity;
		
	} // End of getAffinityForCisElement()

	
	/**
	 * 
	 */
	public GeneticElement clone() {
		
		GeneticElement newElement = 
				new GeneticElement(this.type, this.affinityX, 
						this.affinityY, this.sign);
		
		return newElement;
		
	} // End of clone()
	
	
} // End of GeneticElement class