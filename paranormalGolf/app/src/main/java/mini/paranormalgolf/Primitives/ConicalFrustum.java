package mini.paranormalgolf.Primitives;

/**
 * Przechowuje rozmiar ściętego stożka, którego wysokość jest równoległa do osi OY.
 */
public class ConicalFrustum {

    /**
     * Długość promienia dolnej podstawy stożka.
     */
    public float bottomRadius;

    /**
     * Długość promienia górnej podstawy stożka.
     */
    public float topRadius;

    /**
     * Wysokość stożka.
     */
    public float height;

    /**
     * Tworzy obiekt typu ścięty stożek.
     * @param height Wysokość stożka.
     * @param bottomRadius Długość promienia dolnej podstawy stożka.
     * @param topRadius Długość promienia górnej podstawy stożka.
     */
    public ConicalFrustum(float height, float bottomRadius, float topRadius) {
        this.height = height;
        this.bottomRadius = bottomRadius;
        this.topRadius = topRadius;
    }
}
