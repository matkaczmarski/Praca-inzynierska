package mini.paranormalgolf.Primitives;

/**
 * Reprezentuje koło zawarte w płaszczyźnie równoległej do płaszczyzny OXZ umiejscowione w globalnym układzie współrzędnych.
 */
public class Circle {

    /**
     * Współrzędne środka koła.
     */
    public Point center;

    /**
     * Długość promienia koła.
     */
    public float radius;

    /**
     * Tworzy obiekt typu koło.
     * @param center Współrzędne środka koła.
     * @param radius Długość promienia koła.
     */
    public Circle(Point center,float radius){
        this.center=center;
        this.radius=radius;
    }
}
