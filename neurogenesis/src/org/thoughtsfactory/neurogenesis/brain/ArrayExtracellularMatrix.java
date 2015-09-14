package org.thoughtsfactory.neurogenesis.brain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.grid.GridPoint;


/**
 * Implementation of a centralised extracellular matrix that uses an array to
 * keep a record of the concentration of any cell product on the brain's grid.
 *  
 * The matrix is deployed as single agent which update its state every tick of
 * the simulation.
 * 
 * @author Robert Langlois
 */
public class ArrayExtracellularMatrix implements ExtracellularMatrix {

    
    // CONSTANTS ===============================================================
    

    /**
     * The rate at which chemicals diffuses between grid cells.
     */
    public static final double DIFFUSION_RATE = 0.2;
    
    
    /**
     * The rate at which chemicals naturally decay.
     */
    public static final double DECAY_RATE = 0.001;

        
    // INSTANCE VARIABLES ======================================================
    
    
    // Class logger for messages.
    private final static Logger logger =
            Logger.getLogger(ArrayExtracellularMatrix.class);    
        
    
    // The brain grid quadrant size.
    private final int gridQuadrantSize;
    
    
    // The brain grid size.
    private final int gridSize;
    
    
    // The matrix of all chemical concentrations on the grid, with coordinates
    // [z][y][x][product].
    private final double[][][][] concentrations;
    
            
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates and initialises a new array-based extracellular matrix.
     * 
     * @param newGridQuadrantSize An integer specifying the current brain grid 
     *                            quadrant size.
     * @param initialConcentrations A map which associates a cellular product
     *                              with a given initial concentration.
     */
    public ArrayExtracellularMatrix(final int newGridQuadrantSize, 
            final Map<CellProductType, Double> initialConcentrations) {
        
        this.gridQuadrantSize = newGridQuadrantSize;

        this.gridSize = this.gridQuadrantSize * 2 + 1;
        
        this.concentrations = new double[this.gridSize][this.gridSize]
                [this.gridSize][CellProductType.values().length];
        
        for (int z = 0; z < this.gridSize; z++) {
            
            for (int y = 0; y < this.gridSize; y++) {
                
                for (int x = 0; x < this.gridSize; x++) {
                    
                    for (CellProductType substanceType : 
                            CellProductType.values()) {
                        
                        double concentration = 
                                initialConcentrations.containsKey(substanceType) 
                                ? initialConcentrations.get(substanceType) : 0;
                                
                        this.concentrations[z][y][x][substanceType.ordinal()] =
                                concentration;
                                
                    } // End for(substanceType)
                    
                } // End for(x)
                
            } // End for(y)
            
        } // End for(z)
        
    } // End of ArrayExtracellularMatrix()


    // METHODS =================================================================


