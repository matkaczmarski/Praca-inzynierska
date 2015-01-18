package mini.paranormalgolf.Physics;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Point;

/**
 * Poświata występująca przy obiektach typu <em>ControlPoint</em>. Reprezentowana w postaci ściętego stożka, którego wysokość jest równoległa do osi Y.
 */
public class Glow extends ControlPoint {

    /**
     * Stała opisująca stopień przezroczystości poświaty.
     */
    public final float GLOW_OPACITY = 0.65f;

    /**
     * Stała opisująca rozdzielczość siatki trójkątów poświaty.
     */
    private final int GLOW_MESH_DIMENSION = 32;

    /**
     * Stała opisująca kolor aktywnego obiekty typu <em>Finish</em>.
     */
    public final float[] ACTIVE_FINISH_COLOR = new float[]{0.678431f, 1.0f, 0.184314f, GLOW_OPACITY};
    /**
     * Stała opisująca kolor nieaktywnego obiektu typu <em>Finish</em>.
     */
    public final float[] INACTIVE_FINISH_COLOR = new float[]{1f, 0.388235f, 0.278431f, GLOW_OPACITY};

    /**
     * Stała opisująca kolor poświaty obiektu typu <em>Checkpoint</em>.
     */
    public final float[] CHECKPOINT_GLOW_COLOR = new float[]{1f, 1f, 0f, GLOW_OPACITY};

    /**
     * Tworzy obiekt poświaty.
     * @param location Współrzędne środka dolnej podstawy poświaty w globalnym układzie współrzędnych.
     * @param conicalFrustum Rozmiar poświaty.
     */
    public Glow(Point location, ConicalFrustum conicalFrustum) {
        super(location, conicalFrustum);
        GraphicsData generatedData = ObjectGenerator.createControlPointGlowModel(conicalFrustum, GLOW_MESH_DIMENSION);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }
}
