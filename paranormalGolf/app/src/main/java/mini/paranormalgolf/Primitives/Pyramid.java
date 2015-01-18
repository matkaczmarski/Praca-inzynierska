package mini.paranormalgolf.Primitives;

/**
 * Przechowuje rozmiar ostrosłupa, którego wysokość jest równoległa do osi OY układu współrzędnych.
 */
public class Pyramid {

    /**
     * Wysokość ostrosłupa.
     */
    public float height;

    /**
     * Długość promienia okręgu opisanego na podstawie.
     */
    public float radius;

    /**
     * Liczba wierzchołków w podstwie ostrosłupa.
     */
    public int baseVerticesCount;


    //tutaj location - środek podstawy ostrosłupa

    /**
     * Tworzy obiekt typu ostrosłup.
     * @param radius Długość promienia okręgu opisanego na podstawie.
     * @param height Wysokość ostrosłupa.
     * @param verticesCount Liczba wierzchołków w podstwie ostrosłupa.
     */
    public Pyramid(float radius, float height, int verticesCount){
        this.radius = radius;
        this.height = height;
        this.baseVerticesCount = verticesCount;
    }
}