    /**
     * Returns the brain grid quadrant size used to create the current matrix.
     * 
     * @return The brain grid quadrant size as an integer.
     */
    public final int getGridQuadrantSize() {
        return this.gridQuadrantSize;
    }
    
    
    /**
     * Returns the size of the brain grid size used to create the current 
     * matrix.
     * 
     * @return The brain grid size as an integer.
     */
    public final int getGridSize() {
        return this.gridSize;
    }
    
    
    /**
     * Update the concentration of each product in every grid cell of the
     * extracellular matrix.
     * 
     * This method is scheduled for execution at every tick of the simulation
     * but with FIRST priority; concentrations in the matrix are updated
     * BEFORE most cellular agent are allowed action.
     */
    @ScheduledMethod(start = 1, interval = 1, 
            priority = ScheduleParameters.FIRST_PRIORITY)
    public void update() {

        /*
         * Skip a row between updates; this decreases the number of matrix cells
         * to process at each tick while still providing coverage of the
         * whole grid, with an overlap between the areas covered by the updating
         * cells.
         */
        
        for (int z = 1; z < this.gridSize; z += 2) {
            
            for (int y = 1; y < this.gridSize; y += 2) {
                
                for (int x = 1; x < this.gridSize; x += 2) {
                    
                    updateConcentrations(x, y, z);
                    
                } // End for(x)
                
            } // End for(y)
            
        } // End for(z)
                    
    } // End of update()
    
    
    /**
     * Update the concentrations of cellular products at the given location in
     * the matrix.
     * 
     * @param x The x-axis coordinate of the grid cell.
     * @param y The y-axis coordinate of the grid cell.
     * @param z The z-axis coordinate of the grid cell.
     */
    private void updateConcentrations(final int x, final int y, final int z) {
        
        // Apply diffusion and decay to each product in this grid cell.
        for (CellProductType substanceType : CellProductType.values()) {
            
            double previousLocalConcentration =    
                    this.concentrations[z][y][x][substanceType.ordinal()];
            
            // Applies decay rate first.
            double localConcentration = previousLocalConcentration 
                    - (previousLocalConcentration * DECAY_RATE);
            
            logger.debug("Local concentration: " + localConcentration);
            
            // Iterate over the Moore's neighbourhood
            // of the specified grid cell.
            
            for (int xi = Math.max(0, x - 1); 
                    xi <= Math.min(this.gridSize - 1, x + 1); xi++) {
                
                for (int yi = Math.max(0, y - 1); 
                        yi <= Math.min(this.gridSize - 1, y + 1); yi++) {
                    
                    for (int zi = Math.max(0, z - 1); 
                            zi <= Math.min(this.gridSize - 1, z + 1); zi++) {
                        
                        // Skip the current location.
                        if (xi == x && yi == y && zi == z) {
                            continue;
                        }
                        
                        double diffusingConcentration =    (localConcentration    
                                - this.concentrations[zi][yi][xi]
                                        [substanceType.ordinal()]) 
                                        / 2 * DIFFUSION_RATE;
                
                        this.concentrations[zi][yi][xi][substanceType.ordinal()] 
                                += diffusingConcentration;
                        localConcentration -= diffusingConcentration;
                        
                    } // End for(zi)
                    
                } // End for(yi)
                        
            } // End for(xi)
                
            // Update local concentration.
            logger.debug("New local concentration: " + localConcentration);
            this.concentrations[z][y][x][substanceType.ordinal()] = 
                    localConcentration;

        } // End for() products
        
    } // End of updateConcentrations()

    
    /**
     * Callback method for updating the concentration of a substance at a
     * given location.
     * 
     * @param x The x-axis grid coordinate.
     * @param y The y-axis grid coordinate.
     * @param z The z-axis grid coordinate.
     * @param productType The product for which the concentration must be 
     *                    updated.
     * @param newConcentration The new concentration.
     */
    protected synchronized void updateConcentration(
            final int x, final int y, final int z, 
            final CellProductType productType, final double newConcentration) {
        
        this.concentrations[z + this.gridQuadrantSize]
                [y + this.gridQuadrantSize][x + this.gridQuadrantSize]
                        [productType.ordinal()] = newConcentration;
        
    } // End of updateConcentration()
    
    
    // INTERFACES IMPLEMENTATION -----------------------------------------------
    
    
    /**
     * Get the concentration of all cell products at the specified grid
     * location.
     * 
     * @param x The x-axis grid coordinate.
     * @param y The y-axis grid coordinate.
     * @param z The z-axis grid coordinate.
     * @return An {@link ExtracellularMatrixSample} object containing all
     *         the current concentrations at the requested location.
     */
    @Override // ExtracellularMatrix
    public ExtracellularMatrixSample getSample(
            final int x, final int y, final int z) {
    
        ExtracellularMatrixSample sample = 
                new ArrayExtracellularMatrixSample(x, y, z, this);
        
        // Translate the brain grid coordinates into appropriate indexes for
        // the concentrations storage array.
        final int indexX = x + this.gridQuadrantSize;
        final int indexY = y + this.gridQuadrantSize;
        final int indexZ = z + this.gridQuadrantSize;
        
        System.arraycopy(this.concentrations[indexZ][indexY][indexX], 0, 
                sample.concentrations, 0, sample.concentrations.length);
        
        return sample;
        
    } // End of getSample()
    
    
    /**
     * Returns the matrix concentrations in all grid cells in the neighbourhood
     * of the specified grid coordinates.
     * 
     * @param pt The grid point object specifying the coordinates.
     * @param extentX The extent of the neighbourhood on the x-axis.
     * @param extendY The extent of the neighbourhood on the y-axis.
     * @param extentZ The extent of the neighbourhood on the z-axis.
     * @param includeCentre Specify if the concentrations at the specified
     *                      location itself should be returned.
     * @return A list of {@link ExtracellularMatrixSample} objects containing 
     *         all the current concentrations around, and possibly including, 
     *         the requested location.
     */
    @Override // ExtracellularMatrix
    public List<ExtracellularMatrixSample> getAreaSample(final GridPoint pt,
            final int extentX, final int extentY, final int extentZ, 
            final boolean includeCentre) {
        
        logger.debug("Coordinates: x = " + pt.getX() 
                + ", y = " + pt.getY() + ", z = " + pt.getZ());
        
        logger.debug("Extents: x = " + extentX 
                + ", y = " + extentY + ", z = " + extentZ);
        
        // Translate the brain grid coordinates into appropriate indexes for
        // the concentrations storage array.
        final int xIndex = pt.getX() + this.gridQuadrantSize;
        final int yIndex = pt.getY() + this.gridQuadrantSize;
        final int zIndex = pt.getZ() + this.gridQuadrantSize;
        
        logger.debug("Indexes: x = " + xIndex 
                + ", y = " + yIndex + ", z = " + zIndex);
        
        final int xLowerLimit = Math.max(0, xIndex - extentX);
        final int xUpperLimit = Math.min(this.gridSize - 1, xIndex + extentX);
        
        final int yLowerLimit = Math.max(0, yIndex - extentY);
        final int yUpperLimit = Math.min(this.gridSize - 1, yIndex + extentY);
        
        final int zLowerLimit = Math.max(0, zIndex - extentZ);
        final int zUpperLimit = Math.min(this.gridSize - 1, zIndex + extentZ);
        
        logger.debug("Lower limits: x = " + xLowerLimit 
                + ", y = " + yLowerLimit + ", z = " + zLowerLimit);
        
        logger.debug("Upper limits: x = " + xUpperLimit 
                + ", y = " + yUpperLimit + ", z = " + zUpperLimit);
        
        List<ExtracellularMatrixSample> areaSample = 
                new ArrayList<ExtracellularMatrixSample>();

        for (int xi = xLowerLimit; xi <= xUpperLimit; xi++) {
            
            for (int yi = yLowerLimit; yi <= yUpperLimit; yi++) {
                
                for (int zi = zLowerLimit; zi <= zUpperLimit; zi++) {
                    
                    if ((xi == xIndex) && (yi == yIndex) 
                            && (zi == zIndex) && !includeCentre) {
                        logger.debug("Centre: skipping...");
                        continue;
                    }
                    
                    ExtracellularMatrixSample sample = 
                            new ArrayExtracellularMatrixSample(
                                    xi - this.gridQuadrantSize, 
                                    yi - this.gridQuadrantSize, 
                                    zi - this.gridQuadrantSize, this);
                    
                    System.arraycopy(this.concentrations[zi][yi][xi], 0, 
                            sample.concentrations, 0, 
                            sample.concentrations.length);
                    
                    areaSample.add(sample);
                    
                } // End for(zi)
                
            } // End for(yi)
            
        } // End for(xi)
        
        logger.debug("Returning " + areaSample.size() + " sample.");
        return areaSample;
        
    } // End of getAreaSample()

    
} // End of ArrayExtracellularMatrix class
