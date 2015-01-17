package mini.paranormalgolf.Primitives;

/**
 * Reprezentuje punkt w przestrzeni 3D.
 */
public class Point {

    /**
     * Wartość współrzędnej x.
     */
    public float x;

    /**
     * Wartość współrzędnej y.
     */
    public float y;

    /**
     * Wartość współrzędnej z.
     */
    public float z;

    /**
     * Tworzy obiekt typu punkt.
     * @param x Wartość współrzędnej x.
     * @param y Wartość współrzędnej y.
     * @param z Wartość współrzędnej z.
     */
    public Point(float x,float y, float z)
    {
        this.x =x;
        this.y =y;
        this.z =z;
    }


    /**
     * Tworzy nowy punkt będący różnicą po współrzędnych danego punktu i punktu <b><em>other</em></b>.
     * @param other Punkt, którego współrzędne używamy jako odjemniki.
     * @return Punkt będący różnicą po współrzędnych danego punktu i punktu <b><em>other</em></b>.
     */
    public Point Substract(Point other){
        return new Point(this.x - other.x, this.y - other.y, this.z - other.z);
    }
}
