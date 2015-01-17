package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.R;

/**
 * Created by Sławomir on 2014-12-08.
 */
public class CheckPoint extends ControlPoint {

    public final float CHECKPOINT_OPACITY = 1f;
    private final int CHECKPOINT_PLATFORM_DIMENSION = 32;
    private  final float CHECKPOINT_PLATFORM_SHIFT = 0.01f;

    private boolean visited;
    private final Glow glow;
    private static int checkPointTextureId;

    public boolean isVisited(){return visited;}

    public void visit(){visited = true;}

    public Glow getGlow(){return glow;}

    public static int getTexture(){return checkPointTextureId;}

    public CheckPoint(Point location, ConicalFrustum conicalFrustum){
        super(location,conicalFrustum);
        visited=false;

        GraphicsData generatedData = ObjectGenerator.createControlPointPlatformModel(conicalFrustum.bottomRadius, CHECKPOINT_PLATFORM_DIMENSION, CHECKPOINT_PLATFORM_SHIFT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        glow = new Glow(location, conicalFrustum);
    }

    public static void initTextures(Context context){
        checkPointTextureId = ResourceHelper.loadTexture(context, R.drawable.checkpoint_texture);
    }
}
