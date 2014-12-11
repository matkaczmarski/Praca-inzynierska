package mini.paranormalgolf.Graphics.ShaderPrograms;

import android.content.Context;

import mini.paranormalgolf.Primitives.Point;
import mini.paranormalgolf.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by Mateusz on 2014-12-10.
 */
public class LightColorShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uMVPMatrixLocation;
    private final int uMVMatrixLocation;
    private final int uColorLocation;
    private final int uLightPosLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aNormalLocation;

    public LightColorShaderProgram(Context context) {
        super(context, R.raw.light_vertex_shader, R.raw.light_fragmet_shader);

        // Retrieve uniform locations for the shader program.
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVPMATRIX);
        uMVMatrixLocation = glGetUniformLocation(program, U_MVMATRIX);
        uColorLocation = glGetUniformLocation(program, U_COLOR);
        uLightPosLocation = glGetUniformLocation(program, U_LIGHTPOS);

        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
    }

    public void setUniforms(float[] mvpMatrix, float[] mvMatrix, float[]rgba, Point lightPosition) {
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
        glUniform4f(uColorLocation, rgba[0], rgba[1], rgba[2], rgba[3]);
        glUniform3f(uLightPosLocation, lightPosition.X, lightPosition.Y, lightPosition.Z);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }


}
