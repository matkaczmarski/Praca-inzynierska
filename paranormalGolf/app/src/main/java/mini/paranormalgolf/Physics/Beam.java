package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.TriangleMeshData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Vector;
import mini.paranormalgolf.R;

/**
 * Belka w grze reprezentowana jako prostopadłościan równoległy do osi układu współrzędnych.
 */
public class Beam extends MovableElement {

    /**
     * Stała opisująca stopień przezroczystości belki.
     */
    public final float BEAM_OPACITY = 1f;

    /**
     * Stała definiująca rozmiar kwadratowego kafelka tekstury belki.
     */
    private static final float BEAM_TEXTURE_UNIT = 5f;

    /**
     * Rozmiar prostopadłościanu belki.
     */
    private BoxSize measurements;

    /**
     * Pierwszy punkt ograniczający poruszanie się belki;
     * informuje do jakiego miejsca ma dochodzić środek elementu.
     */
    private Point patrolFrom;

    /**
     * Drugi punkt ograniczający poruszanie się belki;
     * informuje do jakiego miejsca ma dochodzić środek elementu.
     */
    private Point patrolTo;

    /**
     * Statyczna wartość identyfikatora OpenGL tekstury belki.
     */
    private static int beamTextureId;

    /**
     * Zwraca rozmiar prostopadłościanu belki.
     * @return Obiekt <b><em>measurements</em></b>.
     */
    public BoxSize getMeasurements() {
        return measurements;
    }

    /**
     * Zwraca wartość identyfikatora OpenGL tekstury belki.
     * @return Wartość <b><em>beamTextureId</em></b>.
     */
    public static int getTexture(){return beamTextureId;}

    /**
     * Tworzy obiekt typu belka.
     * @param location Współrzędne środka prostopadłościanu w globalnym układzie współrzędnych.
     * @param velocity Wektor prędkości początkowej belki.
     * @param measures Wymiary prostopadłościanu opisującego belkę.
     * @param from Pierwszy punkt ograniczający poruszanie się środka elementu.
     * @param to Drugi punkt ograniczający poruszanie się środka elementu.
     */
    public Beam(Point location, Vector velocity, BoxSize measures, Point from, Point to) {
        super(velocity, location);
        this.measurements = measures;
        this.patrolFrom = from;
        this.patrolTo = to;

        TriangleMeshData generatedData = ObjectGenerator.createBoxModel(measures, BEAM_TEXTURE_UNIT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

    /**
     * Odświeża położenie i prędkość belki.
     * @param dt Czas (w sekundach), który upływa pomiędzy 2 kolejnymi wyświetlanymi klatkami.
     */
    public void Update(float dt) {
        lastMove = new Vector(velocity.x * dt, 0, velocity.z * dt);
        location.x = location.x + lastMove.x;
        location.z = location.z + lastMove.z;
        if (velocity.x != 0) {
            if ((location.x >= patrolTo.x && velocity.x > 0) || (location.x <= patrolFrom.x && velocity.x < 0))
                velocity.x = -velocity.x;
        } else  //velocity.z!=0
            if ((location.z >= patrolTo.z && velocity.z > 0) || (location.z <= patrolFrom.z && velocity.z < 0))
                velocity.z = -velocity.z;

    }

    /**
     * Inicjuje wartość identyfikatora OpenGL tekstury belki.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public static void initTextures(Context context){
        beamTextureId = ResourceHelper.loadTexture(context, R.drawable.beam_texture);
    }
}
