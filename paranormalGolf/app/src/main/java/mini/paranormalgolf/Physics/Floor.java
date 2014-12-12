package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.ColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.LightColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Primitives.CuboidMeasurement;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Floor extends Element {

    public final float[] rgba = new float[] { 0.196078f, 0.703922f, 0.296078f, 1f};
    public CuboidMeasurement measurements;
    public float mu;

    public Floor(CuboidMeasurement measures,float mu, Point location) {
        super(location);
        this.measurements=measures;
        this.mu=mu;

        GraphicsData generatedData = ObjectGenerator.createFloor(location, measures.sizeX, measures.sizeY, measures.sizeZ);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

//    public void bindData(ShaderProgram shaderProgram){
//        //TODO dodać bindowanie Z texture program
//        vertexData.setVertexAttribPointer(0, ((ColorShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, 0);
//    }
        public void bindData(ShaderProgram colorProgram) {
        vertexData.setVertexAttribPointer(0, ((TextureShaderProgram)colorProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, (POSITION_COMPONENT_COUNT + 2) * 4);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((TextureShaderProgram)colorProgram).getTextureCoordinatesAttributeLocation(), 2, (POSITION_COMPONENT_COUNT + 2) * 4);
    }

//    public void bindData(ShaderProgram colorProgram) {
//        vertexData.setVertexAttribPointer(0, ((LightColorShaderProgram)colorProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
//        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((LightColorShaderProgram)colorProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
//    }
}
