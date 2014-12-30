package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Primitives.Box;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Sphere;

/**
 * Created by Sławomir on 2014-12-29.
 */
public final class Collisions {
    public static boolean CheckSphereAABBCollsion(Sphere sphere,Box box) {

        Point boxCenter = box.center;
        BoxSize boxSize = box.size;
        Point sphereCenter = sphere.center;
        float sphereRadius = sphere.radius;
        Point min = new Point(boxCenter.x - boxSize.x / 2, boxCenter.y - boxSize.y / 2, boxCenter.z - boxSize.z / 2);
        Point max = new Point(boxCenter.x + boxSize.x / 2, boxCenter.y + boxSize.y / 2, boxCenter.z + boxSize.z / 2);
        float d = 0;
        if (sphereCenter.x < min.x) {
            d += (sphereCenter.x - min.x) * (sphereCenter.x - min.x);
        } else if (sphereCenter.x > max.x) {
            d += (sphereCenter.x - max.x) * (sphereCenter.x - max.x);
        }

        if (sphereCenter.y < min.y) {
            d += (sphereCenter.y - min.y) * (sphereCenter.y - min.y);
        } else if (sphereCenter.y > max.y) {
            d += (sphereCenter.y - max.y) * (sphereCenter.y - max.y);
        }

        if (sphereCenter.z < min.z) {
            d += (sphereCenter.z - min.z) * (sphereCenter.z - min.z);
        } else if (sphereCenter.z > max.z) {
            d += (sphereCenter.z - max.z) * (sphereCenter.z - max.z);
        }
        return d <= sphereRadius * sphereRadius;
    }

}
