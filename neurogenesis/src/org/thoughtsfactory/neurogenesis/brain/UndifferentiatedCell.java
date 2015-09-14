package org.thoughtsfactory.neurogenesis.brain;

import org.apache.log4j.Logger;
import org.thoughtsfactory.neurogenesis.genetics.RegulatoryNetwork;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;


/**
 * An undifferentiated cell implements the lifecycle of a cell that can
 * potentially differentiate into a neuron.
 * 
 * @author Robert Langlois
 */
public class UndifferentiatedCell extends GeneRegulatedCell {

	
    // INSTANCE VARIABLES ======================================================
    
    
    // Class logger ofr messages.
    private final static Logger logger = 
            Logger.getLogger(UndifferentiatedCell.class);    
        

    // CONSTRUCTORS ============================================================
    

    /**
     * Creates a new undifferentiated cell instance.
     *
     * @param newId The label that identifies the new cell.
     * @param newSpace The continuous space from which this cell will be 
     *                 displayed. 
     * @param newGrid The grid that defines all locations in the virtual brain.
     * @param newRegulatoryNetwork The gene regulatory network that governs this 
     *                             cell.
     * @param newCellAdhesionEnabled Indicates if this cell is allowed to attach
     *                               to any other cell at all during its life 
     *                               time.
     */
    public UndifferentiatedCell(final String newId,
            final ContinuousSpace<Object> newSpace,
            final Grid<Object> newGrid,
            final RegulatoryNetwork newRegulatoryNetwork,
            final boolean newCellAdhesionEnabled) {
        
        super(newId, newSpace, newGrid, 
                newRegulatoryNetwork, newCellAdhesionEnabled);
        
    } /* End of UndifferentiatedCell(String, 
    		ContinuousSpace, Grid, RegulatoryNetwork, boolean) */
    

    /**
     * Creates a new undifferentiated cell instance from another existing cell.
     * 
     * @param newId The label that identifies the new cell.
     * @param motherCell The cell to initialise from.
     */
    protected UndifferentiatedCell(final String newId,
            final UndifferentiatedCell motherCell) {
        
        super(newId, motherCell, motherCell.cellAdhesionEnabled);
        
    } // End of UndifferentiatedCell(String, UndifferentiatedCell)

    
    // METHODS =================================================================
    
    
    // CELL LIFE CYCLE METHDOS -------------------------------------------------

    
    /**
     * Execute the life cycle of an undifferentiated cell.
     * 
     * This method is scheduled for execution at every tick of the simulation
     * with the same priority given to most cellular agents.
     */
    @ScheduledMethod(start = 1, interval = 1, 
            priority = ScheduleParameters.RANDOM_PRIORITY)
    public void step() {

        absorbProductsFromMatrix();
        updateRegulatoryNetwork();
        updateCellConcentrations();
                
        // Handles cell death.
        if (!cellDeathHandler()) {
            
            // Handles cellular division.
            if (!cellDivisionHandler()) {
                
                // Handles cellular differentiation.
                if (!cellDifferentiationHandler()) {
                
                    // Handles cell adhesion.
                    if (this.cellAdhesionEnabled) {
                        cellAdhesionHandler();
                    }

                    // Handles mutations.
                    //cellMutationHandler();
                    
                    // Handles movement.
                    cellMovementHandler();
                    
                    expelProductsToMatrix();
                    
                } // End if()
                
            } // End if()

        } // End if()
        
    } // End of step()

    
    /**
     * Handles cellular differentiation events.
     * 
     * @return {@code true} if cellular differentiation occured, {@code false}
     *         otherwise.
     */
    protected boolean cellDifferentiationHandler() {
                
        double neurogenConcentration = this.membraneChannels
                .get(CellProductType.NEUROGEN).getConcentration();
        
        logger.debug("Cell differentiation regulator concentration: " 
                + neurogenConcentration);
        
        if (checkConcentrationTrigger(neurogenConcentration, true)) {
            
            // get the grid location of this Cell
            GridPoint pt = this.grid.getLocation(this);
                    
            @SuppressWarnings("unchecked")
            Context<Object> context = ContextUtils.getContext(this);
            context.remove(this);

            Neuron neuron = CellFactory.getNeuronFrom(this);
            context.add(neuron);
            
            neuron.moveTo(pt);

            logger.info("Cell differentiation event: regulator = " 
                    + neurogenConcentration);

            return true;
                
        } // End if()
        
        return false;
        
    } // End of cellDifferentiationHandler()
    
    
    // OVERRIDEN METHODS -------------------------------------------------------
    
    
    /**
     * Clone the current cell giving it a new ID.
     * 
     * @param newId The ID of the new cell.
     */
    @Override // Cell
    protected Cell getClone(final String newId) {
        return new UndifferentiatedCell(newId, this);
    }
    

} // End of UndifferentiatedCell class
