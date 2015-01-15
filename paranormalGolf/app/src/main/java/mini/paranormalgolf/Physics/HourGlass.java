package mini.paranormalgolf.Physics;

import android.content.Context;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
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
    public final float[] GLASS_COLOR = new float[] {0.690196f, 0.878431f, 0.901961f, HOURGLASS_OPACITY};

    private static int hourGlassTexture;

    public ConicalFrustum getLowerCone() {
        return lowerCone;
    }

    private final ConicalFrustum lowerCone = new ConicalFrustum(0.7f, 0.5f, 0.15f);
    private final ConicalFrustum upperCone = new ConicalFrustum(0.7f, 0.15f, 0.5f);

    public class HourGlassWoodenParts extends Bonus{

        public final float HOURGLASS_WOODEN_PART_OPACITY = 1f;
        private final int HOURGLASS_MESH_DIMENSION = 32;
        private final float TEXTURE_UNIT = 5f;

        public HourGlassWoodenParts(Point location, Cylinder lowerCylinder, Cylinder upperCylinder) {
            super(location, 0, 0);

            GraphicsData generatedData = ObjectGenerator.createHourglassWoodenPartsModel(upperCylinder, lowerCylinder, HOURGLASS_MESH_DIMENSION, TEXTURE_UNIT);
            vertexData = new VertexArray(generatedData.vertexData);
            drawCommands = generatedData.drawCommands;
            texture = hourGlassTexture;//ResourceHelper.loadTexture(context, R.drawable.hourglass_texture_wooden_part);
            ROTATION_SPEED = HOURGLASS_ROTATION_SPEED;
        }
    }


    private HourGlassWoodenParts woodenParts;
    public HourGlassWoodenParts getWoodenParts(){return woodenParts;}

    public HourGlass(Point location, int value, float yShift) {
        super(location, value, yShift);

        GraphicsData generatedData = ObjectGenerator.createHourglassGlassPartModel(lowerCone, upperCone, HOURGLASS_MESH_DIMENSION);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
        float woodenBaseHeight = (lowerCone.height + upperCone.height) * WOODEN_BASE_HEIGHT_RATIO;
        woodenParts = new HourGlassWoodenParts(location, new Cylinder(new Point(0f, -lowerCone.height + woodenBaseHeight /2,0f),lowerCone.bottomRadius, woodenBaseHeight), new Cylinder(new Point(0f, upperCone.height - woodenBaseHeight /2,0f),upperCone.topRadius, woodenBaseHeight));
        ROTATION_SPEED = HOURGLASS_ROTATION_SPEED;
        UP_DOWN_SPEED = HOURGLASS_UP_DOWN_SPEED;
    }

    public static void initTextures(Context context){
        hourGlassTexture = ResourceHelper.loadTexture(context, R.drawable.hourglass_texture_wooden_part);
    }

    public static int getHourGlassTexture(){
        return hourGlassTexture;
    }
}
