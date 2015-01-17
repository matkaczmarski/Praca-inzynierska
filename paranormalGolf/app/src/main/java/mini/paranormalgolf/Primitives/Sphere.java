package mini.paranormalgolf.Primitives;

/**
 * Reprezentuje kulę umiejscowioną w globalnym układzie współrzędnych.
 */
public class Sphere {

    /**
     * Współrzędne środka kuli w globalnym układzie współrzędnych.
     */
    public Point center;

    /**
     * Wartość promienia kuli.
     */
    public float radius;

    /**
     * Tworzy obiekt typu kula.
     * @param center Współrzędne środka kuli w globalnym układzie współrzędnych.
     * @param radius Wartość promienia kuli.
     */
    public Sphere(Point center, float radius){
        this.center = center;
        this.radius = radius;
    }
}
