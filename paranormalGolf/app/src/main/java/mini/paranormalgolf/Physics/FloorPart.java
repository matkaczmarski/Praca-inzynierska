package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureLightShaderProgram;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Primitives.Rectangle;

/**
 * Created by Mateusz on 2014-12-13.
 */
public class FloorPart  extends Element {

    protected final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * 4;

    public FloorPart(Rectangle rectangle, ObjectBuilder.Axis axis, float normalVectorDirection) {
        super(rectangle.center);

        GraphicsData generatedData = ObjectGenerator.createFloorPart(rectangle, axis, normalVectorDirection);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

//    public void bindData(ShaderProgram colorProgram) {
//        vertexData.setVertexAttribPointer(0, ((TextureShaderProgram)colorProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
//        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((TextureShaderProgram)colorProgram).getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
//    }

    public void bindData(ShaderProgram shaderProgram) {
        vertexData.setVertexAttribPointer(0, ((TextureLightShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((TextureLightShaderProgram)shaderProgram).getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT, ((TextureLightShaderProgram)shaderProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
    }

}
