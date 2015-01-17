package mini.paranormalgolf.Graphics.ShaderPrograms;

import android.content.Context;

import mini.paranormalgolf.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Opisuje program do generowania przestrzennego tła.
 */
public class SkyBoxShaderProgram extends ShaderProgram {

    /**
     * Określa lokalizację stałej wartości (uniform macierzy modelViewProjection) występującej w kodzie programu.
     */
    private final int uMatrixLocation;
    /**
     * Określa lokalizację stałej wartości (uniform  tekstury) występującej w kodzie programu.
     */
    private final int uTextureUnitLocation;

    /**
     * Określa lokalizację atrybutu pozycji wierzchołków występującego w kodzie programu.
     */
    private final int aPositionLocation;

    /**
     * Tworzy obiekt programu do generowania przestrzennego tła.
     * @param context Bieżący kontekst pozwalający uzyskać dostęp do zasobów aplikacji.
     */
    public SkyBoxShaderProgram(Context context) {
        super(context, R.raw.skybox_vertex_shader,
                R.raw.skybox_fragment_shader);

        uMatrixLocation = glGetUniformLocation(program, U_MVPMATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    /**
     * Przypisuje stałym wartościom (uniformom) występującym w kodzie programu odpowiednie wartości.
     * @param matrix Przypisywana wartość macierzy modelViewProjection.
     * @param textureId Przypisywana wartość identyfikatora tekstury.
     */
    public void setUniforms(float[] matrix, int textureId) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureId);
        glUniform1i(uTextureUnitLocation, 0);
    }

    /**
     * Zwraca lokalizację atrybutu pozycji wierzchołków.
     * @return Wartość <em><b>aPositionLocation</b></em>.
     */
    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}