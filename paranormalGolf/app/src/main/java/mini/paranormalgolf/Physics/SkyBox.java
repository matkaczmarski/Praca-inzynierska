package mini.paranormalgolf.Physics;

import android.content.Context;

import static android.opengl.GLES20.glDrawElements;

import mini.paranormalgolf.Graphics.GraphicsData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Physics.Element;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.R;

/**
 * Created by Mateusz on 2014-12-14.
 */

public class SkyBox extends Element {

    private static final Point CENTER_POINT = new Point(0f,0f,0f);
    private static int skyBoxTextureId;

    public static int getTexture(){return skyBoxTextureId;}

    public SkyBox(Context context) {
        super(CENTER_POINT);
        skyBoxTextureId = ResourceHelper.loadCubeMap(context, new int[]{R.drawable.skybox_texture_space_left, R.drawable.skybox_texture_space_right, R.drawable.skybox_texture_space_bottom, R.drawable.skybox_texture_space_top, R.drawable.skybox_texture_space_back, R.drawable.skybox_texture_space_front});

        GraphicsData generatedData = ObjectGenerator.createSkyBoxModel();
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;

    }
}