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
     * Czas (w sekundach) postoju windy.
     */
    private static float wait_time = 2.0f;

    /**
     * Pozostały czas postoju windy(w sekundach).
     */
    private float time_left;

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

        TriangleMeshData generatedData = ObjectGenerator.createBoxModel(measures, ELEVATOR_TEXTURE_UNIT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

    /**
     * Odświeża położenie i prędkość windy.
     * @param dt Czas (w sekundach), który upływa pomiędzy 2 kolejnymi wyświetlanymi klatkami.
     */
    public void Update(float dt) {
        if (location.y == patrolFrom.y) {
            time_left -= dt;
            if (time_left < 0) {
                velocity.y = Math.abs(velocity.y);
                lastMove.y = (-time_left) * velocity.y;
                location.y += lastMove.y;
            } else lastMove.y = 0;
        } else if (location.y == patrolTo.y) {
            time_left -= dt;
            if (time_left < 0) {
                velocity.y = -Math.abs(velocity.y);
                lastMove.y = (-time_left) * velocity.y;
                location.y += lastMove.y;
            } else
                lastMove.y = 0;
        } else { //winda jest pomiędzy położeniami
            lastMove.y = velocity.y * dt;

            if (location.y + lastMove.y >= patrolTo.y) {
                time_left = wait_time;
                time_left -= (((patrolTo.y - location.y) / lastMove.y) * dt);
                lastMove.y = patrolTo.y - location.y;
                location.y = patrolTo.y;
            } else if (location.y + lastMove.y <= patrolFrom.y) {
                time_left = wait_time;
                time_left -= (((location.y - patrolFrom.y) / (-lastMove.y)) * dt);
                lastMove.y = patrolFrom.y - location.y;
                location.y = patrolFrom.y;
            } else
                location.y += lastMove.y;
        }
    }

    /**
     * Inicjuje wartość identyfikatora OpenGL tekstury windy.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public static void initTextures(Context context){
        elevatorTextureId = ResourceHelper.loadTexture(context, R.drawable.elevator_texture);
    }
}
