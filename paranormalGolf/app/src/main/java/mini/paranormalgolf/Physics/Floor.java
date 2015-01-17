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
 * Podłoga w grze reprezentowana jako prostopadłościan równoległy do osi układu współrzędnych
 */
public class Floor extends Element {

    /** Opisz Mateusz tutaj!!!!!!  */
    public final float FLOOR_OPACITY = 1f;

    /** Opisz Mateusz tutaj!!!!!!!!  */
    public final float THRESHOLD_MU_FACTOR = 0.05f;

    /** Opisz Mateusz tutaj!!!!!!!!  */
    private static final float FLOOR_TEXTURE_UNIT = 5f;

    /** Rozmiar prostopadłościanu podłogi*/
    private BoxSize measurements;

    /** Współczynnik tarcia dla powierzchni podłogi*/
    private float mu;

    /** Opisz Mateusz tutaj!!!!!!!!  */
    private static int standardFloorTextureId;

    /** Opisz Mateusz tutaj!!!!!!!!  */
    private static int stickyFloorTextureId;

    /**
     * Zwraca rozmiar prostopadłościanu  podłogi
     * @return Rozmiar prostopadłościanu podłogi
     */
    public BoxSize getMeasurements() {
        return measurements;
    }

    /**
     * Zwraca wartość współczynnika tarcia dla powierzchni podłogi
     * @return Wartość współczynnika tarcia dla powierzchni podłogi
     */
    public float getMu() {
        return mu;
    }

    /**
     * Opisz Mateusz tutaj!!!!!!!!
     * @return
     */
    public static int getStandardFloorTextureId()
    {
        return standardFloorTextureId;
    }

    /**
     * Opisz Mateusz tutaj!!!!!!!!
     * @return
     */
    public static int getStickyFloorTextureId()
    {
        return stickyFloorTextureId;
    }


    public int getTexture(){return mu <= THRESHOLD_MU_FACTOR ? standardFloorTextureId : stickyFloorTextureId;}

    /**
     * Tworzy obiekt typu podłoga
     * @param measures Wymiary prostopadłościanu opisującego podłogę
     * @param mu Wartość współczynnika tarcia
     * @param location Współrzędne środka prostopadłościanu w globalnym układzie współrzędnych
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
     * Opisz Mateusz tutaj!!!!!!!!
     * @param context
     */
    public static void initTextures(Context context){
        stickyFloorTextureId = ResourceHelper.loadTexture(context, R.drawable.new_floor_texture5);
        standardFloorTextureId = ResourceHelper.loadTexture(context, R.drawable.new_floor_texture3);
        //standardFloorTextureId = ResourceHelper.loadTexture(context, R.drawable.floor_texture_cos);
        //standardFloorTextureId = ResourceHelper.loadTexture(context, R.drawable.floor_texture_lunabase);
    }
}
