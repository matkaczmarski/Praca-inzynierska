package mini.paranormalgolf.Graphics;

import java.util.List;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder.DrawCommand;

/**
 * Przechowuje informacje o modelu obiektu.
 */
public class GraphicsData {

    /**
     * Tablica atrybutów wszyskich wierzchołków siatki trójkątów.
     */
    public final float[] vertexData;
    /**
     * Lista reguł rysowania siatki trójkątów.
     */
    public final List<DrawCommand> drawCommands;

    /**
     * Tworzy obiekt do przechowywania wierzchołków modeli obiektów oraz reguły ich rysowania.
     * @param vertexData
     * @param drawCommands
     */
    public GraphicsData(float[] vertexData, List<DrawCommand> drawCommands) {
        this.vertexData = vertexData;
        this.drawCommands = drawCommands;
    }
}
