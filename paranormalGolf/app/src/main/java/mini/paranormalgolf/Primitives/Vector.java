package mini.paranormalgolf.Primitives;

import android.util.FloatMath;

/**
 * Reprezentuje wektor w przestrzeni 3D.
 */
public class Vector {

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
     * Tworzy obiekt typu wektor.
     * @param x Wartość współrzędnej x.
     * @param y Wartość współrzędnej y.
     * @param z Wartość współrzędnej z.
     */
    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Tworzy obiekt typu wektor o wartościach współrzędnych pobranych z <b><em>point</em></b>.
     * @param point Punkt konwertowany do obiektu typu Vector.
     */
    public Vector(Point point) {
        x = point.x;
        y = point.y;
        z = point.z;
    }

    /**
     * Zwraca długość wektora.
     * @return Długość wektora.
     */
    public float length() {
        return FloatMath.sqrt(x * x + y * y + z * z);
    }


    /**
     * Oblicza iloczyn skalarny danego wektora z wektorem <b><em>other</em></b>.
     * @param other Wektor używany przy obliczaniu iloczynu skalarnego.
     * @return Wartość iloczynu skalarnego.
     */
    public float dotProduct(Vector other) {
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * Tworzy przeskalowany wektor.
     * @param f Parametr skalujący wektora.
     * @return Przeskalowany wektor.
     */
    public Vector scale(float f) {
        return new Vector(x * f, y * f, z * f);
    }

    /**
     * Na podstawie danego wektora tworzy wektor znormalizowany.
     * @return Wektor znormalizowany dla danego wektora.
     */
    public Vector normalize() {
        return scale(1f / length());
    }

    /**
     * Sprawdza, czy wektor jest równoległy do osi układu współrzędnych.
     * @return Informacja, czy wektor jest równoległy do osi układu współrzędnych.
     */
    public boolean IsParallelToAxis() {
        return (x == 0 & y == 0) || (x == 0 & z == 0) || (y == 0 & z == 0);
    }
}
