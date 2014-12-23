package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.LightColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Mateusz on 2014-12-22.
 */
public class Glow extends ControlPoint {

    public final float GLOW_OPACITY = 0.75f;
    private final int GLOW_MESH_DIMENSION = 32;
    private final int STRIDE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * 4;

    private boolean canFinish;

    public final float[] CAN_FINISH_COLOR = new float[] {0.678431f, 1.0f, 0.184314f, GLOW_OPACITY};
    public final float[] CANNOT_FINISH_COLOR = new float[] {1f, 0.388235f, 0.278431f, GLOW_OPACITY};

    public boolean getIfCanFinish() {return canFinish;}

    public Glow(Point location, ConicalFrustum conicalFrustum, boolean canFinish) {
        super(location, conicalFrustum);
        this.canFinish = canFinish;

        GraphicsData generatedData = ObjectGenerator.createControlPointGlow(conicalFrustum, GLOW_MESH_DIMENSION);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

    @Override
    public void bindData(ShaderProgram shaderProgram) {
        vertexData.setVertexAttribPointer(0, ((LightColorShaderProgram) shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((LightColorShaderProgram) shaderProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
    }
}
