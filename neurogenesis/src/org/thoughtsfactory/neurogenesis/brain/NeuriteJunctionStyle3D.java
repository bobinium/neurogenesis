package org.thoughtsfactory.neurogenesis.brain;

import java.awt.Color;
import java.awt.Font;

import javax.media.j3d.Shape3D;

import repast.simphony.visualization.visualization3D.AppearanceFactory;
import repast.simphony.visualization.visualization3D.ShapeFactory;
import repast.simphony.visualization.visualization3D.style.Style3D;
import repast.simphony.visualization.visualization3D.style.TaggedAppearance;
import repast.simphony.visualization.visualization3D.style.TaggedBranchGroup;


/**
 * Class that provides the 3D style applied to neurite junctions when displayed
 * in the 3D simulation runtime environment.
 * 
 * @author Robert Langlois
 */
public class NeuriteJunctionStyle3D implements Style3D<NeuriteJunction> {


    /**
     * Returns the branch group.
     * 
     * @param agent An instance of neurite junction.
     * @param taggedGroup Tagged group.
     * @return The branch group
     */
    public TaggedBranchGroup getBranchGroup(NeuriteJunction agent, 
            TaggedBranchGroup taggedGroup) {
        
        //if (taggedGroup == null || taggedGroup.getTag() == null) {
    	
        	taggedGroup = new TaggedBranchGroup("DEFAULT");
        	Shape3D shape;
        	
        	// Synapses are displayed as cubes.
        	if (agent.getSynapses().isEmpty()) {
                shape = ShapeFactory.createSphere(.03f, "DEFAULT");
            } else {
                shape = ShapeFactory.createCube(.03f, "DEFAULT");
            }
        	
            taggedGroup.getBranchGroup().addChild(shape);
            return taggedGroup;
            
        //} // End if()
            
        //return null;
        
    } // End of getBranchGroup()

    
    /**
     * Specify the rotation to apply to the agent.
     * 
     * @param agent A neurite junction instance.
     * @return An array specifying the rotation to apply on each axis or 
     *         {@code null} if no rotation is to be applied.
     */
    public float[] getRotation(NeuriteJunction agent) {
        return null;
    }
    
    
    /**
     * Specify the label to display on the agent.
     * 
     * @param agent A neurite junction instance.
     * @param currentLabel The current label shown on the agent.
     * @return The label to show on the agent or {@code null} for none.
     */
    public String getLabel(NeuriteJunction agent, String currentLabel) {
        return null;
    }

    
    /**
     * Specify the colour to apply to the label.
     * 
     * @param agent A neurite junction instance.
     * @param currentColor The label's current colour.
     * @return The label's new colour or {@code null} to use the default colour.
     */
    public Color getLabelColor(NeuriteJunction agent, Color currentColor) {
        return null; 
    }

    
    /**
     * Specify the label's font.
     * 
     * @param agent A neurite junction instance.
     * @param currentFont The current font applied to the label.
     * @return The new font to apply to the agent's label.
     */
    public Font getLabelFont(NeuriteJunction agent, Font currentFont) {
        return null; 
    }

          
    /**
     * Specify the label's position or orientation relative to the agent.
     * 
     * @param agent A neurite junction instance.
     * @param currentPosition The label's current position.
     * @return The label's new position.
     */
    public LabelPosition getLabelPosition(NeuriteJunction agent, 
            Style3D.LabelPosition curentPosition) {    
        return Style3D.LabelPosition.NORTH;
    }

    
    /**
     * Specify the label's offset.
     * 
     * @param agent A neurite junction instance.
     * @return The new label's offset.
     */
    public float getLabelOffset(NeuriteJunction agent) {
        return .035f;
    }


    /**
     * Specify the appearance of the agent.
     * 
     * @param agent A neurite junction instance.
     * @param taggedAppearance A tagged appearance.
     * @param shapeID A shape ID.
     * @return The new appearance.
     */
    public TaggedAppearance getAppearance(NeuriteJunction agent, 
            TaggedAppearance taggedAppearance, Object shapeID) {
        
        if (taggedAppearance == null || taggedAppearance.getTag() == null) {
            
            taggedAppearance = new TaggedAppearance("DEFAULT");

            Color agentColour = Color.WHITE;
            
            switch (agent.getType()) { 
            case NEURON:
            case AXON:
                if (agent.getNeuron() instanceof FoodInputNeuron) {
                    agentColour = Color.YELLOW;
                } else if (agent.getNeuron() instanceof MotionInputNeuron) {
                    agentColour = Color.MAGENTA;
                } else if (agent.getNeuron() instanceof OutputNeuron) {
                    agentColour = Color.RED;
                } else {
                    agentColour = Color.CYAN;
                }
                break;
            case DENDRITE:
                if (agent.getNeuron() instanceof OutputNeuron) {
                    agentColour = Color.GREEN;
                } else {
                    agentColour = Color.BLUE;
                }
                break;
            }
            
            AppearanceFactory.setMaterialAppearance(
                    taggedAppearance.getAppearance(), 
                    agentColour);
            
        }
        
        return taggedAppearance;
        
    } // End of getAppearance()
    

    /**
     * Specify the scale or magnification of the agent.
     * 
     * @param agent A neurite junction instance.
     * @return An array specifying the scale for each grid axis.
     */
    public float[] getScale(NeuriteJunction agent) {

    	// Neurons are shown bigger than other neurites.
    	// Inactive neurites are not shown.
        float size = (agent.getType() == NeuriteJunction.Type.NEURON) 
                ? 1.0f : (agent.isActive() ? 0.1f : 0);
        return new float[] { size, size, size };
        
    } // End of getScale()
    

} // End of NeuriteJunctionStyle3D class
