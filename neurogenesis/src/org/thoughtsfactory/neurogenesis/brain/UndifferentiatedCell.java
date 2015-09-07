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
 * 
 * @author bob
 *
 */
public class UndifferentiatedCell extends GeneRegulatedCell {


	//
	private final static Logger logger = 
			Logger.getLogger(UndifferentiatedCell.class);	
		
	
	/**
	 * 
	 * @param newSpace
	 * @param newGrid
	 * @param newRegulatoryNetwork
	 * @param newCellDivisionRegulator
	 * @param newCellDeathRegulator
	 */
	public UndifferentiatedCell(final ContinuousSpace<Object> newSpace,
			final Grid<Object> newGrid,
			final RegulatoryNetwork newRegulatoryNetwork,
			final boolean newCellAdhesionEnabled) {
		
		super(newSpace, newGrid, newRegulatoryNetwork, newCellAdhesionEnabled);
		
	} // End of Cell(ContinuousSpace, Grid, RegulatoryNetwork)
	

	/**
	 * 
	 * @param motherCell
	 */
	protected UndifferentiatedCell(final UndifferentiatedCell motherCell) {
		
		super(motherCell, motherCell.cellAdhesionEnabled);
		
	} // End of UndifferentiatedCell(UndifferentiatedCell)

	
	/**
	 * 
	 */
	@ScheduledMethod(start = 1, interval = 1, 
			priority = ScheduleParameters.RANDOM_PRIORITY)
	public void step() {

		absorbProductsFromMatrix();
		updateRegulatoryNetwork();
		updateCellConcentrations();
				
		// Handles cell death.
		if (!cellDeathHandler()) {
			
			if (!cellInvasionHandler()) {
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
			}
		} // End if()
		
	} // End of step()

	
	/**
	 * 
	 * @return
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
			
			this.space.moveTo(neuron, pt.getX() + 0.5, 
					pt.getY() + 0.5, pt.getZ() + 0.5);
			this.grid.moveTo(neuron, pt.getX(),	pt.getY(), pt.getZ());

			logger.info("Cell differentiation event: regulator = " 
					+ neurogenConcentration);

			return true;
				
		} // End if()
		
		return false;
		
	} // End of cellDifferentiationHandler()
	
	
	/**
	 * 
	 */
	protected Cell clone() {
		return new UndifferentiatedCell(this);
	}
	
	
} // End of UndifferentiatedCell class
