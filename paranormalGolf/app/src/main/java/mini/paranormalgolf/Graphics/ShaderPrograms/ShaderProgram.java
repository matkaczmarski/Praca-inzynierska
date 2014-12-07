package mini.paranormalgolf.Graphics.ShaderPrograms;

import android.content.Context;

import static android.opengl.GLES20.glUseProgram;
import mini.paranormalgolf.Helpers.ResourceHelper;

/**
 * Created by Mateusz on 2014-12-05.
 */
abstract public class ShaderProgram {

    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_COLOR = "u_Color";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    // Shader program
    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {

        program = ShaderHelper.buildProgram(ResourceHelper.readTextFileFromResource(context, vertexShaderResourceId),
                                            ResourceHelper.readTextFileFromResource(context, fragmentShaderResourceId));
    }

    public void useProgram() {
        glUseProgram(program);
    }
}