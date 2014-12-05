package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

/**
 * Created by Sławomir on 2014-12-03.
 */
public abstract class MovableElement extends Element {

    protected Vector velocity;

    protected MovableElement(Vector velocity, Point location) {
        super(location);
        this.velocity = velocity;
    }

}
