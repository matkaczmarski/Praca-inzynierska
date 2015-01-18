package mini.paranormalgolf.Primitives;

/**
 * Reprezentuje prostopadłościan równoległy do osi układu współrzędnych umiejscowiony w globalnym układzie współrzędnych.
 */
public class Box {


    /**
     * Współrzędne środka prostopadłościanu w gloabalnym układzie współrzędnych.
     */
    public Point center;

    /**
     * Rozmiar prostopadłościanu.
     */
    public BoxSize size;

    /**
     * Tworzy obiekt typu prostopadłościan.
     * @param _center Współrzędna środka prostopadłościanu w gloabalnym układzie współrzędnych.
     * @param _size Rozmiar prostopadłościanu.
     */
    public Box(Point _center, BoxSize _size) {
        center = _center;
        size = _size;
    }
}
