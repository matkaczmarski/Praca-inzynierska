package mini.paranormalgolf.Graphics;

import java.util.List;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder.DrawCommand;

/**
 * Created by Mateusz on 2014-12-05.
 */
public class GraphicsData {

    public final float[] vertexData;
    public final List<DrawCommand> drawCommands;

    public GraphicsData(float[] vertexData, List<DrawCommand> drawCommands) {
        this.vertexData = vertexData;
        this.drawCommands = drawCommands;
    }
}
