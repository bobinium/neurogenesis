package neurogenesis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
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
public class ExtracellularMatrix {

	
	/**
	 * 
	 */
	protected final ContinuousSpace<Object> space;
	
	/**
	 * 
	 */
	protected final Grid<Object> grid;

	
	private Map<GeneticElement, Double> concentrations = new HashMap<GeneticElement, Double>();

	
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

		for (GeneticElement product : this.concentrations.keySet()) {
			
			double localConcentration =	this.concentrations.get(product);
			
			double iGradientComponent = 0;
			double jGradientComponent = 0;
			double kGradientComponent = 0;
		
			for (GridCell<ExtracellularMatrix> gridCell : gridCells) {
			
				if (gridCell.size() > 1) {
					throw new IllegalStateException(
							"One extracellular matrix per grid cell allowed!");
				}
				
				for (ExtracellularMatrix matrix : gridCell.items()) {
				
					Map<GeneticElement, Double> neighbourConcentrations = 
							matrix.getConcentrations();
				
					Double neighbourConcentration = 
							neighbourConcentrations.get(product);
					
					if ((neighbourConcentration != null) 
							&& (localConcentration > neighbourConcentration)) {
						
						double neighbourGradient = 
								localConcentration - neighbourConcentration;
						
						GridPoint gridPoint = gridCell.getPoint();
						
						int i = gridPoint.getX() - pt.getX();
						int j = gridPoint.getY() - pt.getY();
						int k = gridPoint.getZ() - pt.getZ();
						
						double iAngle = i / neighbourGradient;
						double jAngle = j / neighbourGradient;
						double kAngle = k / neighbourGradient;
						
						//iGradientComponent += neighbourGradient * i
					}
					
				} // End for() products
				
			} // End for() matrices
			
		} // End for() grid cells
		
	} // End of step()
	
	
} // End of ExtracellularMatrix class
