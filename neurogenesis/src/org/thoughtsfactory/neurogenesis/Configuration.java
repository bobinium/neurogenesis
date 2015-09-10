package org.thoughtsfactory.neurogenesis;


/**
 * 
 * @author bob
 *
 */
public final class Configuration {

	
	//
	private static final Configuration configuration = new Configuration();


	//
	private int brainGridQuadrantSize;
	
	
	//
	private int genomeSize;
	

	//
	private boolean cellAdhesionEnabled;

	
	/**
	 * 
	 */
	private Configuration() {
	}
	
	
	/**
	 * 
	 * @return
	 */
	public static Configuration getInstance() {
		return configuration;
	}
	

	/**
	 * 
	 */
	public int getBrainGridQuadrantSize() {
		return this.brainGridQuadrantSize;
	}
	
	
	/**
	 * 
	 */
	public void setBrainGridQuadrantSize(final int newSize) {
		this.brainGridQuadrantSize = newSize;
	}
	
	
	/**
	 * 
	 */
	public int getGenomeSize() {
		return this.genomeSize;
	}
	
	
	/**
	 * 
	 * @param newGenomeSize
	 */
	public void setGenomeSize(final int newGenomeSize) {
		this.genomeSize = newGenomeSize;
	}
	

	/**
	 * 
	 * @return
	 */
	public boolean isCellAdhesionEnabled() {
		return this.cellAdhesionEnabled;
	}
	
	
	/**
	 * 
	 * @param newValue
	 */
	public void setCellAdhesionEnabled(final boolean newValue) {
		this.cellAdhesionEnabled = newValue;
	}
	
	
} // End of Configuration class
