package org.thoughtsfactory.neurogenesis.brain;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;


/**
 *  Class holding the concentrations of all cell products at a given grid
 *  location. This class is meant to be deployed as an autonomous agent in the
 *  brain grid, updating itself every simulation tick.
 *  
 * @author Robert Langlois
 */
public class DistributedExtracellularMatrixSample 
        extends ExtracellularMatrixSample {


    // CONSTANTS ===============================================================
    

    /**
     * The rate at which chemicals diffuses between grid cells.
     */
    public static final double DIFFUSION_RATE =  0.2;
    
    
    /**
     * The rate at which chemicals naturally decay.
     */
    public static final double DECAY_RATE = 0.001;

        
    // INSTANCE VARIABLES ======================================================
    
    
    // Class logger for messages.
    private final static Logger logger = 
            Logger.getLogger(DistributedExtracellularMatrixSample.class);    
        
    
    // The 3D grid to which this object belong.
    private final Grid<Object> grid;


    // An array of all the neighbouring samples for quick access.
    private ExtracellularMatrixSample[] neighbours = null;
    
    
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates a new extracellular matrix sample.
     * 
     * @param newX The x-axis coordinate of this sample.
     * @param newY The y-axis coordinate of this sample.
     * @param newZ The z-axis coordinate of this sample.
     * @param newGrid The brain grid to which this agent is deployed.
     */
    public DistributedExtracellularMatrixSample(
            final int newX, final int newY, final int newZ,
            final Grid<Object> newGrid) {

        super(newX, newY, newZ);
        
        this.grid = newGrid;
        
    } // End of DistributedExtracellularMatrixSample()


    // METHODS =================================================================
    
    
    /**
     * Initialise the current sample.
     * 
     * This method is scheduled for execution ONCE, with a FIRST priority, i.e.
     * BEFORE most cellular agent are allowed action.
     */
    @ScheduledMethod(start = 1, priority = ScheduleParameters.FIRST_PRIORITY)
    public void init() {
       
    	/*
    	 * Get a reference to each surrounding extracellular matrix sample,
    	 * bypassing the Repast agent retrieval mechanism (used below for 
    	 * the initialisation), thus enabling a quicker diffusion process
    	 * between samples (neighbours will remain the same throughout the
    	 * simulation).
    	 */
    	
        // Get the grid location of this Cell
        GridPoint pt = this.grid.getLocation(this);
        
        // Use the GridCellNgh class to create GridCells for
        // the surrounding neighbourhood.
        GridCellNgh<ExtracellularMatrixSample> nghCreator = 
                new GridCellNgh<ExtracellularMatrixSample>(this.grid,
                        pt, ExtracellularMatrixSample.class, 1, 1, 1);
        List<GridCell<ExtracellularMatrixSample>> gridCells = 
                nghCreator.getNeighborhood(false);

        List<ExtracellularMatrixSample> samples = 
                new ArrayList<ExtracellularMatrixSample>();
        
        for (GridCell<ExtracellularMatrixSample> gridCell : gridCells) {
            
            assert gridCell.size() == 1 : 
                    "One extracellular matrix per grid cell allowed!";
                
            ExtracellularMatrixSample neighbourSample = 
                        gridCell.items().iterator().next();
                
            samples.add(neighbourSample);

        } // End for() grid cells
                        
        this.neighbours = 
                samples.toArray(new ExtracellularMatrixSample[samples.size()]);
        
    } // End of init()

    
    /**
     * Update the concentration of each product of this sample.
     * 
     * This method is scheduled for execution at every tick of the simulation
     * with the same priority given to most cellular agents.
     */
    @ScheduledMethod(start = 1, interval = 1, 
            priority = ScheduleParameters.RANDOM_PRIORITY)
    public void update() {
        
        // Apply diffusion and decay to each product in this grid cell.
        for (CellProductType substanceType : CellProductType.values()) {
            
            double previousLocalConcentration =    
                    this.concentrations[substanceType.ordinal()];
            logger.debug("Local concentration: " + previousLocalConcentration);
            
            // Applies decay rate.
            double localConcentration = previousLocalConcentration 
                    - (previousLocalConcentration * DECAY_RATE);    
    
            for (ExtracellularMatrixSample neighbourSample : this.neighbours) {

                double neighbourConcentration = 
                        neighbourSample.getConcentration(substanceType);
                    
                double diffusingConcentration =    
                        (localConcentration - neighbourConcentration) / 2 
                        * DIFFUSION_RATE;
                
                neighbourSample.setConcentration(substanceType, 
                        neighbourConcentration + diffusingConcentration);
                localConcentration -= diffusingConcentration;
                
            } // End for() grid cells
                        
            logger.debug("New local concentration: " + localConcentration);
            
            this.concentrations[substanceType.ordinal()] = localConcentration;
            
        } // End for() products
        
    } // End of update()


} // End of DistributedExtracellularMatrixSample class
