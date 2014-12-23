package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.LightColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureLightShaderProgram;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.ConicalFrustum;
import mini.paranormalgolf.Primitives.Cylinder;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.R;

/**
 * Created by Sławomir on 2014-12-08.
 */

public class HourGlass extends Bonus {

    public class HourGlassWoodenParts extends Element{

        public final float HOURGLASS_WOODEN_PART_OPACITY = 1f;
        private final int HOURGLASS_MESH_DIMENSION = 32;
        private final float TEXTURE_UNIT = 5f;
        private final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * 4;


        public HourGlassWoodenParts(Point location, Cylinder lowerCylinder, Cylinder upperCylinder, Context context) {
            super(location);

            GraphicsData generatedData = ObjectGenerator.createHourglassWoodenParts(upperCylinder, lowerCylinder, HOURGLASS_MESH_DIMENSION,TEXTURE_UNIT );
            vertexData = new VertexArray(generatedData.vertexData);
            drawCommands = generatedData.drawCommands;
            texture = ResourceHelper.loadTexture(context, R.drawable.hourglass_wooden_part_texture);
        }

        @Override
        public void bindData(ShaderProgram shaderProgram) {
            vertexData.setVertexAttribPointer(0, ((TextureLightShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
            vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((TextureLightShaderProgram)shaderProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
            vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT, ((TextureLightShaderProgram)shaderProgram).getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
        }
    }


    public final float HOURGLASS_OPACITY = 0.75f;
    private final int HOURGLASS_MESH_DIMENSION = 32;
    private final int STRIDE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * 4;
    public final float[] GLASS_COLOR = new float[] {0.690196f, 0.878431f, 0.901961f, HOURGLASS_OPACITY};


    private ConicalFrustum lowerCone;
    private ConicalFrustum upperCone;

    private HourGlassWoodenParts woodenParts;
    public HourGlassWoodenParts getWoodenParts(){return woodenParts;}

    private final float WOODEN_BASE_HEIGHT_RATIO = 0.1f;

    public HourGlass(Point location, int value, ConicalFrustum lowerCone, ConicalFrustum upperCone, Context context) {
        super(location, value);
        this.lowerCone = lowerCone;
        this.upperCone = upperCone;

        GraphicsData generatedData = ObjectGenerator.createHourglassGlassPart(lowerCone, upperCone, HOURGLASS_MESH_DIMENSION);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        float woodenBaseHeight = (lowerCone.getHeight() + upperCone.getHeight()) * WOODEN_BASE_HEIGHT_RATIO;
        woodenParts = new HourGlassWoodenParts(location, new Cylinder(new Point(0f, -lowerCone.getHeight() + woodenBaseHeight /2,0f),lowerCone.getBottomRadius(), woodenBaseHeight), new Cylinder(new Point(0f, upperCone.getHeight() - woodenBaseHeight /2,0f),upperCone.getTopRadius(), woodenBaseHeight), context);
    }

    @Override
    public void bindData(ShaderProgram shaderProgram) {
        vertexData.setVertexAttribPointer(0, ((LightColorShaderProgram) shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((LightColorShaderProgram) shaderProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
    }
}
