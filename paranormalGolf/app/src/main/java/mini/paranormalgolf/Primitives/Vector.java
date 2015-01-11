package mini.paranormalgolf.Primitives;

import android.util.FloatMath;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Vector {

    public float x;
    public float y;
    public float z;

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(Point point) {
        x = point.x;
        y = point.y;
        z = point.z;
    }

    public float length() {
        return FloatMath.sqrt(x * x + y * y + z * z);
    }

    public Vector crossProduct(Vector other) {
        return new Vector(
                (y * other.z) - (z * other.y),
                (z * other.x) - (x * other.z),
                (x * other.y) - (y * other.x));
    }

    public float dotProduct(Vector other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector scale(float f) {
        return new Vector(x * f, y * f, z * f);
    }

    public Vector normalize() {
        return scale(1f / length());
    }

    public boolean IsParallelToAxis() {
        return (x == 0 & y == 0) || (x == 0 & z == 0) || (y == 0 & z == 0);
    }
}
