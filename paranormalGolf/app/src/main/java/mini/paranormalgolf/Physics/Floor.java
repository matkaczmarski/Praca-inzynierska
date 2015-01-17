package mini.paranormalgolf.Physics;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Rectangle;
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
    private static int topFloorTextureNormal;

    /** Opisz Mateusz tutaj!!!!!!!!  */
    private static int topFloorTextureSticky;

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
    public static int getTopFloorTextureNormal()
    {
        return topFloorTextureNormal;
    }

    /**
     * Opisz Mateusz tutaj!!!!!!!!
     * @return
     */
    public static int getTopFloorTextureSticky()
    {
        return topFloorTextureSticky;
    }


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
        topFloorTextureSticky = ResourceHelper.loadTexture(context, R.drawable.new_floor_texture5);
        topFloorTextureNormal = ResourceHelper.loadTexture(context, R.drawable.new_floor_texture3);
        //topFloorTextureNormal = ResourceHelper.loadTexture(context, R.drawable.floor_texture_cos);
        //topFloorTextureNormal = ResourceHelper.loadTexture(context, R.drawable.floor_texture_lunabase);
    }
}
