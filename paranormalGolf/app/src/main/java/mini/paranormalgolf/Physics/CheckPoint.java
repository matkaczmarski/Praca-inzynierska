package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.R;

/**
 * Punkt kontrolny w grze, reprezentowany w postaci ściętego stożka, którego wysokość jest
 * rownoległa do osi OY układu współrzędnych.
 */
public class CheckPoint extends ControlPoint {

    /**
     * OPISZ MATEUSZ TUTAJ
     */
    public final float CHECKPOINT_OPACITY = 1f;

    /**
     * OPISZ MATEUSZ TUTAJ
     */
    private final int CHECKPOINT_PLATFORM_DIMENSION = 32;

    /**
     * OPISZ MATEUSZ TUTAJ
     */
    private final float CHECKPOINT_PLATFORM_SHIFT = 0.01f;

    /**
     * Przechowuje informację o rozmiarze i wyglądzie punktu kontrolnego.
     */
    private final Glow glow;

    /**
     * Informacja, czy punkt został już odwiedzony.
     */
    private boolean visited;

    /**
     * OPISZ MATEUSZ TUTAJ
     */
    private static int checkPointTextureId;

    /**
     * Zwraca informację, czy punkt kontrolny został już odwiedzony.
     * @return Wartość <b><em>visited</em></b>.
     */
    public boolean isVisited(){return visited;}

    /**
     * Zwraca informację o rozmiarze i wyglądzie punktu kontrolnego.
     * @return Obiekt <b><em>glow</em></b>.
     */
    public Glow getGlow(){return glow;}

    /**
     * OPISZ MATEUSZ TUTAJ
     * @return
     */
    public static int getTexture(){return checkPointTextureId;}

    /**
     * Tworzy obiekt typu punkt kontrolny.
     * @param location Współrzędne środka dolnej podstawy w globalnym układzie współrzędnych.
     * @param conicalFrustum Rozmiar ściętego stożka opisującego punkt kontrolny.
     */
    public CheckPoint(Point location, ConicalFrustum conicalFrustum){
        super(location,conicalFrustum);
        visited=false;

        GraphicsData generatedData = ObjectGenerator.createControlPointPlatformModel(conicalFrustum.bottomRadius, CHECKPOINT_PLATFORM_DIMENSION, CHECKPOINT_PLATFORM_SHIFT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        glow = new Glow(location, conicalFrustum);
    }

    /**
     * OPISZ MATEUSZ TUTAJ
     * @param context
     */
    public static void initTextures(Context context){
        checkPointTextureId = ResourceHelper.loadTexture(context, R.drawable.checkpoint_texture);
    }

    /**
     * Oznacza punkt jako odwiedzony.
     */
    public void visit(){visited = true;}
}
