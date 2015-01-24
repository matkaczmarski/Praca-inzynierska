package mini.paranormalgolf.Physics;

import android.content.Context;

import static android.opengl.GLES20.glDrawElements;

import mini.paranormalgolf.Graphics.TriangleMeshData;
import mini.paranormalgolf.Graphics.ModelBuilders.ObjectGenerator;
import mini.paranormalgolf.Graphics.VertexArray;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.R;

/**
 * Tło sceny reprezentowane jako sześcian z nałożonymi teksturami przestrzennego tła.
 */
public class SkyBox extends Element {

    /**
     * Statyczna wartość identyfikatora OpenGL sześciennej tekstury tła.
     */
    private static int skyBoxTextureId;

    /**
     * Zwraca wartość identyfikatora OpenGL sześciennej tekstury tła.
     * @return Wartość <b><em>skyBoxTextureId</em></b>.
     */
    public static int getTexture(){return skyBoxTextureId;}

    /**
     * Tworzy obiekt typu przestrzenne tło.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public SkyBox(Context context) {
        super(new Point(0f,0f,0f));
        skyBoxTextureId = ResourceHelper.loadCubeMap(context, new int[]{R.drawable.skybox_texture_space_left, R.drawable.skybox_texture_space_right, R.drawable.skybox_texture_space_bottom, R.drawable.skybox_texture_space_top, R.drawable.skybox_texture_space_back, R.drawable.skybox_texture_space_front});
        TriangleMeshData generatedData = ObjectGenerator.createSkyBoxModel();
        vertexData = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;

    }
}