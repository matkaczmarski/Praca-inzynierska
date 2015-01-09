package mini.paranormalgolf.Primitives;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class ConicalFrustum {
    public float bottomRadius;
    public float topRadius;
    public float height;

    public ConicalFrustum(float height, float bottomRadius, float topRadius) {
        this.height = height;
        this.bottomRadius = bottomRadius;
        this.topRadius = topRadius;
    }
}
