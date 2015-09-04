package org.thoughtsfactory.neurogenesis.brain;

import java.awt.Color;
import java.awt.Font;

import javax.media.j3d.Shape3D;

import repast.simphony.visualization.visualization3D.AppearanceFactory;
import repast.simphony.visualization.visualization3D.ShapeFactory;
import repast.simphony.visualization.visualization3D.style.Style3D;
import repast.simphony.visualization.visualization3D.style.TaggedAppearance;
import repast.simphony.visualization.visualization3D.style.TaggedBranchGroup;

public class UndifferentiatedCellStyle3D implements Style3D<UndifferentiatedCell> {

	//
	private static final double MINIMUM_CELL_SIZE = 0.3;
	

	/**
	 * 
	 * @param agent
	 * @param taggedGroup
	 * @return
	 */
	public TaggedBranchGroup getBranchGroup(UndifferentiatedCell agent, 
			TaggedBranchGroup taggedGroup) {
		
		//if (taggedGroup == null || taggedGroup.getTag() == null) {
			taggedGroup = new TaggedBranchGroup("DEFAULT");
			Shape3D shape;
			if (agent.isAttached()) {
				shape = ShapeFactory.createCube(.03f, "DEFAULT");
			} else {
				shape = ShapeFactory.createSphere(.03f, "DEFAULT");
			}
		    taggedGroup.getBranchGroup().addChild(shape);
		    return taggedGroup;
		//}
		    
		//return null;
		
	} // End of getBranchGroup()

	
	/**
	 * 
	 * @param agent
	 * @return
	 */
	public float[] getRotation(UndifferentiatedCell agent) {
		return null;
	}
	
	
	/**
	 * 
	 * @param agent
	 * @param currentLabel
	 * @return
	 */
	public String getLabel(UndifferentiatedCell agent, String currentLabel) {
		return null;
	}
	
	/**
	 * 
	 */
	public Color getLabelColor(UndifferentiatedCell agent, Color currentColor) {
		return null; 
	}

	
	/**
	 * 
	 */
	public Font getLabelFont(UndifferentiatedCell agent, Font currentFont) {
		return null; 
	}

		  
	/**
	 * 
	 */
	public LabelPosition getLabelPosition(UndifferentiatedCell agent, 
			Style3D.LabelPosition curentPosition) {	
		return Style3D.LabelPosition.NORTH;
	}

	
	/**
	 * 
	 */
	public float getLabelOffset(UndifferentiatedCell agent) {
		return .035f;
	}


	/**
	 * 
	 */
	public TaggedAppearance getAppearance(UndifferentiatedCell agent, 
			TaggedAppearance taggedAppearance, Object shapeID) {
		
		if (taggedAppearance == null || taggedAppearance.getTag() == null) {
			taggedAppearance = new TaggedAppearance("DEFAULT");
			AppearanceFactory.setMaterialAppearance(
					taggedAppearance.getAppearance(), 
					agent.isAttached() ? Color.MAGENTA : Color.GREEN);
	    }
	    
	    return taggedAppearance;
	    
	} // End of getAppearance()
	

	/**
	 * 
	 */
	public float[] getScale(UndifferentiatedCell agent) {
		
		float size = (float) (MINIMUM_CELL_SIZE 
				+ (agent.getCellDivisionConcentration() 
						* (1 - MINIMUM_CELL_SIZE)));
		return new float[] { size, size, size };
		
	} // End of getScale()
	
	
} // End of UndifferentiatedCellStyle3D class
