package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.ShaderPrograms.ColorShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.DepthMapShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.TextureShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.ShadowingShaderProgram;
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

    private float HOURGLASS_ROTATION_SPEED = 0.5f;
    private float HOURGLASS_UP_DOWN_SPEED = 0.001f;
    public final float HOURGLASS_OPACITY = 0.75f;
    private final int HOURGLASS_MESH_DIMENSION = 32;
    private final float WOODEN_BASE_HEIGHT_RATIO = 0.1f;
    private final int STRIDE = (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * 4;
    public final float[] GLASS_COLOR = new float[] {0.690196f, 0.878431f, 0.901961f, HOURGLASS_OPACITY};

    public ConicalFrustum getLowerCone() {
        return lowerCone;
    }

    private final ConicalFrustum lowerCone = new ConicalFrustum(0.7f, 0.5f, 0.15f);
    private final ConicalFrustum upperCone = new ConicalFrustum(0.7f, 0.15f, 0.5f);

    public class HourGlassWoodenParts extends Bonus{

        public final float HOURGLASS_WOODEN_PART_OPACITY = 1f;
        private final int HOURGLASS_MESH_DIMENSION = 32;
        private final float TEXTURE_UNIT = 5f;
        private final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * 4;


        public HourGlassWoodenParts(Point location, Cylinder lowerCylinder, Cylinder upperCylinder, Context context) {
            super(location, 0, 0);

            GraphicsData generatedData = ObjectGenerator.createHourglassWoodenParts(upperCylinder, lowerCylinder, HOURGLASS_MESH_DIMENSION,TEXTURE_UNIT );
            vertexData = new VertexArray(generatedData.vertexData);
            drawCommands = generatedData.drawCommands;
            texture = ResourceHelper.loadTexture(context, R.drawable.hourglass_texture_wooden_part);
            ROTATION_SPEED = HOURGLASS_ROTATION_SPEED;
        }

//        @Override
//        public void bindData(ShaderProgram shaderProgram) {
//            vertexData.setVertexAttribPointer(0, ((TextureShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
//            vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((TextureShaderProgram)shaderProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
//            vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT, ((TextureShaderProgram)shaderProgram).getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
//        }
//
//        public void bindShadowData(ShaderProgram shaderProgram) {
//            vertexData.setVertexAttribPointer(0, ((ShadowingShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
//            vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((ShadowingShaderProgram)shaderProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
//            vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT, ((ShadowingShaderProgram)shaderProgram).getTextureCoordinatesAttributeLocation(), TEXTURE_COMPONENT_COUNT, STRIDE);
//        }
//
//        public void bindDepthMapData(ShaderProgram shaderProgram) {
//            vertexData.setVertexAttribPointer(0, ((DepthMapShaderProgram)shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
//        }
    }


    private HourGlassWoodenParts woodenParts;
    public HourGlassWoodenParts getWoodenParts(){return woodenParts;}

    public HourGlass(Point location, int value, float yShift, Context context) {
        super(location, value, yShift);

        GraphicsData generatedData = ObjectGenerator.createHourglassGlassPart(lowerCone, upperCone, HOURGLASS_MESH_DIMENSION);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        float woodenBaseHeight = (lowerCone.height + upperCone.height) * WOODEN_BASE_HEIGHT_RATIO;
        woodenParts = new HourGlassWoodenParts(location, new Cylinder(new Point(0f, -lowerCone.height + woodenBaseHeight /2,0f),lowerCone.bottomRadius, woodenBaseHeight), new Cylinder(new Point(0f, upperCone.height - woodenBaseHeight /2,0f),upperCone.topRadius, woodenBaseHeight), context);
        ROTATION_SPEED = HOURGLASS_ROTATION_SPEED;
        UP_DOWN_SPEED = HOURGLASS_UP_DOWN_SPEED;
    }

//    @Override
//    public void bindData(ShaderProgram shaderProgram) {
//        vertexData.setVertexAttribPointer(0, ((ColorShaderProgram) shaderProgram).getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
//        vertexData.setVertexAttribPointer(POSITION_COMPONENT_COUNT, ((ColorShaderProgram) shaderProgram).getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, STRIDE);
//    }

}
