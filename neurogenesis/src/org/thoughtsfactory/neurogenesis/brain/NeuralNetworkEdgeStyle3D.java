package org.thoughtsfactory.neurogenesis.brain;

import java.awt.Color;
import java.awt.Font;

import javax.media.j3d.Shape3D;

import repast.simphony.space.graph.RepastEdge;
import repast.simphony.visualization.visualization3D.AppearanceFactory;
import repast.simphony.visualization.visualization3D.ShapeFactory;
import repast.simphony.visualization.visualization3D.style.EdgeStyle3D;
import repast.simphony.visualization.visualization3D.style.TaggedAppearance;
import repast.simphony.visualization.visualization3D.style.TaggedBranchGroup;


/**
 * Class that provides the 3D style applied to neural network edges in the 3D
 * display of the simulation runtime environment.
 * 
 * @author Robert Langlois
 */
public class NeuralNetworkEdgeStyle3D<T> implements EdgeStyle3D<T> {
   
    
    /**
     * The radius of a network edge. 
     */
    protected float radius = .01f;
      
    
    /**
     * Returns the branch group.
     * 
     * @return The branch group.
     */
    public TaggedBranchGroup getBranchGroup(
            T o, TaggedBranchGroup taggedGroup) {

        if (taggedGroup == null || taggedGroup.getTag() == null) {
            taggedGroup = new TaggedBranchGroup("DEFAULT");
            Shape3D shape = ShapeFactory.createCylinder(radius, 1f, "DEFAULT");
            taggedGroup.getBranchGroup().addChild(shape);
            return taggedGroup;
        }
        
        return null;
        
    } // End of getBranchGroup()
    
    
    /**
     * Returns the edge radius.
     * 
     * @return The edge radius.
     */
    public float edgeRadius(T o) {
        return radius;
    }

    
    /**
     * Returns the edge type.
     * 
     * @return The edge type.
     */
    public EdgeType getEdgeType() {
        return EdgeType.SHAPE;
    }

    
    /**
     * Return the label associated with an edge. Currently this is the
     * connection weight between the two neurons.
     * 
     * @return The label as a string.
     */
    public String getLabel(T o, String currentLabel) {
        
        @SuppressWarnings("unchecked" )
        RepastEdge<Object> edge = (RepastEdge<Object>) o;
        return String.format("%+.2f", edge.getWeight());
        
    } // End of getLabel()
    
    
    /**
     * Return the label's colour.
     * 
     * @return The label's colour, or {@code null} to use the default colour.
     */
    public Color getLabelColor(T t, Color currentColor) {
        return null;
    }

    
    /**
     * Returns the font to use for the label.
     * 
     * @return The font to use for the label, or {@code null} to use the 
     *         default font.
     */
    public Font getLabelFont(T t, Font currentFont) {
            return null;
    }

    
    /**
     * Returns the label's position.
     * 
     * @return The label's position.
     */
    public EdgeStyle3D.LabelPosition getLabelPosition(
            T o, LabelPosition curentPosition) {
        return LabelPosition.NORTH;
    }

    
    /**
     * Returns the label's offset.
     * 
     * @return The label's offset.
     */
    public float getLabelOffset(T t) {
        return .015f;
    }

    
    /**
     * Returns the scale factor.
     * 
     * @returns The scale factor.
     */
    public float[] getScale(T o) {
        return null;
    }

    
    /**
     * Returns the rotation applied to the edge.
     * 
     * @return A float array giving the rotation of the edge on each axis, or
     *         {@code null} for no rotation.
     */
    public float[] getRotation(T t) {
        return null;
    }
    
    
    /**
     * Returns the appearance of the edge.
     * 
     * @return The appearance of the edge.
     */
    public TaggedAppearance getAppearance(
            T t, TaggedAppearance taggedAppearance, Object shapeID) {
        
        if (taggedAppearance == null || taggedAppearance.getTag() == null) {
            taggedAppearance = new TaggedAppearance("DEFAULT");
            AppearanceFactory.setMaterialAppearance(
                    taggedAppearance.getAppearance(), Color.GRAY);
            return taggedAppearance;
        }

        return null;
        
    } // End of getAppearance()

      
} // End of NeuralNetworkEdgeStyle3D class
