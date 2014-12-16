package mini.paranormalgolf.Physics;

import java.util.Arrays;
import java.util.List;

import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Rectangle;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Floor extends Element {

    public final float FLOOR_OPACITY = 1f;
    protected final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * 4;

    public BoxSize measurements;
    public float mu;

    public FloorPart topPart;
    public FloorPart bottomPart;
    public List<FloorPart> sideParts;


//    public int getTopFloorTexture(){return topFloorTexture;}
//    public  int getSideFloorTexture() {return  sideFloorTexture;}
//    public  int getBottomFloorTexture() {return  bottomFloorTexture;}


    public Floor(BoxSize measures, float mu, Point location) {
        super(location);
        this.measurements = measures;
        this.mu = mu;
        createFloor(measures, location);
    }

    private void createFloor(BoxSize measures, Point location) {
        topPart = new FloorPart(new Rectangle(new Point(location.X,location.Y + measures.y / 2, location.Z), measures.x, measures.z), ObjectBuilder.Axis.yAxis, 1);
        bottomPart = new FloorPart(new Rectangle(new Point(location.X, location.Y-measures.y / 2, location.Z), measures.x, measures.z), ObjectBuilder.Axis.yAxis, -1);

        FloorPart sidePart1 = new FloorPart(new Rectangle(new Point(location.X + measures.x / 2, location.Y, location.Z), measures.y, measures.z), ObjectBuilder.Axis.xAxis, 1);
        FloorPart sidePart2 = new FloorPart(new Rectangle(new Point(location.X -measures.x / 2, location.Y, location.Z), measures.y, measures.z), ObjectBuilder.Axis.xAxis, -1);
        FloorPart sidePart3 = new FloorPart(new Rectangle(new Point(location.X, location.Y, location.Z + measures.z / 2), measures.x, measures.y), ObjectBuilder.Axis.zAxis, 1);
        FloorPart sidePart4 = new FloorPart(new Rectangle(new Point(location.X, location.Y, location.Z - measures.z / 2), measures.x, measures.y), ObjectBuilder.Axis.zAxis, -1);

        sideParts = Arrays.asList(sidePart1, sidePart2, sidePart3, sidePart4);
    }

    public void bindData(ShaderProgram colorProgram) {
        vertexData.setVertexAttribPointer(0, ((TextureShaderProgram) colorProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((TextureShaderProgram) colorProgram).getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
    }

}
