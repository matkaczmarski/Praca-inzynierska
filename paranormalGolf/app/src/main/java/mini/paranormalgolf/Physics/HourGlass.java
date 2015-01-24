package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.TriangleMeshData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Cylinder;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.R;

/**
 * Klepsydra w grze reprezentowana jako 2 ścięte stożki sklejone podstawami,
 * których wysokości są równoległe do osi OY układu współrzędnych.
 */
public class HourGlass extends Bonus {

    /**
     * Opisuje drewniane części klepsydry.
     */
    public class HourGlassWoodenParts extends Bonus{

        /**
         * Stała opisująca stopień przezroczystości drewnianych części klepsydry.
         */
        public final float HOURGLASS_WOODEN_PART_OPACITY = 1f;

        /**
         * Stała opisująca rozdzielczość siatki trójkątów drewnianych części klepsydry.
         */
        private final int HOURGLASS_MESH_DIMENSION = 32;

        /**
         * Stała definiująca rozmiar kwadratowego kafelka tekstury drewnianych części klepsydry.
         */
        private final float TEXTURE_UNIT = 5f;

        /**
         * Tworzy obiekt drewnianych części klepsydry.
         * @param location Współrzędne klepsydry w globalnym układzie współrzędnych.
         * @param lowerCylinder Rozmiary dolnej drewnianej części klepsydry.
         * @param upperCylinder Rozmiary górnej drewnianej części klepsydry.
         */
        public HourGlassWoodenParts(Point location, Cylinder lowerCylinder, Cylinder upperCylinder) {
            super(location, 0, 0);

            TriangleMeshData generatedData = ObjectGenerator.createHourglassWoodenPartsModel(upperCylinder, lowerCylinder, HOURGLASS_MESH_DIMENSION, TEXTURE_UNIT);
            vertexData = new VertexArray(generatedData.vertexData);
            drawCommands = generatedData.drawCommands;
            rotationSpeed = HOURGLASS_ROTATION_SPEED;
        }
    }

    /**
     * Stała opisująca kolor oraz stopień przezroczystości szkła klepsydry.
     */
    public final float[] GLASS_COLOR = new float[] {0.690196f, 0.878431f, 0.901961f, 0.75f};

    /**
     * Stała opisująca prędkość kątową obrotu klepsydry.
     */
    private final float HOURGLASS_ROTATION_SPEED = 0.5f;

    /**
     * Stała opisująca szybkość podnoszenia i opadania klepsydry.
     */
    private final float HOURGLASS_UP_DOWN_SPEED = 0.001f;

    /**
     * Stała opisująca rozdzielczość siatki trójkątów szklanej części klepsydry.
     */
    private final int HOURGLASS_MESH_DIMENSION = 32;

    /**
     * Stała opisująca stosunek wysokości drewnianych części klepsydry do wysokości klepsydry.
     */
    private final float WOODEN_BASE_HEIGHT_RATIO = 0.1f;


    /**
     * Rozmiar dolnego względem osi OY ściętego stożka.
     */
    private final ConicalFrustum lowerCone = new ConicalFrustum(0.7f, 0.5f, 0.15f);

    /**
     * Rozmiar górnego względem osi OY ściętego stożka.
     */
    private final ConicalFrustum upperCone = new ConicalFrustum(0.7f, 0.15f, 0.5f);

    /**
     * Statyczna wartość identyfikatora OpenGL tekstury drewnianych części klepsydry.
     */
    private static int hourGlassTextureId;


    /**
     * Zwraca wartość identyfikatora OpenGL tekstury drewnianych części klepsydry.
     * @return Wartość <b><em>hourGlassTextureId</em></b>.
     */
    private HourGlassWoodenParts woodenParts;

    /**
     * Zwraca rozmiar dolnego względem osi OY ściętego stożka.
     * @return Obiekt <b><em>lowerCone</em></b>
     */
    public ConicalFrustum getLowerCone() {
        return lowerCone;
    }

    /**
     * Zwraca obiekt definiujący drewniane części klepsydry.
     * @return Obiekt <b><em>woodenParts</em></b>.
     */
    public HourGlassWoodenParts getWoodenParts(){return woodenParts;}


    /**
     * Zwraca wartość identyfikatora OpenGL tekstury klepsydry.
     * @return Wartość <b><em>hourGlassTextureId</em></b>.
     */
    public static int getTexture(){return hourGlassTextureId;}

    /**
     * Tworzy obiekt typu klepsydra.
     * @param location Współrzędne środka klepsydry w globalnym układzie współrzędnych.
     * @param value Liczba dodawanych sekund związanych z zebraniem klepsydry.
     * @param yShift Wartość o jaką wzdłuż osi OY można podnosić klepsydrę podczas animacji ruchu.
     */
    public HourGlass(Point location, int value, float yShift) {
        super(location, value, yShift);

        TriangleMeshData generatedData = ObjectGenerator.createHourglassGlassPartModel(lowerCone, upperCone, HOURGLASS_MESH_DIMENSION);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        float woodenBaseHeight = (lowerCone.height + upperCone.height) * WOODEN_BASE_HEIGHT_RATIO;
        woodenParts = new HourGlassWoodenParts(location, new Cylinder(new Point(0f, -lowerCone.height + woodenBaseHeight /2,0f),lowerCone.bottomRadius, woodenBaseHeight), new Cylinder(new Point(0f, upperCone.height - woodenBaseHeight /2,0f),upperCone.topRadius, woodenBaseHeight));
        rotationSpeed = HOURGLASS_ROTATION_SPEED;
        upDownSpeed = HOURGLASS_UP_DOWN_SPEED;
    }

    /**
     * Inicjuje wartość identyfikatora OpenGL tekstury drewnianych częsci klepsydry.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public static void initTextures(Context context){
        hourGlassTextureId = ResourceHelper.loadTexture(context, R.drawable.hourglass_texture_wooden_part);
    }
}
