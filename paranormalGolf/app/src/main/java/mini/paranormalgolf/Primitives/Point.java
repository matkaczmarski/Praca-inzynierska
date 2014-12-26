package mini.paranormalgolf.Primitives;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Point {

    public float x;
    public float y;
    public float z;

    public Point(float x,float y, float z)
    {
        this.x =x;
        this.y =y;
        this.z =z;
    }


    public Point Add(Point other){
        return new Point(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Point Substract(Point other){
        return new Point(this.x - other.x, this.y - other.y, this.z - other.z);
    }
}
