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
    protected final int NORMAL_COMPONENT_COUNT = 3;
    protected final int TEXTURE_COMPONENT_COUNT = 2;


    protected Point location;
    protected int texture;
    protected VertexArray vertexData;
    protected List<DrawCommand> drawCommands;

    public Point getLocation() { return this.location;}
    public void setLocation(Point location) { this.location = location;}

    public int getTexture() {return  this.texture;}

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
