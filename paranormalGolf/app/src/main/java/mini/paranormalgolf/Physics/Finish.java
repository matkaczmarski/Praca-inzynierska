package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.DepthMapShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShadowingShaderProgram;
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

    public void enableFinishing() {
        canFinish = true;
        glow.enableFinishing();
    }

    public Glow getGlow(){return glow;}

    public Finish(Point location, ConicalFrustum conicalFrustum, boolean canFinish, Context context) {
        super(location, conicalFrustum);
        this.canFinish = canFinish;

        GraphicsData generatedData = ObjectGenerator.createControlPointPlatform(conicalFrustum.bottomRadius, FINISH_PLATFORM_DIMENSION, FINISH_PLATFORM_SHIFT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        texture = ResourceHelper.loadTexture(context, R.drawable.finish_texture);
        glow = new Glow(location, conicalFrustum, canFinish);
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
