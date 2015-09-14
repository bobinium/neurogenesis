package org.thoughtsfactory.neurogenesis.brain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import repast.simphony.context.Context;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;


/**
 * Implementation of a distributed extracellular matrix where each sample is
 * an agent in a grid cell updating itself every tick of the simulation. The
 * purpose of the current class is only to initially deploy a sample in every
 * grid cell and to implement the interface needed to retrieve samples.
 *  
 * @author Robert Langlois
 */
public class DistributedExtracellularMatrix implements ExtracellularMatrix {

    
    // INSTANCE VARIABLES ======================================================
    

    // Class logger for messages.
    @SuppressWarnings("unused")
    private final static Logger logger = 
            Logger.getLogger(DistributedExtracellularMatrix.class);    
        
    
    // The 3D continuous space to which this object belong.
    private final ContinuousSpace<Object> space;
    
    
    // The 3D grid to which this object belong.
    private final Grid<Object> grid;

        
    // CONSTRUCTORS ============================================================
    
    
    /**
     * Creates and initialises a new distributed extracellular matrix.
     * 
     * @param context The Repast context.
     * @param newSpace The brain continuous space where the samples are deployed
     *                 as potential visible agents.
     * @param newGrid The brain grid where the samples are deployed as agents.
     * @param gridQuadrantSize An integer specifying the current brain grid 
     *                         quadrant size.
     * @param initialConcentrations A map which associates a cellular product
     *                              with a given initial concentration.
     */
    public DistributedExtracellularMatrix(final Context<Object> context,
            final ContinuousSpace<Object> newSpace, final Grid<Object> newGrid, 
            final int gridQuadrantSize, 
            final Map<CellProductType, Double> initialConcentrations) {
    
        this.space = newSpace;
        this.grid = newGrid;
        
        // For each grid cell...
        
        for (int x = -gridQuadrantSize; x <= gridQuadrantSize; x++) {
            
            for (int y = -gridQuadrantSize; y <= gridQuadrantSize; y++) {
                
                for (int z = -gridQuadrantSize; z <= gridQuadrantSize; z++) {
                    
                    ExtracellularMatrixSample matrixSample;
                    
                    /*
                     * Add an autonomous extracellular matrix sample capable of
                     * self updates at every 2 grid cells only; this decreases
                     * the number of deployed extracellular matrix agents, thus
                     * improving performance while still providing coverage of
                     * the whole grid, as there is an overlap between the areas
                     * covered by the active samples. Otherwise a simple
                     * passive (i.e. not scheduled) sample object is deployed
                     * into the grid.
                     */
                    
                    if ((x % 2 == 0) && (y % 2 == 0) && (z % 2 == 0)) {
                        
                        // Active sample.
                        matrixSample = 
                                new DistributedExtracellularMatrixSample(
                                        x, y, z, this.grid);
                    
                    } else {
                        
                        // Passive sample.
                        matrixSample = new ExtracellularMatrixSample(x, y, z);
                        
                    } // End if()
                    
                    for (CellProductType substanceType : 
                            initialConcentrations.keySet()) {
                        
                        matrixSample.setConcentration(substanceType, 
                                initialConcentrations.get(substanceType));
                        
                    } // End for(substanceType)
                    
                    context.add(matrixSample);
                    
                    this.space.moveTo(matrixSample, x + 0.5, y + 0.5, z + 0.5);
                    this.grid.moveTo(matrixSample, x, y, z);
                    
                } // End of for(z)
                
            } // End of for(y)
            
        } // End of for(x)
            
    } // End of DistributedExtracellularMatrix()

    
    // METHODS =================================================================
    
    
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
    
        ExtracellularMatrixSample sample = null; 
        
        // Get the external concentration from the extracellular matrix
        // at current position.
        
        // Find extracellular matrix (there should be only one instance).
        for (Object obj : this.grid.getObjectsAt(x, y, z)) {            
            if (obj instanceof ExtracellularMatrixSample) {
                 sample = (ExtracellularMatrixSample) obj;
                 break;
            }
        }

        assert sample != null : "Distributed extracellular matrix not found!";
        
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

        // Use the GridCellNgh class to create GridCells for
        // the surrounding neighbourhood.
        GridCellNgh<ExtracellularMatrixSample> nghCreator = 
                new GridCellNgh<ExtracellularMatrixSample>(this.grid, pt, 
                        ExtracellularMatrixSample.class, 
                        extentX, extentY, extentZ);
        List<GridCell<ExtracellularMatrixSample>> gridCells = 
                nghCreator.getNeighborhood(includeCentre);

        List<ExtracellularMatrixSample> samples = 
                new ArrayList<ExtracellularMatrixSample>();
        
        for (GridCell<ExtracellularMatrixSample> gridCell : gridCells) {
            
            assert gridCell.size() == 1 : "No extracellular matrix!";
            
            samples.add(gridCell.items().iterator().next());
            
        } // End for(gridCell)
        
        return samples;

    } // End getAreaSample();


} // End of DistributedExtracellularMatrix class
