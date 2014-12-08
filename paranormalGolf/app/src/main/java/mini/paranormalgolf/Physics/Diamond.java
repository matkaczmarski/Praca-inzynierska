package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Pyramid;

/**
 * Created by Sławomir on 2014-12-08.
 */
public class Diamond extends Bonus {

    private Pyramid lowerPyramid;
    private Pyramid upperPyramid;

    public Diamond(Point location, int value, Pyramid upperPyramid, Pyramid lowerPyramid) {
        super(location, value);
        this.lowerPyramid = lowerPyramid;
        this.upperPyramid = upperPyramid;
    }

    @Override
    public void bindData(ShaderProgram shaderProgram) {

    }
}
