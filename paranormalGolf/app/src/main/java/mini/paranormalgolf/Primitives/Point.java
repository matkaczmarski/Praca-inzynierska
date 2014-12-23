package mini.paranormalgolf.Primitives;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Point {

    public float X;
    public float Y;
    public float Z;

    public Point(float x,float y, float z)
    {
        this.X =x;
        this.Y =y;
        this.Z =z;
    }


    public Point Add(Point other){
        return new Point(this.X + other.X, this.Y + other.Y, this.Z + other.Z);
    }

    public Point Substract(Point other){
        return new Point(this.X - other.X, this.Y - other.Y, this.Z - other.Z);
    }
}
