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
 * Winda w grze reprezentowana jako prostopadłościan równoległy do osi układu współrzędnych.
 */
public class Elevator extends MovableElement {

    /**
     * Stała opisująca stopień przezroczystości windy.
     */
    public final float ELEVATOR_OPACITY = 1f;

    /**
     * Stała definiująca rozmiar kwadratowego kafelka tekstury windy.
     */
    private static final float ELEVATOR_TEXTURE_UNIT = 5f;

    /**
     * Rozmiar prostopadłościanu windy.
     */
    private BoxSize measurements;

    /**
     * Pierwszy punkt ograniczający poruszanie się windy;
     * informuje do jakiego miejsca ma dochodzić środek elementu.
     */
    private Point patrolFrom;

    /**
     * Drugi punkt ograniczający poruszanie się windy;
     * informuje do jakiego miejsca ma dochodzić środek elementu.
     */
    private Point patrolTo;

    /**
    * Współczynnik tarcia dla powierzchni windy.
    */
    private float mu;

    /**
     * OPISZ KUBA TUTAJ
     */
    private boolean moveToPatrolTo;

    /**
     * OPISZ KUBA TUTAJ
     */
    private static float wait_time = 2.0f;

    /**
     * OPISZ KUBA TUTAJ
     */
    private float time_left;

    /**
     * OPISZ KUBA TUTAJ
     */
    private boolean change = false;

    /**
     * Statyczna wartość identyfikatora OpenGL tekstury windy.
     */
    private static int elevatorTextureId;



    /**
     * Zwraca rozmiar prostopadłościanu windy.
     * @return Obiekt <b><em>measurements</em></b>.
     */
    public BoxSize getMeasurements() {
        return measurements;
    }

    /**
     * Zwraca wartość współczynnika tarcia dla powierzchni windy.
     * @return Wartość <b><em>mu</em></b>.
     */
    public float getMu() {
        return mu;
    }

    /**
     * Zwraca wartość identyfikatora OpenGL tekstury windy.
     * @return Wartość <b><em>elevatorTextureId</em></b>.
     */
    public static int getTexture(){return elevatorTextureId;}

    /**
     * Tworzy obiekt typu winda.
     * @param location Współrzędne środka prostopadłościanu w globalnym układzie współrzędnych.
     * @param velocity Wektor prędkości początkowej windy.
     * @param measures Wymiary prostopadłościanu opisującego windę.
     * @param from Pierwszy punkt ograniczający poruszanie się środka elementu.
     * @param to Drugi punkt ograniczający poruszanie się środka elementu.
     * @param mu Wartość współczynnika tarcia.
     */
    public Elevator(Point location, Vector velocity, BoxSize measures, Point from, Point to, float mu) {
        super(velocity, location);
        this.measurements = measures;
        this.patrolFrom = from;
        this.patrolTo = to;
        this.mu = mu;

        moveToPatrolTo = findMovementDirection(from, to, velocity);

        GraphicsData generatedData = ObjectGenerator.createBoxModel(measures, ELEVATOR_TEXTURE_UNIT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

    /**
     * Odświeża położenie i prędkość windy.
     * @param dt Czas (w sekundach), który upływa pomiędzy 2 kolejnymi wyświetlanymi klatkami.
     */
    public void Update(float dt)
    {
        if (change)
        {
            time_left -= dt;
            if (time_left <= 0)
            {
                velocity.x = -velocity.x;
                velocity.y = -velocity.y;
                velocity.z = -velocity.z;
                moveToPatrolTo = !moveToPatrolTo;
                change = false;
            }
            else
                return;
        }

        lastMove = new Vector(velocity.x *dt, velocity.y * dt, velocity.z * dt);
        location.x = location.x + lastMove.x;
        location.y = location.y + lastMove.y;
        location.z = location.z + lastMove.z;

        if ((moveToPatrolTo && ((location.x > patrolTo.x) || (location.y > patrolTo.y) || location.z > patrolTo.z)) || (!moveToPatrolTo && ((location.x < patrolFrom.x) || (location.y < patrolFrom.y) || location.z < patrolFrom.z)))
        {
            change = true;
            time_left = wait_time;
            lastMove = new Vector(0,0,0);
            /*if (moveToPatrolTo)
                location = patrolTo;
            else
                location = patrolFrom;*/
        }
    }

    /**
     * OPISZ KUBA TUTAJ
     * @param from
     * @param to
     * @param velocity
     * @return
     */
    private boolean findMovementDirection(Point from, Point to, Vector velocity)
    {
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
     * Inicjuje wartość identyfikatora OpenGL tekstury windy.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public static void initTextures(Context context){
        elevatorTextureId = ResourceHelper.loadTexture(context, R.drawable.elevator_texture);
    }
}
