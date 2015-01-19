package mini.paranormalgolf.Graphics;

import mini.paranormalgolf.Primitives.Point;

/**
 * Przehowuje informacje na temat źródła światła występującego na scenie.
 */
public class LightData{
    /**
     * Aktualna pozycja źródła światła w globalnym układzie współrzędnych.
     */
    public Point position;
    /**
     * Współczynnik światła otoczenia.
     */
    public float ambient;
    /**
     * Współczynnik światła rozproszonego.
     */
    public float diffusion;

    /**
     * Tworzy obiekt opisujący parametry światła.
     * @param ambient Współczynnik światła otoczenia.
     * @param diffusion Współczynnik światła rozproszonego.
     */
    public LightData( float ambient, float diffusion){
        this.position = new Point(0f,0f,0f);
        this.ambient = ambient;
        this.diffusion = diffusion;
    }
}
