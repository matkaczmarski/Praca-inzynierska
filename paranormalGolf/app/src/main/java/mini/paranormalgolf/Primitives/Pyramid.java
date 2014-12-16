package mini.paranormalgolf.Primitives;

/**
 * Created by Sławomir on 2014-12-08.
 */
public class Pyramid {

    public float height;
    public float radius;
    public Vector axis;
    public int baseVerticesCount;

    //tutaj location - środek podstawy ostrosłupa
    public Pyramid(float radius, float height, Vector axis, int verticesCount){
        this.radius = radius;
        this.height = height;
        this.axis = axis;
        this.baseVerticesCount = verticesCount;
    }
}
