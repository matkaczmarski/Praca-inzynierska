package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.TriangleMeshData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.R;

/**
 * Punkt mety w grze, reprezentowany w postaci ściętego stożka, którego wysokość jest
 * rownoległa do osi OY układu współrzędnych.
 */
public class Finish extends ControlPoint {

    /**
     * Stała opisująca stopień przezroczystości mety.
     */
    public final float FINISH_OPACITY = 1f;

    /**
     * Rozdzielczość siatki trójkątów mety.
     */
    private final int FINISH_PLATFORM_DIMENSION = 32;

    /**
     * Stała określająca podniesienie platformy mety względem swojego położenia.
     */
    private  final float FINISH_PLATFORM_SHIFT = 0.01f;

    /**
     * Przechowuje informację o rozmiarze i wyglądzie punktu kontrolnego.
     */
    private final Glow glow;

    /**
     * Informacja, czy punkt mety można już odwiedzić, żeby zakończyć poziom.
     */
    private boolean isActive;

    /**
     * Statyczna wartość identyfikatora OpenGL tekstury mety.
     */
    private static int finishTextureId;

    /**
     * Zwraca informację, czy można już ukończyć poziom.
     * @return Wartość <b><em>isActive</em></b>.
     */
    public boolean isActive(){
        return isActive;
    }

    /**
     * Zwraca informację o rozmiarze i wyglądzie punktu mety.
     * @return Obiekt <b><em>glow</em></b>.
     */
    public Glow getGlow(){return glow;}

    /**
     * Zwraca wartość identyfikatora OpenGL tekstury mety.
     * @return Wartość <b><em>finishTextureId</em></b>.
     */
    public static int getTexture(){return finishTextureId;}

    /**
     * Tworzy obiekt typu meta.
     * @param location Współrzędne środka dolnej podstawy w globalnym układzie współrzędnych.
     * @param conicalFrustum Rozmiar ściętego stożka opisującego punkt mety.
     */
    public Finish(Point location, ConicalFrustum conicalFrustum) {
        super(location, conicalFrustum);
        isActive = false;

        TriangleMeshData generatedData = ObjectGenerator.createControlPointPlatformModel(conicalFrustum.bottomRadius, FINISH_PLATFORM_DIMENSION, FINISH_PLATFORM_SHIFT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        glow = new Glow(location, conicalFrustum);
    }

    /**
     * Inicjuje wartość identyfikatora OpenGL tekstury mety.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public static void initTextures(Context context){
        finishTextureId = ResourceHelper.loadTexture(context, R.drawable.finish_texture);
    }

    /**
     * Ustawia informację, że można odwiedzić punkt mety, żeby zakończyć poziom.
     */
    public void activate() {
        isActive = true;
    }

}
