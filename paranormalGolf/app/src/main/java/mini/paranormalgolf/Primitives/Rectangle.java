package mini.paranormalgolf.Primitives;

/**
 * Reprezentuje prostokąt w globalnym układzie współrzędnych.
 */
public class Rectangle {

    /**
     * Współrzędne środka prostokąta w globalnym układzie współrzędnych.
     */
    public Point center;

    /**
     * Długość pierwszego boku prostokąta.
     */
    public float a;

    /**
     * Długość drugiego boku prostokąta.
     */
    public float b;

    /**
     * Tworzy obiekt typu prostokąt.
     * @param center Współrzędne środka prostokąta w globalnym układzie współrzędnych.
     * @param a Długość pierwszego boku prostokąta.
     * @param b Długość drugiego boku prostokąta.
     */
    public Rectangle(Point center, float a, float b){
        this.center = center;
        this.a = a;
        this.b = b;
    }
}
