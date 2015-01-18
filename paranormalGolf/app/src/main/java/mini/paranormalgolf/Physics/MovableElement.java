package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;

/**
 * Klasa abstrakcyjna reprezentująca element poruszający się w grze.
 */
public abstract class MovableElement extends Element {

    /**
     * Aktualny wektor prędkości elementu.
     */
    protected Vector velocity;

    /**
     * Przesunięcie przebyte pomiędzy 2 klatkami symulacji przez element, wyrażone jako wektor.
     */
    protected Vector lastMove;

    /**
     * Zwraca aktualny wektor prędkości elementu.
     * @return Obiekt <b><em>velocity</em></b>.
     */
    public Vector getVelocity() {return velocity;}

    /**
     * Ustawia aktualny wektor prędkości elementu.
     * @param velocity Nowy wektor prędkości elementu.
     */
    public void setVelocity(Vector velocity) {this.velocity = velocity;}

    /**
     * Zwraca przesunięcie przebyte pomiędzy 2 klatkami symulacji przez element, wyrażone jako wektor.
     * @return Obiekt <b><em>lastMove</em></b>.
     */
    public Vector getLastMove() {
        return lastMove;
    }

    /**
     * Tworzy obiekt typu <em>MovableElement</em>.
     * @param velocity Wektor początkowej prędkości elementu.
     * @param location Współrzędne środka elementu w globalnym układzie współrzędnych.
     */
    protected MovableElement(Vector velocity, Point location) {
        super(location);
        this.velocity = velocity;
        lastMove=new Vector(0,0,0);
    }

}
