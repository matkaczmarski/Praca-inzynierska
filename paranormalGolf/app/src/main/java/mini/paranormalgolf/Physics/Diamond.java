package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Pyramid;
import mini.paranormalgolf.R;

/**
 * Created by Sławomir on 2014-12-08.
 */
public class Diamond extends Bonus {

    public final float DIAMOND_OPACITY = 0.9f;

    private static int diamondTexture;

    public Pyramid getPyramid() {
        return pyramid;
    }

    private final Pyramid pyramid = new Pyramid(0.7f, 1.4f, 6);

    public Diamond(Point location, int value, float yShift) {
        super(location, value, yShift);
        GraphicsData generatedData = ObjectGenerator.createDiamondModel(pyramid);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        texture = diamondTexture;//ResourceHelper.loadTexture(context, R.drawable.diamond_texture);
    }

    public static void initTextures(Context context)
    {
        diamondTexture = ResourceHelper.loadTexture(context, R.drawable.diamond_texture);
    }

    public static int getDiamondTexture()
    {
        return diamondTexture;
    }
}
