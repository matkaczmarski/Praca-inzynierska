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
public class Finish extends ControlPoint {

    public final float FINISH_OPACITY = 1f;
    private final int FINISH_PLATFORM_DIMENSION = 32;
    private  final float FINISH_PLATFORM_SHIFT = 0.01f;

    private boolean canFinish;
    private Glow glow;

    private static int finishTextureId;

    public void enableFinishing() {
        canFinish = true;
        glow.activateFinish();
    }

    public int getTexture(){return finishTextureId;}

    public Glow getGlow(){return glow;}

    public Finish(Point location, ConicalFrustum conicalFrustum, boolean canFinish) {
        super(location, conicalFrustum);
        this.canFinish = canFinish;

        GraphicsData generatedData = ObjectGenerator.createControlPointPlatformModel(conicalFrustum.bottomRadius, FINISH_PLATFORM_DIMENSION, FINISH_PLATFORM_SHIFT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        glow = new Glow(location, conicalFrustum, canFinish);
    }

    public static void initTextures(Context context){
        finishTextureId = ResourceHelper.loadTexture(context, R.drawable.finish_texture);
    }

    public boolean isCanFinish()
    {
        return canFinish;
    }

    public void setCanFinish(boolean canFinish)
    {
        this.canFinish = canFinish;
    }
}
