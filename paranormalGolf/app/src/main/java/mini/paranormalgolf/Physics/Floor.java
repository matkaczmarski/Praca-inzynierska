package mini.paranormalgolf.Physics;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Primitives.BoxMeasurement;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Rectangle;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Floor extends Element {

    public final float[] rgba = new float[]{0.196078f, 0.703922f, 0.296078f, 1f};
    protected final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * 4;

    public BoxMeasurement measurements;
    public float mu;

    public FloorPart topPart;
    public FloorPart bottomPart;
    public List<FloorPart> sideParts;


    public Floor(BoxMeasurement measures, float mu, Point location) {
        super(location);
        this.measurements = measures;
        this.mu = mu;
        createFloor(measures, location);
    }

    private void createFloor(BoxMeasurement measures, Point location) {
        topPart = new FloorPart(new Rectangle(new Point(location.X,location.Y + measures.sizeY / 2, location.Z), measures.sizeX, measures.sizeZ), ObjectBuilder.Axis.yAxis, 1);
        bottomPart = new FloorPart(new Rectangle(new Point(location.X, location.Y-measures.sizeY / 2, location.Z), measures.sizeX, measures.sizeZ), ObjectBuilder.Axis.yAxis, -1);

        FloorPart sidePart1 = new FloorPart(new Rectangle(new Point(location.X + measures.sizeX / 2, location.Y, location.Z), measures.sizeY, measures.sizeZ), ObjectBuilder.Axis.xAxis, 1);
        FloorPart sidePart2 = new FloorPart(new Rectangle(new Point(location.X -measures.sizeX / 2, location.Y, location.Z), measures.sizeY, measures.sizeZ), ObjectBuilder.Axis.xAxis, -1);
        FloorPart sidePart3 = new FloorPart(new Rectangle(new Point(location.X, location.Y, location.Z + measures.sizeZ / 2), measures.sizeX, measures.sizeY), ObjectBuilder.Axis.zAxis, 1);
        FloorPart sidePart4 = new FloorPart(new Rectangle(new Point(location.X, location.Y, location.Z - measures.sizeZ / 2), measures.sizeX, measures.sizeY), ObjectBuilder.Axis.zAxis, -1);

        sideParts = Arrays.asList(sidePart1, sidePart2, sidePart3, sidePart4);
    }

    public void bindData(ShaderProgram colorProgram) {
        vertexData.setVertexAttribPointer(0, ((TextureShaderProgram) colorProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((TextureShaderProgram) colorProgram).getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
    }

}
