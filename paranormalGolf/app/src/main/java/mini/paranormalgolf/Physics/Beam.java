package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureLightShaderProgram;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;
import mini.paranormalgolf.R;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Beam extends MovableElement {

    private static final float BEAM_TEXTURE_UNIT = 5f;
    public final float BEAM_OPACITY = 1f;
    private final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * 4;

    private BoxSize measurements;

    //punkty oznaczjące do jakiego miejsca ma dochodzić środek elementu
    private Point patrolFrom;
    private Point patrolTo;
  //  private float mu;

    public Beam(Point location, Vector velocity, BoxSize measure, Point from, Point to/*, float mu*/, Context context) {
        super(velocity, location);
        this.measurements = measure;
        this.patrolFrom = from;
        this.patrolTo = to;
        //  this.mu = mu;

        GraphicsData generatedData = ObjectGenerator.createBox(measure, BEAM_TEXTURE_UNIT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        texture = ResourceHelper.loadTexture(context, R.drawable.beam_texture);
    }

    @Override
    public void bindData(ShaderProgram shaderProgram) {
        vertexData.setVertexAttribPointer(0, ((TextureLightShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((TextureLightShaderProgram)shaderProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT, ((TextureLightShaderProgram)shaderProgram).getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
    }

    public void Update(float dt) {
        //zmniejszony update ze względu na to, że to jest bar
        location.x = location.x + velocity.x * dt;
        location.z = location.z + velocity.z * dt;
        if (location.x > patrolTo.x || location.x < patrolFrom.x ||
                location.z > patrolTo.z || location.z < patrolFrom.z) {
            velocity.x = -velocity.x;
            velocity.z = -velocity.z;
        }
    }
}
