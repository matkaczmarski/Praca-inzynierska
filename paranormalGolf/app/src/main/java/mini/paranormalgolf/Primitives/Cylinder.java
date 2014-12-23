package mini.paranormalgolf.Primitives;

/**
 * Created by Mateusz on 2014-12-23.
 */
public class Cylinder {
    private Point center;
    private float radius;
    private float height;

    public Point getCenter(){return center;}
    public float getRadius(){return radius;}
    public float getHeight(){return height;}

    public Cylinder(Point center, float radius, float height){
        this.center = center;
        this.radius = radius;
        this.height = height;
    }
}
