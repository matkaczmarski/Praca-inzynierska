package mini.paranormalgolf.Primitives;

/**
 * Created by Sławomir on 2014-12-08.
 */
public class Pyramid {

    public float height;
    public float radius;
    public int baseVerticesCount;

    //tutaj location - środek podstawy ostrosłupa
    public Pyramid(float radius, float height, int verticesCount){
        this.radius = radius;
        this.height = height;
        this.baseVerticesCount = verticesCount;
    }
}
