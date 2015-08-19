package neurogenesis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.log4j.Logger;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;


/**
 * 
 * @author bob
 *
 */
public class ExtracellularMatrix {

	private static final boolean DEBUG = false;
	
	/*
	 * 
	 */
	private final static Logger logger = Logger.getLogger(ExtracellularMatrix.class);
	
	
	/**
	 * 
	 */
	public static final double DIFFUSION_RATE =  0.2;
	
	
	/**
	 * 
	 */
	protected final ContinuousSpace<Object> space;
	
	/**
	 * 
	 */
	protected final Grid<Object> grid;

	
	private Map<GeneticElement, Double> concentrations = new HashMap<GeneticElement, Double>();

	
	private int colour;
	
	
	/**
	 * 
	 * @param space
	 * @param grid
	 */
	protected ExtracellularMatrix(final ContinuousSpace<Object> newSpace, 
			final Grid<Object> newGrid) {
		
		this.space = newSpace;
		this.grid = newGrid;
		
	} // End of ExtracellularMatrix()


	
	/**
	 * 
	 * @return
	 */
	public Map<GeneticElement, Double> getConcentrations() {
		return this.concentrations;
	}

	
	/**
	 * Push diffusion model.
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.RANDOM_PRIORITY)
	public void step() {
		
		// get the grid location of this Cell
		GridPoint pt = this.grid.getLocation(this);
		
		// use the GridCellNgh class to create GridCells for
		// the surrounding neighbourhood.
		GridCellNgh<ExtracellularMatrix> nghCreator = 
				new GridCellNgh<ExtracellularMatrix>(this.grid,
						pt,	ExtracellularMatrix.class, 1, 1, 1);
		List<GridCell<ExtracellularMatrix>> gridCells = 
				nghCreator.getNeighborhood(false);

		double maxConcentration = 0;
		
		Stack<Map<GeneticElement, Double>> lowerConcentrations = 
				new Stack<Map<GeneticElement, Double>>();
		
		for (GeneticElement product : this.concentrations.keySet()) {
			
			double localConcentration =	this.concentrations.get(product);
			
			if (DEBUG)
				System.out.println("Local concentration: " + localConcentration);
			
			double totalConcentration = 0;
		
			for (GridCell<ExtracellularMatrix> gridCell : gridCells) {
			
				if (gridCell.size() > 1) {
					throw new IllegalStateException(
							"One extracellular matrix per grid cell allowed!");
				}
				
				for (ExtracellularMatrix matrix : gridCell.items()) {
				
					Map<GeneticElement, Double> neighbourConcentrations = 
							matrix.getConcentrations();
				
					double neighbourConcentration = 
							(neighbourConcentrations.get(product) == null) 
							? 0 : neighbourConcentrations.get(product);
					
					if (localConcentration > neighbourConcentration) {						
						totalConcentration += neighbourConcentration;
						lowerConcentrations.push(neighbourConcentrations);
					}
					
				} // End for() matrices
				
			} // End for() grid cells
			
			if (DEBUG)
				System.out.println("Total concentration: " + totalConcentration);
			
			double averageConcentration = (lowerConcentrations.size() == 0) 
					? 0 : totalConcentration / lowerConcentrations.size();
			// The greater the magnitude in the difference, the greater the rate.
			// Concentrations will tend toward the average, but not more.
			double maxDiffusingConcentration =  (localConcentration - averageConcentration) / 2;
			
			if (DEBUG)
				System.out.println("Max diffusing concentration: " + maxDiffusingConcentration);
				
			double diffusingConcentration = 
					Math.tanh(maxDiffusingConcentration) * maxDiffusingConcentration;
			
			if (DEBUG)
				System.out.println("Diffusing concentration: " + diffusingConcentration);

			double totalConcentrationDelta = (localConcentration 
					* lowerConcentrations.size()) - totalConcentration;
			
			while (!lowerConcentrations.empty()) {
				
				Map<GeneticElement, Double> neighbourConcentrations = 
						lowerConcentrations.pop();
								
				double neighbourConcentration = 
						(neighbourConcentrations.get(product) == null) 
						? 0 : neighbourConcentrations.get(product);
				
				if (DEBUG)
					System.out.println("Neighbour concentration: " 
							+ neighbourConcentration);
				
				double newNeighbourConcentration = 
						neighbourConcentration + diffusingConcentration 
						* ((localConcentration - neighbourConcentration) 
								/ totalConcentrationDelta);
				neighbourConcentrations.put(product, 
						newNeighbourConcentration); 
				
				if (DEBUG)
					System.out.println("New neighbour concentration: " 
							+ newNeighbourConcentration);

			} // End of while()
			
			double newLocalConcentration = localConcentration - diffusingConcentration;
			
			if (DEBUG)
				System.out.println("New local concentration: " + newLocalConcentration);
			
			this.concentrations.put(product, newLocalConcentration);
			maxConcentration = Math.max(maxConcentration, newLocalConcentration);
			
		} // End for() products
		
		//System.out.println("Max concentration: " + maxConcentration);
		this.colour = (int) (maxConcentration * 256);
		//System.out.println("New matrix colour: " + this.colour);
		
	} // End of step()
	
	
	public int getColour() {
		return this.colour;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public double getFoodConcentration() {
		return this.concentrations.get(RegulatedCell.ENERGY_REGULATOR);
	}
	
	
} // End of ExtracellularMatrix class
