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
 * Podłoga w grze reprezentowana jako prostopadłościan równoległy do osi układu współrzędnych.
 */
public class Floor extends Element {

    /**
     * Stała opisująca stopień przezroczystości podłogi.
     */
    public final float FLOOR_OPACITY = 1f;

    /**
     * Stała definiująca próg, powyżej którego tekstura podłogi jest zmnieniana.
     */
    public final float THRESHOLD_MU_FACTOR = 0.05f;

    /**
     * Stała definiująca rozmiar kwadratowego kafelka tekstury podłogi.
     */
    private static final float FLOOR_TEXTURE_UNIT = 5f;

    /** Rozmiar prostopadłościanu podłogi.*/
    private BoxSize measurements;

    /** Współczynnik tarcia dla powierzchni podłogi.*/
    private float mu;

    /** Statyczna wartość identyfikatora OpenGL tekstury podłogi o standardowym współczynniku tarcia.  */
    private static int standardFloorTextureId;

    /** Statyczna wartość identyfikatora OpenGL tekstury podłogi o zwiększonym współczynniku tarcia.  */
    private static int stickyFloorTextureId;

    /**
     * Zwraca rozmiar prostopadłościanu  podłogi.
     * @return Obiekt <b><em>measurements</em></b>.
     */
    public BoxSize getMeasurements() {
        return measurements;
    }

    /**
     * Zwraca wartość współczynnika tarcia dla powierzchni podłogi.
     * @return Wartość <b><em>mu</em></b>.
     */
    public float getMu() {
        return mu;
    }

    /**
     * Zwraca wartość identyfikatora OpenGL tekstury podłogi o standardowym współczynniku tarcia.
     * @return Wartość <b><em>standardFloorTextureId</em></b>.
     */
    public static int getStandardFloorTextureId()
    {
        return standardFloorTextureId;
    }

    /**
     * Zwraca wartość identyfikatora OpenGL tekstury podłogi o zwiększonym współczynniku tarcia.
     * @return Wartość <b><em>stickyFloorTextureId</em></b>.
     */
    public static int getStickyFloorTextureId()
    {
        return stickyFloorTextureId;
    }

    /**
     * Zwraca wartość identyfikatora OpenGL aktualnej tekstury podłogi.
     * @return Wartość <b><em>standardFloorTextureId</em></b> gdy wartość współczynnika tarcia jest nie większy niż wartość progowego współczynnika lub <b><em>stickyFloorTextureId</em></b> w przeciwnym przypadku.
     */
    public int getTexture(){return mu <= THRESHOLD_MU_FACTOR ? standardFloorTextureId : stickyFloorTextureId;}

    /**
     * Tworzy obiekt typu podłoga.
     * @param measures Wymiary prostopadłościanu opisującego podłogę.
     * @param mu Wartość współczynnika tarcia.
     * @param location Współrzędne środka prostopadłościanu w globalnym układzie współrzędnych.
     */
    public Floor(BoxSize measures, float mu, Point location) {
        super(location);
        this.measurements = measures;
        this.mu = mu;

        GraphicsData generatedData = ObjectGenerator.createBoxModel(measures, FLOOR_TEXTURE_UNIT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

    /**
     * Inicjuje wartość identyfikatora OpenGL tekstury podłogi o standardowym i zwiększkonym współczynniku tarcia.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public static void initTextures(Context context){
        stickyFloorTextureId = ResourceHelper.loadTexture(context, R.drawable.new_floor_texture5);
        standardFloorTextureId = ResourceHelper.loadTexture(context, R.drawable.new_floor_texture3);
    }
}
