package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.R;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Wall extends Element {

    public BoxSize getMeasurements() {
        return measurements;
    }

    private BoxSize measurements;

    private static final float WALL_TEXTURE_UNIT = 5f;
    public final float WALL_OPACITY = 1f;

    public Wall(Point location, BoxSize measure, Context context) {
        super(location);
        measurements = measure;

        GraphicsData generatedData = ObjectGenerator.createBox(measure, WALL_TEXTURE_UNIT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        texture = ResourceHelper.loadTexture(context, R.drawable.wall_texture);
    }

}
