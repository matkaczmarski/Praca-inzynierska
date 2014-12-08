package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Primitives.Cylinder;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Sławomir on 2014-12-08.
 */
public class Finish extends ControlPoint {

    private boolean canFinish;

    public Finish(Point location, Cylinder cylinder, boolean canFinish) {
        super(location, cylinder);
        this.canFinish = canFinish;
    }

    @Override
    public void bindData(ShaderProgram shaderProgram) {

    }
}
