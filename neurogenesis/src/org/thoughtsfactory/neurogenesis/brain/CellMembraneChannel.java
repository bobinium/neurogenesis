package org.thoughtsfactory.neurogenesis.brain;


/**
 * Permeable cell products that can be exchanged between the cell and the
 * extracellular matrix.
 * 
 * @author bob
 *
 */
public class CellMembraneChannel {
	

	//
	private CellProductType substanceType;
	
	//
	private double concentration;
	
	//
	private double inputRate;
	
	//
	private boolean openForInput;
	
	//
	private double outputRate;
	
	//
	private boolean openForOutput;
	
	
	/**
	 * 
	 * @param newConcentration
	 */
	public CellMembraneChannel(final CellProductType newSubstanceType, 
			final double newConcentration, 
			final double newInputRate,
			final boolean newInputOpen,
			final double newOutputRate,
			final boolean newOutputOpen) {
		
		this.substanceType = newSubstanceType;
		this.concentration = newConcentration;
		this.inputRate = newInputRate;
		this.openForInput = newInputOpen;
		this.outputRate = newOutputRate;
		this.openForOutput = newOutputOpen;
		
	} // End of CellProduct()
	
	
	/**
	 * 
	 * @return
	 */
	public final CellProductType getSubstanceType() {
		return this.substanceType;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final double getConcentration() {
		return this.concentration;
	}
	
	
	/**
	 * 
	 * @param newConcentration
	 */
	public final void setConcentration(final double newConcentration) {
		this.concentration = newConcentration;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final double getInputRate() {
		return this.inputRate;
	}
	
	
	/**
	 * 
	 * @param newInputRate
	 */
	public final void setInputRate(final double newInputRate) {
		this.inputRate = newInputRate;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final boolean isOpenForInput() {
		return this.openForInput;
	}
	
	
	/**
	 * 
	 * @param newValue
	 */
	public final void setOpenForInput(final boolean newValue) {
		this.openForInput = newValue;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final double getOutputRate() {
		return this.outputRate;
	}
	
	
	/**
	 * 
	 * @param newOutputRate
	 */
	public final void setOutputRate(final double newOutputRate) {
		this.outputRate = newOutputRate;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public final boolean isOpenForOutput() {
		return this.openForOutput;
	}
	
	
	/**
	 * 
	 * @param newValue
	 */
	public final void setOpenForOutput(final boolean newValue) {
		this.openForOutput = newValue;
	}
	
	
} // End of CellMembraneChannel class
