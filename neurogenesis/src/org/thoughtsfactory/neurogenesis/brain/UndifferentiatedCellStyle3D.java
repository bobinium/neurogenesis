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
 * Class that provides the 3D style applied to undifferentiated cells when
 * displayed in the 3D simulation runtime environment.
 * 
 * @author Robert Langlois
 */
public class UndifferentiatedCellStyle3D implements 
		Style3D<UndifferentiatedCell> {

    
    // The minimum size at which a cell should be displayed.
    private static final double MINIMUM_CELL_SIZE = 0.3;
    

    /**
     * Returns the branch group.
     * 
     * @param agent An instance of undifferentiated cell.
     * @param taggedGroup Tagged group.
     * @return The branch group
     */
    public TaggedBranchGroup getBranchGroup(UndifferentiatedCell agent, 
            TaggedBranchGroup taggedGroup) {
        
        //if (taggedGroup == null || taggedGroup.getTag() == null) {
        
            taggedGroup = new TaggedBranchGroup("DEFAULT");
            Shape3D shape;
            
            if (agent.isAttached()) {
                // Attached cells are displayed as cones.
                shape = ShapeFactory.createCone(.03f, .03f, "DEFAULT");
            } else {
                shape = ShapeFactory.createSphere(.03f, "DEFAULT");
            }
            
            taggedGroup.getBranchGroup().addChild(shape);
            return taggedGroup;
            
        //} // End if()
            
        //return null;
        
    } // End of getBranchGroup()

    
    /**
     * Specify the rotation to apply to the agent.
     * 
     * @param agent An undifferentiated cell instance.
     * @return An array specifying the rotation to apply on each axis or 
     *         {@code null} if no rotation is to be applied.
     */
    public float[] getRotation(UndifferentiatedCell agent) {
        
        // Free roaming cells are not subject to rotation.
        if (!agent.attached) {
            return null;
        }
        
        float[] rotations;
        
        // This attached cell has no polarity?
        if (agent.polarity == null) {
            
            // Skip: it will get one most likely before next time.
            return null;
            
        } else {
            
            /*
             *  Rotate the shape of the cone so as to have the base of the cone
             *  in the direction of the cell's polarity.
             */
            
            float rotationX = 0;
            float rotationY = 0;
            float rotationZ = 0;
            float angle = (float) Math.PI / 2;
            
            if (agent.polarity[0] != 0) {
                
                // x: Rotate 90 degees positive/negative around the z-axis.
                rotationZ = agent.polarity[0];
                
            } else if (agent.polarity[1] != 0) {
                
                // y: Do not rotate if y < 0, otherwise
                //    turn the cone upside down.
                rotationZ = (agent.polarity[1] > 0) ? 1 : 0;
                angle = (float) Math.PI;
                
            } else if (agent.polarity[2] != 0) {
                
                // z: Rotate around the x-axis, but inverse sign.
                rotationX = -agent.polarity[2];
                
            } // End if()
            
            rotations = new float[] { rotationX, rotationY, rotationZ, angle };
            
        } // End if()
        
        return rotations;
        
    } // End of getRotation()
    
    
    /**
     * Specify the label to display on the agent.
     * 
     * @param agent An undifferentiated cell instance.
     * @param currentLabel The current label shown on the agent.
     * @return The label to show on the agent or {@code null} for none.
     */
    public String getLabel(UndifferentiatedCell agent, String currentLabel) {
        return null;
    }
    
    
    /**
     * Specify the colour to apply to the label.
     * 
     * @param agent An undifferentiated cell instance.
     * @param currentColor The label's current colour.
     * @return The label's new colour or {@code null} to use the default colour.
     */
    public Color getLabelColor(UndifferentiatedCell agent, Color currentColor) {
        return null; 
    }

    
    /**
     * Specify the label's font.
     * 
     * @param agent An undifferentiated cell instance.
     * @param currentFont The current font applied to the label.
     * @return The new font to apply to the agent's label.
     */
    public Font getLabelFont(UndifferentiatedCell agent, Font currentFont) {
        return null; 
    }

          
    /**
     * Specify the label's position or orientation relative to the agent.
     * 
     * @param agent An undifferentiated cell instance.
     * @param currentPosition The label's current position.
     * @return The label's new position.
     */
    public LabelPosition getLabelPosition(UndifferentiatedCell agent, 
            Style3D.LabelPosition curentPosition) {    
        return Style3D.LabelPosition.NORTH;
    }

    
    /**
     * Specify the label's offset.
     * 
     * @param agent An undifferentiated cell instance.
     * @return The new label's offset.
     */
    public float getLabelOffset(UndifferentiatedCell agent) {
        return .035f;
    }


    /**
     * Specify the appearance of the agent.
     * 
     * @param agent An undifferentiated cell instance.
     * @param taggedAppearance A tagged appearance.
     * @param shapeID A shape ID.
     * @return The new appearance.
     */
    public TaggedAppearance getAppearance(UndifferentiatedCell agent, 
            TaggedAppearance taggedAppearance, Object shapeID) {
        
        if (taggedAppearance == null || taggedAppearance.getTag() == null) {
            
            taggedAppearance = new TaggedAppearance("DEFAULT");
            
            // Attached cells are pink, free roaming cells are green.
            AppearanceFactory.setMaterialAppearance(
                    taggedAppearance.getAppearance(), 
                    agent.isAttached() ? Color.PINK : Color.GREEN);
            
        } // End if()
        
        return taggedAppearance;
        
    } // End of getAppearance()
    

    /**
     * Specify the scale or magnification of the agent.
     * 
     * @param agent An undifferentiated cell instance.
     * @return An array specifying the scale for each grid axis.
     */
    public float[] getScale(UndifferentiatedCell agent) {
        
        /*
         *  The cell is displayed with a size that is proportional to its
         *  internal concentration of cell growth regulator, i.e. the closer
         *  it is about to divide the bigger it looks.
         */

        float size = (float) (MINIMUM_CELL_SIZE 
                + (agent.getCellDivisionConcentration() 
                        * (1 - MINIMUM_CELL_SIZE)));
        return new float[] { size, size, size };
        
    } // End of getScale()
    

} // End of UndifferentiatedCellStyle3D class
