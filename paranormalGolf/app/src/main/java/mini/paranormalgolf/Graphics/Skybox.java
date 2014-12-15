package mini.paranormalgolf.Graphics;

import android.content.Context;

import java.nio.ByteBuffer;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glDrawElements;

import mini.paranormalgolf.Graphics.ShaderPrograms.ShaderProgram;
import mini.paranormalgolf.Graphics.ShaderPrograms.SkyboxShaderProgram;
import mini.paranormalgolf.Helpers.ResourceHelper;
import mini.paranormalgolf.Physics.Element;
import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.R;

/**
 * Created by Mateusz on 2014-12-14.
 */

public class Skybox extends Element {

    public enum SkyboxTexture{
        nightClouds,
        dayClouds,
        stars
    }
    private static final int POSITION_COMPONENT_COUNT = 3;

    private final VertexArray vertexArray;
    private final ByteBuffer indexArray;

    public Skybox(Context context, Point location, SkyboxTexture skyboxTexture) {
        super(location);
        texture = textureLoader(skyboxTexture, context);

        vertexArray = new VertexArray(new float[] {
                -1,  1,  1,     // (0) Top-left near
                1,  1,  1,     // (1) Top-right near
                -1, -1,  1,     // (2) Bottom-left near
                1, -1,  1,     // (3) Bottom-right near
                -1,  1, -1,     // (4) Top-left far
                1,  1, -1,     // (5) Top-right far
                -1, -1, -1,     // (6) Bottom-left far
                1, -1, -1      // (7) Bottom-right far
        });

        // 6 indices per cube side
        indexArray =  ByteBuffer.allocateDirect(6 * 6)
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

    private int textureLoader(SkyboxTexture skyboxTextureType, Context context) {
        int skyboxTexture = -1;
        switch (skyboxTextureType) {
            case dayClouds:
                skyboxTexture = ResourceHelper.loadCubeMap(context, new int[]{R.drawable.left, R.drawable.right, R.drawable.bottom, R.drawable.top, R.drawable.front, R.drawable.back});
                break;
            case nightClouds:
                skyboxTexture = ResourceHelper.loadCubeMap(context, new int[]{R.drawable.night_left, R.drawable.night_right, R.drawable.night_bottom, R.drawable.night_top, R.drawable.night_back, R.drawable.night_front});
                break;
        }
        return skyboxTexture;
    }

    public void bindData(ShaderProgram skyboxProgram) {
        vertexArray.setVertexAttribPointer(0, ((SkyboxShaderProgram)skyboxProgram).getPositionAttributeLocation(),POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, 36,  GL_UNSIGNED_BYTE, indexArray);
    }
}