package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Primitives.CuboidMeasurement;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Wall extends Element {
    private CuboidMeasurement measurements;

    public Wall(CuboidMeasurement measure,Point location) {
        super(location);
        measurements = measure;
    }
}
