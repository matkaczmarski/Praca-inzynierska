package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.ColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Primitives.CuboidMeasurement;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Floor extends Element {

    public final float[] rgba = new float[] {0f, 0f, 1f, 1f};
    private CuboidMeasurement measurements;
    private float mu;

    public Floor(CuboidMeasurement measures,float mu, Point location) {
        super(location);
        this.measurements=measures;
        this.mu=mu;

        GraphicsData generatedData = ObjectGenerator.createFloor(location, measures.sizeX, measures.sizeY);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

    public void bindData(ShaderProgram shaderProgram){
        //TODO dodać bindowanie z texture program
        vertexData.setVertexAttribPointer(0, ((ColorShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, 0);
    }
}
