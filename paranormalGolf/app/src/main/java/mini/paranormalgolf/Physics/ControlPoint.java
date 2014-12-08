package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Primitives.Cylinder;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Sławomir on 2014-12-08.
 */
public abstract class ControlPoint extends Element {

    protected Cylinder cylinder;

    public ControlPoint(Point location, Cylinder cylinder) {
        super(location);
        this.cylinder = cylinder;
    }
}
