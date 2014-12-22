package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Sławomir on 2014-12-08.
 */
public abstract class ControlPoint extends Element {

    protected ConicalFrustum conicalFrustum;

    public ControlPoint(Point location, ConicalFrustum conicalFrustum) {
        super(location);
        this.conicalFrustum = conicalFrustum;
    }
}
