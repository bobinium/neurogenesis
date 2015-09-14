package org.thoughtsfactory.neurogenesis.brain;

import java.util.List;

import repast.simphony.space.grid.GridPoint;


/**
 *  An extracellular matrix represents the chemical content of the whole brain
 *  grid which is external to any cellular agent. It defines the chemical 
 *  environment in which cell agents evolve, keeping a record of cell products 
 *  and their current external concentration. Client classes such as agents can
 *  query this interface to get access to specific extracellular matrix samples.
 *  
 * @author Robert Langlois
 */
public interface ExtracellularMatrix {

    
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
    public ExtracellularMatrixSample getSample(int x, int y, int z);

    
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
    public List<ExtracellularMatrixSample> getAreaSample(GridPoint pt, 
            int extentX, int extentY, int extentZ, boolean includeCentre);
    

} // End of ExtracellularMatrix interface
