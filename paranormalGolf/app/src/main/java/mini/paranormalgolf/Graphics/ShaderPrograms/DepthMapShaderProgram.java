package mini.paranormalgolf.Graphics.ShaderPrograms;

import android.content.Context;
import mini.paranormalgolf.R;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by Mateusz on 2015-01-03.
 */
public class DepthMapShaderProgram extends ShaderProgram {
    private final int uMVPMatrixLocation;
    private final int uMVMatrixLocation;

    private final int aPositionLocation;

    public DepthMapShaderProgram(Context context){
        super(context, R.raw.depthmap_vertex_shader, R.raw.depthmap_fragment_shader);
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVPMATRIX);
        uMVMatrixLocation = glGetUniformLocation(program, U_MVMATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    public void setUniforms(float[] mvpMatrix, float[] mvMatrix) {
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

}
