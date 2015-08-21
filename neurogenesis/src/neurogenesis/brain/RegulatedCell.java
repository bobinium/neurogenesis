package neurogenesis.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neurogenesis.Cell;
import neurogenesis.ExtracellularMatrix;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.SimUtilities;


/**
 * 
 * @author bob
 *
 */
public abstract class RegulatedCell extends Cell {

	/**
	 * 
	 */
	protected static final double REGULATOR_UNIVERSAL_THRESHOLD = 0.7;
	
	
	/**
	 * 
	 */
	protected static final double INITIAL_CONCENTRATION = 0.01;
	
	
	/**
	 * 
	 */
	protected static final double OSMOSIS_RATE = 0.2;
	
	
	/**
	 * Concentration of permeable products inside the current cell.
	 */
	protected Map<CellProduct, Double> internalConcentrations = 
			new HashMap<CellProduct, Double>();
	

	/**
	 * 
	 */
	protected String generationId;
	
	
	/**
	 * 
	 */
	protected RegulatoryNetwork regulatoryNetwork;
	

	/**
	 * 
	 */
	protected double cellDivisionRegulator = INITIAL_CONCENTRATION;
	
	
	/**
	 * 
	 */
	protected double cellAdhesionRegulator = INITIAL_CONCENTRATION;
	
	
	/**
	 * 
	 */
	protected double cellDifferentiationRegulator = INITIAL_CONCENTRATION;
	
	
	/**
	 * 
	 */
	public boolean attached = false;

	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	protected RegulatedCell(final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid) {
		
		super(newSpace, newGrid);
		
		this.internalConcentrations.put(CellProduct.FOOD,
				INITIAL_CONCENTRATION);
		this.internalConcentrations.put(CellProduct.WASTE, 
				INITIAL_CONCENTRATION);
		this.internalConcentrations.put(CellProduct.MUTAGEN, 
				INITIAL_CONCENTRATION);
		this.internalConcentrations.put(CellProduct.SAM, 
				INITIAL_CONCENTRATION);
		
	} // End of Cell()


