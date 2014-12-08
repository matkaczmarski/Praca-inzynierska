package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Primitives.Cylinder;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Sławomir on 2014-12-08.
 */
public class Start extends ControlPoint {

    public Start(Point location, Cylinder cylinder){
        super(location,cylinder);
    }

    @Override
    public void bindData(ShaderProgram shaderProgram) {

    }
}
