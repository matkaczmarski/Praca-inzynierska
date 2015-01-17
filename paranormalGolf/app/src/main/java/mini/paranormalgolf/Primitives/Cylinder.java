package mini.paranormalgolf.Primitives;

/**
 * Reprezentuje walec umiejscowiony w globalnym układzie współrzędnych,
 * a jego wysokość jest równoległa do osi OY układu współrzędnych.
 *
 */
public class Cylinder {

     /**
     * Współrzędne środka walca w gloabalnym układzie współrzędnych.
     */
    public Point center;

    /**
     * Długość promienia podstawy walca.
     */
    public float radius;

    /**
     * Wysokość walca.
     */
    public float height;

    /**
     * Tworzy obiekt typu walec.
     * @param center Współrzędne środka walca w globalnym układzie współrzędnych.
     * @param radius Długość promienia podstawy walca.
     * @param height Wysokość walca.
     */
    public Cylinder(Point center, float radius, float height){
        this.center = center;
        this.radius = radius;
        this.height = height;
    }
}
