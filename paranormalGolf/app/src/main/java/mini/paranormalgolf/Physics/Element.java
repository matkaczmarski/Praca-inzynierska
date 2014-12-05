package mini.paranormalgolf.Physics;

import java.util.List;

import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder.DrawCommand;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;

/**
 * Created by Sławomir on 2014-12-03.
 */
public abstract class Element {

    protected final int POSITION_COMPONENT_COUNT = 3;

    protected Point location;
    protected VertexArray vertexData;
    protected List<DrawCommand> drawCommands;

    protected Element(Point location){
     this.location=location;
    }

    public abstract void bindData(ShaderProgram shaderProgram);

    public void draw() {
        for (DrawCommand drawCommand : drawCommands) {
            drawCommand.draw();
        }
    }
}
