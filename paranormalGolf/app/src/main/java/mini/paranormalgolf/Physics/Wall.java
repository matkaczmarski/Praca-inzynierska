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
 * Ściana w grze reprezentowana jako prostopadłościan równoległy do osi układu współrzędnych.
 */
public class Wall extends Element {

    /**
     * Stała opisująca stopień przezroczystości ściany.
     */
    public final float WALL_OPACITY = 1f;

    /**
     * Stała definiująca rozmiar kwadratowego kafelka tekstury ściany.
     */
    private static final float WALL_TEXTURE_UNIT = 5f;

    /**
     * Rozmiar prostopadłościanu ściany.
     */
    private BoxSize measurements;

    /**
     * Statyczna wartość identyfikatora OpenGL tekstury ściany.
     */
    private static int wallTextureId;

    /**
     * Zwraca rozmiar prostopadłościanu ściany.
     * @return Obiekt <b><em>measurements</em></b>.
     */
    public BoxSize getMeasurements() {
        return measurements;
    }

    /**
     * Zwraca wartość identyfikatora OpenGL tekstury ściany.
     * @return Wartość <b><em>wallTextureId</em></b>.
     */
    public static int getTexture(){return wallTextureId;}


    /**
     * Tworzy obiekt typu ściana.
     * @param location Współrzędne środka prostopadłościanu w globalnym układzie współrzędnych.
     * @param measures Wymiary prostopadłościanu opisującego ścianę.
     */
    public Wall(Point location, BoxSize measures) {
        super(location);
        measurements = measures;

        GraphicsData generatedData = ObjectGenerator.createBoxModel(measures, WALL_TEXTURE_UNIT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

    /**
     * Inicjuje wartość identyfikatora OpenGL tekstury ściany.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public static void initTextures(Context context){
        wallTextureId = ResourceHelper.loadTexture(context, R.drawable.wall_texture);
    }
}
