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
public class CheckPoint extends ControlPoint {

    public final float CHECKPOINT_OPACITY = 1f;
    private final int CHECKPOINT_PLATFORM_DIMENSION = 32;
    private  final float CHECKPOINT_PLATFORM_SHIFT = 0.0001f;

    private boolean visited;
    private Glow glow;

    public Glow getGlow(){return glow;}
    public boolean isVisited(){return visited;}
    public void visit(){visited = true;}

    public CheckPoint(Point location, ConicalFrustum conicalFrustum, boolean visited, Context context){
        super(location,conicalFrustum);
        this.visited=visited;

        GraphicsData generatedData = ObjectGenerator.createControlPointPlatform(conicalFrustum.bottomRadius, CHECKPOINT_PLATFORM_DIMENSION, CHECKPOINT_PLATFORM_SHIFT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        texture = ResourceHelper.loadTexture(context, R.drawable.checkpoint_texture);
        glow = new Glow(location, conicalFrustum, true);
    }
}
