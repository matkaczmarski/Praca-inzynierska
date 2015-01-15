package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Mateusz on 2014-12-22.
 */
public class Glow extends ControlPoint {

    public final float GLOW_OPACITY = 0.65f;
    private final int GLOW_MESH_DIMENSION = 32;

    private boolean canFinish;

    public final float[] CAN_FINISH_COLOR = new float[] {0.678431f, 1.0f, 0.184314f, GLOW_OPACITY};
    public final float[] CANNOT_FINISH_COLOR = new float[] {1f, 0.388235f, 0.278431f, GLOW_OPACITY};
    public final float[] ACTIVE_COLOR = new float[] {1f, 1f, 0f, GLOW_OPACITY};

    public boolean ifCanFinish() {return canFinish;}
    public void enableFinishing(){canFinish = true;}

    public Glow(Point location, ConicalFrustum conicalFrustum, boolean canFinish) {
        super(location, conicalFrustum);
        this.canFinish = canFinish;

        GraphicsData generatedData = ObjectGenerator.createControlPointGlowModel(conicalFrustum, GLOW_MESH_DIMENSION);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }
}
