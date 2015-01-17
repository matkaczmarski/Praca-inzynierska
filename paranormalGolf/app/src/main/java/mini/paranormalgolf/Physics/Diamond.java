package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Pyramid;
import mini.paranormalgolf.R;

/**
 * Diament w grze reprezentowany jako 2 identyczne ostrosłupy sklejone podstawami,
 * których wysokości są równoległe do osi OY układu współrzędnych.
 */
public class Diamond extends Bonus {

    /**
     * Stała opisująca stopień przezroczystości diamentu.
     */
    public final float DIAMOND_OPACITY = 0.9f;

    /**
     * Rozmiar ostrosłupa używanego przy rysowaniu diamentu.
     */
    private final Pyramid pyramid = new Pyramid(0.7f, 1.4f, 6);
    /**
     * Statyczna wartość identyfikatora OpenGL tekstury diamentu.
     */
    private static int diamondTextureId;

    /**
     * Zwraca rozmiar ostrosłupa - elementu składowego diamentu.
     * @return Obiekt <b><em>pyramid</em></b>.
     */
    public Pyramid getPyramid() {
        return pyramid;
    }

    /**
     * Zwraca wartość identyfikatora OpenGL tekstury diamentu.
     * @return Wartość <b><em>diamondTextureId</em></b>.
     */
    public static int getTexture(){return diamondTextureId;}

    /**
     * Tworzy obiekt typu diament.
    * @param location Współrzędne środka diamentu w globalnym układzie współrzędnych.
    * @param value Liczba punktów związanych ze zdobyciem diamentów.
    * @param yShift Wartość o jaką wzdłuż osi OY można podnosić diament podczas animacji ruchu.
    */
    public Diamond(Point location, int value, float yShift) {
        super(location, value, yShift);
        GraphicsData generatedData = ObjectGenerator.createDiamondModel(pyramid);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }

    /**
     * Inicjuje wartość identyfikatora OpenGL tekstury diamentu.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public static void initTextures(Context context){
        diamondTextureId = ResourceHelper.loadTexture(context, R.drawable.diamond_texture);
    }
}
