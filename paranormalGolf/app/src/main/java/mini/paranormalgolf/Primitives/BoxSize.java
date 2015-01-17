package mini.paranormalgolf.Primitives;

/**
 * Przechowuje rozmiar prostopadłościanu równoległego do osi układu współrzędnych.
 */
public class BoxSize {

    /**
     * Wymiar prostopadłościanu wzdłuż osi OX.
     */
    public float x;

    /**
     * Wymiar prostopadłościanu wzdłuż osi OY.
     */
    public float y;

    /**
     * Wymiar prostopadłościanu wzdłuż osi OZ.
     */
    public float z;

    /**
     * Tworzy obiekt typu rozmiar prostopadłościanu.
     * @param sizeX Wymiar prostopadłościanu wzdłuż osi OX.
     * @param sizeY Wymiar prostopadłościanu wzdłuż osi OY.
     * @param sizeZ Wymiar prostopadłościanu wzdłuż osi OZ.
     */
    public BoxSize(float sizeX, float sizeY, float sizeZ)
    {
        this.x =sizeX;
        this.y =sizeY;
        this.z =sizeZ;
    }
}
