package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
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
     * OPISZ MATEUSZ TUTAJ
     */
    public final float BEAM_OPACITY = 1f;

    /**
     * OPISZ MATEUSZ TUTAJ
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
     * OPISZ KUBA TUTAJ
     */
    private boolean moveToPatrolTo;

    /**
     * OPISZ MATEUSZ TUTAJ
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
     * OPISZ MATEUSZ TUTAJ
     * @return
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
    public Beam(Point location, Vector velocity, BoxSize measures, Point from, Point to){
        super(velocity, location);
        this.measurements = measures;
        this.patrolFrom = from;
        this.patrolTo = to;

        moveToPatrolTo = findMovementDirection(from, to, velocity);

        GraphicsData generatedData = ObjectGenerator.createBoxModel(measures, BEAM_TEXTURE_UNIT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

    /**
     * Odświeża położenie i prędkość belki.
     * @param dt Czas (w sekundach), który upływa pomiędzy 2 kolejnymi wyświetlanymi klatkami.
     */
    public void Update(float dt){
        lastMove = new Vector(velocity.x * dt, 0, velocity.z * dt);
        location.x = location.x + lastMove.x;
        location.z = location.z + lastMove.z;
        if ((moveToPatrolTo && (location.x > patrolTo.x || location.z > patrolTo.z)) || (!moveToPatrolTo && (location.x < patrolFrom.x || location.z < patrolFrom.z)))
        {
            velocity.x = -velocity.x;
            velocity.z = -velocity.z;

            moveToPatrolTo = !moveToPatrolTo;
        }
    }

    /**
     * OPISZ KUBA TUTAJ
     * @param from
     * @param to
     * @param velocity
     * @return
     */
    private boolean findMovementDirection(Point from, Point to, Vector velocity){
        if (velocity.x != 0)
        {
            if (from.x > to.x)
            {
                patrolFrom = to;
                patrolTo = from;
            }

            return velocity.x > 0;
        }
        else if (velocity.y != 0)
        {
            if (from.y > to.y)
            {
                patrolFrom = to;
                patrolTo = from;
            }

            return velocity.y > 0;
        }
        else if (velocity.z != 0)
        {
            if (from.z > to.z)
            {
                patrolFrom = to;
                patrolTo = from;
            }

            return velocity.z > 0;
        }

        return true;
    }

    /**
     * OPISZ MATEUSZ TUTAJ
     * @param context
     */
    public static void initTextures(Context context){
        beamTextureId = ResourceHelper.loadTexture(context, R.drawable.beam_texture);
    }
}
