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
    private static final int INDICES_COUNT = 36;

    private final VertexArray vertexArray;
    private final ByteBuffer indexArray;

    public Skybox(Context context, Point location, SkyboxTexture skyboxTexture) {
        super(location);
        texture = textureLoader(skyboxTexture, context);

        vertexArray = new VertexArray(new float[] {
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

    private int textureLoader(SkyboxTexture skyboxTextureType, Context context) {
        int skyboxTexture = -1;
        switch (skyboxTextureType) {
            case dayClouds:
                skyboxTexture = ResourceHelper.loadCubeMap(context, new int[]{R.drawable.skybox_texture_dayclouds_left, R.drawable.skybox_texture_dayclouds_right, R.drawable.skybox_texture_dayclouds_bottom, R.drawable.skybox_texture_dayclouds_top, R.drawable.skybox_texture_dayclouds_front, R.drawable.skybox_texture_dayclouds_back});
                break;
            case nightClouds:
                skyboxTexture = ResourceHelper.loadCubeMap(context, new int[]{R.drawable.skybox_texture_nightclouds_left, R.drawable.skybox_texture_nightclouds_right, R.drawable.skybox_texture_nightclouds_bottom, R.drawable.skybox_texture_nightclouds_top, R.drawable.skybox_texture_nightclouds_back, R.drawable.skybox_texture_nightclouds_front});
                break;
//            case stars:
//                skyboxTexture = ResourceHelper.loadCubeMap(context, new int[]{R.drawable.skybox_texture_stars_left, R.drawable.skybox_texture_stars_right, R.drawable.skybox_texture_stars_bottom, R.drawable.skybox_texture_stars_top, R.drawable.skybox_texture_stars_back, R.drawable.skybox_texture_stars_front});
//                break;
            case stars:
                skyboxTexture = ResourceHelper.loadCubeMap(context, new int[]{R.drawable.skybox_texture_stars_wholesky, R.drawable.skybox_texture_stars_wholesky, R.drawable.skybox_texture_stars_wholesky, R.drawable.skybox_texture_stars_wholesky, R.drawable.skybox_texture_stars_wholesky, R.drawable.skybox_texture_stars_wholesky});
                break;
        }
        return skyboxTexture;
    }

    public void bindData(ShaderProgram skyboxProgram) {
        vertexArray.setVertexAttribPointer(0, ((SkyboxShaderProgram)skyboxProgram).getPositionAttributeLocation(),POSITION_COMPONENT_COUNT, 0);
    }

    public void bindShadowData(ShaderProgram skyboxProgram) {
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, INDICES_COUNT,  GL_UNSIGNED_BYTE, indexArray);
    }
}