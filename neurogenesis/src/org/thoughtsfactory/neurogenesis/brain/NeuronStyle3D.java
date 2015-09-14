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
 * Class that provides the 3D style applied to neurons when displayed in the 3D 
 * simulation runtime environment.
 * 
 * @author Robert Langlois
 */
public class NeuronStyle3D implements Style3D<Neuron> {

    
    // The minimum size at which a cell should be displayed.
    private static final double MINIMUM_CELL_SIZE = 0.3;
    

    /**
     * Returns the branch group.
     * 
     * @param agent An instance of neuron.
     * @param taggedGroup Tagged group.
     * @return The branch group
     */
    public TaggedBranchGroup getBranchGroup(Neuron agent, 
            TaggedBranchGroup taggedGroup) {
        
        //if (taggedGroup == null || taggedGroup.getTag() == null) {
        
            taggedGroup = new TaggedBranchGroup("DEFAULT");
            Shape3D shape = ShapeFactory.createSphere(.03f, "DEFAULT");
            taggedGroup.getBranchGroup().addChild(shape);
            return taggedGroup;
            
        //} // End if()
            
        //return null;
        
    } // End of getBranchGroup()

    
    /**
     * Specify the rotation to apply to the agent.
     * 
     * @param agent A neuron instance.
     * @return An array specifying the rotation to apply on each axis or 
     *         {@code null} if no rotation is to be applied.
     */
    public float[] getRotation(Neuron agent) {
        return null;
    }
    
    
    /**
     * Specify the label to display on the agent.
     * 
     * @param agent A neuron instance.
     * @param currentLabel The current label shown on the agent.
     * @return The label to show on the agent or {@code null} for none.
     */
    public String getLabel(Neuron agent, String currentLabel) {
        return null;
    }

    
    /**
     * Specify the colour to apply to the label.
     * 
     * @param agent A neuron instance.
     * @param currentColor The label's current colour.
     * @return The label's new colour or {@code null} to use the default colour.
     */
    public Color getLabelColor(Neuron agent, Color currentColor) {
        return null; 
    }

    
    /**
     * Specify the label's font.
     * 
     * @param agent A neuron instance.
     * @param currentFont The current font applied to the label.
     * @return The new font to apply to the agent's label.
     */
    public Font getLabelFont(Neuron agent, Font currentFont) {
        return null; 
    }

          
    /**
     * Specify the label's position or orientation relative to the agent.
     * 
     * @param agent A neuron instance.
     * @param currentPosition The label's current position.
     * @return The label's new position.
     */
    public LabelPosition getLabelPosition(Neuron agent, 
            Style3D.LabelPosition curentPosition) {    
        return Style3D.LabelPosition.NORTH;
    }

    
    /**
     * Specify the label's offset.
     * 
     * @param agent A neuron instance.
     * @return The new label's offset.
     */
    public float getLabelOffset(Neuron agent) {
        return .035f;
    }


    /**
     * Specify the appearance of the agent.
     * 
     * @param agent A neuron instance.
     * @param taggedAppearance A tagged appearance.
     * @param shapeID A shape ID.
     * @return The new appearance.
     */
    public TaggedAppearance getAppearance(Neuron agent, 
            TaggedAppearance taggedAppearance, Object shapeID) {
        
        if (taggedAppearance == null || taggedAppearance.getTag() == null) {
            
            taggedAppearance = new TaggedAppearance("DEFAULT");
            
            Color agentColour;
            
            if (agent.isAttached()) {
                // If there would be such a thing as an
                // attached neuron it would look blue.
                agentColour = Color.BLUE;
            } else {
                agentColour = Color.CYAN;
            }
            
            AppearanceFactory.setMaterialAppearance(
                    taggedAppearance.getAppearance(), agentColour);
            
        } // End if()
        
        return taggedAppearance;
        
    } // End of getAppearance()
    

    /**
     * Specify the scale or magnification of the agent.
     * 
     * @param agent A neuron instance.
     * @return An array specifying the scale for each grid axis.
     */
    public float[] getScale(Neuron agent) {

        /*
         *  The cell is displayed with a size that is proportional to its
         *  internal concentration of cell growth regulator. For neurons this
         *  would only show a neuron's potential for growing neurites.
         */

        float size = (float) (MINIMUM_CELL_SIZE 
                + (agent.getCellDivisionConcentration() 
                        * (1 - MINIMUM_CELL_SIZE)));
        return new float[] { size, size, size };
        
    } // End of getScale()
    

} // End of NeuronStyle3D class
