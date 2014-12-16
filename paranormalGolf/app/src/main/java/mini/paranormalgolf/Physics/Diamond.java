package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureLightShaderProgram;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Pyramid;
import mini.paranormalgolf.R;

/**
 * Created by Sławomir on 2014-12-08.
 */
public class Diamond extends Bonus {

    public final float DIAMOND_OPACITY = 0.85f;
    private final float DIAMOND_ROTATION_SPEED = 1f;
    private float diamondCurrentRotation;

    protected final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * 4;

    private Pyramid pyramid;

    public Diamond(Point location, int value, Pyramid pyramid, Context context) {
        super(location, value);
        this.pyramid = pyramid;
        GraphicsData generatedData = ObjectGenerator.createDiamond(pyramid);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        texture = ResourceHelper.loadTexture(context, R.drawable.diamond_texture);
        diamondCurrentRotation = 0f;
    }

    public float rotate(){
        diamondCurrentRotation = (diamondCurrentRotation + DIAMOND_ROTATION_SPEED) % 360;
        return  diamondCurrentRotation;
    }

    @Override
    public void bindData(ShaderProgram shaderProgram) {
        vertexData.setVertexAttribPointer(0, ((TextureLightShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((TextureLightShaderProgram)shaderProgram).getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT, ((TextureLightShaderProgram)shaderProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
    }
}
