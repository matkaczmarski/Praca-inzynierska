package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Primitives.Rectangle;

/**
 * Created by Mateusz on 2014-12-13.
 */
public class FloorPart  extends Element {

    private static final float FLOOR_TEXTURE_UNIT = 5f;

    public FloorPart(Rectangle rectangle, ObjectBuilder.Axis axis, float normalVectorDirection) {
        super(rectangle.center);

        GraphicsData generatedData = ObjectGenerator.createFloorPart(rectangle, axis, normalVectorDirection, FLOOR_TEXTURE_UNIT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }
}
