package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Point;

/**
 * Klasa abstrakcyjna reprezentująca element w grze, który należy odwiedzić, aby zaliczyć lub ukończyć poziom.
 * W grze reprezentowana w postaci ściętego stożka.
 */
public abstract class ControlPoint extends Element {

    /**
     * Rozmiar ściętego stożka opisującego obiekt typu <em>ControlPoint</em>.
     */
    protected ConicalFrustum conicalFrustum;

    /**
     * Tworzy obiekt typu <em>ControlPoint</em>.
     * @param location Współrzędne środka dolnej podstawy w globalnym układzie współrzędnych.
     * @param conicalFrustum Rozmiar ściętego stożka opisującego obiekt typu <em>ControlPoint</em>.
     */
    public ControlPoint(Point location, ConicalFrustum conicalFrustum) {
        super(location);
        this.conicalFrustum = conicalFrustum;
    }
}
