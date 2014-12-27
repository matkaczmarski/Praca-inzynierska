package mini.paranormalgolf.Graphics.ShaderPrograms;

import android.content.Context;

import mini.paranormalgolf.Graphics.DrawManager;
import mini.paranormalgolf.Graphics.LightData;
import mini.paranormalgolf.Primitives.Vector;
import mini.paranormalgolf.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by Mateusz on 2014-12-13.
 */
public class TextureShaderProgram extends ShaderProgram {

    private final int uMVPMatrixLocation;
    private final int uMVMatrixLocation;
    private final int uItMVMatrixLocation;
    private final int uLightPosLocation;
    private final int uLightAmbLocation;
    private final int uLightDiffLocation;
    private final int uTextureUnitLocation;
    private final int uOpacityLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aNormalLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_light_vertex_shader, R.raw.texture_light_fragment_shader);

        // Retrieve uniform locations for the shader program.
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVPMATRIX);
        uMVMatrixLocation = glGetUniformLocation(program, U_MVMATRIX);
        uItMVMatrixLocation = glGetUniformLocation(program, U_ITMVMATRIX);
        uLightPosLocation = glGetUniformLocation(program, U_LIGHTPOS);
        uLightAmbLocation = glGetUniformLocation(program, U_LIGHTAMB);
        uLightDiffLocation = glGetUniformLocation(program, U_LIGHTDIFF);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        uOpacityLocation = glGetUniformLocation(program, U_OPACITY);

        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] mvpMatrix, float[] mvMatrix, float[] itMvMatrix, LightData light, int textureId, float opacity) {
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
        glUniformMatrix4fv(uItMVMatrixLocation, 1, false, itMvMatrix, 0);
        glUniform3f(uLightPosLocation, light.position.x, light.position.y, light.position.z);
        glUniform1f(uLightAmbLocation, light.ambient);
        glUniform1f(uLightDiffLocation, light.diffusion);
        glUniform1f(uOpacityLocation, opacity);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }

    public  int getTextureCoordinatesAttributeLocation(){return aTextureCoordinatesLocation;}

}
