package mini.paranormalgolf.Graphics.ShaderPrograms;

import android.content.Context;
import mini.paranormalgolf.R;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Opisuje program do generowania mapy głębokości cieni.
 */
public class DepthMapShaderProgram extends ShaderProgram {

    /**
     * Określa lokalizację stałej wartości (uniform macierzy lightsViewProjection) występującej w kodzie programu.
     */
    private final int uShadowPMatrixLocation;
    /**
     * Określa lokalizację stałej wartości (uniform macierzy modelView) występującej w kodzie programu.
     */
    private final int uMVMatrixLocation;

    /**
     * Określa lokalizację atrybutu pozycji wierzchołków występującego w kodzie programu.
     */
    private final int aPositionLocation;

    /**
     * Tworzy obiekt programu do generowania mapy głębokości cieni.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public DepthMapShaderProgram(Context context){
        super(context, R.raw.depthmap_vertex_shader, R.raw.depthmap_fragment_shader);
        uShadowPMatrixLocation = glGetUniformLocation(program, U_SHADOWPMATRIX);
        uMVMatrixLocation = glGetUniformLocation(program, U_MODELMATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    /**
     * Przypisuje stałym wartościom (uniformom) występującym w kodzie programu odpowiednie wartości.
     * @param mvpMatrix Przypisywana wartość macierzy modelViewProjection dla światła.
     * @param mvMatrix Przypisywana wartość macierzy modelView.
     */
    public void setUniforms(float[] mvpMatrix, float[] mvMatrix) {
        glUniformMatrix4fv(uShadowPMatrixLocation, 1, false, mvpMatrix, 0);
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
    }

    /**
     * Zwraca lokalizację atrybutu pozycji wierzchołków.
     * @return Wartość <em><b>aPositionLocation</b></em>.
     */
    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

}
