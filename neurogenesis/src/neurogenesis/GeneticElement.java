package neurogenesis;

public class GeneticElement {

	
	/**
	 * 
	 */
	public static final int ELEMENT_TYPE_CIS = 1;
	
	/**
	 * 
	 */
	public static final int ELEMENT_TYPE_TRANS = 2;
	
	/**
	 * 
	 */
	public static final int ELEMENT_TYPE_SPECIAL_IN = 3;

	/**
	 * 
	 */
	public static final int ELEMENT_TYPE_SPECIAL_OUT = 4;
	
	
	//
	private int type;
	
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
	public GeneticElement(final int newType, final double newAffinityX, 
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
	public int getType() {
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

	
} // End of GeneticElement class
