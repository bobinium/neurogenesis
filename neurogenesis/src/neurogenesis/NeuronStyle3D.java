package neurogenesis;

import java.awt.Color;
import java.awt.Font;

import javax.media.j3d.Shape3D;

import repast.simphony.visualization.visualization3D.AppearanceFactory;
import repast.simphony.visualization.visualization3D.ShapeFactory;
import repast.simphony.visualization.visualization3D.style.Style3D;
import repast.simphony.visualization.visualization3D.style.TaggedAppearance;
import repast.simphony.visualization.visualization3D.style.TaggedBranchGroup;

public class NeuronStyle3D implements Style3D<Neuron> {

	//
	private static final double MINIMUM_CELL_SIZE = 0.3;
	

	/**
	 * 
	 * @param agent
	 * @param taggedGroup
	 * @return
	 */
	public TaggedBranchGroup getBranchGroup(Neuron agent, 
			TaggedBranchGroup taggedGroup) {
		
		if (taggedGroup == null || taggedGroup.getTag() == null) {
			taggedGroup = new TaggedBranchGroup("DEFAULT");
			Shape3D shape;
			if (agent.isAttached()) {
				shape = ShapeFactory.createCube(.03F, "DEAFULT");
			} else {
				shape = ShapeFactory.createSphere(.03f, "DEFAULT");
			}
		    taggedGroup.getBranchGroup().addChild(shape);
		    return taggedGroup;
		}
		    
		return null;
		
	} // End of getBranchGroup()

	
	/**
	 * 
	 * @param agent
	 * @return
	 */
	public float[] getRotation(Neuron agent) {
		return null;
	}
	
	
	/**
	 * 
	 * @param agent
	 * @param currentLabel
	 * @return
	 */
	public String getLabel(Neuron agent, String currentLabel) {
		return null;
	}

	
	/**
	 * 
	 */
	public Color getLabelColor(Neuron agent, Color currentColor) {
		return null; 
	}

	
	/**
	 * 
	 */
	public Font getLabelFont(Neuron agent, Font currentFont) {
		return null; 
	}

		  
	/**
	 * 
	 */
	public LabelPosition getLabelPosition(Neuron agent, 
			Style3D.LabelPosition curentPosition) {	
		return Style3D.LabelPosition.NORTH;
	}

	
	/**
	 * 
	 */
	public float getLabelOffset(Neuron agent) {
		return .035f;
	}


	/**
	 * 
	 */
	public TaggedAppearance getAppearance(Neuron agent, 
			TaggedAppearance taggedAppearance, Object shapeID) {
		
		if (taggedAppearance == null || taggedAppearance.getTag() == null) {
			taggedAppearance = new TaggedAppearance("DEFAULT");
			AppearanceFactory.setMaterialAppearance(
					taggedAppearance.getAppearance(), 
					agent.isAttached() ? Color.BLUE : Color.CYAN);
	    }
	    
	    return taggedAppearance;
	    
	} // End of getAppearance()
	

	/**
	 * 
	 */
	public float[] getScale(Neuron agent) {
		
		float size = (float) (MINIMUM_CELL_SIZE 
				+ (agent.getCellDivisionConcentration() 
						* (1 - MINIMUM_CELL_SIZE)));
		return new float[] { size, size, size };
		
	} // End of getScale()
	
	
} // End of NeuronStyle3D class
