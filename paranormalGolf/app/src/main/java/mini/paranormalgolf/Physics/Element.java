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
 * Created by Sławomir on 2014-12-03.
 */
public abstract class Element {

    protected final int POSITION_COMPONENT_COUNT = 3;
    protected final int NORMAL_COMPONENT_COUNT = 3;
    protected final int TEXTURE_COMPONENT_COUNT = 2;
    protected final int BYTES_PER_FLOAT = 4;

    private final int STRIDE_WITH_TEXTURE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private final int STRIDE_WITHOUT_TEXTURE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * 4;

    protected Point location;
    protected int texture;
    protected VertexArray vertexData;
    protected List<DrawCommand> drawCommands;

    public Point getLocation() { return this.location;}
    public void setLocation(Point location) { this.location = location;}

    public int getTexture() {return  this.texture;}

    protected Element(Point location){
     this.location=location;
    }

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

    public void draw() {
        for (DrawCommand drawCommand : drawCommands) {
            drawCommand.draw();
        }
    }
}
