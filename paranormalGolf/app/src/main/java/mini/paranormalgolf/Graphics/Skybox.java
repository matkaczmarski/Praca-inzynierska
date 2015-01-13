package mini.paranormalgolf.Graphics;

import android.content.Context;

import java.nio.ByteBuffer;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glDrawElements;

import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.SkyBoxShaderProgram;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Physics.Element;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.R;

/**
 * Created by Mateusz on 2014-12-14.
 */

public class SkyBox extends Element {

    private static final int INDICES_COUNT = 36;
    private static final Point CENTER_POINT = new Point(0f,0f,0f);

    private final ByteBuffer indexArray;

    public SkyBox(Context context) {
        super(CENTER_POINT);
        texture = ResourceHelper.loadCubeMap(context, new int[]{R.drawable.skybox_texture_space_left, R.drawable.skybox_texture_space_right, R.drawable.skybox_texture_space_bottom, R.drawable.skybox_texture_space_top, R.drawable.skybox_texture_space_back, R.drawable.skybox_texture_space_front});


        vertexData = new VertexArray(new float[] {
                -1,  1,  1,
                1,  1,  1,
                -1, -1,  1,
                1, -1,  1,
                -1,  1, -1,
                1,  1, -1,
                -1, -1, -1,
                1, -1, -1 });

        // 6 indices per cube side
        indexArray =  ByteBuffer.allocateDirect(INDICES_COUNT)
                .put(new byte[]{
                        // Front
                        1, 3, 0,
                        0, 3, 2,

                        // Back
                        4, 6, 5,
                        5, 6, 7,

                        // Left
                        0, 2, 4,
                        4, 2, 6,

                        // Right
                        5, 7, 1,
                        1, 7, 3,

                        // Top
                        5, 1, 4,
                        4, 1, 0,

                        // Bottom
                        6, 2, 7,
                        7, 2, 3
                });
        indexArray.position(0);
    }


    public void draw() {
        glDrawElements(GL_TRIANGLES, INDICES_COUNT,  GL_UNSIGNED_BYTE, indexArray);
    }
}