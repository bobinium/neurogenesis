package org.thoughtsfactory.neurogenesis.brain;

import java.awt.Color;
import java.awt.Font;

import javax.media.j3d.Shape3D;

import repast.simphony.visualization.visualization3D.AppearanceFactory;
import repast.simphony.visualization.visualization3D.ShapeFactory;
import repast.simphony.visualization.visualization3D.style.Style3D;
import repast.simphony.visualization.visualization3D.style.TaggedAppearance;
import repast.simphony.visualization.visualization3D.style.TaggedBranchGroup;

public class NeuriteJunctionStyle3D implements Style3D<NeuriteJunction> {

	//
	private static final double MINIMUM_CELL_SIZE = 0.3;
	

	/**
	 * 
	 * @param agent
	 * @param taggedGroup
	 * @return
	 */
	public TaggedBranchGroup getBranchGroup(NeuriteJunction agent, 
			TaggedBranchGroup taggedGroup) {
		
		//if (taggedGroup == null || taggedGroup.getTag() == null) {
			taggedGroup = new TaggedBranchGroup("DEFAULT");
			Shape3D shape;
			if (agent.getSynapses().isEmpty()) {
				shape = ShapeFactory.createSphere(.03f, "DEFAULT");
			} else {
				shape = ShapeFactory.createCube(.03f, "DEFAULT");
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
	public float[] getRotation(NeuriteJunction agent) {
		return null;
	}
	
	
	/**
	 * 
	 * @param agent
	 * @param currentLabel
	 * @return
	 */
	public String getLabel(NeuriteJunction agent, String currentLabel) {
		return null;
	}

	
	/**
	 * 
	 */
	public Color getLabelColor(NeuriteJunction agent, Color currentColor) {
		return null; 
	}

	
	/**
	 * 
	 */
	public Font getLabelFont(NeuriteJunction agent, Font currentFont) {
		return null; 
	}

		  
	/**
	 * 
	 */
	public LabelPosition getLabelPosition(NeuriteJunction agent, 
			Style3D.LabelPosition curentPosition) {	
		return Style3D.LabelPosition.NORTH;
	}

	
	/**
	 * 
	 */
	public float getLabelOffset(NeuriteJunction agent) {
		return .035f;
	}


	/**
	 * 
	 */
	public TaggedAppearance getAppearance(NeuriteJunction agent, 
			TaggedAppearance taggedAppearance, Object shapeID) {
		
		if (taggedAppearance == null || taggedAppearance.getTag() == null) {
			
			taggedAppearance = new TaggedAppearance("DEFAULT");

			Color agentColour = Color.WHITE;
			switch (agent.getType()) { 
			case NEURON:
				if (agent.getNeuron() instanceof InputNeuron) {
					agentColour = Color.YELLOW;
				} else if (agent.getNeuron() instanceof OutputNeuron) {
					agentColour = Color.RED;
				} else {
					agentColour = Color.CYAN;
				}
				break;
			case DENDRITE:
				agentColour = (agent.getNeuron() instanceof OutputNeuron) 
						? Color.GREEN : Color.BLUE;
				break;
			case AXON:
				agentColour = (agent.getNeuron() instanceof InputNeuron)
						? Color.MAGENTA : Color.RED;
				break;
			}
			
			AppearanceFactory.setMaterialAppearance(
					taggedAppearance.getAppearance(), 
					agentColour);
			
	    }
	    
	    return taggedAppearance;
	    
	} // End of getAppearance()
	

	/**
	 * 
	 */
	public float[] getScale(NeuriteJunction agent) {
		
//		float size = (float) (MINIMUM_CELL_SIZE 
//				+ (agent.getCellDivisionConcentration() 
//						* (1 - MINIMUM_CELL_SIZE)));
		float size = (agent.getType() == NeuriteJunction.Type.NEURON) 
				? 1.0f : (agent.isActive() ? 0.1f : 0);
		return new float[] { size, size, size };
		
	} // End of getScale()
	
	
} // End of NeuronStyle3D class
