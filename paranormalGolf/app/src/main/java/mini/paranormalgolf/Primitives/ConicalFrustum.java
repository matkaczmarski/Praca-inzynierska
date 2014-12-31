package mini.paranormalgolf.Primitives;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class ConicalFrustum {
    private float bottomRadius;
    private float topRadius;
    private float height;

    public float getBottomRadius(){return bottomRadius;}
    public float getTopRadius(){return  topRadius;}
    public float getHeight(){return  height;}

    public ConicalFrustum(float height, float bottomRadius, float topRadius) {
        this.height = height;
        this.bottomRadius = bottomRadius;
        this.topRadius = topRadius;
    }
}
