package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Primitives.Cylinder;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Sławomir on 2014-12-08.
 */
public class CheckPoint extends ControlPoint {

    private boolean visited;

    public CheckPoint(Point location, Cylinder cylinder, boolean visited){
        super(location,cylinder);
        this.visited=visited;
    }

    @Override
    public void bindData(ShaderProgram shaderProgram) {

    }
}
