package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.ColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.DepthMapShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShadowingShaderProgram;
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
        //texture = ResourceHelper.loadTexture(context, R.drawable.wall_texture);
        texture = ResourceHelper.loadTexture(context, R.drawable.new_wall_texture3);
    }
//
//    public void bindData(ShaderProgram shaderProgram) {
//        vertexData.setVertexAttribPointer(0, ((TextureShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
//        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((TextureShaderProgram)shaderProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
//        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT, ((TextureShaderProgram)shaderProgram).getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
//    }
//
//    public void bindShadowData(ShaderProgram shaderProgram) {
//        vertexData.setVertexAttribPointer(0, ((ShadowingShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
//        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((ShadowingShaderProgram)shaderProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
//        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT, ((ShadowingShaderProgram)shaderProgram).getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
//    }
//
//    public void bindDepthMapData(ShaderProgram shaderProgram) {
//        vertexData.setVertexAttribPointer(0, ((DepthMapShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
//      }
}
