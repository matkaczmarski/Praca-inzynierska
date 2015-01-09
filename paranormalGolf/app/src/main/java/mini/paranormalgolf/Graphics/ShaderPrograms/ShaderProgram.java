package mini.paranormalgolf.Graphics.ShaderPrograms;

import android.content.Context;

import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;

import mini.paranormalgolf.Graphics.LightData;
import mini.paranormalgolf.Helpers.ResourceHelper;

/**
 * Created by Mateusz on 2014-12-05.
 */
abstract public class ShaderProgram {

    public enum ShaderProgramType{
        color,
        depthMap,
        withShadowing,
        withoutShadowing

    }

    // Uniform constants
    protected static final String U_MVPMATRIX = "u_MVPMatrix";
    protected static final String U_MVMATRIX = "u_MVMatrix";
    protected static final String U_ITMVMATRIX = "u_itMVMatrix";
    protected static final String U_COLOR = "u_Color";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String U_LIGHTPOS = "u_LightPos";
    protected static final String U_LIGHTAMB = "u_LightsAmbient";
    protected static final String U_LIGHTDIFF = "u_LightsDiffusion";
    protected static final String U_OPACITY = "u_Opacity";
    protected  static final String U_SHADOW_PMATRIX = "u_ShadowProjMatrix";
    protected  static final String U_SHADOW_TEXTURE = "u_ShadowTexture";


    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_NORMAL = "a_Normal";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    // Shader program
    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
        program = ShaderHelper.buildProgram(ResourceHelper.readTextFileFromResource(context, vertexShaderResourceId), ResourceHelper.readTextFileFromResource(context, fragmentShaderResourceId));
    }


    public void useProgram() {
        glUseProgram(program);
    }
}
