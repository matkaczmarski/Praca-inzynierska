package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Primitives.CuboidMeasurement;
import mini.paranormalgolf.Primitives.Point;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Floor extends Element {

    private CuboidMeasurement measurements;
    private float mu;

    public Floor(CuboidMeasurement measure,float _mu, Point _location) {
        super(_location);
        measurements=measure;
        mu=_mu;
    }
}
