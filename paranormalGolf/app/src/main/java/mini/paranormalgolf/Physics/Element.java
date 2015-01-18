package mini.paranormalgolf.Physics;

import java.util.List;

import mini.paranormalgolf.Graphics.ShaderPrograms.ColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.DepthMapShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShadowingShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.SkyBoxShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder.DrawCommand;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;

/**
 * Klasa abstrakcyjna reprezentująca element znajdujący się na planszy.
 */
public abstract class Element {

    /**
     * OPISZ MATEUSZ TUTAJ
     */
    protected final int POSITION_COMPONENT_COUNT = 3;

    /**
     * OPISZ MATEUSZ TUTAJ
     */
    protected final int NORMAL_COMPONENT_COUNT = 3;

    /**
     * OPISZ MATEUSZ TUTAJ
     */
    protected final int TEXTURE_COMPONENT_COUNT = 2;

    /**
     * OPISZ MATEUSZ TUTAJ
     */
    protected final int BYTES_PER_FLOAT = 4;

    /**
     * OPISZ MATEUSZ TUTAJ
     */
    private final int STRIDE_WITH_TEXTURE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    /**
     * OPISZ MATEUSZ TUTAJ
     */
    private final int STRIDE_WITHOUT_TEXTURE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * 4;

    /**
     * Współrzędne punktu charakterystycznego dla elementu w globalnym układzie współrzędnych.
     */
    protected Point location;

    /**
     * OPISZ MATEUSZ TUTAJ
     */
    protected VertexArray vertexData;

    /**
     * OPISZ MATEUSZ TUTAJ
     */
    protected List<DrawCommand> drawCommands;

    /**
     * Zwraca współrzędne punktu charakterystycznego dla elementu w globalnym układzie współrzędnych.
     * @return Obiekt <b><em>location</em></b>.
     */
    public Point getLocation() { return this.location;}

    /**
     * Ustawia obiekt <b><em>location</em></b>.
     * @param location Współrzędne punktu charakterystycznego dla elementu w globalnym układzie współrzędnych.
     */
    public void setLocation(Point location) { this.location = location;}

    /**
     * Tworzy obiekt typu <em>Element</em>.
     * @param location Współrzędne punktu charakterystycznego dla elementu w globalnym układzie współrzędnych.
     */
    protected Element(Point location){
     this.location=location;
    }

    /**
     * Kojarzy atrybuty z odpowiednimi miejscami w buforze <em><b>vertexData</b></em> uwzględniając typ przekazanego programu.
     * @param shaderProgram Obiekt programu, którego atrybuty mają być odpowiednio skojarzone.
     * @param shaderProgramType Typ programu.
     */
    public void bindData(ShaderProgram shaderProgram, ShaderProgram.ShaderProgramType shaderProgramType){
            switch(shaderProgramType) {
                case depthMap:
                    vertexData.setVertexAttribPointer(0, ((DepthMapShaderProgram) shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE_WITH_TEXTURE);
                    break;
                case color:
                    ColorShaderProgram colorShaderProgram = (ColorShaderProgram) shaderProgram;
                    vertexData.setVertexAttribPointer(0, colorShaderProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE_WITHOUT_TEXTURE);
                    vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, colorShaderProgram.getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE_WITHOUT_TEXTURE);
                    break;
                case withShadowing:
                    ShadowingShaderProgram shadowingShaderProgram = (ShadowingShaderProgram) shaderProgram;
                    vertexData.setVertexAttribPointer(0, shadowingShaderProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE_WITH_TEXTURE);
                    vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, shadowingShaderProgram.getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE_WITH_TEXTURE);
                    vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT, shadowingShaderProgram.getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE_WITH_TEXTURE);
                    break;
                case withoutShadowing:
                    TextureShaderProgram textureShaderProgram = (TextureShaderProgram) shaderProgram;
                    vertexData.setVertexAttribPointer(0, textureShaderProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE_WITH_TEXTURE);
                    vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, textureShaderProgram.getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE_WITH_TEXTURE);
                    vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT, textureShaderProgram.getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE_WITH_TEXTURE);
                    break;
                case skyBox:
                    vertexData.setVertexAttribPointer(0, ((SkyBoxShaderProgram)shaderProgram).getPositionAttributeLocation(),POSITION_COMPONENT_COUNT, 0);
                    break;
            }
    }

    /**
     * Rysuje siatkę trójkątów zdefiniowaną w buforze <em><b>vertexData</b></em> na podstawie reguł z listy <b><em>drawCommands</em></b>
     */
    public void draw() {
        for (DrawCommand drawCommand : drawCommands) {
            drawCommand.draw();
        }
    }
}
