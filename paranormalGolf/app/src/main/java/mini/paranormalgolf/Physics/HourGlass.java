package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Cylinder;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.R;

/**
 * Klepsydra w grze reprezentowana jako 2 ścięte stożki sklejone podstawami,
 * których wysokości są równoległe do osi OY układu współrzędnych
 */
public class HourGlass extends Bonus {

    /**
     * OPISZ MATEUSZ TUTAJ
     */
    public class HourGlassWoodenParts extends Bonus{

        /**
         * OPISZ MATEUSZ TUTAJ!!!!
         */
        public final float HOURGLASS_WOODEN_PART_OPACITY = 1f;

        /**
         * OPISZ MATEUSZ TUTAJ!!!!
         */
        private final int HOURGLASS_MESH_DIMENSION = 32;

        /**
         * OPISZ MATEUSZ TUTAJ!!!!
         */
        private final float TEXTURE_UNIT = 5f;

        /**
         * OPISZ MATEUSZ TUTAJ!!!
         * @param location
         * @param lowerCylinder
         * @param upperCylinder
         */
        public HourGlassWoodenParts(Point location, Cylinder lowerCylinder, Cylinder upperCylinder) {
            super(location, 0, 0);

            GraphicsData generatedData = ObjectGenerator.createHourglassWoodenPartsModel(upperCylinder, lowerCylinder, HOURGLASS_MESH_DIMENSION, TEXTURE_UNIT);
            vertexData = new VertexArray(generatedData.vertexData);
            drawCommands = generatedData.drawCommands;
            rotationSpeed = HOURGLASS_ROTATION_SPEED;
        }
    }

    /**
     * OPISZ MATEUSZ TUTAJ!!!
     */
    public final float HOURGLASS_OPACITY = 0.75f;

    /**
     * OPISZ MATEUSZ TUTAJ!!!
     */
    public final float[] GLASS_COLOR = new float[] {0.690196f, 0.878431f, 0.901961f, HOURGLASS_OPACITY};

    /**
     * OPISZ MATEUSZ TUTAJ!!!
     */
    private final float HOURGLASS_ROTATION_SPEED = 0.5f;

    /**
     * OPISZ MATEUSZ TUTAJ!!!
     */
    private final float HOURGLASS_UP_DOWN_SPEED = 0.001f;

    /**
     * OPISZ MATEUSZ TUTAJ!!!
     */
    private final int HOURGLASS_MESH_DIMENSION = 32;

    /**
     * OPISZ MATEUSZ TUTAJ!!!
     */
    private final float WOODEN_BASE_HEIGHT_RATIO = 0.1f;


    /**
     * Rozmiar dolnego względem osi OY ściętego stożka
     */
    private final ConicalFrustum lowerCone = new ConicalFrustum(0.7f, 0.5f, 0.15f);

    /**
     * Rozmiar górnego względem osi OY ściętego stożka
     */
    private final ConicalFrustum upperCone = new ConicalFrustum(0.7f, 0.15f, 0.5f);

    /**
     * OPISZ MATEUSZ TUTAJ!!!
     */
    private static int hourGlassTextureId;


    public static int getTexture(){return hourGlassTextureId;}

    /**
     * OPISZ MATEUSZ TUTAJ
     */
    private HourGlassWoodenParts woodenParts;

    /**
     * Zwraca rozmiar dolnego względem osi OY ściętego stożka
     * @return Rozmiar dolnego względem osi OY ściętego stożka
     */
    public ConicalFrustum getLowerCone() {
        return lowerCone;
    }

    /**
     * OPISZ MATEUSZ TUTAJ!!!
     * @return
     */
    public HourGlassWoodenParts getWoodenParts(){return woodenParts;}

    /**
     * Tworzy obiekt typu klepsydra
     * @param location Współrzędne środka klepsydry w globalnym układzie współrzędnych
     * @param value Liczba dodawanych sekund związanych z zebraniem klepsydry
     * @param yShift Wartość o jaką wzdłuż osi OY można podnosić klepsydrę podczas animacji ruchu
     */
    public HourGlass(Point location, int value, float yShift) {
        super(location, value, yShift);

        GraphicsData generatedData = ObjectGenerator.createHourglassGlassPartModel(lowerCone, upperCone, HOURGLASS_MESH_DIMENSION);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        float woodenBaseHeight = (lowerCone.height + upperCone.height) * WOODEN_BASE_HEIGHT_RATIO;
        woodenParts = new HourGlassWoodenParts(location, new Cylinder(new Point(0f, -lowerCone.height + woodenBaseHeight /2,0f),lowerCone.bottomRadius, woodenBaseHeight), new Cylinder(new Point(0f, upperCone.height - woodenBaseHeight /2,0f),upperCone.topRadius, woodenBaseHeight));
        rotationSpeed = HOURGLASS_ROTATION_SPEED;
        upDownSpeed = HOURGLASS_UP_DOWN_SPEED;
    }

    /**
     * OPISZ MATEUSZ TUTAJ
     * @param context
     */
    public static void initTextures(Context context){
        hourGlassTextureId = ResourceHelper.loadTexture(context, R.drawable.hourglass_texture_wooden_part);
    }
}
