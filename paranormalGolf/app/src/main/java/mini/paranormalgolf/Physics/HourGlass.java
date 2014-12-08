package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Primitives.Cone;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Sławomir on 2014-12-08.
 */

public class HourGlass extends Bonus {

    private Cone lowerCone;
    private Cone upperCone;

    public HourGlass(Point location, int value, Cone lowerCone, Cone upperCone) {
        super(location, value);
        this.lowerCone = lowerCone;
        this.upperCone = upperCone;
    }

    @Override
    public void bindData(ShaderProgram shaderProgram) {

    }
}
