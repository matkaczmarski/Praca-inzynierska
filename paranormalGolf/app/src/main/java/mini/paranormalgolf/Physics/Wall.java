package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.R;

/**
 * Ściana w grze reprezentowana jako prostopadłościan równoległy do osi układu współrzędnych
 */
public class Wall extends Element {

    /**
     * OPISZ MATEUSZ TUTAJ!!!!
     */
    public final float WALL_OPACITY = 1f;

    /**
     * OPISZ MATEUSZ TUTAJ!!!!
     */
    private static final float WALL_TEXTURE_UNIT = 5f;

    /**
     * Rozmiar prostopadłościanu ściany
     */
    private BoxSize measurements;

    /**
     * OPISZ MATEUSZ TUTAJ!!!!
     */
    private static int wallTexture;

    /**
     * Zwraca rozmiar prostopadłościanu ściany
     * @return Rozmiar prostopadłościanu ściany
     */
    public BoxSize getMeasurements() {
        return measurements;
    }

    /**
     * OPISZ MATEUSZ TUTAJ!!!!
     * @return
     */
    public static int getWallTexture()
    {
        return wallTexture;
    }


    /**
     * Tworzy obiekt typu ściana
     * @param location Współrzędne środka prostopadłościanu w globalnym układzie współrzędnych
     * @param measures Wymiary prostopadłościanu opisującego ścianę
     */
    public Wall(Point location, BoxSize measures) {
        super(location);
        measurements = measures;

        GraphicsData generatedData = ObjectGenerator.createBoxModel(measures, WALL_TEXTURE_UNIT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        texture = wallTexture;//ResourceHelper.loadTexture(context, R.drawable.wall_texture);
    }

    /**
     * OPISZ MATEUSZ TUTAJ!!!!
     * @param context
     */
    public static void initTextures(Context context)
    {
        wallTexture = ResourceHelper.loadTexture(context, R.drawable.wall_texture);
    }
}
