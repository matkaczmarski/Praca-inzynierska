package mini.paranormalgolf.Primitives;

import android.util.FloatMath;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Vector {

    public float X;
    public float Y;
    public float Z;

    public Vector(float x,float y, float z)
    {
        X=x;
        Y=y;
        Z=z;
    }

    public float length() {
        return FloatMath.sqrt(X * X + Y * Y + Z * Z);
    }

    public Vector crossProduct(Vector other) {
        return new Vector(
                (Y * other.Z) - (Z * other.Y),
                (Z * other.X) - (X * other.Z),
                (X * other.Y) - (Y * other.X));
    }

    public float dotProduct(Vector other) {
        return X * other.X + Y * other.Y  + Z * other.Z;
    }

    public Vector scale(float f) {
        return new Vector( X * f, Y * f, Z * f);
    }

    public Vector normalize(){
        return scale(1f/length());
    }
}
