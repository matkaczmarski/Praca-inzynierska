package mini.paranormalgolf.Physics;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectBuilder;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.BoxSize;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.Primitives.Rectangle;
import mini.paranormalgolf.R;

/**
 * Created by Sławomir on 2014-12-03.
 */
public class Floor extends Element {

    public final float FLOOR_OPACITY = 1f;
    public final float THRESHOLD_MU_FACTOR = 0.05f;

    private static final float FLOOR_TEXTURE_UNIT = 5f;

    public BoxSize measurements;
    public float mu;

    private static int topFloorTextureNormal;
    private static int topFloorTextureSticky;


    public Floor(BoxSize measures, float mu, Point location) {
        super(location);
        this.measurements = measures;
        this.mu = mu;

        GraphicsData generatedData = ObjectGenerator.createBoxModel(measures, FLOOR_TEXTURE_UNIT);
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;
    }


    public static void initTextures(Context context){
        topFloorTextureSticky = ResourceHelper.loadTexture(context, R.drawable.new_floor_texture5);
        topFloorTextureNormal = ResourceHelper.loadTexture(context, R.drawable.new_floor_texture3);
        //topFloorTextureNormal = ResourceHelper.loadTexture(context, R.drawable.floor_texture_cos);
        //topFloorTextureNormal = ResourceHelper.loadTexture(context, R.drawable.floor_texture_lunabase);
    }


    public BoxSize getMeasurements() {
        return measurements;
    }

    public static int getTopFloorTextureNormal()
    {
        return topFloorTextureNormal;
    }

    public static int getTopFloorTextureSticky()
    {
        return topFloorTextureSticky;
    }

}