	/**
	 * 
	 * @return
	 */
	public String getGenerationId() {
		return this.generationId;
	}
	
	
	/**
	 * 
	 * @param newId
	 */
	public void setGenerationId(final String newId) {
		this.generationId = newId;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getCellDivisionConcentration() {
		return this.cellDivisionRegulator;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public boolean isAttached() {
		return this.attached;
	}
	
	
	/**
	 * 
	 * @return
	 */
	protected List<RegulatedCell> findPartnerCells(final GridPoint pt, 
			final double cellAdhesionThreshold) {
		
		List<RegulatedCell> partnerCells = new ArrayList<RegulatedCell>();
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<RegulatedCell> nghCreator = new GridCellNgh<RegulatedCell>(
				this.grid, pt, RegulatedCell.class, 1, 1, 1);
		List<GridCell<RegulatedCell>> gridCells = 
				nghCreator.getNeighborhood(false);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		
		for (GridCell<RegulatedCell> gridCell : gridCells) {
			
			if (gridCell.size() > 1) {
				//throw new IllegalStateException("Only one cell per grid unit!");
			}
			
			for (RegulatedCell cell : gridCell.items()) {
				
				if (cell.cellAdhesionRegulator > cellAdhesionThreshold) {
					partnerCells.add(cell);
				}
				
			} // End for() cells
			
		} // End for() gridCells
		
		return partnerCells;
		
	} // End of findPartnerCells()


	/**
	 * 
	 */
	protected void absorbProductsFromMatrix() {
		
		// get the grid location of this Cell
		GridPoint pt = this.grid.getLocation(this);
		
		// Get the external concentration from the extracellular matrix
		// at current position.
		
		Map<CellProduct, Double> externalConcentrations = null;

		// Find extracellular matrix (there should be only one instance).
		for (Object obj : this.grid.getObjectsAt(
				pt.getX(), pt.getY(), pt.getZ())) {			
			if (obj instanceof ExtracellularMatrix) {
				 ExtracellularMatrix matrix = (ExtracellularMatrix) obj;
				 externalConcentrations = matrix.getConcentrations();
				 break;
			}
		}

		if (externalConcentrations == null) {
			throw new IllegalStateException("No input elements!");
		}

		// Absorb external products if external concentration is higher.
		
		for (CellProduct product : externalConcentrations.keySet()) {
			
			double externalConcentration = externalConcentrations.get(product);
			double internalConcentration = 
					this.internalConcentrations.get(product);
			
			System.out.println("External = " + externalConcentration 
					+ " ==> Internal = " + internalConcentration);
			
			if (externalConcentration > internalConcentration) {
				
				double equilibriumConcentration = 
						(externalConcentration + internalConcentration) / 2;
				
				double diffusingConcentration =
						(externalConcentration - equilibriumConcentration) 
						* OSMOSIS_RATE;
				
				double newExternalConcentration = 
						externalConcentration - diffusingConcentration;
				double newInternalConcentration = 
						internalConcentration + diffusingConcentration;
				
				externalConcentrations.put(product, newExternalConcentration);
				this.internalConcentrations.put(product, 
						newInternalConcentration);
				System.out.println("New internal concentration: " 
						+ newInternalConcentration);
				
			} // End if()
			
		} // end for()
		
	} // End of absorbProductsFromMatrix()
	
	
	/**
	 * Handles gene expression and associated energy cost.
	 */
	protected void updateRegulatoryNetwork() {
		
		Map<GeneticElement, Double> inputConcentrations = 
				new HashMap<GeneticElement, Double>();
		
		for (GeneticElement inputElement : 
				this.regulatoryNetwork.getInputElements()) {
			
			switch (inputElement.getType()) {
			case SPECIAL_IN_FOOD:
				inputConcentrations.put(inputElement, 
					this.internalConcentrations.get(CellProduct.FOOD));
				break;
			case SPECIAL_IN_CAM:
				inputConcentrations.put(inputElement, 
						this.cellAdhesionRegulator);
				break;
			case SPECIAL_IN_MUTAGEN:
				inputConcentrations.put(inputElement, 
						this.internalConcentrations.get(CellProduct.MUTAGEN));
				break;
			default:
			}
			
		} // End for()

		// Update the regulatory network.
		this.regulatoryNetwork.updateNetwork(inputConcentrations);
				
	} // End of updateRegulatoryNetwork()
	
	
	/**
	 * 
	 */
	protected void updateCellConcentrations() {
		
		for (GeneticElement outputElement : 
				this.regulatoryNetwork.getOutputElements()) {
			
			switch (outputElement.getType()) {
			
			case SPECIAL_OUT_WASTE:
				
				double currentWasteConcentration = 
						this.internalConcentrations.get(CellProduct.WASTE);
				double deltaWasteConcentration = this.regulatoryNetwork
						.calculateOutputConcentrationDelta(outputElement, 
								currentWasteConcentration);
				double newWasteConcentration = 
						currentWasteConcentration + deltaWasteConcentration;
				
				if (newWasteConcentration < 0) {
					throw new IllegalStateException(
							"Output element WASTE concentration is negative!"
							+ " (old = " + currentWasteConcentration
							+ ", new = " + newWasteConcentration + ")");
				}
								
				this.internalConcentrations.put(CellProduct.WASTE,
						newWasteConcentration);
				break;
				
			case SPECIAL_OUT_CAM:
								
				double deltaCamConcentration = this.regulatoryNetwork
						.calculateOutputConcentrationDelta(outputElement, 
								this.cellAdhesionRegulator);
				double newCamConcentration = 
						this.cellAdhesionRegulator + deltaCamConcentration;
				
				if (newCamConcentration < 0) {
					throw new IllegalStateException(
							"Output element CAM concentration is negative!"
							+ " (old = " + this.cellAdhesionRegulator
							+ ", new = " + newCamConcentration + ")");
				}
												
				this.cellAdhesionRegulator = newCamConcentration;
				break;
				
			case SPECIAL_OUT_SAM:
				
				double currentSamConcentration = 
						this.internalConcentrations.get(CellProduct.SAM);
				double deltaSamConcentration = this.regulatoryNetwork
						.calculateOutputConcentrationDelta(outputElement, 
								currentSamConcentration);
				double newSamConcentration = 
						currentSamConcentration + deltaSamConcentration;
				
				if (newSamConcentration < 0) {
					throw new IllegalStateException(
							"Output element SAM concentration is negative!"
							+ " (old = " + currentSamConcentration
							+ ", new = " + newSamConcentration + ")");
				}
								
				this.internalConcentrations.put(CellProduct.SAM,
						newSamConcentration);
				break;
				
			case SPECIAL_OUT_MUTAGEN:
				
				double currentMutagenConcentration = 
						this.internalConcentrations.get(CellProduct.MUTAGEN);
				double deltaMutagenConcentration = this.regulatoryNetwork
						.calculateOutputConcentrationDelta(outputElement, 
								currentMutagenConcentration);
				double newMutagenConcentration =
						currentMutagenConcentration + deltaMutagenConcentration;
				
				if (newMutagenConcentration < 0) {
					throw new IllegalStateException(
							"Output element MUTAGEN concentration is negative!"
							+ " (old = " + currentMutagenConcentration
							+ ", new = " + newMutagenConcentration + ")");
				}
												
				this.internalConcentrations.put(CellProduct.MUTAGEN,
						newMutagenConcentration);
				break;
				
			case SPECIAL_OUT_MITOGEN:
				
				double deltaMitogenConcentration = this.regulatoryNetwork
						.calculateOutputConcentrationDelta(outputElement, 
								this.cellDivisionRegulator);
				double newMitogenConcentration =
						this.cellDivisionRegulator + deltaMitogenConcentration;
				
				if (newMitogenConcentration < 0) {
					throw new IllegalStateException(
							"Output element MITOGEN concentration is negative!"
							+ " (old = " + this.cellDivisionRegulator
							+ ", new = " + newMitogenConcentration + ")");
				}
																
				this.cellDivisionRegulator = newMitogenConcentration;
				break;
				
			case SPECIAL_OUT_NEUROGEN:

				double deltaNeurogenConcentration = this.regulatoryNetwork
						.calculateOutputConcentrationDelta(outputElement, 
								this.cellDifferentiationRegulator);
				double newNeurogenConcentration =
						this.cellDifferentiationRegulator 
						+ deltaNeurogenConcentration;
				
				if (newNeurogenConcentration < 0) {
					throw new IllegalStateException(
							"Output element NEUROGEN concentration is negative!"
							+ " (old = " + this.cellDifferentiationRegulator
							+ ", new = " + newNeurogenConcentration + ")");
				}
				
				this.cellDifferentiationRegulator = newNeurogenConcentration;				
				break;
				
			case SPECIAL_OUT_ENERGY:
				
				// Energy spent delta is always negative.
				
				double currentFoodConcentration = 
						this.internalConcentrations.get(CellProduct.FOOD);
				double deltaFoodConcentration = this.regulatoryNetwork
						.calculateEnergyConcentrationDelta(outputElement, 
								currentFoodConcentration);
				System.out.println("Energy delta: " + deltaFoodConcentration);
				double newFoodConcentration =
						currentFoodConcentration + deltaFoodConcentration;
								
//				if (newFoodConcentration < 0) {
//					throw new IllegalStateException(
//							"Output element ENERGY concentration is negative!"
//							+ " (old = " + currentFoodConcentration
//							+ ", new = " + newFoodConcentration + ")");
//				}
																
				this.internalConcentrations.put(CellProduct.FOOD,
						newFoodConcentration);
				break;
				
				// Handles energy spent.
				
//				double currentEnergy = this.internalConcentrations.get(CellProduct.FOOD);
//				double newEnergy = currentEnergy - energyCost;
//				this.internalConcentrations.put(CellProduct.FOOD, Math.max(0, newEnergy));
//				if (newEnergy < 0) {
//					double newCellDivisionRegulator = 
//							this.cellDivisionRegulator + newEnergy;
//					this.cellDivisionRegulator = Math.max(0, newCellDivisionRegulator);
//					if (newCellDivisionRegulator < 0) {
//						this.cellDeathRegulator =- 
//					}
//				}

			default:
				
			} // End switch()
			
		} // End for()
		
	} // End of updateCellConcentrations()
	
	
	/**
	 * 
	 */
	protected void expelProductsToMatrix() {
		
		// get the grid location of this Cell
		GridPoint pt = this.grid.getLocation(this);
		
		// Get the external concentration from the extracellular matrix
		// at current position.
		
		Map<CellProduct, Double> externalConcentrations = null;

		// Find extracellular matrix (there should be only one instance).
		for (Object obj : this.grid.getObjectsAt(
				pt.getX(), pt.getY(), pt.getZ())) {			
			if (obj instanceof ExtracellularMatrix) {
				 ExtracellularMatrix matrix = (ExtracellularMatrix) obj;
				 externalConcentrations = matrix.getConcentrations();
				 break;
			}
		}

		if (externalConcentrations == null) {
			throw new IllegalStateException("No input elements!");
		}

		// Expel internal products if internal concentration is higher.
		
		for (CellProduct product : this.internalConcentrations.keySet()) {
			
			// Food is only taken in, not out.
			if (product == CellProduct.FOOD) {
				continue;
			}
			
			double internalConcentration = 
					this.internalConcentrations.get(product);
			double externalConcentration = externalConcentrations.get(product);
			
			System.out.println("Internal = " + internalConcentration 
					+ " ==> External = " + externalConcentration);
			
			if (internalConcentration > externalConcentration) {
				
				double equilibriumConcentration = 
						(internalConcentration + externalConcentration) / 2;
				
				double diffusingConcentration =
						(internalConcentration - equilibriumConcentration) 
						* OSMOSIS_RATE;
				
				double newInternalConcentration = 
						internalConcentration - diffusingConcentration;
				double newExternalConcentration = 
						externalConcentration + diffusingConcentration;
				
				this.internalConcentrations.put(product, 
						newInternalConcentration);
				externalConcentrations.put(product, newExternalConcentration);
				System.out.println("New internal concentration: " 
						+ newInternalConcentration);
				
			} // End if()
			
		} // end for()

	} // End of expelProductsToMatrix()
	
	
} // End of RegulatedCell class
