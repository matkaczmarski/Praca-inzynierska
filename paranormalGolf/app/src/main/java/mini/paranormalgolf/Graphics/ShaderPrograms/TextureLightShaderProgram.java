package mini.paranormalgolf.Graphics.ShaderPrograms;

import android.content.Context;

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
public class TextureLightShaderProgram extends ShaderProgram {

    private final int uMVPMatrixLocation;
    private final int uMVMatrixLocation;
    private final int uItMVMatrixLocation;
    private final int uLightPosLocation;
    private final int uTextureUnitLocation;
    private final int uOpacityLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aNormalLocation;
    private final int aTextureCoordinatesLocation;

    public TextureLightShaderProgram(Context context) {
        super(context, R.raw.texture_light_vertex_shader, R.raw.texture_light_fragment_shader);

        // Retrieve uniform locations for the shader program.
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVPMATRIX);
        uMVMatrixLocation = glGetUniformLocation(program, U_MVMATRIX);
        uItMVMatrixLocation = glGetUniformLocation(program, U_ITMVMATRIX);
        uLightPosLocation = glGetUniformLocation(program, U_LIGHTPOS);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        uOpacityLocation = glGetUniformLocation(program, U_OPACITY);

        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] mvpMatrix, float[] mvMatrix, float[] itMvMatrix, Vector lightPosition, int textureId, float opacity) {
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
        glUniformMatrix4fv(uItMVMatrixLocation, 1, false, itMvMatrix, 0);
        glUniform3f(uLightPosLocation, lightPosition.x, lightPosition.y, lightPosition.z);
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
